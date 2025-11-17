import java.util.*;

/**
 * Abstract base class representing a student.
 *
 * Subclasses should provide concrete behavior for computing connection
 * strength between students (used by graph and matching algorithms).
 */
public abstract class Student {
    protected String name;
    protected int age;
    protected String gender;
    protected int year;
    protected String major;
    protected double gpa;
    protected List<String> roommatePreferences;
    protected List<String> previousInternships;

    /**
     * Compute a heuristic integer value representing how strongly this student
     * is connected to another student. Higher values indicate a stronger
     * connection (for example, more shared preferences or compatible majors).
     *
     * @param other the other student to compare against
     * @return an integer score describing connection strength
     */
    public abstract int calculateConnectionStrength(Student other);
}
