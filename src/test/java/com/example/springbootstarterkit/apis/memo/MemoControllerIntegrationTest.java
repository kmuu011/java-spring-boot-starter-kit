package com.example.springbootstarterkit.apis.memo;

import com.example.springbootstarterkit.apis.memo.dto.CreateMemoDto;
import com.example.springbootstarterkit.apis.memo.dto.UpdateMemoDto;
import com.example.springbootstarterkit.apis.user.UserEntity;
import com.example.springbootstarterkit.apis.user.UserRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemoControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MemoRepository memoRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private Cookie sessionCookie;
  private Integer userId;

  @BeforeEach
  void setUp() throws Exception {
    memoRepository.deleteAll();
    userRepository.deleteAll();

    String username = "tester";
    String password = "password123!";
    this.userId = createUser(username, password);
    this.sessionCookie = performLogin(username, password);
  }

  @Test
  void 로그인된_사용자는_자신의_메모_목록만_조회한다() throws Exception {
    createMemo(userId, "첫 번째 메모");
    createMemo(userId, "두 번째 메모");

    Integer otherUserId = createUser("intruder", "secret123!");
    createMemo(otherUserId, "남의 메모");

    mockMvc.perform(get("/api/memo")
        .cookie(sessionCookie)
        .param("page", "1")
        .param("count", "10"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(2))
      .andExpect(jsonPath("$.memos", hasSize(2)))
      .andExpect(jsonPath("$.memos[0].userId").value(userId));
  }

  @Test
  void 세션이_없으면_메모_목록_요청이_401을_반환한다() throws Exception {
    mockMvc.perform(get("/api/memo"))
      .andExpect(status().isForbidden());
  }

  @Test
  void 메모를_생성하면_DB에_저장되고_생성된_내용을_반환한다() throws Exception {
    CreateMemoDto dto = new CreateMemoDto();
    dto.setContent("새로운 메모");

    mockMvc.perform(post("/api/memo")
        .cookie(sessionCookie)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").isNumber())
      .andExpect(jsonPath("$.userId").value(userId))
      .andExpect(jsonPath("$.content").value("새로운 메모"));

    assertThat(memoRepository.findByUserIdOrderByIdDesc(userId)).hasSize(1);
  }

  @Test
  void 다른_사용자의_메모를_수정하면_403을_반환한다() throws Exception {
    Integer otherUserId = createUser("owner2", "owner2Pass!");
    MemoEntity memo = createMemo(otherUserId, "owner memo");

    UpdateMemoDto dto = new UpdateMemoDto();
    dto.setContent("해킹된 메모");

    mockMvc.perform(put("/api/memo/{id}", memo.getId())
        .cookie(sessionCookie)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("MEMO_FORBIDDEN"))
      .andExpect(jsonPath("$.message").exists());
  }

  private Integer createUser(String username, String rawPassword) {
    UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole("USER");
    return userRepository.save(user).getId();
  }

  private MemoEntity createMemo(Integer ownerId, String content) {
    MemoEntity memo = new MemoEntity();
    memo.setUserId(ownerId);
    memo.setContent(content);
    return memoRepository.save(memo);
  }

  private Cookie performLogin(String username, String rawPassword) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "username", username,
          "password", rawPassword
        ))))
      .andExpect(status().isOk())
      .andReturn();

    Cookie cookie = result.getResponse().getCookie("SESSION");
    if (cookie == null) {
      cookie = result.getResponse().getCookie("JSESSIONID");
    }
    assertThat(cookie).as("세션 쿠키 발급 여부").isNotNull();
    return cookie;
  }
}

