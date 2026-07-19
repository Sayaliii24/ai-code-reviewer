import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [pastReviews, setPastReviews] = useState([]);
  const fileInputRef = useRef(null);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  if (!token) {
    navigate('/auth');
    return null;
  }

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/v1/reviews`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (res.ok) {
          const data = await res.json();
          setPastReviews(data);
        }
      } catch (err) {
        console.error("Failed to fetch past reviews", err);
      }
    };
    fetchReviews();
  }, [token]);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    setLoading(true);
    
    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await fetch(`${import.meta.env.VITE_API_URL}/api/v1/upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });
      if (res.ok) {
        const data = await res.json();
        navigate('/results', { state: { results: data } });
      } else {
        alert('Failed to upload file.');
      }
    } catch (err) {
      alert('Error uploading file.');
    } finally {
      setLoading(false);
    }
  };

  const viewPastReview = (review) => {
    const issues = review.staticAnalysisIssuesJson ? JSON.parse(review.staticAnalysisIssuesJson) : [];
    navigate('/results', { state: { results: { aiFeedback: review.aiFeedback, staticAnalysisIssues: issues, originalCode: review.originalCode } } });
  };

  const logout = () => {
    localStorage.removeItem('token');
    navigate('/auth');
  };

  return (
    <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto', width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '3rem' }}>
        <h1 className="text-gradient">AI Code Reviewer</h1>
        <button className="btn-secondary" onClick={logout}>Logout</button>
      </div>

      <div className="glass-card" style={{ marginBottom: '2rem' }}>
        <h2 style={{ marginBottom: '1.5rem' }}>Upload Code</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
          Upload a single <code>.java</code> file or a <code>.zip</code> archive containing your project. Our AI will analyze it for bugs, code smells, and vulnerabilities.
        </p>
        
        <div 
          className="upload-zone" 
          onClick={() => fileInputRef.current.click()}
        >
          <div className="upload-icon">📁</div>
          {file ? (
            <h3 style={{ color: 'var(--success)' }}>{file.name}</h3>
          ) : (
            <h3>Click or drag file to this area to upload</h3>
          )}
          <input 
            type="file" 
            ref={fileInputRef} 
            onChange={handleFileChange} 
            style={{ display: 'none' }} 
            accept=".java,.zip" 
          />
        </div>

        <div style={{ marginTop: '2rem', textAlign: 'right' }}>
          <button 
            className="btn-primary" 
            disabled={!file || loading} 
            onClick={handleUpload}
          >
            {loading ? 'Analyzing Code...' : 'Run AI Analysis'}
          </button>
        </div>
      </div>

      {pastReviews.length > 0 && (
        <div className="glass-card">
          <h2 style={{ marginBottom: '1.5rem' }}>Past Reviews</h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {pastReviews.map((review) => (
              <div 
                key={review.id} 
                style={{ 
                  padding: '1rem', 
                  border: '1px solid var(--border-color)', 
                  borderRadius: '8px', 
                  display: 'flex', 
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  background: 'rgba(255,255,255,0.02)'
                }}
              >
                <div>
                  <h4 style={{ margin: '0 0 0.5rem 0', color: 'var(--text-primary)' }}>{review.filename}</h4>
                  <small style={{ color: 'var(--text-secondary)' }}>
                    {new Date(review.createdAt).toLocaleString()}
                  </small>
                </div>
                <button className="btn-secondary" onClick={() => viewPastReview(review)}>View Report</button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
