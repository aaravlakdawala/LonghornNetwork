import static spark.Spark.*;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Main {

    // Use Gson for converting Java objects to JSON
    private static final Gson gson = new Gson();

    public static void main(String[] args) {

        // 1. CONFIGURATION (Port & WebSocket - MUST BE FIRST)
        port(8080); // Set the server port to 8080

        // 2. WEBSOCKET SETUP (MUST be before any other routes/filters)
        // This maps the WebSocketServer class to the path /websocketendpoint
        webSocket("/websocketendpoint", WebSocketServer.class);

        // 3. CORS (Cross-Origin Resource Sharing) Setup & Filters
        // These are the first routes/filters, allowing Spark to start mapping
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // Allow requests from the React frontend's origin (e.g., http://localhost:5173)
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:5173");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        // 4. REST Endpoint Setup

        // Health Check Endpoint: GET /health
        get("/health", (req, res) -> {
            res.type("application/json");
            Map<String, String> response = new HashMap<>();
            response.put("status", "UP");
            response.put("application", "Spark/WebSocket Server");
            return gson.toJson(response);
        });

        // Initial Graph Data Endpoint: GET /api/graph (NOW WITH TRY/CATCH)
        get("/api/graph", (req, res) -> {
            res.type("application/json");

            try {
                // Load data using the robust method
                java.util.List<UniversityStudent> allStudents = loadStudents();

                // Processing logic
                GaleShapley.assignRoommates(allStudents);
                StudentGraph sg = new StudentGraph(allStudents);

                // Build JSON response
                Map<String, Object> graphData = buildGraphResponse(sg);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("type", "initial-graph-data");
                response.put("graph", graphData);

                System.out.println("[Main] Responded to /api/graph with " + sg.getAllNodes().size() + " nodes.");
                return gson.toJson(response);

            } catch (Exception e) {
                // Catch ANY exception that happens during the route processing
                System.err.println("----------------------------------------------------------");
                System.err.println("[Main] CRITICAL ERROR in /api/graph route! Stack Trace Below:");
                e.printStackTrace();
                System.err.println("----------------------------------------------------------");

                // Return a proper JSON error response (HTTP 500)
                res.status(500);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message",
                        "Internal Server Error: Data processing failed. Check Java console for stack trace.");
                return gson.toJson(errorResponse);
            }
        });

        // Command Endpoint: POST /api/command (Used for Refresh)
        post("/api/command", (req, res) -> {
            res.type("application/json");

            // Parse incoming command
            Map commandData = gson.fromJson(req.body(), Map.class);
            String command = commandData != null && commandData.get("command") != null
                    ? commandData.get("command").toString()
                    : "Unknown";

            // Load data for the response
            java.util.List<UniversityStudent> allStudents = loadStudents();
            GaleShapley.assignRoommates(allStudents);
            StudentGraph sg = new StudentGraph(allStudents);

            // Build JSON response from the graph object
            Map<String, Object> graphData = buildGraphResponse(sg);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Command received and processed: " + command);
            response.put("command", command);
            response.put("graph", graphData);

            System.out.println("[Main] Received POST /api/command: " + command);
            System.out.println("[Main] Responded with dynamic graph with " + sg.getAllNodes().size() + " nodes.");

            return gson.toJson(response);
        });

        // Add Student Endpoint: POST /api/student
        post("/api/student", (req, res) -> {
            res.type("application/json");

            try {
                // Parse incoming student data
                Map<String, Object> studentData = gson.fromJson(req.body(), Map.class);

                // Extract fields
                String name = (String) studentData.get("name");
                double age = ((Number) studentData.get("age")).doubleValue();
                String gender = (String) studentData.get("gender");
                double year = ((Number) studentData.get("year")).doubleValue();
                String major = (String) studentData.get("major");
                double gpa = ((Number) studentData.get("gpa")).doubleValue();

                java.util.List<String> roommatePreferences = (java.util.List<String>) studentData
                        .get("roommatePreferences");
                if (roommatePreferences == null)
                    roommatePreferences = new java.util.ArrayList<>();

                java.util.List<String> previousInternships = (java.util.List<String>) studentData
                        .get("previousInternships");
                if (previousInternships == null)
                    previousInternships = new java.util.ArrayList<>();

                // Create new student
                UniversityStudent newStudent = new UniversityStudent(
                        name,
                        (int) age,
                        gender,
                        (int) year,
                        major,
                        gpa,
                        roommatePreferences,
                        previousInternships);

                // Save to file
                DataParser.appendStudentToFile(newStudent, "FINAL_STUDENTS.txt");

                // Reload all students including the new one
                java.util.List<UniversityStudent> allStudents = loadStudents();
                GaleShapley.assignRoommates(allStudents);
                StudentGraph sg = new StudentGraph(allStudents);

                // Build JSON response
                Map<String, Object> graphData = buildGraphResponse(sg);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Student " + name + " added successfully");
                response.put("graph", graphData);

                System.out.println("[Main] Added new student: " + name);
                return gson.toJson(response);

            } catch (Exception e) {
                System.err.println("[Main] ERROR adding student: " + e.getMessage());
                e.printStackTrace();

                res.status(400);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Failed to add student: " + e.getMessage());
                return gson.toJson(errorResponse);
            }
        });

        // DEBUG: Log before registering /api/referral-path
        System.out.println("[Main] About to register /api/referral-path endpoint");

        // Find Referral Path Endpoint: POST /api/referral-path
        post("/api/referral-path", (req, res) -> {
            res.type("application/json");
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Referral path endpoint works");
            return gson.toJson(response);
        });

        System.out.println("[Main] Registered /api/referral-path endpoint");

        // 5. Start the Server
        // Global exception handler: return JSON on unhandled exceptions (avoid HTML
        // error pages)
        exception(Exception.class, (e, req, res) -> {
            res.type("application/json");
            res.status(500);
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("status", "error");
            error.put("message", e.getClass().getName() + ": " + e.getMessage());
            res.body(gson.toJson(error));
            System.err.println("[Main] Unhandled exception caught by global handler:");
            e.printStackTrace();
        });

        init();

        System.out.println("\n\n**************************************************************");
        System.out.println("✅ Java Spark Backend Running!");
        System.out.println("   REST endpoints available on http://localhost:8080");
        System.out.println("   WebSocket endpoint ready on ws://localhost:8080/websocketendpoint");
        System.out.println("**************************************************************");
    }

    // --- UTILITY METHODS ---

    private static Map<String, Object> buildGraphResponse(StudentGraph sg) {
        java.util.List<java.util.Map<String, Object>> nodes = new java.util.ArrayList<>();
        java.util.List<java.util.Map<String, Object>> links = new java.util.ArrayList<>();

        for (UniversityStudent s : sg.getAllNodes()) {
            java.util.Map<String, Object> node = new java.util.HashMap<>();
            String id = s.getName() == null ? "" : s.getName();

            // Full student data in each node
            node.put("id", id);
            node.put("name", s.getName());
            node.put("age", s.getAge());
            node.put("gender", s.getGender());
            node.put("year", s.getYear());
            node.put("major", s.getMajor());
            node.put("gpa", s.getGpa());
            node.put("roommate", s.getRoommate() != null ? s.getRoommate().getName() : null);
            node.put("roommatePreferences", s.getRoommatePreferences());
            node.put("previousInternships", s.getPreviousInternships());

            nodes.add(node);

            java.util.List<StudentGraph.Edge> edges = sg.getNeighbors(s);
            if (edges != null) {
                for (StudentGraph.Edge e : edges) {
                    java.util.Map<String, Object> link = new java.util.HashMap<>();
                    String target = e.neighbor == null ? "" : e.neighbor.getName();
                    if (id.compareTo(target) < 0) {
                        link.put("source", id);
                        link.put("target", target);
                        link.put("weight", e.weight);
                        links.add(link);
                    }
                }
            }
        }

        java.util.Map<String, Object> graph = new java.util.HashMap<>();
        graph.put("nodes", nodes);
        graph.put("links", links);
        return graph;
    }

    private static java.util.List<UniversityStudent> loadStudents() {
        String[] candidates = new String[] { "FINAL_STUDENTS.txt", "src/FINAL_STUDENTS.txt",
                "result/FINAL_STUDENTS.txt" };
        for (String path : candidates) {
            try {
                // Assuming DataParser.parseStudents is a static method that handles file
                // reading
                java.util.List<UniversityStudent> list = DataParser.parseStudents(path);
                if (list != null && !list.isEmpty()) {
                    System.out.println("[Main] Loaded " + list.size() + " students from: " + path);
                    return list;
                } else {
                    System.out.println("[Main] No students found or file empty at: " + path);
                }
            } catch (Exception e) {
                // Note: We suppress stack trace here, but the main route try/catch will show it
                // if it crashes.
                System.out.println("[Main] Error reading " + path + ": " + e.getMessage());
            }
        }
        System.out.println("[Main] Could not find any student file. Returning empty list.");
        return new java.util.ArrayList<>();
    }
}
