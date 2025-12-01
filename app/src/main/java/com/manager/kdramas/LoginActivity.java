package com.manager.kdramas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manager.kdramas.utils.AuthHelper;
import com.manager.kdramas.model.UserIdentity;

/**
 * LoginActivity - Pantalla de inicio de sesión.
 *
 * Responsabilidades:
 * - Permitir login con Google (Firebase Auth).
 * - Permitir acceso como invitado mediante autenticación anónima.
 * - Registrar usuarios en Firebase Database.
 * - Redirigir a ChatActivity o ContactActivity tras autenticación exitosa.
 */
public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient googleClient;
    private FirebaseAuth auth;

    private Button btnGoogle;
    private Button btnAnon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth y Google Sign-In
        auth = FirebaseAuth.getInstance();
        googleClient = AuthHelper.getGoogleClient(this);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnAnon = findViewById(R.id.btnAnon);

        // Login con Google
        btnGoogle.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            AuthHelper.startGoogleSignIn(this, googleClient);
        });

        // Acceso anónimo
        btnAnon.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Acceso como invitado", Toast.LENGTH_SHORT).show();
                    // Crear identidad invitado
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        AuthHelper.setIdentity(new UserIdentity(user.getUid(), "Invitado"));
                        AuthHelper.getIdentity().isAnonymous = true;
                    }
                    navegarDespuesLogin("global");
                } else {
                    Toast.makeText(this, "Error en acceso anónimo", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task =
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);

            AuthHelper.handleGoogleResult(task, auth,
                    () -> {
                        Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        navegarDespuesLogin("global");
                    },
                    () -> Toast.makeText(this, "Error en login", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Navega a la pantalla adecuada tras autenticación.
     * - Invitado → Chat global directo.
     * - Google → registra usuario en Firebase y abre ContactActivity.
     */
    private void navegarDespuesLogin(String room) {
        UserIdentity identity = AuthHelper.getIdentity();

        if (identity != null && identity.isAnonymous) {
            // Invitado → solo chat global
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        } else {
            // Usuario Google → registrar en Firebase y abrir contactos
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && identity != null) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                usersRef.child(user.getUid()).setValue(identity);
            }
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
        }
        finish();
    }
}




