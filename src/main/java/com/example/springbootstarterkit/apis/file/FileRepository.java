package com.example.springbootstarterkit.apis.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {

	Page<FileEntity> findByUserIdOrderByIdDesc(Integer userId, Pageable pageable);

	@Query("SELECT f FROM FileEntity f WHERE f.userId = :userId " +
		"AND (f.fileName LIKE %:search% OR f.fileType LIKE %:search%) " +
		"ORDER BY f.id DESC")
	Page<FileEntity> findByUserIdWithSearch(
		@Param("userId") Integer userId,
		@Param("search") String search,
		Pageable pageable
	);
}
