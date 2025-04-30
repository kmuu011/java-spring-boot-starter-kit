package com.example.springbootstarterkit.apis.memo;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemoService {
  private final MemoRepository repo;

  public MemoService(MemoRepository repo) {
    this.repo = repo;
  }

  public List<MemoEntity> getMemosByUser(Integer userid) {
    return repo.findByUserIdOrderByIdDesc(userid);
  }

  public MemoEntity getMemo(Integer id, Integer userid) throws AccessDeniedException {
    MemoEntity memo = repo.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Memo not found: " + id));

    if (!memo.getUserId().equals(userid)) {
      throw new AccessDeniedException("권한이 없습니다.");
    }

    return memo;
  }

  @Transactional
  public MemoEntity createMemo(Integer userid, String content) {
    MemoEntity memo = new MemoEntity();
    memo.setUserId(userid);
    memo.setContent(content);

    return repo.save(memo);
  }

  @Transactional
  public MemoEntity updateMemo(Integer id, Integer userid, String newContent) {
    MemoEntity memo = repo.findById(id)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Memo not found: " + id
      ));

    if (!memo.getUserId().equals(userid)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN, "수정 권한이 없습니다."
      );
    }

    memo.setContent(newContent);

    return repo.save(memo);
  }

  @Transactional
  public void deleteMemo(Integer id, Integer userid) {
    MemoEntity memo = repo.findById(id)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Memo not found: " + id
      ));

    if (!memo.getUserId().equals(userid)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN, "삭제 권한이 없습니다."
      );
    }

    repo.delete(memo);
  }

}
