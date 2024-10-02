package com.mrh.listacompra

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListasCompraViewModel : ViewModel() {
    val _listas = MutableLiveData<List<ListaCompra>>()
    var listas : LiveData<List<ListaCompra>> = _listas
}