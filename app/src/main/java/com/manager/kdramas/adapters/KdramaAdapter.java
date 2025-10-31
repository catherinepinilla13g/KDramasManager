package com.manager.kdramas.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.manager.kdramas.R;
import com.manager.kdramas.model.Kdrama;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una lista de K-Dramas en un RecyclerView.

 * Responsabilidades:
 * - Renderizar datos del ViewModel en la interfaz.
 * - Delegar eventos de interacción al componente que lo contiene (Activity o Fragment).
 * - No contiene lógica de negocio.
 */
public class KdramaAdapter extends RecyclerView.Adapter<KdramaAdapter.ViewHolder> {

    // Lista de K-Dramas a mostrar, proporcionada por la capa ViewModel
    private List<Kdrama> listaKdramas;

    // Listener para notificar eventos de clic al componente contenedor
    private OnItemClickListener listener;

    /**
     * Interface para comunicar eventos de clic desde el Adapter hacia la Activity o Fragment.
     */
    public interface OnItemClickListener {
        /**
         * Notifica al listener cuando el usuario hace clic sobre un item.
         *
         * @param kdrama Instancia seleccionada del modelo Kdrama.
         */
        void onItemClick(Kdrama kdrama);
    }

    /**
     * Constructor del adaptador.
     *
     * @param listener Listener que gestiona los eventos de clic sobre los items.
     */
    public KdramaAdapter(OnItemClickListener listener) {
        this.listaKdramas = new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Infla el layout XML correspondiente a un item de K-Drama.
     * Invocado por el RecyclerView para crear nuevos ViewHolders.

     * @param parent ViewGroup padre donde se insertará el nuevo item.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return ViewHolder con las vistas inicializadas.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kdrama, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Asocia los datos del modelo con el ViewHolder correspondiente.
     *
     * @param holder ViewHolder que representa el item.
     * @param position Posición del item en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kdrama kdrama = listaKdramas.get(position);
        holder.enlazarDatos(kdrama, listener);
    }

    /**
     * Retorna la cantidad de elementos en la lista.
     * Utilizado por el RecyclerView para determinar cuántos items renderizar.
     *
     * @return Número total de K-Dramas en la lista.
     */
    @Override
    public int getItemCount() {
        return listaKdramas.size();
    }

    /**
     * Reemplaza la lista actual de K-Dramas por una nueva y actualiza la vista.
     *
     * @param nuevaLista Nueva lista de K-Dramas a mostrar.
     */
    public void actualizarLista(List<Kdrama> nuevaLista) {
        this.listaKdramas.clear();
        this.listaKdramas.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder que cachea las vistas del item para mejorar el rendimiento del RecyclerView.
     * Evita llamadas repetidas a findViewById durante el scroll.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Referencias a las vistas del layout del item
        private ImageView imgKdrama;
        private TextView txtTitulo, txtGenero, txtAnio, txtCapitulos, txtEstado;
        private RatingBar ratingCalificacion;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista raíz del item inflado desde XML.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            inicializarViews();
        }

        /**
         * Inicializa las referencias a las vistas del layout.
         */
        private void inicializarViews() {
            imgKdrama = itemView.findViewById(R.id.imgKdrama);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtGenero = itemView.findViewById(R.id.txtGenero);
            txtAnio = itemView.findViewById(R.id.txtAnio);
            txtCapitulos = itemView.findViewById(R.id.txtCapitulos);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            ratingCalificacion = itemView.findViewById(R.id.ratingCalificacion);
        }

        /**
         * Asocia los datos del modelo Kdrama con las vistas del item.
         * También configura el evento de clic para notificar al listener.
         *
         * @param kdrama Instancia del modelo Kdrama a mostrar.
         * @param listener Listener que maneja el clic sobre el item.
         */
        public void enlazarDatos(Kdrama kdrama, OnItemClickListener listener) {
            establecerTextos(kdrama);
            cargarImagen(kdrama.getImagenUrl());
            configurarCalificacion(kdrama.getCalificacion());
            configurarClick(kdrama, listener);
        }

        /**
         * Establece los textos en las vistas del item.
         *
         * @param kdrama K-Drama con los datos a mostrar.
         */
        private void establecerTextos(Kdrama kdrama) {
            txtTitulo.setText(kdrama.getTitulo());
            txtGenero.setText(kdrama.getGenero());
            txtAnio.setText(String.format("Año: %s", kdrama.getAnio()));
            txtCapitulos.setText(String.format("%s capítulos", kdrama.getCapitulos()));
            txtEstado.setText(kdrama.getEstadoLegible());
        }

        /**
         * Carga la imagen del K-Drama usando Glide.
         * Si la URL es inválida, se muestra una imagen por defecto.
         *
         * @param imagenUrl URL de la imagen a cargar.
         */
        private void cargarImagen(String imagenUrl) {
            if (imagenUrl != null && !imagenUrl.trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imagenUrl)
                        .placeholder(R.drawable.ic_kdrama_default)
                        .error(R.drawable.ic_kdrama_default)
                        .centerCrop()
                        .into(imgKdrama);
            } else {
                imgKdrama.setImageResource(R.drawable.ic_kdrama_default);
            }
        }

        /**
         * Establece la calificación en el RatingBar.
         * Si el valor no es numérico, se asigna 0 por defecto.
         *
         * @param calificacion Calificación en formato String.
         */
        private void configurarCalificacion(String calificacion) {
            try {
                float rating = Float.parseFloat(calificacion);
                ratingCalificacion.setRating(rating);
            } catch (NumberFormatException e) {
                ratingCalificacion.setRating(0);
            }
        }

        /**
         * Configura el evento de clic sobre el item.
         * Notifica al listener con el K-Drama asociado.
         *
         * @param kdrama Instancia del modelo Kdrama.
         * @param listener Listener que gestiona el evento de clic.
         */
        private void configurarClick(Kdrama kdrama, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(kdrama);
                    }
                }
            });
        }
    }
}
