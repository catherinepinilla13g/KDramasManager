package com.manager.kdramas;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.manager.kdramas.model.Kdrama;
import com.manager.kdramas.viewmodel.KdramaViewModel;
import androidx.activity.OnBackPressedCallback;

/**
 * EditarKdrama - Actividad que permite modificar los datos de un K-Drama existente.

 * Responsabilidades:
 * - Capturar y validar datos desde la interfaz de usuario.
 * - Observar el ViewModel para recibir actualizaciones y errores.
 * - Delegar operaciones CRUD al ViewModel.
 * - Reaccionar a cambios de estado mediante LiveData.
 * - Navegar entre pantallas según el resultado de las operaciones.
 */
public class EditarKdrama extends AppCompatActivity {

    // Componentes de la interfaz de usuario
    private EditText edTitulo, edAnio, edCapitulos, edImagenUrl, edId;
    private Spinner spnGenero, spnEstado;
    private RatingBar ratingCalificacion;
    private Button btnActualizar, btnEliminar, btnVolver;

    // ViewModel que gestiona la lógica de presentación
    private KdramaViewModel kdramaViewModel;

    // Instancia del K-Drama que se está editando
    private Kdrama kdramaActual;

    /**
     * Método invocado al crear la actividad.
     * Configura el ViewModel, la interfaz de usuario, los observadores y los eventos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_kdrama);

        inicializarViewModel();
        configurarToolbar();
        vincularComponentes();
        configurarSpinners();
        configurarBotonRetroceso();
        cargarDatosIntent();
        configurarEventos();
        configurarObservadores();
    }

    /**
     * Inicializa el ViewModel utilizando ViewModelProvider.
     */
    private void inicializarViewModel() {
        kdramaViewModel = new ViewModelProvider(this).get(KdramaViewModel.class);
    }

    /**
     * Configura los observadores para los LiveData expuestos por el ViewModel.
     * Muestra mensajes de éxito o error y navega según el resultado de la operación.
     */
    private void configurarObservadores() {
        kdramaViewModel.operationSuccess.observe(this, exito -> {
            if (exito != null && exito) {
                Toast.makeText(this, "Operación completada exitosamente", Toast.LENGTH_LONG).show();
                volverALista();
            }
        });

        kdramaViewModel.errorMessage.observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Configura la toolbar con botón de retroceso.
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
        edId = findViewById(R.id.edId);
        edTitulo = findViewById(R.id.edTitulo);
        edAnio = findViewById(R.id.edAnio);
        edCapitulos = findViewById(R.id.edCapitulos);
        edImagenUrl = findViewById(R.id.edImagenUrl);
        spnGenero = findViewById(R.id.spnGenero);
        spnEstado = findViewById(R.id.spnEstado);
        ratingCalificacion = findViewById(R.id.ratingCalificacion);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVolver = findViewById(R.id.btnVolver);
    }

    /**
     * Configura los spinners de género y estado con los valores definidos en strings.xml.
     */
    private void configurarSpinners() {
        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(this,
                R.array.generos_kdrama, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenero.setAdapter(generoAdapter);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(this,
                R.array.estados_visionado, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEstado.setAdapter(estadoAdapter);
    }

    /**
     * Extrae los datos del K-Drama desde el Intent recibido y actualiza la interfaz.
     */
    private void cargarDatosIntent() {
        Intent intent = getIntent();

        kdramaActual = new Kdrama();
        kdramaActual.setId(intent.getStringExtra("id"));
        kdramaActual.setTitulo(intent.getStringExtra("titulo"));
        kdramaActual.setGenero(intent.getStringExtra("genero"));
        kdramaActual.setAnio(intent.getStringExtra("anio"));
        kdramaActual.setCapitulos(intent.getStringExtra("capitulos"));
        kdramaActual.setCalificacion(intent.getStringExtra("calificacion"));
        kdramaActual.setFinalizado(intent.getStringExtra("finalizado"));
        kdramaActual.setImagenUrl(intent.getStringExtra("imagen_url"));

        poblarUIConDatos();
    }

    /**
     * Pobla la interfaz con los datos del K-Drama actual.
     */
    private void poblarUIConDatos() {
        if (kdramaActual == null) return;

        edId.setText(kdramaActual.getId());
        edTitulo.setText(kdramaActual.getTitulo());
        edAnio.setText(kdramaActual.getAnio());
        edCapitulos.setText(kdramaActual.getCapitulos());
        edImagenUrl.setText(kdramaActual.getImagenUrl());

        try {
            float calificacion = Float.parseFloat(kdramaActual.getCalificacion());
            ratingCalificacion.setRating(calificacion);
        } catch (NumberFormatException e) {
            ratingCalificacion.setRating(3);
        }

        establecerSeleccionSpinner(spnGenero, kdramaActual.getGenero());
        establecerSeleccionEstado(kdramaActual.getFinalizado());
    }

    /**
     * Establece la selección de un spinner según el valor recibido.
     *
     * @param spinner Spinner a configurar.
     * @param valor   Valor a buscar en el spinner.
     */
    private void establecerSeleccionSpinner(Spinner spinner, String valor) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(valor)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Establece la selección del spinner de estado según el índice recibido.
     * El índice debe corresponder al orden definido en strings.xml.
     *
     * @param estado Índice del estado como String.
     */
    private void establecerSeleccionEstado(String estado) {
        try {
            int index = Integer.parseInt(estado);
            if (index >= 0 && index < spnEstado.getCount()) {
                spnEstado.setSelection(index);
            } else {
                spnEstado.setSelection(0);
            }
        } catch (NumberFormatException e) {
            spnEstado.setSelection(0);
        }
    }

    /**
     * Configura los eventos de los botones de acción.
     */
    private void configurarEventos() {
        btnActualizar.setOnClickListener(v -> actualizarKdrama());
        btnEliminar.setOnClickListener(v -> eliminarKdrama());
        btnVolver.setOnClickListener(v -> volverALista());
    }

    /**
     * Actualiza el K-Drama actual con los datos ingresados en la interfaz.
     * Valida los campos obligatorios y delega la operación al ViewModel.
     */
    private void actualizarKdrama() {
        if (kdramaActual == null || kdramaActual.getId() == null) {
            Toast.makeText(this, "Error: K-Drama no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = edTitulo.getText().toString().trim();
        String genero = spnGenero.getSelectedItem().toString();
        String anio = edAnio.getText().toString().trim();
        String capitulos = edCapitulos.getText().toString().trim();
        String imagenUrl = edImagenUrl.getText().toString().trim();
        float calificacion = ratingCalificacion.getRating();
        String finalizado = String.valueOf(spnEstado.getSelectedItemPosition());

        if (titulo.isEmpty() || anio.isEmpty() || capitulos.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        kdramaActual.setTitulo(titulo);
        kdramaActual.setGenero(genero);
        kdramaActual.setAnio(anio);
        kdramaActual.setCapitulos(capitulos);
        kdramaActual.setImagenUrl(imagenUrl);
        kdramaActual.setCalificacion(String.valueOf(calificacion));
        kdramaActual.setFinalizado(finalizado);

        if (!kdramaActual.esValido()) {
            Toast.makeText(this, "Datos del K-Drama no válidos", Toast.LENGTH_SHORT).show();
            return;
        }
        kdramaViewModel.actualizarKdrama(kdramaActual);
    }

    /**
     * Elimina el K-Drama actual utilizando el ViewModel.
     * Verifica que el objeto y su ID sean válidos antes de ejecutar la operación.
     * Si la validación falla, se muestra un mensaje de error al usuario.
     */
    private void eliminarKdrama() {
        if (kdramaActual == null || kdramaActual.getId() == null) {
            Toast.makeText(this, "Error: K-Drama no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        kdramaViewModel.eliminarKdrama(kdramaActual.getId());
    }

    /**
     * Navega de regreso a la actividad que lista los K-Dramas.
     * Finaliza la actividad actual para evitar duplicación en el back stack.
     */
    private void volverALista() {
        Intent intent = new Intent(this, ListarKdramas.class);
        startActivity(intent);
        finish();
    }

    /**
     * Intercepta el evento del botón de retroceso en la toolbar.
     * Si el botón corresponde a 'home', se navega de regreso a la lista.
     *
     * @param item Elemento del menú seleccionado.
     * @return true si se manejó el evento, false en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            volverALista();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configura el comportamiento del botón de retroceso físico o de sistema.
     * Utiliza OnBackPressedDispatcher para interceptar la acción y navegar correctamente.
     */
    private void configurarBotonRetroceso() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                volverALista();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}