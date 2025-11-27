package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.fragmentos.contenido.adapter.PublicacionAdapter;
import com.example.saber_share.model.CursoDto;
import com.example.saber_share.model.Publicacion;
import com.example.saber_share.model.ServicioDto;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.ServicioApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comprar extends Fragment {

    private RecyclerView rvResultados;
    private EditText etBuscar;
    private PublicacionAdapter adapter;
    private List<Publicacion> listaGlobal = new ArrayList<>();

    public Comprar() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_comprar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvResultados = view.findViewById(R.id.rvResultadosBusqueda);
        etBuscar = view.findViewById(R.id.etBuscarComprar); // Asegúrate que el ID en XML sea este

        // Configurar RecyclerView
        rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicacionAdapter(getContext(), new ArrayList<>());
        rvResultados.setAdapter(adapter);

        // Cargar datos desde API
        cargarDatos();

        // Configurar Buscador
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarDatos() {
        listaGlobal.clear();

        CursoApi cursoApi = RetrofitClient.getClient().create(CursoApi.class);
        cursoApi.lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CursoDto c : response.body()) {
                        listaGlobal.add(new Publicacion(
                                Publicacion.TIPO_CURSO,
                                c.getIdCurso(),
                                c.getTitulo(),
                                c.getDescripcion(),
                                c.getPrecio(),
                                c.getNombreUsuario(),
                                c.getCalificacion(),
                                null
                        ));
                    }
                    // Actualizamos la lista (tendrá solo cursos por ahora)
                    adapter.setDatos(listaGlobal);
                }
                // Ahora cargamos servicios para agregarlos
                cargarServicios();
            }

            @Override
            public void onFailure(Call<List<CursoDto>> call, Throwable t) {
                // Si falla cursos, intentamos servicios igual
                cargarServicios();
            }
        });
    }

    private void cargarServicios() {
        ServicioApi servicioApi = RetrofitClient.getClient().create(ServicioApi.class);
        servicioApi.lista().enqueue(new Callback<List<ServicioDto>>() {
            @Override
            public void onResponse(Call<List<ServicioDto>> call, Response<List<ServicioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ServicioDto s : response.body()) {
                        listaGlobal.add(new Publicacion(
                                Publicacion.TIPO_CLASE,
                                s.getServicioId(),
                                s.getTitulo(),
                                s.getDescripcion(),
                                s.getPrecio(),
                                s.getNombreUsuario(),
                                "N/A",
                                null
                        ));
                    }
                    // Actualizamos la lista final (Cursos + Servicios)
                    adapter.setDatos(listaGlobal);
                }
            }

            @Override
            public void onFailure(Call<List<ServicioDto>> call, Throwable t) {
                // Solo mostramos error si no hay nada en la lista
                if(listaGlobal.isEmpty()) {
                    Toast.makeText(getContext(), "No se pudieron cargar las publicaciones", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}