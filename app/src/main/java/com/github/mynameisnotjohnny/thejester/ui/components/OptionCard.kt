package com.github.mynameisnotjohnny.thejester.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OptionCard(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val textColor =
        if (!enabled) MaterialTheme.colorScheme.tertiary else if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = textColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(16.dp)
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = textColor,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopStart)
            )
        }

        Text(
            text = option,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
