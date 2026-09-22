package com.mcl.controledegastos.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.mcl.controledegastos.databinding.ActivityDetalheGastoBinding
import com.mcl.controledegastos.model.Gasto
import com.mcl.controledegastos.ui.corResId
import com.mcl.controledegastos.ui.letraIcone
import com.mcl.controledegastos.ui.list.ListaGastosActivity
import com.mcl.controledegastos.util.paraMoeda

/**
 * Tela 2: detalhe de um gasto específico.
 *
 * Recebe o [Gasto] selecionado na tela anterior através de uma Intent
 * explícita (extra [ListaGastosActivity.EXTRA_GASTO]) e apenas exibe seus
 * dados — nenhuma edição é feita nesta etapa do projeto.
 */
class DetalheGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheGastoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheGastoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gasto = IntentCompat.getSerializableExtra(
            intent, ListaGastosActivity.EXTRA_GASTO, Gasto::class.java
        )

        if (gasto == null) {
            // Não deveria acontecer em uso normal do app, mas evita crash
            // caso a Activity seja aberta sem o extra esperado.
            finish()
            return
        }

        exibirGasto(gasto)
        binding.imageVoltar.setOnClickListener { finish() }
    }

    private fun exibirGasto(gasto: Gasto) {
        binding.textLetraCategoria.text = gasto.categoria.letraIcone()
        binding.imageCirculoCategoria.setColorFilter(
            ContextCompat.getColor(this, gasto.categoria.corResId())
        )
        binding.textDescricao.text = gasto.descricao
        binding.textValor.text = gasto.valor.paraMoeda()
        binding.textCategoria.text = gasto.categoria.rotulo
        binding.textData.text = gasto.data
        binding.textFormaPagamento.text = gasto.formaPagamento.rotulo

        // Tratamento do valor opcional: só mostramos o bloco de observação
        // quando ele realmente existe.
        val temObservacao = !gasto.observacao.isNullOrBlank()
        binding.containerObservacao.visibility = if (temObservacao) View.VISIBLE else View.GONE
        if (temObservacao) {
            binding.textObservacao.text = gasto.observacao
        }
    }
}
