package com.manager.kdramas.repositories;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.manager.kdramas.database.DBHelper;
import com.manager.kdramas.model.Kdrama;
import java.util.ArrayList;
import java.util.List;

/**
 * KdramaRepository - Implementación del patrón Repository para acceso a datos de K-Dramas.

 * Responsabilidades:
 * - Encapsular operaciones de lectura y escritura sobre la base de datos SQLite.
 * - Proporcionar una interfaz limpia para acceder a los datos desde el ViewModel.
 * - Centralizar el mapeo entre registros de base de datos y objetos del modelo.
 */
public class KdramaRepository {

    private Context context;
    private DBHelper dbHelper;

    /**
     * Constructor del repositorio.
     * Inicializa el helper de base de datos con el contexto de aplicación.
     *
     * @param context Contexto de la aplicación.
     */
    public KdramaRepository(Context context) {
        this.context = context.getApplicationContext(); // Evita memory leaks
        this.dbHelper = new DBHelper(this.context);
    }

    /**
     * Recupera todos los K-Dramas almacenados, ordenados alfabéticamente por título.
     *
     * @return Lista completa de K-Dramas.
     */
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
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return listaKdramas;
    }

    /**
     * Recupera un K-Drama específico según su ID.
     *
     * @param id Identificador único del K-Drama.
     * @return Instancia de Kdrama si se encuentra, o null en caso contrario.
     */
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
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return kdrama;
    }

    /**
     * Inserta un nuevo K-Drama en la base de datos.
     *
     * @param kdrama Instancia del modelo con los datos a insertar.
     * @return ID del nuevo registro insertado, o -1 si falla.
     */
    public long insertarKdrama(Kdrama kdrama) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("INSERT INTO kdrama (titulo, genero, anio, capitulos, calificacion, imagen_url) VALUES (?, ?, ?, ?, ?, ?)",
                    new Object[]{
                            kdrama.getTitulo(),
                            kdrama.getGenero(),
                            Integer.parseInt(kdrama.getAnio()),
                            Integer.parseInt(kdrama.getCapitulos()),
                            Float.parseFloat(kdrama.getCalificacion()),
                            kdrama.getImagenUrl()
                    });

            try (Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null)) {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(0);
                }
            }
            return -1;
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar K-Drama: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Actualiza los datos de un K-Drama existente.
     *
     * @param kdrama Instancia con los datos actualizados.
     * @return Número de filas modificadas.
     */
    public int actualizarKdrama(Kdrama kdrama) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE kdrama SET titulo=?, genero=?, anio=?, capitulos=?, calificacion=?, finalizado=?, imagen_url=? WHERE id=?",
                    new Object[]{
                            kdrama.getTitulo(),
                            kdrama.getGenero(),
                            Integer.parseInt(kdrama.getAnio()),
                            Integer.parseInt(kdrama.getCapitulos()),
                            Float.parseFloat(kdrama.getCalificacion()),
                            kdrama.getFinalizado(),
                            kdrama.getImagenUrl(),
                            Integer.parseInt(kdrama.getId())
                    });

            try (Cursor cursor = db.rawQuery("SELECT changes()", null)) {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(0);
                }
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar K-Drama: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Elimina un K-Drama de la base de datos.
     *
     * @param id Identificador del K-Drama a eliminar.
     * @return Número de filas eliminadas.
     */
    public int eliminarKdrama(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM kdrama WHERE id=?", new Object[]{Integer.parseInt(id)});

            try (Cursor cursor = db.rawQuery("SELECT changes()", null)) {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(0);
                }
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar K-Drama: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Convierte un registro de base de datos (Cursor) en una instancia del modelo Kdrama.
     *
     * @param cursor Cursor posicionado en el registro deseado.
     * @return Instancia de Kdrama con los datos del registro.
     */
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
        return kdrama;
    }
}
