package com.mcl.controledegastos.model

import java.io.Serializable

/**
 * Categorias possíveis de um gasto. Um enum garante que só existam essas
 * opções — não é possível criar uma categoria "inválida" por engano.
 */
enum class CategoriaGasto(val rotulo: String) {
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    MORADIA("Moradia"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    OUTROS("Outros")
}

/** Formas de pagamento possíveis de um gasto. */
enum class FormaPagamento(val rotulo: String) {
    DINHEIRO("Dinheiro"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    PIX("Pix")
}

/**
 * Representa um gasto pessoal.
 *
 * É um modelo imutável: uma `data class` cujos campos são todos `val`.
 * Depois de criado, um [Gasto] não muda — se algo precisar ser diferente,
 * criamos uma cópia com [copy] em vez de alterar o objeto original.
 *
 * [observacao] é um valor opcional: nem todo gasto tem uma nota extra, por
 * isso o tipo é `String?` (aceita nulo) e o valor padrão já é `null`.
 * Implementa [Serializable] para poder ser passado como extra de um
 * [android.content.Intent] entre as telas do aplicativo.
 *
 * [parcelaAtual] e [totalParcelas] representam uma compra parcelada: uma
 * compra em 6x, por exemplo, vira 6 objetos [Gasto] (um por mês), cada um
 * com o valor da parcela e [totalParcelas] = 6. Uma compra à vista (a
 * grande maioria) simplesmente tem [totalParcelas] = 1, e a tela não
 * mostra nenhuma informação extra de parcela nesse caso.
 */
data class Gasto(
    val id: Int,
    val descricao: String,
    val categoria: CategoriaGasto,
    val valor: Double,
    val data: String,
    val formaPagamento: FormaPagamento,
    val observacao: String? = null,
    val parcelaAtual: Int = 1,
    val totalParcelas: Int = 1
) : Serializable {
    /** true quando este gasto é uma das parcelas de uma compra parcelada. */
    val eParcelado: Boolean
        get() = totalParcelas > 1
}
