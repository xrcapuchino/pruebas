package com.example.saber_share.fragmentos.contenido.adapter;

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
import com.example.saber_share.model.Publicacion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.ViewHolder> {

    private List<Publicacion> listaOriginal; // Lista completa para el filtro
    private List<Publicacion> listaMostrada; // Lista que se ve en pantalla
    private OnItemClickListener listener;
    private List<Integer> idsComprados = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Publicacion publicacion);
    }

    public PublicacionAdapter(List<Publicacion> lista, OnItemClickListener listener) {
        this.listaOriginal = new ArrayList<>(lista);
        this.listaMostrada = lista;
        this.listener = listener;
    }

    // Método para actualizar datos desde el fragmento
    public void setDatos(List<Publicacion> nuevosDatos) {
        this.listaOriginal = new ArrayList<>(nuevosDatos);
        this.listaMostrada = new ArrayList<>(nuevosDatos);
        notifyDataSetChanged();
    }

    public void setIdsComprados(List<Integer> ids) {
        this.idsComprados = ids;
        notifyDataSetChanged();
    }

    // Método de filtrado para el buscador
    public void filtrar(String texto) {
        if (texto.isEmpty()) {
            listaMostrada = new ArrayList<>(listaOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                listaMostrada = listaOriginal.stream()
                        .filter(p -> p.getTitulo().toLowerCase().contains(texto.toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                // Versiones viejas de Android
                List<Publicacion> temp = new ArrayList<>();
                for (Publicacion p : listaOriginal) {
                    if (p.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                        temp.add(p);
                    }
                }
                listaMostrada = temp;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion item = listaMostrada.get(position);

        holder.tvTitulo.setText(item.getTitulo());
        holder.tvAutor.setText(item.getAutor());
        holder.tvCalif.setText(item.getCalificacion() + " ★");

        // Lógica de Compra Visual
        if (idsComprados.contains(item.getIdOriginal())) {
            holder.tvPrecio.setText("¡Adquirido!");
            holder.tvPrecio.setTextColor(Color.parseColor("#4CAF50")); // Verde
            holder.btnAccion.setText("Abrir");
            holder.btnAccion.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvPrecio.setText(String.format("$ %.2f MXN", item.getPrecio()));
            holder.tvPrecio.setTextColor(Color.parseColor("#20232A")); // Negro
            holder.btnAccion.setText("Pagar");
            holder.btnAccion.setBackgroundColor(Color.parseColor("#2E70FF"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnAccion.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return listaMostrada.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvAutor, tvPrecio, tvCalif;
        ImageView imgPortada;
        Button btnAccion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloPub);
            tvAutor = itemView.findViewById(R.id.tvAutorPub);
            tvPrecio = itemView.findViewById(R.id.tvPrecioPub);
            tvCalif = itemView.findViewById(R.id.tvCalificacionPub);
            imgPortada = itemView.findViewById(R.id.imgPortada);
            btnAccion = itemView.findViewById(R.id.btnAccionPub);
        }
    }
}