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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.BandejaAdapter;
import com.example.saber_share.model.MensajeDto;
import com.example.saber_share.util.api.MensajeApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.local.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BandejaMensajes extends Fragment {

    private RecyclerView rvBandeja;
    private TextView tvVacio;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Reutilizamos el layout de agenda que ya tiene la estructura necesaria
        return inflater.inflate(R.layout.fragment_main_agendar_clase, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());

        // Ajuste visual: Cambiamos el título del layout reutilizado
        TextView tvTitulo = view.findViewById(R.id.tvTituloAgendar);
        if(tvTitulo != null) tvTitulo.setText("Mis Mensajes");

        // Ocultamos el subtítulo
        TextView tvSub = view.findViewById(R.id.tvSubtituloServicio);
        if(tvSub != null) tvSub.setVisibility(View.GONE);

        rvBandeja = view.findViewById(R.id.rvHorarios); // ID reutilizado
        tvVacio = view.findViewById(R.id.tvSinHorarios); // ID reutilizado

        if(tvVacio != null) tvVacio.setText("No tienes conversaciones recientes.");

        rvBandeja.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarBandeja();
    }

    private void cargarBandeja() {
        int miId = session.getUserId();
        MensajeApi api = RetrofitClient.getClient().create(MensajeApi.class);
        api.verBandeja(miId).enqueue(new Callback<List<MensajeDto>>() {
            @Override
            public void onResponse(Call<List<MensajeDto>> call, Response<List<MensajeDto>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<MensajeDto> raw = response.body();
                    List<MensajeDto> filtrados = filtrarUnicos(raw, miId);

                    if(filtrados.isEmpty()) {
                        if(tvVacio != null) tvVacio.setVisibility(View.VISIBLE);
                        rvBandeja.setVisibility(View.GONE);
                    } else {
                        if(tvVacio != null) tvVacio.setVisibility(View.GONE);
                        rvBandeja.setVisibility(View.VISIBLE);

                        rvBandeja.setAdapter(new BandejaAdapter(filtrados, miId, (otroId, nombre) -> {
                            // AQUÍ ESTÁ LA MAGIA: Navegamos al Chat Directo
                            Bundle b = new Bundle();
                            b.putInt("otroId", otroId);
                            b.putString("otroNombre", nombre);
                            Navigation.findNavController(requireView()).navigate(R.id.chatDirecto, b);
                        }));
                    }
                }
            }
            @Override public void onFailure(Call<List<MensajeDto>> call, Throwable t) {
                if(getContext()!=null) Toast.makeText(getContext(), "Error cargando mensajes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<MensajeDto> filtrarUnicos(List<MensajeDto> todos, int miId) {
        List<MensajeDto> unicos = new ArrayList<>();
        Set<Integer> usuariosYaListados = new HashSet<>();

        for(MensajeDto m : todos) {
            int otroUsuario = (m.getRemitente().getIdUsuario() == miId)
                    ? m.getDestinatario().getIdUsuario()
                    : m.getRemitente().getIdUsuario();

            if(!usuariosYaListados.contains(otroUsuario)) {
                usuariosYaListados.add(otroUsuario);
                unicos.add(m);
            }
        }
        return unicos;
    }
}