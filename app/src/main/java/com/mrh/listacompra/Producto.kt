package com.mrh.listacompra

data class Producto(
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val categoria: String? = null,
)
