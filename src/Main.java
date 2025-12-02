import static spark.Spark.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    // Use Gson for converting Java objects to JSON
    private static final Gson gson = new Gson();
    // In-memory cache of loaded students for runtime modifications (friend
    // requests, messages)
    private static java.util.List<UniversityStudent> cachedStudents = null;
    // Simple in-memory session store: token -> username
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();

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

        // Simple request logger to help debug incoming requests (method + path)
        before((request, response) -> {
            System.out.println("[Request] " + request.requestMethod() + " " + request.pathInfo());
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

        // Testcases Endpoint: GET /api/testcases - return built-in TestReal cases as
        // graphs + sample messages
        get("/api/testcases", (req, res) -> {
            res.type("application/json");
            try {
                java.util.List<java.util.Map<String, Object>> tcList = new java.util.ArrayList<>();

                java.util.List<java.util.List<UniversityStudent>> raw = new java.util.ArrayList<>();
                raw.add(TestReal.generateTestCase1());
                raw.add(TestReal.generateTestCase2());
                raw.add(TestReal.generateTestCase3());

                for (int i = 0; i < raw.size(); i++) {
                    java.util.List<UniversityStudent> students = raw.get(i);

                    // Ensure roommates are assigned for the testcase
                    GaleShapley.assignRoommates(students);

                    // Build student graph
                    StudentGraph sg = new StudentGraph(students);
                    Map<String, Object> graphData = buildGraphResponse(sg);

                    // Build sample messages for each link (two sample messages per edge)
                    java.util.List<java.util.Map<String, Object>> messages = new java.util.ArrayList<>();
                    java.util.List<java.util.Map<String, Object>> links = (java.util.List<java.util.Map<String, Object>>) graphData
                            .get("links");
                    if (links != null) {
                        for (java.util.Map<String, Object> link : links) {
                            String src = (String) link.get("source");
                            String tgt = (String) link.get("target");

                            java.util.Map<String, Object> m1 = new java.util.HashMap<>();
                            m1.put("from", src);
                            m1.put("to", tgt);
                            m1.put("text", "Hi " + tgt + ", this is a test message from " + src + " (auto-generated).");
                            m1.put("timestamp", System.currentTimeMillis());

                            java.util.Map<String, Object> m2 = new java.util.HashMap<>();
                            m2.put("from", tgt);
                            m2.put("to", src);
                            m2.put("text", "Hey " + src + ", replying to your message. (auto-generated)");
                            m2.put("timestamp", System.currentTimeMillis() + 1000);

                            java.util.Map<String, Object> edgeMsgs = new java.util.HashMap<>();
                            edgeMsgs.put("source", src);
                            edgeMsgs.put("target", tgt);
                            edgeMsgs.put("messages", java.util.Arrays.asList(m1, m2));

                            messages.add(edgeMsgs);
                        }
                    }

                    java.util.Map<String, Object> tcObj = new java.util.HashMap<>();
                    tcObj.put("id", i + 1);
                    tcObj.put("graph", graphData);
                    tcObj.put("messages", messages);
                    tcList.add(tcObj);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("testcases", tcList);
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(500);
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "Failed to build testcases: " + e.getMessage());
                return gson.toJson(error);
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

        // Login Endpoint: POST /api/login - body { name }
        post("/api/login", (req, res) -> {
            res.type("application/json");
            try {
                Map<String, String> body = gson.fromJson(req.body(), Map.class);
                String name = body.get("name");
                if (name == null || name.trim().isEmpty()) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Name is required");
                    return gson.toJson(err);
                }

                java.util.List<UniversityStudent> students = loadStudents();
                UniversityStudent found = null;
                for (UniversityStudent s : students) {
                    if (s.getName() != null && s.getName().equalsIgnoreCase(name.trim())) {
                        found = s;
                        break;
                    }
                }
                if (found == null) {
                    res.status(404);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Student not found");
                    return gson.toJson(err);
                }

                // Build a safe, non-cyclic representation of the student to avoid
                // StackOverflowError when Gson attempts to serialize object graphs
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("name", found.getName());
                studentMap.put("age", found.getAge());
                studentMap.put("gender", found.getGender());
                studentMap.put("year", found.getYear());
                studentMap.put("major", found.getMajor());
                studentMap.put("gpa", found.getGpa());
                studentMap.put("roommate", found.getRoommate() != null ? found.getRoommate().getName() : null);
                studentMap.put("roommatePreferences", found.getRoommatePreferences());
                studentMap.put("previousInternships", found.getPreviousInternships());
                java.util.List<String> friendNames = new java.util.ArrayList<>();
                if (found.getFriendsList() != null) {
                    for (UniversityStudent f : found.getFriendsList()) {
                        if (f != null && f.getName() != null)
                            friendNames.add(f.getName());
                    }
                }
                studentMap.put("friends", friendNames);
                java.util.List<String> chat = new java.util.ArrayList<>();
                if (found.getChatHistory() != null) {
                    chat.addAll(found.getChatHistory());
                }
                studentMap.put("chatHistory", chat);

                // Issue a simple session token for this login
                String token = UUID.randomUUID().toString();
                sessions.put(token, found.getName());

                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("student", studentMap);
                out.put("token", token);
                return gson.toJson(out);
            } catch (Exception e) {
                res.status(500);
                Map<String, Object> err = new HashMap<>();
                err.put("status", "error");
                err.put("message", e.getMessage());
                return gson.toJson(err);
            }
        });

        // Friend Request Endpoint: POST /api/friend-request - body { to }
        post("/api/friend-request", (req, res) -> {
            res.type("application/json");
            try {
                // Validate auth token and determine the sender server-side
                String authHeader = req.headers("Authorization");
                String senderName = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    senderName = sessions.get(token);
                }
                if (senderName == null) {
                    res.status(401);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Missing or invalid auth token");
                    return gson.toJson(err);
                }

                Map<String, String> body = gson.fromJson(req.body(), Map.class);
                String to = body != null ? body.get("to") : null;
                if (to == null) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "'to' required");
                    return gson.toJson(err);
                }

                java.util.List<UniversityStudent> students = loadStudents();
                UniversityStudent sender = null, receiver = null;
                for (UniversityStudent s : students) {
                    if (s.getName() != null && s.getName().equalsIgnoreCase(senderName.trim()))
                        sender = s;
                    if (s.getName() != null && s.getName().equalsIgnoreCase(to.trim()))
                        receiver = s;
                }
                if (sender == null || receiver == null) {
                    res.status(404);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Sender or receiver not found");
                    return gson.toJson(err);
                }

                // Add a pending friend request on the receiver (sender is derived from token)
                synchronized (receiver.getPendingFriendRequests()) {
                    if (!receiver.getPendingFriendRequests().contains(sender.getName())) {
                        receiver.getPendingFriendRequests().add(sender.getName());
                    }
                }

                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("message", "Friend request processed");
                // Return updated graph
                StudentGraph sg = new StudentGraph(students);
                out.put("graph", buildGraphResponse(sg));
                return gson.toJson(out);
            } catch (Exception e) {
                res.status(500);
                Map<String, Object> err = new HashMap<>();
                err.put("status", "error");
                err.put("message", e.getMessage());
                return gson.toJson(err);
            }
        });

        // Accept Friend Request: POST /api/friend-request/accept - body { from }
        post("/api/friend-request/accept", (req, res) -> {
            res.type("application/json");
            try {
                // Authenticate receiver via token
                String authHeader = req.headers("Authorization");
                String receiverName = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    receiverName = sessions.get(token);
                }
                if (receiverName == null) {
                    res.status(401);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Missing or invalid auth token");
                    return gson.toJson(err);
                }

                Map<String, String> body = gson.fromJson(req.body(), Map.class);
                String from = body != null ? body.get("from") : null;
                if (from == null) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "'from' required");
                    return gson.toJson(err);
                }

                java.util.List<UniversityStudent> students = loadStudents();
                UniversityStudent sender = null, receiver = null;
                for (UniversityStudent s : students) {
                    if (s.getName() != null && s.getName().equalsIgnoreCase(from.trim()))
                        sender = s;
                    if (s.getName() != null && s.getName().equalsIgnoreCase(receiverName.trim()))
                        receiver = s;
                }
                if (sender == null || receiver == null) {
                    res.status(404);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Sender or receiver not found");
                    return gson.toJson(err);
                }

                // Remove pending request and add to friends lists (both sides)
                // Avoid capturing mutable 'sender' in lambda by using a final local name
                final String senderNameLocal = sender.getName();
                synchronized (receiver.getPendingFriendRequests()) {
                    receiver.getPendingFriendRequests().removeIf(n -> n.equalsIgnoreCase(senderNameLocal));
                }
                synchronized (sender.getFriendsList()) {
                    if (!sender.getFriendsList().contains(receiver))
                        sender.getFriendsList().add(receiver);
                }
                synchronized (receiver.getFriendsList()) {
                    if (!receiver.getFriendsList().contains(sender))
                        receiver.getFriendsList().add(sender);
                }

                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("message", "Friend request accepted");
                StudentGraph sg = new StudentGraph(students);
                out.put("graph", buildGraphResponse(sg));
                return gson.toJson(out);
            } catch (Exception e) {
                res.status(500);
                Map<String, Object> err = new HashMap<>();
                err.put("status", "error");
                err.put("message", e.getMessage());
                return gson.toJson(err);
            }
        });

        // Message Endpoint: POST /api/message - body { to, text }
        post("/api/message", (req, res) -> {
            res.type("application/json");
            try {
                // Validate auth token and determine the sender server-side
                String authHeader = req.headers("Authorization");
                String senderName = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    senderName = sessions.get(token);
                }
                if (senderName == null) {
                    res.status(401);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Missing or invalid auth token");
                    return gson.toJson(err);
                }

                Map<String, String> body = gson.fromJson(req.body(), Map.class);
                String to = body != null ? body.get("to") : null;
                String text = body != null ? body.get("text") : null;
                if (to == null || text == null) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "'to' and 'text' required");
                    return gson.toJson(err);
                }

                java.util.List<UniversityStudent> students = loadStudents();
                UniversityStudent sender = null, receiver = null;
                for (UniversityStudent s : students) {
                    if (s.getName() != null && s.getName().equalsIgnoreCase(senderName.trim()))
                        sender = s;
                    if (s.getName() != null && s.getName().equalsIgnoreCase(to.trim()))
                        receiver = s;
                }
                if (sender == null || receiver == null) {
                    res.status(404);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Sender or receiver not found");
                    return gson.toJson(err);
                }

                String full = "[" + java.time.LocalDateTime.now().toString() + "] " + sender.getName() + ": " + text;
                synchronized (receiver.getChatHistory()) {
                    receiver.getChatHistory().add(full);
                }

                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("message", "Message delivered");
                StudentGraph sg = new StudentGraph(students);
                out.put("graph", buildGraphResponse(sg));
                return gson.toJson(out);
            } catch (Exception e) {
                res.status(500);
                Map<String, Object> err = new HashMap<>();
                err.put("status", "error");
                err.put("message", e.getMessage());
                return gson.toJson(err);
            }
        });

        // Add Student Endpoint: POST /api/student
        post("/api/student", (req, res) -> {
            res.type("application/json");

            try {
                // Parse incoming student data robustly
                Map<String, Object> studentData = gson.fromJson(req.body(), Map.class);

                if (studentData == null) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Invalid JSON body");
                    return gson.toJson(err);
                }

                String name = studentData.get("name") != null ? studentData.get("name").toString() : null;
                if (name == null || name.trim().isEmpty()) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "'name' is required");
                    return gson.toJson(err);
                }

                int age = 18;
                try {
                    Object ageObj = studentData.get("age");
                    if (ageObj instanceof Number) {
                        age = ((Number) ageObj).intValue();
                    } else if (ageObj != null) {
                        age = Integer.parseInt(ageObj.toString());
                    }
                } catch (Exception ignored) {
                }

                String gender = studentData.get("gender") != null ? studentData.get("gender").toString() : "Other";

                int year = 1;
                try {
                    Object yearObj = studentData.get("year");
                    if (yearObj instanceof Number) {
                        year = ((Number) yearObj).intValue();
                    } else if (yearObj != null) {
                        year = Integer.parseInt(yearObj.toString());
                    }
                } catch (Exception ignored) {
                }

                String major = studentData.get("major") != null ? studentData.get("major").toString() : "Undeclared";

                double gpa = 0.0;
                try {
                    Object gpaObj = studentData.get("gpa");
                    if (gpaObj instanceof Number) {
                        gpa = ((Number) gpaObj).doubleValue();
                    } else if (gpaObj != null) {
                        gpa = Double.parseDouble(gpaObj.toString());
                    }
                } catch (Exception ignored) {
                }

                java.util.List<String> roommatePreferences = new java.util.ArrayList<>();
                Object rpObj = studentData.get("roommatePreferences");
                if (rpObj instanceof java.util.List) {
                    for (Object o : (java.util.List) rpObj) {
                        if (o != null)
                            roommatePreferences.add(o.toString());
                    }
                } else if (rpObj != null) {
                    // comma separated string
                    String s = rpObj.toString();
                    for (String part : s.split(",")) {
                        if (!part.trim().isEmpty() && !part.trim().equalsIgnoreCase("None"))
                            roommatePreferences.add(part.trim());
                    }
                }

                java.util.List<String> previousInternships = new java.util.ArrayList<>();
                Object piObj = studentData.get("previousInternships");
                if (piObj instanceof java.util.List) {
                    for (Object o : (java.util.List) piObj) {
                        if (o != null)
                            previousInternships.add(o.toString());
                    }
                } else if (piObj != null) {
                    String s = piObj.toString();
                    for (String part : s.split(",")) {
                        if (!part.trim().isEmpty() && !part.trim().equalsIgnoreCase("None"))
                            previousInternships.add(part.trim());
                    }
                }

                // Create new student
                UniversityStudent newStudent = new UniversityStudent(
                        name,
                        age,
                        gender,
                        year,
                        major,
                        gpa,
                        roommatePreferences,
                        previousInternships);

                // Save to file (propagate IO errors to client)
                try {
                    DataParser.appendStudentToFile(newStudent, "FINAL_STUDENTS.txt");
                } catch (IOException ioex) {
                    System.err.println("[Main] Failed to append student to file: " + ioex.getMessage());
                    res.status(500);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Failed to save student: " + ioex.getMessage());
                    return gson.toJson(err);
                }

                // Reload all students including the new one
                java.util.List<UniversityStudent> allStudents = reloadStudentsFromFiles();
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
            try {
                Map<String, Object> input = gson.fromJson(req.body(), Map.class);
                String studentName = input.get("student") != null ? input.get("student").toString() : null;
                String company = input.get("company") != null ? input.get("company").toString() : null;

                if (studentName == null || studentName.trim().isEmpty() || company == null
                        || company.trim().isEmpty()) {
                    res.status(400);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Both 'student' and 'company' must be provided in request body.");
                    return gson.toJson(err);
                }

                java.util.List<UniversityStudent> allStudents = loadStudents();
                GaleShapley.assignRoommates(allStudents);
                StudentGraph sg = new StudentGraph(allStudents);

                UniversityStudent start = GaleShapley.nameToStudent(studentName, allStudents);
                if (start == null) {
                    res.status(404);
                    Map<String, Object> err = new HashMap<>();
                    err.put("status", "error");
                    err.put("message", "Student '" + studentName + "' not found.");
                    return gson.toJson(err);
                }

                ReferralPathFinder finder = new ReferralPathFinder(sg);
                java.util.List<UniversityStudent> path = finder.findReferralPath(start, company);

                java.util.List<java.util.Map<String, String>> edges = new java.util.ArrayList<>();
                if (path != null && path.size() >= 2) {
                    for (int i = 0; i < path.size() - 1; i++) {
                        java.util.Map<String, String> e = new java.util.HashMap<>();
                        e.put("source", path.get(i).getName());
                        e.put("target", path.get(i + 1).getName());
                        edges.add(e);
                    }
                }

                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("path",
                        path == null ? new java.util.ArrayList<>() : path.stream().map(s -> s.getName()).toArray());
                out.put("edges", edges);
                return gson.toJson(out);

            } catch (Exception e) {
                res.status(500);
                Map<String, Object> err = new HashMap<>();
                err.put("status", "error");
                err.put("message", "Referral path search failed: " + e.getMessage());
                return gson.toJson(err);
            }
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
            // Friends list (names only) and chat history
            java.util.List<String> friendNames = new java.util.ArrayList<>();
            if (s.getFriendsList() != null) {
                for (UniversityStudent f : s.getFriendsList()) {
                    if (f != null && f.getName() != null)
                        friendNames.add(f.getName());
                }
            }
            node.put("friends", friendNames);

            java.util.List<String> chat = new java.util.ArrayList<>();
            if (s.getChatHistory() != null) {
                chat.addAll(s.getChatHistory());
            }
            node.put("chatHistory", chat);
            // Pending friend requests (names) so recipients can accept them in UI
            java.util.List<String> pending = new java.util.ArrayList<>();
            if (s.getPendingFriendRequests() != null) {
                pending.addAll(s.getPendingFriendRequests());
            }
            node.put("pendingFriendRequests", pending);

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
        // If cache exists, return it so runtime updates persist across requests
        if (cachedStudents != null) {
            return cachedStudents;
        }

        String[] candidates = new String[] { "FINAL_STUDENTS.txt", "src/FINAL_STUDENTS.txt",
                "result/FINAL_STUDENTS.txt" };
        for (String path : candidates) {
            try {
                java.util.List<UniversityStudent> list = DataParser.parseStudents(path);
                if (list != null && !list.isEmpty()) {
                    System.out.println("[Main] Loaded " + list.size() + " students from: " + path);
                    cachedStudents = list;
                    return cachedStudents;
                } else {
                    System.out.println("[Main] No students found or file empty at: " + path);
                }
            } catch (Exception e) {
                System.out.println("[Main] Error reading " + path + ": " + e.getMessage());
            }
        }
        System.out.println("[Main] Could not find any student file. Returning empty list.");
        cachedStudents = new java.util.ArrayList<>();
        return cachedStudents;
    }

    // Force reload students from files (clears cache)
    private static java.util.List<UniversityStudent> reloadStudentsFromFiles() {
        cachedStudents = null;
        return loadStudents();
    }
}
