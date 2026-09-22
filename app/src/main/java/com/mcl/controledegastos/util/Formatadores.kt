package com.mcl.controledegastos.util

import java.text.NumberFormat
import java.util.Locale

/** Formata um valor em reais, ex.: 45.9 -> "R$ 45,90". */
fun Double.paraMoeda(): String {
    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return formatador.format(this)
}
