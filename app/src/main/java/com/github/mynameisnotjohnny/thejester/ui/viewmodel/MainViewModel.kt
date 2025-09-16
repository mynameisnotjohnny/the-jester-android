package com.github.mynameisnotjohnny.thejester.ui.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private lateinit var usbManager: UsbManager
    private var usbSerialPort: UsbSerialPort? = null
    private val ACTION_USB_PERMISSION = "com.example.rf_clown.USB_PERMISSION"

    // UI State
    private val _isConnected = mutableStateOf(false)
    val isConnected: State<Boolean> = _isConnected

    val logMessages = mutableStateListOf<String>()

    private val _externalStartupMode = mutableStateOf<String?>(null)
    val onStartupMode: State<String?> = _externalStartupMode

    // Callbacks
    var onShowAlert: ((String, String) -> Unit)? = null

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.let { connectToDevice(it) }
                        }
                    }
                }

                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    device?.let { requestUsbPermission(it) }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    disconnectDevice()
                }
            }
        }
    }

    fun initializeUsbManager() {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    fun registerUsbReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(usbReceiver, filter)
        }

        // Try to connect to existing device
        findAndConnectDevice()
    }

    fun unregisterUsbReceiver() {
        try {
            context.unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error unregistering receiver: ${e.message}")
        }
    }

    private fun findAndConnectDevice() {
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Log.d("MainViewModel", "No USB serial drivers found")
            addLogMessage("No USB serial drivers found")
            return
        }

        val driver = availableDrivers[0]
        val connection = usbManager.openDevice(driver.device)
        if (connection == null) {
            requestUsbPermission(driver.device)
            return
        }

        connectToDevice(driver.device)
    }

    private fun requestUsbPermission(device: UsbDevice) {
        val permissionIntent = PendingIntent.getBroadcast(
            context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
        )
        usbManager.requestPermission(device, permissionIntent)
    }

    private fun connectToDevice(device: UsbDevice) {
        try {
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            val driver = availableDrivers.find { it.device == device }

            if (driver != null) {
                val connection = usbManager.openDevice(device)
                if (connection != null) {
                    usbSerialPort = driver.ports[0]
                    usbSerialPort?.open(connection)
                    usbSerialPort?.setParameters(
                        115200,
                        8,
                        UsbSerialPort.STOPBITS_1,
                        UsbSerialPort.PARITY_NONE
                    )
                    _isConnected.value = true
                    Log.d("MainViewModel", "Connected to USB device: ${device.deviceName}")
                    addLogMessage("Connected to USB device: ${device.deviceName}")
                    startSerialReading()
                }
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error connecting to device: ${e.message}")
        }
    }

    fun disconnectDevice() {
        try {
            viewModelScope.launch {
                // Cancel any ongoing serial reading
            }
            usbSerialPort?.close()
            usbSerialPort = null
            _isConnected.value = false
            Log.d("MainViewModel", "Disconnected from USB device")
            addLogMessage("Disconnected from USB device")
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error disconnecting device: ${e.message}")
        }
    }

    fun sendSerialData(option: String): Boolean {
        if (usbSerialPort != null && usbSerialPort!!.isOpen) {
            try {
                val data = "$option\n".toByteArray()
                usbSerialPort!!.write(data, 1000)
                Log.d("MainViewModel", "Sent via serial: $option")
                addLogMessage("Sent via serial: $option")
                return true
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error sending serial data: ${e.message}")
                addLogMessage("Error sending serial data: ${e.message}")
                onShowAlert?.invoke("Serial Error", "Failed to send data: ${e.message}")
                return false
            }
        } else {
            Log.d("MainViewModel", "USB serial port not available")
            addLogMessage("USB serial port not available")
            onShowAlert?.invoke(
                "Serial Port Unavailable",
                "USB serial port is not connected. Please connect a USB serial device."
            )
            return false
        }
    }

    private fun addLogMessage(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "[$timestamp] $message"
        logMessages.add(logEntry)
    }

    private fun startSerialReading() {
        viewModelScope.launch {
            val buffer = ByteArray(1024)
            while (isActive && usbSerialPort?.isOpen == true) {
                try {
                    withContext(Dispatchers.IO) {
                        usbSerialPort?.read(buffer, 1000)
                    }?.let { bytesRead ->
                        if (bytesRead > 0) {
                            val message = String(buffer, 0, bytesRead).trim()
                            if (message.isNotEmpty()) {
                                Log.d("MainViewModel", "Received: $message")
                                addLogMessage("Received: $message")
                                parseIncomingMessage(message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error reading serial data: ${e.message}")
                    addLogMessage("Error reading serial data: ${e.message}")
                    break
                }
            }
        }
    }

    private fun parseIncomingMessage(message: String) {
        if (message.startsWith("default:")) {
            val mode = message.substringAfter("default:")
            val uiMode = when (mode.lowercase()) {
                "off" -> "Off"
                "bluetooth" -> "Bluetooth"
                "ble" -> "BLE"
                "both" -> "Both"
                else -> "Off" // Default fallback
            }
            Log.d("MainViewModel", "Startup mode: $uiMode")
            addLogMessage("Startup mode: $uiMode")
            _externalStartupMode.value = uiMode
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectDevice()
    }
}