package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/b")
public class FileUploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    @Value("${upload.base-url:http://localhost:8080/uploads/}")
    private String baseUrl;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("只允许上传 JPG/PNG/GIF/WebP 图片");
        }

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = Paths.get(uploadPath).toAbsolutePath().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        file.transferTo(new File(dir, filename).getAbsoluteFile());
        return Result.success(baseUrl + filename);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}
