package com.manager.kdramas.model;

/**
 * UserIdentity - Representa la identidad del usuario en el sistema.
 * Se utiliza tanto para el chat como para perfil y contactos.
 */
public class UserIdentity {
    private String userId;
    private String displayName;
    private String email;
    private String photoUrl;
    private boolean isAnonymous;

    public UserIdentity() {}

    // Constructor básico (invitado)
    public UserIdentity(String id, String name) {
        this.userId = id;
        this.displayName = name;
        this.isAnonymous = true;
    }

    public UserIdentity(String id, String name, String email, String photoUrl) {
        this.userId = id;
        this.displayName = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.isAnonymous = false;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }
    public boolean isAnonymous() { return isAnonymous; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }
}


