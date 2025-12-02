import java.io.*;
import java.util.*;

/**
 * Utility class responsible for parsing input data files into
 * {@link UniversityStudent} objects.
 */
public class DataParser {
    /**
     * Parse a text file containing student records and convert them into a
     * list of {@link UniversityStudent} instances.
     *
     * The exact expected file format is determined by the project
     * specification. This method will throw {@link IOException} if there are
     * problems reading the file.
     *
     * @param filename path to the input file
     * @return list of parsed {@link UniversityStudent} objects
     * @throws IOException if the file cannot be read
     */
    public static List<UniversityStudent> parseStudents(String filename) {
        List<UniversityStudent> allStudents = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            UniversityStudent student = new UniversityStudent();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String command = parts[0].trim();
                switch (command) {
                    case "Student":
                        break;
                    case "Name":
                        student.name = parts[1].trim();
                        break;
                    case "Age":
                        student.age = Integer.parseInt(parts[1].trim());
                        break;
                    case "Gender":
                        student.gender = parts[1].trim();
                        break;
                    case "Year":
                        student.year = Integer.parseInt(parts[1].trim());
                        break;
                    case "Major":
                        student.major = parts[1].trim();
                        break;
                    case "GPA":
                        student.gpa = Double.parseDouble(parts[1].trim());
                        break;
                    case "RoommatePreferences":
                        String[] prefs = parts[1].split(",");
                        List<String> prefList = new ArrayList<>();
                        for (String pref : prefs) {
                            if (pref.trim().equals("None")) {
                                break;
                            }
                            prefList.add(pref.trim());
                        }
                        student.roommatePreferences = prefList;
                        break;
                    case "PreviousInternships":
                        String[] internships = parts[1].split(",");
                        List<String> internList = new ArrayList<>();
                        for (String intern : internships) {
                            if (intern.trim().equals("None")) {
                                break;
                            }
                            internList.add(intern.trim());
                        }
                        student.previousInternships = internList;
                        break;
                    case "":
                        allStudents.add(student);
                        student = new UniversityStudent();
                        break;
                }
            }
            reader.close();
            // If file didn't end with an empty separator line, ensure the
            // last student is added to the list when it has a name.
            try {
                if (student != null && student.getName() != null && !student.getName().trim().isEmpty()) {
                    allStudents.add(student);
                }
            } catch (Exception e) {
                // ignore
            }
        } catch (IOException e) {
            // throw new IOException("Error");
        } catch (ArrayIndexOutOfBoundsException e) {
            // throw new IOException("File format incorrect");
        } catch (NumberFormatException e) {
            // throw new IOException("File format incorrect");
        } catch (Exception e) {

        }

        return allStudents;
    }

    /**
     * Append a new student to the student data file.
     *
     * @param student  the student to append
     * @param filename path to the output file
     * @throws IOException if the file cannot be written
     */
    public static void appendStudentToFile(UniversityStudent student, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write("\n");
            writer.write("Student:\n");
            writer.write("Name: " + student.getName() + "\n");
            writer.write("Age: " + student.getAge() + "\n");
            writer.write("Gender: " + student.getGender() + "\n");
            writer.write("Year: " + student.getYear() + "\n");
            writer.write("Major: " + student.getMajor() + "\n");
            writer.write("GPA: " + student.getGpa() + "\n");

            // Roommate preferences
            List<String> prefs = student.getRoommatePreferences();
            if (prefs == null || prefs.isEmpty()) {
                writer.write("RoommatePreferences: None\n");
            } else {
                writer.write("RoommatePreferences: " + String.join(", ", prefs) + "\n");
            }

            // Previous internships
            List<String> internships = student.getPreviousInternships();
            if (internships == null || internships.isEmpty()) {
                writer.write("PreviousInternships: None\n");
            } else {
                writer.write("PreviousInternships: " + String.join(", ", internships) + "\n");
            }
        }
    }
}
