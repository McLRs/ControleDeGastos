package com.mcl.controledegastos.data

import com.mcl.controledegastos.model.Gasto

/**
 * Guarda a lista de gastos enquanto o app está aberto.
 *
 * Esta etapa do projeto ainda não usa banco de dados (isso é assunto da
 * Etapa 2, com Room) — então, em vez de cada tela ter a sua própria cópia
 * da lista, este `object` (um singleton: existe uma única instância dele
 * em todo o app) guarda a lista de verdade. A tela de lista e a tela de
 * nova despesa usam o mesmo repositório, então adicionar ou remover um
 * gasto em uma tela aparece imediatamente para a outra.
 *
 * Importante: como é um `object` guardado só na memória, os dados voltam
 * a ser os do [GastoMockData] sempre que o app é reaberto do zero — não há
 * persistência em disco nesta etapa.
 */
object GastoRepository {

    private val gastos: MutableList<Gasto> = GastoMockData.listaMock().toMutableList()

    private var proximoId: Int = (gastos.maxOfOrNull { it.id } ?: 0) + 1

    /** Retorna uma cópia da lista atual (quem recebe não consegue alterar o original). */
    fun listarTodos(): List<Gasto> = gastos.toList()

    /** Gera um id novo e único, usado ao criar um [Gasto] na tela de nova despesa. */
    fun proximoIdDisponivel(): Int = proximoId++

    /** Adiciona um gasto novo no início da lista (fica visível no topo). */
    fun adicionar(gasto: Gasto) {
        gastos.add(0, gasto)
    }

    /** Remove um gasto existente (comparando pelo [Gasto.id], que é único). */
    fun remover(gasto: Gasto) {
        gastos.removeAll { it.id == gasto.id }
    }
}
