package com.example.gestaofinanceira

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelaPrincipal(repo: Repositorio) {
    val transacoes = remember { mutableStateListOf<Transacao>().apply { addAll(repo.carregar()) } }
    var renda by remember { mutableStateOf(repo.carregarRenda()) }
    var editando by remember { mutableStateOf<Transacao?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarRenda by remember { mutableStateOf(false) }

    val receitas = transacoes.filter { it.tipo == "Receita" }.sumOf { it.valor }
    val despesas = transacoes.filter { it.tipo == "Despesa" }.sumOf { it.valor }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editando = null
                mostrarDialogo = true
            }) { Text("+", fontSize = 26.sp) }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "Finance Manager",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                CardResumo(
                    renda = renda,
                    receitas = receitas,
                    despesas = despesas,
                    onAlterarRenda = { mostrarRenda = true }
                )
            }
            item { GraficoCategorias(transacoes) }
            item { Text("Transactions", style = MaterialTheme.typography.titleMedium) }
            if (transacoes.isEmpty()) {
                item { Text("No transactions yet. Tap + to add one.") }
            }
            items(transacoes.sortedByDescending { it.id }, key = { it.id }) { t ->
                ItemTransacao(
                    t = t,
                    onEditar = {
                        editando = t
                        mostrarDialogo = true
                    },
                    onExcluir = {
                        transacoes.remove(t)
                        repo.salvar(transacoes)
                    }
                )
            }
            item { Spacer(Modifier.height(70.dp)) }
        }
    }

    if (mostrarDialogo) {
        DialogoTransacao(
            atual = editando,
            onFechar = { mostrarDialogo = false },
            onSalvar = { nova ->
                val i = transacoes.indexOfFirst { it.id == nova.id }
                if (i >= 0) transacoes[i] = nova else transacoes.add(nova)
                repo.salvar(transacoes)
                mostrarDialogo = false
            }
        )
    }

    if (mostrarRenda) {
        DialogoRenda(
            rendaAtual = renda,
            onFechar = { mostrarRenda = false },
            onSalvar = { nova ->
                renda = nova
                repo.salvarRenda(nova)
                mostrarRenda = false
            }
        )
    }
}

@Composable
fun CardResumo(
    renda: Double,
    receitas: Double,
    despesas: Double,
    onAlterarRenda: () -> Unit
) {
    val saldo = renda + receitas - despesas
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Monthly income", style = MaterialTheme.typography.bodySmall)
                    Text(formatar(renda), fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onAlterarRenda) {
                    Text(if (renda == 0.0) "Set" else "Change")
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Monthly balance")
            Text(
                formatar(saldo),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (saldo >= 0) VERDE else VERMELHO
            )

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Extra: ${formatar(receitas)}", color = VERDE)
                Text("Expenses: ${formatar(despesas)}", color = VERMELHO)
            }

            if (renda > 0) {
                val porcentagem = (despesas / renda * 100).toInt()
                val fracao = (despesas / renda).toFloat().coerceIn(0f, 1f)
                Spacer(Modifier.height(12.dp))
                Text(
                    "You have spent $porcentagem% of your income",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(Color.LightGray, RoundedCornerShape(6.dp))
                ) {
                    if (fracao > 0f) {
                        Box(
                            Modifier
                                .fillMaxWidth(fracao)
                                .fillMaxHeight()
                                .background(
                                    if (porcentagem >= 100) VERMELHO else VERDE,
                                    RoundedCornerShape(6.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoRenda(rendaAtual: Double, onFechar: () -> Unit, onSalvar: (Double) -> Unit) {
    var texto by remember { mutableStateOf(if (rendaAtual > 0) rendaAtual.toString() else "") }
    var erro by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("How much do you earn per month?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Monthly income") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                if (erro.isNotEmpty()) Text(erro, color = VERMELHO)
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = texto.replace(",", ".").toDoubleOrNull()
                if (v == null || v < 0) {
                    erro = "Invalid amount"
                } else {
                    onSalvar(v)
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancel") }
        }
    )
}

@Composable
fun ItemTransacao(t: Transacao, onEditar: () -> Unit, onExcluir: () -> Unit) {
    val receita = t.tipo == "Receita"
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(t.descricao, fontWeight = FontWeight.Bold)
                Text("${t.categoria} • ${t.data}", style = MaterialTheme.typography.bodySmall)
                Row {
                    TextButton(onClick = onEditar) { Text("Edit") }
                    TextButton(onClick = onExcluir) { Text("Delete", color = VERMELHO) }
                }
            }
            Text(
                (if (receita) "+ " else "- ") + formatar(t.valor),
                color = if (receita) VERDE else VERMELHO,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DialogoTransacao(atual: Transacao?, onFechar: () -> Unit, onSalvar: (Transacao) -> Unit) {
    var descricao by remember { mutableStateOf(atual?.descricao ?: "") }
    var valor by remember { mutableStateOf(atual?.valor?.toString() ?: "") }
    var categoria by remember { mutableStateOf(atual?.categoria ?: "") }
    var tipo by remember { mutableStateOf(atual?.tipo ?: "Despesa") }
    var erro by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text(if (atual == null) "New transaction" else "Edit transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Description") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Category e.g. Groceries") },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipo == "Despesa", onClick = { tipo = "Despesa" })
                    Text("Expense")
                    Spacer(Modifier.width(12.dp))
                    RadioButton(selected = tipo == "Receita", onClick = { tipo = "Receita" })
                    Text("Income")
                }
                if (erro.isNotEmpty()) Text(erro, color = VERMELHO)
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = valor.replace(",", ".").toDoubleOrNull()
                if (descricao.isBlank()) {
                    erro = "Please enter a description"
                } else if (v == null || v <= 0) {
                    erro = "Invalid amount"
                } else {
                    onSalvar(
                        Transacao(
                            id = atual?.id ?: System.currentTimeMillis(),
                            descricao = descricao.trim(),
                            valor = v,
                            categoria = categoria.trim().ifBlank { "Other" },
                            tipo = tipo,
                            data = atual?.data
                                ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        )
                    )
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancel") }
        }
    )
}
