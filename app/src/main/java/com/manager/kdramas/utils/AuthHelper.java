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
 *
 * Responsabilidades:
 * - Proveer identidad anónima si el usuario no inicia sesión.
 * - Permitir login con Google y vincularlo a Firebase Auth.
 * - Retornar un objeto UserIdentity con userId, displayName, email y foto.
 */
public class AuthHelper {

    private static final int RC_SIGN_IN = 9001;
    private static UserIdentity identity; 

    /**
     * Asegura que exista una sesión anónima si no hay usuario actual.
     */
    public static void ensureAnonymousLogin(FirebaseAuth auth, Runnable onSuccess, Runnable onFailure) {
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                identity = new UserIdentity(user.getUid(), "Invitado");
                                identity.setAnonymous(true);
                            }
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
     */
    public static UserIdentity getIdentity() {
        if (identity != null) return identity;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            identity = new UserIdentity("anon-" + UUID.randomUUID(), "Invitado");
            identity.setAnonymous(true);
        } else {
            identity = new UserIdentity(
                    user.getUid(),
                    user.getDisplayName() != null ? user.getDisplayName() : "Usuario",
                    user.getEmail(),
                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
            );
            identity.setAnonymous(user.isAnonymous());
        }
        return identity;
    }

    /**
     * Permite establecer manualmente la identidad
     */
    public static void setIdentity(UserIdentity id) {
        identity = id;
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
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                identity = new UserIdentity(
                                        user.getUid(),
                                        user.getDisplayName() != null ? user.getDisplayName() : account.getGivenName(),
                                        user.getEmail(),
                                        user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
                                );
                                identity.setAnonymous(false);
                            }
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
