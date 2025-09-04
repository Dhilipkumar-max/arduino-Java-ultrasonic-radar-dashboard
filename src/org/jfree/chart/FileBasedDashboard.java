package radar.dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class FileBasedDashboard {
    private static final int DETECTION_THRESHOLD = 50;
    private List<Integer> distanceHistory = new ArrayList<>();
    private List<Integer> degreeHistory = new ArrayList<>();
    private JLabel statusLabel;
    private JLabel fileInfoLabel;
    private GraphPanel graphPanel;
    private JFrame frame;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileBasedDashboard dashboard = new FileBasedDashboard();
            dashboard.createAndShowGUI();
        });
    }
    
    private void createAndShowGUI() {
        frame = new JFrame("File-Based Radar Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create top control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        JButton loadButton = new JButton("📁 Load Radar Data File");
        loadButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadButton.setBackground(new Color(70, 130, 180));
        loadButton.setForeground(Color.WHITE);
        loadButton.addActionListener(e -> loadDataFile());
        
        JButton specificFileButton = new JButton("📊 Load Specific File");
        specificFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        specificFileButton.setBackground(new Color(255, 140, 0));
        specificFileButton.setForeground(Color.WHITE);
        specificFileButton.addActionListener(e -> loadSpecificFile());
        
        JButton demoButton = new JButton("🎯 Load Demo Data");
        demoButton.setFont(new Font("Arial", Font.BOLD, 14));
        demoButton.setBackground(new Color(34, 139, 34));
        demoButton.setForeground(Color.WHITE);
        demoButton.addActionListener(e -> loadDemoData());
        
        controlPanel.add(loadButton);
        controlPanel.add(specificFileButton);
        controlPanel.add(demoButton);
        
        // Create info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(250, 250, 250));
        infoPanel.setBorder(BorderFactory.createTitledBorder("File Information"));
        
        fileInfoLabel = new JLabel("No file loaded");
        fileInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(fileInfoLabel);
        
        statusLabel = new JLabel("Status: Ready to load data");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(Color.BLUE);
        infoPanel.add(statusLabel);
        
        // Create graph panel
        graphPanel = new GraphPanel();
        graphPanel.setBorder(BorderFactory.createTitledBorder("Radar Data Visualization"));
        
        // Layout
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(graphPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        // Auto-load the specific file on startup
        loadSpecificFile();
        
        System.out.println("File-Based Radar Dashboard is running!");
        System.out.println("Click 'Load Radar Data File' to load a CSV file, or 'Load Demo Data' for testing.");
    }
    
    private void loadDataFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV and Excel files", "csv", "xlsx"));
        fileChooser.setDialogTitle("Select Radar Data File");
        
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            loadDataFromFile(file);
        }
    }
    
    private void loadSpecificFile() {
        String filePath = "C:\\Users\\Dilip Kumar\\OneDrive\\Documents\\radar_data_20250831_101524.csv.xlsx";
        File file = new File(filePath);
        
        if (file.exists()) {
            loadDataFromFile(file);
        } else {
            // Try without .xlsx extension (in case it's actually a CSV)
            String csvPath = "C:\\Users\\Dilip Kumar\\OneDrive\\Documents\\radar_data_20250831_101524.csv";
            File csvFile = new File(csvPath);
            
            if (csvFile.exists()) {
                loadDataFromFile(csvFile);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "File not found: " + filePath + "\nAlso tried: " + csvPath, 
                    "File Not Found", 
                    JOptionPane.WARNING_MESSAGE);
                statusLabel.setText("Status: Specific file not found");
                statusLabel.setForeground(Color.RED);
            }
        }
    }
    
    private void loadDemoData() {
        // Generate realistic demo data
        distanceHistory.clear();
        degreeHistory.clear();
        
        Random random = new Random();
        int baseDistance = 100;
        
        // Generate data for degrees 15 to 165
        for (int degree = 15; degree <= 165; degree++) {
            // Simulate realistic distance changes
            int change = random.nextInt(20) - 10;
            baseDistance += change;
            
            // Keep distance in reasonable range
            if (baseDistance > 300) baseDistance = 300;
            if (baseDistance < 20) baseDistance = 20;
            
            // Add some random variation
            int distance = baseDistance + random.nextInt(15) - 7;
            distance = Math.max(2, Math.min(400, distance));
            
            degreeHistory.add(degree);
            distanceHistory.add(distance);
        }
        
        // Generate return sweep data (165 to 15)
        for (int degree = 165; degree >= 15; degree--) {
            int change = random.nextInt(20) - 10;
            baseDistance += change;
            
            if (baseDistance > 300) baseDistance = 300;
            if (baseDistance < 20) baseDistance = 20;
            
            int distance = baseDistance + random.nextInt(15) - 7;
            distance = Math.max(2, Math.min(400, distance));
            
            degreeHistory.add(degree);
            distanceHistory.add(distance);
        }
        
        fileInfoLabel.setText("Demo Data: " + distanceHistory.size() + " points generated");
        statusLabel.setText("Status: Demo data loaded successfully");
        statusLabel.setForeground(new Color(34, 139, 34));
        graphPanel.repaint();
        
        System.out.println("Demo data loaded: " + distanceHistory.size() + " data points");
    }
    
    private void loadDataFromFile(File file) {
        try {
            distanceHistory.clear();
            degreeHistory.clear();
            
            String fileName = file.getName().toLowerCase();
            
            if (fileName.endsWith(".xlsx")) {
                loadExcelFile(file);
            } else {
                loadCsvFile(file);
            }
            
            fileInfoLabel.setText("File: " + file.getName() + " | " + distanceHistory.size() + " data points");
            statusLabel.setText("Status: Data loaded successfully from " + file.getName());
            statusLabel.setForeground(new Color(34, 139, 34));
            graphPanel.repaint();
            
            System.out.println("Data loaded from " + file.getName() + ": " + distanceHistory.size() + " data points");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, 
                "Error loading file: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Status: Error loading file");
            statusLabel.setForeground(Color.RED);
            e.printStackTrace();
        }
    }
    
    private void loadCsvFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        int degree = Integer.parseInt(parts[0].trim());
                        int distance = Integer.parseInt(parts[1].trim());
                        
                        degreeHistory.add(degree);
                        distanceHistory.add(distance);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid line " + lineCount + ": " + line);
                    }
                }
            }
        }
    }
    
    private void loadExcelFile(File file) throws Exception {
        // For Excel files, we'll show a message that Excel support needs additional libraries
        // For now, we'll try to read it as a CSV if it's actually a CSV file with .xlsx extension
        JOptionPane.showMessageDialog(frame, 
            "Excel (.xlsx) files require additional libraries (Apache POI).\n" +
            "Please save your file as CSV format or use the file chooser to select a CSV file.", 
            "Excel Support", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Try to read as CSV anyway (in case it's actually a CSV with wrong extension)
        try {
            loadCsvFile(file);
        } catch (Exception e) {
            throw new Exception("Cannot read Excel file. Please convert to CSV format.");
        }
    }
    
    // Enhanced graph panel with better visualization
    private class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int padding = 60;
            int graphWidth = width - 2 * padding;
            int graphHeight = height - 2 * padding;
            
            // Draw background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Draw grid
            g2d.setColor(new Color(230, 230, 230));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i <= 10; i++) {
                int x = padding + (i * graphWidth) / 10;
                g2d.drawLine(x, padding, x, height - padding);
                
                int y = padding + (i * graphHeight) / 10;
                g2d.drawLine(padding, y, width - padding, y);
            }
            
            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(padding, padding, padding, height - padding); // Y-axis
            g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
            
            // Draw axis labels
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(Color.BLACK);
            
            // Y-axis labels (Distance)
            for (int i = 0; i <= 8; i++) {
                int y = padding + (i * graphHeight) / 8;
                int distance = 400 - (i * 400) / 8;
                g2d.drawString(distance + "cm", 5, y + 5);
            }
            
            // X-axis labels (Degree)
            if (degreeHistory.size() > 0) {
                int minDegree = Collections.min(degreeHistory);
                int maxDegree = Collections.max(degreeHistory);
                for (int i = 0; i <= 6; i++) {
                    int x = padding + (i * graphWidth) / 6;
                    int degree = minDegree + (i * (maxDegree - minDegree)) / 6;
                    g2d.drawString(degree + "°", x - 10, height - padding + 20);
                }
            }
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(new Color(70, 70, 70));
            g2d.drawString("Radar Distance vs Angle", width/2 - 80, padding - 10);
            
            // Draw data
            if (distanceHistory.size() > 1) {
                // Draw distance line
                g2d.setColor(new Color(30, 144, 255));
                g2d.setStroke(new BasicStroke(3));
                
                for (int i = 1; i < distanceHistory.size(); i++) {
                    int x1 = padding + ((i - 1) * graphWidth) / Math.max(1, distanceHistory.size() - 1);
                    int y1 = padding + graphHeight - (distanceHistory.get(i - 1) * graphHeight) / 400;
                    int x2 = padding + (i * graphWidth) / Math.max(1, distanceHistory.size() - 1);
                    int y2 = padding + graphHeight - (distanceHistory.get(i) * graphHeight) / 400;
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
                
                // Draw detection points
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < distanceHistory.size(); i++) {
                    int distance = distanceHistory.get(i);
                    if (distance < DETECTION_THRESHOLD) {
                        int x = padding + (i * graphWidth) / Math.max(1, distanceHistory.size() - 1);
                        int y = padding + graphHeight - (distance * graphHeight) / 400;
                        g2d.fillOval(x - 4, y - 4, 8, 8);
                    }
                }
                
                // Draw legend
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(new Color(30, 144, 255));
                g2d.drawString("Distance Line", width - 120, padding + 20);
                g2d.setColor(Color.RED);
                g2d.drawString("Object Detection (<50cm)", width - 120, padding + 35);
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1100, 600);
        }
    }
}
