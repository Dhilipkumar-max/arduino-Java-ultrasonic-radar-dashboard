/*
 * Arduino Sketch for Radar Dashboard Testing
 * This sketch simulates distance data from an ultrasonic sensor
 * Connect to your computer via USB and set the port in the Java application
 */

// Pin definitions (adjust based on your actual setup)
const int TRIG_PIN = 9;
const int ECHO_PIN = 10;

void setup() {
  // Initialize serial communication
  Serial.begin(9600);
  
  // Initialize ultrasonic sensor pins
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  
  Serial.println("Arduino Radar Sensor Ready");
}

void loop() {
  // Get distance from sensor
  long distance = getDistance();
  
  // Send distance data to Java application
  Serial.println(distance);
  
  // Wait before next reading
  delay(100);
}

long getDistance() {
  // Clear the trigger pin
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  
  // Send trigger pulse
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  
  // Read echo pulse
  long duration = pulseIn(ECHO_PIN, HIGH);
  
  // Calculate distance in cm
  long distance = duration * 0.034 / 2;
  
  // Return distance (limit to reasonable range)
  if (distance > 400) distance = 400; // Max 4 meters
  if (distance < 2) distance = 2;     // Min 2 cm
  
  return distance;
}

/*
 * Alternative: For testing without actual sensor, uncomment this function
 * and comment out the getDistance() function above
 */
/*
long getDistance() {
  // Simulate distance data for testing
  static long simulatedDistance = 100;
  static int direction = 1;
  
  // Gradually change distance
  simulatedDistance += direction * random(1, 10);
  
  // Reverse direction at boundaries
  if (simulatedDistance > 300) direction = -1;
  if (simulatedDistance < 20) direction = 1;
  
  return simulatedDistance;
}
*/
