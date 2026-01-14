package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.PublicacionAdapter;
import com.example.saber_share.model.CursoDto;
import com.example.saber_share.model.Publicacion;
import com.example.saber_share.model.ServicioDto;
import com.example.saber_share.model.UsuarioDto;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.ServicioApi;
import com.example.saber_share.util.api.UsuarioApi;
import com.example.saber_share.util.local.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comprar extends Fragment {

    private RecyclerView rvResultados;
    private EditText etBuscar;
    private TextView tvSaludo;
    private ChipGroup cgCategorias;

    private PublicacionAdapter adapter;
    private List<Publicacion> listaGlobal = new ArrayList<>();
    private SessionManager sessionManager;

    // Estado actual de filtros
    private String textoBusqueda = "";
    private String categoriaFiltro = "Top"; // Por defecto "Top" o "Todos"

    public Comprar() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_comprar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        int miId = sessionManager.getUserId();

        rvResultados = view.findViewById(R.id.rvResultadosBusqueda);
        etBuscar = view.findViewById(R.id.etBuscarComprar);
        tvSaludo = view.findViewById(R.id.tvSaludo);
        cgCategorias = view.findViewById(R.id.cgCategorias);

        rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicacionAdapter(getContext(), new ArrayList<>(), miId, this::irADetalle);
        rvResultados.setAdapter(adapter);

        cargarDatos();
        cargarNombreUsuario(miId);

        // 1. Listener de Buscador (Texto)
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusqueda = s.toString();
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 2. Listener de Chips (Categorías)
        cgCategorias.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    categoriaFiltro = chip.getText().toString();
                    aplicarFiltros();
                }
            }
        });
    }

    private void aplicarFiltros() {
        List<Publicacion> listaFiltrada = new ArrayList<>();

        for (Publicacion p : listaGlobal) {
            boolean coincideTexto = true;
            boolean coincideCategoria = true;

            // Filtro Texto
            if (!textoBusqueda.isEmpty()) {
                if (!p.getTitulo().toLowerCase().contains(textoBusqueda.toLowerCase()) &&
                        !p.getDescripcion().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                    coincideTexto = false;
                }
            }

            // Filtro Categoría (Simulado)
            if (!categoriaFiltro.equals("Top") && !categoriaFiltro.equals("Todos")) {
                // Buscamos si la categoría aparece en título o descripción
                // Ejemplo: Si filtro es "Programación", buscamos esa palabra
                String todoTexto = (p.getTitulo() + " " + p.getDescripcion()).toLowerCase();

                // Mapeo simple de categorías a palabras clave
                String keyword = categoriaFiltro.toLowerCase();
                if(keyword.equals("programación")) keyword = "java"; // Ajuste para demo

                if (!todoTexto.contains(keyword)) {
                    coincideCategoria = false;
                }
            }

            if (coincideTexto && coincideCategoria) {
                listaFiltrada.add(p);
            }
        }
        adapter.setDatos(listaFiltrada);
    }

    private void cargarNombreUsuario(int id) {
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

    private void irADetalle(Publicacion p) {
        Bundle bundle = new Bundle();
        bundle.putInt("idOriginal", p.getIdOriginal());
        bundle.putInt("idAutor", p.getIdAutor());
        bundle.putString("tipo", p.getTipo());
        bundle.putString("titulo", p.getTitulo());
        bundle.putString("descripcion", p.getDescripcion());
        bundle.putDouble("precio", p.getPrecio());
        bundle.putString("autor", p.getAutor());
        bundle.putString("calificacion", p.getCalificacion());
        bundle.putString("extra", p.getExtraInfo());
        Navigation.findNavController(requireView()).navigate(R.id.detallePublicacion, bundle);
    }

    private void cargarDatos() {
        listaGlobal.clear();
        RetrofitClient.getClient().create(CursoApi.class).lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CursoDto c : response.body()) {
                        listaGlobal.add(new Publicacion(
                                Publicacion.TIPO_CURSO, c.getIdCurso(), c.getTitulo(), c.getDescripcion(),
                                c.getPrecio(), c.getNombreUsuario(), c.getCalificacion(), null, c.getFoto(), c.getUsuarioId()
                        ));
                    }
                    aplicarFiltros(); // Actualizar UI
                }
                cargarServicios();
            }
            @Override public void onFailure(Call<List<CursoDto>> call, Throwable t) { cargarServicios(); }
        });
    }

    private void cargarServicios() {
        RetrofitClient.getClient().create(ServicioApi.class).lista().enqueue(new Callback<List<ServicioDto>>() {
            @Override
            public void onResponse(Call<List<ServicioDto>> call, Response<List<ServicioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ServicioDto s : response.body()) {
                        listaGlobal.add(new Publicacion(
                                Publicacion.TIPO_CLASE, s.getServicioId(), s.getTitulo(), s.getDescripcion(),
                                s.getPrecio(), s.getNombreUsuario(), "N/A", null, s.getRequisitos(), s.getUsuarioId()
                        ));
                    }
                    aplicarFiltros(); // Actualizar UI final
                }
            }
            @Override public void onFailure(Call<List<ServicioDto>> call, Throwable t) {}
        });
    }
}