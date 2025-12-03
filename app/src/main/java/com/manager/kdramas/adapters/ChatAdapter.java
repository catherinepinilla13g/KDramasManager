package com.manager.kdramas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.manager.kdramas.R;
import com.manager.kdramas.model.ChatMessage;

import java.util.List;

/**
 * ChatAdapter - Adaptador para mostrar mensajes en el RecyclerView del chat.
 * Distingue entre mensajes enviados por el usuario actual y los recibidos.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mensajes;
    private final String currentUid;

    // Tipos de vista
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> mensajes) {
        this.mensajes = mensajes;
        // UID del usuario actual (para distinguir mensajes)
        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = mensajes.get(position);
        if (msg != null && msg.userId != null && msg.userId.equals(currentUid)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = mensajes.get(position);
        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).bind(msg);
        } else if (holder instanceof ReceivedViewHolder) {
            ((ReceivedViewHolder) holder).bind(msg);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes != null ? mensajes.size() : 0;
    }

    /**
     * Actualiza la lista de mensajes y refresca la vista.
     */
    public void actualizarLista(List<ChatMessage> nuevaLista) {
        this.mensajes = nuevaLista;
        notifyDataSetChanged();
    }

    // ViewHolder para mensajes enviados
    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        SentViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.txtMessageSent);
        }

        void bind(ChatMessage msg) {
            message.setText(msg.text != null ? msg.text : "");
        }
    }

    // ViewHolder para mensajes recibidos
    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView user, message;

        ReceivedViewHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.txtUserReceived);
            message = itemView.findViewById(R.id.txtMessageReceived);
        }

        void bind(ChatMessage msg) {
            user.setText(msg.displayName != null ? msg.displayName : "Anon");
            message.setText(msg.text != null ? msg.text : "");
        }
    }
}







