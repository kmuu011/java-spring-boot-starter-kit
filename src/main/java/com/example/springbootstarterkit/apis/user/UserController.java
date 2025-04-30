package com.example.springbootstarterkit.apis.user;

import com.example.springbootstarterkit.apis.user.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final AuthenticationManager authManager;
  private final CustomUserDetailsService userDetailsService;

  public UserController(
    AuthenticationManager authManager,
    CustomUserDetailsService userDetailsService
  ) {
    this.authManager = authManager;
    this.userDetailsService = userDetailsService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto dto, HttpServletRequest req) {
    UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
    Authentication auth = authManager.authenticate(token);

    SecurityContextHolder.getContext().setAuthentication(auth);
    req.getSession(true).setAttribute(
      HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
      SecurityContextHolder.getContext()
    );

    return ResponseEntity.ok("로그인 성공");
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody LoginDto dto) {
    try {
      userDetailsService.loadUserByUsername(dto.getUsername());
      return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 사용자");
    } catch (UsernameNotFoundException ex) {
    }

    userDetailsService.register(dto.getUsername(), dto.getPassword());
    return ResponseEntity.ok("가입 완료");
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
    new SecurityContextLogoutHandler().logout(req, res, SecurityContextHolder.getContext().getAuthentication());
    return ResponseEntity.ok("로그아웃 성공");
  }

}