package com.manager.kdramas.repositories;

import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.datatypes.MqttQos;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * MqttRepository - Encapsula la conexión MQTT usando HiveMQ Client.
 * - Conecta al broker (ej. HiveMQ público).
 * - Permite suscribirse y publicar mensajes.
 * - Maneja desconexión.
 */
public class MqttRepository {
    private final Mqtt5AsyncClient client;

    // Broker público de HiveMQ (no requiere usuario ni contraseña)
    private static final String SERVER_URI = "broker.hivemq.com";
    private static final int SERVER_PORT = 1883;

    public MqttRepository(String clientId) {
        client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(SERVER_URI)
                .serverPort(SERVER_PORT)
                .identifier(clientId)
                .buildAsync();
    }

    public void connect(Runnable onConnected, Consumer<Throwable> onError) {
        client.connect()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("MqttRepository", "Error al conectar MQTT", throwable);
                        onError.accept(throwable);
                    } else {
                        Log.d("MqttRepository", "Conectado a MQTT con clientId: " + client.getConfig().getClientIdentifier());
                        onConnected.run();
                    }
                });
    }

    public void subscribe(String topic, Consumer<String> onMessage) {
        client.subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback((Mqtt5Publish publish) -> {
                    String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    onMessage.accept(payload);
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("MqttRepository", "Error al suscribirse al tópico: " + topic, throwable);
                    } else {
                        Log.d("MqttRepository", "Suscrito al tópico: " + topic);
                    }
                });
    }

    public void publish(String topic, String payload) {
        client.publishWith()
                .topic(topic)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((pubAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("MqttRepository", "Error al publicar en tópico: " + topic, throwable);
                    } else {
                        Log.d("MqttRepository", "Publicado en tópico: " + topic);
                    }
                });
    }

    public void disconnect() {
        client.disconnect()
                .whenComplete((discAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("MqttRepository", "Error al desconectar MQTT", throwable);
                    } else {
                        Log.d("MqttRepository", "Cliente MQTT desconectado");
                    }
                });
    }

    public boolean isConnected() {
        return client.getState().isConnected();
    }
}
