import java.io.Serializable;

public class Feedback implements Serializable {
    private final String studentName;
    private final String lecturerName;
    private String content;
    private int rating;

    public Feedback(String studentName, String lecturerName, String content, int rating) {
        this.studentName = studentName;
        this.lecturerName = lecturerName;
        this.content = content;
        this.rating = rating;
    }

    // Getters
    public String getStudentName() {
        return studentName;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    // Setters
    public void setContent(String content) {
        this.content = content;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}