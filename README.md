<div align="center">
  <img src="https://github.com/user-attachments/assets/1b4ca968-6d71-41bb-a723-fbf235024d5b" alt="logo" width="200" height="auto" />
  
  <h1>The Jester Android Companion App</h1>
  <p>A modern Android companion application for [The Jester](https://github.com/mynameisnotjohnny/the-jester) Bluetooth and BLE jammer device. This app provides an intuitive interface to control your Jester device via USB serial communication.</p>
</div>

## 🌟 Features

- **USB Serial Communication**: Connects to The Jester device via USB OTG
- **Real-time Connection Status**: Visual indicators for device connection state
- **Mode Control**: Switch between OFF, BLUETOOTH, BLE, and BOTH modes
- **Startup Mode Configuration**: Set the default mode that The Jester will use on startup
- **Live Serial Log**: Real-time display of serial communication messages
- **Modern Material Design 3 UI**: Clean, intuitive interface built with Jetpack Compose


## 📱 Screenshots

The app features a clean, modern interface with connection status indicators, startup mode configuration, mode selection cards, and real-time serial communication logging.

| Disconnected State | Connected State | Startup Options | Light Mode |
|:------------------:|:---------------:|:---------------:|:----------:|
| <img src="https://github.com/user-attachments/assets/d18e287e-09dc-49f1-9bbf-ad89f1d5cbbd" alt="Disconnected State" width="200" /> | <img src="https://github.com/user-attachments/assets/5c2a7762-26fa-461f-acb6-7c07bd244d55" alt="Connected State" width="200" /> | <img src="https://github.com/user-attachments/assets/36c6d79d-f97e-4d5b-83f2-143888f70729" alt="Startup Options" width="200" /> | <img src="https://github.com/user-attachments/assets/79f9127f-f05f-4312-bc4e-1b1c0c9ad9bd" alt="Light Mode" width="200" /> |

## 🔧 Hardware Requirements

- Android device with USB OTG support (Android 6.0+)
- USB OTG cable or adapter
- The Jester device (ESP32 with nRF24L01 modules)
- USB cable to connect The Jester to your Android device

## 🚀 Installation

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API level 23+ (Android 6.0)
- USB OTG support on your Android device

### Building from Source

1. Clone this repository:
   ```bash
   git clone https://github.com/mynameisnotjohnny/the-jester-android.git
   cd the-jester-android
   ```

2. Open the project in Android Studio

3. Build and install the APK:
   ```bash
   ./gradlew assembleDebug
   ```

4. Install on your device:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 📖 Usage

### Initial Setup

1. **Connect The Jester Device**: Use a USB cable to connect your Jester device to your Android device via USB OTG
2. **Grant USB Permissions**: When prompted, grant USB permission to the app
3. **Verify Connection**: The app will show "Connected" status when successfully connected

### Controlling The Jester

#### Mode Selection
The app provides four operating modes:

- **Off**: Device is inactive
- **Bluetooth**: Jams Bluetooth Classic signals only  
- **BLE**: Jams Bluetooth Low Energy signals only
- **Both**: Alternates between Bluetooth and BLE jamming

Simply tap on any mode card to switch The Jester to that mode.

#### Startup Mode Configuration
Use the "On startup" dropdown to set the default mode that The Jester will use when it powers on:

- **Off**: Device starts in OFF mode
- **Bluetooth**: Device starts in BLUETOOTH mode
- **BLE**: Device starts in BLE mode  
- **Both**: Device starts in BOTH mode

This setting is saved persistently on The Jester device.

### Serial Communication

The app communicates with The Jester using the following serial commands:

#### Mode Control Commands
- `mode:off` - Set current mode to OFF
- `mode:bluetooth` - Set current mode to BLUETOOTH
- `mode:ble` - Set current mode to BLE
- `mode:both` - Set current mode to BOTH

#### Default Mode Commands
- `default:off` - Set default startup mode to OFF
- `default:bluetooth` - Set default startup mode to BLUETOOTH
- `default:ble` - Set default startup mode to BLE
- `default:both` - Set default startup mode to BOTH

All communication is logged in real-time in the serial log section at the bottom of the app.

## 🔌 Technical Details

### USB Serial Configuration
- **Baud Rate**: 115200
- **Data Bits**: 8
- **Stop Bits**: 1
- **Parity**: None
- **Flow Control**: None

### Architecture
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with ViewModel and StateFlow
- **Serial Communication**: USB Serial for Android library
- **Minimum SDK**: API 23 (Android 6.0)
- **Target SDK**: API 36 (Android 14)

### Permissions
- `android.hardware.usb.host` - Required for USB OTG functionality
- `android.permission.USB_PERMISSION` - Required for USB device access

## 🛠️ Development

### Project Structure
```
app/src/main/java/com/github/mynameisnotjohnny/thejester/
├── MainActivity.kt                 # Main activity and UI composition
├── ui/
│   ├── components/                 # Reusable UI components
│   │   ├── LogMessageBox.kt       # Serial log display
│   │   ├── OnStartupBox.kt        # Startup mode selector
│   │   ├── OptionCard.kt          # Mode selection cards
│   │   ├── OptionsGrid.kt         # Mode selection grid
│   │   └── StatusBox.kt           # Connection status indicator
│   ├── theme/                     # Material Design 3 theme
│   └── viewmodel/
│       └── MainViewModel.kt       # Business logic and USB communication
```

### Key Dependencies
- **Jetpack Compose**: Modern declarative UI toolkit
- **USB Serial for Android**: USB serial communication library
- **Material Design 3**: Latest Material Design components
- **AndroidX Lifecycle**: ViewModel and lifecycle management

## 🔒 Security & Legal Notice

This companion app is designed for:

- Educational purposes
- Security research and testing  
- Penetration testing in authorized environments
- Understanding RF communication vulnerabilities

**Important**: Always ensure you have proper authorization before testing in any environment. Unauthorized jamming may violate local laws and regulations.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly on a physical device
5. Submit a pull request

## 📄 License

This project is open source. Please refer to the main [Jester project](https://github.com/mynameisnotjohnny/the-jester) for licensing details.

## 🔗 Related Projects

- [The Jester Hardware Project](https://github.com/mynameisnotjohnny/the-jester) - The main ESP32-based jammer device
- [RF-Clown by cifertech](https://github.com/cifertech/RF-Clown) - Original inspiration for The Jester

---

**Remember**: Use this device and app responsibly and only in authorized testing environments!
