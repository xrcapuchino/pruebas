package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saber_share.R;
import com.example.saber_share.model.AgendaDto;
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.model.Publicacion;
import com.example.saber_share.util.api.AgendaApi;
import com.example.saber_share.util.api.HistorialApi;
import com.example.saber_share.util.api.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerAlumnos extends Fragment {

    private RecyclerView rv;
    private int idOriginal;
    private String tipo;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle s) {
        return i.inflate(R.layout.fragment_main_ver_alumnos, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rvAlumnos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            idOriginal = getArguments().getInt("idOriginal");
            tipo = getArguments().getString("tipo");

            if (Publicacion.TIPO_CURSO.equals(tipo)) {
                cargarAlumnosCurso();
            } else {
                cargarAlumnosClase();
            }
        }
    }

    private void cargarAlumnosCurso() {
        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);
        api.getAlumnosPorCurso(idOriginal).enqueue(new Callback<List<HistorialDto>>() {
            @Override
            public void onResponse(Call<List<HistorialDto>> call, Response<List<HistorialDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Mapear a una lista de Strings simple por ahora
                    List<String> nombres = new ArrayList<>();
                    for (HistorialDto h : response.body()) {
                        // Aquí asumimos que el backend envía el nombre del usuario en el DTO
                        // Si no, habría que hacer otra llamada o mostrar el ID
                        nombres.add("Usuario ID: " + h.getUsuarioId() + " (Comprado el " + h.getFechapago() + ")");
                    }
                    mostrarLista(nombres);
                }
            }
            @Override public void onFailure(Call<List<HistorialDto>> call, Throwable t) {}
        });
    }

    private void cargarAlumnosClase() {
        AgendaApi api = RetrofitClient.getClient().create(AgendaApi.class);
        api.getSlotsPorServicio(idOriginal).enqueue(new Callback<List<AgendaDto>>() {
            @Override
            public void onResponse(Call<List<AgendaDto>> call, Response<List<AgendaDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> nombres = new ArrayList<>();
                    for (AgendaDto a : response.body()) {
                        if ("RESERVADA".equals(a.getEstado())) {
                            String alumno = a.getNombreAlumno() != null ? a.getNombreAlumno() : "ID: " + a.getAlumnoId();
                            nombres.add(alumno + " - " + a.getFecha() + " " + a.getHora());
                        }
                    }
                    mostrarLista(nombres);
                }
            }
            @Override public void onFailure(Call<List<AgendaDto>> call, Throwable t) {}
        });
    }

    private void mostrarLista(List<String> datos) {
        // Usamos un adaptador simple de Android para no complicarnos creando otro XML
        // Pero idealmente crearías un AlumnoAdapter.
        // Aquí simulamos uno rápido con un RecyclerView Adapter básico
        rv.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                android.widget.TextView tv = new android.widget.TextView(getContext());
                tv.setPadding(32, 32, 32, 32);
                tv.setTextSize(18f);
                return new ViewHolder(tv);
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                ((android.widget.TextView) holder.itemView).setText(datos.get(position));
            }
            @Override public int getItemCount() { return datos.size(); }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) { super(v); }
    }
}