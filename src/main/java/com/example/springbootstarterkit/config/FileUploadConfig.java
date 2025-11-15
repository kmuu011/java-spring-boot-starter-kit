package com.example.springbootstarterkit.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig {

  @Value("${file.upload.dir}")
  private String uploadDir;

  @PostConstruct
  public void init() {
    Path uploadPath = Paths.get(uploadDir);
    if (!Files.exists(uploadPath)) {
      try {
        Files.createDirectories(uploadPath);
        System.out.println("✅ Upload directory created: " + uploadPath.toAbsolutePath());
      } catch (IOException e) {
        throw new RuntimeException("❌ Could not create upload directory: " + uploadPath.toAbsolutePath(), e);
      }
    } else {
      System.out.println("✅ Upload directory exists: " + uploadPath.toAbsolutePath());
    }
  }
}
