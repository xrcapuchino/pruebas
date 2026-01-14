package com.example.saber_share.fragmentos.contenido.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saber_share.R;
import com.example.saber_share.model.HistorialDto;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<HistorialDto> lista;

    public HistorialAdapter(List<HistorialDto> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorialDto h = lista.get(position);

        String titulo = "Sin título";
        String tipo = "Compra";

        // Lógica de títulos robusta (evita nulos)
        if (h.getCursoId() != null) {
            titulo = h.getTituloCurso() != null ? h.getTituloCurso() : "Curso #" + h.getCursoId();
            tipo = "Curso";
        } else if (h.getServicioId() != null) {
            titulo = h.getTituloServicio() != null ? h.getTituloServicio() : "Clase #" + h.getServicioId();
            tipo = "Clase 1:1";
        }

        // Asignaciones seguras
        if (holder.tvTitulo != null) holder.tvTitulo.setText(titulo);

        if (holder.tvFecha != null) {
            holder.tvFecha.setText(h.getFechapago() != null ? h.getFechapago() : "Fecha desc.");
        }

        if (holder.tvPrecio != null) {
            holder.tvPrecio.setText(String.format("$ %.2f", h.getPago() != null ? h.getPago() : 0.0));
        }

        if (holder.tvTipo != null) holder.tvTipo.setText(tipo);
    }

    @Override
    public int getItemCount() { return lista != null ? lista.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvPrecio, tvTipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Estos IDs deben coincidir EXACTAMENTE con el XML de arriba
            tvTitulo = itemView.findViewById(R.id.tvHistorialTitulo);
            tvFecha = itemView.findViewById(R.id.tvHistorialFecha);
            tvPrecio = itemView.findViewById(R.id.tvHistorialPrecio);
            tvTipo = itemView.findViewById(R.id.tvHistorialTipo);
        }
    }
}