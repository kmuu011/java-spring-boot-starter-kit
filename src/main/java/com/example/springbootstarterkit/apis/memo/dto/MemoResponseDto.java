package com.example.springbootstarterkit.apis.memo.dto;

import com.example.springbootstarterkit.apis.memo.MemoEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemoResponseDto {

	private final Integer id;
	private final Integer userId;
	private final String content;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private MemoResponseDto(Integer id, Integer userId, String content,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.userId = userId;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static MemoResponseDto from(MemoEntity entity) {
		return new MemoResponseDto(
			entity.getId(),
			entity.getUserId(),
			entity.getContent(),
			entity.getCreatedAt(),
			entity.getUpdatedAt()
		);
	}
}
