package com.example.saber_share.fragmentos.contenido.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.dialogs.CalificarDialog;
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.model.OpinionServicioDto;
import com.example.saber_share.model.OpinionesCursoDto;
import com.example.saber_share.util.api.OpinionApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.local.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private Context context;
    private List<HistorialDto> lista;

    // Necesitamos el contexto para abrir el Dialog y Toast
    public HistorialAdapter(Context context, List<HistorialDto> lista) {
        this.context = context;
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

        String titulo = "Sin título";
        String tipo = "Compra";

        // Lógica para determinar qué es (Curso o Servicio)
        if (item.getCursoId() != null) {
            titulo = item.getTituloCurso() != null ? item.getTituloCurso() : "Curso #" + item.getCursoId();
            tipo = "Curso";
        } else if (item.getServicioId() != null) {
            titulo = item.getTituloServicio() != null ? item.getTituloServicio() : "Clase 1:1 #" + item.getServicioId();
            tipo = "Clase 1:1";
        }

        // Asignar textos a la vista
        holder.tvTitulo.setText(titulo);
        holder.tvFecha.setText(item.getFechapago() != null ? item.getFechapago() : "---");
        holder.tvPrecio.setText(String.format("$ %.2f", item.getPago() != null ? item.getPago() : 0.0));
        holder.tvTipo.setText(tipo);

        // --- LÓGICA DEL BOTÓN CALIFICAR ---
        holder.btnCalificar.setOnClickListener(v -> {
            // Verificamos que el contexto sea una Activity para poder usar SupportFragmentManager
            if (context instanceof AppCompatActivity) {
                CalificarDialog dialog = new CalificarDialog();
                dialog.setListener((estrellas, comentario) -> {
                    // Al recibir la calificación del diálogo, la enviamos al backend
                    enviarCalificacion(item, estrellas, comentario);
                });
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "CalificarDialog");
            }
        });
    }

    // Método auxiliar para enviar la calificación a la API correcta
    private void enviarCalificacion(HistorialDto item, int estrellas, String comentario) {
        OpinionApi api = RetrofitClient.getClient().create(OpinionApi.class);
        SessionManager session = new SessionManager(context);
        int miId = session.getUserId();

        if (item.getServicioId() != null) {
            // Es una CLASE 1 a 1 -> Usamos OpinionServicioDto
            OpinionServicioDto dto = new OpinionServicioDto(miId, item.getServicioId(), estrellas, comentario);

            api.calificarServicio(dto).enqueue(new Callback<OpinionServicioDto>() {
                @Override
                public void onResponse(Call<OpinionServicioDto> call, Response<OpinionServicioDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "¡Clase calificada con éxito!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al calificar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OpinionServicioDto> call, Throwable t) {
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (item.getCursoId() != null) {
            // Es un CURSO -> Usamos OpinionesCursoDto
            OpinionesCursoDto dto = new OpinionesCursoDto(miId, item.getCursoId(), estrellas, comentario);

            api.calificarCurso(dto).enqueue(new Callback<OpinionesCursoDto>() {
                @Override
                public void onResponse(Call<OpinionesCursoDto> call, Response<OpinionesCursoDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "¡Curso calificado con éxito!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al calificar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OpinionesCursoDto> call, Throwable t) {
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvPrecio, tvTipo;
        Button btnCalificar; // Nuevo botón

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvHistorialTitulo);
            tvFecha = itemView.findViewById(R.id.tvHistorialFecha);
            tvPrecio = itemView.findViewById(R.id.tvHistorialPrecio);
            tvTipo = itemView.findViewById(R.id.tvHistorialTipo);

            // Asegúrate de que este ID exista en item_historial.xml
            btnCalificar = itemView.findViewById(R.id.btnCalificarHistorial);
        }
    }
}