package com.mcl.controledegastos.ui.list

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mcl.controledegastos.R
import com.mcl.controledegastos.data.GastoRepository
import com.mcl.controledegastos.databinding.ActivityListaGastosBinding
import com.mcl.controledegastos.databinding.ChipCategoriaBinding
import com.mcl.controledegastos.model.CategoriaGasto
import com.mcl.controledegastos.model.Gasto
import com.mcl.controledegastos.ui.add.NovaDespesaActivity
import com.mcl.controledegastos.ui.detail.DetalheGastoActivity
import com.mcl.controledegastos.util.formatadorDeData
import com.mcl.controledegastos.util.paraMoeda
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela 1: lista de gastos do mês.
 *
 * Interações que atualizam a interface/avançam o fluxo:
 *  - Tocar em um chip de categoria filtra a lista e atualiza o total exibido;
 *  - Tocar nas setas do cabeçalho troca o mês exibido (uma compra parcelada
 *    cai em meses seguintes, então é assim que as próximas parcelas aparecem);
 *  - Tocar no botão "+" abre a tela de nova despesa (Intent explícita);
 *  - Arrastar um item da lista para o lado remove esse gasto (com opção de
 *    desfazer pelo Snackbar);
 *  - Tocar em um gasto da lista abre a tela de detalhe via Intent explícita,
 *    levando o [Gasto] selecionado.
 */
class ListaGastosActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_GASTO = "extra_gasto"
    }

    private lateinit var binding: ActivityListaGastosBinding
    private lateinit var adapter: GastoAdapter

    private var todosOsGastos: List<Gasto> = emptyList()
    private var categoriaSelecionada: CategoriaGasto? = null
    private var mesAtual: YearMonth = YearMonth.now()
    private val chipsPorCategoria = mutableMapOf<CategoriaGasto?, View>()

    private val formatadorMes =
        DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaGastosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarLista()
        configurarChipsDeCategoria()
        configurarNavegacaoDeMes()
        configurarSwipeParaExcluir()
        binding.fabAdicionar.setOnClickListener {
            startActivity(Intent(this, NovaDespesaActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega do repositório: um gasto pode ter sido adicionado (tela de
        // nova despesa) ou removido (tela de detalhe) desde a última vez que
        // esta tela apareceu.
        todosOsGastos = GastoRepository.listarTodos()
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

    private fun configurarNavegacaoDeMes() {
        binding.imageMesAnterior.setOnClickListener {
            mesAtual = mesAtual.minusMonths(1)
            atualizarInterface()
        }
        binding.imageProximoMes.setOnClickListener {
            mesAtual = mesAtual.plusMonths(1)
            atualizarInterface()
        }
    }

    /** Configura o "arrastar para excluir" no RecyclerView, com fundo vermelho e ícone de lixeira. */
    private fun configurarSwipeParaExcluir() {
        val corExclusao = ContextCompat.getColor(this, R.color.expense_value)
        val iconeExclusao = ContextCompat.getDrawable(this, R.drawable.ic_delete)
        val fundo = Paint().apply { color = corExclusao }

        val callback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val gasto = adapter.gastoNaPosicao(viewHolder.bindingAdapterPosition)
                excluirComOpcaoDeDesfazer(gasto)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                if (iconeExclusao == null) return
                val itemView = viewHolder.itemView
                val margem = (itemView.height - iconeExclusao.intrinsicHeight) / 2
                val top = itemView.top + margem
                val bottom = top + iconeExclusao.intrinsicHeight

                when {
                    dX > 0 -> {
                        c.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            dX, itemView.bottom.toFloat(), fundo
                        )
                        val left = itemView.left + margem
                        iconeExclusao.setBounds(left, top, left + iconeExclusao.intrinsicWidth, bottom)
                        iconeExclusao.draw(c)
                    }
                    dX < 0 -> {
                        c.drawRect(
                            itemView.right + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), fundo
                        )
                        val right = itemView.right - margem
                        iconeExclusao.setBounds(right - iconeExclusao.intrinsicWidth, top, right, bottom)
                        iconeExclusao.draw(c)
                    }
                }
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerGastos)
    }

    private fun excluirComOpcaoDeDesfazer(gasto: Gasto) {
        GastoRepository.remover(gasto)
        todosOsGastos = GastoRepository.listarTodos()
        atualizarInterface()

        Snackbar.make(binding.root, R.string.mensagem_gasto_removido, Snackbar.LENGTH_LONG)
            .setAction(R.string.acao_desfazer) {
                GastoRepository.adicionar(gasto)
                todosOsGastos = GastoRepository.listarTodos()
                atualizarInterface()
            }
            .show()
    }

    private fun atualizarInterface() {
        binding.textMesAtual.text = formatadorMes.format(mesAtual)
            .replaceFirstChar { it.uppercase() }

        val listaFiltrada = filtrarGastos()
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

    /** Filtra os gastos pelo mês exibido no cabeçalho e pela categoria selecionada nos chips. */
    private fun filtrarGastos(): List<Gasto> {
        return todosOsGastos.filter { gasto ->
            val mesDoGasto = YearMonth.from(LocalDate.parse(gasto.data, formatadorDeData))
            val noMesAtual = mesDoGasto == mesAtual
            val naCategoriaSelecionada = categoriaSelecionada == null || gasto.categoria == categoriaSelecionada
            noMesAtual && naCategoriaSelecionada
        }
    }

    private fun abrirDetalhe(gasto: Gasto) {
        val intent = Intent(this, DetalheGastoActivity::class.java)
        intent.putExtra(EXTRA_GASTO, gasto)
        startActivity(intent)
    }
}
