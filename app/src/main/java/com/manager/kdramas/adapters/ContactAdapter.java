package com.manager.kdramas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.kdramas.R;
import com.manager.kdramas.model.Contact;

import java.util.List;

/**
 * ContactAdapter - Adaptador para mostrar contactos en el RecyclerView.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactos;
    private final OnContactClickListener clickListener;
    private final OnContactLongClickListener longClickListener;

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    public interface OnContactLongClickListener {
        void onContactLongClick(Contact contact);
    }

    public ContactAdapter(List<Contact> contactos,
                          OnContactClickListener clickListener,
                          OnContactLongClickListener longClickListener) {
        this.contactos = contactos;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactos.get(position);

        holder.nombre.setText(contact.getNombre() != null ? contact.getNombre() : "Sin nombre");
        holder.email.setText(contact.getEmail() != null ? contact.getEmail() : "");

        Glide.with(holder.itemView.getContext())
                .load(contact.getFotoUrl() != null ? contact.getFotoUrl() : R.drawable.ic_person)
                .placeholder(R.drawable.ic_person)
                .into(holder.foto);

        holder.itemView.setOnClickListener(v -> clickListener.onContactClick(contact));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onContactLongClick(contact);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return contactos != null ? contactos.size() : 0;
    }

    public void actualizarLista(List<Contact> nuevaLista) {
        this.contactos = nuevaLista;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, email;
        ImageView foto;

        ContactViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombre);
            email = itemView.findViewById(R.id.txtEmail);
            foto = itemView.findViewById(R.id.imgFoto);
        }
    }
}




