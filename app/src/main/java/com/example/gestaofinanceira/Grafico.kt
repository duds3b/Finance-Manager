package com.example.gestaofinanceira

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CORES = listOf(
    Color(0xFF1E88E5), Color(0xFFE53935), Color(0xFFFB8C00),
    Color(0xFF8E24AA), Color(0xFF43A047), Color(0xFF00ACC1)
)

@Composable
fun GraficoCategorias(transacoes: List<Transacao>) {
    val totais = transacoes
        .filter { it.tipo == "Despesa" }
        .groupBy { it.categoria }
        .map { (cat, lista) -> cat to lista.sumOf { it.valor } }
        .sortedByDescending { it.second }
    val maior = totais.maxOfOrNull { it.second } ?: 1.0

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Despesas por categoria", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            if (totais.isEmpty()) Text("Nenhuma despesa ainda")
            totais.forEachIndexed { i, (cat, total) ->
                Text("$cat  ${formatar(total)}", style = MaterialTheme.typography.bodySmall)
                Box(
                    Modifier
                        .fillMaxWidth((total / maior).toFloat().coerceIn(0.03f, 1f))
                        .height(20.dp)
                        .background(CORES[i % CORES.size], RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}