package com.example.saber_share.fragmentos.contenido;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.saber_share.Cuenta;
import com.example.saber_share.R;
import com.example.saber_share.model.CursoDto;
import com.example.saber_share.model.ServicioDto;
import com.example.saber_share.model.UsuarioDto;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.ServicioApi;
import com.example.saber_share.util.api.UsuarioApi;
import com.example.saber_share.util.local.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends Fragment {

    // UI Elements
    private EditText etNombre, etApellido, etCorreo;
    private TextView tvNombreCompleto, tvCorreoPerfil, tvCountCursos, tvCountClases, tvMetodoPagoStatus;
    private Button btnGuardar, btnCerrarSesion;
    private Button btnHistorial, btnEstadisticas, btnVerAgenda, btnGestionarTarjetas;

    private SessionManager sessionManager;
    private int miId;
    private UsuarioDto usuarioActual;

    public Perfil() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        miId = sessionManager.getUserId();

        // 1. Vincular Vistas con los IDs de TU XML
        etNombre = view.findViewById(R.id.etNombrePublico);
        etApellido = view.findViewById(R.id.etApellidoPublico);
        etCorreo = view.findViewById(R.id.etCorreoPublico);

        tvNombreCompleto = view.findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = view.findViewById(R.id.tvCorreoPerfil);
        tvCountCursos = view.findViewById(R.id.tvCountCursos);
        tvCountClases = view.findViewById(R.id.tvCountClases);
        tvMetodoPagoStatus = view.findViewById(R.id.tvMetodoPagoStatus);

        btnGuardar = view.findViewById(R.id.btnGuardarPerfil);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        btnHistorial = view.findViewById(R.id.btnHistorial);
        btnEstadisticas = view.findViewById(R.id.btnEstadisticas);
        btnVerAgenda = view.findViewById(R.id.btnVerAgenda);
        btnGestionarTarjetas = view.findViewById(R.id.btnGestionarTarjetas);

        // 2. Cargar Datos
        cargarDatosUsuario();
        cargarEstadisticas();

        // 3. Listeners
        btnGuardar.setOnClickListener(v -> actualizarDatos());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // Navegación (Asegúrate que estos IDs existan en main_nav.xml)
        if(btnHistorial != null)
            btnHistorial.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.historial));

        if(btnEstadisticas != null) {
            btnEstadisticas.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.estadisticasFragment)
            );
        }

        if(btnVerAgenda != null)
            btnVerAgenda.setOnClickListener(v -> {
                // Antes iba a gestionarAgenda (Teacher View)
                // Ahora va a misClases (Student/User View)
                Navigation.findNavController(v).navigate(R.id.misClases);
            });

        if(btnGestionarTarjetas != null)
            btnGestionarTarjetas.setOnClickListener(v -> {
                // Navegar a administrar tarjetas
                // Navigation.findNavController(v).navigate(R.id.administrarTarjetas);
                Toast.makeText(getContext(), "Gestión de tarjetas", Toast.LENGTH_SHORT).show();
            });
    }

    private void cargarDatosUsuario() {
        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);
        api.getById(miId).enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();

                    if(etNombre != null) etNombre.setText(usuarioActual.getNombre());
                    if(etApellido != null) etApellido.setText(usuarioActual.getApellido());
                    if(etCorreo != null) etCorreo.setText(usuarioActual.getCorreo());

                    // Actualizar Header
                    if(tvNombreCompleto != null)
                        tvNombreCompleto.setText(usuarioActual.getNombre() + " " + usuarioActual.getApellido());
                    if(tvCorreoPerfil != null)
                        tvCorreoPerfil.setText(usuarioActual.getCorreo());
                }
            }
            @Override public void onFailure(Call<UsuarioDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error cargando perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEstadisticas() {
        CursoApi cursoApi = RetrofitClient.getClient().create(CursoApi.class);
        cursoApi.lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = 0;
                    for (CursoDto c : response.body()) {
                        if (c.getUsuarioId() == miId) count++;
                    }
                    if(tvCountCursos != null) tvCountCursos.setText(String.valueOf(count));
                }
            }
            @Override public void onFailure(Call<List<CursoDto>> call, Throwable t) {}
        });

        ServicioApi servicioApi = RetrofitClient.getClient().create(ServicioApi.class);
        servicioApi.lista().enqueue(new Callback<List<ServicioDto>>() {
            @Override
            public void onResponse(Call<List<ServicioDto>> call, Response<List<ServicioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = 0;
                    for (ServicioDto s : response.body()) {
                        if (s.getUsuarioId() == miId) count++;
                    }
                    if(tvCountClases != null) tvCountClases.setText(String.valueOf(count));
                }
            }
            @Override public void onFailure(Call<List<ServicioDto>> call, Throwable t) {}
        });
    }

    private void actualizarDatos() {
        if (usuarioActual == null) return;

        usuarioActual.setNombre(etNombre.getText().toString());
        usuarioActual.setApellido(etApellido.getText().toString());
        usuarioActual.setCorreo(etCorreo.getText().toString());

        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);
        // Nota: Asegúrate que tu API soporte update(id, body)
        api.update(miId, usuarioActual).enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    // Refrescar textos estáticos
                    tvNombreCompleto.setText(usuarioActual.getNombre() + " " + usuarioActual.getApellido());
                    tvCorreoPerfil.setText(usuarioActual.getCorreo());
                } else {
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<UsuarioDto> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cerrarSesion() {
        sessionManager.logoutUser();
        Intent intent = new Intent(getActivity(), Cuenta.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}