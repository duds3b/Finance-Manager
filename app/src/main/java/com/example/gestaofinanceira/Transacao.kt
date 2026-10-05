package com.example.gestaofinanceira

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale

data class Transacao(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val categoria: String,
    val tipo: String,
    val data: String
)

val VERDE = Color(0xFF2E7D32)
val VERMELHO = Color(0xFFC62828)

fun formatar(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)