package com.example.springbootstarterkit.apis.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {
  @Schema(description = "사용자명", example = "qa1")
  @JsonProperty("username")
  private String username;

  @Schema(description = "비밀번호", example = "qa1")
  @JsonProperty("password")
  private String password;
}