package com.mcl.controledegastos.data

import com.mcl.controledegastos.model.CategoriaGasto
import com.mcl.controledegastos.model.FormaPagamento
import com.mcl.controledegastos.model.Gasto
import com.mcl.controledegastos.util.formatadorDeData
import java.time.LocalDate

/**
 * Fonte de dados simulada (mock). Nesta etapa do projeto ainda não existe
 * API nem banco de dados: a lista abaixo faz esse papel temporariamente,
 * simulando gastos reais de um mês.
 *
 * As datas são calculadas em relação ao dia de hoje (em vez de datas fixas)
 * para que a tela de lista, que mostra o mês atual por padrão, sempre tenha
 * gastos para exibir — não importa em que dia o app for aberto.
 */
object GastoMockData {

    private val hoje: LocalDate = LocalDate.now()

    private fun diasAtras(dias: Long): String = formatadorDeData.format(hoje.minusDays(dias))

    fun listaMock(): List<Gasto> = listOf(
        Gasto(
            id = 1,
            descricao = "Almoço no restaurante",
            categoria = CategoriaGasto.ALIMENTACAO,
            valor = 45.90,
            data = diasAtras(0),
            formaPagamento = FormaPagamento.CARTAO_DEBITO
        ),
        Gasto(
            id = 2,
            descricao = "Uber para a faculdade",
            categoria = CategoriaGasto.TRANSPORTE,
            valor = 18.50,
            data = diasAtras(1),
            formaPagamento = FormaPagamento.PIX,
            observacao = "Corrida durante a chuva"
        ),
        Gasto(
            id = 3,
            descricao = "Aluguel do apartamento",
            categoria = CategoriaGasto.MORADIA,
            valor = 1200.00,
            data = diasAtras(15),
            formaPagamento = FormaPagamento.PIX
        ),
        Gasto(
            id = 4,
            descricao = "Cinema com amigos",
            categoria = CategoriaGasto.LAZER,
            valor = 60.00,
            data = diasAtras(2),
            formaPagamento = FormaPagamento.CARTAO_CREDITO,
            observacao = "Filme + pipoca"
        ),
        Gasto(
            id = 5,
            descricao = "Consulta odontológica",
            categoria = CategoriaGasto.SAUDE,
            valor = 150.00,
            data = diasAtras(5),
            formaPagamento = FormaPagamento.DINHEIRO
        ),
        Gasto(
            id = 6,
            descricao = "Supermercado da semana",
            categoria = CategoriaGasto.ALIMENTACAO,
            valor = 230.75,
            data = diasAtras(6),
            formaPagamento = FormaPagamento.CARTAO_DEBITO
        ),
        Gasto(
            id = 7,
            descricao = "Assinatura de streaming",
            categoria = CategoriaGasto.LAZER,
            valor = 39.90,
            data = diasAtras(10),
            formaPagamento = FormaPagamento.CARTAO_CREDITO
        ),
        Gasto(
            id = 8,
            descricao = "Gasolina",
            categoria = CategoriaGasto.TRANSPORTE,
            valor = 120.00,
            data = diasAtras(12),
            formaPagamento = FormaPagamento.DINHEIRO,
            observacao = "Tanque cheio"
        ),
        Gasto(
            id = 9,
            descricao = "Farmácia",
            categoria = CategoriaGasto.SAUDE,
            valor = 54.30,
            data = diasAtras(13),
            formaPagamento = FormaPagamento.PIX
        ),
        Gasto(
            id = 10,
            descricao = "Conta de internet",
            categoria = CategoriaGasto.OUTROS,
            valor = 99.90,
            data = diasAtras(17),
            formaPagamento = FormaPagamento.PIX
        ),
        Gasto(
            id = 11,
            descricao = "Notebook novo",
            categoria = CategoriaGasto.OUTROS,
            valor = 450.00,
            data = diasAtras(4),
            formaPagamento = FormaPagamento.CARTAO_CREDITO,
            observacao = "Comprado em 3x, parcela 2 de 3",
            parcelaAtual = 2,
            totalParcelas = 3
        )
    )
}
