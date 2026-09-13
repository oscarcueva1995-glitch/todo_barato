package com.example.todo_barato

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var imgGokuIntro: ImageView
    private lateinit var etBuscar: EditText
    private lateinit var txtFacturaBox: TextView
    private lateinit var txtBoletaBox: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgGokuIntro = findViewById(R.id.imgGokuIntro)
        etBuscar = findViewById(R.id.etBuscar)
        txtFacturaBox = findViewById(R.id.txtFacturaBox)
        txtBoletaBox = findViewById(R.id.txtBoletaBox)

        // Cargar y reproducir el GIF de forma permanente arriba como logo
        imgGokuIntro.setImageResource(R.drawable.goku_ui)
        val drawable = imgGokuIntro.drawable
        if (drawable is AnimatedImageDrawable) {
            drawable.start()
        }

        // Base de datos local (Lista de ventas)
        val listaVentas = listOf(
            "Cod: xyz001\nProd: laptop gamer php\nPrecio: S/3000 | Cant: 1\nTipo: factura",
            "Cod: xyz002\nProd: teclado\nPrecio: S/50 | Cant: 1\nTipo: boleta",
            "Cod: xyz003\nProd: mouse inalambrico\nPrecio: S/35 | Cant: 2\nTipo: boleta",
            "Cod: xyz004\nProd: luces led\nPrecio: S/150 | Cant: 1\nTipo: factura",
            "Cod: xyz005\nProd: audifonos gamer\nPrecio: S/120 | Cant: 1\nTipo: boleta"
        )

        // Lógica de búsqueda al presionar "Enter" o la Lupa en el teclado
        etBuscar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val busqueda = etBuscar.text.toString().trim()

                val encontrado = listaVentas.find {
                    it.contains(busqueda, ignoreCase = true)
                }

                txtFacturaBox.visibility = TextView.GONE
                txtBoletaBox.visibility = TextView.GONE

                if (encontrado != null) {
                    if (encontrado.contains("factura", ignoreCase = true)) {
                        txtFacturaBox.text = "📄 FACTURA ENCONTRADA:\n\n$encontrado"
                        txtFacturaBox.visibility = TextView.VISIBLE
                    } else if (encontrado.contains("boleta", ignoreCase = true)) {
                        txtBoletaBox.text = "🎫 BOLETA ENCONTRADA:\n\n$encontrado"
                        txtBoletaBox.visibility = TextView.VISIBLE
                    }
                } else {
                    txtFacturaBox.text = "⚠️ Producto no encontrado"
                    txtFacturaBox.visibility = TextView.VISIBLE
                }
                true
            } else {
                false
            }
        }
    }
}