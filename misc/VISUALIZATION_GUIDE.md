# Graph Visualization Integration - User Guide

## Overview

The graph visualization system now integrates the **Executor** (backend communication center) with the **Visualization** tab, which renders real student data from your Java backend using React components.

## Architecture

### Data Flow

```
Java Backend (Main.java)
    ↓ (REST /api/graph)
Executor.jsx (Fetches & communicates)
    ↓ (via callback onDataReceived)
App.jsx (State management)
    ↓ (passes executorData prop)
ExecutorGraph.jsx (Visualization)
    ↓ (uses GraphNode & GraphLink components)
Browser Rendering (SVG Graph Display)
```

## How to Use

### Step 1: Start the Java Backend Server

```bash
cd c:\Users\aarav\LonghornNetwork
mvn exec:java -Dexec.mainClass=Main
```

The server will start on `http://localhost:8080` and be ready to serve graph data.

### Step 2: Start the React Frontend

```bash
cd c:\Users\aarav\LonghornNetwork\longhornnetwork-web
npm run dev
```

The app will run on `http://localhost:5173`

### Step 3: Load the Data

1. **Navigate to the "Executor" tab** - This shows the communication center
2. **Click "Request Graph"** button - This fetches student data from the backend
3. **Watch the Communication Log** - You'll see status messages and when data arrives
4. **Navigate to the "Visualization" tab** - Now you'll see the interactive graph!

## Features

### ExecutorGraph Component (`src/components/ExecutorGraph.jsx`)

**Key Features:**

- **Dynamic Node Positioning**: Automatically positions nodes in a circular/radial layout to avoid overlaps
- **Interactive Nodes**: Click on any node to see student details
- **Link Visualization**: Shows connections between students with weighted lines
- **Student Details Panel**: Displays full student information when selected

**Node Layout Algorithm:**

- Positions nodes using a random radial distribution
- Implements collision detection to prevent node overlap
- Maintains minimum distance between nodes

### Available Student Information

When you click on a student node, you can see:

- **Name** - Student's full name
- **Age** - Student's age
- **Gender** - Student's gender
- **Year** - Academic year (Freshman, Sophomore, etc.)
- **Major** - Primary field of study
- **GPA** - Grade Point Average
- **Roommate** - Assigned roommate (if any)
- **Preferences** - Roommate preferences list
- **Internships** - Previous internship experience

## Component Breakdown

### `ExecutorGraph.jsx` (Main Visualization Component)

- Receives `executorData` prop from `App.jsx`
- Generates node positions automatically
- Renders SVG graph with `GraphNode` and `GraphLink` components
- Side panel for detailed student information
- Shows connection count and node count

### Modified Files

1. **`App.jsx`**

   - Added `executorData` state
   - Added "Visualization" tab routing
   - Passes `onDataReceived` callback to `Executor`
   - Imports `ExecutorGraph` component

2. **`Executor.jsx`**

   - Now accepts `onDataReceived` prop (callback function)
   - Calls callback when graph data is received from backend
   - Updates parent App state with latest data

3. **`Navbar.jsx`**
   - Added "Visualization" tab to navigation menu
   - Now appears between "Graph" and "Executor" tabs

## Technical Details

### Data Structure

The executor data follows this structure:

```javascript
{
  graph: {
    nodes: [
      {
        id: "student-1",
        name: "John Doe",
        age: 20,
        gender: "Male",
        year: "Junior",
        major: "Computer Science",
        gpa: 3.8,
        roommate: "Jane Smith",
        roommatePreferences: ["quiet", "organized"],
        previousInternships: ["Google", "Microsoft"]
      },
      // ... more nodes
    ],
    links: [
      {
        source: "student-1",
        target: "student-2",
        weight: 0.85
      },
      // ... more links
    ]
  }
}
```

### Node Positioning

The component calculates positions using:

- Circular/radial layout within defined bounds
- Random angle and distance from center
- Collision detection with 100px minimum distance
- Boundary padding to keep nodes visible

## Workflow Example

1. **Backend Running**: `mvn exec:java -Dexec.mainClass=Main`
2. **Frontend Running**: `npm run dev`
3. **Open Browser**: `http://localhost:5173`
4. **Click "Executor" tab**: See "✅ Server is healthy!"
5. **Click "Request Graph"**: Fetch student data
6. **Click "Visualization" tab**: See the interactive student graph
7. **Click any node**: View detailed student information in the right panel

## Building & Deployment

### Development Build

```bash
# React frontend
cd longhornnetwork-web
npm run dev

# Java backend
mvn exec:java -Dexec.mainClass=Main
```

### Production Build

```bash
# React frontend
cd longhornnetwork-web
npm run build

# Java backend
mvn clean package
java -jar target/longhornnetwork-server-0.1.0.jar
```

## Troubleshooting

### "Waiting for executor data..." message in Visualization tab

- Make sure you're in the "Executor" tab first
- Click the "Request Graph" button
- Check the Communication Log for errors
- Ensure Java backend is running on `http://localhost:8080`

### Graph not rendering

- Verify the backend returned data (check browser console)
- Check browser developer tools for any JavaScript errors
- Ensure nodes and links arrays are present in the response

### Nodes overlapping

- The positioning algorithm automatically avoids overlaps
- If you still see overlap, try refreshing the data (Request Graph again)

## Future Enhancements

Potential features to add:

- Drag nodes to reposition them manually
- Search/filter students by name or major
- Zoom and pan capabilities
- Export graph as image
- Animate node connections
- Show connection reason/type
- Real-time updates when data changes
