package com.smart.docat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.smart.docat.ui.navigation.AppNavGraph
import com.smart.docat.ui.theme.DoCatTheme // Asegúrate de que este nombre coincida con tu tema
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // ¡Súper importante! Sin esto, Hilt no inyectará tus ViewModels
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoCatTheme {
                // Un contenedor "Surface" que toma el color de fondo de tu tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ¡Llamamos a tu gráfica de navegación!
                    AppNavGraph()
                }
            }
        }
    }
}