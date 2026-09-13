package com.example.todo_barato

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Conexión a la base de datos SQLite
        val admin = AdminSQLiteOpenHelper(this)
        val bd = admin.writableDatabase

        // 2. Limpiar la tabla previa para evitar duplicados de clave primaria al reiniciar
        bd.execSQL("DELETE FROM ventas")

        // 3. Lista con los 5 registros requeridos
        val listaVentas = listOf(
            Venta("xyz001", "laptop gamer hp", 3000.0, 1, "factura", "20/07/2026"),
            Venta("xyz002", "teclado", 50.0, 1, "boleta", "14/08/2026"),
            Venta("xyz003", "mouse inalambrico", 35.0, 2, "boleta", "15/08/2026"),
            Venta("xyz004", "luces led", 150.0, 1, "factura", "18/08/2026"),
            Venta("xyz005", "audifonos gamer", 120.0, 1, "boleta", "22/08/2026")
        )

        // 4. Inserción de registros en la tabla 'ventas'
        for (venta in listaVentas) {
            val registro = ContentValues().apply {
                put("codigo", venta.codigo)
                put("nombre", venta.nombre)
                put("precio", venta.precio)
                put("cantidad", venta.cantidad)
                put("tipo", venta.tipo)
                put("fecha_venta", venta.fechaVenta)
            }
            bd.insert("ventas", null, registro)
        }

        // 5. Lectura de datos e impresión en Logcat
        val fila = bd.rawQuery("SELECT codigo, nombre, precio, cantidad, tipo, fecha_venta FROM ventas", null)
        if (fila.moveToFirst()) {
            do {
                val cod = fila.getString(0)
                val nom = fila.getString(1)
                val prec = fila.getDouble(2)
                val cant = fila.getInt(3)
                val tipo = fila.getString(4)
                val fecha = fila.getString(5)

                Log.i("BD_REGISTRO", "Venta: Cod=$cod | Nom=$nom | Precio=S/$prec | Cant=$cant | Tipo=$tipo | Fecha=$fecha")
            } while (fila.moveToNext())
        }
        fila.close()
        bd.close()
    }
}

// Modelo de datos para gestionar las ventas
data class Venta(
    val codigo: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val tipo: String,
    val fechaVenta: String
)