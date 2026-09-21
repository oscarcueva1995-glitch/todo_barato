package com.example.todo_barato

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.todo_barato.iu.adapter.VentaAdapter
import com.example.todo_barato.db.Config
import com.example.todo_barato.model.Venta

class MainActivity : AppCompatActivity() {

    private val listaVentas = ArrayList<Venta>()
    private val listaFiltrada = ArrayList<Venta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutIntro = findViewById<LinearLayout>(R.id.layoutIntro)
        val layoutContenido = findViewById<LinearLayout>(R.id.layoutContenido)
        val imgGokuIntro = findViewById<ImageView>(R.id.imgGokuIntro)
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        val txtFacturaBox = findViewById<TextView>(R.id.txtFacturaBox)
        val txtBoletaBox = findViewById<TextView>(R.id.txtBoletaBox)
        val lvVentas = findViewById<ListView>(R.id.lvVentas)

        // Cargar y animar el GIF de Goku
        Glide.with(this)
            .asGif()
            .load(R.drawable.goku_ui)
            .into(imgGokuIntro)

        // 1. Splash Screen
        Handler(Looper.getMainLooper()).postDelayed({
            layoutIntro.visibility = View.GONE
            layoutContenido.visibility = View.VISIBLE
        }, 6000)

        // 2. Base de Datos
        val admin = Config(this)
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

        // 3. Consultar Registros
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
            } while (fila.moveToNext())
        }
        fila.close()
        bd.close()

        listaFiltrada.addAll(listaVentas)

        // 4. Configurar Adaptador (Ahora desde su propia clase)
        val adapter = VentaAdapter(this, listaFiltrada)
        lvVentas.adapter = adapter

        // 5. Búsqueda y Filtrado
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