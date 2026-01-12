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
        HistorialDto item = lista.get(position);
        // Lógica: Mostrar nombre del curso o servicio según lo que venga
        String titulo = (item.getTituloCurso() != null) ? item.getTituloCurso() :
                (item.getTituloServicio() != null ? item.getTituloServicio() : "Compra sin título");

        holder.tvTitulo.setText(titulo);
        holder.tvFecha.setText(item.getFechapago());
        holder.tvPrecio.setText("$" + item.getPago());
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvPrecio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloHistorial);
            tvFecha = itemView.findViewById(R.id.tvFechaHistorial);
            tvPrecio = itemView.findViewById(R.id.tvPrecioHistorial);
        }
    }
}