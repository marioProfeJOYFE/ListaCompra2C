package com.mrh.listacompra

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavBarValues(
    val icon: ImageVector,
    val label: String? = null,
    val destination: String
){
    HOME(
        icon = Icons.Filled.Home,
        label = "Inicio",
        destination = "home_view"
    ),
    GUARDADOS(
        icon = Icons.Filled.Favorite,
        label = "Guardados",
        destination = "guardados_view"
    )
}
