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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                        if (navBackStackEntry.value?.destination?.route == NavBarValues.HOME.destination) {
                            FloatingActionButton(
                                onClick = {
                                    // TODO: Ir a formulario de lista nueva navController.navigate(NavBarValues.FORMULARIO_PRODUCTO.destination)
                                }
                            ) {
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
            composable(NavBarValues.LISTAS.destination) { direccion ->
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
            composable(NavBarValues.FORMULARIO_PRODUCTO.destination) { direccion ->
                val posicion = direccion.arguments!!.getInt("posicion")
                FormularioProductoView(navController = navController, viewModel = viewModel, id = posicion)
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
    var busqueda by remember { mutableStateOf("") }
    val listas = getListFromViewModel(viewModel)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = { Text("Buscar lista") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        listas.filter { lista -> lista.nombre.uppercase().contains(busqueda.uppercase()) }.forEach { lista ->
            Card(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
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

@Composable
fun ListaCompraView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ListasCompraViewModel,
    lista: ListaCompra
) {
    var busqueda by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = { Text("Buscar lista") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp)
        )
        FilledTonalButton(
            onClick = {
                navController.navigate(NavBarValues.FORMULARIO_PRODUCTO.destination.replace(oldValue = "{posicion}", newValue = getListFromViewModel(viewModel).indexOf(lista)
                    .toString()))
            },
            modifier = Modifier.padding(8.dp).height(60.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            Text(text = "Añadir lista")
        }
        lista.productos.filter { producto -> producto.nombre.uppercase().contains(busqueda.uppercase()) }.forEach { producto ->
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

@Composable
fun FormularioProductoView(navController: NavHostController, viewModel: ListasCompraViewModel, id: Int){

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Nombre") }
        )

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

