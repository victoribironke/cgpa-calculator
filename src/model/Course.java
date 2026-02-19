package model;

/**
 * Represents a single course with its name, credit hours, and grade.
 */
public class Course {
    private String name;
    private int creditHours;
    private String grade;

    public Course(String name, int creditHours, String grade) {
        this.name = name;
        this.creditHours = creditHours;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Returns the quality points for this course (creditHours × gradePoint).
     */
    public double getQualityPoints() {
        return creditHours * GradeUtils.gradeToPoint(grade);
    }
}
