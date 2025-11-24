import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Concrete implementation of {@link Student} used by the project.
 *
 * This class models a university student with typical attributes (name,
 * year, major, GPA, roommate preferences, previous internships). Concrete
 * constructors and helper methods are expected to be added in the project.
 */
public class UniversityStudent extends Student {
    private List<UniversityStudent> friendsList = Collections.synchronizedList(new ArrayList<>());
    private List<String> chatHistory = Collections.synchronizedList(new ArrayList<>());

    /**
     * Construct a UniversityStudent with the basic fields.
     *
     * @param name                student's name
     * @param age                 student's age
     * @param gender              student's gender
     * @param year                student's year
     * @param major               student's major
     * @param gpa                 student's GPA
     * @param roommatePreferences list of roommate preferences
     * @param previousInternships list of previous internships
     */

    public UniversityStudent() {
        super();
    }

    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa,
            List<String> roommatePreferences,
            List<String> previousInternships) {
        super(name, age, gender, major, gpa, roommatePreferences, previousInternships);
        this.year = year;
        this.roommate = null;
    }

    public UniversityStudent getRoommate() {
        return (UniversityStudent) roommate;
    }

    public void setRoommate(UniversityStudent r) {
        this.roommate = r;
    }

    /**
     * Get the thread-safe friends list for this student.
     */
    public List<UniversityStudent> getFriendsList() {
        return friendsList;
    }

    /**
     * Get the thread-safe chat history for this student.
     */
    public List<String> getChatHistory() {
        return chatHistory;
    }

    /**
     * Simple heuristic to compute connection strength between students.
     * This can be improved later (preferences, shared internships, etc.).
     */
    @Override
    public int calculateConnectionStrength(Student other) {// Save for later
        int score = 0;
        Set<String> sharedPreferences = new HashSet<>(this.previousInternships);
        for (String pref : other.previousInternships) {
            if (sharedPreferences.contains(pref)) {
                score += 3;
                break;
            }
        }
        if (this.roommate != null && this.roommate.name.equals(other.name)) {
            score += 4;
        }
        if (this.major.equals(other.major)) {
            score += 2;
        }
        if (this.age == other.age) {
            score += 1;
        }
        return score;
    }
}
