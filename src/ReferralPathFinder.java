import java.nio.file.Path;
import java.util.*;

/**
 * Finds referral paths from a starting student to any student who has worked
 * at a target company. The implementation typically relies on a
 * {@link StudentGraph} representation and priority queue search.
 */
public class ReferralPathFinder {

    public static class PathNode {
        public UniversityStudent student;
        public List<StudentGraph.Edge> path;
        public int totalWeight;

        public PathNode(UniversityStudent student, List<StudentGraph.Edge> path, int totalWeight) {
            this.student = student;
            this.path = path;
            this.totalWeight = totalWeight;
        }

        public PathNode(UniversityStudent student) {
            this.student = student;
            this.path = new ArrayList<>();
            this.totalWeight = 0;
        }
    }

    public class PathResult {
        public List<UniversityStudent> students;
        public int totalWeight;

        public PathResult(List<UniversityStudent> students, int totalWeight) {
            this.students = students;
            this.totalWeight = totalWeight;
        }
    }

    /**
     * Create a finder backed by the provided student graph.
     *
     * @param graph the {@link StudentGraph} used for searches
     */
    private StudentGraph graph;

    public ReferralPathFinder(StudentGraph graph) {
        this.graph = graph;
    }

    /**
     * Attempt to find a referral path from the given start student to any
     * student who lists {@code targetCompany} in their previous internships.
     *
     * @param start         the starting {@link UniversityStudent}
     * @param targetCompany the company name to search for
     * @return an ordered list of {@link UniversityStudent} instances forming a
     *         referral chain, or an empty list if no path exists
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        List<UniversityStudent> candidates = new ArrayList<>();
        for (UniversityStudent s : graph.getAllNodes()) {
            if (s.previousInternships.contains(targetCompany)) {
                candidates.add(s);
            }
        }

        List<UniversityStudent> bestPath = new ArrayList<>();
        int bestWeight = Integer.MAX_VALUE;

        for (UniversityStudent end : candidates) {
            PathResult currentPath = helper(start, end);
            if (bestWeight > currentPath.totalWeight && currentPath.students != null) {
                bestWeight = currentPath.totalWeight;
                bestPath = currentPath.students;
            }

        }

        return bestPath;

    }

    public PathResult helper(UniversityStudent start, UniversityStudent end) {
        PriorityQueue<PathNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.totalWeight));
        Map<UniversityStudent, Integer> visited = new HashMap<>();

        pq.add(new PathNode(start));
        visited.put(start, 0);

        while (!pq.isEmpty()) {
            PathNode currentNode = pq.poll();
            UniversityStudent currentStudent = currentNode.student;

            if (currentStudent.equals(end)) {

                return convertEdgesToStudents(start, currentNode.path);
            }

            if (currentNode.totalWeight > visited.getOrDefault(currentStudent, Integer.MAX_VALUE)) {
                continue;
            }

            for (StudentGraph.Edge edge : graph.getNeighbors(currentStudent)) {
                if (visited.getOrDefault(edge.neighbor, Integer.MAX_VALUE) > currentNode.totalWeight + edge.weight) {
                    List<StudentGraph.Edge> newPath = new ArrayList<>(currentNode.path);
                    newPath.add(edge);
                    pq.add(new PathNode(edge.neighbor, newPath, currentNode.totalWeight + edge.weight));
                    visited.put(edge.neighbor, currentNode.totalWeight + edge.weight);
                }
            }
        }

        return new PathResult(null, 0);
    }

    /**
     * Converts a list of edges into the corresponding list of students (nodes) in
     * the path.
     * 
     * @param start    The starting student.
     * @param edgePath The sequence of edges representing the path.
     * @return The sequence of students [start, node1, node2, ..., end].
     */
    private PathResult convertEdgesToStudents(UniversityStudent start, List<StudentGraph.Edge> edgePath) {
        List<UniversityStudent> studentPath = new ArrayList<>();
        int totalWeight = 0;
        studentPath.add(start);
        for (StudentGraph.Edge edge : edgePath) {

            studentPath.add(edge.neighbor);
            totalWeight += edge.weight;
        }

        return new PathResult(studentPath, totalWeight);
    }
}
