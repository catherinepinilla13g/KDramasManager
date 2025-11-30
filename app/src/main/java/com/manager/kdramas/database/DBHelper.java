package com.manager.kdramas.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBHelper - Clase auxiliar para la gestión de la base de datos SQLite.
 *
 * Responsabilidades:
 * - Crear y actualizar el esquema de la base de datos.
 * - Proporcionar instancias de SQLiteDatabase para operaciones CRUD.
 * - No contiene lógica de negocio, solo operaciones estructurales sobre la base de datos.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DB_NAME = "KDramas.db";
    private static final int DB_VERSION = 3; // incrementamos versión

    // Sentencia SQL para crear la tabla principal de K-Dramas
    private static final String CREATE_TABLE_KDrama =
            "CREATE TABLE kdrama (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT NOT NULL, " +
                    "genero TEXT NOT NULL, " +
                    "anio INTEGER NOT NULL, " +
                    "capitulos INTEGER NOT NULL, " +
                    "calificacion REAL NOT NULL, " +
                    "finalizado INTEGER NOT NULL, " +
                    "imagen_url TEXT, " +
                    "url_plataforma TEXT, " +   // nueva columna
                    "url_trailer TEXT)";        // nueva columna

    // Sentencias SQL para migraciones
    private static final String ALTER_TABLE_ADD_IMAGEN_URL =
            "ALTER TABLE kdrama ADD COLUMN imagen_url TEXT";
    private static final String ALTER_TABLE_ADD_URL_PLATAFORMA =
            "ALTER TABLE kdrama ADD COLUMN url_plataforma TEXT";
    private static final String ALTER_TABLE_ADD_URL_TRAILER =
            "ALTER TABLE kdrama ADD COLUMN url_trailer TEXT";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KDrama);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migraciones progresivas según la versión anterior
        if (oldVersion < 2) {
            db.execSQL(ALTER_TABLE_ADD_IMAGEN_URL);
        }
        if (oldVersion < 3) {
            db.execSQL(ALTER_TABLE_ADD_URL_PLATAFORMA);
            db.execSQL(ALTER_TABLE_ADD_URL_TRAILER);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
