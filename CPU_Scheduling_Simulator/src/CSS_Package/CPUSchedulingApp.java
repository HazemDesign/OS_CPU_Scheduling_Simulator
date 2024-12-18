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
    private GanttChartPanel ganttChartPanel;
    private List<GanttChartBar> ganttChartData = new ArrayList<>();
    private JButton toggleGanttChartButton;

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

    // Gantt chart bar class
    static class GanttChartBar {
        String processId;
        int startTime;
        int endTime;
        Color color;

        public GanttChartBar(String processId, int startTime, int endTime, Color color) {
            this.processId = processId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.color = color;
        }
    }

    // Gantt chart panel class
    class GanttChartPanel extends JPanel {
        private static final int PADDING = 20;
        private static final int BAR_HEIGHT = 40;
        private static final int TIME_MARKER_HEIGHT = 20;
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (ganttChartData.isEmpty()) return;

            // Find the maximum end time
            int maxEndTime = ganttChartData.stream()
                    .mapToInt(bar -> bar.endTime)
                    .max()
                    .orElse(0);

            // Calculate scaling factor
            double timeScale = (double) (getWidth() - 2 * PADDING) / maxEndTime;

            // Draw the timeline
            g2d.setColor(Color.BLACK);
            int baselineY = getHeight() - PADDING - TIME_MARKER_HEIGHT;
            g2d.drawLine(PADDING, baselineY, getWidth() - PADDING, baselineY);

            // Draw time markers
            for (int t = 0; t <= maxEndTime; t++) {
                int x = PADDING + (int)(t * timeScale);
                g2d.drawLine(x, baselineY, x, baselineY + 5);
                g2d.drawString(String.valueOf(t), x - 3, baselineY + TIME_MARKER_HEIGHT);
            }

            // Draw process bars
            int y = PADDING;
            for (GanttChartBar bar : ganttChartData) {
                int x1 = PADDING + (int)(bar.startTime * timeScale);
                int x2 = PADDING + (int)(bar.endTime * timeScale);
                
                // Draw the bar
                g2d.setColor(bar.color);
                g2d.fillRect(x1, y, x2 - x1, BAR_HEIGHT);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x1, y, x2 - x1, BAR_HEIGHT);
                
                // Draw the process ID
                g2d.drawString(bar.processId, x1 + 5, y + BAR_HEIGHT/2 + 5);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 100);
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
        toggleGanttChartButton = new JButton("Hide Gantt Chart");

        addProcessButton.addActionListener(e -> addProcess());
        scheduleButton.addActionListener(e -> scheduleProcesses());
        clearButton.addActionListener(e -> clearAllProcesses());
        generateRandomButton.addActionListener(e -> generateRandomProcesses());
        toggleGanttChartButton.addActionListener(e -> toggleGanttChart());

        buttonPanel.add(addProcessButton);
        buttonPanel.add(generateRandomButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(toggleGanttChartButton);

        // Table for displaying processes
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority", 
                                "Completion Time", "Turnaround Time", "Waiting Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(processTable);

        // Create Gantt chart panel
        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        
        // Create a panel for the table and Gantt chart
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(ganttChartPanel, BorderLayout.SOUTH);

        // Layout
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
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

//Scheduling Algorithms//

//First Come First Serve
    private void scheduleFCFS(List<Process> processes) {
        // Clear previous Gantt chart data
        ganttChartData.clear();
        
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        Random colorRandom = new Random();

        for (Process process : processes) {
            int startTime = Math.max(currentTime, process.arrivalTime);
            
            // Create a Gantt chart bar for this process
            Color randomColor = new Color(
                colorRandom.nextFloat(),
                colorRandom.nextFloat(),
                colorRandom.nextFloat(),
                0.5f
            );
            ganttChartData.add(new GanttChartBar(
                process.processId,
                startTime,
                startTime + process.burstTime,
                randomColor
            ));
            
            process.completionTime = startTime + process.burstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;
            
            currentTime = process.completionTime;
        }
        
        // Repaint the Gantt chart
        ganttChartPanel.repaint();
    }

//Priority
    private void schedulePriority(List<Process> processes) {
        // Sort by arrival time and priority (lower priority number = higher priority)
        
    }

//Round Robin
    private void scheduleRoundRobin(List<Process> processes, int timeQuantum) {
       

    }

//Shortest Job First
    private void scheduleSJF(List<Process> processes) {
        // Sort by arrival time and burst time
        
    }

    private void toggleGanttChart() {
        boolean isVisible = ganttChartPanel.isVisible();
        ganttChartPanel.setVisible(!isVisible);
        toggleGanttChartButton.setText(isVisible ? "Show Gantt Chart" : "Hide Gantt Chart");
        
        // Revalidate and repaint to ensure proper layout update
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CPUSchedulingApp().setVisible(true);
        });
    }
}