package com.manager.kdramas.model;

/**
 * UserIdentity - Representa la identidad del usuario en el sistema.
 * Se utiliza tanto para el chat como para perfil y contactos.
 */
public class UserIdentity {
    public String userId;
    public String displayName;
    public String email;
    public String photoUrl;
    public boolean isAnonymous;

    // Constructor básico (para invitados)
    public UserIdentity(String id, String name) {
        this.userId = id;
        this.displayName = name;
        this.isAnonymous = true;
    }

    // Constructor completo (para usuarios autenticados)
    public UserIdentity(String id, String name, String email, String photoUrl) {
        this.userId = id;
        this.displayName = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.isAnonymous = false;
    }

    // Constructor vacío requerido por Firebase
    public UserIdentity() {}
}

