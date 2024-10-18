package com.mrh.listacompra

import java.util.Date

data class ListaCompra(
    val nombre: String,
    val dia_creacion: Date,
    var productos: List<Producto>,
    val supermercado: String? = null,
)
