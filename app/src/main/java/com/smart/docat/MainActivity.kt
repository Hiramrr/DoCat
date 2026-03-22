package com.smart.docat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.smart.docat.ui.ambient.AmbientSoundScreen
import com.smart.docat.ui.theme.DoCatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoCatTheme {
                // TRUCO: Cargamos la pantalla directamente aquí
                AmbientSoundScreen()
            }
        }
    }
}