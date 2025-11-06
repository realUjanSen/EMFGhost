# EMF Ghost Detector

An Android app that uses your phone's magnetometer and temperature sensors to detect electromagnetic fields (EMF), perfect for ghost hunting!

## Features

- **Real-time EMF Detection**: Uses the phone's magnetometer to detect magnetic fields in microTesla (μT)
- **Dual View Modes**: Swipe between digital and analog gauge displays
  - **Digital View**: Precise numeric readings with LED indicators
  - **Analog View**: Classic speedometer-style gauge with gradient color indicator
- **5 LED Indicators**: Visual LED feedback that lights up based on EMF strength (Green → Lime → Yellow → Orange → Red)
- **Dynamic Color Display**: 
  - Ghost Presence reading changes from green (low) to red (600+ μT)
  - Temperature reading changes from green (warm 25-28°C) to violet (cold 10°C and below)
- **Temperature Monitoring**: 
  - Real hardware sensor when available
  - Environmental simulation when no hardware sensor present
  - Simulated temperature drops dramatically when high EMF detected (paranormal cold spots!)
- **Audio Alerts**: Variable pitch sound that plays when EMF exceeds 60 μT
  - Lower pitch at 60 μT
  - Higher pitch at 600+ μT
  - Continuous playback while in detection range
- **Recording Sessions**: Record EMF and temperature data over time
- **Local Storage**: All sessions saved using Room database (no internet required!)
- **Graphical Analysis**: View past recordings with interactive graphs featuring dashed crosshairs
- **Point Selection**: Tap graph data points to see exact values at specific times
- **Session Management**: Delete unwanted recording sessions with confirmation
- **Sensor Information**: Displays actual hardware sensor model names (chip identification)

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
  - Color changes from green (low) to red (600+ μT)
  - Displays actual hardware sensor model/chip name
- **Temperature**: Displays ambient temperature with 3 decimal places
  - Color changes from green (warm 25-28°C) to violet (freezing cold 10°C and below)
  - Shows hardware sensor model or "Environmental Simulation Module" if no hardware sensor
  - **Paranormal Effect**: When EMF exceeds 60 μT, temperature drops dramatically
    - Normal: 25-28°C with gentle fluctuations
    - High EMF (60-100 μT): Temperature begins dropping
    - Extreme EMF (100+ μT): Temperature plunges below 0°C (paranormal cold spot!)
    - Minimum: -10°C at very high EMF levels
- **LED Indicators**: 5 LEDs light up progressively as you get closer to magnetic sources
  - Green LED: ≥ 60 μT
  - Lime LED: ≥ 150 μT
  - Yellow LED: ≥ 250 μT
  - Orange LED: ≥ 400 μT
  - Red LED: ≥ 550 μT (Extreme field - strong paranormal activity!)
- **Sensor Delay**: Uses SENSOR_DELAY_FASTEST for maximum sampling rate


### Analog View
- Swipe left from the digital view to access the analog gauge
- Classic speedometer-style display with color gradient (Green → Red)
- Animated needle pointer shows current EMF strength (0-600 μT)
- Large numeric display below the gauge
- Swipe right to return to digital view

### Audio Alerts
- Sound automatically plays when EMF reaches 60 μT or higher
- Pitch and speed increase together with EMF strength (like a Geiger counter):
  - 60 μT: Low pitch, slow beeps (0.5x speed)
  - 600+ μT: High pitch, fast beeps (2.0x speed)
- Creates urgency as EMF increases - faster beeping = stronger field
- Sound loops continuously while EMF is in detection range
- Automatically stops when EMF drops below 60 μT
- **Note**: Pitch and playback speed are linked (Android SoundPool limitation) - this mimics real EMF/radiation detectors

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
    - Displays actual chip/model name (e.g., "AK09918" instead of generic "Magnetometer")
    - Uses SENSOR_DELAY_FASTEST for maximum sampling rate
    - Requires HIGH_SAMPLING_RATE_SENSORS permission on Android 12+
  - `TYPE_AMBIENT_TEMPERATURE`: Measures ambient temperature
    - Displays hardware sensor model when available
    - Falls back to environmental simulation if no hardware sensor present
    - Simulation mimics paranormal cold spots when high EMF detected

- **Temperature Simulation** (when no hardware sensor available):
  - Normal conditions (EMF < 60 μT): 25-28°C with gentle oscillation
  - High EMF (60-100 μT): Temperature drops linearly
    - Formula: T = 26 - (field - 60) × 0.65
    - At 60 μT: ~26°C (starts dropping)
    - At 61 μT: ~25.35°C
    - At 100 μT: 0°C (freezing point!)
  - Extreme EMF (100-200 μT): Continues dropping below zero
    - Formula: T = 0 - (field - 100) × 0.1
    - At 150 μT: -5°C
    - At 200+ μT: -10°C (minimum cap)
  - Smooth interpolation for realistic transitions
  - Mimics reported paranormal "cold spots" phenomenon

- **Color Interpolation**:
  - Ghost Presence: Linear RGB interpolation from green (#00FF00) to red (#FF0000) over 0-600 μT range
  - Temperature: Linear RGB interpolation from violet (#9400D3) to green (#00FF00) over 10-28°C range

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
8. Enable flashlight flickering for visual feedback in dark environments
9. Flashlight will activate (start flickering) only when EMF reaches 60 μT or higher
10. Rapid flashlight flickering (10 flickers/3s at 550+ μT) indicates extremely strong EMF activity

Happy Ghost Hunting!

