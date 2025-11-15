package com.example.springbootstarterkit.apis.file;

import com.example.springbootstarterkit.apis.file.dto.FileListResponseDto;
import com.example.springbootstarterkit.apis.file.dto.FileUploadResponseDto;
import com.example.springbootstarterkit.apis.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @GetMapping
  public FileListResponseDto getFileList(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int count,
      @RequestParam(required = false) String search
  ) {
    return fileService.getFileList(user.getId(), page, count, search);
  }

  @PostMapping("/upload")
  public FileUploadResponseDto uploadFiles(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam("files") List<MultipartFile> files
  ) {
    return fileService.uploadFiles(user.getId(), files);
  }

  @PostMapping("/{fileId}/download")
  public void downloadFile(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Integer fileId,
      HttpServletResponse response
  ) {
    FileEntity file = fileService.getFileById(fileId, user.getId());
    Path filePath = fileService.getFilePath(file.getFileKey());

    if (!Files.exists(filePath)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다");
    }

    try {
      String fileName = file.getFileName() + "." + file.getFileType();
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
      response.setContentLengthLong(file.getFileSize());

      Files.copy(filePath, response.getOutputStream());
      response.getOutputStream().flush();
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 중 오류 발생");
    }
  }

  @DeleteMapping("/{fileId}")
  public void deleteFile(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Integer fileId
  ) {
    fileService.deleteFile(fileId, user.getId());
  }
}
