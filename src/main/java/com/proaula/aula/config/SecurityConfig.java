package com.proaula.aula.config;

import com.proaula.aula.config.CustomOAuth2UserService;
import com.proaula.aula.Service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.proaula.aula.config.JwtCookieService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                         CustomOAuth2UserService customOAuth2UserService,
                         CustomAuthenticationSuccessHandler successHandler,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.successHandler = successHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (incluye registro y login en todas sus variantes)
                .requestMatchers("/", 
                                "/registro-mejorado", "/registro-mejorado.html", "/registro", "/registro/**",
                                "/inicio-de-sesion-mejorado", "/inicio-de-sesion-mejorado.html", "/inicio_de_sesion",
                                "/admin-login", "/admin/verificar-codigo", "/admin/login",
                                "/debug/**",
                                "/css/**", "/js/**", "/images/**", "/error", "/error/**",
                                "/public_index_3", "/viajar_public", "/index_3_public", 
                                "/contacto", "/contacto_public", "/contacto_usuario",
                                "/terminos", "/terminos.html", "/privacidad", "/privacidad.html",
                                "/api/auth/login", "/api/auth/register", "/rutas/**", "/buses/**",
                                "/oauth2/**", "/login/oauth2/**", "/login/oauth2/code/**", "/oauth2/complete-password").permitAll()
                // Rutas de administrador
                .requestMatchers("/index_2", "/admin/**", "/reportes", "/gestionar-usuarios", 
                            "/editar-usuario/**", "/eliminar-usuario/**", "/admin-crear-usuario", 
                            "/actualizar-usuario", "/rutas/admin", "/agregar_rutas", "/editar-ruta", "/editar-ruta/**", "/eliminar-ruta/**",
                            "/contacto/mensajes", "/mensajes_contacto", "/eliminar_mensaje/**",
                            "/registro-buses", "/actualizarbuses", "/actualizar-bus/**", "/eliminarbuses", "/eliminar-bus/**", "/asignaciones/**").hasRole("ADMIN")
                // Rutas para usuarios y administradores
                .requestMatchers("/dashboard", "/perfil", "/perfil/actualizar", "/perfil/cambiar-password",
                            "/viajar", "/consultas", "/historial", "/usuario/**", "/index_3").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .formLogin(form -> form
                .loginPage("/inicio-de-sesion-mejorado")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureUrl("/inicio-de-sesion-mejorado?error=true")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/inicio-de-sesion-mejorado")
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOAuth2UserService::loadOidcUser)
                    .userService(customOAuth2UserService)
                )
                .successHandler(successHandler)
                .failureUrl("/inicio-de-sesion-mejorado?error=true")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(false)
                .deleteCookies("jwt")
                .permitAll()
            );

        return http.build();
    }
}