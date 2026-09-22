package com.mcl.controledegastos.data

import com.mcl.controledegastos.model.CategoriaGasto
import com.mcl.controledegastos.model.FormaPagamento
import com.mcl.controledegastos.model.Gasto

/**
 * Fonte de dados simulada (mock). Nesta etapa do projeto ainda não existe
 * API nem banco de dados: a lista abaixo faz esse papel temporariamente,
 * simulando gastos reais de um mês.
 */
object GastoMockData {

    fun listaMock(): List<Gasto> = listOf(
        Gasto(
            id = 1,
            descricao = "Almoço no restaurante",
            categoria = CategoriaGasto.ALIMENTACAO,
            valor = 45.90,
            data = "20/09/2026",
            formaPagamento = FormaPagamento.CARTAO_DEBITO
        ),
        Gasto(
            id = 2,
            descricao = "Uber para a faculdade",
            categoria = CategoriaGasto.TRANSPORTE,
            valor = 18.50,
            data = "19/09/2026",
            formaPagamento = FormaPagamento.PIX,
            observacao = "Corrida durante a chuva"
        ),
        Gasto(
            id = 3,
            descricao = "Aluguel do apartamento",
            categoria = CategoriaGasto.MORADIA,
            valor = 1200.00,
            data = "05/09/2026",
            formaPagamento = FormaPagamento.PIX
        ),
        Gasto(
            id = 4,
            descricao = "Cinema com amigos",
            categoria = CategoriaGasto.LAZER,
            valor = 60.00,
            data = "18/09/2026",
            formaPagamento = FormaPagamento.CARTAO_CREDITO,
            observacao = "Filme + pipoca"
        ),
        Gasto(
            id = 5,
            descricao = "Consulta odontológica",
            categoria = CategoriaGasto.SAUDE,
            valor = 150.00,
            data = "15/09/2026",
            formaPagamento = FormaPagamento.DINHEIRO
        ),
        Gasto(
            id = 6,
            descricao = "Supermercado da semana",
            categoria = CategoriaGasto.ALIMENTACAO,
            valor = 230.75,
            data = "14/09/2026",
            formaPagamento = FormaPagamento.CARTAO_DEBITO
        ),
        Gasto(
            id = 7,
            descricao = "Assinatura de streaming",
            categoria = CategoriaGasto.LAZER,
            valor = 39.90,
            data = "10/09/2026",
            formaPagamento = FormaPagamento.CARTAO_CREDITO
        ),
        Gasto(
            id = 8,
            descricao = "Gasolina",
            categoria = CategoriaGasto.TRANSPORTE,
            valor = 120.00,
            data = "08/09/2026",
            formaPagamento = FormaPagamento.DINHEIRO,
            observacao = "Tanque cheio"
        ),
        Gasto(
            id = 9,
            descricao = "Farmácia",
            categoria = CategoriaGasto.SAUDE,
            valor = 54.30,
            data = "07/09/2026",
            formaPagamento = FormaPagamento.PIX
        ),
        Gasto(
            id = 10,
            descricao = "Conta de internet",
            categoria = CategoriaGasto.OUTROS,
            valor = 99.90,
            data = "03/09/2026",
            formaPagamento = FormaPagamento.PIX
        )
    )
}
