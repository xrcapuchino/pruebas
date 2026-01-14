package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.MensajeAdapter;
import com.example.saber_share.model.MensajeDto;
import com.example.saber_share.model.UsuarioDto;
import com.example.saber_share.util.api.MensajeApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.local.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Mensajes extends Fragment {

    private RecyclerView rvMensajes;
    private EditText etInput;
    private Button btnEnviar;
    private TextView tvTitulo;

    private MensajeAdapter adapter;
    private List<MensajeDto> listaMensajes = new ArrayList<>();
    private SessionManager session;

    private int miId;
    private int otroId;
    private String otroNombre;

    // Handler para actualizar el chat automáticamente cada 3 segundos
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            cargarConversacion();
            handler.postDelayed(this, 3000);
        }
    };

    public Mensajes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Asegúrate que este XML es el que tiene la lista y el input
        return inflater.inflate(R.layout.fragment_main_mensajes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        miId = session.getUserId();

        // Recuperar datos del otro usuario (profesor o alumno)
        if (getArguments() != null) {
            otroId = getArguments().getInt("otroId", -1);
            otroNombre = getArguments().getString("otroNombre", "Usuario");
        } else {
            otroId = -1;
            otroNombre = "Chat";
        }

        // Vincular vistas con el XML fragment_main_mensajes.xml
        rvMensajes = view.findViewById(R.id.rvMensajes);
        etInput = view.findViewById(R.id.etMensajeInput);
        btnEnviar = view.findViewById(R.id.btnEnviarMensaje);
        tvTitulo = view.findViewById(R.id.tvTituloChat);

        tvTitulo.setText(otroNombre);

        // Configurar RecyclerView
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true); // Empieza desde abajo
        rvMensajes.setLayoutManager(lm);

        // Inicializar Adapter
        adapter = new MensajeAdapter(getContext(), listaMensajes, miId);
        rvMensajes.setAdapter(adapter);

        cargarConversacion();

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void cargarConversacion() {
        if(otroId == -1) return;

        MensajeApi api = RetrofitClient.getClient().create(MensajeApi.class);
        api.verConversacion(miId, otroId).enqueue(new Callback<List<MensajeDto>>() {
            @Override
            public void onResponse(Call<List<MensajeDto>> call, Response<List<MensajeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Solo actualizamos si hay mensajes nuevos para no parpadear
                    if (response.body().size() != listaMensajes.size()) {
                        listaMensajes.clear();
                        listaMensajes.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                    }
                }
            }
            @Override public void onFailure(Call<List<MensajeDto>> call, Throwable t) {}
        });
    }

    private void enviarMensaje() {
        String texto = etInput.getText().toString().trim();
        if (texto.isEmpty()) return;

        // Crear el mensaje usando los IDs directamente
        MensajeDto nuevoMsg = new MensajeDto(texto, miId, otroId);

        MensajeApi api = RetrofitClient.getClient().create(MensajeApi.class);
        api.enviar(nuevoMsg).enqueue(new Callback<MensajeDto>() {
            @Override
            public void onResponse(Call<MensajeDto> call, Response<MensajeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Agregamos el mensaje a la vista inmediatamente
                    adapter.agregarMensaje(response.body());
                    rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
                    etInput.setText("");
                } else {
                    Toast.makeText(getContext(), "Error al enviar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<MensajeDto> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable); // Iniciar actualización automática
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Detener actualización
    }
}