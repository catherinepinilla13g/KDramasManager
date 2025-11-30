package com.manager.kdramas.model;

/**
 * Contact - Representa un contacto de usuario en la aplicación.

 * Campos:
 * - id: identificador único del contacto (uid de Firebase).
 * - nombre: nombre visible del contacto.
 * - email: correo electrónico del contacto.
 * - fotoUrl: URL de la foto de perfil.
 */
public class Contact {
    private String id;
    private String nombre;
    private String email;
    private String fotoUrl;

    public Contact() {
    }

    public Contact(String id, String nombre, String email, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fotoUrl = fotoUrl;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getFotoUrl() { return fotoUrl; }

    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}

