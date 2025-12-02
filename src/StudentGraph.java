import java.util.*;

/**
 * Minimal placeholder for StudentGraph used by the project.
 *
 * This stub provides just enough API surface for documentation generation
 * and basic compilation during local tasks. It is not a full implementation.
 *
 */

public class StudentGraph {
    /**
     * Edge structure representing a weighted connection from one student to
     * another.
     */
    public static class Edge implements Comparable<Edge> {
        public UniversityStudent neighbor;
        public int weight;

        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }

        public UniversityStudent getNeighbor() {
            return this.neighbor;
        }

        public int getWeight() {
            return this.weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.getWeight(), other.getWeight());
        }
    }

    private Map<UniversityStudent, List<Edge>> ajacentList = new HashMap<>();

    /**
     * Construct a StudentGraph from a list of students.
     * 
     * @param students list of students
     */
    public StudentGraph(List<UniversityStudent> students) {
        // stub
        // First, initialize all students as nodes
        for (UniversityStudent s : students) {
            if (!ajacentList.containsKey(s)) {
                ajacentList.put(s, new ArrayList<>());
            }
        }

        // Second, add edges from roommate preferences
        for (UniversityStudent newStudent : students) {
            List<Edge> currentList = ajacentList.get(newStudent);
            if (currentList == null) {
                currentList = new ArrayList<>();
            }
            for (String name : newStudent.roommatePreferences) {
                UniversityStudent otherStudent = nameToStudent(name, students);
                // If nameToStudent couldn't find a matching student, skip this preference
                if (otherStudent == null) {
                    continue;
                }
                // Defensive: ensure otherStudent is not null before calculating strength
                int weight = newStudent.calculateConnectionStrength(otherStudent);
                weight = 10 - weight;
                Edge newEdge = new Edge(otherStudent, weight);
                currentList.add(newEdge);
            }
            ajacentList.put(newStudent, currentList);
        }

        // Third, add edges for actual roommate assignments (post-matching)
        for (UniversityStudent s : students) {
            UniversityStudent roommate = s.getRoommate();
            if (roommate != null) {
                // Check if edge from s to roommate already exists
                List<Edge> edges = ajacentList.get(s);
                boolean edgeExists = false;
                for (Edge e : edges) {
                    if (e.neighbor.equals(roommate)) {
                        edgeExists = true;
                        break;
                    }
                }
                // If no edge exists, add one with weight 0 (roommate edge)
                if (!edgeExists) {
                    edges.add(new Edge(roommate, 0));
                }
            }
        }
    }

    public List<Edge> getUniversityStudent(UniversityStudent s) {
        return ajacentList.get(s);
    }

    private UniversityStudent nameToStudent(String name, List<UniversityStudent> mylist) {
        for (UniversityStudent newStudent : mylist) {
            if (newStudent.getName().equals(name)) {
                return newStudent;
            }
        }
        return null;
    }

    /**
     * Return all nodes in the graph.
     */
    public List<UniversityStudent> getAllNodes() {
        List<UniversityStudent> ans = new ArrayList<>();
        for (UniversityStudent newStudent : ajacentList.keySet()) {
            ans.add(newStudent);
        }

        return ans;
    }

    /**
     * Return neighbor edges for a given student.
     */
    public List<Edge> getNeighbors(UniversityStudent s) {
        return ajacentList.get(s);
    }

    /**
     * Prints the entire graph structure, showing each student
     * and their weighted connections (edges) to other students.
     */
    public void printGraph() {
        System.out.println("--- Student Graph Adjacency List ---");

        // Iterate over all nodes (UniversityStudent keys) in the graph
        for (UniversityStudent student : ajacentList.keySet()) {
            System.out.print("**Student: " + student.getName() + "** -> [");

            // Get the list of edges for the current student
            List<Edge> neighbors = ajacentList.get(student);

            if (neighbors != null) {
                // Iterate through all neighbors (Edges) of the current student
                for (int i = 0; i < neighbors.size(); i++) {
                    Edge edge = neighbors.get(i);
                    // Print the neighbor's name and the edge weight
                    System.out.print(edge.neighbor.getName() + " (Weight: " + edge.weight + ")");

                    // Add a separator if it's not the last edge
                    if (i < neighbors.size() - 1) {
                        System.out.print(", ");
                    }
                }
            }
            System.out.println("]");
        }
        System.out.println("------------------------------------");
    }
}
