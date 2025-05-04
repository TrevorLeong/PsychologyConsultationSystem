import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;

public class PsychologyConsultationSystem {
    private final static UserManager userManager = UserManager.getInstance();
    private static final AppointmentManager appointmentManager = new AppointmentManager();

    public static void main(String[] args) {
        loadData();
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));

        // 添加窗口关闭时的保存操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveAllData();
        }));
    }

    private static void loadData() {
        // 加载用户数据
        try {
            List<User> savedUsers = FileManager.loadUsers();
            for (User user : savedUsers) {
                userManager.addUser(user);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Loading default users...");
            preloadUsers();
        }

        // 加载预约数据
        try {
            List<Appointment> savedAppointments = FileManager.loadAppointments();
            for (Appointment appointment : savedAppointments) {
                appointmentManager.addAppointment(appointment);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved appointments found.");
        }
    }

    private static void preloadUsers() {
        userManager.addUser(new User("student1", "password1", "student"));
        userManager.addUser(new User("student2", "password2", "student"));
        userManager.addUser(new User("lecturer1", "password1", "lecturer"));
        userManager.addUser(new User("lecturer2", "password2", "lecturer"));
        try {
            FileManager.saveUsers(userManager.getAllUsers());
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static UserManager getUserManager() {
        return userManager;
    }

    public static AppointmentManager getAppointmentManager() {
        return appointmentManager;
    }

    public static void saveAllData() {
        try {
            FileManager.saveUsers(userManager.getAllUsers());
            FileManager.saveAppointments(appointmentManager.getAllAppointments());
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

static class LoginPage extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public LoginPage() {
        setTitle("Psychology Consultation System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(5, 1));

        // Username
        JPanel usernamePanel = new JPanel(new FlowLayout());
        usernamePanel.add(new JLabel("Username: "));
        usernameField = new JTextField(20);
        usernamePanel.add(usernameField);
        add(usernamePanel);

        // Password
        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordPanel.add(new JLabel("Password: "));
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);
        add(passwordPanel);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginActionListener());
        add(loginButton);

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = PsychologyConsultationSystem.getUserManager().findUser(username, password);
            if (user != null) {
                statusLabel.setText("Login successful! Welcome " + user.getRole());
                dispose();
                if (user.getRole().equals("student")) {
                    SwingUtilities.invokeLater(() -> new StudentDashboard(user.getUsername()).setVisible(true));
                } else {
                    SwingUtilities.invokeLater(() -> new LecturerDashboard(user.getUsername()).setVisible(true));
                }
            } else {
                statusLabel.setText("Invalid username or password. Try again.");
            }
        }
    }
}

static class StudentDashboard extends JFrame {
    private final AppointmentManager appointmentManager;
    private final UserManager userManager;
    private final String currentUsername;

    public StudentDashboard(String username) {
        this.currentUsername = username;
        setTitle("Student Dashboard - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        appointmentManager = PsychologyConsultationSystem.getAppointmentManager();
        userManager = PsychologyConsultationSystem.getUserManager();

        // Create main panel with buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton createAppointmentBtn = new JButton("Create Appointment");
        JButton viewAppointmentsBtn = new JButton("View Appointments");
        JButton cancelAppointmentBtn = new JButton("Cancel Appointment");
        JButton logoutBtn = new JButton("Logout");

        createAppointmentBtn.addActionListener(e -> createAppointment());
        viewAppointmentsBtn.addActionListener(e-> viewAppointments());
        cancelAppointmentBtn.addActionListener(e -> cancelAppointment());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(createAppointmentBtn);
        buttonPanel.add(viewAppointmentsBtn);
        buttonPanel.add(cancelAppointmentBtn);
        buttonPanel.add(logoutBtn);

        add(new JLabel("Welcome, " + currentUsername + "!", SwingConstants.CENTER), BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void createAppointment() {
        try {
            // Get lecturer list
            List<User> lecturers = userManager.getUsersByRole("lecturer");
            if (lecturers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No lecturers available.");
                return;
            }

            // Create lecturer selection dialog
            String[] lecturerNames = lecturers.stream()
                    .map(User::getUsername)
                    .toArray(String[]::new);
            String lecturerName = (String) JOptionPane.showInputDialog(this,
                    "Select Lecturer:",
                    "Create Appointment",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    lecturerNames,
                    lecturerNames[0]);

            if (lecturerName == null) return;

            String dateTime = JOptionPane.showInputDialog(this,
                    "Enter appointment date (YYYY-MM-DD HH:mm):",
                    "Create Appointment",
                    JOptionPane.QUESTION_MESSAGE);
            if (dateTime == null) return;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, formatter);

            String description = JOptionPane.showInputDialog(this,
                    "Enter appointment description:",
                    "Create Appointment",
                    JOptionPane.QUESTION_MESSAGE);
            if (description == null) return;

            Appointment newAppointment = new Appointment(currentUsername, lecturerName, appointmentTime, description);
            appointmentManager.addAppointment(newAppointment);
            JOptionPane.showMessageDialog(this, "Appointment created successfully!");
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use YYYY-MM-DD HH:mm format.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid appointment details: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all appointment details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
    }

    private void viewAppointments() {
        List<Appointment> userAppointments = appointmentManager.getAllAppointments().stream()
                .filter(a -> a.getStudentName().equals(currentUsername))
                .toList();

        if (userAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no appointments.");
            return;
        }

        StringBuilder sb = new StringBuilder("Your Appointments:\n\n");
        for (int i = 0; i < userAppointments.size(); i++) {
            sb.append(i).append(". ").append(userAppointments.get(i).toString()).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Your Appointments", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelAppointment() {
        List<Appointment> userAppointments = appointmentManager.getAllAppointments().stream()
                .filter(a -> a.getStudentName().equals(currentUsername))
                .toList();

        if (userAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no appointments to cancel.");
            return;
        }

        String[] appointmentStrings = userAppointments.stream()
                .map(Appointment::toString)
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select appointment to cancel:",
                "Cancel Appointment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                appointmentStrings,
                appointmentStrings[0]);

        if (selected != null) {
            int index = -1;
            for (int i = 0; i < appointmentStrings.length; i++) {
                if (appointmentStrings[i].equals(selected)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                appointmentManager.getAllAppointments().remove(userAppointments.get(index));
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
            }
        }
    }

    private void logout() {
        PsychologyConsultationSystem.saveAllData();
        dispose();
        new LoginPage().setVisible(true);
    }
}

static class LecturerDashboard extends JFrame {
    private final AppointmentManager appointmentManager;
    private final String currentUsername;

    public LecturerDashboard(String username) {
        this.currentUsername = username;
        setTitle("Lecturer Dashboard - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        appointmentManager = PsychologyConsultationSystem.getAppointmentManager();

        // Create main panel with buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewAppointmentsBtn = new JButton("View Appointments");
        JButton confirmAppointmentBtn = new JButton("Confirm Appointment");
        JButton logoutBtn = new JButton("Logout");

        viewAppointmentsBtn.addActionListener(e -> viewAppointments());
        confirmAppointmentBtn.addActionListener(e -> confirmAppointment());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(viewAppointmentsBtn);
        buttonPanel.add(confirmAppointmentBtn);
        buttonPanel.add(logoutBtn);

        add(new JLabel("Welcome, " + currentUsername + "!", SwingConstants.CENTER), BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void viewAppointments() {
        List<Appointment> lecturerAppointments = appointmentManager.getAllAppointments().stream()
                .filter(a -> a.getLecturerName().equals(currentUsername))
                .toList();

        if (lecturerAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no appointments.");
            return;
        }

        StringBuilder sb = new StringBuilder("Your Appointments:\n\n");
        for (int i = 0; i < lecturerAppointments.size(); i++) {
            sb.append(i).append(". ").append(lecturerAppointments.get(i).toString()).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Your Appointments", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmAppointment() {
        List<Appointment> lecturerAppointments = appointmentManager.getAllAppointments().stream()
                .filter(a -> a.getLecturerName().equals(currentUsername) && !a.isConfirmed())
                .toList();

        if (lecturerAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments to confirm.");
            return;
        }

        String[] appointmentStrings = lecturerAppointments.stream()
                .map(Appointment::toString)
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select appointment to confirm:",
                "Confirm Appointment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                appointmentStrings,
                appointmentStrings[0]);

        if (selected != null) {
            int index = -1;
            for (int i = 0; i < appointmentStrings.length; i++) {
                if (appointmentStrings[i].equals(selected)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                Appointment appointment = lecturerAppointments.get(index);
                appointment.setConfirmed(true);  // 假设Appointment类中有setConfirmed方法
                JOptionPane.showMessageDialog(this, "Appointment confirmed successfully!");
            }
        }
    }

        private void logout() {
            PsychologyConsultationSystem.saveAllData();
            dispose();
            new LoginPage().setVisible(true);
        }
    }
}