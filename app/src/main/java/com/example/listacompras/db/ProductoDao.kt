package com.example.listacompras.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//Usando metodo Dao, para poder interactuar con la base de datos y hacer CRUD en Kotlin
@Dao
interface ProductoDao {

    @Query("SELECT * FROM producto ORDER BY  realizada DESC")
    fun findAll(): List<Producto>

    @Query("SELECT COUNT(*) FROM producto")
    fun contar(): Int

    @Insert
    fun insertar(producto:Producto):Long

    @Update
    fun actualizar(producto: Producto)

    @Delete
    fun eliminar(producto: Producto)

}