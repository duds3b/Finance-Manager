package com.example.gestaofinanceira

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestaofinanceira.ui.theme.GestaoFinanceiraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = Repositorio(this)
        setContent {
            GestaoFinanceiraTheme {
                TelaPrincipal(repo)
            }
        }
    }
}