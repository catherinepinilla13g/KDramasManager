package com.manager.kdramas.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBHelper - Clase auxiliar para la gestión de la base de datos SQLite.

 * Responsabilidades:
 * - Crear y actualizar el esquema de la base de datos.
 * - Proporcionar instancias de SQLiteDatabase para operaciones CRUD.
 * - No contiene lógica de negocio, solo operaciones estructurales sobre la base de datos.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DB_NAME = "KDramas.db";
    private static final int DB_VERSION = 2;

    // Sentencia SQL para crear la tabla principal de K-Dramas
    private static final String CREATE_TABLE_KDrama =
            "CREATE TABLE kdrama (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT NOT NULL, " +
                    "genero TEXT NOT NULL, " +
                    "anio INTEGER NOT NULL, " +
                    "capitulos INTEGER NOT NULL, " +
                    "calificacion REAL NOT NULL, " +
                    "finalizado INTEGER DEFAULT 0, " +
                    "imagen_url TEXT)";

    // Sentencia SQL para agregar columna nueva en versión 2
    private static final String ALTER_TABLE_ADD_IMAGEN_URL =
            "ALTER TABLE kdrama ADD COLUMN imagen_url TEXT";

    /**
     * Constructor del helper.
     *
     * @param context Contexto de la aplicación, utilizado para acceder a recursos.
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Invocado automáticamente al crear la base de datos por primera vez.
     * Define el esquema inicial ejecutando la sentencia de creación de tabla.
     *
     * @param db Instancia de SQLiteDatabase sobre la que se ejecutan las operaciones.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KDrama);
    }

    /**
     * Invocado automáticamente cuando se detecta un cambio en la versión de la base de datos.
     * Aplica migraciones necesarias para mantener la integridad del esquema.

     * @param db Instancia de SQLiteDatabase.
     * @param oldVersion Versión anterior de la base de datos.
     * @param newVersion Nueva versión de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // En la versión 2 se agregó la columna imagen_url
            db.execSQL(ALTER_TABLE_ADD_IMAGEN_URL);
        }
    }

    /**
     * Invocado automáticamente al abrir la base de datos.
     * Puede utilizarse para configurar restricciones o activar PRAGMAs si se requiere.

     * @param db Instancia de SQLiteDatabase abierta.
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
