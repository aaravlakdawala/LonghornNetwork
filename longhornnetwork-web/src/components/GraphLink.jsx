
import React, { useState, useMemo } from 'react';

// ==========================================================
// 2. GraphLink Component (Analogous to components/GraphLink.jsx)
// Defined using a standard function declaration.
// ==========================================================
function GraphLink({ sourceNode, targetNode, weight, isHighlighted = false, allNodes = [] }) {
  const strokeWidth = isHighlighted ? 4 : Math.max(1, 6 - (weight || 1));
  const strokeColor = isHighlighted ? '#FF0000' : (weight > 0.7 ? '#FFFFFF' : '#CCCCCC');

  // Always render a single straight line between node centers (no curves or corners)
  return (
    <line
      x1={sourceNode.x}
      y1={sourceNode.y}
      x2={targetNode.x}
      y2={targetNode.y}
      stroke={strokeColor}
      strokeWidth={strokeWidth}
      strokeLinecap="round"
      style={{ filter: isHighlighted ? 'drop-shadow(0 0 3px #FF0000)' : 'none' }}
    />
  );
}
export default GraphLink;