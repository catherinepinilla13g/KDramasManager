package com.manager.kdramas.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
 * - Maneja conexión MQTT (tiempo real) con HiveMQ Client.
 * - Persiste mensajes en Firebase Realtime Database.
 * - Expone LiveData para que la UI observe cambios.
 * - Permite borrar mensajes globalmente en una sala.
 */
public class ChatViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ChatMessage>> mensajes =
            new MutableLiveData<>(new ArrayList<>());

    private MqttRepository mqttRepository;
    private final DatabaseReference mensajesRef =
            FirebaseDatabase.getInstance().getReference("messages");

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ChatMessage>> getMensajes() {
        return mensajes;
    }

    // Inicializar cliente MQTT con HiveMQ Client
    public void inicializar(String clientId) {
        mqttRepository = new MqttRepository(clientId);
        mqttRepository.connect(
                () -> Log.d("ChatViewModel", "Conectado a MQTT"),
                error -> Log.e("ChatViewModel", "Error al conectar MQTT", error)
        );
    }

    // Suscribirse a un tópico MQTT
    public void suscribir(String topic) {
        if (mqttRepository == null) return;

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
                List<ChatMessage> lista = new ArrayList<>();
                if (mensajes.getValue() != null) {
                    lista.addAll(mensajes.getValue());
                }
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
        if (mqttRepository == null) return;

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
                        error.toException().printStackTrace();
                    }
                });
    }

    // Borrar todos los mensajes de una sala (vaciar chat global)
    public void borrarMensajesGlobal(String room) {
        mensajesRef.child(room).removeValue()
                .addOnSuccessListener(aVoid -> {
                    mensajes.setValue(new ArrayList<>());
                    Log.d("ChatViewModel", "Mensajes eliminados globalmente en sala: " + room);
                })
                .addOnFailureListener(e -> Log.e("ChatViewModel", "Error al eliminar mensajes", e));
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










