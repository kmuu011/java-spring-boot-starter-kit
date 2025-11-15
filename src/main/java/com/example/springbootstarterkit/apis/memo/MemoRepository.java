package com.example.springbootstarterkit.apis.memo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<MemoEntity, Integer> {
  List<MemoEntity> findByUserIdOrderByIdDesc(Integer userId);

  Page<MemoEntity> findByUserIdOrderByIdDesc(Integer userId, Pageable pageable);
}
