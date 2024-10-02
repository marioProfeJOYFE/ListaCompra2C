# Lista de la compra
Vamos a desarrollar una aplicación para guardar listas de la compra, y va a constar de las siguientes vistas:

| Vista       | Imagen      |
| ----------- | ----------- |
| ListView    |  <img src="https://github.com/user-attachments/assets/efd53918-7b7c-46c4-8f20-08ade822df25" height="500"> |
| HomeView    | <img src="https://github.com/user-attachments/assets/3ead4120-2338-4319-9fef-82ebb3fa593a" height="500"> |
| FormularioView    | <img src="https://github.com/user-attachments/assets/bc7a102f-c1fa-493e-bd3f-8f794ad7befe" height="500"> |

### Necesitaremos hacer uso del tipo viewModel para todos nuestros datos
Ejemplo

```kotlin
class ListaCompraViewModel : ViewModel() {
    private val _lista = MutableLiveData<List<Producto>>()
    var lista : LiveData<List<Producto>> = _lista
}
```

### Recordar instanciar la clase en el MainActivity
