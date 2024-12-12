import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// Main GUI Application
public class CPUSchedulingApp extends JFrame {
    private List<Process> processes = new ArrayList<>();
    private JTextField processIdField, arrivalTimeField, burstTimeField, priorityField;
    private JComboBox<String> algorithmComboBox;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JTextField randomProcessCountField;
    private Random random = new Random();

    // Process class to represent individual processes
    static class Process {
        String processId;
        int arrivalTime;
        int burstTime;
        int priority;
        int completionTime;
        int turnaroundTime;
        int waitingTime;

        public Process(String processId, int arrivalTime, int burstTime, int priority) {
            this.processId = processId;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
        }
    }

    public CPUSchedulingApp() {
        setTitle("CPU Scheduling Simulator");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Process Input"));

        processIdField = new JTextField();
        arrivalTimeField = new JTextField();
        burstTimeField = new JTextField();
        priorityField = new JTextField();
        randomProcessCountField = new JTextField("5");

        inputPanel.add(new JLabel("Process ID:"));
        inputPanel.add(processIdField);
        inputPanel.add(new JLabel("Arrival Time:"));
        inputPanel.add(arrivalTimeField);
        inputPanel.add(new JLabel("Burst Time:"));
        inputPanel.add(burstTimeField);
        inputPanel.add(new JLabel("Priority (Optional):"));
        inputPanel.add(priorityField);
        inputPanel.add(new JLabel("Random Process Count:"));
        inputPanel.add(randomProcessCountField);

        // Algorithm Selection
        algorithmComboBox = new JComboBox<>(new String[]{"FCFS", "Priority","RoundRobin","SJF"});
        inputPanel.add(new JLabel("Scheduling Algorithm:"));
        inputPanel.add(algorithmComboBox);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton addProcessButton = new JButton("Add Process");
        JButton scheduleButton = new JButton("Schedule");
        JButton clearButton = new JButton("Clear All");
        JButton generateRandomButton = new JButton("Generate Random Processes");

        addProcessButton.addActionListener(e -> addProcess());
        scheduleButton.addActionListener(e -> scheduleProcesses());
        clearButton.addActionListener(e -> clearAllProcesses());
        generateRandomButton.addActionListener(e -> generateRandomProcesses());

        buttonPanel.add(addProcessButton);
        buttonPanel.add(generateRandomButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(clearButton);

        // Table for displaying processes
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority", 
                                "Completion Time", "Turnaround Time", "Waiting Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(processTable);

        // Layout
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void generateRandomProcesses() {
        // Clear existing processes
        clearAllProcesses();

        // Get number of random processes to generate
        int processCount;
        try {
            processCount = Integer.parseInt(randomProcessCountField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid number of processes", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generate random processes
        for (int i = 0; i < processCount; i++) {
            String processId = "P" + (i + 1);
            int arrivalTime = random.nextInt(10); // Arrival time between 0-9
            int burstTime = random.nextInt(10) + 1; // Burst time between 1-10
            int priority = random.nextInt(5) + 1; // Priority between 1-5

            Process process = new Process(processId, arrivalTime, burstTime, priority);
            processes.add(process);

            // Add to table
            tableModel.addRow(new Object[]{
                processId, arrivalTime, burstTime, priority, 
                "-", "-", "-"
            });
        }
    }

    private void addProcess() {
        try {
            String processId = processIdField.getText();
            int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
            int burstTime = Integer.parseInt(burstTimeField.getText());
            int priority = priorityField.getText().isEmpty() ? 0 : Integer.parseInt(priorityField.getText());

            Process process = new Process(processId, arrivalTime, burstTime, priority);
            processes.add(process);

            // Add to table
            tableModel.addRow(new Object[]{
                processId, arrivalTime, burstTime, priority, 
                "-", "-", "-"
            });

            // Clear input fields
            processIdField.setText("");
            arrivalTimeField.setText("");
            burstTimeField.setText("");
            priorityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values for Arrival Time and Burst Time", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scheduleProcesses() {
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please add some processes first", 
                "No Processes", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Clear previous scheduling results
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 4; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt("-", i, j);
            }
        }

        List<Process> processesCopy = new ArrayList<>(processes);
        String selectedAlgo = (String) algorithmComboBox.getSelectedItem();
        
        switch (selectedAlgo) {
            case "FCFS":
                scheduleFCFS(processesCopy);
                break;
            case "Priority":
                schedulePriority(processesCopy);
                break;
            case "RoundRobin":
                scheduleRoundRobin(processesCopy, 2); // Time quantum of 2
                break;
            case "SJF":
                scheduleSJF(processesCopy);
                break;
        }

        // Update table with scheduling results
        for (int i = 0; i < processesCopy.size(); i++) {
            Process p = processesCopy.get(i);
            tableModel.setValueAt(p.completionTime, i, 4);
            tableModel.setValueAt(p.turnaroundTime, i, 5);
            tableModel.setValueAt(p.waitingTime, i, 6);
        }

        // Calculate and display overall statistics
        calculateAndDisplayStats(processesCopy);
    }

    private void calculateAndDisplayStats(List<Process> scheduledProcesses) {
        double avgTurnaroundTime = scheduledProcesses.stream()
            .mapToInt(p -> p.turnaroundTime)
            .average()
            .orElse(0.0);

        double avgWaitingTime = scheduledProcesses.stream()
            .mapToInt(p -> p.waitingTime)
            .average()
            .orElse(0.0);

        JOptionPane.showMessageDialog(this, 
            String.format("Average Turnaround Time: %.2f\nAverage Waiting Time: %.2f", 
                avgTurnaroundTime, avgWaitingTime), 
            "Scheduling Statistics", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearAllProcesses() {
        processes.clear();
        tableModel.setRowCount(0);
    }

    private void scheduleFCFS(List<Process> processes) {
        // Step 1: Sort processes by arrival time (First Come First Serve)
        processes.sort((p1, p2) -> p1.arrivalTime - p2.arrivalTime);
        
        // Step 2: Process each job one by one
        int currentTime = 0;  // Keep track of current time
        
        for (Process process : processes) {
            // Step 3: If there's a gap between current time and process arrival,
            // move current time forward to when the process arrives
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;
            }
            
            // Step 4: Calculate times for this process
            process.completionTime = currentTime + process.burstTime;  // When process finishes
            process.turnaroundTime = process.completionTime - process.arrivalTime;  // Total time in system
            process.waitingTime = process.turnaroundTime - process.burstTime;  // Time spent waiting
            
            // Step 5: Move time forward by the burst time of this process
            currentTime = process.completionTime;
        }
    }

    private void schedulePriority(List<Process> processes) {
        // Sort by arrival time and priority (lower priority number = higher priority)
        processes.sort(Comparator
            .<Process>comparingInt(p -> p.arrivalTime)
            .thenComparingInt(p -> p.priority));
        
        int currentTime = 0;
        for (Process p : processes) {
            currentTime = Math.max(currentTime, p.arrivalTime);
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            currentTime = p.completionTime;
        }
    }

    private void scheduleRoundRobin(List<Process> processes, int timeQuantum) {
        List<Process> queue = new ArrayList<>(processes);
        int currentTime = 0;
        
        while (!queue.isEmpty()) {
            Process p = queue.remove(0);
            if (p.burstTime > timeQuantum) {
                currentTime += timeQuantum;
                p.burstTime -= timeQuantum;
                queue.add(p);
            } else {
                currentTime += p.burstTime;
                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            }
        }
    }

    private void scheduleSJF(List<Process> processes) {
        // Sort by arrival time and burst time
        processes.sort(Comparator
            .<Process>comparingInt(p -> p.arrivalTime)
            .thenComparingInt(p -> p.burstTime));
        
        int currentTime = 0;
        for (Process p : processes) {
            currentTime = Math.max(currentTime, p.arrivalTime);
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            currentTime = p.completionTime;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CPUSchedulingApp().setVisible(true);
        });
    }
}