package com.manager.kdramas.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manager.kdramas.model.ChatMessage;
import com.manager.kdramas.repositories.MqttRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatViewModel - Lógica de negocio para el chat.
 * - Maneja conexión MQTT (tiempo real).
 * - Persiste mensajes en Firebase Realtime Database.
 * - Expone LiveData para que la UI observe cambios.
 */
public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<ChatMessage>> mensajes =
            new MutableLiveData<>(new ArrayList<>());

    private MqttRepository mqttRepository;
    private final DatabaseReference mensajesRef =
            FirebaseDatabase.getInstance().getReference("messages");

    public LiveData<List<ChatMessage>> getMensajes() {
        return mensajes;
    }

    // Inicializar cliente MQTT
    public void inicializar(String clientId) {
        mqttRepository = new MqttRepository(clientId);
        mqttRepository.connect();
    }

    // Suscribirse a un tópico MQTT
    public void suscribir(String topic) {
        mqttRepository.subscribe(topic, payload -> {
            try {
                JSONObject json = new JSONObject(payload);
                ChatMessage msg = new ChatMessage(
                        json.getString("userId"),
                        json.getString("displayName"),
                        json.getString("text"),
                        json.getLong("timestamp")
                );

                // Actualizar lista en memoria
                List<ChatMessage> lista = new ArrayList<>(mensajes.getValue());
                lista.add(msg);
                mensajes.postValue(lista);

                // Guardar también en Firebase para historial
                String room = topic.replace("kdramas/chat/", "");
                mensajesRef.child(room)
                        .child(String.valueOf(msg.timestamp))
                        .setValue(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Enviar mensaje por MQTT y guardar en Firebase
    public void enviarMensaje(String topic, ChatMessage msg) {
        try {
            JSONObject json = new JSONObject();
            json.put("userId", msg.userId);
            json.put("displayName", msg.displayName);
            json.put("text", msg.text);
            json.put("timestamp", msg.timestamp);

            // Publicar en MQTT
            mqttRepository.publish(topic, json.toString());

            // Guardar en Firebase
            String room = topic.replace("kdramas/chat/", "");
            mensajesRef.child(room)
                    .child(String.valueOf(msg.timestamp))
                    .setValue(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cargar historial desde Firebase al abrir la sala
    public void cargarHistorial(String room) {
        mensajesRef.child(room).orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<ChatMessage> lista = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ChatMessage msg = child.getValue(ChatMessage.class);
                            if (msg != null) lista.add(msg);
                        }
                        mensajes.setValue(lista);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    // Método público para desconectar manualmente
    public void desconectar() {
        if (mqttRepository != null) {
            mqttRepository.disconnect();
        }
    }

    // Desconectar MQTT cuando el ViewModel se destruye automáticamente
    @Override
    protected void onCleared() {
        super.onCleared();
        if (mqttRepository != null) {
            mqttRepository.disconnect();
        }
    }
}






