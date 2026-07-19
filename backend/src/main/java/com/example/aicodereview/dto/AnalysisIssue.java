package com.example.aicodereview.dto;

public class AnalysisIssue {
    private String file;
    private int beginLine;
    private int endLine;
    private String rule;
    private String message;
    private String priority;

    public AnalysisIssue(String file, int beginLine, int endLine, String rule, String message, String priority) {
        this.file = file;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.rule = rule;
        this.message = message;
        this.priority = priority;
    }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
    public int getBeginLine() { return beginLine; }
    public void setBeginLine(int beginLine) { this.beginLine = beginLine; }
    public int getEndLine() { return endLine; }
    public void setEndLine(int endLine) { this.endLine = endLine; }
    public String getRule() { return rule; }
    public void setRule(String rule) { this.rule = rule; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
