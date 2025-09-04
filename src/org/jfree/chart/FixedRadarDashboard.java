package org.jfree.chart;

import com.fazecast.jSerialComm.SerialPort;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class FixedRadarDashboard {
    private static final int DETECTION_THRESHOLD = 50; // cm

    public static void main(String[] args) {
        // Replace "COM3" with your Arduino port
        SerialPort port = SerialPort.getCommPort("COM3"); // Try COM4, COM5, etc.
        port.setBaudRate(9600);
        if (!port.openPort()) {
            System.out.println("Error: Could not open port.");
            return;
        }

        // Create auto-save file with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "radar_data_" + timestamp + ".csv";
        PrintWriter dataLogger = null;
        
        try {
            dataLogger = new PrintWriter(new FileWriter(filename));
            dataLogger.println("Degree,Distance,Timestamp,Detection");
            System.out.println("Auto-saving data to: " + filename);
            
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
            return;
        }

        // Create time series for plotting
        TimeSeries distanceSeries = new TimeSeries("Distance (cm)");
        TimeSeries detectionSeries = new TimeSeries("Detected Objects");

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(distanceSeries);
        dataset.addSeries(detectionSeries);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Fixed Ultrasonic Sensor Dashboard - Auto-Saving to " + filename,
                "Time",
                "Distance (cm)",
                dataset,
                true,
                true,
                false
        );

        // Setup UI
        JFrame frame = new JFrame("Radar Dashboard - Auto-Saving");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.add(new ChartPanel(chart));
        
        // Add status label
        JLabel statusLabel = new JLabel("Status: Connected to Arduino - Auto-saving to " + filename);
        statusLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        frame.add(statusLabel, java.awt.BorderLayout.SOUTH);
        
        frame.setVisible(true);

        // Read serial data in background
        final PrintWriter finalDataLogger = dataLogger;
        new Thread(() -> {
            InputStream in = port.getInputStream();
            Scanner scanner = new Scanner(in);

            while (scanner.hasNextLine()) {
                try {
                    String line = scanner.nextLine().trim();
                    System.out.println("Received: '" + line + "'"); // Debug output
                    if (!line.isEmpty()) {
                        // Parse Arduino output format: "degree,distance,timestamp"
                        if (line.contains(",") && line.split(",").length >= 2) {
                            String[] parts = line.split(",");
                            if (parts.length >= 2) {
                                int degree = Integer.parseInt(parts[0]);
                                int distance = Integer.parseInt(parts[1]);
                                String dataTimestamp = parts.length >= 3 ? parts[2] : String.valueOf(System.currentTimeMillis());
                                
                                // Auto-save to CSV file
                                boolean detected = distance < DETECTION_THRESHOLD;
                                finalDataLogger.println(degree + "," + distance + "," + dataTimestamp + "," + (detected ? "YES" : "NO"));
                                finalDataLogger.flush(); // Ensure data is written immediately
                                
                                // Add distance data to chart
                                distanceSeries.addOrUpdate(new Millisecond(), distance);

                                // Mark detections
                                if (detected) {
                                    detectionSeries.addOrUpdate(new Millisecond(), distance);
                                    System.out.println("Object detected at " + degree + "° - distance " + distance + "cm");
                                    statusLabel.setText("Status: OBJECT DETECTED at " + degree + "° - " + distance + "cm | Auto-saving to " + filename);
                                } else {
                                    statusLabel.setText("Status: Scanning... Distance: " + distance + "cm | Auto-saving to " + filename);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore parse errors
                }
            }
            scanner.close();
        }).start();

        // Add shutdown hook to close file properly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (finalDataLogger != null) {
                finalDataLogger.close();
                System.out.println("Data saved to: " + filename);
            }
            if (port.isOpen()) {
                port.closePort();
            }
        }));
        
        System.out.println("Dashboard started! All data is being auto-saved to: " + filename);
        System.out.println("Press Ctrl+C to stop and save data.");
    }
}
