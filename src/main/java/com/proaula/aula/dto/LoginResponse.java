package com.proaula.aula.dto;

public class LoginResponse {

    private String token;
    private String username;
    private long issuedAt;
    private long expiration;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, long issuedAt, long expiration) {
        this.token = token;
        this.username = username;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
