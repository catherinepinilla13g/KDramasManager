package com.manager.kdramas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

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

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerChat;
    private EditText edMessage;
    private ImageButton btnSend;
    private ImageButton btnMenu;

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

        // Identidad del usuario
        UserIdentity identity = AuthHelper.getIdentity();
        if (identity != null) {
            userId = identity.getUserId();
            displayName = identity.getDisplayName() != null ? identity.getDisplayName() : "Usuario";
        } else {
            userId = "anon";
            displayName = "Invitado";
        }

        // Vincular componentes
        recyclerChat = findViewById(R.id.recyclerChat);
        edMessage = findViewById(R.id.edMessage);
        btnSend = findViewById(R.id.btnSend);
        btnMenu = findViewById(R.id.btnMenu);

        // Configurar RecyclerView
        chatAdapter = new ChatAdapter(new ArrayList<>());
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Configurar ViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.inicializar("client-" + System.currentTimeMillis());

        // Cargar historial desde Firebase
        chatViewModel.cargarHistorial(room);

        // Suscribirse al tópico MQTT
        chatViewModel.suscribir(topic);

        // Observar mensajes
        chatViewModel.getMensajes().observe(this, this::mostrarMensajes);

        // Enviar mensaje
        btnSend.setOnClickListener(v -> {
            String texto = edMessage.getText().toString().trim();
            if (!texto.isEmpty()) {
                ChatMessage msg = new ChatMessage(userId, displayName, texto, System.currentTimeMillis());
                chatViewModel.enviarMensaje(topic, msg);
                edMessage.setText("");
            }
        });

        // Abrir menú
        btnMenu.setOnClickListener(v -> mostrarMenu());
    }

    private void mostrarMensajes(List<ChatMessage> mensajes) {
        chatAdapter.actualizarLista(mensajes);
        if (mensajes != null && !mensajes.isEmpty()) {
            recyclerChat.scrollToPosition(mensajes.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatViewModel != null) {
            chatViewModel.desconectar();
        }
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void mostrarMenu() {
        PopupMenu popup = new PopupMenu(this, btnMenu);
        popup.getMenu().add("Perfil").setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        });
        popup.getMenu().add("Contactos").setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, ContactActivity.class));
            return true;
        });
        popup.getMenu().add("Vaciar chat (solo yo)").setOnMenuItemClickListener(item -> {
            chatAdapter.actualizarLista(new ArrayList<>()); // limpiar solo local
            return true;
        });
        popup.getMenu().add("Vaciar chat (todos)").setOnMenuItemClickListener(item -> {
            chatViewModel.borrarMensajesGlobal(room);
            return true;
        });
        popup.getMenu().add("Cerrar sesión").setOnMenuItemClickListener(item -> {
            cerrarSesion();
            return true;
        });
        popup.show();
    }
}




