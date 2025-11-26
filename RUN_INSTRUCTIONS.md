# LonghornNetwork: Running Both Executors

## Prerequisites

- Java 21 JDK installed
- Maven 3.9.11 installed globally
- Node.js and npm installed

## Quick Start

### Terminal 1: Run the Java Backend Server

```bash
cd c:\Users\aarav\LonghornNetwork

# Compile the Java code
mvn clean compile

# Run the main Java application (or create a server entry point)
java -cp target/classes com.example.Main
```

### Terminal 2: Start the React Frontend Development Server

```bash
cd c:\Users\aarav\LonghornNetwork\longhornnetwork-web

# Install dependencies (first time only)
npm install

# Start the development server
npm run dev
```

### Terminal 3 (Optional): Watch for File Changes

```bash
cd c:\Users\aarav\LonghornNetwork

# Continuously compile Java on file changes
mvn compile -DwatchMode
```

## Verification Steps

1. **Check Java Server is Running:**

   - Open Terminal 1 and look for output showing the server started on port 8080
   - You should see: `Server running on ws://localhost:8080/ws/graph`

2. **Check React Frontend is Running:**

   - Open Terminal 2 and look for Vite output
   - You should see: `Local: http://localhost:5173`

3. **Test Connection:**
   - Open http://localhost:5173 in your browser
   - Go to the Executor page
   - Status should change from "Connecting..." to "Connected"
   - You should see graph data displayed or "Awaiting graph data..." message

## Important Notes

- The Java WebSocket endpoint is at: `ws://localhost:8080/ws/graph`
- The React app is at: `http://localhost:5173`
- Make sure both ports (8080 and 5173) are not blocked by a firewall
- If you change Java code, recompile with `mvn compile` and restart the server

## Troubleshooting

- **Port 8080 in use:** Change the port in `GraphSocketEndpoint.java`
- **WebSocket connection fails:** Check that the Java server is running and listening
- **React can't reach Java:** Check your firewall and ensure both are on `localhost`
- **npm not found:** Install Node.js from https://nodejs.org/
