package model;

import java.util.List;
import java.util.Map;

/**
 * Utility class for grade-to-point conversion and GPA/CGPA calculations.
 * Uses the 5.0 Nigerian grading scale.
 */
public class GradeUtils {

    public static final String[] GRADES = {"A", "B", "C", "D", "E", "F"};
    public static final int[] CREDIT_OPTIONS = {1, 2, 3, 4, 5, 6};

    private static final Map<String, Double> GRADE_POINTS = Map.of(
        "A", 5.0,
        "B", 4.0,
        "C", 3.0,
        "D", 2.0,
        "E", 1.0,
        "F", 0.0
    );

    /**
     * Converts a letter grade to its numeric point value.
     * @param grade the letter grade (A–F)
     * @return the point value, or 0.0 if the grade is unrecognised
     */
    public static double gradeToPoint(String grade) {
        return GRADE_POINTS.getOrDefault(grade.toUpperCase(), 0.0);
    }

    /**
     * Calculates the GPA for a list of courses.
     * GPA = Σ(creditHours × gradePoint) / Σ(creditHours)
     *
     * @param courses list of courses in a semester
     * @return the GPA, or 0.0 if there are no credit hours
     */
    public static double calculateGPA(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return 0.0;
        }

        double totalQualityPoints = 0;
        int totalCredits = 0;

        for (Course course : courses) {
            totalQualityPoints += course.getQualityPoints();
            totalCredits += course.getCreditHours();
        }

        return totalCredits == 0 ? 0.0 : totalQualityPoints / totalCredits;
    }

    /**
     * Calculates the CGPA across multiple semesters.
     * CGPA = Σ(all quality points) / Σ(all credit hours)
     *
     * @param semesters a list where each element is a list of courses for one semester
     * @return the cumulative GPA, or 0.0 if there are no credit hours
     */
    public static double calculateCGPA(List<List<Course>> semesters) {
        double totalQualityPoints = 0;
        int totalCredits = 0;

        for (List<Course> semester : semesters) {
            for (Course course : semester) {
                totalQualityPoints += course.getQualityPoints();
                totalCredits += course.getCreditHours();
            }
        }

        return totalCredits == 0 ? 0.0 : totalQualityPoints / totalCredits;
    }
}
