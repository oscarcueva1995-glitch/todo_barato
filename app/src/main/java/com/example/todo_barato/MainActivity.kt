package com.example.todo_barato

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var layoutIntro: View
    private lateinit var layoutContenido: View
    private lateinit var imgGokuIntro: ImageView
    private lateinit var etBuscar: EditText
    private lateinit var txtFacturaBox: TextView
    private lateinit var txtBoletaBox: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Vistas
        layoutIntro = findViewById(R.id.layoutIntro)
        layoutContenido = findViewById(R.id.layoutContenido)
        imgGokuIntro = findViewById(R.id.imgGokuIntro)
        etBuscar = findViewById(R.id.etBuscar)
        txtFacturaBox = findViewById(R.id.txtFacturaBox)
        txtBoletaBox = findViewById(R.id.txtBoletaBox)

        // Configurar e iniciar la animación de Goku
        imgGokuIntro.setImageResource(R.drawable.goku_ui)
        imgGokuIntro.post {
            val drawable = imgGokuIntro.drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable is AnimatedImageDrawable) {
                drawable.start()
            }
        }

        // Mostrar intro por 3 segundos, luego el contenido
        Handler(Looper.getMainLooper()).postDelayed({
            layoutIntro.visibility = View.GONE
            layoutContenido.visibility = View.VISIBLE
        }, 3000)

        // Base de datos local de prueba
        val listaVentas = listOf(
            "Cod: xyz001\nProd: laptop gamer php\nPrecio: S/3000 | Cant: 1\nTipo: factura",
            "Cod: xyz002\nProd: teclado\nPrecio: S/50 | Cant: 1\nTipo: boleta",
            "Cod: xyz003\nProd: mouse inalambrico\nPrecio: S/35 | Cant: 2\nTipo: boleta",
            "Cod: xyz004\nProd: luces led\nPrecio: S/150 | Cant: 1\nTipo: factura",
            "Cod: xyz005\nProd: audifonos gamer\nPrecio: S/120 | Cant: 1\nTipo: boleta"
        )

        // Lógica de búsqueda
        etBuscar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val busqueda = etBuscar.text.toString().trim()
                val encontrado = listaVentas.find { it.contains(busqueda, ignoreCase = true) }

                txtFacturaBox.visibility = View.GONE
                txtBoletaBox.visibility = View.GONE

                if (encontrado != null) {
                    if (encontrado.contains("factura", ignoreCase = true)) {
                        txtFacturaBox.text = "📄 FACTURA ENCONTRADA:\n\n$encontrado"
                        txtFacturaBox.visibility = View.VISIBLE
                    } else if (encontrado.contains("boleta", ignoreCase = true)) {
                        txtBoletaBox.text = "🎫 BOLETA ENCONTRADA:\n\n$encontrado"
                        txtBoletaBox.visibility = View.VISIBLE
                    }
                } else {
                    txtFacturaBox.text = "⚠️ Producto no encontrado"
                    txtFacturaBox.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }
    }
}