package com.example.proyectointegradordam.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectointegradordam.models.Cliente

class clubDeportivoDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "clubdeportivo.db"
        const val DATABASE_VERSION = 3
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE cliente (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                email TEXT NOT NULL,
                telefono TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE cuota (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_pago TEXT NOT NULL,
                fecha_vencimiento TEXT,
                medio_pago TEXT NOT NULL,
                monto REAL NOT NULL,
                tipo_cuota INTEGER NOT NULL,
                plazo_cuota INTEGER DEFAULT 1,
                id_cliente INTEGER,
                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
            )
        """)

        db.execSQL("""
            CREATE TABLE actividad (
                id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                horario TEXT NOT NULL,
                dia TEXT NOT NULL,
                profesor TEXT NOT NULL,
                costo REAL NOT NULL,
                cupo INTEGER NOT NULL
            )
        """)


        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario TEXT NOT NULL,
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL,
                pass_usuario TEXT NOT NULL,
                activo INTEGER DEFAULT 1
                )
        """)

        db.execSQL("""
            CREATE TABLE credito_actividades (
                id_credito INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                cantidad_creditos INTEGER NOT NULL,
                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
            )
        """)

        // Datos iniciales
        db.execSQL("INSERT INTO usuario (nombre_usuario, pass_usuario, activo, nombre, telefono) VALUES ('Test', '123456', 1, 'Test', '0000000000')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS credito_actividades")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS actividad")
        db.execSQL("DROP TABLE IF EXISTS cuota")
        db.execSQL("DROP TABLE IF EXISTS cliente")
        onCreate(db)
    }
    fun buscarClientePorNombre(texto: String): List<Cliente>{
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM cliente WHERE nombre LIKE ? OR apellido LIKE ?",
            arrayOf("%$texto%", "%$texto%")
        )
        val lista = mutableListOf<Cliente>()
        if(cursor.moveToFirst()){
            do{
                lista.add(
                    Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista

    }

    fun actualizarDatosCliente(
        id: Int,
        email: String,
        telefono: String
    ): Int{
        val db = writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("telefono", telefono)
        }
        return db.update("cliente", values, "id_cliente = ?", arrayOf(id.toString()))

    }


}
