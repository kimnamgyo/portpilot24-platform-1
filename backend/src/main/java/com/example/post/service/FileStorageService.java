package com.example.post.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> savedPaths = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Files.copy(file.getInputStream(), this.root.resolve(filename));
                savedPaths.add("/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file: " + file.getOriginalFilename());
            }
        }
        return savedPaths;
    }
}
