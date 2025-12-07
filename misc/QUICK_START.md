# Quick Start Guide

## Running the Full Stack (Java Backend + React Frontend)

### Option 1: Run in Separate Terminals (Recommended)

#### Terminal 1 - Java Backend:

```bash
cd c:/Users/aarav/LonghornNetwork
java -cp target/classes Main
```

This will run all the test cases and display the results.

#### Terminal 2 - React Frontend:

```bash
cd c:/Users/aarav/LonghornNetwork/longhornnetwork-web
npm install  # Only needed first time
npm run dev
```

This will start the React dev server on `http://localhost:5173`

### Open in Browser

Visit: `http://localhost:5173`

Navigate to the Executor component to see the React interface.

---

## Build Commands

```bash
# Compile Java code
cd c:/Users/aarav/LonghornNetwork
mvn clean compile

# Build JAR
mvn package

# Run tests
mvn test
```

---

## Project Structure

```
LonghornNetwork/
├── src/                      # Java source files
│   ├── Main.java            # Main executor (test runner)
│   ├── StudentGraph.java    # Graph data structure
│   ├── Student.java         # Student model
│   ├── GraphSocketEndpoint.java  # WebSocket endpoint
│   └── ...
├── target/                  # Compiled classes (created after mvn compile)
├── longhornnetwork-web/     # React frontend
│   ├── src/
│   │   ├── Executor.jsx     # React WebSocket client
│   │   ├── App.jsx
│   │   └── ...
│   └── package.json
└── pom.xml                  # Maven configuration
```

---

## Troubleshooting

### "ClassNotFoundException: Main"

- Solution: Run `mvn clean compile` first, then use `java -cp target/classes Main`

### npm not found

- Solution: Install Node.js from https://nodejs.org

### Port 5173 already in use

- Solution: Change the port in `vite.config.js` or kill the process using that port

### Java compilation errors

- Solution: Ensure you're using Java 17+ by running `java -version`
