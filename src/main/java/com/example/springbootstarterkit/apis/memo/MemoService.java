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

  public List<MemoEntity> getMemosByUser(Integer userIdx) {
    return repo.findByUserIdx(userIdx);
  }

  public MemoEntity getMemo(Integer idx) {
    return repo.findById(idx)
      .orElseThrow(() -> new RuntimeException("Memo not found: " + idx));
  }
}
