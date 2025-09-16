package com.github.mynameisnotjohnny.thejester.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnStartupBox(
    isConnected: Boolean,
    onSerialSend: (String) -> Unit = {},
    onStartupMode: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedStartupOption by remember { mutableStateOf("Off") }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Off", "Bluetooth", "BLE", "Both")

    val optionToSerialCommand = mapOf(
        "Off" to "default:off",
        "Bluetooth" to "default:bluetooth",
        "BLE" to "default:ble",
        "Both" to "default:both"
    )

    LaunchedEffect(onStartupMode) {
        onStartupMode?.let { mode ->
            if (options.contains(mode)) {
                selectedStartupOption = mode
            }
        }
    }

    val textColor =
        if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = isConnected) { expanded = !expanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "On startup",
                fontSize = 16.sp,
                color = textColor
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedStartupOption,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded && isConnected,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontWeight = if (option == selectedStartupOption) FontWeight.Medium else FontWeight.Normal,
                            color = if (option == selectedStartupOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    },
                    onClick = {
                        if (option != selectedStartupOption) {
                            optionToSerialCommand[option]?.let { command ->
                                onSerialSend(command)
                            }
                        }
                        selectedStartupOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}
