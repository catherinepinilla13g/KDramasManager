package com.manager.kdramas.model;


/**
 * Kdrama - Clase modelo que representa la entidad K-Drama.

 * Responsabilidades:
 * - Representar los datos persistentes y temporales de la aplicación.
 * - Facilitar el paso de información entre capas (ViewModel, View).
 * - Contener lógica de negocio simple asociada a la entidad.
 * - Ser inmutable cuando sea posible.
 */
public class Kdrama {

    // Atributos encapsulados que representan las propiedades del K-Drama
    private String id;
    private String titulo;
    private String genero;
    private String anio;
    private String capitulos;
    private String calificacion;
    private String finalizado;
    private String imagenUrl;

    /**
     * Constructor vacío requerido por ciertas operaciones
     */
    public Kdrama() {
    }

    /**
     * Constructor principal para crear una instancia con datos iniciales.
     * El estado 'finalizado' se inicializa como "0".
     * La URL de imagen se inicializa como cadena vacía.
     *
     * @param titulo       Título del K-Drama.
     * @param genero       Género principal.
     * @param anio         Año de emisión.
     * @param capitulos    Número de capítulos.
     * @param calificacion Calificación del usuario.
     */
    public Kdrama(String titulo, String genero, String anio, String capitulos, String calificacion) {
        this.titulo = titulo;
        this.genero = genero;
        this.anio = anio;
        this.capitulos = capitulos;
        this.calificacion = calificacion;
        this.finalizado = "0";
        this.imagenUrl = "";
    }

    // Métodos de acceso (getters y setters) para cada atributo

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCapitulos() {
        return capitulos;
    }

    public void setCapitulos(String capitulos) {
        this.capitulos = capitulos;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(String finalizado) {
        this.finalizado = finalizado;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Verifica si la instancia contiene los datos mínimos requeridos.
     * Se considera válida si tiene título, año y número de capítulos.
     *
     * @return true si los campos esenciales están presentes y no vacíos.
     */
    public boolean esValido() {
        return titulo != null && !titulo.trim().isEmpty() &&
                anio != null && !anio.trim().isEmpty() &&
                capitulos != null && !capitulos.trim().isEmpty();
    }

    /**
     * Retorna el estado del K-Drama en formato legible.
     * Interpreta el campo 'finalizado' como texto descriptivo según los valores definidos en strings.xml.
     *
     * @return Estado legible como texto ("Viendo", "Completado", etc.).
     */
    public String getEstadoLegible() {
        switch (finalizado) {
            case "1":
                return "Completado";
            case "2":
                return "Pendiente";
            case "3":
                return "En pausa";
            case "4":
                return "Abandonado";
            case "5":
                return "Reviendo";
            default:
                return "Viendo"; // Valor por defecto
        }
    }
}
