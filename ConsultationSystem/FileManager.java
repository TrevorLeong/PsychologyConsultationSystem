import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String USER_FILE = "users.dat";
    private static final String APPOINTMENT_FILE = "appointments.dat";

    // 保存用户数据
    public static void saveUsers(List<User> users) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(USER_FILE))) {
            out.writeObject(users);
        }
    }

    // 加载用户数据
    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(USER_FILE))) {
            return (List<User>) in.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 保存预约数据
    public static void saveAppointments(List<Appointment> appointments) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(APPOINTMENT_FILE))) {
            out.writeObject(appointments);
        }
    }

    // 加载预约数据
    @SuppressWarnings("unchecked")
    public static List<Appointment> loadAppointments() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(APPOINTMENT_FILE))) {
            return (List<Appointment>) in.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }
}