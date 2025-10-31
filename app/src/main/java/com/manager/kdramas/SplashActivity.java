package com.manager.kdramas;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity - Pantalla de bienvenida que se muestra al iniciar la aplicación.

 * Responsabilidades:
 * - No requiere ViewModel ni lógica de negocio.
 * - Muestra una pantalla temporal antes de navegar a MainActivity.
 * - Puede utilizarse para inicializaciones básicas si se requiere.
 */
public class SplashActivity extends AppCompatActivity {

    // Duración del splash en milisegundos antes de iniciar la navegación
    private static final int SPLASH_DELAY = 2000;

    /**
     * Método invocado al crear la actividad.
     * Establece el layout y programa la transición automática a la actividad principal.
     *
     * @param savedInstanceState Estado guardado de la instancia, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        programarTransicion();
    }

    /**
     * Programa la transición automática hacia MainActivity después de un retraso definido.
     * Utiliza un Handler para ejecutar la navegación en segundo plano.
     */
    private void programarTransicion() {
        new Handler().postDelayed(() -> navegarAMainActivity(), SPLASH_DELAY);
    }

    /**
     * Inicia la actividad principal y finaliza la actual para evitar que el usuario regrese al splash.
     */
    private void navegarAMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
