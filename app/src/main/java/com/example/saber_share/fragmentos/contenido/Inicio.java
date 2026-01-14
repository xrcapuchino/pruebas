package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.PublicacionAdapter;
import com.example.saber_share.model.CursoDto;
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.model.Publicacion;
import com.example.saber_share.model.UsuarioDto;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.HistorialApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.UsuarioApi;
import com.example.saber_share.util.local.SessionManager;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends Fragment {

    private TextView tvSaludo;
    private RecyclerView rvProximasClases, rvContinuarCursos, rvCursosImpartidos;
    private LinearLayout layoutProximas, layoutContinuar;
    private ChipGroup cgFiltros;
    private SessionManager sessionManager;

    private PublicacionAdapter adapterImpartidos;
    private PublicacionAdapter adapterContinuar;
    private PublicacionAdapter adapterProximas;

    public Inicio() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        int miId = sessionManager.getUserId();

        tvSaludo = view.findViewById(R.id.tvSaludo);
        layoutProximas = view.findViewById(R.id.layoutProximasClases);
        layoutContinuar = view.findViewById(R.id.layoutContinuarCursos);
        cgFiltros = view.findViewById(R.id.cgFiltrosInicio);

        rvProximasClases = view.findViewById(R.id.rvProximasClases);
        rvContinuarCursos = view.findViewById(R.id.rvContinuarCursos);
        rvCursosImpartidos = view.findViewById(R.id.rvCursosImpartidos);

        rvProximasClases.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvContinuarCursos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCursosImpartidos.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterImpartidos = new PublicacionAdapter(getContext(), new ArrayList<>(), miId, this::irADetalle);
        adapterContinuar = new PublicacionAdapter(getContext(), new ArrayList<>(), miId, this::irADetalle);
        adapterProximas = new PublicacionAdapter(getContext(), new ArrayList<>(), miId, this::irADetalle);

        rvCursosImpartidos.setAdapter(adapterImpartidos);
        rvContinuarCursos.setAdapter(adapterContinuar);
        rvProximasClases.setAdapter(adapterProximas);

        cargarSaludo(miId);
        cargarMisPublicaciones(miId);
        cargarMisCompras(miId);

        view.findViewById(R.id.tvBuscarInicio).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.comprar)
        );

        if(cgFiltros != null) {
            cgFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) return;
                int id = checkedIds.get(0);

                if (id == R.id.chipTodos) {
                    if(layoutProximas != null) layoutProximas.setVisibility(View.VISIBLE);
                    if(layoutContinuar != null) layoutContinuar.setVisibility(View.VISIBLE);
                } else if (id == R.id.chipCursos) {
                    if(layoutProximas != null) layoutProximas.setVisibility(View.GONE);
                    if(layoutContinuar != null) layoutContinuar.setVisibility(View.VISIBLE);
                } else if (id == R.id.chipClases) {
                    if(layoutProximas != null) layoutProximas.setVisibility(View.VISIBLE);
                    if(layoutContinuar != null) layoutContinuar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void cargarSaludo(int id) {
        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);
        api.getById(id).enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if(response.isSuccessful() && response.body() != null){
                    tvSaludo.setText("HOLA DE NUEVO " + response.body().getNombre().toUpperCase());
                }
            }
            @Override public void onFailure(Call<UsuarioDto> call, Throwable t) {}
        });
    }

    private void cargarMisCompras(int miId) {
        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);
        api.historialPorUsuario(miId).enqueue(new Callback<List<HistorialDto>>() {
            @Override
            public void onResponse(Call<List<HistorialDto>> call, Response<List<HistorialDto>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Publicacion> listaCursos = new ArrayList<>();
                    List<Publicacion> listaClases = new ArrayList<>();

                    for(HistorialDto h : response.body()) {
                        // CORRECCIÓN: Usamos getCursoId() y getTituloCurso()
                        if(h.getCursoId() != null) {
                            listaCursos.add(new Publicacion(
                                    Publicacion.TIPO_CURSO,
                                    h.getCursoId(),
                                    h.getTituloCurso() != null ? h.getTituloCurso() : "Curso Comprado",
                                    "Comprado el: " + h.getFechapago(),
                                    0.0,
                                    "SaberShare",
                                    "5.0",
                                    null,
                                    null,
                                    0
                            ));
                        } else if(h.getServicioId() != null) {
                            listaClases.add(new Publicacion(
                                    Publicacion.TIPO_CLASE,
                                    h.getServicioId(),
                                    h.getTituloServicio() != null ? h.getTituloServicio() : "Clase Agendada",
                                    "Fecha: " + h.getFechapago(),
                                    0.0,
                                    "Profesor",
                                    "5.0",
                                    null,
                                    null,
                                    0
                            ));
                        }
                    }
                    adapterContinuar.setDatos(listaCursos);
                    adapterProximas.setDatos(listaClases);
                }
            }
            @Override public void onFailure(Call<List<HistorialDto>> call, Throwable t) {}
        });
    }

    private void cargarMisPublicaciones(int miId) {
        CursoApi cursoApi = RetrofitClient.getClient().create(CursoApi.class);
        cursoApi.lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Publicacion> misPubs = new ArrayList<>();
                    for(CursoDto c : response.body()){
                        if(c.getUsuarioId() == miId) {
                            misPubs.add(new Publicacion(Publicacion.TIPO_CURSO, c.getIdCurso(), c.getTitulo(), c.getDescripcion(), c.getPrecio(), "Tú", c.getCalificacion(), null, c.getFoto(), c.getUsuarioId()));
                        }
                    }
                    adapterImpartidos.setDatos(misPubs);
                }
            }
            @Override public void onFailure(Call<List<CursoDto>> call, Throwable t) {}
        });
    }

    private void irADetalle(Publicacion p) {
        Bundle bundle = new Bundle();
        bundle.putInt("idOriginal", p.getIdOriginal());
        bundle.putInt("idAutor", p.getIdAutor());
        bundle.putString("tipo", p.getTipo());
        bundle.putString("titulo", p.getTitulo());
        bundle.putString("descripcion", p.getDescripcion());
        bundle.putDouble("precio", p.getPrecio());
        bundle.putString("autor", p.getAutor());

        try {
            Navigation.findNavController(requireView()).navigate(R.id.detallePublicacion, bundle);
        } catch (Exception e) {}
    }
}