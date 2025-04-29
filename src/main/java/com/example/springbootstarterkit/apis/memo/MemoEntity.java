package com.example.springbootstarterkit.apis.memo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "memo")
public class MemoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer idx;

  @Column(nullable = false)
  private Integer userIdx;

  @Column(nullable = false, length = 1000)
  private String content;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
