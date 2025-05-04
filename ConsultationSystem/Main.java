import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final static Scanner scanner = new Scanner(System.in);
    private final static AppointmentManager appointmentManager = new AppointmentManager();
    private final static UserManager userManager = UserManager.getInstance();

    public static void main(String[] args) {
        loadData();
        showMenu();
        saveData();
    }

    private static void loadData() {
        try {
            List<User> savedUsers = FileManager.loadUsers();
            for (User user : savedUsers) {
                userManager.addUser(user);
            }
            appointmentManager.setAppointments(FileManager.loadAppointments());
        } catch (IOException e) {
            System.out.println("Error loading data: File I/O error - " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading data: Class not found - " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Error loading data: Security error - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("No previous data found, starting fresh.");
        }
    }

    private static void saveData() {
    try {
        FileManager.saveUsers(userManager.getAllUsers());
        FileManager.saveAppointments(appointmentManager.getAllAppointments());
        System.out.println("Data saved successfully.");
    } catch (IOException e) {
        System.out.println("Error saving data: File I/O error - " + e.getMessage());
    } catch (SecurityException e) {
        System.out.println("Error saving data: Security error - " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Error saving data: Unexpected error - " + e.getMessage());
    }
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\n=== Psychology Consultation System ===");
            System.out.println("1. Register User");
            System.out.println("2. Create Appointment");
            System.out.println("3. View Appointments");
            System.out.println("4. Confirm Appointment");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符

                switch (choice) {
                    case 1 -> registerUser();
                    case 2 -> createAppointment();
                    case 3 -> viewAppointments();
                    case 4 -> confirmAppointment();
                    case 5 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // 清除无效输入
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (student/lecturer): ");
        String role = scanner.nextLine().toLowerCase();

        if (role.equals("student") || role.equals("lecturer")) {
            User newUser = new User(username, password, role);
            userManager.addUser(newUser);
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Invalid role. Please enter 'student' or 'lecturer'.");
        }
    }

    private static void createAppointment() {
        try {
            System.out.print("Enter student name: ");
            String studentName = scanner.nextLine();
            System.out.print("Enter lecturer name: ");
            String lecturerName = scanner.nextLine();
            System.out.print("Enter appointment date (YYYY-MM-DD HH:mm): ");
            String dateInput = scanner.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime appointmentTime = LocalDateTime.parse(dateInput, formatter);

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Appointment newAppointment = new Appointment(studentName, lecturerName, appointmentTime, description);
            appointmentManager.addAppointment(newAppointment);
            System.out.println("Appointment created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating appointment: " + e.getMessage());
        }
    }

    private static void viewAppointments() {
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        for (int i = 0; i < appointments.size(); i++) {
            System.out.println(i + ": " + appointments.get(i));
        }
    }

    private static void confirmAppointment() {
        viewAppointments();
        try {
            System.out.print("Enter appointment index to confirm: ");
            int index = scanner.nextInt();
            scanner.nextLine(); // 消费换行符

            List<Appointment> appointments = appointmentManager.getAllAppointments();
            if (index >= 0 && index < appointments.size()) {
                Appointment appointment = appointments.get(index);
                appointment.setStatus("Confirmed");
                System.out.println("Appointment confirmed.");
            } else {
                System.out.println("Invalid appointment index.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }
}
