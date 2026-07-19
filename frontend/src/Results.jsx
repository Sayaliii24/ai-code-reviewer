import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import ReactDiffViewer from 'react-diff-viewer-continued';

export default function Results() {
  const location = useLocation();
  const navigate = useNavigate();
  const { results } = location.state || {};

  if (!results) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center' }}>
        <h2>No results found.</h2>
        <button className="btn-primary" onClick={() => navigate('/dashboard')}>Go Back</button>
      </div>
    );
  }

  const { aiFeedback, staticAnalysisIssues, originalCode } = results;

  // Extract <fixed_code> block if present
  let cleanAiFeedback = aiFeedback || '';
  let fixedCode = null;

  const fixedCodeMatch = cleanAiFeedback.match(/<fixed_code>([\s\S]*?)<\/fixed_code>/);
  if (fixedCodeMatch) {
    fixedCode = fixedCodeMatch[1].trim();
    // Remove the block from the feedback string so it doesn't render in markdown
    cleanAiFeedback = cleanAiFeedback.replace(/<fixed_code>[\s\S]*?<\/fixed_code>/, '').trim();
  }

  return (
    <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <h1 className="text-gradient">Analysis Report</h1>
        <button className="btn-secondary" onClick={() => navigate('/dashboard')}>Back to Dashboard</button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
        
        {/* Static Analysis Section */}
        <div className="glass-card">
          <h2 style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', marginBottom: '1rem' }}>
            Static Analysis (PMD)
          </h2>
          {staticAnalysisIssues && staticAnalysisIssues.length > 0 ? (
            <div style={{ overflowX: 'auto', maxHeight: '400px', overflowY: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                <thead>
                  <tr style={{ color: 'var(--text-secondary)' }}>
                    <th style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>Priority</th>
                    <th style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>File</th>
                    <th style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>Line</th>
                    <th style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>Issue</th>
                  </tr>
                </thead>
                <tbody>
                  {staticAnalysisIssues.map((issue, idx) => (
                    <tr key={idx}>
                      <td style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)', color: issue.priority === 'High' ? 'var(--danger)' : 'var(--text-primary)' }}>
                        {issue.priority}
                      </td>
                      <td style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>{issue.file.split('/').pop()}</td>
                      <td style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>{issue.beginLine}</td>
                      <td style={{ padding: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>{issue.message}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p style={{ color: 'var(--success)' }}>No static analysis issues found! Great job.</p>
          )}
        </div>

        {/* AI Feedback Section */}
        <div className="glass-card">
          <h2 style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', marginBottom: '1rem' }}>
            AI Code Review (Gemini)
          </h2>
          <div className="markdown-body">
            <ReactMarkdown>{cleanAiFeedback}</ReactMarkdown>
          </div>
        </div>

        {/* Diff Viewer Section */}
        {fixedCode && originalCode && (
          <div className="glass-card">
            <h2 style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', marginBottom: '1rem' }}>
              Suggested Code Refactor
            </h2>
            <div style={{ borderRadius: '8px', overflow: 'hidden' }}>
              <ReactDiffViewer
                oldValue={originalCode}
                newValue={fixedCode}
                splitView={true}
                useDarkTheme={true}
                styles={{
                  variables: {
                    dark: {
                      diffViewerBackground: 'rgba(34, 38, 48, 0.4)',
                      diffViewerTitleBackground: 'rgba(34, 38, 48, 0.8)',
                    }
                  }
                }}
              />
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
