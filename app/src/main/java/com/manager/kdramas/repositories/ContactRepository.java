package com.manager.kdramas.repositories;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manager.kdramas.model.UserIdentity;

public class ContactRepository {
    private final DatabaseReference usersRef;
    private final DatabaseReference contactsRef;

    public ContactRepository() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users");
        contactsRef = db.getReference("contacts");
    }

    // Referencia a todos los usuarios
    public DatabaseReference getUsersRef() {
        return usersRef;
    }

    // Referencia a los contactos de un usuario
    public DatabaseReference getContactsRef(String uid) {
        return contactsRef.child(uid);
    }

    // Guardar un usuario en la lista global de usuarios
    public void saveUser(UserIdentity user) {
        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) return;
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d("ContactRepository", "Usuario guardado: " + user.getDisplayName()))
                .addOnFailureListener(e -> Log.e("ContactRepository", "Error al guardar usuario", e));
    }

    // Método para agregar contacto (guarda objeto completo en la lista de contactos del usuario)
    public void addContact(String uid, UserIdentity contact) {
        if (uid == null || uid.isEmpty() || contact == null || contact.getUserId() == null || contact.getUserId().isEmpty()) return;
        getContactsRef(uid).child(contact.getUserId()).setValue(contact)
                .addOnSuccessListener(aVoid -> Log.d("ContactRepository", "Contacto agregado: " + contact.getDisplayName()))
                .addOnFailureListener(e -> Log.e("ContactRepository", "Error al agregar contacto", e));
    }

    // Método para eliminar contacto
    public void removeContact(String uid, String contactUid) {
        if (uid == null || uid.isEmpty() || contactUid == null || contactUid.isEmpty()) return;
        getContactsRef(uid).child(contactUid).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("ContactRepository", "Contacto eliminado: " + contactUid))
                .addOnFailureListener(e -> Log.e("ContactRepository", "Error al eliminar contacto", e));
    }
}







