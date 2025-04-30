package com.example.springbootstarterkit.apis.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CustomUserDetails implements UserDetails, Serializable {
  private static final long serialVersionUID = 1L;

  private final Integer id;
  private final String username;
  private final String password;
  private final List<GrantedAuthority> authorities;

  @JsonCreator
  public CustomUserDetails(
    @JsonProperty("id") Integer id,
    @JsonProperty("username") String username,
    @JsonProperty("password") String password,
    @JsonProperty("authorities") List<GrantedAuthority> authorities
  ) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public CustomUserDetails(UserEntity user) {
    this(
      user.getId(),
      user.getUsername(),
      user.getPassword(),
      List.of(new SimpleGrantedAuthority(user.getRole()))
    );
  }

  @Override
  public boolean isEnabled() {
    return true;
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
}