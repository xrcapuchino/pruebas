package com.example.saber_share.fragmentos.contenido.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.model.Publicacion;

import java.util.ArrayList;
import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {

    private Context context;
    private List<Publicacion> listaOriginal;
    private List<Publicacion> listaFiltrada;

    public PublicacionAdapter(Context context, List<Publicacion> lista) {
        this.context = context;
        this.listaOriginal = lista;
        this.listaFiltrada = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Publicacion p = listaFiltrada.get(position);

        // 1. Configurar Título con Prefijo (Curso: ... o Clase 1 a 1: ...)
        String prefijo = Publicacion.TIPO_CURSO.equals(p.getTipo()) ? "Curso: " : "Clase 1 a 1: ";
        holder.tvTitulo.setText(prefijo + p.getTitulo());

        // 2. Configurar resto de datos
        holder.tvAutor.setText("Por: " + (p.getAutor() != null ? p.getAutor() : "Anónimo"));
        holder.tvPrecio.setText(String.format("$ %.2f MXN", p.getPrecio()));

        // Calificación (solo si existe, si no ponemos N/A)
        String calif = (p.getCalificacion() != null && !p.getCalificacion().equals("0")) ? p.getCalificacion() : "N/A";
        holder.tvCalificacion.setText(calif + " ★");

        // 3. Configurar Imagen y Botón según el tipo
        if (Publicacion.TIPO_CURSO.equals(p.getTipo())) {
            holder.btnAccion.setText("Pagar");
            holder.imgPortada.setImageResource(R.drawable.img); // Imagen default curso
        } else {
            holder.btnAccion.setText("Agendar");
            holder.imgPortada.setImageResource(R.drawable.img_1); // Imagen default clase
        }

        // 4. Click en el botón
        holder.btnAccion.setOnClickListener(v -> {
            Toast.makeText(context, "Comprando: " + p.getTitulo(), Toast.LENGTH_SHORT).show();
            // Aquí iría la navegación al detalle de la compra
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public void setDatos(List<Publicacion> nuevosDatos) {
        this.listaOriginal = nuevosDatos;
        this.listaFiltrada = new ArrayList<>(nuevosDatos);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        if (texto.isEmpty()) {
            listaFiltrada = new ArrayList<>(listaOriginal);
        } else {
            List<Publicacion> temp = new ArrayList<>();
            for (Publicacion p : listaOriginal) {
                if (p.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                    temp.add(p);
                }
            }
            listaFiltrada = temp;
        }
        notifyDataSetChanged();
    }

    static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPortada;
        TextView tvTitulo, tvAutor, tvCalificacion, tvPrecio;
        Button btnAccion;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPortada = itemView.findViewById(R.id.imgPortada);
            tvTitulo = itemView.findViewById(R.id.tvTituloPub);
            tvAutor = itemView.findViewById(R.id.tvAutorPub);
            tvCalificacion = itemView.findViewById(R.id.tvCalificacionPub);
            tvPrecio = itemView.findViewById(R.id.tvPrecioPub);
            btnAccion = itemView.findViewById(R.id.btnAccionPub);
        }
    }
}