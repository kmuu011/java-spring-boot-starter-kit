package com.example.springbootstarterkit.apis.memo;

import com.example.springbootstarterkit.apis.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    return memoService.getMemosByUser(userDetails.getIdx());
  }
}
