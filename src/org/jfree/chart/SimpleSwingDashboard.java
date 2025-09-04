package radar.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleSwingDashboard {
    private static final int DETECTION_THRESHOLD = 50; // cm
    private static final Random random = new Random();
    private static volatile boolean running = true;
    
    private List<Integer> distanceHistory = new ArrayList<>();
    private List<Integer> detectionHistory = new ArrayList<>();
    private JLabel distanceLabel;
    private JLabel statusLabel;
    private GraphPanel graphPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleSwingDashboard dashboard = new SimpleSwingDashboard();
            dashboard.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        System.out.println("Starting Simple Swing Radar Dashboard...");
        System.out.println("This version uses only Java Swing - no external libraries required.");
        
        // Create main frame
        JFrame frame = new JFrame("Simple Swing Radar Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create graph panel
        graphPanel = new GraphPanel();
        mainPanel.add(graphPanel, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        distanceLabel = new JLabel("Distance: -- cm");
        distanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(distanceLabel);
        
        statusLabel = new JLabel("Status: No objects detected");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.BLUE);
        controlPanel.add(statusLabel);
        
        JButton startButton = new JButton("Start Simulation");
        JButton stopButton = new JButton("Stop Simulation");
        
        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        System.out.println("Simple Swing Radar Dashboard is running. Close the window to stop.");
    }
    
    private void startSimulation() {
        if (!running) {
            running = true;
            new Thread(this::simulateData).start();
        }
    }
    
    private void stopSimulation() {
        running = false;
    }
    
    private void simulateData() {
        int baseDistance = 100;
        
        while (running) {
            try {
                // Simulate realistic distance changes
                int change = random.nextInt(20) - 10; // -10 to +10
                baseDistance += change;
                
                // Keep distance in reasonable range
                if (baseDistance > 300) baseDistance = 300;
                if (baseDistance < 20) baseDistance = 20;
                
                // Add some random variation
                int rawDistance = baseDistance + random.nextInt(15) - 7;
                final int distance = Math.max(2, Math.min(400, rawDistance));

                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    updateDistance(distance);
                    updateGraph(distance);
                });

                // Update every 200ms
                Thread.sleep(200);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in data generation: " + e.getMessage());
            }
        }
    }
    
    private void updateDistance(int distance) {
        distanceLabel.setText("Distance: " + distance + " cm");
        
        if (distance < DETECTION_THRESHOLD) {
            statusLabel.setText("Status: OBJECT DETECTED at " + distance + " cm!");
            statusLabel.setForeground(Color.RED);
            System.out.println("Object detected at distance " + distance + "cm");
        } else {
            statusLabel.setText("Status: No objects detected");
            statusLabel.setForeground(Color.BLUE);
        }
    }
    
    private void updateGraph(int distance) {
        distanceHistory.add(distance);
        if (distance < DETECTION_THRESHOLD) {
            detectionHistory.add(distance);
        } else {
            detectionHistory.add(null);
        }
        
        // Keep only last 100 points
        if (distanceHistory.size() > 100) {
            distanceHistory.remove(0);
            detectionHistory.remove(0);
        }
        
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
            int padding = 50;
            int graphWidth = width - 2 * padding;
            int graphHeight = height - 2 * padding;
            
            // Draw background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Draw grid
            g2d.setColor(Color.LIGHT_GRAY);
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
            
            // Draw labels
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Distance (cm)", 10, height / 2);
            g2d.drawString("Time (samples)", width / 2, height - 10);
            
            // Draw distance line
            if (distanceHistory.size() > 1) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2));
                
                for (int i = 1; i < distanceHistory.size(); i++) {
                    int x1 = padding + ((i - 1) * graphWidth) / Math.max(1, distanceHistory.size() - 1);
                    int y1 = padding + graphHeight - (distanceHistory.get(i - 1) * graphHeight) / 400;
                    int x2 = padding + (i * graphWidth) / Math.max(1, distanceHistory.size() - 1);
                    int y2 = padding + graphHeight - (distanceHistory.get(i) * graphHeight) / 400;
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
            
            // Draw detection points
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i < detectionHistory.size(); i++) {
                Integer detection = detectionHistory.get(i);
                if (detection != null) {
                    int x = padding + (i * graphWidth) / Math.max(1, detectionHistory.size() - 1);
                    int y = padding + graphHeight - (detection * graphHeight) / 400;
                    g2d.fillOval(x - 3, y - 3, 6, 6);
                }
            }
        }
    }
}
