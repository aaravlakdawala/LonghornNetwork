export const HeroSection = () => {
    return (
        <section
            id="hero"
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100%',
                height: '100vh',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                textAlign: 'center',
                zIndex: 1,
                padding: '2rem'
            }}
        >
            <style>{`
                @keyframes fadeInCyan {
                    from {
                        opacity: 0;
                        color: #06b6d4;
                    }
                    to {
                        opacity: 1;
                        color: #06b6d4;
                    }
                }
                .cyan-fade {
                    animation: fadeInCyan 2s ease-in forwards;
                }
            `}</style>
            <div style={{ maxWidth: '1000px', width: '100%' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                    <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: '#fff', margin: 0 }}>
                        <span style={{ display: 'block' }}>Hi I'm</span>
                        <span className="cyan-fade" style={{ display: 'block' }}>Aarav</span>
                        <span style={{ display: 'block' }}>Lakdawala</span>
                    </h1>
                </div>
            </div>



            </section>
    );};