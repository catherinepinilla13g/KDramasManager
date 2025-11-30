package com.manager.kdramas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * ProfileActivity - Pantalla de perfil de usuario.
 * - Muestra y permite editar nombre y foto de perfil.
 * - Condiciona edición según tipo de usuario (Google vs invitado).
 * - Permite cerrar sesión.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private ImageView imgFotoPerfil;
    private EditText edNombre;
    private TextView txtEmail;
    private Button btnGuardar;
    private Button btnLogout;

    private String uid;
    private DatabaseReference userRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        edNombre = findViewById(R.id.edNombre);
        txtEmail = findViewById(R.id.txtEmail);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        storageRef = FirebaseStorage.getInstance().getReference("profile_photos");

        // Condicionar según tipo de usuario
        if (currentUser.isAnonymous()) {
            edNombre.setEnabled(false);
            btnGuardar.setEnabled(false);
            txtEmail.setText("Invitado");
        } else {
            txtEmail.setText(currentUser.getEmail());
        }

        // Cargar datos actuales desde Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String nombre = snapshot.child("nombre").getValue(String.class);
                String fotoUrl = snapshot.child("fotoUrl").getValue(String.class);

                if (nombre != null) edNombre.setText(nombre);
                if (fotoUrl != null) {
                    Glide.with(ProfileActivity.this).load(fotoUrl).into(imgFotoPerfil);
                }
            }

            @Override public void onCancelled(DatabaseError error) {}
        });

        // Seleccionar nueva foto
        imgFotoPerfil.setOnClickListener(v -> {
            if (!currentUser.isAnonymous()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            } else {
                Toast.makeText(this, "Invitado no puede cambiar foto", Toast.LENGTH_SHORT).show();
            }
        });

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = edNombre.getText().toString().trim();
            if (!nuevoNombre.isEmpty() && !currentUser.isAnonymous()) {
                userRef.child("nombre").setValue(nuevoNombre);
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.isAnonymous()) return;

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imgFotoPerfil.setImageURI(imageUri);

            // Subir a Firebase Storage
            StorageReference fileRef = storageRef.child(uid + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Guardar URL en Realtime Database
                        userRef.child("fotoUrl").setValue(uri.toString());
                        Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    })
            );
        }
    }
}



