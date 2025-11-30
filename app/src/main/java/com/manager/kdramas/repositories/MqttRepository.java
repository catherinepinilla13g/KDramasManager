package com.manager.kdramas.repositories;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import java.util.function.Consumer;

public class MqttRepository {
    private Mqtt5BlockingClient client;
    private final String server = "11a86f0e13684922ab64a3edb381ece0.s1.eu.hivemq.cloud"; 
    private final int port = 8883; 
    private final String clientId;

    // Credenciales de HiveMQ Cloud
    private final String username = "kdramasmanager";
    private final String password = "P6HDshB13";

    public MqttRepository(String clientId) {
        this.clientId = clientId;
        client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(server)
                .serverPort(port)
                .sslWithDefaultConfig()
                .identifier(clientId)
                .buildBlocking();
    }

    public void connect() {
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .send();
    }

    public void subscribe(String topic, Consumer<String> onMessage) {
        client.toAsync().subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    String payload = new String(publish.getPayloadAsBytes());
                    onMessage.accept(payload);
                })
                .send();
    }

    public void publish(String topic, String payload) {
        client.toAsync().publishWith()
                .topic(topic)
                .payload(payload.getBytes())
                .send();
    }

    public void disconnect() {
        client.disconnect();
    }
}


