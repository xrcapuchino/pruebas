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
import com.example.saber_share.model.AgendaDto;
import com.example.saber_share.util.api.AgendaApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.local.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisClases extends Fragment {

    private RecyclerView rv;
    private TextView tvVacio;
    private SessionManager session;

    public MisClases() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_mis_clases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new SessionManager(requireContext());

        rv = view.findViewById(R.id.rvMisClases);
        tvVacio = view.findViewById(R.id.tvVacioAgenda);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Botón atrás (si lo agregas al layout) o usar el del sistema
        // view.findViewById(R.id.btnAtras).setOnClickListener(...)

        cargarAgenda();
    }

    private void cargarAgenda() {
        AgendaApi api = RetrofitClient.getClient().create(AgendaApi.class);
        api.getMisAgendas(session.getUserId()).enqueue(new Callback<List<AgendaDto>>() {
            @Override
            public void onResponse(Call<List<AgendaDto>> call, Response<List<AgendaDto>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<AgendaDto> lista = response.body();

                    if(lista.isEmpty()){
                        tvVacio.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    } else {
                        tvVacio.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);

                        // Usamos un adaptador simple para visualizar
                        rv.setAdapter(new MisClasesAdapter(lista));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<AgendaDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adaptador Interno Simple para visualizar
    private class MisClasesAdapter extends RecyclerView.Adapter<MisClasesAdapter.Holder> {
        List<AgendaDto> datos;
        public MisClasesAdapter(List<AgendaDto> datos){ this.datos = datos; }

        @NonNull @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AgendaDto a = datos.get(position);
            holder.tvFecha.setText(a.getFecha() + " - " + a.getHora());

            // Mostramos si soy el profe o el alumno
            if(a.getProfesorId() == session.getUserId()){
                holder.tvDetalle.setText("Clase que impartes: " + a.getTituloServicio());
                holder.tvDetalle.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                holder.tvDetalle.setText("Clase que tomas: " + a.getTituloServicio());
                holder.tvDetalle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            holder.btn.setVisibility(View.GONE); // Ocultamos botón de reservar/eliminar
        }

        @Override public int getItemCount() { return datos.size(); }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvFecha, tvDetalle;
            View btn;
            public Holder(@NonNull View itemView) {
                super(itemView);
                tvFecha = itemView.findViewById(R.id.tvFechaSlot);
                tvDetalle = itemView.findViewById(R.id.tvHoraSlot); // Reusamos el textview de hora para titulo
                btn = itemView.findViewById(R.id.btnReservarSlot);
            }
        }
    }
}