import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Auth from './Auth';
import Dashboard from './Dashboard';
import Results from './Results';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Routes>
          <Route path="/" element={<Navigate to="/auth" replace />} />
          <Route path="/auth" element={<Auth />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/results" element={<Results />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
