package com.smart.docat.ui.components

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun MotivationalPhrase(
    phrase: String,
    modifier: Modifier = Modifier
){
    Text(
        text = phrase,
        style = MaterialTheme.typography.bodyLarge,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}