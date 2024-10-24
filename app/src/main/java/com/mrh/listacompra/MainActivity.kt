package com.mrh.listacompra

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
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

    private val categorias: List<String> = listOf(
        Categoria.FRUTERIA.nombre,
        Categoria.CARNICERIA.nombre,
        Categoria.POLLERIA.nombre,
        Categoria.PESCADERIA.nombre,
        Categoria.VERDULERIA.nombre,
        Categoria.OTROS.nombre,
        Categoria.LIMPIEZA.nombre,
        Categoria.HIGINE.nombre,
        Categoria.LACTEOS.nombre,
        Categoria.BOLLLERIA.nombre
    )

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
                                    navController.navigate(NavBarValues.FORMULARIO_LISTA.destination)
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
                    val posicion = direccion.arguments!!.getString("posicion").toString().toInt()
                    val lista = getListFromViewModel(viewModel)[posicion]
                    ListaCompraView(
                        navController = navController,
                        viewModel = viewModel,
                        lista = lista
                    )
                }
                composable(NavBarValues.FORMULARIO_PRODUCTO.destination) { direccion ->
                    val posicion = direccion.arguments!!.getString("posicion").toString().toInt()
                    FormularioProductoView(
                        navController = navController,
                        viewModel = viewModel,
                        id = posicion
                    )
                }

                composable(NavBarValues.FORMULARIO_LISTA.destination) {
                    FormularioListaView(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }


            composable(NavBarValues.GUARDADOS.destination) {
                Text("hola")
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("SimpleDateFormat")
    @Composable
    fun ListasView(
        viewModel: ListasCompraViewModel,
        navController: NavHostController
    ) {
        var busqueda by remember { mutableStateOf("") }
        val listas = getListFromViewModel(viewModel)
        val options = mutableListOf(GridTypes.FILAS, GridTypes.GRID)
        var selectedIndex by remember { mutableIntStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                label = { Text("Buscar lista") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )


            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, option ->
                    val selected = selectedIndex == index
                    SegmentedButton(
                        selected = selected,
                        onClick = {
                            selectedIndex = index
                        },
                        label = { Text(option.title) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        icon = {
                            Icon(imageVector = option.icon, "")
                        }
                    )
                }
            }

            if (listas.isEmpty()) {
                Text("No hay listas")

            } else {
                when (selectedIndex) {
                    0 -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        listas.filter { lista ->
                            lista.nombre.uppercase().contains(busqueda.uppercase())
                        }.forEach { lista ->
                            Card(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp)
                                    .fillMaxWidth()
                                    .height(100.dp),
                                onClick = {
                                    navController.navigate(
                                        "lista_compra_view/" + listas.indexOf(
                                            lista
                                        )
                                    )
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = lista.nombre,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Creado el: " + SimpleDateFormat("dd/MM/yyyy").format(
                                            lista.dia_creacion
                                        )
                                    )
                                }
                            }
                        }

                    }

                    1 -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(listas.filter { lista ->
                            lista.nombre.uppercase().contains(busqueda.uppercase())
                        }) { lista ->
                            Card(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp)
                                    .fillMaxWidth()
                                    .height(100.dp),
                                onClick = {
                                    navController.navigate(
                                        "lista_compra_view/" + listas.indexOf(
                                            lista
                                        )
                                    )
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = lista.nombre,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Creado el: " + SimpleDateFormat("dd/MM/yyyy").format(
                                            lista.dia_creacion
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun ListaCompraView(
        navController: NavHostController,
        viewModel: ListasCompraViewModel,
        lista: ListaCompra
    ) {
        var busqueda by remember { mutableStateOf("") }
        var selectedCategories by remember { mutableStateOf(setOf<String>()) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                label = { Text("Buscar lista") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                categorias.forEach { category ->
                    FilterChip(
                        onClick = {
                            selectedCategories = if (category in selectedCategories) {
                                selectedCategories - category
                            } else {
                                selectedCategories + category
                            }
                        },
                        label = { Text(category) },
                        selected = category in selectedCategories,
                        modifier = Modifier.padding(end = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (category in selectedCategories) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        ),
                        trailingIcon = {
                            if (category in selectedCategories) {
                                Icon(Icons.Filled.Clear, "")
                            }
                        }
                    )
                }
            }
            FilledTonalButton(
                onClick = {
                    navController.navigate(
                        NavBarValues.FORMULARIO_PRODUCTO.destination.replace(
                            oldValue = "{posicion}",
                            newValue = getListFromViewModel(viewModel).indexOf(lista)
                                .toString()
                        )
                    )
                },
                modifier = Modifier
                    .padding(8.dp)
                    .height(60.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                Text(text = "Añadir lista")
            }

            lista.productos.filter { producto ->
                producto.nombre.uppercase().contains(busqueda.uppercase())
            }
                .filter { if (selectedCategories.isNotEmpty()) it.categoria in selectedCategories else true }
                .forEach { producto ->
                    ProductoCard(producto = producto)
                }
        }

    }

    @Composable
    fun ProductoCard(producto: Producto) {
        var comprado by remember { mutableStateOf(producto.comprado) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .height(100.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Checkbox(
                        checked = comprado,
                        onCheckedChange = {
                            producto.comprado = it
                            comprado = it
                        }
                    )
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = producto.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textDecoration = if (comprado) TextDecoration.LineThrough else TextDecoration.None
                        )
                        Text(
                            text = "Cantidad: " + producto.cantidad.toString(),
                            textDecoration = if (comprado) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }
                }

                Text(
                    text = "Precio: " + producto.precio.toString() + "€",
                    modifier = Modifier.padding(end = 10.dp),
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (comprado) TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }
    }

    @Composable
    fun FormularioProductoView(
        navController: NavHostController,
        viewModel: ListasCompraViewModel,
        id: Int
    ) {

        var nombre by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }
        var cantidad by remember { mutableStateOf("") }
        var categoria by remember { mutableStateOf("") }
        var mostrarLista by remember { mutableStateOf(false) }
        var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                    },
                    label = { Text("Nombre") },
                    modifier = Modifier.width(180.dp)
                )
                TextField(
                    value = precio,
                    onValueChange = {
                        precio = it
                    },
                    label = { Text("Precio") },
                    modifier = Modifier.width(180.dp),
                    suffix = {
                        Text("€")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextField(
                    value = cantidad,
                    onValueChange = {
                        cantidad = it
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.width(180.dp)
                )
                Column {
                    TextField(
                        value = categoria,
                        readOnly = true,
                        onValueChange = {
                            categoria = it
                        },
                        label = { Text("Categoria") },
                        modifier = Modifier
                            .width(180.dp)
                            .onGloballyPositioned { coordinates ->
                                // This value is used to assign to
                                // the DropDown the same width
                                mTextFieldSize = coordinates.size.toSize()
                            },
                        trailingIcon = {
                            IconButton(onClick = {
                                mostrarLista = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    DropdownMenu(
                        expanded = mostrarLista,
                        onDismissRequest = { mostrarLista = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                    ) {
                        categorias.forEach { label ->
                            DropdownMenuItem(
                                onClick = {
                                    categoria = label
                                    mostrarLista = false
                                },
                                text = {
                                    Text(label)
                                }
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                    val listas = getListFromViewModel(viewModel)
                    val lista = listas[id]
                    val productos = ArrayList(lista.productos)
                    productos.add(
                        Producto(
                            nombre = nombre,
                            precio = precio.toDouble(),
                            cantidad = cantidad.toInt(),
                            categoria = categoria
                        )
                    )
                    lista.productos = productos
                    FileUtils.agregarProducto(this@MainActivity, lista, id)
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Añadir Producto")
            }
        }
    }


    @Composable
    fun FormularioListaView(
        navController: NavHostController,
        viewModel: ListasCompraViewModel,
    ) {
        var nombre by remember { mutableStateOf("") }
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                },
                label = { Text("Nombre") }
            )
            Button(
                onClick = {
                    val listas = getListFromViewModel(viewModel)
                    listas.add(
                        ListaCompra(
                            nombre = nombre,
                            dia_creacion = Date.from(Instant.now()),
                            productos = ArrayList()
                        )
                    )
                    viewModel.listas = MutableLiveData(listas)
                    FileUtils.agregarListaCompra(this@MainActivity, listas.last())
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Añadir Producto")
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

    private fun getListFromViewModel(viewModel: ListasCompraViewModel): ArrayList<ListaCompra> {
        if (viewModel.listas.value === null) {
            viewModel.listas = MutableLiveData(FileUtils.recuperarListasCompra(this))
        }

        return viewModel.listas.value as ArrayList<ListaCompra>
    }
}