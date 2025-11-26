import java.util.*;

/**
 * Static utility class implementing the Gale-Shapley stable matching
 * algorithm with fallback pairing for unmatched students.
 * Handles students with empty preference lists by pairing them in Phase 2.
 */
public class GaleShapley {
    /**
     * Assign roommates for the provided list of students using an
     * implementation of the Gale-Shapley algorithm. Implementations should
     * update student state to reflect assigned roommates.
     *
     * @param students list of {@link UniversityStudent} instances to match
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        Queue<UniversityStudent> freeStudents = new LinkedList<>(students);
        Map<UniversityStudent, Integer> indexes = new HashMap<>();

        // Phase 1: Standard Gale-Shapley matching using preference lists
        while (!freeStudents.isEmpty()) {
            UniversityStudent prosper = freeStudents.poll();
            if (indexes.getOrDefault(prosper, 0) >= prosper.getRoommatePreferences().size()) {
                // Proposer is out of options; they remain unmatched for now.
                continue;
            }
            String preferredName = prosper.getRoommatePreferences().get(indexes.getOrDefault(prosper, 0));
            UniversityStudent preferredStudent = nameToStudent(preferredName, students);
            indexes.put(prosper, indexes.getOrDefault(prosper, 0) + 1);

            if (preferredStudent == null) {
                freeStudents.add(prosper);
                continue;
            }
            UniversityStudent currentRoommate = preferredStudent.getRoommate();
            if (currentRoommate == null) {
                prosper.setRoommate(preferredStudent);
                preferredStudent.setRoommate(prosper);
                freeStudents.remove(preferredStudent);

            } else if (prefersOver(prosper.getName(), currentRoommate.getName(),
                    preferredStudent.roommatePreferences)) {
                currentRoommate.setRoommate(null);
                freeStudents.add(currentRoommate);
                prosper.setRoommate(preferredStudent);
                preferredStudent.setRoommate(prosper);
                freeStudents.remove(prosper);

            } else {
                freeStudents.add(prosper);
            }

        }

        // Phase 2: Pair up remaining unmatched students
        // This handles students with empty preference lists or those who exhausted
        // their list
        List<UniversityStudent> unmatched = new ArrayList<>();
        for (UniversityStudent s : students) {
            if (s.getRoommate() == null) {
                unmatched.add(s);
            }
        }

        // Pair them up greedily
        for (int i = 0; i < unmatched.size() - 1; i += 2) {
            UniversityStudent student1 = unmatched.get(i);
            UniversityStudent student2 = unmatched.get(i + 1);
            student1.setRoommate(student2);
            student2.setRoommate(student1);
        }
    }

    public static UniversityStudent nameToStudent(String name, List<UniversityStudent> mylist) {
        if (name == null || mylist == null)
            return null;
        String target = name.trim();
        if (target.isEmpty())
            return null;
        for (UniversityStudent s : mylist) {
            String sname = s.getName();
            if (sname != null && sname.trim().equalsIgnoreCase(target)) {
                return s;
            }
        }
        return null;
    }

    public static boolean prefersOver(String potentialRoomate, String currentRoommate, List<String> fullList) {
        for (String name : fullList) {
            if (name.equals(potentialRoomate)) {
                return true;
            }
            if (name.equals(currentRoommate)) {
                return false;
            }
        }
        return false;
    }
}
