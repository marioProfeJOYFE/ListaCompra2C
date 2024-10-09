package com.mrh.listacompra

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavBarValues(
    val icon: ImageVector? = null,
    val label: String? = null,
    val destination: String,
    val main_route: String
){
    HOME(
        icon = Icons.Filled.Home,
        label = "Inicio",
        destination = "home_view",
        main_route = "listas"
    ),
    LISTAS(
        destination = "lista_compra_view/{posicion}",
        main_route = "listas"
    ),
    FORMULARIO_PRODUCTO(
        destination = "formulario_producto_view/{posicion}",
        main_route = "listas"
    ),
    GUARDADOS(
        icon = Icons.Filled.Favorite,
        label = "Guardados",
        destination = "guardados_view",
        main_route = "guardados_view"
    )
}
