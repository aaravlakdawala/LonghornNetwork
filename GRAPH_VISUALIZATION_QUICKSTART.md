# Quick Start - Graph Visualization

## One-Time Setup

### 1. Install Dependencies

**Java Backend** (if not already done):

- Temurin JDK 21 is installed
- Maven 3.9.11 is installed globally

**React Frontend**:

```bash
cd c:\Users\aarav\LonghornNetwork\longhornnetwork-web
npm install
```

## Running the Application

### Terminal 1: Start Java Backend

```bash
cd c:\Users\aarav\LonghornNetwork
mvn exec:java -Dexec.mainClass=Main
```

You should see output like:

```
[main] INFO org.eclipse.jetty.server.Server - Started Server
[main] INFO org.eclipse.jetty.server.NetworkTrafficSelectChannelConnector - Started SocketConnector@0.0.0.0:8080
```

**Server is now running on: `http://localhost:8080`**

### Terminal 2: Start React Frontend

```bash
cd c:\Users\aarav\LonghornNetwork\longhornnetwork-web
npm run dev
```

You should see:

```
VITE v7.2.4 ready in 123 ms

➜  Local:   http://localhost:5173/
```

**Frontend is now running on: `http://localhost:5173`**

## Using the Application

### Step-by-Step Navigation

1. **Open Browser**: Go to `http://localhost:5173`

2. **Click "Executor" Tab**

   - You should see "✅ Server is healthy!" status
   - Scroll down and click **"Request Graph"** button
   - Watch the Communication Log for messages:
     ```
     [time] 🔍 Checking server health...
     [time] ✅ Server is healthy!
     [time] 📥 Requesting graph data from server...
     [time] 📤 Received graph data from server
     ```

3. **Click "Visualization" Tab**

   - Now you see the interactive student graph!
   - Each circle represents a student
   - Lines connect students with relationships
   - Shows count: "X students • Y connections"

4. **Interact with the Graph**

   - **Click on any student node** to see their details:
     - Name, Age, Gender, Year, Major, GPA
     - Assigned Roommate
     - Roommate Preferences
     - Previous Internships

5. **Return to Executor Tab**
   - Click **"Refresh"** to reload data anytime
   - Click **"Request Graph"** to fetch fresh data from backend

## What's Happening Behind the Scenes

### Data Flow:

1. **Java Backend** (`Main.java`)

   - Reads student data from `FINAL_STUDENTS.txt`
   - Assigns roommates using Gale-Shapley algorithm
   - Creates student graph with connections
   - Sends JSON response with all student details

2. **React Executor Component** (`Executor.jsx`)

   - Fetches data from `http://localhost:8080/api/graph`
   - Displays communication status and logs
   - Passes data to parent component (`App.jsx`)

3. **React App Component** (`App.jsx`)

   - Manages state of executor data
   - Routes between tabs (Home, Graph, Visualization, Executor)

4. **ExecutorGraph Component** (`ExecutorGraph.jsx`)
   - Takes executor data
   - Generates x,y positions for nodes
   - Renders graph using `GraphNode` and `GraphLink` components
   - Shows details panel when node is clicked

## Troubleshooting

### "Cannot connect to server" in Executor

- ❌ Java backend not running
- ✅ Start Java backend in Terminal 1: `mvn exec:java -Dexec.mainClass=Main`

### "Waiting for executor data..." in Visualization tab

- ❌ Haven't fetched data yet
- ✅ Go to Executor tab and click "Request Graph"

### Graph shows but no nodes visible

- ❌ Data fetch failed
- ✅ Check Java backend console for errors
- ✅ Verify `FINAL_STUDENTS.txt` exists in project root

### React app not starting

- ❌ Dependencies not installed
- ✅ Run `npm install` in `longhornnetwork-web` folder

## Stopping the Application

- **Backend**: Press `Ctrl+C` in Terminal 1
- **Frontend**: Press `Ctrl+C` in Terminal 2

## Next Steps

- Explore the "Graph" tab to see the static demo graph
- Try the "Home" tab for the hero section
- Use the Executor tab to test communication
- Check browser console (F12) for any errors

## File Locations

- **Backend Source**: `c:\Users\aarav\LonghornNetwork\src\Main.java`
- **Frontend Source**: `c:\Users\aarav\LonghornNetwork\longhornnetwork-web\src\`
- **Visualization Component**: `longhornnetwork-web\src\components\ExecutorGraph.jsx`
- **Student Data**: `c:\Users\aarav\LonghornNetwork\src\FINAL_STUDENTS.txt`

## Key Endpoints

- **Health Check**: `http://localhost:8080/health`
- **Get Graph Data**: `http://localhost:8080/api/graph`
- **Send Command**: `http://localhost:8080/api/command` (POST)
