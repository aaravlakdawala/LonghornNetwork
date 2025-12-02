

// ==========================================================
// Imports using your requested file structure (for a real app)
// ==========================================================
import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar.jsx';
import { StarBackground } from './components/StarBackground.jsx';
import { HeroSection } from './components/HeroSection.jsx';
import Executor from './Executor.jsx';
import ExecutorGraph from './components/ExecutorGraph.jsx';
import AddStudentForm from './components/AddStudentForm.jsx';
import Testcases from './components/Testcases.jsx';
import Search from './components/Search.jsx';
import Signin from './components/Signin.jsx';

// ==========================================================
// Main App Component (Logic)
// ==========================================================
export default function App() {
    const [activeTab, setActiveTab] = useState('home');
    const [executorData, setExecutorData] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);

    // Fetch graph data automatically on component mount
    useEffect(() => {
        const fetchGraphData = async () => {
            const SERVER_URL = 'http://localhost:8080';
            try {
                const res = await fetch(`${SERVER_URL}/api/graph`);
                if (res.ok) {
                    const data = await res.json();
                    setExecutorData(data);
                }
            } catch (e) {
                console.error('Failed to fetch graph data:', e);
            }
        };

        // Fetch immediately on mount
        fetchGraphData();

        // Optionally refresh data every 30 seconds
        const interval = setInterval(fetchGraphData, 30000);
        return () => clearInterval(interval);
    }, []);

    const handleStudentAdded = (graphData) => {
        setExecutorData(graphData);
        // Automatically switch to graph tab to show updated data
        setActiveTab('graph');
    };

    return (
        <div style={{ minHeight: '100vh', position: 'relative', zIndex: 1 }}>
            <StarBackground />
            <Navbar activeTab={activeTab} onTabChange={setActiveTab} />
            {activeTab === 'home' && <HeroSection />}
            {activeTab === 'graph' && <ExecutorGraph executorData={executorData} currentUser={currentUser} onUpdateGraph={setExecutorData} />}
            {activeTab === 'search' && <Search executorData={executorData} />}
            {activeTab === 'testcases' && <Testcases />}
            {activeTab === 'signin' && (
                <div style={{ marginTop: '80px' }}>
                    <Signin onSignIn={(name) => { setCurrentUser(name); setActiveTab('graph'); }} />
                </div>
            )}
            {/* Executor tab temporarily hidden. Keep rendering code commented out for now.
            {activeTab === 'executor' && (
                <div style={{ marginTop: '80px' }}>
                    <Executor onDataReceived={setExecutorData} />
                </div>
            )}
            */}
            {activeTab === 'addstudent' && (
                <AddStudentForm onStudentAdded={handleStudentAdded} />
            )}
            {/* Other tabs remain empty, no text shown */}
        </div>
    );
}