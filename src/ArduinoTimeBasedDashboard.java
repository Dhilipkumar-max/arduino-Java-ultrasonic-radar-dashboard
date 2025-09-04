import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.fazecast.jSerialComm.SerialPort;

public class ArduinoTimeBasedDashboard {
    private static final int DETECTION_THRESHOLD = 50; // cm
    private static volatile boolean running = false;
    private static volatile boolean arduinoConnected = false;
    
    private List<DataPoint> dataHistory = new ArrayList<>();
    private JLabel timeLabel;
    private JLabel distanceLabel;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JLabel connectionLabel;
    private GraphPanel graphPanel;
    private JFrame frame;
    private int dataCount = 0;
    private SerialPort port;
    
    private static class DataPoint {
        long timestamp;
        int distance;
        boolean detected;
        
        DataPoint(long timestamp, int distance, boolean detected) {
            this.timestamp = timestamp;
            this.distance = distance;
            this.detected = detected;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ArduinoTimeBasedDashboard dashboard = new ArduinoTimeBasedDashboard();
            dashboard.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Arduino Time-Based Radar Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        JButton connectButton = new JButton("🔌 Connect Arduino");
        connectButton.setFont(new Font("Arial", Font.BOLD, 14));
        connectButton.setBackground(new Color(34, 139, 34));
        connectButton.setForeground(Color.WHITE);
        connectButton.addActionListener(e -> connectArduino());
        
        JButton disconnectButton = new JButton("🔌 Disconnect Arduino");
        disconnectButton.setFont(new Font("Arial", Font.BOLD, 14));
        disconnectButton.setBackground(new Color(220, 20, 60));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.addActionListener(e -> disconnectArduino());
        
        JButton startButton = new JButton("▶ Start Data Collection");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(0, 100, 200));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> startDataCollection());
        
        JButton stopButton = new JButton("⏹ Stop Data Collection");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(220, 20, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.addActionListener(e -> stopDataCollection());
        
        JButton clearButton = new JButton("🗑 Clear Data");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(255, 140, 0));
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearData());
        
        controlPanel.add(connectButton);
        controlPanel.add(disconnectButton);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        infoPanel.setBackground(new Color(250, 250, 250));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Live Information"));
        
        connectionLabel = new JLabel("Arduino: Disconnected");
        connectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        connectionLabel.setForeground(Color.RED);
        
        timeLabel = new JLabel("Time: --:--:--");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(Color.BLUE);
        
        distanceLabel = new JLabel("Distance: -- cm");
        distanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        distanceLabel.setForeground(Color.GREEN);
        
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.BLUE);
        
        countLabel = new JLabel("Data Points: 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(new Color(128, 0, 128));
        
        JLabel portLabel = new JLabel("Port: None");
        portLabel.setFont(new Font("Arial", Font.BOLD, 14));
        portLabel.setForeground(Color.GRAY);
        
        infoPanel.add(connectionLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(distanceLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(countLabel);
        infoPanel.add(portLabel);
        
        // Create graph panel
        graphPanel = new GraphPanel();
        graphPanel.setBorder(BorderFactory.createTitledBorder("Distance Over Time"));
        
        // Layout
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(graphPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        System.out.println("Arduino Time-Based Radar Dashboard is running!");
        System.out.println("1. Click 'Connect Arduino' to connect to your Arduino");
        System.out.println("2. Click 'Start Data Collection' to begin reading data");
    }
    
    private void connectArduino() {
        // First, close any existing connection
        if (port != null && port.isOpen()) {
            port.closePort();
        }
        
        // Try different COM ports
        String[] ports = {"COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10"};
        
        for (String portName : ports) {
            try {
                port = SerialPort.getCommPort(portName);
                port.setBaudRate(9600);
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                
                if (port.openPort()) {
                    // Test if we can actually read from the port
                    Thread.sleep(100); // Wait a bit
                    if (port.isOpen()) {
                        arduinoConnected = true;
                        connectionLabel.setText("Arduino: Connected to " + portName);
                        connectionLabel.setForeground(new Color(34, 139, 34));
                        statusLabel.setText("Status: Arduino Connected");
                        statusLabel.setForeground(new Color(34, 139, 34));
                        System.out.println("Connected to Arduino on " + portName);
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to connect to " + portName + ": " + e.getMessage());
            }
        }
        
        // If no port worked
        JOptionPane.showMessageDialog(frame, 
            "Could not connect to Arduino.\n" +
            "Please check:\n" +
            "1. Arduino is connected via USB\n" +
            "2. Arduino Serial Monitor is closed\n" +
            "3. Arduino is sending data in format: degree,distance,timestamp", 
            "Connection Failed", 
            JOptionPane.ERROR_MESSAGE);
        
        connectionLabel.setText("Arduino: Connection Failed");
        connectionLabel.setForeground(Color.RED);
    }
    
    private void disconnectArduino() {
        if (port != null && port.isOpen()) {
            port.closePort();
            arduinoConnected = false;
            connectionLabel.setText("Arduino: Disconnected");
            connectionLabel.setForeground(Color.RED);
            statusLabel.setText("Status: Arduino Disconnected");
            statusLabel.setForeground(Color.RED);
            System.out.println("Arduino disconnected");
        }
    }
    
    private void startDataCollection() {
        if (!arduinoConnected) {
            JOptionPane.showMessageDialog(frame, 
                "Please connect to Arduino first!", 
                "No Connection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!running) {
            running = true;
            new Thread(this::readArduinoData).start();
            statusLabel.setText("Status: Reading Arduino Data");
            statusLabel.setForeground(new Color(34, 139, 34));
        }
    }
    
    private void stopDataCollection() {
        running = false;
        statusLabel.setText("Status: Stopped");
        statusLabel.setForeground(Color.RED);
    }
    
    private void clearData() {
        dataHistory.clear();
        dataCount = 0;
        countLabel.setText("Data Points: 0");
        graphPanel.repaint();
        statusLabel.setText("Status: Data Cleared");
        statusLabel.setForeground(Color.ORANGE);
    }
    
    private void readArduinoData() {
        try {
            InputStream in = port.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            
            while (running && arduinoConnected) {
                try {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        System.out.println("Received: '" + line + "'");
                        
                        // Parse Arduino output format: "degree,distance,timestamp"
                        if (line.contains(",") && line.split(",").length >= 2) {
                            String[] parts = line.split(",");
                            if (parts.length >= 2) {
                                int degree = Integer.parseInt(parts[0].trim());
                                int distance = Integer.parseInt(parts[1].trim());
                                
                                // Create data point
                                long timestamp = System.currentTimeMillis();
                                boolean detected = distance < DETECTION_THRESHOLD;
                                DataPoint point = new DataPoint(timestamp, distance, detected);
                                
                                // Update UI on EDT
                                SwingUtilities.invokeLater(() -> {
                                    updateUI(point);
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore parse errors, continue reading
                    System.out.println("Parse error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading Arduino data: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Status: Read Error");
                statusLabel.setForeground(Color.RED);
            });
        }
    }
    
    private void updateUI(DataPoint point) {
        // Add to history
        dataHistory.add(point);
        dataCount++;
        
        // Keep only last 100 points
        if (dataHistory.size() > 100) {
            dataHistory.remove(0);
        }
        
        // Update labels
        timeLabel.setText("Time: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(point.timestamp)));
        distanceLabel.setText("Distance: " + point.distance + " cm");
        countLabel.setText("Data Points: " + dataCount);
        
        if (point.detected) {
            statusLabel.setText("Status: OBJECT DETECTED!");
            statusLabel.setForeground(Color.RED);
            System.out.println("Object detected at distance " + point.distance + "cm");
        } else {
            statusLabel.setText("Status: Reading Arduino Data");
            statusLabel.setForeground(new Color(34, 139, 34));
        }
        
        // Repaint graph
        graphPanel.repaint();
    }
    
    // Custom graph panel
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
            
            // X-axis labels (Time)
            if (dataHistory.size() > 1) {
                long minTime = dataHistory.get(0).timestamp;
                long maxTime = dataHistory.get(dataHistory.size() - 1).timestamp;
                for (int i = 0; i <= 6; i++) {
                    int x = padding + (i * graphWidth) / 6;
                    long time = minTime + (i * (maxTime - minTime)) / 6;
                    String timeStr = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(time));
                    g2d.drawString(timeStr, x - 20, height - padding + 20);
                }
            }
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(new Color(70, 70, 70));
            g2d.drawString("Arduino Distance Over Time", width/2 - 100, padding - 10);
            
            // Draw data
            if (dataHistory.size() > 1) {
                // Draw distance line
                g2d.setColor(new Color(30, 144, 255));
                g2d.setStroke(new BasicStroke(3));
                
                for (int i = 1; i < dataHistory.size(); i++) {
                    DataPoint prev = dataHistory.get(i - 1);
                    DataPoint curr = dataHistory.get(i);
                    
                    int x1 = padding + ((i - 1) * graphWidth) / Math.max(1, dataHistory.size() - 1);
                    int y1 = padding + graphHeight - (prev.distance * graphHeight) / 400;
                    int x2 = padding + (i * graphWidth) / Math.max(1, dataHistory.size() - 1);
                    int y2 = padding + graphHeight - (curr.distance * graphHeight) / 400;
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
                
                // Draw detection points
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < dataHistory.size(); i++) {
                    DataPoint point = dataHistory.get(i);
                    if (point.detected) {
                        int x = padding + (i * graphWidth) / Math.max(1, dataHistory.size() - 1);
                        int y = padding + graphHeight - (point.distance * graphHeight) / 400;
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
            return new Dimension(1100, 400);
        }
    }
}
