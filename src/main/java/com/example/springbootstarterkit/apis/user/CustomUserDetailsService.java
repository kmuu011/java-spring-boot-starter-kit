package com.example.springbootstarterkit.apis.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public CustomUserDetailsService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

//  @Override
//  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    UserEntity user = userRepository.findByUsername(username)
//        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//    return org.springframework.security.core.userdetails.User.builder()
//        .username(user.getUsername())
//        .password(user.getPassword())
//        .build();
//  }

  @Override
  public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new CustomUserDetails(user);
  }

  public void register(String username, String rawPassword) {
    String enc = passwordEncoder.encode(rawPassword);
    UserEntity u = new UserEntity();
    u.setUsername(username);
    u.setPassword(enc);
    userRepository.save(u);
  }

}
