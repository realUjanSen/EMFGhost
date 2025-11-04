# EMF Ghost Detector - Changelog

## Version 1.0 - Current Release

### Features
- **Real-time Ghost Detection**: Uses phone's magnetometer (labeled as "Ghost Presence")
- **5-LED Warning System**: Progressive indicators (Green → Lime → Yellow → Orange → Red)
- **Dual Display Modes**: 
  - Digital view with precise numeric readings
  - Analog gauge with speedometer-style display
- **Dynamic Color System**:
  - Ghost Presence: Green (safe) → Red (danger) based on 0-600 μT range
  - Temperature: Green (warm) → Violet (paranormal cold) based on 25°C → 10°C range
- **Environmental Temperature Simulation**:
  - Mimics paranormal cold spots when high EMF detected
  - Drops from 26°C to 0°C as EMF increases from 60 to 100 μT
  - Can reach -10°C at extreme EMF levels (200+ μT)
- **Audio Alert System**:
  - Activates at 60 μT
  - Increasing pitch and speed with EMF intensity (like Geiger counter)
  - Continuous loop while detecting
- **Session Recording**: Save EMF and temperature data over time
- **Graphical Analysis**: View past sessions with interactive graphs
- **Local Storage**: All data stored on device using Room database

### Technical Specifications

#### Sensors
- **Magnetometer**: 
  - Type: `TYPE_MAGNETIC_FIELD`
  - Sampling: `SENSOR_DELAY_FASTEST` (maximum speed)
  - Display: Actual chip model (e.g., "AKM09919")
  - Range: 0-600+ μT
  
- **Temperature**:
  - Type: `TYPE_AMBIENT_TEMPERATURE` (if available)
  - Fallback: Environmental Simulation Module
  - Display: Actual sensor model or simulation mode indicator

#### LED Thresholds
- Green (LED 1): ≥ 60 μT
- Lime (LED 2): ≥ 150 μT
- Yellow (LED 3): ≥ 250 μT
- Orange (LED 4): ≥ 400 μT
- Red (LED 5): ≥ 550 μT

#### Temperature Simulation Formula
When no hardware sensor available:
- **Normal** (< 60 μT): T = 26.5 + 1.5 × sin(time/10000)
  - Range: 25-28°C with gentle oscillation
  
- **Paranormal** (60-100 μT): T = 26 - (field - 60) × 0.65
  - Linear drop from 26°C to 0°C
  - Example: 61 μT = 25.35°C, 80 μT = 13°C, 100 μT = 0°C
  
- **Extreme** (100+ μT): T = 0 - (field - 100) × 0.1
  - Continues dropping below zero
  - Example: 150 μT = -5°C, 200+ μT = -10°C (capped)

#### Audio System
- Engine: SoundPool
- File: `emf_alert.wav`
- Pitch/Speed: 0.5x (60 μT) to 2.0x (600+ μT)
- Behavior: Faster beeping = stronger field (mimics real EMF detectors)
- Note: Pitch and speed are linked due to Android API limitation

#### Data Storage
- Database: Room (SQLite)
- Recording: 1 sample per second
- Fields: Timestamp, Magnetic Field, Temperature
- Charts: MPAndroidChart library with dashed crosshairs

### Permissions Required
- `INTERNET` - For potential future updates
- `ACCESS_NETWORK_STATE` - Network status checking
- `HIGH_SAMPLING_RATE_SENSORS` - Maximum sensor speed (Android 12+)

### Device Requirements
- Android 6.0+ (API 23+)
- Magnetometer (standard on most phones)
- Temperature sensor (optional - will simulate if unavailable)

### Known Limitations
1. **Audio Pitch/Speed**: Cannot be changed independently with SoundPool
   - This is an Android API limitation
   - Current behavior mimics real Geiger counters (faster = more intense)
   
2. **Temperature Sensor**: Most phones don't have ambient temperature sensors
   - App uses realistic simulation that responds to EMF changes
   
3. **Magnetic Interference**: Readings affected by:
   - Electronic devices
   - Magnets and metal objects
   - Phone case magnets
   - Earth's magnetic field varies by location (25-65 μT normal)

### Tips for Best Results
1. Calibrate in a quiet room away from electronics
2. Remove phone from magnetic cases
3. Hold phone steady during readings
4. Normal Earth magnetic field: 25-65 μT
5. Strong magnets can reach 500+ μT
6. Record sessions for at least 30 seconds
7. Temperature drops of 2-3°C may indicate anomalies

### Future Considerations
- Could add real-time pitch shifting library for independent pitch control
- Could add vibration feedback at high EMF levels
- Could add compass display showing magnetic field direction
- Could add export functionality for session data

---

**Note**: This app is for entertainment and educational purposes. Magnetic field detection is real, but interpretation of results as "paranormal activity" is subjective.

