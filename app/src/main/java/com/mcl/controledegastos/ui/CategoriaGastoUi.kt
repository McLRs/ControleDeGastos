package com.mcl.controledegastos.ui

import com.mcl.controledegastos.R
import com.mcl.controledegastos.model.CategoriaGasto

/**
 * Mapeamento de cada [CategoriaGasto] para a sua representação visual
 * (cor e letra do "avatar"). Fica separado do modelo de dados de propósito:
 * o [CategoriaGasto] não precisa saber nada sobre cores ou Views.
 */
fun CategoriaGasto.corResId(): Int = when (this) {
    CategoriaGasto.ALIMENTACAO -> R.color.categoria_alimentacao
    CategoriaGasto.TRANSPORTE -> R.color.categoria_transporte
    CategoriaGasto.MORADIA -> R.color.categoria_moradia
    CategoriaGasto.LAZER -> R.color.categoria_lazer
    CategoriaGasto.SAUDE -> R.color.categoria_saude
    CategoriaGasto.OUTROS -> R.color.categoria_outros
}

fun CategoriaGasto.letraIcone(): String = rotulo.take(1).uppercase()
