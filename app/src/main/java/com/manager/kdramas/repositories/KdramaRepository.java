package com.manager.kdramas.repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.manager.kdramas.database.DBHelper;
import com.manager.kdramas.model.Kdrama;
import com.manager.kdramas.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * KdramaRepository - Implementación del patrón Repository para acceso a datos de K-Dramas.
 * Responsabilidades:
 * - Encapsular operaciones de lectura y escritura sobre la base de datos SQLite.
 * - Sincronizar con Firebase Firestore cuando haya conexión a internet.
 * - Proporcionar una interfaz limpia para acceder a los datos desde el ViewModel.
 * - Centralizar el mapeo entre registros de base de datos y objetos del modelo.
 */
public class KdramaRepository {

    private final Context context;
    private final DBHelper dbHelper;
    private final FirebaseFirestore firestore;

    public KdramaRepository(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DBHelper(this.context);
        this.firestore = FirebaseFirestore.getInstance();
    }


    public List<Kdrama> obtenerTodosKdramas() {
        List<Kdrama> listaKdramas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM kdrama ORDER BY titulo", null)) {
            if (cursor.moveToFirst()) {
                do {
                    Kdrama kdrama = mapearCursorAKdrama(cursor);
                    listaKdramas.add(kdrama);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener K-Dramas: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
        }

        return listaKdramas;
    }

    public Kdrama obtenerKdramaPorId(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Kdrama kdrama = null;

        try (Cursor cursor = db.rawQuery("SELECT * FROM kdrama WHERE id = ?", new String[]{id})) {
            if (cursor.moveToFirst()) {
                kdrama = mapearCursorAKdrama(cursor);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener K-Drama por ID: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
        }

        return kdrama;
    }

    public long insertarKdrama(Kdrama kdrama) {
        long id = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("INSERT INTO kdrama (titulo, genero, anio, capitulos, calificacion, finalizado, imagen_url, url_plataforma, url_trailer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{
                            kdrama.getTitulo(),
                            kdrama.getGenero(),
                            Integer.parseInt(kdrama.getAnio()),
                            Integer.parseInt(kdrama.getCapitulos()),
                            Float.parseFloat(kdrama.getCalificacion()),
                            kdrama.getFinalizado(),
                            kdrama.getImagenUrl(),
                            kdrama.getUrlPlataforma(),
                            kdrama.getUrlTrailer()
                    });

            try (Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null)) {
                if (cursor.moveToFirst()) {
                    id = cursor.getLong(0);
                    kdrama.setId(String.valueOf(id));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar K-Drama: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
        }

        if (NetworkUtils.isConnected(context)) {
            firestore.collection("kdramas")
                    .document(kdrama.getId())
                    .set(kdrama, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Repo", "Insertado en Firebase"))
                    .addOnFailureListener(e -> Log.w("Repo", "Error al insertar en Firebase", e));
        }

        return id;
    }

    public int eliminarKdrama(String id) {
        int filas = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM kdrama WHERE id=?", new Object[]{Integer.parseInt(id)});

            try (Cursor cursor = db.rawQuery("SELECT changes()", null)) {
                if (cursor.moveToFirst()) filas = cursor.getInt(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar K-Drama: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
        }

        if (NetworkUtils.isConnected(context)) {
            firestore.collection("kdramas")
                    .document(id)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Repo", "Eliminado en Firebase"))
                    .addOnFailureListener(e -> Log.w("Repo", "Error al eliminar en Firebase", e));
        }

        return filas;
    }

    public int actualizarKdrama(Kdrama kdrama) {
        int filas = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE kdrama SET titulo=?, genero=?, anio=?, capitulos=?, calificacion=?, finalizado=?, imagen_url=?, url_plataforma=?, url_trailer=? WHERE id=?",
                    new Object[]{
                            kdrama.getTitulo(),
                            kdrama.getGenero(),
                            Integer.parseInt(kdrama.getAnio()),
                            Integer.parseInt(kdrama.getCapitulos()),
                            Float.parseFloat(kdrama.getCalificacion()),
                            kdrama.getFinalizado(),
                            kdrama.getImagenUrl(),
                            kdrama.getUrlPlataforma(),
                            kdrama.getUrlTrailer(),
                            Integer.parseInt(kdrama.getId())
                    });

            try (Cursor cursor = db.rawQuery("SELECT changes()", null)) {
                if (cursor.moveToFirst()) filas = cursor.getInt(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar K-Drama: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
        }

        if (NetworkUtils.isConnected(context)) {
            firestore.collection("kdramas")
                    .document(kdrama.getId())
                    .set(kdrama, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Repo", "Actualizado en Firebase"))
                    .addOnFailureListener(e -> Log.w("Repo", "Error al actualizar en Firebase", e));
        }

        return filas;
    }

    private Kdrama mapearCursorAKdrama(Cursor cursor) {
        Kdrama kdrama = new Kdrama();
        kdrama.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        kdrama.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
        kdrama.setGenero(cursor.getString(cursor.getColumnIndexOrThrow("genero")));
        kdrama.setAnio(cursor.getString(cursor.getColumnIndexOrThrow("anio")));
        kdrama.setCapitulos(cursor.getString(cursor.getColumnIndexOrThrow("capitulos")));
        kdrama.setCalificacion(cursor.getString(cursor.getColumnIndexOrThrow("calificacion")));
        kdrama.setFinalizado(cursor.getString(cursor.getColumnIndexOrThrow("finalizado")));
        kdrama.setImagenUrl(cursor.getString(cursor.getColumnIndexOrThrow("imagen_url")));
        kdrama.setUrlPlataforma(cursor.getString(cursor.getColumnIndexOrThrow("url_plataforma")));
        kdrama.setUrlTrailer(cursor.getString(cursor.getColumnIndexOrThrow("url_trailer")));
        return kdrama;
    }
}
