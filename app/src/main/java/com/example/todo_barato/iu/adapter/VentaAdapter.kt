package com.example.todo_barato.iu.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.todo_barato.R
import com.example.todo_barato.model.Venta

class VentaAdapter(context: Context, private val ventas: List<Venta>) :
    ArrayAdapter<Venta>(context, R.layout.item_venta, ventas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false)
        val item = getItem(position)!!

        // Código y fecha
        val txtCodFecha = view.findViewById<TextView>(R.id.txtItemCodFecha)
        txtCodFecha.text = "${item.codigo} • ${item.fechaVenta}"
        txtCodFecha.textSize = 15f

        // Tipo FACTURA / BOLETA
        val txtTipo = view.findViewById<TextView>(R.id.txtItemTipo)
        txtTipo.text = item.tipo.uppercase()
        txtTipo.textSize = 15f

        if (item.tipo.equals("factura", ignoreCase = true)) {
            txtTipo.setTextColor(Color.parseColor("#FF5252")) // Rojo
        } else {
            txtTipo.setTextColor(Color.parseColor("#4CAF50")) // Verde
        }

        // Nombre del producto
        val txtNombre = view.findViewById<TextView>(R.id.txtItemNombre)
        txtNombre.text = item.nombre
        txtNombre.textSize = 20f

        // Cantidad
        val txtCantidad = view.findViewById<TextView>(R.id.txtItemCantidad)
        txtCantidad.text = "${item.cantidad} unidad(es)"
        txtCantidad.textSize = 16f

        // Precio
        val txtPrecio = view.findViewById<TextView>(R.id.txtItemPrecio)
        txtPrecio.text = "S/ ${item.precio}"
        txtPrecio.textSize = 18f

        return view
    }
}