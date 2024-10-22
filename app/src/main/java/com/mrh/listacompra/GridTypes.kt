package com.mrh.listacompra

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.ui.graphics.vector.ImageVector

enum class GridTypes(
    val id: Int,
    val title: String,
    val icon: ImageVector
)  {
    FILAS(
        id = 0,
        title = "Columnas",
        icon = Icons.Filled.TableRows
    ),
    GRID(
        id = 1,
        title = "Grid",
        icon = Icons.Filled.GridOn
    )
}