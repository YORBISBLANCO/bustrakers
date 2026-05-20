package com.proaula.aula.config;

import com.proaula.aula.Service.UsuarioService;
import com.proaula.aula.document.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UsuarioService usuarioService;

    public CustomOAuth2UserService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());

        String email = (String) attributes.get("email");
        if ((email == null || email.isBlank()) && "github".equals(userRequest.getClientRegistration().getRegistrationId())) {
            email = fetchGithubEmail(userRequest);
            if (email != null) {
                attributes.put("email", email);
            }
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("OAuth2 user email is required");
        }

        String localUsername = ensureLocalUser(email, attributes, oauth2User.getAuthorities());

        attributes.put("username", localUsername);
        attributes.put("sub", localUsername);

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oauth2User.getAuthorities());
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new DefaultOAuth2User(mappedAuthorities, attributes, "username");
    }

    public OidcUser loadOidcUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());

        String email = oidcUser.getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google OIDC user email is required");
        }

        String localUsername = ensureLocalUser(email, attributes, oidcUser.getAuthorities());

        attributes.put("username", localUsername);
        attributes.put("sub", localUsername);

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new OidcUser() {
            @Override
            public Map<String, Object> getAttributes() {
                return attributes;
            }

            @Override
            public Map<String, Object> getClaims() {
                return attributes;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return mappedAuthorities;
            }

            @Override
            public OidcUserInfo getUserInfo() {
                return oidcUser.getUserInfo();
            }

            @Override
            public OidcIdToken getIdToken() {
                return oidcUser.getIdToken();
            }

            @Override
            public String getName() {
                return localUsername;
            }
        };
    }

    private String ensureLocalUser(String email, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        var existingUser = usuarioService.findByEmail(email);
        if (existingUser != null) {
            String localUsername = existingUser.getUsername();
            logger.info("OAuth2 login: existing user found email='{}', username='{}'", email, localUsername);
            return localUsername;
        }

        String usernameBase = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "");
        if (usernameBase.isBlank()) {
            usernameBase = "user";
        }

        String localUsername = usernameBase;
        int suffix = 1;
        while (usuarioService.existsByUsername(localUsername)) {
            localUsername = usernameBase + suffix++;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(localUsername);
        nuevoUsuario.setPassword(UUID.randomUUID().toString());
        nuevoUsuario.setRole("ROLE_USER");
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPasswordSetupRequired(true);
        String nombres = (String) attributes.getOrDefault("given_name", attributes.getOrDefault("name", "Google"));
        String apellidos = (String) attributes.getOrDefault("family_name", "User");
        if (nombres == null || nombres.isBlank()) {
            nombres = "Google";
        }
        if (apellidos == null || apellidos.isBlank()) {
            apellidos = "User";
        }
        nuevoUsuario.setNombres(nombres);
        nuevoUsuario.setApellidos(apellidos);
        usuarioService.register(nuevoUsuario);
        logger.info("OAuth2 login: new user created email='{}', username='{}'", email, localUsername);
        return localUsername;
    }

    private String fetchGithubEmail(OAuth2UserRequest userRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isBlank()) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> emails = mapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> emailEntry : emails) {
                Boolean primary = Boolean.TRUE.equals(emailEntry.get("primary"));
                Boolean verified = Boolean.TRUE.equals(emailEntry.get("verified"));
                String email = (String) emailEntry.get("email");
                if (email != null && verified && primary) {
                    return email;
                }
            }
            for (Map<String, Object> emailEntry : emails) {
                Boolean verified = Boolean.TRUE.equals(emailEntry.get("verified"));
                String email = (String) emailEntry.get("email");
                if (email != null && verified) {
                    return email;
                }
            }
            if (!emails.isEmpty()) {
                return (String) emails.get(0).get("email");
            }
        } catch (Exception ex) {
            logger.warn("Unable to fetch GitHub email from /user/emails: {}", ex.getMessage());
        }
        return null;
    }
}
