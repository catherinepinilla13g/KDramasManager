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
 *
 * Responsabilidades:
 * - Capturar datos desde la interfaz de usuario.
 * - Validar y construir objetos del modelo Kdrama.
 * - Delegar operaciones de guardado al ViewModel.
 * - Observar LiveData para mostrar mensajes y actualizar la interfaz.
 * - Navegar hacia la actividad de listado.
 */
public class MainActivity extends AppCompatActivity {

    // Componentes visuales del formulario
    private EditText txtTitulo, txtAnio, txtCapitulos, txtImagenUrl, txtUrlPlataforma, txtUrlTrailer;
    private Spinner spnGenero, spnEstado;
    private RatingBar ratingCalificacion;
    private Button btnGuardar, btnVerLista;

    // ViewModel que gestiona la lógica de presentación y acceso a datos
    private KdramaViewModel kdramaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarViewModel();
        configurarToolbar();
        vincularComponentes();
        configurarSpinners();
        configurarEventos();
        configurarObservadores();
    }

    private void inicializarViewModel() {
        kdramaViewModel = new ViewModelProvider(this).get(KdramaViewModel.class);
    }

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

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void vincularComponentes() {
        txtTitulo = findViewById(R.id.txtTitulo);
        txtAnio = findViewById(R.id.txtAnio);
        txtCapitulos = findViewById(R.id.txtCapitulos);
        txtImagenUrl = findViewById(R.id.txtImagenUrl);
        txtUrlPlataforma = findViewById(R.id.txtUrlPlataforma); 
        txtUrlTrailer = findViewById(R.id.txtUrlTrailer);       
        spnGenero = findViewById(R.id.spnGenero);
        spnEstado = findViewById(R.id.spnEstado);               
        ratingCalificacion = findViewById(R.id.ratingCalificacion);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVerLista = findViewById(R.id.btnVerLista);
    }

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

    private void configurarEventos() {
        btnGuardar.setOnClickListener(v -> guardarKdrama());
        btnVerLista.setOnClickListener(v -> navegarALista());
    }

    private void guardarKdrama() {
        String titulo = txtTitulo.getText().toString().trim();
        String genero = spnGenero.getSelectedItem().toString();
        String anio = txtAnio.getText().toString().trim();
        String capitulos = txtCapitulos.getText().toString().trim();
        String imagenUrl = txtImagenUrl.getText().toString().trim();
        String urlPlataforma = txtUrlPlataforma.getText().toString().trim();
        String urlTrailer = txtUrlTrailer.getText().toString().trim();
        float calificacion = ratingCalificacion.getRating();
        String finalizado = String.valueOf(spnEstado.getSelectedItemPosition());

        if (titulo.isEmpty() || anio.isEmpty() || capitulos.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Kdrama nuevoKdrama = new Kdrama(titulo, genero, anio, capitulos, String.valueOf(calificacion), finalizado);
        nuevoKdrama.setImagenUrl(imagenUrl);
        nuevoKdrama.setUrlPlataforma(urlPlataforma);
        nuevoKdrama.setUrlTrailer(urlTrailer);

        kdramaViewModel.guardarKdrama(nuevoKdrama);
    }

    private void navegarALista() {
        Intent intent = new Intent(this, ListarKdramas.class);
        startActivity(intent);
    }

    private void limpiarCampos() {
        txtTitulo.setText("");
        txtAnio.setText("");
        txtCapitulos.setText("");
        txtImagenUrl.setText("");
        txtUrlPlataforma.setText("");
        txtUrlTrailer.setText("");
        ratingCalificacion.setRating(3);
        spnGenero.setSelection(0);
        spnEstado.setSelection(0);
        txtTitulo.requestFocus();
    }
}
