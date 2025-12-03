package com.manager.kdramas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manager.kdramas.adapters.ContactAdapter;
import com.manager.kdramas.model.UserIdentity;
import com.manager.kdramas.viewmodel.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * ContactActivity - Pantalla de gestión de contactos.
 * - Lista contactos desde Firebase.
 * - Permite buscarlos, agregarlos y eliminarlos.
 * - Abre chats privados con cada contacto.
 * - Da acceso al perfil del usuario.
 */
public class ContactActivity extends AppCompatActivity {

    private RecyclerView recyclerContactos;
    private SearchView searchContactos;
    private ContactAdapter adapter;
    private List<UserIdentity> listaCompleta = new ArrayList<>();
    private ContactViewModel contactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Bloquear invitados
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.isAnonymous()) {
            Toast.makeText(this, "Invitado no puede gestionar contactos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerContactos = findViewById(R.id.recyclerContactos);
        searchContactos = findViewById(R.id.searchContactos);
        FloatingActionButton btnAgregar = findViewById(R.id.btnAgregarContacto);
        FloatingActionButton btnPerfil = findViewById(R.id.btnPerfil);

        adapter = new ContactAdapter(
                listaCompleta,
                contact -> {
                    // Abrir chat privado con el contacto
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("room", contact.getUserId());
                    startActivity(intent);
                },
                contact -> {
                    // Eliminar contacto
                    new AlertDialog.Builder(this)
                            .setTitle("Eliminar contacto")
                            .setMessage("¿Deseas eliminar a " + contact.getDisplayName() + "?")
                            .setPositiveButton("Eliminar", (dialog, which) -> {
                                if (contactViewModel != null) {
                                    contactViewModel.eliminarContacto(contact.getUserId());
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
        );

        recyclerContactos.setLayoutManager(new LinearLayoutManager(this));
        recyclerContactos.setAdapter(adapter);

        // Inicializar ViewModel y observar cambios
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        contactViewModel.getContactos().observe(this, lista -> {
            listaCompleta = lista;
            adapter.actualizarLista(lista);
        });
        contactViewModel.cargarContactos();

        // Búsqueda en la lista
        searchContactos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarContactos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarContactos(newText);
                return true;
            }
        });

        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregar());

        // Abrir perfil del usuario
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Diálogo para agregar un nuevo contacto por UID.
     */
    private void mostrarDialogoAgregar() {
        EditText input = new EditText(this);
        input.setHint("UID del contacto");

        new AlertDialog.Builder(this)
                .setTitle("Agregar contacto")
                .setMessage("Ingresa el UID del usuario que deseas agregar:")
                .setView(input)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String contactUid = input.getText().toString().trim();
                    if (!contactUid.isEmpty() && contactViewModel != null) {
                        // Construir objeto mínimo UserIdentity
                        UserIdentity nuevo = new UserIdentity(contactUid, "Nuevo contacto");
                        nuevo.setEmail("");
                        nuevo.setPhotoUrl(null);
                        nuevo.setAnonymous(false);

                        contactViewModel.agregarContacto(nuevo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void filtrarContactos(String texto) {
        if (texto == null) texto = "";
        List<UserIdentity> filtrada = new ArrayList<>();
        for (UserIdentity c : listaCompleta) {
            String nombre = c.getDisplayName() != null ? c.getDisplayName().toLowerCase() : "";
            String email = c.getEmail() != null ? c.getEmail().toLowerCase() : "";
            if (nombre.contains(texto.toLowerCase()) || email.contains(texto.toLowerCase())) {
                filtrada.add(c);
            }
        }
        adapter.actualizarLista(filtrada);
    }
}
