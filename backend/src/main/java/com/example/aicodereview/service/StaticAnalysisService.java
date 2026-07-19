package com.example.aicodereview.service;

import com.example.aicodereview.dto.AnalysisIssue;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaticAnalysisService {

    public List<AnalysisIssue> runPmdAnalysis(List<File> javaFiles) {
        List<AnalysisIssue> issues = new ArrayList<>();

        if (javaFiles == null || javaFiles.isEmpty()) {
            return issues;
        }

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(net.sourceforge.pmd.RulePriority.LOW);
        
        // Use standard rulesets for Java: basic, braces, design, unusedcode, etc.
        configuration.setRuleSets("category/java/bestpractices.xml,category/java/codestyle.xml,category/java/design.xml,category/java/errorprone.xml,category/java/performance.xml");

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            for (File file : javaFiles) {
                pmd.files().addFile(file.toPath());
            }

            Report report = pmd.performAnalysisAndCollectReport();

            for (RuleViolation violation : report.getViolations()) {
                issues.add(new AnalysisIssue(
                        violation.getFilename(),
                        violation.getBeginLine(),
                        violation.getEndLine(),
                        violation.getRule().getName(),
                        violation.getDescription(),
                        violation.getRule().getPriority().getName()
                ));
            }
        }

        return issues;
    }
}
