import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {
    private String appointmentId;
    private final String studentName;
    private final String lecturerName;
    private final LocalDateTime appointmentTime;
    private final String description;
    private String status;

    public Appointment(String studentName, String lecturerName, LocalDateTime appointmentTime, String description) {
        this.studentName = studentName;
        this.lecturerName = lecturerName;
        this.appointmentTime = appointmentTime;
        this.description = description;
        this.status = "Pending";
    }

    private boolean confirmed = false;

    // 其他属性和方法...

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // Getter for appointmentId
    public String getAppointmentId() {
        return appointmentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Student: %s, Lecturer: %s, Time: %s, Status: %s, Description: %s",
                studentName, lecturerName, appointmentTime, status, description);
    }
}