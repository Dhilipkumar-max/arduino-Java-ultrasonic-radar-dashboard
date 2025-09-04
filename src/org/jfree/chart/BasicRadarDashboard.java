package radar.dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BasicRadarDashboard {
    private static final int DETECTION_THRESHOLD = 50; // cm
    private static final Random random = new Random();
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("Starting Basic Radar Dashboard...");
        System.out.println("This version uses minimal JFreeChart functionality.");
        
        // Create simple category dataset for plotting
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Basic Ultrasonic Sensor Dashboard",
                "Time (samples)",
                "Distance (cm)",
                dataset
        );

        // Setup UI
        JFrame frame = new JFrame("Basic Radar Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        
        // Add chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(950, 600));
        frame.add(chartPanel);
        
        // Add info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        JLabel infoLabel = new JLabel("Basic Chart - Minimal JFreeChart | Detection Threshold: " + DETECTION_THRESHOLD + " cm");
        infoLabel.setForeground(Color.BLUE);
        infoPanel.add(infoLabel);
        frame.add(infoPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);

        // Generate simulated data in background
        new Thread(() -> {
            int sampleCount = 0;
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
                    int distance = baseDistance + random.nextInt(15) - 7;
                    if (distance < 2) distance = 2;
                    if (distance > 400) distance = 400;

                    // Add distance data to chart
                    dataset.addValue(distance, "Distance", String.valueOf(sampleCount));

                    // Mark detections
                    if (distance < DETECTION_THRESHOLD) {
                        dataset.addValue(distance, "Detected Objects", String.valueOf(sampleCount));
                        System.out.println("Object detected at distance " + distance + "cm");
                    }

                    sampleCount++;
                    
                    // Update every 200ms
                    Thread.sleep(200);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error in data generation: " + e.getMessage());
                }
            }
        }).start();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            System.out.println("Shutting down Basic Radar Dashboard...");
        }));
        
        System.out.println("Basic Radar Dashboard is running. Close the window to stop.");
    }
}
