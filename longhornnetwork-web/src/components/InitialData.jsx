import React, { useState, useMemo } from 'react';

// ==========================================================
// Graph Layout Algorithm - Responsive to window size
// ==========================================================
function generateRandomNodePositions(nodeCount, minDistance = 80) {
  // Get window/viewport dimensions
  var width = typeof window !== 'undefined' ? window.innerWidth : 600;
  var height = typeof window !== 'undefined' ? window.innerHeight : 400;
  
  // Calculate center of the graph
  var centerX = width / 2;
  var centerY = height / 2;
  
  // Maximum radius from center - scales with window size
  var maxRadius = Math.min(width, height) / 3;
  
  var nodes = [];
  var maxAttempts = 200;
  var attempt = 0;
  
  while (nodes.length < nodeCount && attempt < maxAttempts) {
    attempt++;
    
    // Generate random angle and distance from center
    var angle = Math.random() * Math.PI * 2;
    var distance = Math.random() * maxRadius;
    
    // Convert polar to Cartesian coordinates
    var x = centerX + distance * Math.cos(angle);
    var y = centerY + distance * Math.sin(angle);
    
    // Ensure nodes stay within bounds with padding
    var padding = 50;
    x = Math.max(padding, Math.min(width - padding, x));
    y = Math.max(padding + 80, Math.min(height - padding, y)); // Extra padding for navbar
    
    // Check if this position overlaps with existing nodes
    var overlaps = false;
    for (var i = 0; i < nodes.length; i++) {
      var dx = nodes[i].x - x;
      var dy = nodes[i].y - y;
      var dist = Math.sqrt(dx * dx + dy * dy);
      
      if (dist < minDistance) {
        overlaps = true;
        break;
      }
    }
    
    // Add node if no overlap
    if (!overlaps) {
      nodes.push({ x: x, y: y });
    }
  }
  
  return nodes;
}

// Node data with descriptions
var nodeDescriptions = [
  { id: 'A', label: 'Start Node', description: 'This is the initiation point of the entire process flow. All operations begin here.', detail: 'Node A is the initial data source and typically runs a validation script before passing control.' },
  { id: 'B', label: 'Process B', description: 'Process B handles data filtering and transformation before routing it further.', detail: 'Process B uses an advanced neural network model to clean and standardize the input data.' },
  { id: 'C', label: 'Gateway C', description: 'Gateway C is a decision node. It routes the flow based on a weight threshold.', detail: 'Gateway C checks if the link weight is above 0.8; otherwise, it takes the fallback path.' },
  { id: 'D', label: 'End System D', description: 'End System D is the final destination, responsible for archiving the results.', detail: 'System D guarantees persistence by writing the final record to a distributed, immutable ledger.' },
];

// Generate random positions based on window size
var randomPositions = generateRandomNodePositions(nodeDescriptions.length, 80);

// Combine positions with descriptions
var nodesWithPositions = nodeDescriptions.map(function(desc, index) {
  var pos = randomPositions[index] || { x: 300, y: 200 };
  return {
    id: desc.id,
    x: pos.x,
    y: pos.y,
    label: desc.label,
    description: desc.description,
    detail: desc.detail
  };
});

// ==========================================================
// 1. DATA DEFINITION
// ==========================================================
var initialGraphData = {
  nodes: nodesWithPositions,
  links: [
    { source: 'A', target: 'B', weight: 0.5 },
    { source: 'A', target: 'C', weight: 0.9 },
    { source: 'B', target: 'D', weight: 0.7 },
    { source: 'C', target: 'D', weight: 0.3 },
  ],
};

// Define constants for view states
var VIEWS = {
    GRAPH_VIEW: 'graph',
    DETAIL_VIEW: 'detail',
};

export { initialGraphData, VIEWS };