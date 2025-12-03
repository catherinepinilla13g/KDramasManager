package com.manager.kdramas.model;

/**
 * Kdrama - Clase modelo que representa la entidad K-Drama.
 *
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
    private String finalizado;     // Estado del K-Drama (ej. "1"=Completado, "2"=Pendiente, etc.)
    private String imagenUrl;      // URL de imagen opcional
    private String urlPlataforma;  // URL de plataforma (Netflix, Viki, etc.)
    private String urlTrailer;     // URL de trailer (YouTube, etc.)

    /**
     * Constructor vacío requerido por ciertas operaciones.
     */
    public Kdrama() {
    }

    /**
     * Constructor principal para crear una instancia con datos iniciales.
     * El estado 'finalizado' se recibe como parámetro en lugar de asignarse por defecto.
     * Las URLs opcionales se inicializan como cadena vacía.
     *
     * @param titulo       Título del K-Drama.
     * @param genero       Género principal.
     * @param anio         Año de emisión.
     * @param capitulos    Número de capítulos.
     * @param calificacion Calificación del usuario.
     * @param finalizado   Estado del K-Drama (ej. "1"=Completado, "2"=Pendiente, etc.).
     */
    public Kdrama(String titulo, String genero, String anio, String capitulos, String calificacion, String finalizado) {
        this.titulo = titulo;
        this.genero = genero;
        this.anio = anio;
        this.capitulos = capitulos;
        this.calificacion = calificacion;
        this.finalizado = finalizado; // se asigna desde el Spinner en el formulario
        this.imagenUrl = "";
        this.urlPlataforma = "";
        this.urlTrailer = "";
    }

    // Métodos de acceso (getters y setters) para cada atributo

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public String getCapitulos() { return capitulos; }
    public void setCapitulos(String capitulos) { this.capitulos = capitulos; }

    public String getCalificacion() { return calificacion; }
    public void setCalificacion(String calificacion) { this.calificacion = calificacion; }

    public String getFinalizado() { return finalizado; }
    public void setFinalizado(String finalizado) { this.finalizado = finalizado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getUrlPlataforma() { return urlPlataforma; }
    public void setUrlPlataforma(String urlPlataforma) { this.urlPlataforma = urlPlataforma; }

    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }

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
        if (finalizado == null) return "";
        switch (finalizado) {
            case "1": return "Completado";
            case "2": return "Pendiente";
            case "3": return "En pausa";
            case "4": return "Abandonado";
            case "5": return "Reviendo";
            case "0": return "Viendo";
            default:  return "";
        }
    }
}

