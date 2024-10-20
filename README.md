# Lista de la compra
Vamos a desarrollar una aplicación para guardar listas de la compra, y va a constar de las siguientes vistas:

| Vista       | Imagen      |
| ----------- | ----------- |
| ListView    |  <img src="https://github.com/user-attachments/assets/dcf66361-5cbf-4f07-bea9-cb2595c3c1e5" height="500"> |
| HomeView    | <img src="https://github.com/user-attachments/assets/e032d1d7-e6f1-4510-8d3b-390105fc15c3" height="500"> |
| FormularioListaView    | <img src="https://github.com/user-attachments/assets/18b5725c-3999-427b-9812-f71c60aa3892" height="500"> |
| FormularioProductoView    | <img src="https://github.com/user-attachments/assets/090b170e-e0b1-4613-a925-c875537f94dc" height="500"> |

### Necesitaremos hacer uso del tipo viewModel para todos nuestros datos
Ejemplo

```kotlin
class ListaCompraViewModel : ViewModel() {
    private val _lista = MutableLiveData<List<ListaCompra>>()
    var lista : LiveData<List<ListaCompra>> = _lista
}
```

### Recordar instanciar la clase en el MainActivity


## Como guardar los datos en el dispositivo

Vamos a guardar los datos en el dispositivo de forma que al cerrar la aplicación se conserven nuestros cambios.

Antes de empezar, necesitamos añadir al build.gradle.kts(Module: App) la siguiente dependencia:
```kotlin
implementation(libs.gson)
// Si no funciona añadir la siguiente
implementation("com.google.code.gson:gson:2.11.0")
```

### 1º Crear un objeto que se encarge de crear, leer y borrar datos del fichero.

Yo he nombrado la clase FileUtils.kt
```kotlin
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

object FileUtils {

    fun recuperarListasCompra(context: Context): List<ListaCompra> {
        val gson = Gson()
        val file = File(context.cacheDir, "listas.json")

        return if (file.exists()) {
            val jsonString = file.readText()
            val listType: Type = object : TypeToken<List<ListaCompra>>() {}.type
            gson.fromJson<List<ListaCompra>>(jsonString, listType)
        } else {
            ArrayList(emptyList())
        }
    }

    fun agregarListaCompra(context: Context, lista: ListaCompra) {
        val gson = Gson()
        val file = File(context.cacheDir, "listas.json")
        val listas = recuperarListasCompra(context).toMutableList()
        listas.add(lista)
        val jsonString = gson.toJson(listas)
        file.writeText(jsonString)
    }

    fun eliminarListaCompra(context: Context, lista: ListaCompra) {
        val gson = Gson()
        val file = File(context.cacheDir, "listas.json")
        val listas = recuperarListasCompra(context).toMutableList()
        listas.remove(lista)
        val jsonString = gson.toJson(listas)
        file.writeText(jsonString)
    }

    fun agregarProducto(context: Context, lista: ListaCompra, posicion: Int) {
        val gson = Gson()
        val file = File(context.cacheDir, "listas.json")
        val listas = recuperarListasCompra(context).toMutableList()
        listas[posicion].productos = lista.productos
        val jsonString = gson.toJson(listas)
        file.writeText(jsonString)
    }

}
```
### 2º Paso: Sustituir la llamada a cargarDatos que creaba datos falsos por la llamada a la lectura del fichero:
#### En caso de que al escribir this en el metodo recuperarListaCompra de error, comprobar que todas nuestras funciones están dentro de la clase MainActivity, cuidado con las llaves de cierre.

```kotlin
fun getListFromViewModel(viewModel: ListasCompraViewModel): ArrayList<ListaCompra> {
        if (viewModel.listas.value === null) {
            viewModel.listas = MutableLiveData(FileUtils.recuperarListasCompra(this))
        }

        return viewModel.listas.value as ArrayList<ListaCompra>
    }
```

### Ultimo paso: Al crear una lista o añadir un producto, guardarlo también en el fichero

Ejemplo boton que crea una nueva ListaCompra
```kotlin
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
        // Esta es la implementacion nueva
        FileUtils.agregarListaCompra(this@MainActivity, listas.last())
        navController.popBackStack()
    },
    modifier = Modifier.padding(top = 10.dp)
) {
    Text("Crear Lista")
}
```

Ejemplo boton que añade un nuevo Producto a una ListaCompra
```kotlin
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
        // Esta es la implementacion nueva
        FileUtils.agregarProducto(this@MainActivity, lista, id)
        navController.popBackStack()
    },
    modifier = Modifier.padding(top = 10.dp)
) {
    Text("Añadir Producto")
}
```
