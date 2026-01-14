package com.example.saber_share.fragmentos.contenido;

import android.graphics.Color;
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

import com.example.saber_share.R;
import com.example.saber_share.model.CursoDto;
import com.example.saber_share.model.MetodoDePagoDto;
import com.example.saber_share.model.ServicioDto;
import com.example.saber_share.model.UsuarioDto;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.MetodoPagoApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.ServicioApi;
import com.example.saber_share.util.api.UsuarioApi;
import com.example.saber_share.util.local.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends Fragment {

    private SessionManager sessionManager;
    private TextView tvNombre, tvCorreo, tvCountCursos, tvCountClases, tvMetodoPagoStatus;
    private EditText etNombrePub, etApellidoPub, etCorreoPub;
    private Button btnGuardar, btnGestionarTarjetas, btnHistorial, btnEstadisticas, btnCerrarSesion, btnVerAgenda;

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

        // 1. Vincular Vistas
        tvNombre = view.findViewById(R.id.tvNombrePerfil);
        tvCorreo = view.findViewById(R.id.tvCorreoPerfil);
        tvCountCursos = view.findViewById(R.id.tvCountCursos);
        tvCountClases = view.findViewById(R.id.tvCountClases);
        tvMetodoPagoStatus = view.findViewById(R.id.tvMetodoPagoStatus);

        etNombrePub = view.findViewById(R.id.etNombrePublico);
        // CORREGIDO: El ID debe coincidir con el XML nuevo
        etApellidoPub = view.findViewById(R.id.etApellidoPublico);
        etCorreoPub = view.findViewById(R.id.etCorreoPublico);

        btnGuardar = view.findViewById(R.id.btnGuardarPerfil);
        btnGestionarTarjetas = view.findViewById(R.id.btnGestionarTarjetas);
        btnHistorial = view.findViewById(R.id.btnHistorial);
        btnEstadisticas = view.findViewById(R.id.btnEstadisticas);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnVerAgenda = view.findViewById(R.id.btnVerAgenda);

        int userId = sessionManager.getUserId();

        // 2. Listeners
        btnGuardar.setOnClickListener(v -> guardarCambios(userId));
        btnCerrarSesion.setOnClickListener(v -> sessionManager.logoutUser());

        btnGestionarTarjetas.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_perfil_to_administrarTarjetas)
        );

        btnHistorial.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_perfil_to_historial)
        );

        btnVerAgenda.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_perfil_to_misClases)
        );

        btnEstadisticas.setOnClickListener(v ->
                Toast.makeText(getContext(), "Próximamente", Toast.LENGTH_SHORT).show()
        );

        // 3. Cargar Datos
        cargarDatosUsuario(userId);
        cargarConteoCursos(userId);
        cargarConteoServicios(userId);
        verificarTarjetas(userId);
    }

    private void cargarDatosUsuario(int userId) {
        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);

        // NOTA: Si tu UsuarioApi no tiene getById, avísame. Debería tenerlo.
        api.getById(userId).enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();

                    // USAMOS LOS NOMBRES QUE ESTÁN EN TU DTO (nomUsu, apeUsu)
                    // Si te marca rojo, cambia a getNombre() y getApellido()
                    String nombre = usuarioActual.getNombre();
                    String apellido = usuarioActual.getApellido();
                    String correo = usuarioActual.getCorreo();

                    if(nombre == null) nombre = "";
                    if(apellido == null) apellido = "";

                    tvNombre.setText(nombre + " " + apellido);
                    tvCorreo.setText(correo);

                    etNombrePub.setText(nombre);
                    etApellidoPub.setText(apellido);
                    etCorreoPub.setText(correo);
                }
            }
            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo red perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambios(int userId) {
        if(usuarioActual == null) return;

        String nuevoNombre = etNombrePub.getText().toString().trim();
        String nuevoApellido = etApellidoPub.getText().toString().trim();
        String nuevoCorreo = etCorreoPub.getText().toString().trim();

        if(nuevoNombre.isEmpty() || nuevoCorreo.isEmpty()) {
            Toast.makeText(getContext(), "Faltan datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizamos DTO (Adaptado a tus variables)
        usuarioActual.setNombre(nuevoNombre);
        usuarioActual.setApellido(nuevoApellido);
        usuarioActual.setCorreo(nuevoCorreo);

        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);
        api.update(userId, usuarioActual).enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Guardado", Toast.LENGTH_SHORT).show();
                    tvNombre.setText(nuevoNombre + " " + nuevoApellido);
                    tvCorreo.setText(nuevoCorreo);
                } else {
                    Toast.makeText(getContext(), "Error guardando", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<UsuarioDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarConteoCursos(int userId) {
        CursoApi api = RetrofitClient.getClient().create(CursoApi.class);
        // USAMOS lista() COMO PEDISTE
        api.lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long count = 0;
                    for (CursoDto c : response.body()) {
                        if (c.getUsuarioId() != null && c.getUsuarioId() == userId) count++;
                    }
                    tvCountCursos.setText(String.valueOf(count));
                }
            }
            @Override public void onFailure(Call<List<CursoDto>> call, Throwable t) { tvCountCursos.setText("-"); }
        });
    }

    private void cargarConteoServicios(int userId) {
        ServicioApi api = RetrofitClient.getClient().create(ServicioApi.class);
        // USAMOS lista() COMO PEDISTE
        api.lista().enqueue(new Callback<List<ServicioDto>>() {
            @Override
            public void onResponse(Call<List<ServicioDto>> call, Response<List<ServicioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long count = 0;
                    for (ServicioDto s : response.body()) {
                        if (s.getUsuarioId() != null && s.getUsuarioId() == userId) count++;
                    }
                    tvCountClases.setText(String.valueOf(count));
                }
            }
            @Override public void onFailure(Call<List<ServicioDto>> call, Throwable t) { tvCountClases.setText("-"); }
        });
    }

    private void verificarTarjetas(int userId) {
        MetodoPagoApi api = RetrofitClient.getClient().create(MetodoPagoApi.class);
        api.listarTarjetas().enqueue(new Callback<List<MetodoDePagoDto>>() {
            @Override
            public void onResponse(Call<List<MetodoDePagoDto>> call, Response<List<MetodoDePagoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean tiene = false;
                    String digitos = "xxxx";
                    for (MetodoDePagoDto m : response.body()) {
                        if (m.getUsuarioId() == userId) {
                            tiene = true;
                            if(m.getNumeroTarjeta() != null && m.getNumeroTarjeta().length() > 4)
                                digitos = m.getNumeroTarjeta().substring(m.getNumeroTarjeta().length()-4);
                            break;
                        }
                    }
                    if (tiene) {
                        tvMetodoPagoStatus.setText("Terminada en " + digitos);
                        tvMetodoPagoStatus.setTextColor(Color.parseColor("#4CAF50"));
                    } else {
                        tvMetodoPagoStatus.setText("(Sin tarjeta)");
                        tvMetodoPagoStatus.setTextColor(Color.parseColor("#7A7A7A"));
                    }
                }
            }
            @Override public void onFailure(Call<List<MetodoDePagoDto>> call, Throwable t) {}
        });
    }
}