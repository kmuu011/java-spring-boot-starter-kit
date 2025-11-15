package com.example.springbootstarterkit.apis.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListRequestDto {

	@Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
	private int page = 1;

	@Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
	private int count = 10;

	@Schema(description = "검색어 (파일명 또는 확장자)", example = "jpg")
	private String search;
}
