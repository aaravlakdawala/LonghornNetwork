import React, { useState, useMemo } from 'react';
import GraphNode from './GraphNode.jsx';
import GraphLink from './GraphLink.jsx';

// ==========================================================
// Graph Layout Algorithm - Position nodes from executor data
// Random placement with guaranteed 50px minimum distance enforcement
// ==========================================================
function generateNodePositions(nodeCount) {
  const width = typeof window !== 'undefined' ? window.innerWidth * 0.8 : 1200;
  const height = typeof window !== 'undefined' ? window.innerHeight * 0.7 : 800;

  const centerX = width / 2;
  const centerY = height / 2;
  const maxRadius = Math.min(width, height) / 3;

  const nodes = [];
  const minDistance = 50; // Minimum distance between node centers
  const maxAttemptsPerNode = 5000; // More aggressive attempts

  for (let nodeIdx = 0; nodeIdx < nodeCount; nodeIdx++) {
    let placed = false;
    let attempts = 0;

    while (!placed && attempts < maxAttemptsPerNode) {
      attempts++;

      const angle = Math.random() * Math.PI * 2;
      const distance = Math.random() * maxRadius;

      let x = centerX + distance * Math.cos(angle);
      let y = centerY + distance * Math.sin(angle);

      const padding = 50;
      x = Math.max(padding, Math.min(width - padding, x));
      y = Math.max(padding, Math.min(height - padding, y));

      // Check collision against ALL existing nodes
      let hasCollision = false;
      for (let i = 0; i < nodes.length; i++) {
        const dx = nodes[i].x - x;
        const dy = nodes[i].y - y;
        const dist = Math.sqrt(dx * dx + dy * dy);

        // Strict 50px minimum distance enforcement
        if (dist < minDistance) {
          hasCollision = true;
          break;
        }
      }

      // If no collision, place the node
      if (!hasCollision) {
        nodes.push({ x, y });
        placed = true;
      }
    }

    // If random placement fails, use grid-based fallback with strict spacing
    if (!placed) {
      const gridSize = Math.ceil(Math.sqrt(nodeCount));
      const cellWidth = Math.max(100, (width - 100) / gridSize);
      const cellHeight = Math.max(100, (height - 100) / gridSize);

      const row = Math.floor(nodeIdx / gridSize);
      const col = nodeIdx % gridSize;

      let x = 50 + col * cellWidth + Math.random() * Math.min(20, cellWidth * 0.1);
      let y = 50 + row * cellHeight + Math.random() * Math.min(20, cellHeight * 0.1);

      x = Math.max(50, Math.min(width - 50, x));
      y = Math.max(50, Math.min(height - 50, y));

      nodes.push({ x, y });
    }
  }

  return nodes;
}

// ==========================================================
// ExecutorGraph Component
// ==========================================================
function ExecutorGraph({ executorData }) {
  const [selectedNodeId, setSelectedNodeId] = useState(null);
  const WIDTH = typeof window !== 'undefined' ? window.innerWidth * 0.8 : 1200;
  const HEIGHT = typeof window !== 'undefined' ? window.innerHeight * 0.7 : 800;

  // Convert executor data to graph format with positions
  const graphData = useMemo(() => {
    if (!executorData || !executorData.graph) {
      return { nodes: [], links: [] };
    }

    const executorNodes = executorData.graph.nodes || [];
    const executorLinks = executorData.graph.links || [];

    // Generate positions for nodes
    const positions = generateNodePositions(executorNodes.length);

    // Map executor nodes to graph nodes with positions and metadata
    const nodes = executorNodes.map((node, idx) => {
      const pos = positions[idx] || { x: WIDTH / 2, y: HEIGHT / 2 };
      return {
        id: node.id || node.name || `node-${idx}`,
        label: node.name || node.id || `Node ${idx}`,
        x: pos.x,
        y: pos.y,
        ...node, // Include all original executor node data
      };
    });

    // Map executor links to graph links
    const links = executorLinks.map(link => ({
      source: link.source,
      target: link.target,
      weight: link.weight || 1,
    }));

    return { nodes, links };
  }, [executorData, WIDTH, HEIGHT]);

  // Create a map for quick node lookup
  const nodeMap = useMemo(() => {
    return graphData.nodes.reduce((acc, node) => {
      acc[node.id] = node;
      return acc;
    }, {});
  }, [graphData.nodes]);

  function handleNodeClick(nodeId) {
    setSelectedNodeId(nodeId);
  }

  // Helper function to check if an edge is highlighted
  // No highlighted edges UI now — function removed.


  // Get list of all student names (unused without referral UI)
  const studentNames = useMemo(() => {
    return graphData.nodes.map(node => node.name || node.label);
  }, [graphData.nodes]);

  if (!executorData || !executorData.graph || graphData.nodes.length === 0) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '400px',
        color: '#999',
        textAlign: 'center'
      }}>
        <p>Waiting for executor data... Click "Request Graph" in the Executor tab to load student graph.</p>
      </div>
    );
  }

  const selectedNode = selectedNodeId ? nodeMap[selectedNodeId] : null;

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      gap: '20px',
      padding: '20px',
      marginTop: '80px'
    }}>
      {/* Graph and Details */}
      <div style={{
        display: 'flex',
        gap: '20px',
      }}>
        {/* Graph SVG */}
        <div style={{ flex: 1 }}>
          <h3 style={{ marginTop: 0, color: '#333' }}>📊 Student Graph Visualization</h3>
          <div className="bg-gray-800 rounded-xl shadow-2xl p-4" style={{ width: '100%' }}>
            <svg
              viewBox={`0 0 ${WIDTH} ${HEIGHT}`}
              xmlns="http://www.w3.org/2000/svg"
              className="w-full h-auto border border-gray-700 rounded-lg bg-gray-700"
              style={{ minHeight: '600px' }}
            >
              {/* Draw links */}
              <g className="links">
                {graphData.links.map((link, index) => {
                  const sourceNode = nodeMap[link.source];
                  const targetNode = nodeMap[link.target];
                  if (sourceNode && targetNode) {
                    return (
                      <GraphLink
                        key={index}
                        sourceNode={sourceNode}
                        targetNode={targetNode}
                        weight={link.weight}
                        isHighlighted={false}
                        allNodes={graphData.nodes}
                      />
                    );
                  }
                  return null;
                })}
              </g>

              {/* Draw nodes */}
              <g className="nodes">
                {graphData.nodes.map((node) => (
                  <GraphNode
                    key={node.id}
                    node={node}
                    onNodeClick={handleNodeClick}
                    isSelected={node.id === selectedNodeId}
                  />
                ))}
              </g>
            </svg>
          </div>
          <p style={{ fontSize: '12px', color: '#666', marginTop: '10px' }}>
            {graphData.nodes.length} students • {graphData.links.length} connections
          </p>
        </div>

        {/* Details Panel */}
        <div style={{
          width: '320px',
          backgroundColor: '#f5f5f5',
          borderRadius: '5px',
          padding: '15px',
          maxHeight: '700px',
          overflowY: 'auto',
          border: '1px solid #ddd',
          fontSize: '13px',
          color: '#000'
        }}>
          <h3 style={{ marginTop: 0, marginBottom: '15px', borderBottom: '2px solid #06b6d4', paddingBottom: '10px', color: '#000' }}>
            👤 Student Profile
          </h3>
          {selectedNode ? (
            <div style={{ color: '#000' }}>
              {/* Name Header */}
              <div style={{
                backgroundColor: '#06b6d4',
                color: '#fff',
                padding: '10px',
                borderRadius: '5px',
                marginBottom: '12px',
                fontWeight: 'bold',
                textAlign: 'center'
              }}>
                {selectedNode.name || selectedNode.label}
              </div>

              {/* Basic Information */}
              <div style={{ backgroundColor: '#fff', padding: '10px', borderRadius: '3px', marginBottom: '10px', border: '1px solid #eee', color: '#000' }}>
                <h4 style={{ margin: '0 0 8px 0', color: '#000', fontSize: '12px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>📋 Basic Info</h4>
                <p style={{ margin: '4px 0', color: '#000' }}><strong>Age:</strong> {selectedNode.age}</p>
                <p style={{ margin: '4px 0', color: '#000' }}><strong>Gender:</strong> {selectedNode.gender}</p>
                <p style={{ margin: '4px 0', color: '#000' }}><strong>Year:</strong> {selectedNode.year}</p>
                <p style={{ margin: '4px 0', color: '#000' }}><strong>Major:</strong> {selectedNode.major}</p>
                <p style={{ margin: '4px 0', color: '#000' }}><strong>GPA:</strong> {selectedNode.gpa?.toFixed(2)}</p>
              </div>

              {/* Roommate Information */}
              <div style={{ backgroundColor: '#fff', padding: '10px', borderRadius: '3px', marginBottom: '10px', border: '1px solid #eee', color: '#000' }}>
                <h4 style={{ margin: '0 0 8px 0', color: '#000', fontSize: '12px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>🛏️ Roommate</h4>
                <p style={{ margin: '4px 0', color: '#000' }}>
                  <strong>Assigned:</strong> {selectedNode.roommate ? selectedNode.roommate : <span style={{ color: '#000', fontStyle: 'italic' }}>Not assigned</span>}
                </p>
              </div>

              {/* Roommate Preferences */}
              {selectedNode.roommatePreferences && selectedNode.roommatePreferences.length > 0 && (
                <div style={{ backgroundColor: '#fff', padding: '10px', borderRadius: '3px', marginBottom: '10px', border: '1px solid #eee', color: '#000' }}>
                  <h4 style={{ margin: '0 0 8px 0', color: '#000', fontSize: '12px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>❤️ Roommate Preferences</h4>
                  <ul style={{ margin: '0', paddingLeft: '18px', listStyle: 'disc', color: '#000' }}>
                    {selectedNode.roommatePreferences.map((pref, idx) => (
                      <li key={idx} style={{ fontSize: '12px', margin: '3px 0', color: '#000' }}>
                        {pref}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Previous Internships */}
              {selectedNode.previousInternships && selectedNode.previousInternships.length > 0 && (
                <div style={{ backgroundColor: '#fff', padding: '10px', borderRadius: '3px', marginBottom: '10px', border: '1px solid #eee', color: '#000' }}>
                  <h4 style={{ margin: '0 0 8px 0', color: '#000', fontSize: '12px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>💼 Previous Internships</h4>
                  <ul style={{ margin: '0', paddingLeft: '18px', listStyle: 'disc', color: '#000' }}>
                    {selectedNode.previousInternships.map((intern, idx) => (
                      <li key={idx} style={{ fontSize: '12px', margin: '3px 0', color: '#000' }}>
                        {intern}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* All Fields Reference */}
              <div style={{
                backgroundColor: '#f0f0f0',
                padding: '8px',
                borderRadius: '3px',
                marginTop: '10px',
                fontSize: '11px',
                color: '#000',
                borderLeft: '3px solid #06b6d4'
              }}>
                <p style={{ margin: '0', color: '#000' }}><strong>Student ID:</strong> {selectedNode.id}</p>
              </div>
            </div>
          ) : (
            <div style={{
              textAlign: 'center',
              padding: '20px 10px',
              color: '#000'
            }}>
              <p style={{ fontSize: '12px', margin: '0', color: '#000' }}>👆 Click on a node to see all student information</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ExecutorGraph;
