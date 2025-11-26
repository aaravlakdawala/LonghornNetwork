// Executor.jsx - React component that communicates with Java backend
import React, { useState, useEffect } from 'react';

function Executor({ onDataReceived }) {
  // State to hold the data received from the Java server
  const [graphData, setGraphData] = useState(null);
  const [connectionStatus, setConnectionStatus] = useState('Connecting...');
  const [logs, setLogs] = useState([]);

  // Java backend server URL
  const SERVER_URL = 'http://localhost:8080';

  // Add log message
  const addLog = (message) => {
    console.log(message);
    setLogs(prev => [...prev, `[${new Date().toLocaleTimeString()}] ${message}`]);
  };

  // Helper function to update data and notify parent
  const updateGraphData = (data) => {
    setGraphData(data);
    if (onDataReceived) {
      onDataReceived(data);
    }
  };

  useEffect(() => {
    // Define the fetch function
    const requestGraphData = async () => {
      try {
        addLog('📥 Requesting graph data from server...');
        const res = await fetch(`${SERVER_URL}/api/graph`);
        if (res.ok) {
          const data = await res.json();
          addLog('📤 Received graph data from server');
          updateGraphData(data);
          setConnectionStatus('Data Received');
        }
      } catch (e) {
        addLog('❌ Failed to fetch graph data: ' + e.message);
        setConnectionStatus('Error');
      }
    };

    // Check if server is healthy and fetch data
    const checkServer = async () => {
      try {
        addLog('🔍 Checking server health...');
        const healthRes = await fetch(`${SERVER_URL}/health`);
        if (healthRes.ok) {
          addLog('✅ Server is healthy!');
          setConnectionStatus('Connected');
          
          // Automatically request initial graph data
          await requestGraphData();
        }
      } catch (e) {
        addLog('❌ Cannot connect to server at ' + SERVER_URL);
        setConnectionStatus('Error - Server unreachable');
      }
    };

    // Check server on component mount
    checkServer();
  }, [onDataReceived]);

  const handleRefresh = async () => {
    try {
      addLog('🔄 Sending refresh command to server...');
      const response = await fetch(`${SERVER_URL}/api/command`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ command: 'refresh' })
      });
      const data = await response.json();
      addLog('📤 Received response: ' + data.message);
      updateGraphData(data);
    } catch (e) {
      addLog('❌ Error: ' + e.message);
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'monospace' }}>
      <h1>🌐 Executor - Java Backend Communication</h1>
      
      <div style={{ marginBottom: '20px', padding: '10px', backgroundColor: '#f0f0f0', borderRadius: '5px' }}>
        <p><strong>Status:</strong> <span style={{ color: connectionStatus === 'Connected' ? 'green' : connectionStatus === 'Data Received' ? 'blue' : 'red' }}>
          {connectionStatus}
        </span></p>
        <p><strong>Server:</strong> {SERVER_URL}</p>
      </div>

      <div style={{ marginBottom: '20px' }}>
        <button onClick={handleRefresh} style={{ marginRight: '10px', padding: '10px', cursor: 'pointer' }}>
          🔄 Refresh Data
        </button>
      </div>

      <div style={{ marginBottom: '20px', padding: '10px', backgroundColor: '#fff3cd', borderRadius: '5px', maxHeight: '300px', overflowY: 'auto', border: '1px solid #ffc107' }}>
        <h3>📋 Communication Log:</h3>
        {logs.length === 0 ? (
          <p style={{ color: '#999' }}>Waiting for events...</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {logs.map((log, idx) => (
              <li key={idx} style={{ marginBottom: '5px', fontSize: '12px' }}>
                {log}
              </li>
            ))}
          </ul>
        )}
      </div>

      <div style={{ marginBottom: '20px', padding: '10px', backgroundColor: '#e8f5e9', borderRadius: '5px' }}>
        <h3>📊 Received Data:</h3>
        {graphData ? (
          <pre style={{ backgroundColor: '#fff', padding: '10px', borderRadius: '3px', overflow: 'auto', maxHeight: '300px' }}>
            {JSON.stringify(graphData, null, 2)}
          </pre>
        ) : (
          <p style={{ color: '#999' }}>Awaiting graph data...</p>
        )}
      </div>

      <div style={{ padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '5px', fontSize: '12px' }}>
        <h4>📡 How Communication Works:</h4>
        <ol>
          <li>React frontend (this page) runs on http://localhost:5173</li>
          <li>Java backend REST server runs on http://localhost:8080</li>
          <li>Graph data is automatically fetched when component loads</li>
          <li>Click "Refresh Data" button to update graph from server</li>
          <li>Server responses are logged in the Communication Log</li>
          <li>Watch the Java console to see server-side messages (📥 📤 ❌ ✅)</li>
        </ol>
      </div>
    </div>
  );
}

export default Executor;