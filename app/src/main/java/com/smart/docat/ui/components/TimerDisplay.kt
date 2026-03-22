package com.smart.docat.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.smart.docat.core.utils.TimeFormatter

private val formatter = TimeFormatter()


@Composable
fun TimerDisplay(
    seconds: Int,
    modifier: Modifier = Modifier
){
    Text(
        text = formatter.formatSeconds(seconds),
        fontSize =  72.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}