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
import com.example.saber_share.R;
import com.example.saber_share.model.EstadisticasDto;
import com.example.saber_share.util.api.EstadisticasApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.local.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estadisticas extends Fragment {

    private TextView tvGanancias, tvVentas, tvDetalleCursos, tvDetalleClases;
    private SessionManager session;

    public Estadisticas() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_estadisticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new SessionManager(requireContext());

        tvGanancias = view.findViewById(R.id.tvTotalGanancias);
        tvVentas = view.findViewById(R.id.tvTotalVentas);
        tvDetalleCursos = view.findViewById(R.id.tvDetalleCursos);
        tvDetalleClases = view.findViewById(R.id.tvDetalleClases);

        cargarDatos();
    }

    private void cargarDatos() {
        EstadisticasApi api = RetrofitClient.getClient().create(EstadisticasApi.class);
        api.obtenerMisEstadisticas(session.getUserId()).enqueue(new Callback<EstadisticasDto>() {
            @Override
            public void onResponse(Call<EstadisticasDto> call, Response<EstadisticasDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EstadisticasDto datos = response.body();

                    tvGanancias.setText(String.format("$ %.2f", datos.getTotalGanancias()));
                    tvVentas.setText(String.valueOf(datos.getCantidadVentas()));
                    tvDetalleCursos.setText("Cursos: " + datos.getCursosVendidos());
                    tvDetalleClases.setText("Clases: " + datos.getClasesVendidas());
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar datos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<EstadisticasDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}