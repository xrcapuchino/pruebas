package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.HistorialAdapter;
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.HistorialApi;
import com.example.saber_share.util.local.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Historial extends Fragment {

    private RecyclerView rvHistorial;
    private SessionManager session;

    public Historial() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvHistorial = view.findViewById(R.id.rvHistorial);
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        session = new SessionManager(requireContext());

        cargarHistorial();
    }

    private void cargarHistorial() {
        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);
        int miId = session.getUserId();

        api.historialPorUsuario(miId).enqueue(new Callback<List<HistorialDto>>() {
            @Override
            public void onResponse(Call<List<HistorialDto>> call, Response<List<HistorialDto>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<HistorialDto> misCompras = response.body();

                    // --- CORRECCIÓN AQUÍ ---
                    // Agregamos getContext() como primer parámetro
                    if (getContext() != null) {
                        rvHistorial.setAdapter(new HistorialAdapter(getContext(), misCompras));
                    }
                } else {
                    Toast.makeText(getContext(), "No se pudo cargar el historial", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<HistorialDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}