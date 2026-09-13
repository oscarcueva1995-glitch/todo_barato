package com.example.todo_barato

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val listaVentas = ArrayList<Venta>()
    private val listaFiltrada = ArrayList<Venta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutIntro = findViewById<LinearLayout>(R.id.layoutIntro)
        val layoutContenido = findViewById<LinearLayout>(R.id.layoutContenido)
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        val txtFacturaBox = findViewById<TextView>(R.id.txtFacturaBox)
        val txtBoletaBox = findViewById<TextView>(R.id.txtBoletaBox)
        val lvVentas = findViewById<ListView>(R.id.lvVentas)

        // 1. Mostrar pantalla de carga brevemente
        Handler(Looper.getMainLooper()).postDelayed({
            layoutIntro.visibility = View.GONE
            layoutContenido.visibility = View.VISIBLE
        }, 1500)

        // 2. Insertar registros iniciales en la base de datos SQLite
        val admin = AdminSQLiteOpenHelper(this)
        val bd = admin.writableDatabase
        bd.execSQL("DELETE FROM ventas")

        val registrosIniciales = listOf(
            Venta("xyz001", "laptop gamer hp", 3000.0, 1, "factura", "20/07/2026"),
            Venta("xyz002", "teclado", 50.0, 1, "boleta", "14/08/2026"),
            Venta("xyz003", "mouse inalambrico", 35.0, 2, "boleta", "15/08/2026"),
            Venta("xyz004", "luces led", 150.0, 1, "factura", "18/08/2026"),
            Venta("xyz005", "audifonos gamer", 120.0, 1, "boleta", "22/08/2026")
        )

        for (v in registrosIniciales) {
            val reg = ContentValues().apply {
                put("codigo", v.codigo)
                put("nombre", v.nombre)
                put("precio", v.precio)
                put("cantidad", v.cantidad)
                put("tipo", v.tipo)
                put("fecha_venta", v.fechaVenta)
            }
            bd.insert("ventas", null, reg)
        }

        // 3. Consultar la base de datos e imprimir en Logcat
        val fila = bd.rawQuery("SELECT codigo, nombre, precio, cantidad, tipo, fecha_venta FROM ventas", null)
        if (fila.moveToFirst()) {
            do {
                val v = Venta(
                    fila.getString(0),
                    fila.getString(1),
                    fila.getDouble(2),
                    fila.getInt(3),
                    fila.getString(4),
                    fila.getString(5)
                )
                listaVentas.add(v)
                Log.i("BD_REGISTRO", "Venta: Cod=${v.codigo} | Nom=${v.nombre} | Precio=S/${v.precio} | Cant=${v.cantidad} | Tipo=${v.tipo} | Fecha=${v.fechaVenta}")
            } while (fila.moveToNext())
        }
        fila.close()
        bd.close()

        // 4. Cargar los 5 registros en la lista principal desde el inicio
        listaFiltrada.addAll(listaVentas)

        // 5. Configurar el adaptador para mostrar las tarjetas directamente
        val adapter = object : ArrayAdapter<Venta>(this, R.layout.item_venta, listaFiltrada) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_venta, parent, false)
                val item = getItem(position)!!

                view.findViewById<TextView>(R.id.txtItemCodFecha).text = "${item.codigo} • ${item.fechaVenta}"
                view.findViewById<TextView>(R.id.txtItemTipo).text = item.tipo.uppercase()
                view.findViewById<TextView>(R.id.txtItemNombre).text = item.nombre
                view.findViewById<TextView>(R.id.txtItemCantidad).text = "${item.cantidad} unidad(es)"
                view.findViewById<TextView>(R.id.txtItemPrecio).text = "S/ ${item.precio}"

                return view
            }
        }
        lvVentas.adapter = adapter

        // 6. Búsqueda y filtrado dinámico
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                listaFiltrada.clear()

                if (query.isNotEmpty()) {
                    val coincidencias = listaVentas.filter {
                        it.nombre.lowercase().contains(query) || it.codigo.lowercase().contains(query)
                    }
                    listaFiltrada.addAll(coincidencias)

                    val primero = coincidencias.firstOrNull()
                    if (primero != null) {
                        val contenido = "Cod: ${primero.codigo}\nProd: ${primero.nombre}\nPrecio: S/${primero.precio} | Cant: ${primero.cantidad}\nTipo: ${primero.tipo}"
                        if (primero.tipo.lowercase() == "factura") {
                            txtFacturaBox.text = "📄 FACTURA ENCONTRADA:\n\n$contenido"
                            txtFacturaBox.visibility = View.VISIBLE
                            txtBoletaBox.visibility = View.GONE
                        } else {
                            txtBoletaBox.text = "💳 BOLETA ENCONTRADA:\n\n$contenido"
                            txtBoletaBox.visibility = View.VISIBLE
                            txtFacturaBox.visibility = View.GONE
                        }
                    } else {
                        txtFacturaBox.visibility = View.GONE
                        txtBoletaBox.visibility = View.GONE
                    }
                } else {
                    listaFiltrada.addAll(listaVentas)
                    txtFacturaBox.visibility = View.GONE
                    txtBoletaBox.visibility = View.GONE
                }

                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}

data class Venta(
    val codigo: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val tipo: String,
    val fechaVenta: String
)