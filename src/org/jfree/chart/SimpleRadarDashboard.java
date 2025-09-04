package org.jfree.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SimpleRadarDashboard {
    private static final int DETECTION_THRESHOLD = 50; // cm
    private static final Random random = new Random();
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("Starting Simple Radar Dashboard...");
        System.out.println("This version uses basic JFreeChart functionality.");
        
        // Create simple XY series for plotting
        XYSeries distanceSeries = new XYSeries("Distance (cm)");
        XYSeries detectionSeries = new XYSeries("Detected Objects");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(distanceSeries);
        dataset.addSeries(detectionSeries);

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Simple Ultrasonic Sensor Dashboard",
                "Time (samples)",
                "Distance (cm)",
                dataset
        );

        // Setup UI
        JFrame frame = new JFrame("Simple Radar Dashboard");
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
        JLabel infoLabel = new JLabel("Simple Chart - Basic JFreeChart | Detection Threshold: " + DETECTION_THRESHOLD + " cm");
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
                    distanceSeries.add(sampleCount, distance);

                    // Mark detections
                    if (distance < DETECTION_THRESHOLD) {
                        detectionSeries.add(sampleCount, distance);
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
            System.out.println("Shutting down Simple Radar Dashboard...");
        }));
        
        System.out.println("Simple Radar Dashboard is running. Close the window to stop.");
    }
}
