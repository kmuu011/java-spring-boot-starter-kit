package com.example.springbootstarterkit.apis.file.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class FileListResponseDto {

	private final List<FileResponseDto> files;
	private final long totalElements;
	private final int totalPages;
	private final int currentPage;
	private final int pageSize;

	public FileListResponseDto(Page<FileResponseDto> page) {
		this.files = page.getContent();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.currentPage = page.getNumber() + 1; // 0-based to 1-based
		this.pageSize = page.getSize();
	}
}
