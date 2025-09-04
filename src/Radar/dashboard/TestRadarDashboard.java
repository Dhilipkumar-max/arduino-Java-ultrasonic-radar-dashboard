package Radar.dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TestRadarDashboard {
    private static final int DETECTION_THRESHOLD = 50; // cm
    private static final Random random = new Random();
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("Starting Test Radar Dashboard...");
        System.out.println("This version generates simulated data for testing.");
        System.out.println("No Arduino connection required.");
        
        // Create time series for plotting
        TimeSeries distanceSeries = new TimeSeries("Distance (cm)");
        TimeSeries detectionSeries = new TimeSeries("Detected Objects");

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(distanceSeries);
        dataset.addSeries(detectionSeries);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Test Ultrasonic Sensor Dashboard (Simulated Data)",
                "Time",
                "Distance (cm)",
                dataset,
                true,
                true,
                false
        );

        // Setup UI
        JFrame frame = new JFrame("Test Radar Dashboard");
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
        JLabel infoLabel = new JLabel("Simulated Data - No Arduino Required | Detection Threshold: " + DETECTION_THRESHOLD + " cm");
        infoLabel.setForeground(Color.BLUE);
        infoPanel.add(infoLabel);
        frame.add(infoPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);

        // Generate simulated data in background
        new Thread(() -> {
            int baseDistance = 100;
            int direction = 1;
            
            while (running) {
                try {
                    // Simulate realistic distance changes
                    int change = random.nextInt(20) - 10; // -10 to +10
                    baseDistance += change;
                    
                    // Keep distance in reasonable range
                    if (baseDistance > 300) {
                        baseDistance = 300;
                        direction = -1;
                    } else if (baseDistance < 20) {
                        baseDistance = 20;
                        direction = 1;
                    }
                    
                    // Add some random variation
                    int distance = baseDistance + random.nextInt(15) - 7;
                    if (distance < 2) distance = 2;
                    if (distance > 400) distance = 400;

                    // Add distance data to chart
                    distanceSeries.addOrUpdate(new Millisecond(), distance);

                    // Mark detections
                    if (distance < DETECTION_THRESHOLD) {
                        detectionSeries.addOrUpdate(new Millisecond(), distance);
                        System.out.println("Object detected at distance " + distance + "cm");
                    }

                    // Update every 100ms
                    Thread.sleep(100);
                    
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
            System.out.println("Shutting down Test Radar Dashboard...");
        }));
        
        System.out.println("Test Radar Dashboard is running. Close the window to stop.");
    }
}
