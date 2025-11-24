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
    protected Student roommate;

    /**
     * Compute a heuristic integer value representing how strongly this student
     * is connected to another student. Higher values indicate a stronger
     * connection (for example, more shared preferences or compatible majors).
     *
     * @param other the other student to compare against
     * @return an integer score describing connection strength
     */
    public abstract int calculateConnectionStrength(Student other);

    /**
     * Basic constructor to initialize common student fields.
     *
     * @param name   student's name
     * @param age    student's age
     * @param gender student's gender
     * @param major  student's major
     * @param gpa    student's GPA
     */

    public Student() {
        this.name = "";
        this.age = 0;
        this.gender = "";
        this.year = 1; // default year
        this.major = "";
        this.gpa = 0.0;
        this.roommatePreferences = new ArrayList<>();
        this.previousInternships = new ArrayList<>();
        this.roommate = null;
    }

    public Student(String name, int age, String gender, String major, double gpa, List<String> roommatePreferences,
            List<String> previousInternships) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = 1; // default year
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = roommatePreferences;
        this.previousInternships = previousInternships;
        this.roommate = null;
    }

    /**
     * Accessor for the assigned roommate (may be null).
     */

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int newAge) {
        this.age = newAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String newGender) {
        this.gender = newGender;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int newYear) {
        this.year = newYear;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String newMajor) {
        this.major = newMajor;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double newGpa) {
        this.gpa = newGpa;
    }

    public void addRoommatePreference(String preference) {
        this.roommatePreferences.add(preference);
    }

    public void removeRoommatePreference(String preference) {
        this.roommatePreferences.remove(preference);
    }

    public void addPreviousInternship(String internship) {
        this.previousInternships.add(internship);
    }

    public void removePreviousInternship(String internship) {
        this.previousInternships.remove(internship);
    }

    public List<String> getRoommatePreferences() {
        return roommatePreferences;
    }

    public List<String> getPreviousInternships() {
        return previousInternships;
    }

    public void setRoommatePreferences(List<String> preferences) {
        this.roommatePreferences = preferences;
    }

    public void setPreviousInternships(List<String> internships) {
        this.previousInternships = internships;
    }

    public Student getRoommate() {
        return roommate;
    }

    public void setRoommate(Student r) {
        this.roommate = r;
    }
}
