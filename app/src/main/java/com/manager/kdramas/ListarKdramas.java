package com.manager.kdramas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.manager.kdramas.model.Kdrama;
import com.manager.kdramas.adapters.KdramaAdapter;
import com.manager.kdramas.viewmodel.KdramaViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * ListarKdramas - Actividad que muestra la lista de K-Dramas registrados.
 *
 * Responsabilidades:
 * - Observar los LiveData del ViewModel para recibir actualizaciones.
 * - Configurar y gestionar el RecyclerView y su adaptador.
 * - Navegar a la pantalla de edición al seleccionar un item.
 * - Mostrar estado vacío cuando no hay datos disponibles.
 * - Permitir búsqueda y filtrado por campos importantes.
 * - Permitir agregar nuevos K-Dramas desde la lista mediante botón flotante.
 */
public class ListarKdramas extends AppCompatActivity {

    // Componentes visuales del layout
    private RecyclerView recyclerKdramas;
    private LinearLayout layoutEmpty;
    private SearchView searchKdramas;
    private FloatingActionButton fabAgregar;

    private FloatingActionButton fabChat;


    // Adaptador para mostrar los K-Dramas en el RecyclerView
    private KdramaAdapter adapter;

    // ViewModel que gestiona la lógica de presentación y acceso a datos
    private KdramaViewModel kdramaViewModel;

    // Lista completa para aplicar filtros
    private List<Kdrama> listaCompleta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_kdramas);

        inicializarViewModel();
        configurarToolbar();
        vincularComponentes();
        configurarRecyclerView();
        configurarEventos();
        configurarObservadores();
        cargarDatosIniciales();
    }

    private void inicializarViewModel() {
        kdramaViewModel = new ViewModelProvider(this).get(KdramaViewModel.class);
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
        recyclerKdramas = findViewById(R.id.recyclerKdramas);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        searchKdramas = findViewById(R.id.searchKdramas);
        fabAgregar = findViewById(R.id.fabAgregar);
        fabChat = findViewById(R.id.fabChat);
    }

    private void configurarRecyclerView() {
        adapter = new KdramaAdapter(kdrama -> navegarAEditarKdrama(kdrama));
        recyclerKdramas.setLayoutManager(new LinearLayoutManager(this));
        recyclerKdramas.setAdapter(adapter);
    }

    /**
     * Configura eventos de búsqueda y botón flotante.
     */
    private void configurarEventos() {
        // Botón flotante (+) para agregar
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); // actividad de agregar
            startActivity(intent);
        });

        fabChat.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                // No hay sesión → abrir LoginActivity
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("room", "global"); // puede pasar a la sala que quieras
                startActivity(intent);
            } else {
                // Ya hay sesión → abrir ChatActivity directamente
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("room", "global");
                startActivity(intent);
            }
        });


        // Barra de búsqueda
        searchKdramas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarKdramas(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarKdramas(newText);
                return true;
            }
        });
    }

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

    private void cargarDatosIniciales() {
        kdramaViewModel.cargarKdramas();
    }

    private void actualizarUIConDatos(List<Kdrama> kdramas) {
        if (kdramas == null || kdramas.isEmpty()) {
            mostrarEstadoVacio();
        } else {
            listaCompleta = kdramas; // guardar lista completa para filtros
            mostrarListaConDatos(kdramas);
        }
    }

    private void mostrarEstadoVacio() {
        recyclerKdramas.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void mostrarListaConDatos(List<Kdrama> kdramas) {
        layoutEmpty.setVisibility(View.GONE);
        recyclerKdramas.setVisibility(View.VISIBLE);
        adapter.actualizarLista(kdramas);
    }

    private void mostrarError(String mensajeError) {
        Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
    }

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
        intent.putExtra("url_plataforma", kdrama.getUrlPlataforma()); 
        intent.putExtra("url_trailer", kdrama.getUrlTrailer());       
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        kdramaViewModel.cargarKdramas();
    }

    /**
     * Filtra la lista de K-Dramas según el texto ingresado en la barra de búsqueda.
     *
     * @param texto Texto ingresado por el usuario.
     */
    private void filtrarKdramas(String texto) {
        if (listaCompleta == null) return;

        List<Kdrama> filtrada = new ArrayList<>();
        for (Kdrama k : listaCompleta) {
            if (k.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    k.getGenero().toLowerCase().contains(texto.toLowerCase()) ||
                    k.getEstadoLegible().toLowerCase().contains(texto.toLowerCase())) {
                filtrada.add(k);
            }
        }
        adapter.actualizarLista(filtrada);
    }
}

