package com.github.mynameisnotjohnny.thejester

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mynameisnotjohnny.thejester.ui.components.LogMessageBox
import com.github.mynameisnotjohnny.thejester.ui.components.OnStartupBox
import com.github.mynameisnotjohnny.thejester.ui.components.OptionsGrid
import com.github.mynameisnotjohnny.thejester.ui.components.StatusBox

import com.github.mynameisnotjohnny.thejester.ui.theme.TheJesterTheme
import com.github.mynameisnotjohnny.thejester.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.initializeUsbManager()
        viewModel.onShowAlert = { title, message -> showAlert(title, message) }

        setContent {
            TheJesterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerUsbReceiver()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterUsbReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectDevice()
    }

    private fun showAlert(title: String, message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { _, _ -> }
                .show()
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel? = null
) {
    val isConnected = viewModel?.isConnected?.value ?: false
    val logMessages = viewModel?.logMessages ?: emptyList()
    val onStartupMode = viewModel?.onStartupMode?.value

    var selectedOption by remember { mutableStateOf("Off") }

    LaunchedEffect(isConnected) {
        if (viewModel != null && !isConnected) {
            selectedOption = "Off"
        }
    }

    LaunchedEffect(onStartupMode) {
        onStartupMode?.let { mode ->
            selectedOption = mode
        }
    }

    fun handleOptionSelection(option: String) {
        if (option != selectedOption) {
            val success = viewModel?.sendSerialData("mode:$option") ?: false
            if (success) {
                selectedOption = option
            }
        }
    }

    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isSmallScreen = maxHeight < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .let { if (isSmallScreen) it.verticalScroll(scrollState) else it }
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StatusBox(
                isConnected = isConnected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OnStartupBox(
                isConnected = isConnected,
                onSerialSend = { command -> viewModel?.sendSerialData(command) ?: false },
                onStartupMode = onStartupMode,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OptionsGrid(
                selectedOption = selectedOption,
                onOptionSelected = ::handleOptionSelection,
                isConnected = isConnected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LogMessageBox(
                messages = logMessages,
                modifier = Modifier
                    .fillMaxWidth()
                    .let {
                        if (isSmallScreen) {
                            it.height(200.dp)
                        } else {
                            it.weight(1f)
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    TheJesterTheme {
        MainContent()
    }
}
