package com.example.saber_share.fragmentos.contenido;

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

    public Mensajes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_mensajes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        miId = session.getUserId();

        if (getArguments() != null) {
            otroId = getArguments().getInt("otroId", -1);
            otroNombre = getArguments().getString("otroNombre", "Usuario");
        } else {
            otroId = 1;
            otroNombre = "Chat";
        }

        rvMensajes = view.findViewById(R.id.rvMensajes);
        etInput = view.findViewById(R.id.etMensajeInput);
        btnEnviar = view.findViewById(R.id.btnEnviarMensaje);
        tvTitulo = view.findViewById(R.id.tvTituloChat);

        tvTitulo.setText(otroNombre);

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        rvMensajes.setLayoutManager(lm);

        adapter = new MensajeAdapter(listaMensajes, miId);
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
                    listaMensajes.clear();
                    listaMensajes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                }
            }
            @Override public void onFailure(Call<List<MensajeDto>> call, Throwable t) {}
        });
    }

    private void enviarMensaje() {
        String texto = etInput.getText().toString().trim();
        if (texto.isEmpty()) return;

        UsuarioDto remitente = new UsuarioDto();
        remitente.setIdUsuario(miId); // <--- CORREGIDO: ahora coincide con UsuarioDto

        UsuarioDto destinatario = new UsuarioDto();
        destinatario.setIdUsuario(otroId); // <--- CORREGIDO: ahora coincide con UsuarioDto

        MensajeDto nuevoMsg = new MensajeDto(texto, remitente, destinatario);

        MensajeApi api = RetrofitClient.getClient().create(MensajeApi.class);
        api.enviar(nuevoMsg).enqueue(new Callback<MensajeDto>() {
            @Override
            public void onResponse(Call<MensajeDto> call, Response<MensajeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
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
}