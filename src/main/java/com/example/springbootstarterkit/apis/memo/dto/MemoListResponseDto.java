package com.example.springbootstarterkit.apis.memo.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class MemoListResponseDto {

	private final List<MemoResponseDto> memos;
	private final long totalElements;
	private final int totalPages;
	private final int currentPage;
	private final int pageSize;

	public MemoListResponseDto(Page<MemoResponseDto> page) {
		this.memos = page.getContent();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.currentPage = page.getNumber() + 1; // 0-based to 1-based
		this.pageSize = page.getSize();
	}
}
