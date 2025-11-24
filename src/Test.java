import java.util.*;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test class executed.");
        List<UniversityStudent> allStudents = DataParser.parseStudents("normal_1.txt");
        GaleShapley.assignRoommates(allStudents);
        for (Student s : allStudents) {
            System.out.println(s.name);
            System.out.println(s.age);
            System.out.println("Roomate " + (s.getRoommate() != null ? s.getRoommate().getName() : "None"));

            for (String intern : s.previousInternships) {
                System.out.println(intern);
            }
        }
        StudentGraph graph = new StudentGraph(allStudents);
        graph.printGraph();

    }

}
