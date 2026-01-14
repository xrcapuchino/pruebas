package com.example.saber_share.fragmentos.contenido.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.model.Publicacion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {

    private Context context;
    private List<Publicacion> listaOriginal;
    private List<Publicacion> listaFiltrada;
    private int usuarioActualId;
    private OnItemClickListener listener;

    private Set<Integer> cursosComprados = new HashSet<>();
    private Set<Integer> serviciosComprados = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(Publicacion publicacion);
    }

    public PublicacionAdapter(Context context, List<Publicacion> lista, int usuarioActualId, OnItemClickListener listener) {
        this.context = context;
        this.listaOriginal = lista;
        this.listaFiltrada = new ArrayList<>(lista);
        this.usuarioActualId = usuarioActualId;
        this.listener = listener;
    }

    // CORRECCIÓN: Usamos getCursoId() directo
    public void setCompras(List<HistorialDto> historial) {
        cursosComprados.clear();
        serviciosComprados.clear();
        if (historial != null) {
            for (HistorialDto h : historial) {
                if (h.getCursoId() != null) {
                    cursosComprados.add(h.getCursoId());
                }
                if (h.getServicioId() != null) {
                    serviciosComprados.add(h.getServicioId());
                }
            }
        }
        notifyDataSetChanged();
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

        String prefijo = Publicacion.TIPO_CURSO.equals(p.getTipo()) ? "Curso: " : "Clase 1 a 1: ";
        holder.tvTitulo.setText(prefijo + p.getTitulo());
        holder.tvPrecio.setText(String.format("$ %.2f MXN", p.getPrecio()));
        String calif = (p.getCalificacion() != null && !p.getCalificacion().equals("0")) ? p.getCalificacion() : "N/A";
        holder.tvCalificacion.setText(calif + " ★");

        boolean esMio = p.getIdAutor() == usuarioActualId;
        boolean comprado = false;
        if (Publicacion.TIPO_CURSO.equals(p.getTipo())) {
            comprado = cursosComprados.contains(p.getIdOriginal());
        } else {
            comprado = serviciosComprados.contains(p.getIdOriginal());
        }

        if (esMio) {
            holder.tvAutor.setText("Hecho por ti");
            holder.btnAccion.setText("Gestionar");
            holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        } else if (comprado) {
            holder.tvAutor.setText("Por: " + (p.getAutor() != null ? p.getAutor() : "Anónimo"));
            holder.btnAccion.setText("Ver");
            holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else {
            holder.tvAutor.setText("Por: " + (p.getAutor() != null ? p.getAutor() : "Anónimo"));
            holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E70FF")));

            if (Publicacion.TIPO_CURSO.equals(p.getTipo())) {
                holder.btnAccion.setText("Pagar");
            } else {
                holder.btnAccion.setText("Agendar");
            }
        }

        if (Publicacion.TIPO_CURSO.equals(p.getTipo())) {
            holder.imgPortada.setImageResource(R.drawable.img);
        } else {
            holder.imgPortada.setImageResource(R.drawable.img_1);
        }

        holder.btnAccion.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(p);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(p);
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