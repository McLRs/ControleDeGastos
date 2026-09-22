package com.mcl.controledegastos.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcl.controledegastos.R
import com.mcl.controledegastos.data.GastoMockData
import com.mcl.controledegastos.databinding.ActivityListaGastosBinding
import com.mcl.controledegastos.databinding.ChipCategoriaBinding
import com.mcl.controledegastos.model.CategoriaGasto
import com.mcl.controledegastos.model.Gasto
import com.mcl.controledegastos.ui.detail.DetalheGastoActivity
import com.mcl.controledegastos.util.paraMoeda

/**
 * Tela 1: lista de gastos do mês.
 *
 * Duas interações atualizam a interface/avançam o fluxo:
 *  - Tocar em um chip de categoria filtra a lista e atualiza o total exibido
 *    (atualiza a interface, sem trocar de tela).
 *  - Tocar em um gasto da lista abre a tela de detalhe via Intent explícita,
 *    levando o [Gasto] selecionado (avança o fluxo).
 */
class ListaGastosActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_GASTO = "extra_gasto"
    }

    private lateinit var binding: ActivityListaGastosBinding
    private lateinit var adapter: GastoAdapter

    private val todosOsGastos = GastoMockData.listaMock()
    private var categoriaSelecionada: CategoriaGasto? = null
    private val chipsPorCategoria = mutableMapOf<CategoriaGasto?, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaGastosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarLista()
        configurarChipsDeCategoria()
        atualizarInterface()
    }

    private fun configurarLista() {
        adapter = GastoAdapter(emptyList()) { gasto -> abrirDetalhe(gasto) }
        binding.recyclerGastos.layoutManager = LinearLayoutManager(this)
        binding.recyclerGastos.adapter = adapter
    }

    private fun configurarChipsDeCategoria() {
        adicionarChip(rotulo = getString(R.string.chip_todos), categoria = null)
        CategoriaGasto.entries.forEach { categoria ->
            adicionarChip(rotulo = categoria.rotulo, categoria = categoria)
        }
    }

    private fun adicionarChip(rotulo: String, categoria: CategoriaGasto?) {
        val chipBinding = ChipCategoriaBinding.inflate(
            LayoutInflater.from(this), binding.containerChips, false
        )
        chipBinding.textChip.text = rotulo
        chipBinding.root.setOnClickListener {
            categoriaSelecionada = categoria
            atualizarInterface()
        }
        binding.containerChips.addView(chipBinding.root)
        chipsPorCategoria[categoria] = chipBinding.root
    }

    private fun atualizarInterface() {
        val listaFiltrada = filtrarPorCategoria(categoriaSelecionada)
        adapter.atualizarLista(listaFiltrada)

        val total = listaFiltrada.sumOf { it.valor }
        binding.textTotal.text = total.paraMoeda()

        val listaVazia = listaFiltrada.isEmpty()
        binding.textListaVazia.visibility = if (listaVazia) View.VISIBLE else View.GONE
        binding.recyclerGastos.visibility = if (listaVazia) View.GONE else View.VISIBLE

        chipsPorCategoria.forEach { (categoria, view) ->
            view.isSelected = categoria == categoriaSelecionada
        }
    }

    private fun filtrarPorCategoria(categoria: CategoriaGasto?): List<Gasto> {
        return if (categoria == null) {
            todosOsGastos
        } else {
            todosOsGastos.filter { it.categoria == categoria }
        }
    }

    private fun abrirDetalhe(gasto: Gasto) {
        val intent = Intent(this, DetalheGastoActivity::class.java)
        intent.putExtra(EXTRA_GASTO, gasto)
        startActivity(intent)
    }
}
