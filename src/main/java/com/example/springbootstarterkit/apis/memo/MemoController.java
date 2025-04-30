package com.example.springbootstarterkit.apis.memo;

import com.example.springbootstarterkit.apis.memo.dto.CreateMemoDto;
import com.example.springbootstarterkit.apis.memo.dto.UpdateMemoDto;
import com.example.springbootstarterkit.apis.user.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/memo")
public class MemoController {
  private final MemoService memoService;

  public MemoController(final MemoService memoService) {
    this.memoService = memoService;
  }

  @GetMapping
  public List<MemoEntity> selectList(
    @AuthenticationPrincipal CustomUserDetails user
  ) {
    return memoService.getMemosByUser(user.getId());
  }

  @GetMapping("/{id}")
  public MemoEntity selectOne(
    @PathVariable Integer id,
    @AuthenticationPrincipal CustomUserDetails user
  ) throws AccessDeniedException {
    return memoService.getMemo(id, user.getId());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MemoEntity insert(
    @AuthenticationPrincipal CustomUserDetails user,
    @RequestBody @Valid CreateMemoDto dto
  ) {
    return memoService.createMemo(user.getId(), dto.getContent());
  }

  @PutMapping("/{id}")
  public MemoEntity update(
    @PathVariable Integer id,
    @AuthenticationPrincipal CustomUserDetails user,
    @RequestBody @Valid UpdateMemoDto dto
  ) {
    return memoService.updateMemo(id, user.getId(), dto.getContent());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @PathVariable Integer id,
    @AuthenticationPrincipal CustomUserDetails user
  ) {
    memoService.deleteMemo(id, user.getId());
  }
}
