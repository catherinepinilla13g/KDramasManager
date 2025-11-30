package com.manager.kdramas.utils;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.manager.kdramas.R;
import com.manager.kdramas.model.UserIdentity;

import java.util.UUID;

/**
 * AuthHelper - Maneja la autenticación de usuarios para el chat.

 * Responsabilidades:
 * - Proveer identidad anónima si el usuario no inicia sesión.
 * - Permitir login con Google y vincularlo a Firebase Auth.
 * - Retornar un objeto UserIdentity con userId y displayName.
 */
public class AuthHelper {

    private static final int RC_SIGN_IN = 9001;

    /**
     * Asegura que exista una sesión anónima si no hay usuario actual.
     */
    public static void ensureAnonymousLogin(FirebaseAuth auth, Runnable onSuccess, Runnable onFailure) {
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            onSuccess.run();
                        } else {
                            onFailure.run();
                        }
                    });
        } else {
            onSuccess.run();
        }
    }

    /**
     * Obtiene la identidad actual del usuario.
     * Si no hay sesión, devuelve un invitado con UUID.
     */
    public static UserIdentity getIdentity() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        String uid = user != null ? user.getUid() : "anon-" + UUID.randomUUID();
        String name = (user != null && user.getDisplayName() != null)
                ? user.getDisplayName()
                : "Invitado";

        return new UserIdentity(uid, name);
    }

    /**
     * Configura Google Sign-In.
     */
    public static GoogleSignInClient getGoogleClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    /**
     * Lanza el intent de Google Sign-In.
     */
    public static void startGoogleSignIn(Activity activity, GoogleSignInClient client) {
        Intent signInIntent = client.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Maneja el resultado de Google Sign-In y autentica en Firebase.
     */
    public static void handleGoogleResult(Task<GoogleSignInAccount> completedTask,
                                          FirebaseAuth auth,
                                          Runnable onSuccess,
                                          Runnable onFailure) {
        try {
            GoogleSignInAccount account = completedTask.getResult(Exception.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            onSuccess.run();
                        } else {
                            onFailure.run();
                        }
                    });
        } catch (Exception e) {
            onFailure.run();
        }
    }
}
