package com.researchworkbench.service;

import com.researchworkbench.config.AppProperties;
import com.researchworkbench.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(AppProperties appProperties) throws IOException {
        this.root = Paths.get(appProperties.storage().root()).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
        Files.createDirectories(this.root.resolve("avatars"));
        Files.createDirectories(this.root.resolve("knowledge"));
    }

    public String store(MultipartFile file, String category) {
        if (file.isEmpty()) {
            throw new BadRequestException("上传文件不能为空");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String generatedName = UUID.randomUUID() + extension;
        Path target = root.resolve(category).resolve(generatedName).normalize();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BadRequestException("文件保存失败: " + ex.getMessage());
        }

        return "/storage/" + category + "/" + generatedName;
    }
}
