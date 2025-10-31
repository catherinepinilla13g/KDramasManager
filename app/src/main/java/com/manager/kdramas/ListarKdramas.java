package com.manager.kdramas;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.manager.kdramas.model.Kdrama;
import com.manager.kdramas.adapters.KdramaAdapter;
import com.manager.kdramas.viewmodel.KdramaViewModel;
import java.util.List;

/**
 * ListarKdramas - Actividad que muestra la lista de K-Dramas registrados.

 * Responsabilidades:
 * - Observar los LiveData del ViewModel para recibir actualizaciones.
 * - Configurar y gestionar el RecyclerView y su adaptador.
 * - Navegar a la pantalla de edición al seleccionar un item.
 * - Mostrar estado vacío cuando no hay datos disponibles.
 */
public class ListarKdramas extends AppCompatActivity {

    // Componentes visuales del layout
    private RecyclerView recyclerKdramas;
    private LinearLayout layoutEmpty;

    // Adaptador para mostrar los K-Dramas en el RecyclerView
    private KdramaAdapter adapter;

    // ViewModel que gestiona la lógica de presentación y acceso a datos
    private KdramaViewModel kdramaViewModel;

    /**
     * Método invocado al crear la actividad.
     * Configura el ViewModel, la interfaz de usuario, los observadores y carga los datos iniciales.
     *
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_kdramas);

        inicializarViewModel();
        configurarToolbar();
        vincularComponentes();
        configurarRecyclerView();
        configurarObservadores();
        cargarDatosIniciales();
    }

    /**
     * Inicializa el ViewModel utilizando ViewModelProvider.
     */
    private void inicializarViewModel() {
        kdramaViewModel = new ViewModelProvider(this).get(KdramaViewModel.class);
    }

    /**
     * Configura la toolbar superior con botón de navegación hacia atrás.
     */
    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * Vincula los componentes visuales del layout con las variables Java.
     */
    private void vincularComponentes() {
        recyclerKdramas = findViewById(R.id.recyclerKdramas);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    /**
     * Configura el RecyclerView con su adaptador y layout manager.
     * Define el comportamiento al hacer clic en un item.
     */
    private void configurarRecyclerView() {
        adapter = new KdramaAdapter(kdrama -> navegarAEditarKdrama(kdrama));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerKdramas.setLayoutManager(layoutManager);
        recyclerKdramas.setAdapter(adapter);
    }

    /**
     * Configura los observadores para los LiveData expuestos por el ViewModel.
     * Actualiza la interfaz según los cambios en los datos o el estado de las operaciones.
     */
    private void configurarObservadores() {
        kdramaViewModel.kdramas.observe(this, this::actualizarUIConDatos);

        kdramaViewModel.errorMessage.observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                mostrarError(mensajeError);
            }
        });

        kdramaViewModel.operationSuccess.observe(this, exito -> {
            if (exito != null && exito) {
                kdramaViewModel.cargarKdramas();
            }
        });
    }

    /**
     * Solicita al ViewModel la carga inicial de datos.
     */
    private void cargarDatosIniciales() {
        kdramaViewModel.cargarKdramas();
    }

    /**
     * Actualiza la interfaz según la lista de K-Dramas recibida.
     * Muestra la lista o el estado vacío según corresponda.
     *
     * @param kdramas Lista de K-Dramas obtenida desde el ViewModel.
     */
    private void actualizarUIConDatos(List<Kdrama> kdramas) {
        if (kdramas == null || kdramas.isEmpty()) {
            mostrarEstadoVacio();
        } else {
            mostrarListaConDatos(kdramas);
        }
    }

    /**
     * Muestra el estado vacío cuando no hay K-Dramas disponibles.
     */
    private void mostrarEstadoVacio() {
        recyclerKdramas.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * Muestra la lista de K-Dramas en el RecyclerView.
     *
     * @param kdramas Lista de K-Dramas a mostrar.
     */
    private void mostrarListaConDatos(List<Kdrama> kdramas) {
        layoutEmpty.setVisibility(View.GONE);
        recyclerKdramas.setVisibility(View.VISIBLE);
        adapter.actualizarLista(kdramas);
    }

    /**
     * Muestra un mensaje de error al usuario mediante un Toast.
     *
     * @param mensajeError Texto del mensaje de error.
     */
    private void mostrarError(String mensajeError) {
        Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
    }

    /**
     * Navega a la actividad de edición, pasando los datos del K-Drama seleccionado.
     *
     * @param kdrama Instancia del K-Drama que se desea editar.
     */
    private void navegarAEditarKdrama(Kdrama kdrama) {
        Intent intent = new Intent(this, EditarKdrama.class);
        intent.putExtra("id", kdrama.getId());
        intent.putExtra("titulo", kdrama.getTitulo());
        intent.putExtra("genero", kdrama.getGenero());
        intent.putExtra("anio", kdrama.getAnio());
        intent.putExtra("capitulos", kdrama.getCapitulos());
        intent.putExtra("calificacion", kdrama.getCalificacion());
        intent.putExtra("finalizado", kdrama.getFinalizado());
        intent.putExtra("imagen_url", kdrama.getImagenUrl());
        startActivity(intent);
    }

    /**
     * Maneja el botón de retroceso de la toolbar.
     * Navega hacia atrás en la pila de actividades.
     *
     * @return true si el evento fue manejado correctamente.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Recarga los datos al reanudar la actividad.
     * Útil para reflejar cambios realizados en la actividad de edición.
     */
    @Override
    protected void onResume() {
        super.onResume();
        kdramaViewModel.cargarKdramas();
    }
}
