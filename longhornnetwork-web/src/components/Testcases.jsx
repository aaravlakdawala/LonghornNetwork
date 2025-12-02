import React, { useEffect, useState } from 'react';
import ExecutorGraph from './ExecutorGraph.jsx';

export default function Testcases() {
  const SERVER_URL = 'http://localhost:8080';
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [testcases, setTestcases] = useState([]);

  useEffect(() => {
    const fetchTestcases = async () => {
      try {
        const res = await fetch(`${SERVER_URL}/api/testcases`);
        if (!res.ok) throw new Error('Server returned ' + res.status);
        const body = await res.json();
        if (body && body.testcases) {
          setTestcases(body.testcases);
        } else {
          setError('No testcases returned');
        }
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };
    fetchTestcases();
  }, []);

  if (loading) return <div style={{ marginTop: '80px', padding: '20px' }}>Loading test cases...</div>;
  if (error) return <div style={{ marginTop: '80px', padding: '20px', color: 'red' }}>Error: {error}</div>;

  return (
    <div style={{ marginTop: '80px', padding: '20px' }}>
      <h2>📚 Built-in Test Cases</h2>
      {testcases.map((tc, idx) => (
        <div key={idx} style={{ marginBottom: '30px' }}>
          <h3 style={{ marginTop: 0 }}>Test Case {tc.id || idx + 1}</h3>

          <div style={{ background: '#0f172a', padding: '12px', borderRadius: '8px' }}>
            {/* Render graph using existing ExecutorGraph component by passing executorData-like object */}
            {tc.graph ? (
              <ExecutorGraph executorData={{ graph: tc.graph }} />
            ) : (
              <p style={{ color: '#fff' }}>No graph data available for this test case.</p>
            )}
          </div>

          <div style={{ marginTop: '12px', padding: '12px', background: '#111827', borderRadius: '8px', color: '#fff' }}>
            <h4 style={{ marginTop: 0 }}>Messages (edge chat history)</h4>
            {tc.messages && tc.messages.length > 0 ? (
              tc.messages.map((edge, eidx) => (
                <div key={eidx} style={{ marginBottom: '8px', borderBottom: '1px solid rgba(255,255,255,0.04)', paddingBottom: '8px' }}>
                  <strong style={{ display: 'block' }}>{edge.source} ↔ {edge.target}</strong>
                  {(edge.messages || []).map((m, midx) => (
                    <div key={midx} style={{ fontSize: '13px', color: '#d1d5db' }}>
                      <span style={{ color: '#9ca3af' }}>[{new Date(m.timestamp).toLocaleTimeString()}]</span> <strong>{m.from}</strong>: {m.text}
                    </div>
                  ))}
                </div>
              ))
            ) : (
              <p style={{ color: '#9ca3af' }}>No messages available for this test case.</p>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}
