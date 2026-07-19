package com.example.aicodereview.controller;

import com.example.aicodereview.dto.AiReviewResult;
import com.example.aicodereview.dto.AnalysisIssue;
import com.example.aicodereview.model.Review;
import com.example.aicodereview.model.User;
import com.example.aicodereview.repository.ReviewRepository;
import com.example.aicodereview.repository.UserRepository;
import com.example.aicodereview.service.AiService;
import com.example.aicodereview.service.FileService;
import com.example.aicodereview.service.StaticAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    private final FileService fileService;
    private final StaticAnalysisService staticAnalysisService;
    private final AiService aiService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UploadController(FileService fileService, StaticAnalysisService staticAnalysisService, AiService aiService, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.fileService = fileService;
        this.staticAnalysisService = staticAnalysisService;
        this.aiService = aiService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @PostMapping
    public ResponseEntity<AiReviewResult> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            List<File> processedFiles = fileService.processUploadedFile(file);
            List<AnalysisIssue> issues = staticAnalysisService.runPmdAnalysis(processedFiles);
            String aiFeedback = aiService.getAiReview(processedFiles, issues);
            
            // Build originalCode string
            StringBuilder originalCodeBuilder = new StringBuilder();
            for (File f : processedFiles) {
                if (f.getName().endsWith(".java")) {
                    originalCodeBuilder.append("// File: ").append(f.getName()).append("\n");
                    originalCodeBuilder.append(java.nio.file.Files.readString(f.toPath())).append("\n\n");
                }
            }
            String originalCode = originalCodeBuilder.toString();

            AiReviewResult result = new AiReviewResult(aiFeedback, issues, originalCode);

            // Save review to database
            if (authentication != null && authentication.getName() != null) {
                Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
                if (userOpt.isPresent()) {
                    String issuesJson = objectMapper.writeValueAsString(issues);
                    Review review = new Review(userOpt.get(), file.getOriginalFilename(), aiFeedback, issuesJson, originalCode);
                    reviewRepository.save(review);
                }
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
