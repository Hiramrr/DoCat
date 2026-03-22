package com.smart.docat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.smart.docat.R


@Composable
fun CanMascot(
    mood: CatMood,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
){
    val redId = when (mood){
        CatMood.BASE -> R.drawable.cat_base
        CatMood.WORKING -> R.drawable.cat_working
        CatMood.SLEEPING -> R.drawable.cat_sleep
        CatMood.ALARM -> R.drawable.cat_alarm
    }

    Image(
        painter = painterResource(id = redId),
        contentDescription = null,
        modifier = modifier.size(size)
    )
}