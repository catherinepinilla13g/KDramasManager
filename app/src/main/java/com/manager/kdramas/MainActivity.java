package com.manager.kdramas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.manager.kdramas.model.Kdrama;
import com.manager.kdramas.viewmodel.KdramaViewModel;

/**
 * MainActivity - Actividad principal para registrar nuevos K-Dramas.

 * Responsabilidades:
 * - Capturar datos desde la interfaz de usuario.
 * - Validar y construir objetos del modelo Kdrama.
 * - Delegar operaciones de guardado al ViewModel.
 * - Observar LiveData para mostrar mensajes y actualizar la interfaz.
 * - Navegar hacia la actividad de listado.
 */
public class MainActivity extends AppCompatActivity {

    // Componentes visuales del formulario
    private EditText txtTitulo, txtAnio, txtCapitulos, txtImagenUrl;
    private Spinner spnGenero;
    private RatingBar ratingCalificacion;
    private Button btnGuardar, btnVerLista;

    // ViewModel que gestiona la lógica de presentación y acceso a datos
    private KdramaViewModel kdramaViewModel;

    /**
     * Método invocado al crear la actividad.
     * Configura el ViewModel, la interfaz de usuario, los observadores y los eventos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarViewModel();
        configurarToolbar();
        vincularComponentes();
        configurarSpinner();
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
     * Muestra mensajes de éxito o error y actualiza la interfaz según el resultado.
     */
    private void configurarObservadores() {
        kdramaViewModel.operationSuccess.observe(this, exito -> {
            if (exito != null && exito) {
                Toast.makeText(this, "K-Drama guardado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
        });

        kdramaViewModel.errorMessage.observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Configura la toolbar superior.
     * Establece el botón de navegación hacia atrás y oculta el título por defecto.
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
        txtTitulo = findViewById(R.id.txtTitulo);
        txtAnio = findViewById(R.id.txtAnio);
        txtCapitulos = findViewById(R.id.txtCapitulos);
        txtImagenUrl = findViewById(R.id.txtImagenUrl);
        spnGenero = findViewById(R.id.spnGenero);
        ratingCalificacion = findViewById(R.id.ratingCalificacion);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVerLista = findViewById(R.id.btnVerLista);
    }

    /**
     * Configura el spinner de géneros con los valores definidos en strings.xml.
     */
    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.generos_kdrama, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenero.setAdapter(adapter);
    }

    /**
     * Configura los eventos de los botones de acción.
     * Define el comportamiento para guardar y navegar a la lista.
     */
    private void configurarEventos() {
        btnGuardar.setOnClickListener(v -> guardarKdrama());
        btnVerLista.setOnClickListener(v -> navegarALista());
    }

    /**
     * Captura los datos del formulario, valida los campos y guarda el K-Drama.
     * Si los datos son válidos, delega la operación al ViewModel.
     */
    private void guardarKdrama() {
        String titulo = txtTitulo.getText().toString().trim();
        String genero = spnGenero.getSelectedItem().toString();
        String anio = txtAnio.getText().toString().trim();
        String capitulos = txtCapitulos.getText().toString().trim();
        String imagenUrl = txtImagenUrl.getText().toString().trim();
        float calificacion = ratingCalificacion.getRating();

        if (titulo.isEmpty() || anio.isEmpty() || capitulos.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Kdrama nuevoKdrama = new Kdrama(titulo, genero, anio, capitulos, String.valueOf(calificacion));
        nuevoKdrama.setImagenUrl(imagenUrl);

        kdramaViewModel.guardarKdrama(nuevoKdrama);
    }

    /**
     * Navega hacia la actividad que muestra la lista de K-Dramas registrados.
     */
    private void navegarALista() {
        Intent intent = new Intent(this, ListarKdramas.class);
        startActivity(intent);
    }

    /**
     * Limpia los campos del formulario después de guardar.
     * Restablece los valores por defecto y enfoca el campo de título.
     */
    private void limpiarCampos() {
        txtTitulo.setText("");
        txtAnio.setText("");
        txtCapitulos.setText("");
        txtImagenUrl.setText("");
        ratingCalificacion.setRating(3);
        spnGenero.setSelection(0);
        txtTitulo.requestFocus();
    }
}
