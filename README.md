# EMF Ghost Detector

An Android app that uses your phone's magnetometer and temperature sensors to detect electromagnetic fields (EMF), perfect for ghost hunting!

## Features

- **Real-time EMF Detection**: Uses the phone's magnetometer to detect magnetic fields in microTesla (μT)
- **Dual View Modes**: Swipe between digital and analog gauge displays
  - **Digital View**: Precise numeric readings with LED indicators
  - **Analog View**: Classic speedometer-style gauge with gradient color indicator
- **5 LED Indicators**: Visual LED feedback that lights up based on EMF strength (Green → Lime → Yellow → Orange → Red)
- **Temperature Monitoring**: Displays ambient temperature with high precision to detect small changes
- **Audio Alerts**: Variable pitch sound that plays when EMF exceeds 60 μT
  - Lower pitch at 60 μT
  - Higher pitch at 500+ μT
  - Continuous playback while in detection range
- **Recording Sessions**: Record EMF and temperature data over time
- **Local Storage**: All sessions saved using Room database (no internet required!)
- **Graphical Analysis**: View past recordings with interactive graphs featuring dashed crosshairs
- **Point Selection**: Tap graph data points to see exact values at specific times
- **Session Management**: Delete unwanted recording sessions with confirmation

## Setup Instructions

### Build and Run

1. Open the project in Android Studio
2. Let Gradle sync (this will download all dependencies)
3. Connect your Android device or use an emulator
4. Click Run!

**Note**: No Firebase or cloud setup required - all data is stored locally on your device!

## How to Use

### Main Screen (Digital View)
- **EMF Reading**: Shows real-time magnetic field strength labeled as "GHOST PRESENCE" with 6 decimal places
- **Temperature**: Displays ambient temperature with 3 decimal places
- **LED Indicators**: 5 LEDs light up progressively as you get closer to magnetic sources
  - Green LED: ≥ 60 μT
  - Lime LED: ≥ 120 μT
  - Yellow LED: ≥ 180 μT
  - Orange LED: ≥ 240 μT
  - Red LED: ≥ 300 μT (Very strong field - "ghost" detected!)


### Analog View
- Swipe left from the digital view to access the analog gauge
- Classic speedometer-style display with color gradient (Green → Red)
- Animated needle pointer shows current EMF strength
- Large numeric display below the gauge
- Swipe right to return to digital view

### Audio Alerts
- Sound automatically plays when EMF reaches 60 μT or higher
- Pitch increases with EMF strength:
  - 60 μT: Low pitch (0.5x speed)
  - 500+ μT: High pitch (2.0x speed)
- Sound loops continuously while EMF is in detection range
- Automatically stops when EMF drops below 60 μT

### Recording
1. Tap "START RECORDING" to begin capturing data
2. The app records readings every second
3. Recording status indicator appears when active
4. Walk around to detect EMF anomalies
5. Tap "STOP RECORDING" to save the session

### Viewing Past Records
1. Tap "VIEW PAST RECORDS"
2. See a list of all your recording sessions with timestamp and duration
3. Tap any session to view detailed graphs
4. Delete unwanted sessions with the trash icon (requires confirmation)
5. On the graph detail screen:
   - View both magnetic field and temperature data
   - Tap data points to see exact values with dashed crosshair indicator
   - Data values display near your finger for easy reading
   - Use back button to return to session list

## Technical Details

- **Sensors Used**:
  - `TYPE_MAGNETIC_FIELD`: Measures magnetic field strength (magnetometer)
  - `TYPE_AMBIENT_TEMPERATURE`: Measures ambient temperature
  - App detects available sensors and notifies if temperature sensor is unavailable

- **Data Storage**: 
  - Room Database (SQLite) for local storage
  - No internet connection required
  - Type converters for complex data structures

- **Libraries**:
  - Room Database (local SQLite)
  - MPAndroidChart (for graphs)
  - ViewPager2 (swipeable views)
  - AndroidX libraries

- **Audio**: 
  - SoundPool for low-latency playback
  - Variable playback rate for pitch control
  - Raw audio file: `emf_alert.wav`

## Notes

- Not all phones have temperature sensors. The app will notify you if temperature sensing is unavailable.
- The magnetometer is standard on most smartphones.
- Normal Earth's magnetic field is around 25-65 μT depending on location.
- Electronic devices, magnets, and metal objects will affect readings.
- The HIGH_SAMPLING_RATE_SENSORS permission is required for faster sensor updates.

## Ghost Hunting Tips

1. Calibrate by checking baseline EMF levels in a "normal" room
2. Sudden spikes or drops in EMF may indicate paranormal activity
3. Temperature drops of 2-3°C can indicate spirit presence
4. Record for at least 30 seconds in each location
5. Review graphs later to find patterns
6. The analog view provides a more atmospheric experience for investigations
7. Listen for pitch changes in the audio alert - rapid changes may indicate activity

Happy Ghost Hunting!

