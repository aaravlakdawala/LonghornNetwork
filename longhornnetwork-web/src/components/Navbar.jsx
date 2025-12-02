import { useState, useEffect } from "react";

const navItems = [
    { name: "Home", id: "home" },
    { name: "Graph", id: "graph" },
    { name: "Testcases", id: "testcases" },
    // Executor tab temporarily hidden per request; keep entry commented out
    // { name: "Executor", id: "executor" },
    { name: "Search", id: "search" },
    { name: "Sign In", id: "signin" },
    { name: "Sign Up", id: "addstudent" }
];

export const Navbar = ({ activeTab, onTabChange }) => {
    const [isScrolled, setIsScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 10);
        };
        window.addEventListener("scroll", handleScroll);
        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    return (
        <nav
            style={{
                position: 'fixed',
                top: 0,
                width: '100%',
                zIndex: 10000,
                transition: 'all 0.3s duration',
                padding: isScrolled ? '0.75rem 0' : '1.25rem 0',
                background: isScrolled ? 'rgba(0, 0, 0, 0.8)' : 'transparent',
                backdropFilter: isScrolled ? 'blur(12px)' : 'none',
                boxShadow: isScrolled ? '0 1px 3px rgba(0, 0, 0, 0.3)' : 'none'
            }}
        >
            <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 1rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <a href="#" style={{ fontSize: '1.25rem', textDecoration: 'none', color: '#fff' }}>
                    <span style={{ textShadow: '0 0 20px rgba(6, 182, 212, 0.5)' }}>
                        Longhorn Network
                    </span>
                    <span style={{ marginLeft: '0.5rem', fontSize: '0.875rem', color: '#999' }}>
                        By Aarav Lakdawala
                    </span>
                </a>

                <div style={{ display: 'flex', gap: '2rem' }}>
                    {navItems.map((item) => (
                        <button
                            key={item.id}
                            onClick={() => onTabChange(item.id)}
                            style={{
                                background: 'none',
                                border: 'none',
                                color: activeTab === item.id ? '#06b6d4' : '#999',
                                cursor: 'pointer',
                                fontSize: '1rem',
                                padding: '0.5rem 0',
                                borderBottom: activeTab === item.id ? '2px solid #06b6d4' : '2px solid transparent',
                                transition: 'all 0.3s duration'
                            }}
                            onMouseEnter={(e) => e.target.style.color = '#06b6d4'}
                            onMouseLeave={(e) => e.target.style.color = activeTab === item.id ? '#06b6d4' : '#999'}
                        >
                            {item.name}
                        </button>
                    ))}
                </div>
            </div>
        </nav>
    );
};