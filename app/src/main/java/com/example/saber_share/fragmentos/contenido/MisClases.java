package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.HistorialAdapter; // Reusamos este adapter que ya funciona
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.util.api.HistorialApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.local.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisClases extends Fragment {

    private RecyclerView rvAgenda;
    private TextView tvVacio;
    private SessionManager sessionManager;

    public MisClases() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Reutilizamos el layout de agenda o historial que tenga un RecyclerView
        return inflater.inflate(R.layout.fragment_main_agendar_clase, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        // Ajustamos textos del layout reutilizado
        TextView titulo = view.findViewById(R.id.tvTituloAgendar);
        if(titulo != null) titulo.setText("Mi Agenda");

        View sub = view.findViewById(R.id.tvSubtituloServicio);
        if(sub != null) sub.setVisibility(View.GONE);

        rvAgenda = view.findViewById(R.id.rvHorarios); // ID reusado
        tvVacio = view.findViewById(R.id.tvSinHorarios); // ID reusado

        rvAgenda.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarAgenda();
    }

    private void cargarAgenda() {
        int miId = sessionManager.getUserId();
        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);

        // Obtenemos todo el historial y filtramos solo lo que son Servicios (Clases)
        api.historialPorUsuario(miId).enqueue(new Callback<List<HistorialDto>>() {
            @Override
            public void onResponse(Call<List<HistorialDto>> call, Response<List<HistorialDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HistorialDto> soloClases = new ArrayList<>();

                    for (HistorialDto h : response.body()) {
                        // Filtramos: Si tiene servicioId, es una clase agendada
                        if (h.getServicioId() != null) {
                            soloClases.add(h);
                        }
                    }

                    if (soloClases.isEmpty()) {
                        tvVacio.setText("No tienes clases agendadas.");
                        tvVacio.setVisibility(View.VISIBLE);
                        rvAgenda.setVisibility(View.GONE);
                    } else {
                        tvVacio.setVisibility(View.GONE);
                        rvAgenda.setVisibility(View.VISIBLE);
                        // Usamos el HistorialAdapter que ya arreglamos y sabe mostrar títulos
                        rvAgenda.setAdapter(new HistorialAdapter(soloClases));
                    }
                }
            }
            @Override public void onFailure(Call<List<HistorialDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error cargando agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }
}