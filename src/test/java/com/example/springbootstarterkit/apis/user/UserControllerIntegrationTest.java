package com.example.springbootstarterkit.apis.user;

import com.example.springbootstarterkit.apis.user.dto.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void cleanUp() {
    userRepository.deleteAll();
  }

  @Test
  void 회원가입에_성공하면_사용자가_DB에_저장된다() throws Exception {
    LoginDto dto = new LoginDto();
    dto.setUsername("newbie");
    dto.setPassword("newbiePass!");

    mockMvc.perform(post("/api/user/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isOk())
      .andExpect(content().string("가입 완료"));

    assertThat(userRepository.findByUsername("newbie")).isPresent();
  }

  @Test
  void 이미_존재하는_사용자는_회원가입시_409를_받는다() throws Exception {
    createUser("dupUser", "password!");

    LoginDto dto = new LoginDto();
    dto.setUsername("dupUser");
    dto.setPassword("any");

    mockMvc.perform(post("/api/user/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isConflict())
      .andExpect(content().string("이미 존재하는 사용자"));
  }

  @Test
  void 로그인에_성공하면_세션_쿠키가_발급된다() throws Exception {
    createUser("tester", "secret!");

    LoginDto dto = new LoginDto();
    dto.setUsername("tester");
    dto.setPassword("secret!");

    mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isOk())
      .andExpect(content().string("로그인 성공"))
      .andExpect(cookie().exists("SESSION"));
  }

  @Test
  void 잘못된_비밀번호로_로그인하면_401과_JSON_오류가_반환된다() throws Exception {
    createUser("tester", "secret!");

    LoginDto dto = new LoginDto();
    dto.setUsername("tester");
    dto.setPassword("wrong");

    mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.code").value("AUTH_BAD_CREDENTIALS"))
      .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
  }

  @Test
  void 로그인된_사용자는_로그아웃시_200을_받는다() throws Exception {
    createUser("tester", "secret!");
    Cookie sessionCookie = performLogin("tester", "secret!");

    mockMvc.perform(post("/api/user/logout")
        .cookie(sessionCookie))
      .andExpect(status().isOk())
      .andExpect(content().string("로그아웃 성공"));
  }

  private void createUser(String username, String rawPassword) {
    UserEntity entity = new UserEntity();
    entity.setUsername(username);
    entity.setPassword(passwordEncoder.encode(rawPassword));
    entity.setRole("USER");
    userRepository.save(entity);
  }

  private Cookie performLogin(String username, String rawPassword) throws Exception {
    LoginDto dto = new LoginDto();
    dto.setUsername(username);
    dto.setPassword(rawPassword);

    Cookie cookie = mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getCookie("SESSION");

    assertThat(cookie).as("세션 쿠키 발급 여부").isNotNull();
    return cookie;
  }
}

