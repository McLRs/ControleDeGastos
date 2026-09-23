package com.mcl.controledegastos.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mcl.controledegastos.R
import com.mcl.controledegastos.databinding.ItemGastoBinding
import com.mcl.controledegastos.model.Gasto
import com.mcl.controledegastos.ui.corResId
import com.mcl.controledegastos.ui.letraIcone
import com.mcl.controledegastos.util.paraMoeda

/**
 * Adapter do RecyclerView da lista de gastos. Recebe a lista atual de
 * [Gasto] e uma função de callback chamada quando o usuário toca em um item
 * (usada pela Activity para abrir a tela de detalhe).
 */
class GastoAdapter(
    private var gastos: List<Gasto>,
    private val aoClicarNoItem: (Gasto) -> Unit
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    inner class GastoViewHolder(val binding: ItemGastoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val binding = ItemGastoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GastoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastos[position]
        val binding = holder.binding

        binding.textLetraCategoria.text = gasto.categoria.letraIcone()
        binding.imageCirculoCategoria.setColorFilter(
            ContextCompat.getColor(binding.root.context, gasto.categoria.corResId())
        )
        binding.textDescricao.text = gasto.descricao
        binding.textCategoriaData.text = binding.root.context.getString(
            R.string.formato_categoria_data, gasto.categoria.rotulo, gasto.data
        )
        binding.textValor.text = gasto.valor.paraMoeda()

        binding.root.setOnClickListener { aoClicarNoItem(gasto) }
    }

    override fun getItemCount(): Int = gastos.size

    /** Substitui a lista exibida (usado ao trocar o filtro de categoria). */
    fun atualizarLista(novaLista: List<Gasto>) {
        gastos = novaLista
        notifyDataSetChanged()
    }
}
