package com.example.springbootstarterkit.apis.memo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemoService {
  private final MemoRepository repo;

  public MemoService(MemoRepository repo) {
    this.repo = repo;
  }

  // 회원별 메모 조회
  public List<MemoEntity> getMemosByUser(Integer userIdx) {
    return repo.findByUserIdx(userIdx);
  }

  // 단건 조회
  public MemoEntity getMemo(Integer idx) {
    return repo.findById(idx)
        .orElseThrow(() -> new RuntimeException("Memo not found: " + idx));
  }
}
