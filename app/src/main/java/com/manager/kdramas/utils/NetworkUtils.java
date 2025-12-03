package com.manager.kdramas.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * NetworkUtils - Clase auxiliar para verificar el estado de la conexión a internet.

 * Responsabilidades:
 * - Proporcionar un método estático para comprobar si el dispositivo está conectado.
 * - No contiene lógica de negocio, solo utilidades de red.
 */
public class NetworkUtils {

    /**
     * Verifica si el dispositivo tiene conexión activa a internet.
     *
     * @param context Contexto de la aplicación.
     * @return true si hay conexión, false en caso contrario.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}

