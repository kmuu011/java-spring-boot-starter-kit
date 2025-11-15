package com.example.springbootstarterkit.apis.file;

import com.example.springbootstarterkit.apis.user.UserEntity;
import com.example.springbootstarterkit.apis.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FileControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("${file.upload.dir}")
  private String uploadDir;

  private Integer userId;
  private Cookie sessionCookie;

  @BeforeEach
  void setUp() throws Exception {
    fileRepository.deleteAll();
    userRepository.deleteAll();
    userId = createUser("fileUser", "pass123!");
    sessionCookie = performLogin("fileUser", "pass123!");
  }

  @AfterEach
  void cleanUploadedFiles() throws IOException {
    Path root = Path.of(uploadDir);
    if (Files.notExists(root)) {
      return;
    }

    try (var paths = Files.walk(root)) {
      paths.sorted(Comparator.reverseOrder())
        .forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException ignored) {
          }
        });
    }
  }

  @Test
  void 파일을_업로드하면_DB와_응답에_결과가_반환된다() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(
      "files",
      "note.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "hello world".getBytes()
    );

    mockMvc.perform(multipart("/api/file/upload")
        .file(multipartFile)
        .cookie(sessionCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.files", hasSize(1)))
      .andExpect(jsonPath("$.files[0].fileName").value("note"))
      .andExpect(jsonPath("$.files[0].fileType").value("txt"));

    assertThat(fileRepository.findByUserIdOrderByIdDesc(userId, org.springframework.data.domain.PageRequest.of(0, 10)))
      .isNotEmpty();
  }

  @Test
  void 세션없이_파일을_업로드하면_403을_반환한다() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(
      "files",
      "note.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "hello world".getBytes()
    );

    mockMvc.perform(multipart("/api/file/upload")
        .file(multipartFile))
      .andExpect(status().isForbidden());
  }

  @Test
  void 파일_목록은_사용자별로_분리되어_조회된다() throws Exception {
    // 현재 사용자 파일
    fileRepository.saveAll(List.of(
      createFileEntity(userId, "files/sample1.txt", "sample1", "txt", 12L),
      createFileEntity(userId, "files/sample2.txt", "sample2", "txt", 22L)
    ));

    // 다른 사용자 파일
    Integer otherUser = createUser("other", "otherPass!");
    fileRepository.save(createFileEntity(otherUser, "files/other.txt", "other", "txt", 10L));

    mockMvc.perform(get("/api/file")
        .cookie(sessionCookie)
        .param("page", "1")
        .param("count", "10"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(2))
      .andExpect(jsonPath("$.files", hasSize(2)))
      .andExpect(jsonPath("$.files[0].userId").value(userId));
  }

  @Test
  void 파일을_삭제하면_DB와_물리_파일이_제거된다() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(
      "files",
      "delete.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "delete me".getBytes()
    );

    var uploadResult = mockMvc.perform(multipart("/api/file/upload")
        .file(multipartFile)
        .cookie(sessionCookie))
      .andExpect(status().isOk())
      .andReturn();

    Integer fileId = objectMapper.readTree(uploadResult.getResponse().getContentAsString())
      .get("files").get(0).get("id").asInt();
    String fileKey = objectMapper.readTree(uploadResult.getResponse().getContentAsString())
      .get("files").get(0).get("fileKey").asText();
    Path filePath = Path.of(uploadDir).resolve(fileKey);
    assertThat(Files.exists(filePath)).isTrue();

    mockMvc.perform(delete("/api/file/{id}", fileId)
        .cookie(sessionCookie))
      .andExpect(status().isOk());

    assertThat(fileRepository.findById(fileId)).isNotPresent();
    assertThat(Files.exists(filePath)).isFalse();
  }

  private Integer createUser(String username, String rawPassword) {
    UserEntity entity = new UserEntity();
    entity.setUsername(username);
    entity.setPassword(passwordEncoder.encode(rawPassword));
    entity.setRole("USER");
    return userRepository.save(entity).getId();
  }

  private FileEntity createFileEntity(Integer ownerId, String fileKey, String name, String type, Long size) {
    FileEntity entity = new FileEntity();
    entity.setUserId(ownerId);
    entity.setFileKey(fileKey);
    entity.setFileName(name);
    entity.setFileType(type);
    entity.setFileSize(size);
    return entity;
  }

  private Cookie performLogin(String username, String rawPassword) throws Exception {
    var response = mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
          java.util.Map.of("username", username, "password", rawPassword)
        )))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();

    Cookie cookie = response.getCookie("SESSION");
    if (cookie == null) {
      cookie = response.getCookie("JSESSIONID");
    }
    assertThat(cookie).as("세션 쿠키 발급 여부").isNotNull();
    return cookie;
  }
}

