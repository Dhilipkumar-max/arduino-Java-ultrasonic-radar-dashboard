# Radar Dashboard

A comprehensive Java application suite for real-time radar/sonar data visualization and monitoring. This project provides multiple dashboard implementations with different features and complexity levels, from simple simulated data to full Arduino integration with data logging.
view of Dashboard
![Todo List Dashboard](https://i.postimg.cc/rwjw9Vs6/Screenshot-2025-09-04-022919.png)


## 🚀 Quick Start

**For first-time users:** Double-click `install_and_run.bat` and follow the setup instructions.

## 📋 Prerequisites

1. **Java Development Kit (JDK) 21 or later** - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. **Arduino with ultrasonic sensor** (optional) - For real hardware integration

## 📦 Dependencies

- **JFreeChart 1.0.19** - Advanced chart visualization
- **jSerialComm 2.10.4** - Serial communication with Arduino
- **Java Swing** - GUI components (built-in)

## 🏗️ Project Structure

```
RadarDashboard/
├── src/
│   ├── ArduinoTimeBasedDashboard.java     # Full-featured Arduino dashboard
│   ├── TimeBasedDashboard.java           # Simulated data dashboard
│   ├── org/jfree/chart/
│   │   ├── FixedRadarDashboard.java      # JFreeChart Arduino version
│   │   ├── BasicRadarDashboard.java      # Basic JFreeChart version
│   │   ├── SimpleRadarDashboard.java     # Simple chart version
│   │   ├── SimpleSwingDashboard.java     # Swing-only version
│   │   └── FileBasedDashboard.java       # File-based data version
│   ├── Radar/dashboard/
│   │   ├── BasicRadarDashboard.java      # Alternative radar implementation
│   │   └── TestRadarDashboard.java       # Test implementation
│   └── lib/
│       └── jSerialComm-2.10.4.jar
├── bin/                                  # Compiled classes
├── arduino_sketch.ino                   # Arduino sensor code
├── run.bat                              # Quick run script
├── install_and_run.bat                  # Setup and run script
└── radar_data_*.csv                     # Auto-generated data logs
```

## 🎯 Dashboard Versions

### 1. ArduinoTimeBasedDashboard.java ⭐ **RECOMMENDED**
- **Features**: Full-featured GUI with Arduino integration
- **UI**: Modern Swing interface with real-time controls
- **Data Source**: Arduino via serial communication
- **Visualization**: Custom real-time graph with detection alerts
- **Controls**: Connect/Disconnect, Start/Stop, Clear data
- **Best For**: Production use with real hardware

### 2. TimeBasedDashboard.java
- **Features**: Simulated data with full GUI
- **UI**: Same modern interface as Arduino version
- **Data Source**: Generated realistic simulation data
- **Visualization**: Custom real-time graph
- **Best For**: Testing and demonstration without hardware

### 3. FixedRadarDashboard.java
- **Features**: JFreeChart-based with auto-save
- **Data Source**: Arduino via serial communication
- **Visualization**: Professional JFreeChart time series
- **Auto-save**: CSV data logging with timestamps
- **Best For**: Data analysis and long-term monitoring

### 4. BasicRadarDashboard.java
- **Features**: Minimal JFreeChart implementation
- **Data Source**: Simulated data
- **Visualization**: Basic line chart
- **Best For**: Learning JFreeChart basics

### 5. SimpleSwingDashboard.java
- **Features**: Pure Swing implementation
- **Data Source**: Simulated data
- **Visualization**: Custom painted graphics
- **Best For**: Understanding core concepts

## 🚀 How to Run

### Option 1: Quick Setup (Recommended)
```bash
# Double-click this file for guided setup
install_and_run.bat
```

### Option 2: Direct Execution
```bash
# For Arduino integration (recommended)
java -cp "bin;src/lib/jSerialComm-2.10.4.jar" ArduinoTimeBasedDashboard

# For simulated data
java -cp "bin" TimeBasedDashboard

# For JFreeChart version with Arduino
java -cp "bin;src/lib/jSerialComm-2.10.4.jar;C:/path/to/jfreechart.jar" org.jfree.chart.FixedRadarDashboard
```

### Option 3: Eclipse IDE
1. Import project into Eclipse
2. Right-click on desired main class
3. Select "Run As" → "Java Application"

### Option 4: Manual Compilation
```bash
# Compile Arduino version
javac -cp "src/lib/jSerialComm-2.10.4.jar" -d bin src/ArduinoTimeBasedDashboard.java

# Compile simulated version
javac -d bin src/TimeBasedDashboard.java

# Run
java -cp "bin;src/lib/jSerialComm-2.10.4.jar" ArduinoTimeBasedDashboard
```

## 🔧 Arduino Setup

### Hardware Requirements
- Arduino Uno/Nano/Pro Mini
- HC-SR04 Ultrasonic Sensor
- Jumper wires

### Wiring
```
HC-SR04    Arduino
VCC    →   5V
GND    →   GND
Trig   →   Pin 9
Echo   →   Pin 10
```

### Arduino Code
Upload `arduino_sketch.ino` to your Arduino. The sketch:
- Reads distance from HC-SR04 sensor
- Sends data via Serial at 9600 baud
- Format: `degree,distance,timestamp` (one per line)

### Data Format
The Arduino should send data in this format:
```
0,150,1234567890
45,200,1234567891
90,75,1234567892
```

## ✨ Features

### Real-time Monitoring
- Live distance measurements
- Object detection alerts (threshold: 50cm)
- Real-time graph updates
- Data point counting

### User Interface
- Modern, intuitive GUI
- Color-coded status indicators
- Control buttons for all operations
- Live information display

### Data Management
- Automatic CSV logging (FixedRadarDashboard)
- Data history management
- Clear data functionality
- Timestamp tracking

### Visualization
- Custom real-time graphs
- JFreeChart professional charts
- Detection point highlighting
- Grid and axis labels

## 🛠️ Troubleshooting

### Common Issues

1. **"Could not open port" error**
   - Check Arduino connection
   - Verify COM port (try COM3, COM4, COM5, etc.)
   - Close Arduino IDE Serial Monitor
   - Check USB cable

2. **Compilation errors**
   - Ensure Java JDK 21+ is installed
   - Verify all JAR files are in correct locations
   - Check classpath settings

3. **No data received**
   - Verify Arduino is sending data
   - Check baud rate (9600)
   - Ensure proper data format
   - Check serial port permissions

4. **Java not found**
   - Install Java JDK 21+
   - Add Java to system PATH
   - Restart command prompt/IDE

### Debug Tips
- Check console output for error messages
- Use `TestRadarDashboard` for testing without hardware
- Verify Arduino code is uploaded correctly
- Test serial communication with Arduino IDE first

## 📊 Data Logging

The `FixedRadarDashboard` automatically saves data to CSV files:
- Filename format: `radar_data_YYYYMMDD_HHMMSS.csv`
- Columns: Degree, Distance, Timestamp, Detection
- Real-time saving with flush operations
- Automatic file closure on shutdown

## 🎨 Customization

### Detection Threshold
Modify `DETECTION_THRESHOLD` constant in any dashboard:
```java
private static final int DETECTION_THRESHOLD = 50; // cm
```

### Serial Port
Change COM port in Arduino versions:
```java
SerialPort port = SerialPort.getCommPort("COM3"); // Change to your port
```

### Graph Appearance
Customize colors, fonts, and layout in the `GraphPanel` classes.

## 📝 Notes

- All dashboards run in separate threads for smooth UI updates
- Distance values below threshold trigger visual alerts
- Charts automatically scale and update in real-time
- Test versions are perfect for development and demos
- Production versions require proper Arduino setup

## 🤝 Contributing

This project demonstrates various approaches to real-time data visualization in Java. Feel free to:
- Add new visualization types
- Implement additional sensor support
- Improve UI/UX design
- Add data analysis features

## 📄 License

This project is for educational and demonstration purposes.
