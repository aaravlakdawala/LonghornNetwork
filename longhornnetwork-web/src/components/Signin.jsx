import React, { useState } from 'react';

export default function Signin({ onSignIn }) {
  const [name, setName] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const SERVER_URL = 'http://localhost:8080';

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!name || name.trim().length === 0) {
      setError('Please enter your name');
      return;
    }
    setLoading(true);
    try {
      const res = await fetch(`${SERVER_URL}/api/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: name.trim() })
      });
      const body = await res.json();
      if (!res.ok) {
        setError((body && body.message) || 'Login failed');
        setLoading(false);
        return;
      }

      // Save token and notify parent
      if (body.token) {
        localStorage.setItem('authToken', body.token);
      }
      if (body.student && body.student.name) {
        if (onSignIn) onSignIn(body.student.name);
      }
    } catch (err) {
      setError('Login request failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginTop: 80 }}>
      <form onSubmit={submit} style={{ background: 'rgba(0, 0, 0, 0.95)', padding: 24, borderRadius: 8, width: 360, boxShadow: '0 6px 18px rgba(0,0,0,0.12)' }}>
        <h3 style={{ marginTop: 0, marginBottom: 12 }}>Sign In</h3>
        <div style={{ marginBottom: 12 }}>
          <label style={{ display: 'block', fontSize: 14, marginBottom: 6 }}>Name</label>
          <input value={name} onChange={(e) => setName(e.target.value)} style={{ width: '100%', padding: '8px 10px', fontSize: 14, borderRadius: 4, border: '1px solid #ddd' }} />
        </div>

        {error && <div style={{ color: 'crimson', marginBottom: 12 }}>{error}</div>}

        <div style={{ display: 'flex', gap: 8 }}>
          <button type="submit" disabled={loading} style={{ flex: 1, padding: '8px 12px', background: '#06b6d4', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </div>
      </form>
    </div>
  );
}
