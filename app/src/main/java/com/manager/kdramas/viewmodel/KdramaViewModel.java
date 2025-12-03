package com.manager.kdramas.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.manager.kdramas.repositories.KdramaRepository;
import com.manager.kdramas.model.Kdrama;
import java.util.List;



/**
 * KdramaViewModel - ViewModel que gestiona la lógica de presentación y acceso a datos de K-Dramas.

 * Responsabilidades:
 * - Intermediar entre la interfaz de usuario y el repositorio de datos.
 * - Exponer datos observables mediante LiveData.
 * - Ejecutar operaciones asincrónicas para mantener la UI reactiva.
 * - Manejar errores y estados de operación.
 */
public class KdramaViewModel extends AndroidViewModel {

    private KdramaRepository kdramaRepository;

    // LiveData observables para la lista de K-Dramas
    private final MutableLiveData<List<Kdrama>> _kdramas = new MutableLiveData<>();
    public final LiveData<List<Kdrama>> kdramas = _kdramas;

    // LiveData para indicar éxito o fallo en operaciones
    private final MutableLiveData<Boolean> _operationSuccess = new MutableLiveData<>();
    public final LiveData<Boolean> operationSuccess = _operationSuccess;

    // LiveData para mensajes de error
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;



    /**
     * Constructor del ViewModel.
     * Inicializa el repositorio con el contexto de aplicación.
     *
     * @param application Contexto global de la aplicación.
     */
    public KdramaViewModel(Application application) {
        super(application);
        kdramaRepository = new KdramaRepository(application);
    }



    public void cargarKdramas() {
        new Thread(() -> {
            try {
                List<Kdrama> lista = kdramaRepository.obtenerTodosKdramas();
                _kdramas.postValue(lista);
            } catch (Exception e) {
                _errorMessage.postValue("Error al cargar K-Dramas: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Guarda un nuevo K-Drama en la base de datos.
     * Valida los datos antes de insertar y actualiza los estados observables.
     *
     * @param kdrama Instancia del modelo a guardar.
     */
    public void guardarKdrama(Kdrama kdrama) {
        new Thread(() -> {
            try {
                if (!kdrama.esValido()) {
                    _errorMessage.postValue("Datos del K-Drama no válidos");
                    _operationSuccess.postValue(false);
                    return;
                }

                long resultado = kdramaRepository.insertarKdrama(kdrama);
                if (resultado > 0) {
                    _operationSuccess.postValue(true);
                    cargarKdramas();
                } else {
                    _operationSuccess.postValue(false);
                    _errorMessage.postValue("No se pudo guardar el K-Drama");
                }
            } catch (Exception e) {
                _operationSuccess.postValue(false);
                _errorMessage.postValue("Error al guardar: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Actualiza los datos de un K-Drama existente.
     * Valida la existencia del ID antes de ejecutar la operación.
     *
     * @param kdrama Instancia con los datos actualizados.
     */
    public void actualizarKdrama(Kdrama kdrama) {
        new Thread(() -> {
            try {
                if (kdrama.getId() == null || kdrama.getId().isEmpty()) {
                    _errorMessage.postValue("ID del K-Drama no válido");
                    _operationSuccess.postValue(false);
                    return;
                }

                int filasAfectadas = kdramaRepository.actualizarKdrama(kdrama);
                if (filasAfectadas > 0) {
                    _operationSuccess.postValue(true);
                    cargarKdramas();
                } else {
                    _operationSuccess.postValue(false);
                    _errorMessage.postValue("No se pudo actualizar el K-Drama");
                }
            } catch (Exception e) {
                _operationSuccess.postValue(false);
                _errorMessage.postValue("Error al actualizar: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Elimina un K-Drama según su ID.
     * Actualiza los estados observables según el resultado.
     *
     * @param id Identificador del K-Drama a eliminar.
     */
    public void eliminarKdrama(String id) {
        new Thread(() -> {
            try {
                int filasAfectadas = kdramaRepository.eliminarKdrama(id);
                if (filasAfectadas > 0) {
                    _operationSuccess.postValue(true);
                    cargarKdramas();
                } else {
                    _operationSuccess.postValue(false);
                    _errorMessage.postValue("No se pudo eliminar el K-Drama");
                }
            } catch (Exception e) {
                _operationSuccess.postValue(false);
                _errorMessage.postValue("Error al eliminar: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Obtiene un K-Drama específico desde el repositorio según su ID.
     * Utilizado para operaciones puntuales como edición.
     *
     * @param id Identificador del K-Drama.
     * @return Instancia encontrada o null si ocurre un error.
     */
    public Kdrama obtenerKdramaPorId(String id) {
        try {
            return kdramaRepository.obtenerKdramaPorId(id);
        } catch (Exception e) {
            _errorMessage.postValue("Error al obtener K-Drama: " + e.getMessage());
            return null;
        }
    }
}
