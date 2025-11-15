package com.example.springbootstarterkit.apis.file.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FileUploadResponseDto {

	private final boolean success;
	private final String message;
	private final List<FileResponseDto> files;

	public FileUploadResponseDto(boolean success, String message, List<FileResponseDto> files) {
		this.success = success;
		this.message = message;
		this.files = files;
	}

	public static FileUploadResponseDto success(List<FileResponseDto> files) {
		return new FileUploadResponseDto(true, "파일 업로드 성공", files);
	}
}
