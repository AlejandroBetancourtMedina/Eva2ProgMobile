package com.example.listacompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listacompras.db.AppDatabase
import com.example.listacompras.db.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //consumiendo recursos string para idioma en
        resources.getString(R.string.app_name)
        resources.getString(R.string.boton_agregar)

        setContent {
            ListaProductosUI()

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosUI() {
    val contexto = LocalContext.current
    val (productos, setProductos) = remember { mutableStateOf(emptyList<Producto>()) }
    val nuevoProducto = remember { mutableStateOf("") }

    val alcanceCorrutins = rememberCoroutineScope()

    LaunchedEffect(productos) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(contexto).productoDao()
            setProductos(dao.findAll())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(color= Color.Cyan)
    ) {
        // Agregar un TextField para ingresar nuevos productos
        TextField(
            value = nuevoProducto.value,
            onValueChange = { nuevoProducto.value = it },
            label = { Text("Nuevo producto", color = Color.Black) },
            colors = TextFieldDefaults.textFieldColors(textColor = Color.Black),
            modifier = Modifier.padding(16.dp)
        )

        // botón para agregar productos
        Button(
            onClick = {
                alcanceCorrutins.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(contexto).productoDao()
                    val nuevo = Producto(0, nuevoProducto.value, false)
                    dao.insertar(nuevo)
                    setProductos(dao.findAll())
                    nuevoProducto.value = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Agregar")
        }


        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(productos) { producto ->
                ProdUI(producto) {
                    setProductos(emptyList<Producto>())
                }
            }
        }
    }
}



@Composable
fun ProdUI(producto:Producto, onSave:() -> Unit = {}){
    val contexto = LocalContext.current
    val alcanceCorrutins = rememberCoroutineScope()


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        if(producto.realizada){
            //icono que se cambia de estado a producto comprado al hacer click
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Producto comprado",
                modifier = Modifier.clickable {
                    alcanceCorrutins.launch(Dispatchers.IO)  {
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        producto.realizada = false
                        dao.actualizar(producto)
                        onSave()
                    }
                }
            )

    }else{
        Icon(
            //icono para producto no comprado
            Icons.Filled.AddCircle,
            contentDescription = "Producto No Comprado",
                modifier = Modifier.clickable {
                alcanceCorrutins.launch(Dispatchers.IO)  {
                    val dao = AppDatabase.getInstance(contexto).productoDao()
                    producto.realizada = true
                    dao.actualizar(producto)
                    onSave()
                }
            }
        )

    }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = producto.producto,
            modifier = Modifier.weight(2f)

        )
        //icono para eliminar producto, incluido de la BD
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar Producto",
                modifier = Modifier.clickable {
                alcanceCorrutins.launch(Dispatchers.IO)  {
                    val dao = AppDatabase.getInstance(contexto).productoDao()
                    dao.eliminar(producto)
                    onSave()
                }
            }
        )

    }
}

