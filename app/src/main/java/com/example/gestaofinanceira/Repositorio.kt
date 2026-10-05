package com.example.gestaofinanceira

import android.content.Context
import java.io.File

class Repositorio(context: Context) {

    private val arquivo = File(context.filesDir, "transacoes.txt")
    private val arquivoRenda = File(context.filesDir, "renda.txt")

    fun carregar(): List<Transacao> {
        if (!arquivo.exists()) return emptyList()
        return arquivo.readLines().mapNotNull { linha ->
            val p = linha.split("|")
            if (p.size != 6) return@mapNotNull null
            val id = p[0].toLongOrNull() ?: return@mapNotNull null
            val valor = p[2].toDoubleOrNull() ?: return@mapNotNull null
            Transacao(id, p[1], valor, p[3], p[4], p[5])
        }
    }

    fun salvar(lista: List<Transacao>) {
        arquivo.writeText(
            lista.joinToString("\n") { t ->
                listOf(t.id, limpar(t.descricao), t.valor, limpar(t.categoria), t.tipo, t.data)
                    .joinToString("|")
            }
        )
    }

    fun carregarRenda(): Double {
        if (!arquivoRenda.exists()) return 0.0
        return arquivoRenda.readText().trim().toDoubleOrNull() ?: 0.0
    }

    fun salvarRenda(valor: Double) {
        arquivoRenda.writeText(valor.toString())
    }

    private fun limpar(texto: String) = texto.replace("|", " ").replace("\n", " ")
}