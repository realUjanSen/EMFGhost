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

---

## How It All Works: Technical Architecture

This section explains the complete technical flow of the EMF Ghost Detector app, from sensor hardware to UI display.

### 1. Application Entry Point & Structure

#### Activity Hierarchy
The app uses a multi-activity architecture:
- **MainActivity**: Main detection screen with sensor integration
- **RecordsActivity**: Displays list of past recording sessions
- **SessionDetailActivity**: Shows detailed graphs for a specific session

#### Fragment-Based Views
MainActivity uses **ViewPager2** with two swipeable fragments:
- **DigitalFragment**: Numeric display with LEDs, controls, and sensor info
- **AnalogFragment**: Speedometer-style gauge visualization

The **MainPagerAdapter** manages fragment creation and switching.

### 2. Sensor Data Acquisition

#### Sensor Registration (MainActivity.java)
When the app starts or resumes (`onResume()`):

```
MainActivity implements SensorEventListener
↓
onCreate() initializes:
  - SensorManager (system service)
  - Magnetometer sensor (TYPE_MAGNETIC_FIELD)
  - Temperature sensor (TYPE_AMBIENT_TEMPERATURE, optional)
↓
onResume() registers listeners:
  - sensorManager.registerListener(this, magneticSensor, SENSOR_DELAY_FASTEST)
  - sensorManager.registerListener(this, temperatureSensor, SENSOR_DELAY_FASTEST)
```

**SENSOR_DELAY_FASTEST** provides the highest possible sampling rate for real-time detection.

#### Sensor Data Processing (onSensorChanged)
When sensor hardware detects a change:

```
1. Hardware magnetometer chip (e.g., AK09918) triggers event
↓
2. Android SensorEvent delivered to onSensorChanged()
↓
3. Extract 3-axis magnetic field values:
   - X-axis: event.values[0]
   - Y-axis: event.values[1]
   - Z-axis: event.values[2]
↓
4. Calculate magnitude (total field strength):
   currentMagneticField = √(x² + y² + z²)
↓
5. Trigger cascading updates:
   - simulateTemperature() (if no hardware sensor)
   - notifyFragments() → UI updates
   - updateSound() → audio feedback
   - updateFlashlight() → visual feedback
```

#### Sensor Identification
The app extracts real hardware chip names using `extractSensorIdentifier()`:
- Reads sensor.getName() and sensor.getVendor()
- Filters out generic names like "Magnetometer"
- Extracts chip model (e.g., "AK09918", "BMM150")
- Falls back to vendor + version code if needed
- Result: Displays actual hardware model instead of generic "Magnetometer"

### 3. Temperature Simulation Algorithm

When no hardware temperature sensor exists:

```
Normal Mode (EMF < 60 μT):
  - Base temperature: 26.5°C
  - Add sine wave oscillation: ±1.5°C
  - Formula: T = 26.5 + sin(time/10000) × 1.5
  - Result: Gentle fluctuation between 25-28°C

High EMF Mode (60-100 μT):
  - Linear temperature drop
  - Formula: T = 26 - (field - 60) × 0.65
  - Example: At 80 μT → T = 26 - 20×0.65 = 13°C

Extreme EMF Mode (100-200 μT):
  - Continues dropping below zero
  - Formula: T = 0 - (field - 100) × 0.1
  - Capped at -10°C minimum
  - Example: At 150 μT → T = -5°C

Smoothing:
  - Applies lerp (linear interpolation): 5% per frame
  - currentTemp += (targetTemp - currentTemp) × 0.05
  - Creates gradual, realistic transitions
```

This mimics reported paranormal "cold spots" phenomenon.

### 4. UI Updates: Observer Pattern

The app uses an observer pattern to update UI components:

```
MainActivity (Subject)
↓
implements notifyFragments() method
↓
Gets all active fragments from FragmentManager
↓
For each fragment implementing SensorDataListener:
  - onMagneticFieldChanged(float)
  - onTemperatureChanged(float)
  - onRecordingStateChanged(boolean)
  - onFlashlightStatusChanged(boolean, float)
↓
Fragments update UI on main thread
```

#### Digital Fragment Updates
```
onMagneticFieldChanged(magneticField):
  1. Update emfValue TextView with 6 decimals
  2. Calculate color (green→red gradient based on 0-600 μT)
  3. Update 5 LED indicators:
     - LED1: ≥60 μT (green_bright vs green_dim)
     - LED2: ≥150 μT (lime)
     - LED3: ≥250 μT (yellow)
     - LED4: ≥400 μT (orange)
     - LED5: ≥550 μT (red)

onTemperatureChanged(temperature):
  1. Update tempValue TextView with 3 decimals
  2. Calculate color (violet→green gradient based on 10-28°C)
  3. Apply color to text
```

#### Analog Fragment Updates (Custom View)
**AnalogGaugeView** extends View with custom drawing:
```
setValue(magneticField):
  1. Clamp value to 0-600 μT range
  2. Call invalidate() to trigger redraw
↓
onDraw(canvas):
  1. Draw gradient arc (180° semicircle)
     - Gradient: green→lime→yellow→orange→red
  2. Draw scale markers (0, 60, 120...600)
  3. Calculate needle angle:
     - Ratio = currentValue / 600
     - Angle = 180° + (180° × ratio)
  4. Draw needle as triangular path
  5. Draw center pivot circle
```

### 5. Audio Feedback System

#### Sound Initialization
```
onCreate():
  - Create SoundPool with AudioAttributes
  - Load emf_alert.wav from res/raw/
  - Store soundId for playback
```

#### Dynamic Pitch Control
```
updateSound():
  IF magneticField >= 60 μT:
    1. Calculate pitch (playback rate):
       - Base: 0.5x at 60 μT
       - Max: 2.0x at 600+ μT
       - Formula: pitch = 0.5 + ((field - 60) / 540) × 1.5
       - Clamp: 0.5 ≤ pitch ≤ 2.0
    
    2. IF not already playing:
       - soundPool.play(soundId, volume=1.0, loop=-1, rate=pitch)
       - Store currentStreamId
    
    3. ELSE IF pitch changed significantly (>0.05):
       - soundPool.setRate(currentStreamId, newPitch)
       - Update currentPlaybackRate
  
  ELSE:
    - soundPool.stop(currentStreamId)
    - Reset currentStreamId
```

**Note**: Android SoundPool links pitch and playback speed together. Higher pitch = faster playback, mimicking real Geiger counters and EMF detectors.

### 6. Flashlight Flicker System

#### Mode-Based Flickering
The flashlight uses discrete modes to minimize camera API calls:

```
Flicker Modes (based on EMF):
  Mode 0 (<60 μT):   Steady ON, no flicker (0 Hz)
  Mode 1 (60-100):   Little flicker (1.0 Hz)
  Mode 2 (100-200):  Moderate flicker (1.5 Hz)
  Mode 3 (200-400):  Fast flicker (2.5 Hz)
  Mode 4 (400+):     Extreme flicker (4.0 Hz)
```

#### Flicker Implementation
```
updateFlashlight():
  1. Calculate newFlickerMode based on currentMagneticField
  2. IF mode changed:
     - stopFlashlightFlicker() (remove all callbacks)
     - Switch to new mode:
       
       Mode 0: turnOnFlashlight() (steady)
       
       Mode 1: Start pattern (1 Hz = 1 dim/sec):
         - ON for 0.7s
         - OFF for 0.3s
         - Repeat every 1.0s
       
       Mode 2: Start pattern (1.5 Hz = 1.5 dims/sec):
         - ON for 0.4s
         - OFF for 0.27s
         - Repeat every 0.67s
       
       Mode 3: Start pattern (2.5 Hz = 2.5 dims/sec):
         - ON for 0.2s
         - OFF for 0.2s
         - Repeat every 0.4s
       
       Mode 4: Start pattern (4 Hz = 4 dims/sec):
         - ON for 0.125s
         - OFF for 0.125s
         - Repeat every 0.25s
  
  3. ELSE: Do nothing (avoid restarting pattern unnecessarily)
```

#### Camera Control
```
turnOnFlashlight():
  IF already on: return (skip redundant API call)
  cameraManager.setTorchMode(cameraId, true)
  isFlashlightOn = true
  notifyFlashlightStatus() → update UI

turnOffFlashlight():
  IF already off: return
  cameraManager.setTorchMode(cameraId, false)
  isFlashlightOn = false
  notifyFlashlightStatus() → update UI
```

The app tracks `isFlashlightOn` to avoid redundant camera API calls during fast flicker patterns, which could cause exceptions or lag.

### 7. Recording & Data Persistence

#### Recording Flow
```
User taps "START RECORDING":
  ↓
  toggleRecording() in MainActivity:
    1. Set isRecording = true
    2. Store recordingStartTime = currentTimeMillis()
    3. Create new ArrayList<EMFReading>
    4. Start handler that runs every 1 second:
       - Create EMFReading(timestamp, magneticField, temperature)
       - Add to currentSessionReadings list
    5. notifyFragments() → Update UI (button text, recording indicator)

User taps "STOP RECORDING":
  ↓
  toggleRecording() in MainActivity:
    1. Set isRecording = false
    2. Remove handler callbacks
    3. Create EMFSessionEntity:
       - sessionId = "session_" + startTime
       - startTime, endTime, readings list
    4. Save to database (background thread):
       - executor.execute(() -> database.sessionDao().insert(session))
    5. Clear currentSessionReadings
    6. notifyFragments() → Update UI
```

#### Database Architecture (Room)

**Entities:**
```
EMFSessionEntity (@Entity, table="sessions"):
  - id (auto-generated primary key)
  - sessionId (String, e.g., "session_1730901799191")
  - startTime (long, milliseconds)
  - endTime (long, milliseconds)
  - readings (List<EMFReading>, stored as JSON)

EMFReading (POJO, not a Room entity):
  - timestamp (long, relative to session start)
  - magneticField (double, μT)
  - temperature (double, °C)
```

**Type Converters:**
```
Converters.java:
  - Uses Gson library
  - Converts List<EMFReading> ↔ JSON String
  - Allows Room to store complex objects
  
  @TypeConverter
  fromReadingList(List<EMFReading>):
    → gson.toJson(readings)
  
  @TypeConverter
  toReadingList(String json):
    → gson.fromJson(json, List<EMFReading>)
```

**DAOs (Data Access Objects):**
```
SessionDao (@Dao interface):
  - insert(EMFSessionEntity)
  - getAllSessions() → List<EMFSessionEntity> (ORDER BY startTime DESC)
  - getSessionById(String) → EMFSessionEntity
  - deleteSessionById(String)
```

**Database Singleton:**
```
AppDatabase.getDatabase(context):
  - Uses double-checked locking pattern
  - Creates Room database: "emf_database"
  - Single instance per application
  - Thread-safe access
```

### 8. Records Viewing & Graph Display

#### Records List (RecordsActivity)
```
onCreate():
  1. Initialize RecyclerView with SessionsAdapter
  2. Load sessions from database (background thread):
     - Query: database.sessionDao().getAllSessions()
     - Convert EMFSessionEntity → EMFSession (POJO)
     - Update UI on main thread
  3. Display list with:
     - Session timestamp
     - Duration
     - Tap listener → open SessionDetailActivity
     - Delete listener → show confirmation dialog
```

#### Session Detail & Graphs (SessionDetailActivity)
```
onCreate():
  1. Get sessionId from Intent
  2. Load session from database (background thread):
     - Query: database.sessionDao().getSessionById(sessionId)
  3. Display session info (start time, duration, reading count)
  4. Create two LineCharts using MPAndroidChart library:
     - EMF Chart (magnetic field over time)
     - Temperature Chart (temperature over time)

setupLineChart(chart, entries, label, color):
  1. Create LineDataSet:
     - Set color, line width
     - Disable circle markers (too many points)
     - Enable dashed highlight line (crosshair effect)
  2. Configure chart:
     - Enable touch, drag, zoom, pinch
     - Set dark grid background
     - Format X-axis as seconds (e.g., "45.0s")
     - Format Y-axis for values
  3. Set CustomMarkerView:
     - Shows data point values near finger
     - Displays "Time: Xs\nValue: Y"
     - Positioned above selected point

User Interaction:
  - Tap on graph → highlight point with dashed crosshair
  - Drag finger → select different points
  - Pinch → zoom in/out
  - Scroll → pan through data
```

### 9. Color Calculation Algorithms

#### EMF Color (Green → Red)
```
getMagneticFieldColor(magneticField):
  1. Normalize: ratio = min(1.0, magneticField / 600.0)
  2. Linear RGB interpolation:
     - Red channel:   0 → 255 (increases)
     - Green channel: 255 → 0 (decreases)
     - Blue channel:  0 → 0 (stays 0)
  3. Combine: RGB(r, g, 0)
  4. Return as Android color int: 0xFF000000 | (r<<16) | (g<<8) | b
```

#### Temperature Color (Violet → Green)
```
getTemperatureColor(temperature):
  1. Clamp: temp = clamp(temperature, 10.0, 28.0)
  2. Normalize: ratio = (temp - 10) / (28 - 10)
  3. Linear RGB interpolation:
     - Violet (cold): RGB(148, 0, 211) at 10°C
     - Green (warm):  RGB(0, 255, 0) at 28°C
  4. Formula for each channel:
     - R: 148×(1-ratio) + 0×ratio
     - G: 0×(1-ratio) + 255×ratio
     - B: 211×(1-ratio) + 0×ratio
  5. Return as Android color int
```

### 10. Threading Model

The app uses a careful threading strategy to ensure smooth performance:

```
Main Thread (UI Thread):
  - Sensor callbacks (onSensorChanged)
  - UI updates (TextView, LED, gauge)
  - Audio control (SoundPool)
  - Flashlight control (CameraManager)
  - Handler-based recurring tasks (recording, flicker)

Background Thread (Executor):
  - Database operations (insert, query, delete)
  - Prevents UI freezing during disk I/O
  - Single thread executor: Executors.newSingleThreadExecutor()
  - Results posted back to main thread via runOnUiThread()

Why Main Thread for Sensors:
  - onSensorChanged() already runs on sensor thread
  - Must update UI immediately for real-time feel
  - Audio/flashlight APIs require main thread
  - Sensor processing is very fast (just math)
```

### 11. Performance Optimizations

1. **Flashlight Mode Caching**: Tracks `currentFlickerMode` to avoid restarting patterns when EMF stays in same range
2. **Redundant Camera Calls**: Checks `isFlashlightOn` before calling `setTorchMode()` to prevent exceptions
3. **Sound Rate Threshold**: Only updates playback rate if change > 0.05 to reduce API calls
4. **Fragment Visibility Check**: Only updates visible fragments to save CPU
5. **Database Singleton**: Single Room database instance across app
6. **Background Database Ops**: All DB queries run on executor thread
7. **Color Caching**: Color calculations happen only when values change
8. **LED Drawable Switch**: Uses existing drawable resources instead of creating new ones

### 12. Permissions & Manifest

Required permissions in AndroidManifest.xml:
```
INTERNET - For potential cloud features (not currently used)
ACCESS_NETWORK_STATE - Network status checking
HIGH_SAMPLING_RATE_SENSORS - Faster sensor sampling (Android 12+)
```

Required hardware features (optional):
```
android.hardware.sensor.magnetometer
android.hardware.sensor.ambient_temperature
android.hardware.camera.flash
```

All marked as `required="false"` so app can install on devices without these features.

### 13. Data Flow Summary

```
Hardware Sensors (Magnetometer Chip)
  ↓
Android SensorManager (System Service)
  ↓
MainActivity.onSensorChanged() [Main Thread]
  ↓
├─→ simulateTemperature() → currentTemperature
├─→ notifyFragments() → DigitalFragment, AnalogFragment → UI Update
├─→ updateSound() → SoundPool → Audio Feedback
└─→ updateFlashlight() → CameraManager → Visual Feedback
  ↓
Recording (if enabled) → EMFReading list
  ↓
Stop Recording → EMFSessionEntity → Room Database [Background Thread]
  ↓
RecordsActivity → Load sessions → RecyclerView
  ↓
SessionDetailActivity → Load session → MPAndroidChart → Interactive Graphs
```

This architecture provides real-time sensor feedback while maintaining smooth UI performance and reliable data persistence.

---

Happy Ghost Hunting!

