package com.manager.kdramas.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
 *
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kdrama, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kdrama kdrama = listaKdramas.get(position);
        holder.enlazarDatos(kdrama, listener);
    }

    @Override
    public int getItemCount() {
        return listaKdramas.size();
    }

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
        private ImageButton btnNetflix, btnTrailer; // nuevos iconos

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            inicializarViews();
        }

        private void inicializarViews() {
            imgKdrama = itemView.findViewById(R.id.imgKdrama);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtGenero = itemView.findViewById(R.id.txtGenero);
            txtAnio = itemView.findViewById(R.id.txtAnio);
            txtCapitulos = itemView.findViewById(R.id.txtCapitulos);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            ratingCalificacion = itemView.findViewById(R.id.ratingCalificacion);

            // Inicializar botones de iconos
            btnNetflix = itemView.findViewById(R.id.btnNetflix);
            btnTrailer = itemView.findViewById(R.id.btnTrailer);
        }

        public void enlazarDatos(Kdrama kdrama, OnItemClickListener listener) {
            establecerTextos(kdrama);
            cargarImagen(kdrama.getImagenUrl());
            configurarCalificacion(kdrama.getCalificacion());
            configurarClick(kdrama, listener);
            configurarAccionesExternas(kdrama); // nuevo
        }

        private void establecerTextos(Kdrama kdrama) {
            txtTitulo.setText(kdrama.getTitulo());
            txtGenero.setText(kdrama.getGenero());
            txtAnio.setText(String.format("Año: %s", kdrama.getAnio()));
            txtCapitulos.setText(String.format("%s capítulos", kdrama.getCapitulos()));
            txtEstado.setText(kdrama.getEstadoLegible());
        }

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

        private void configurarCalificacion(String calificacion) {
            try {
                float rating = Float.parseFloat(calificacion);
                ratingCalificacion.setRating(rating);
            } catch (NumberFormatException e) {
                ratingCalificacion.setRating(0);
            }
        }

        private void configurarClick(Kdrama kdrama, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(kdrama);
                }
            });
        }

        /**
         * Configura los iconos de plataforma y trailer para abrir URLs externas.
         *
         * @param kdrama Instancia del modelo Kdrama con las URLs.
         */
        private void configurarAccionesExternas(Kdrama kdrama) {
            Context context = itemView.getContext();

            // Abrir plataforma (ej. Netflix)
            btnNetflix.setOnClickListener(v -> {
                String urlPlataforma = kdrama.getUrlPlataforma();
                if (urlPlataforma != null && !urlPlataforma.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPlataforma));
                    context.startActivity(intent);
                }
            });

            // Abrir trailer (ej. YouTube)
            btnTrailer.setOnClickListener(v -> {
                String urlTrailer = kdrama.getUrlTrailer();
                if (urlTrailer != null && !urlTrailer.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTrailer));
                    context.startActivity(intent);
                }
            });
        }
    }
}

