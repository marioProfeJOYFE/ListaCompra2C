package com.mrh.listacompra

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mrh.listacompra.ui.theme.ListaCompraTheme
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Instant

class MainActivity : ComponentActivity() {
    val idListSelected: Int = 0

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ListasCompraViewModel()
            val navController = rememberNavController()
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val itemsNavBar = listOf(NavBarValues.HOME, NavBarValues.GUARDADOS)
            ListaCompraTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "Lista de Compras")
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            ),
                            navigationIcon = {
                                if (navBackStackEntry.value?.destination?.route != NavBarValues.HOME.destination && navBackStackEntry.value?.destination?.route != NavBarValues.GUARDADOS.destination) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = ""
                                        )
                                    }
                                }

                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            itemsNavBar.forEach { item ->
                                NavigationBarItem(
                                    selected = navBackStackEntry.value?.destination?.hierarchy?.any { it.route == item.main_route } == true,
                                    onClick = { navController.navigate(item.destination) },
                                    icon = {
                                        item.icon?.let { Icon(it, "") }
                                    },
                                    label = {
                                        Text(text = item.label ?: "")
                                    }
                                )
                            }
                        }

                    },
                    floatingActionButton = {
                        if(navBackStackEntry.value?.destination?.route == NavBarValues.LISTAS.destination) {
                            FloatingActionButton(onClick = { }) {
                                Icon(Icons.Filled.Add, "")
                            }
                        }

                    }
                ) { innerPadding ->
                    NavigationHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ListasCompraViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "listas",
        modifier = modifier
    ) {

        navigation(
            startDestination = NavBarValues.HOME.destination,
            route = "listas"
        ) {
            composable(NavBarValues.HOME.destination) {
                ListasView(viewModel = viewModel, navController = navController)
            }
            composable("lista_compra_view/{posicion}") { direccion ->
                // Obtenemos el valor de la posición de la lista, pasado como {posicion}
                val posicion = direccion.arguments?.getInt("posicion")
                // Al existir la posibilidad de que venga null, tenemos que escribir estas lineas para
                // evitar errores
                posicion?.let {
                    getListFromViewModel(viewModel).get(
                        it
                    )
                }?.let {
                    ListaCompraView(
                        navController = navController,
                        viewModel = viewModel,
                        lista = it
                    )
                }
            }
        }


        composable(NavBarValues.GUARDADOS.destination) {
            Text("hola")
        }

    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ListasView(
    modifier: Modifier = Modifier,
    viewModel: ListasCompraViewModel,
    navController: NavHostController
) {
    val listas = getListFromViewModel(viewModel)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        listas.forEach { lista ->
            Card(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                onClick = {
                    navController.navigate("lista_compra_view/" + listas.indexOf(lista))
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = lista.nombre, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Creado el: " + SimpleDateFormat("dd/MM/yyyy").format(lista.dia_creacion))
                }
            }
        }
    }
}

@Preview
@Composable
fun ListasViewPreview() {
    val viewModel = ListasCompraViewModel()
    //ListasView(viewModel = viewModel)
}

@Composable
fun ListaCompraView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ListasCompraViewModel,
    lista: ListaCompra
) {
    Column(modifier = modifier) {
        lista.productos.forEach { producto ->
            ProductoCard(producto = producto)
        }
    }

}

@Composable
fun ProductoCard(producto: Producto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 8.dp, bottom = 8.dp, end = 10.dp)
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = producto.nombre, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Cantidad: " + producto.cantidad.toString())
            }
            Text(
                text = "Precio: " + producto.precio.toString() + "€",
                modifier = Modifier.padding(end = 10.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}


fun cargarListas(): ArrayList<ListaCompra> {
    val listas = ArrayList<ListaCompra>()
    val productos = listOf(
        Producto(
            nombre = "Manzanas",
            precio = 1.99,
            cantidad = 5,
            categoria = "Frutas"
        ),
        Producto(
            nombre = "Pan",
            precio = 0.99,
            cantidad = 2,
            categoria = "Panadería"
        ),
        Producto(
            nombre = "Leche",
            precio = 2.49,
            cantidad = 1,
            categoria = "Lácteos"
        )
    )
    listas.add(
        ListaCompra(
            nombre = "Lista de la compra de la cena",
            dia_creacion = Date.from(Instant.now()),
            productos = productos
        )
    )
    return listas
}

fun getListFromViewModel(viewModel: ListasCompraViewModel): ArrayList<ListaCompra> {
    if (viewModel.listas.value === null) {
        viewModel.listas = MutableLiveData(cargarListas())
    }

    return viewModel.listas.value as ArrayList<ListaCompra>
}

