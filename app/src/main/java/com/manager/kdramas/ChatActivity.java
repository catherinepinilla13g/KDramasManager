package com.manager.kdramas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.manager.kdramas.adapters.ChatAdapter;
import com.manager.kdramas.model.ChatMessage;
import com.manager.kdramas.model.UserIdentity;
import com.manager.kdramas.utils.AuthHelper;
import com.manager.kdramas.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatActivity - Pantalla de chat en tiempo real sobre K-Dramas.
 * - Muestra historial desde Firebase.
 * - Suscribe a tópico MQTT para mensajes en tiempo real.
 * - Permite enviar mensajes.
 */
public class ChatActivity extends AppCompatActivity {

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerChat;
    private EditText edMessage;
    private ImageButton btnSend;

    private String userId;
    private String displayName;
    private String room;
    private String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Obtener sala desde intent (ejemplo: "global")
        room = getIntent().getStringExtra("room");
        if (room == null) room = "global";
        topic = "kdramas/chat/" + room;

        // Identidad del usuario (Firebase o anónima)
        UserIdentity identity = AuthHelper.getIdentity();
        if (identity != null) {
            userId = identity.userId;
            displayName = identity.displayName;
        } else {
            // Fallback si no hay identidad
            userId = "anon";
            displayName = "Invitado";
        }

        // Vincular componentes
        recyclerChat = findViewById(R.id.recyclerChat);
        edMessage = findViewById(R.id.edMessage);
        btnSend = findViewById(R.id.btnSend);

        // Configurar RecyclerView
        chatAdapter = new ChatAdapter(new ArrayList<>());
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Configurar ViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.inicializar("client-" + System.currentTimeMillis());

        // Cargar historial desde Firebase antes de suscribirse
        chatViewModel.cargarHistorial(room);

        // Suscribirse al tópico MQTT
        chatViewModel.suscribir(topic);

        // Observar mensajes
        chatViewModel.getMensajes().observe(this, this::mostrarMensajes);

        // Evento enviar mensaje
        btnSend.setOnClickListener(v -> {
            String texto = edMessage.getText().toString().trim();
            if (!texto.isEmpty()) {
                ChatMessage msg = new ChatMessage(userId, displayName, texto, System.currentTimeMillis());
                chatViewModel.enviarMensaje(topic, msg);
                edMessage.setText("");
            }
        });
    }

    /**
     * Actualiza la lista de mensajes en el RecyclerView.
     */
    private void mostrarMensajes(List<ChatMessage> mensajes) {
        chatAdapter.actualizarLista(mensajes);
        if (mensajes != null && !mensajes.isEmpty()) {
            recyclerChat.scrollToPosition(mensajes.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desconectar cliente MQTT al cerrar la actividad
        if (chatViewModel != null) {
            chatViewModel.desconectar();
        }
    }

    /**
     * Ejemplo de logout opcional desde el chat.
     */
    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}






