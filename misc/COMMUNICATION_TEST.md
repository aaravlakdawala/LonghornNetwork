# Testing Backend-Frontend Communication

## How to Verify They're Talking to Each Other

### Step 1: Start the Java Backend Server

```bash
cd c:/Users/aarav/LonghornNetwork
mvn clean compile
java -cp target/classes WebSocketServer
```

**Watch for these messages in the Java console:**

```
🚀 Starting LonghornNetwork Server...
✅ Server started on http://localhost:8080
✅ REST API endpoints:
   - GET  http://localhost:8080/api/graph
   - POST http://localhost:8080/api/command
   - GET  http://localhost:8080/health
```

### Step 2: Start the React Frontend

```bash
cd c:/Users/aarav/LonghornNetwork/longhornnetwork-web
npm install  # First time only
npm run dev
```

**React should start at: `http://localhost:5173`**

### Step 3: Open the Executor Page

- Open http://localhost:5173 in your browser
- Navigate to the **Executor** component
- Open browser **Developer Console** (F12 → Console tab)

### Step 4: Watch Communication Happen

#### In the Browser (React Component):

You should see in the Communication Log:

```
[HH:MM:SS] 🔍 Checking server health...
[HH:MM:SS] ✅ Server is healthy!
[HH:MM:SS] 📥 Requesting graph data from server...
[HH:MM:SS] 📤 Received graph data from server
```

#### In the Java Console (Backend Server):

You should see:

```
💚 Health check from client
📥 GET /api/graph - Client connected and requesting graph data
📤 Sending: {"status":"success",...}
```

### Step 5: Test Manual Communication

Click the buttons in the React component:

- **🔄 Refresh** → Sends `POST /api/command` with refresh
- **📥 Request Graph** → Sends `POST /api/command` with requestInitialGraph

#### React Console Should Show:

```
[HH:MM:SS] 🔄 Sending refresh command to server...
[HH:MM:SS] 📤 Received response: Graph refreshed
```

#### Java Console Should Show:

```
📥 POST /api/command - Received: {"command":"refresh"}
📤 Sending: {"status":"refreshed",...}
```

---

## ✅ Success Indicators

- [ ] Java console shows "Server started on http://localhost:8080"
- [ ] React page loads at http://localhost:5173
- [ ] Communication Log appears in browser with ✅ messages
- [ ] Java console shows "Health check from client"
- [ ] Java console shows "📥 GET /api/graph" when page loads
- [ ] Clicking buttons in React shows new log entries in both consoles
- [ ] Received Data section shows JSON objects from the server

---

## 🔧 Troubleshooting

### Port 8080 already in use

```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with the number from above)
taskkill /PID <PID> /F
```

### CORS errors in browser console

This means React can't reach Java. Make sure:

- Java server is running on http://localhost:8080
- Firewall isn't blocking localhost connections
- No typos in SERVER_URL

### Connection refused

Java server isn't running. Make sure to run:

```bash
java -cp target/classes WebSocketServer
```

### npm: command not found

Install Node.js from https://nodejs.org/

---

## 📊 Data Flow Diagram

```
REACT (http://localhost:5173)
         ↓
    fetch() call
         ↓
HTTP REQUEST → Java Server (http://localhost:8080)
         ↓
    Spark routes
         ↓
  WebSocketServer
         ↓
HTTP RESPONSE ← return JSON
         ↓
  setGraphData()
         ↓
Update UI with received data
```

Every HTTP request from React logs on **both sides**!
