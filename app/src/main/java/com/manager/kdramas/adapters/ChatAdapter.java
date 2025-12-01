package com.manager.kdramas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.kdramas.R;
import com.manager.kdramas.model.ChatMessage;

import java.util.List;

/**
 * ChatAdapter - Adaptador para mostrar mensajes en el RecyclerView del chat.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> mensajes;

    public ChatAdapter(List<ChatMessage> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage msg = mensajes.get(position);
        holder.user.setText(msg.displayName);   // txtUser en item_chat_message.xml
        holder.message.setText(msg.text);       // txtMessage en item_chat_message.xml
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

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView user, message;

        ChatViewHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.txtUser);
            message = itemView.findViewById(R.id.txtMessage);
        }
    }
}





