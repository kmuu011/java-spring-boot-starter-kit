package com.example.springbootstarterkit.apis.memo;

import com.example.springbootstarterkit.apis.memo.dto.CreateMemoDto;
import com.example.springbootstarterkit.apis.memo.dto.MemoListRequestDto;
import com.example.springbootstarterkit.apis.memo.dto.MemoListResponseDto;
import com.example.springbootstarterkit.apis.memo.dto.UpdateMemoDto;
import com.example.springbootstarterkit.apis.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Memo", description = "메모 관리 API")
@RestController
@RequestMapping("/api/memo")
public class MemoController {
  private final MemoService memoService;

  public MemoController(final MemoService memoService) {
    this.memoService = memoService;
  }

  @Operation(summary = "메모 목록 조회 (페이징)", description = "사용자의 메모 목록을 페이징하여 조회합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @GetMapping
  public MemoListResponseDto selectList(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      MemoListRequestDto request
  ) {
    return memoService.getMemoListWithPaging(user.getId(), request.getPage(), request.getCount());
  }

  @Operation(summary = "메모 단건 조회", description = "메모 ID로 메모를 조회합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "403", description = "권한 없음"),
      @ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @GetMapping("/{id}")
  public MemoEntity selectOne(
      @Parameter(description = "메모 ID", required = true, example = "1")
      @PathVariable Integer id,
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
  ) {
    return memoService.getMemo(id, user.getId());
  }

  @Operation(summary = "메모 생성", description = "새로운 메모를 생성합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MemoEntity insert(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody @Valid CreateMemoDto dto
  ) {
    return memoService.createMemo(user.getId(), dto.getContent());
  }

  @Operation(summary = "메모 수정", description = "메모의 내용을 수정합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "403", description = "권한 없음"),
      @ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @PutMapping("/{id}")
  public MemoEntity update(
      @Parameter(description = "메모 ID", required = true, example = "1")
      @PathVariable Integer id,
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody @Valid UpdateMemoDto dto
  ) {
    return memoService.updateMemo(id, user.getId(), dto.getContent());
  }

  @Operation(summary = "메모 삭제", description = "메모를 삭제합니다")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "403", description = "권한 없음"),
      @ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "메모 ID", required = true, example = "1")
      @PathVariable Integer id,
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
  ) {
    memoService.deleteMemo(id, user.getId());
  }
}
