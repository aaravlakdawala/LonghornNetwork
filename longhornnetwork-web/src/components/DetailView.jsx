import React, { useState, useMemo } from 'react';

function DetailView({ node, onBack }) {
    if (!node) return null;

    return (
        <div className="p-8 bg-gray-800 rounded-xl shadow-2xl w-full max-w-2xl border border-gray-700">
            <h1 className="text-3xl font-bold mb-4 text-orange-400 border-b border-gray-700 pb-2">
               {'\n'}Details for: {node.label} ({node.id})
            </h1>
            
            <p className="text-gray-300 mb-6 text-lg italic">
                {node.description}
            </p>

            <div className="space-y-4 text-gray-200">
                <h2 className="text-xl font-semibold border-b border-gray-700 pb-1">
                    Extended Information
                </h2>
                <p className="bg-gray-700 p-3 rounded">
                    <span className="font-medium text-cyan-400">Function:</span> {node.detail}
                </p>
                <p className="bg-gray-700 p-3 rounded">
                    <span className="font-medium text-cyan-400">Coordinates (x, y):</span> ({node.x}, {node.y})
                </p>
            </div>

            <button
                onClick={onBack}
                className="mt-8 px-6 py-2 bg-gray-600 hover:bg-gray-500 text-white font-semibold rounded-lg shadow-md transition duration-200"
            >
                &larr; Go Back to Graph
            </button>
        </div>
    );
}
export default DetailView;