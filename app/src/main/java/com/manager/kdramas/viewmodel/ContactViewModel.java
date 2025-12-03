package com.manager.kdramas.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.manager.kdramas.model.UserIdentity;
import com.manager.kdramas.repositories.ContactRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ContactViewModel - Maneja la lógica de contactos.
 * - Carga lista de contactos desde Firebase.
 * - Permite agregar y eliminar contactos.
 * - Expone LiveData para que la UI observe cambios.
 */
public class ContactViewModel extends ViewModel {

    private final MutableLiveData<List<UserIdentity>> contactos =
            new MutableLiveData<>(new ArrayList<>());
    private final ContactRepository repository = new ContactRepository();

    public LiveData<List<UserIdentity>> getContactos() {
        return contactos;
    }

    // Cargar contactos desde Firebase
    public void cargarContactos() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null || auth.getCurrentUser().isAnonymous()) {
            contactos.setValue(new ArrayList<>());
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        repository.getContactsRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<UserIdentity> lista = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Object raw = child.getValue();
                    if (raw instanceof Boolean) {
                        continue;
                    }
                    UserIdentity c = child.getValue(UserIdentity.class);
                    if (c != null) {
                        c.setUserId(child.getKey());
                        lista.add(c);
                    }
                }
                contactos.setValue(lista);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }

    // Agregar contacto
    public void agregarContacto(UserIdentity contact) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null || auth.getCurrentUser().isAnonymous()) return;

        String uid = auth.getCurrentUser().getUid();
        repository.addContact(uid, contact);
    }

    // Eliminar contacto
    public void eliminarContacto(String contactUid) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null || auth.getCurrentUser().isAnonymous()) return;

        String uid = auth.getCurrentUser().getUid();
        repository.removeContact(uid, contactUid);
    }
}









