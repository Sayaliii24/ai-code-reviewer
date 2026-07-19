package com.example.aicodereview.dto;

import java.util.List;

public class AiReviewResult {
    private String aiFeedback;
    private List<AnalysisIssue> staticAnalysisIssues;
    private String originalCode;

    public AiReviewResult(String aiFeedback, List<AnalysisIssue> staticAnalysisIssues, String originalCode) {
        this.aiFeedback = aiFeedback;
        this.staticAnalysisIssues = staticAnalysisIssues;
        this.originalCode = originalCode;
    }

    public String getAiFeedback() { return aiFeedback; }
    public void setAiFeedback(String aiFeedback) { this.aiFeedback = aiFeedback; }

    public List<AnalysisIssue> getStaticAnalysisIssues() { return staticAnalysisIssues; }
    public void setStaticAnalysisIssues(List<AnalysisIssue> staticAnalysisIssues) { this.staticAnalysisIssues = staticAnalysisIssues; }

    public String getOriginalCode() { return originalCode; }
    public void setOriginalCode(String originalCode) { this.originalCode = originalCode; }
}
