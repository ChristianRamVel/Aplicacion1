package com.example.aplicacion1

import android.provider.BaseColumns

object LocationContract {
    object LocationEntry : BaseColumns {
        const val TABLE_NAME = "ubicaciones"
        const val COLUMN_LATITUD = "latitud"
        const val COLUMN_LONGITUD = "longitud"
        const val COLUMN_DESCRIPCION = "descripcion"
    }
}