package com.manager.kdramas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manager.kdramas.utils.AuthHelper;

/**
 * ProfileActivity - Pantalla de perfil de usuario.
 * - Muestra y permite editar nombre y foto de perfil (por URL).
 * - Condiciona edición según tipo de usuario (Google vs invitado).
 * - Permite cerrar sesión y abrir chat.
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView imgFotoPerfil;
    private EditText edNombre;
    private EditText edFotoUrl; // campo para escribir la URL de la imagen
    private TextView txtEmail;
    private Button btnGuardar;
    private Button btnLogout;
    private Button btnChatGlobal;
    private Button btnChatPrivado; // opcional

    private String uid;
    private DatabaseReference userRef;
    private GoogleSignInClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        edNombre = findViewById(R.id.edNombre);
        edFotoUrl = findViewById(R.id.edFotoUrl);
        txtEmail = findViewById(R.id.txtEmail);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLogout = findViewById(R.id.btnLogout);
        btnChatGlobal = findViewById(R.id.btnChatGlobal);
        btnChatPrivado = findViewById(R.id.btnChatPrivado);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        googleClient = AuthHelper.getGoogleClient(this);

        // Condicionar según tipo de usuario
        if (currentUser.isAnonymous()) {
            edNombre.setEnabled(false);
            edFotoUrl.setEnabled(false);
            btnGuardar.setEnabled(false);
            txtEmail.setText("Invitado");
        } else {
            txtEmail.setText(currentUser.getEmail());
        }

        // Cargar datos actuales desde Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("displayName").getValue(String.class);
                    String fotoUrl = snapshot.child("photoUrl").getValue(String.class);

                    if (nombre != null) edNombre.setText(nombre);
                    if (fotoUrl != null) {
                        edFotoUrl.setText(fotoUrl);
                        Glide.with(ProfileActivity.this).load(fotoUrl).into(imgFotoPerfil);
                    }
                }
            }

            @Override public void onCancelled(DatabaseError error) {}
        });

        // Guardar cambios (nombre y URL de foto)
        btnGuardar.setOnClickListener(v -> {
            if (!currentUser.isAnonymous()) {
                String nuevoNombre = edNombre.getText().toString().trim();
                String nuevaFotoUrl = edFotoUrl.getText().toString().trim();

                if (!nuevoNombre.isEmpty()) {
                    userRef.child("displayName").setValue(nuevoNombre);
                }
                if (!nuevaFotoUrl.isEmpty()) {
                    userRef.child("photoUrl").setValue(nuevaFotoUrl);
                    Glide.with(ProfileActivity.this).load(nuevaFotoUrl).into(imgFotoPerfil);
                }
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        // Abrir chat global
        btnChatGlobal.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("room", "global");
            startActivity(intent);
        });

        // Abrir chat privado (ejemplo con UID fijo)
        btnChatPrivado.setOnClickListener(v -> {
            String targetUid = "UID_DEL_CONTACTO";
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("room", targetUid);
            startActivity(intent);
        });

        // Cerrar sesión (Firebase + Google)
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            googleClient.signOut().addOnCompleteListener(task -> {
                AuthHelper.setIdentity(null);
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            });
        });
    }
}
