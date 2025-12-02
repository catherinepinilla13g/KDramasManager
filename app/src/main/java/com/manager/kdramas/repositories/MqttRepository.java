package com.manager.kdramas.repositories;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * MqttRepository - Encapsula la conexión MQTT con Eclipse Paho.
 * - Conecta al broker (ej. HiveMQ público).
 * - Permite suscribirse y publicar mensajes.
 * - Maneja desconexión.
 */
public class MqttRepository {
    private final MqttAndroidClient client;
    private final MqttConnectOptions options;

    // Broker público de HiveMQ (no requiere usuario ni contraseña)
    private static final String SERVER_URI = "tcp://broker.hivemq.com:1883";

    // Si usas HiveMQ Cloud privado, aquí pondrías credenciales:
    // private static final String USERNAME = "tuUsuario";
    // private static final String PASSWORD = "tuContraseña";

    public MqttRepository(Context context, String clientId) {
        client = new MqttAndroidClient(context, SERVER_URI, clientId);

        options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);

        // Solo si usas HiveMQ Cloud privado:
        // options.setUserName(USERNAME);
        // options.setPassword(PASSWORD.toCharArray());
    }

    public void connect(Runnable onConnected, Consumer<Throwable> onError) {
        try {
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MqttRepository", "Conectado a MQTT con clientId: " + client.getClientId());
                    onConnected.run();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MqttRepository", "Error al conectar MQTT", exception);
                    onError.accept(exception);
                }
            });
        } catch (Exception e) {
            onError.accept(e);
        }
    }

    public void subscribe(String topic, Consumer<String> onMessage) {
        try {
            client.subscribe(topic, 1, (IMqttMessageListener) (t, message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                onMessage.accept(payload);
            });
            Log.d("MqttRepository", "Suscrito al tópico: " + topic);
        } catch (Exception e) {
            Log.e("MqttRepository", "Error al suscribirse al tópico: " + topic, e);
        }
    }

    public void publish(String topic, String payload) {
        try {
            MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            msg.setQos(1);
            client.publish(topic, msg);
            Log.d("MqttRepository", "Publicado en tópico: " + topic);
        } catch (Exception e) {
            Log.e("MqttRepository", "Error al publicar en tópico: " + topic, e);
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
            Log.d("MqttRepository", "Cliente MQTT desconectado");
        } catch (Exception e) {
            Log.e("MqttRepository", "Error al desconectar MQTT", e);
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }
}
