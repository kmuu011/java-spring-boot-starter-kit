package com.example.springbootstarterkit.apis.file;

import com.example.springbootstarterkit.apis.file.dto.FileListResponseDto;
import com.example.springbootstarterkit.apis.file.dto.FileResponseDto;
import com.example.springbootstarterkit.apis.file.dto.FileUploadResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FileService {

	private final FileRepository fileRepository;
	private final String uploadDir;

	public FileService(
		FileRepository fileRepository,
		@Value("${file.upload.dir}") String uploadDir
	) {
		this.fileRepository = fileRepository;
		this.uploadDir = uploadDir;
	}

	public FileListResponseDto getFileList(Integer userId, int page, int count, String search) {
		Pageable pageable = PageRequest.of(page - 1, count); // 1-based to 0-based

		Page<FileEntity> filePage;
		if (search != null && !search.trim().isEmpty()) {
			filePage = fileRepository.findByUserIdWithSearch(userId, search.trim(), pageable);
		} else {
			filePage = fileRepository.findByUserIdOrderByIdDesc(userId, pageable);
		}

		Page<FileResponseDto> dtoPage = filePage.map(FileResponseDto::from);
		return new FileListResponseDto(dtoPage);
	}

	@Transactional
	public FileUploadResponseDto uploadFiles(Integer userId, List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다");
		}

		// 업로드 디렉토리 생성 (없을 경우)
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			try {
				Files.createDirectories(uploadPath);
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "업로드 디렉토리 생성 실패");
			}
		}

		List<FileResponseDto> uploadedFiles = new ArrayList<>();

		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}

			try {
				// 원본 파일명과 확장자 분리
				String originalFilename = file.getOriginalFilename();
				if (originalFilename == null || originalFilename.isEmpty()) {
					continue;
				}

				String fileExtension = "";
				String fileNameWithoutExt = originalFilename;
				int dotIndex = originalFilename.lastIndexOf('.');
				if (dotIndex > 0) {
					fileExtension = originalFilename.substring(dotIndex + 1);
					fileNameWithoutExt = originalFilename.substring(0, dotIndex);
				}

				// 고유한 파일키 생성 (UUID + timestamp)
				String uniqueFileKey = String.format("files/%s_%d.%s",
					UUID.randomUUID().toString().replace("-", ""),
					System.currentTimeMillis(),
					fileExtension
				);

				// 파일 저장
				Path filePath = uploadPath.resolve(uniqueFileKey);
				Files.createDirectories(filePath.getParent()); // files 하위 디렉토리 생성
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				// DB에 메타데이터 저장
				FileEntity fileEntity = new FileEntity();
				fileEntity.setUserId(userId);
				fileEntity.setFileKey(uniqueFileKey);
				fileEntity.setFileName(fileNameWithoutExt);
				fileEntity.setFileType(fileExtension);
				fileEntity.setFileSize(file.getSize());

				FileEntity savedEntity = fileRepository.save(fileEntity);
				uploadedFiles.add(FileResponseDto.from(savedEntity));

			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"파일 저장 중 오류 발생: " + file.getOriginalFilename());
			}
		}

		if (uploadedFiles.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 파일이 없습니다");
		}

		return FileUploadResponseDto.success(uploadedFiles);
	}

	public FileEntity getFileById(Integer fileId, Integer userId) {
		FileEntity file = fileRepository.findById(fileId)
			.orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다: " + fileId));

		// 권한 검증
		if (!file.getUserId().equals(userId)) {
			throw new AccessDeniedException("파일에 접근할 권한이 없습니다");
		}

		return file;
	}

	public Path getFilePath(String fileKey) {
		return Paths.get(uploadDir).resolve(fileKey);
	}

	@Transactional
	public void deleteFile(Integer fileId, Integer userId) {
		FileEntity file = getFileById(fileId, userId);

		// 물리 파일 삭제
		Path filePath = getFilePath(file.getFileKey());
		try {
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류 발생");
		}

		// DB 레코드 삭제
		fileRepository.delete(file);
	}
}
