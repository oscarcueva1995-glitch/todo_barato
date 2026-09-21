package com.example.todo_barato.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Config(
    context: Context?,
    name: String = "todo_barato_bd",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE ventas (
                codigo TEXT PRIMARY KEY,
                nombre TEXT,
                precio REAL,
                cantidad INTEGER,
                tipo TEXT,
                fecha_venta TEXT
            )
        """.trimIndent()

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ventas")
        onCreate(db)
    }
}