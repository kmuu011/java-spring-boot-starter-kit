package com.example.springbootstarterkit.apis.file.dto;

import com.example.springbootstarterkit.apis.file.FileEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FileResponseDto {

	private final Integer id;
	private final Integer userId;
	private final String fileKey;
	private final String fileName;
	private final String fileType;
	private final Long fileSize;
	private final LocalDateTime createdAt;

	private FileResponseDto(Integer id, Integer userId, String fileKey, String fileName,
		String fileType, Long fileSize, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.fileKey = fileKey;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.createdAt = createdAt;
	}

	public static FileResponseDto from(FileEntity entity) {
		return new FileResponseDto(
			entity.getId(),
			entity.getUserId(),
			entity.getFileKey(),
			entity.getFileName(),
			entity.getFileType(),
			entity.getFileSize(),
			entity.getCreatedAt()
		);
	}
}
