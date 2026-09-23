package com.mcl.controledegastos.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcl.controledegastos.R
import com.mcl.controledegastos.data.GastoRepository
import com.mcl.controledegastos.databinding.ActivityNovaDespesaBinding
import com.mcl.controledegastos.model.CategoriaGasto
import com.mcl.controledegastos.model.FormaPagamento
import com.mcl.controledegastos.model.Gasto
import com.mcl.controledegastos.util.formatadorDeData
import java.time.LocalDate

/**
 * Tela 3: cadastro de uma nova despesa.
 *
 * Ao salvar, a despesa é adicionada ao [GastoRepository] (compartilhado com
 * a tela de lista) e a tela fecha com [finish], voltando para
 * [com.mcl.controledegastos.ui.list.ListaGastosActivity], que recarrega a
 * lista no `onResume`.
 *
 * Quando a compra é parcelada, o valor total informado é dividido em partes
 * iguais entre o número de parcelas: cada parcela vira um [Gasto] próprio,
 * datado um mês depois do anterior, com [Gasto.parcelaAtual] e
 * [Gasto.totalParcelas] preenchidos — é assim que elas aparecem nos meses
 * seguintes ao navegar pelas setas da tela de lista.
 */
class NovaDespesaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNovaDespesaBinding
    private var dataSelecionada: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaDespesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinners()
        configurarCampoDeData()
        configurarSwitchDeParcelas()

        binding.imageFechar.setOnClickListener { finish() }
        binding.buttonSalvar.setOnClickListener { salvarDespesa() }
    }

    private fun configurarSpinners() {
        binding.spinnerCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, CategoriaGasto.entries.map { it.rotulo }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.spinnerFormaPagamento.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, FormaPagamento.entries.map { it.rotulo }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun configurarCampoDeData() {
        atualizarTextoDeData()
        binding.inputData.setOnClickListener { abrirSeletorDeData() }
    }

    private fun abrirSeletorDeData() {
        DatePickerDialog(
            this,
            { _, ano, mesIndiceZero, dia ->
                dataSelecionada = LocalDate.of(ano, mesIndiceZero + 1, dia)
                atualizarTextoDeData()
            },
            dataSelecionada.year,
            dataSelecionada.monthValue - 1,
            dataSelecionada.dayOfMonth
        ).show()
    }

    private fun atualizarTextoDeData() {
        binding.inputData.setText(formatadorDeData.format(dataSelecionada))
    }

    private fun configurarSwitchDeParcelas() {
        binding.switchParcelado.setOnCheckedChangeListener { _, marcado ->
            binding.containerParcelas.visibility = if (marcado) View.VISIBLE else View.GONE
            if (!marcado) {
                binding.inputParcelasLayout.error = null
            }
        }
    }

    private fun salvarDespesa() {
        val descricao = binding.inputDescricao.text?.toString()?.trim().orEmpty()
        if (descricao.isEmpty()) {
            binding.inputDescricaoLayout.error = getString(R.string.erro_campo_obrigatorio)
            return
        }
        binding.inputDescricaoLayout.error = null

        val valorTotal = parseValorInformado(binding.inputValor.text?.toString().orEmpty())
        if (valorTotal == null || valorTotal <= 0.0) {
            binding.inputValorLayout.error = getString(R.string.erro_valor_invalido)
            return
        }
        binding.inputValorLayout.error = null

        val totalParcelas = if (binding.switchParcelado.isChecked) {
            val quantidade = binding.inputParcelas.text?.toString()?.trim()?.toIntOrNull()
            if (quantidade == null || quantidade < 2) {
                binding.inputParcelasLayout.error = getString(R.string.erro_parcelas_invalido)
                return
            }
            binding.inputParcelasLayout.error = null
            quantidade
        } else {
            1
        }

        val categoria = CategoriaGasto.entries[binding.spinnerCategoria.selectedItemPosition]
        val formaPagamento = FormaPagamento.entries[binding.spinnerFormaPagamento.selectedItemPosition]
        val observacao = binding.inputObservacao.text?.toString()?.trim()?.ifBlank { null }

        gerarValoresDasParcelas(valorTotal, totalParcelas).forEachIndexed { indice, valorDaParcela ->
            GastoRepository.adicionar(
                Gasto(
                    id = GastoRepository.proximoIdDisponivel(),
                    descricao = descricao,
                    categoria = categoria,
                    valor = valorDaParcela,
                    data = formatadorDeData.format(dataSelecionada.plusMonths(indice.toLong())),
                    formaPagamento = formaPagamento,
                    observacao = observacao,
                    parcelaAtual = indice + 1,
                    totalParcelas = totalParcelas
                )
            )
        }

        Toast.makeText(this, R.string.mensagem_despesa_adicionada, Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * Interpreta o texto digitado no campo de valor como um número decimal,
     * aceitando tanto "45,90" quanto "45.90" (o separador decimal do teclado
     * numérico varia conforme o idioma configurado no aparelho).
     */
    private fun parseValorInformado(textoDigitado: String): Double? {
        val texto = textoDigitado.trim()
        if (texto.isEmpty()) return null
        val normalizado = if (texto.contains('.') && texto.contains(',')) {
            texto.replace(".", "").replace(",", ".")
        } else {
            texto.replace(",", ".")
        }
        return normalizado.toDoubleOrNull()
    }

    /**
     * Divide [valorTotal] em [quantidade] parcelas iguais, em centavos, para
     * não perder nem sobrar nenhum centavo por causa de arredondamento de
     * ponto flutuante (ex.: R$ 100,00 em 3x -> R$ 33,34 + R$ 33,33 + R$ 33,33).
     */
    private fun gerarValoresDasParcelas(valorTotal: Double, quantidade: Int): List<Double> {
        val totalCentavos = Math.round(valorTotal * 100)
        val baseCentavos = totalCentavos / quantidade
        val restoCentavos = (totalCentavos % quantidade).toInt()
        return (0 until quantidade).map { indice ->
            val centavosDestaParcela = baseCentavos + if (indice < restoCentavos) 1 else 0
            centavosDestaParcela / 100.0
        }
    }
}
