package com.manager.kdramas.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactRepository {
    private final DatabaseReference usersRef;
    private final DatabaseReference contactsRef;

    public ContactRepository() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users");
        contactsRef = db.getReference("contacts");
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }

    public DatabaseReference getContactsRef(String uid) {
        return contactsRef.child(uid);
    }

    // Método para agregar contacto
    public void addContact(String uid, String contactUid) {
        getContactsRef(uid).child(contactUid).setValue(true);
    }

    //Método para eliminar contacto
    public void removeContact(String uid, String contactUid) {
        getContactsRef(uid).child(contactUid).removeValue();
    }
}




