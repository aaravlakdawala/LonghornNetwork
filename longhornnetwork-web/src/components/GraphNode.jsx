import React, { useState, useMemo } from 'react';

function GraphNode({ node, onNodeClick, isSelected }) {
  var [isHovered, setIsHovered] = useState(false);
  var size = isHovered || isSelected ? 20 : 15;
  
  return (
    <g
      transform={`translate(${node.x}, ${node.y})`}
      onMouseEnter={function() { setIsHovered(true); }}
      onMouseLeave={function() { setIsHovered(false); }}
      onClick={function() { onNodeClick(node.id); }} 
      className="cursor-pointer"
    >
      {/* Node Circle */}
      <circle
        r={size}
        className={`shadow-lg transition-all duration-300 ${
          isHovered || isSelected ? 'scale-110 ring-4 ring-yellow-400' : 'ring-white-300/50'
        }`}
        style={{ fill: isSelected ? '#FFEB3B' : '#fff' }} 
      />
      
      {/* Node Label Text - Uses font-sans */}
      <text
        y={-size - 8}
        textAnchor="middle"
        style={{ fill: '#FFEB3B', opacity: isHovered || isSelected ? 1 : 0.75, fontSize: '12px', fontWeight: 500, fontFamily: 'sans-serif' }}
      >
        {node.label}
      </text>
    </g>
  );
}

export default GraphNode;