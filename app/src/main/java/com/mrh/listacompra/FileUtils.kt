package com.mrh.listacompra

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