package com.example.aicodereview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String filename;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String aiFeedback;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String staticAnalysisIssuesJson;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String originalCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Review() {}

    public Review(User user, String filename, String aiFeedback, String staticAnalysisIssuesJson, String originalCode) {
        this.user = user;
        this.filename = filename;
        this.aiFeedback = aiFeedback;
        this.staticAnalysisIssuesJson = staticAnalysisIssuesJson;
        this.originalCode = originalCode;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getAiFeedback() { return aiFeedback; }
    public void setAiFeedback(String aiFeedback) { this.aiFeedback = aiFeedback; }

    public String getStaticAnalysisIssuesJson() { return staticAnalysisIssuesJson; }
    public void setStaticAnalysisIssuesJson(String staticAnalysisIssuesJson) { this.staticAnalysisIssuesJson = staticAnalysisIssuesJson; }

    public String getOriginalCode() { return originalCode; }
    public void setOriginalCode(String originalCode) { this.originalCode = originalCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
