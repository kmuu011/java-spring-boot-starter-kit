package com.example.springbootstarterkit.apis.user;

import com.example.springbootstarterkit.apis.user.dto.LoginDto;
import com.example.springbootstarterkit.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 인증 및 관리 API")
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

  @Operation(summary = "로그인", description = "사용자 로그인 (세션 생성)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(examples = @ExampleObject(value = "로그인 성공"))),
      @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 사용자명 또는 비밀번호)")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto dto, HttpServletRequest req) {
    if (dto.getUsername() == null || dto.getUsername().isBlank() ||
      dto.getPassword() == null || dto.getPassword().isBlank()) {
      throw new BusinessException("AUTH_INVALID_REQUEST", HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 입력하세요.");
    }

    UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
    Authentication auth;
    try {
      auth = authManager.authenticate(token);
    } catch (BadCredentialsException ex) {
      throw new BusinessException("AUTH_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    SecurityContextHolder.getContext().setAuthentication(auth);
    req.getSession(true).setAttribute(
      HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
      SecurityContextHolder.getContext()
    );

    return ResponseEntity.ok("로그인 성공");
  }

  @Operation(summary = "회원가입", description = "새로운 사용자 계정 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "가입 성공",
          content = @Content(examples = @ExampleObject(value = "가입 완료"))),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
  })
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

  @Operation(summary = "로그아웃", description = "사용자 로그아웃 (세션 삭제)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그아웃 성공",
          content = @Content(examples = @ExampleObject(value = "로그아웃 성공")))
  })
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
    new SecurityContextLogoutHandler().logout(req, res, SecurityContextHolder.getContext().getAuthentication());
    return ResponseEntity.ok("로그아웃 성공");
  }

}