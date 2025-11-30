package com.manager.kdramas.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.manager.kdramas.model.Contact;
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

    private final MutableLiveData<List<Contact>> contactos =
            new MutableLiveData<>(new ArrayList<>());
    private final ContactRepository repository = new ContactRepository();

    public LiveData<List<Contact>> getContactos() {
        return contactos;
    }

    // Cargar contactos desde Firebase
    public void cargarContactos() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null ||
                FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            // Invitado: no carga contactos
            contactos.setValue(new ArrayList<>());
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.getContactsRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Contact> lista = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String contactUid = child.getKey();

                    repository.getUsersRef().child(contactUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnap) {
                                    Contact c = userSnap.getValue(Contact.class);
                                    if (c != null) {
                                        c.setId(contactUid);
                                        lista.add(c);
                                    }
                                    // Actualizar lista completa cada vez que se obtiene un contacto
                                    contactos.setValue(new ArrayList<>(lista));
                                }

                                @Override public void onCancelled(DatabaseError error) {}
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // Agregar contacto
    public void agregarContacto(String contactUid) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null ||
                FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.addContact(uid, contactUid);
    }

    // Eliminar contacto
    public void eliminarContacto(String contactUid) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null ||
                FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.removeContact(uid, contactUid);
    }
}




