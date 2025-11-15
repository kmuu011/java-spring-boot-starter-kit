package com.example.springbootstarterkit.apis.file;

import com.example.springbootstarterkit.apis.file.dto.FileListRequestDto;
import com.example.springbootstarterkit.apis.file.dto.FileListResponseDto;
import com.example.springbootstarterkit.apis.file.dto.FileUploadResponseDto;
import com.example.springbootstarterkit.apis.user.CustomUserDetails;
import com.example.springbootstarterkit.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Tag(name = "File", description = "파일 업로드/다운로드/관리 API")
@RestController
@RequestMapping("/api/file")
public class FileController {

  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @Operation(summary = "파일 목록 조회", description = "사용자가 업로드한 파일 목록을 페이징과 검색 기능으로 조회합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @GetMapping
  public FileListResponseDto getFileList(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      FileListRequestDto request
  ) {
    return fileService.getFileList(user.getId(), request.getPage(), request.getCount(), request.getSearch());
  }

  @Operation(summary = "파일 업로드", description = "하나 이상의 파일을 서버에 업로드합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "업로드 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 없음 또는 유효하지 않은 파일)"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public FileUploadResponseDto uploadFiles(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "업로드할 파일들 (최대 20MB)", required = true)
      @RequestParam("files") List<MultipartFile> files
  ) {
    return fileService.uploadFiles(user.getId(), files);
  }

  @Operation(summary = "파일 다운로드", description = "파일 ID로 파일을 다운로드합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "다운로드 성공"),
      @ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 파일)"),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @PostMapping("/{fileId}/download")
  public void downloadFile(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "파일 ID", required = true, example = "1")
      @PathVariable Integer fileId,
      HttpServletResponse response
  ) {
    FileEntity file = fileService.getFileById(fileId, user.getId());
    Path filePath = fileService.getFilePath(file.getFileKey());

    if (!Files.exists(filePath)) {
      throw new BusinessException("FILE_NOT_FOUND", HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다");
    }

    try {
      String fileName = file.getFileName() + "." + file.getFileType();
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
      response.setContentLengthLong(file.getFileSize());

      Files.copy(filePath, response.getOutputStream());
      response.getOutputStream().flush();
    } catch (IOException e) {
      throw new BusinessException("FILE_DOWNLOAD_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 중 오류 발생");
    }
  }

  @Operation(summary = "파일 삭제", description = "파일 ID로 파일을 삭제합니다 (물리 파일 + DB 레코드)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 파일)"),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @DeleteMapping("/{fileId}")
  public void deleteFile(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "파일 ID", required = true, example = "1")
      @PathVariable Integer fileId
  ) {
    fileService.deleteFile(fileId, user.getId());
  }
}
