import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {
    private final List<Appointment> appointments; // List to store all appointments

    // Constructor
    public AppointmentManager() {
        this.appointments = new ArrayList<>();
    }

    // Add a new appointment
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    // Replace the list of appointments
    public void setAppointments(List<Appointment> newAppointments) {
        appointments.clear();
        appointments.addAll(newAppointments);
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointments;
    }
}
