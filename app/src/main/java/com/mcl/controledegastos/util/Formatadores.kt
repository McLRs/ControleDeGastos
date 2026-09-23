package com.mcl.controledegastos.util

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formata um valor em reais, ex.: 45.9 -> "R$ 45,90". */
fun Double.paraMoeda(): String {
    val formatador = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return formatador.format(this)
}

/**
 * Formato de data usado em todo o app ("dd/MM/yyyy", ex.: "20/09/2026").
 * O campo [com.mcl.controledegastos.model.Gasto.data] é guardado como texto
 * nesse formato; este formatador é o único lugar que sabe disso, então tanto
 * a leitura (ex.: para descobrir o mês de um gasto) quanto a escrita (ex.: o
 * seletor de data da tela de nova despesa) usam sempre o mesmo padrão.
 */
val formatadorDeData: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
