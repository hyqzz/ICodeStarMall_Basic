/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.seafoodmall.backend.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/files")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(uploadPath, filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回可访问的URL
            String relativePath = urlPrefix + filename;
            log.info("File uploaded successfully: {}", relativePath);
            
            return Result.success(relativePath);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return Result.error("文件上传失败");
        }
    }
} 
