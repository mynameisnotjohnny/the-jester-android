package com.github.mynameisnotjohnny.thejester.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionsGrid(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val options = listOf("Off", "Bluetooth", "BLE", "Both")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OptionCard(
                option = options[0],
                isSelected = isConnected && options[0] == selectedOption,
                onClick = {
                    onOptionSelected(options[0])
                },
                enabled = isConnected,
                modifier = Modifier.weight(1f)
            )

            OptionCard(
                option = options[1],
                isSelected = isConnected && options[1] == selectedOption,
                onClick = {
                    onOptionSelected(options[1])
                },
                enabled = isConnected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OptionCard(
                option = options[2],
                isSelected = isConnected && options[2] == selectedOption,
                onClick = {
                    onOptionSelected(options[2])
                },
                enabled = isConnected,
                modifier = Modifier.weight(1f)
            )

            OptionCard(
                option = options[3],
                isSelected = isConnected && options[3] == selectedOption,
                onClick = {
                    onOptionSelected(options[3])
                },
                enabled = isConnected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}