package com.example.springbootstarterkit.apis.memo;

import com.example.springbootstarterkit.apis.memo.dto.MemoListResponseDto;
import com.example.springbootstarterkit.apis.memo.dto.MemoResponseDto;
import com.example.springbootstarterkit.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

  public List<MemoEntity> getMemosByUser(Integer userid) {
    return repo.findByUserIdOrderByIdDesc(userid);
  }

  public MemoListResponseDto getMemoListWithPaging(Integer userId, int page, int count) {
    Pageable pageable = PageRequest.of(page - 1, count); // 1-based to 0-based
    Page<MemoEntity> memoPage = repo.findByUserIdOrderByIdDesc(userId, pageable);
    Page<MemoResponseDto> dtoPage = memoPage.map(MemoResponseDto::from);
    return new MemoListResponseDto(dtoPage);
  }

  public MemoEntity getMemo(Integer id, Integer userid) {
    MemoEntity memo = repo.findById(id)
      .orElseThrow(() -> new BusinessException("MEMO_NOT_FOUND", HttpStatus.NOT_FOUND, "Memo not found: " + id));

    if (!memo.getUserId().equals(userid)) {
      throw new BusinessException("MEMO_FORBIDDEN", HttpStatus.FORBIDDEN, "권한이 없습니다.");
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
      .orElseThrow(() -> new BusinessException("MEMO_NOT_FOUND", HttpStatus.NOT_FOUND, "Memo not found: " + id));

    if (!memo.getUserId().equals(userid)) {
      throw new BusinessException("MEMO_FORBIDDEN", HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
    }

    memo.setContent(newContent);

    return repo.save(memo);
  }

  @Transactional
  public void deleteMemo(Integer id, Integer userid) {
    MemoEntity memo = repo.findById(id)
      .orElseThrow(() -> new BusinessException("MEMO_NOT_FOUND", HttpStatus.NOT_FOUND, "Memo not found: " + id));

    if (!memo.getUserId().equals(userid)) {
      throw new BusinessException("MEMO_FORBIDDEN", HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
    }

    repo.delete(memo);
  }

}
