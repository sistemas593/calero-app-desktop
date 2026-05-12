package com.calero.lili.api.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UsuarioSecurity implements UserDetails {

    private String username;
    private String password;
    private Long data;
    private String nivel;

    public Long getRandom() {
        return random;
    }

    public void setRandom(Long random) {
        this.random = random;
    }

    private Long random;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UsuarioSecurity(String username, String password, Long data, String nivel, Collection<? extends GrantedAuthority> authorities, Long random) {
        this.username = username;
        this.password = password;
        this.data = data;
        this.nivel = nivel;
        this.authorities = authorities;
        this.random = random;
    }


    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(int data) {
        this.nivel = nivel;
    }


}
