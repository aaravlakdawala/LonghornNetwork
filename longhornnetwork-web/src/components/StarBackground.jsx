import { useState, useEffect } from "react";

export const StarBackground = () => {
    const [stars, setStars] = useState([])
    const [metors, setMetors] = useState([])

    useEffect(() => {
        generateStars();
        
        // Generate new meteors every random interval
        const createMeteor = () => {
            const newMeror = {
                id: Math.random(),
                size: Math.random() * 2 + 1,
                x: Math.random() * 100,
                y: Math.random() * 30,
                duration: Math.random() * 3 + 2, // Random duration 2-5s
                delay: Math.random() * 2, // Random start delay 0-2s
            };
            setMetors((prev) => [...prev, newMeror]);
            
            // Remove meteor after its animation completes
            const totalTime = (newMeror.duration + newMeror.delay) * 1000;
            setTimeout(() => {
                setMetors((prev) => prev.filter((m) => m.id !== newMeror.id));
            }, totalTime);
            
        };
        
        // Create meteors at random intervals
        const createNextMeteor = () => {
            createMeteor();
            const nextInterval = Math.random() * 2000 + 500; // Next meteor in 0.5-2.5s
            setTimeout(createNextMeteor, nextInterval);
        };
        
        createNextMeteor();
        
        window.addEventListener('resize', generateStars);
        return () => {
            window.removeEventListener('resize', generateStars);
        };
    }, []);

    const generateStars = () => {
        const numberOfStars = (Math.floor(window.innerWidth * window.innerHeight * 0.001) /8);
        const newStars = [];
        for(let i = 0; i < numberOfStars; i++) {
            newStars.push({
                id: i,
                size: Math.random() * 3 + 1,
                x: Math.random() * 100,
                y: Math.random() * 100,
                radius: Math.random() * 1.5 + 0.5,
                opacity: Math.random() * 0.5 + 0.5,
                animationDuration: Math.random() * 4 + 2,
            });
        }
        setStars(newStars);
    };

    const generateMetors = () => {
        const numberOfMetors = 1;
        const newMetors = [];
        for(let i = 0; i < numberOfMetors; i++) {
            newMetors.push({
                id: i,
                size: Math.random() * 2 + 1,
                x: Math.random() * 100,
                y: Math.random() * 20,
                delay : Math.random() * 15,
                animationDuration: Math.random() * 3 + 3,
            });
        }
        setMetors(newMetors);
    };

    return (
        <div style={{position: 'fixed', inset: 0, overflow: 'hidden', pointerEvents: 'none', zIndex: 0}}>
            {
                stars.map((star) => (
                    <div key={star.id} className="star animate-pulse-subtle" style={{
                        width: star.size + "px",
                        height: star.size + "px",
                        top: star.y + "%",
                        left: star.x + "%",
                        opacity: star.opacity,
                        animationDuration: star.animationDuration + "s",
                        backgroundColor: '#fff',
                        borderRadius: '50%',
                        position: 'absolute',
                        willChange: 'transform, opacity',
                    }}> </div>
                ))
            }

            {
                metors.map((metor) => (
                    <div key={metor.id} className="meteor" style={{
                        width: metor.size * 50 + "px",
                        height: metor.size + "px",
                        top: metor.y + "%",
                        left: metor.x + "%",
                        transform: 'rotate(-45deg) translateY(0)',
                        opacity: 0,
                        animation: `meteor ${metor.duration}s ease-in ${metor.delay}s forwards`,
                        willChange: 'transform, opacity',
                    }}> </div>
                ))
            }
        </div>
    );
};