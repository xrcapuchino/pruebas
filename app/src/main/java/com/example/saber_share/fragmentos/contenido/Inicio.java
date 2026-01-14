package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.saber_share.model.ServicioDto;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.HistorialApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.ServicioApi;
import com.example.saber_share.util.local.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends Fragment {

    private RecyclerView rvProximasClases, rvContinuarCursos, rvCursosImpartidos;
    private PublicacionAdapter adapterClases, adapterMisCursos, adapterTodosCursos;
    private SessionManager sessionManager;

    private List<Publicacion> listaClases = new ArrayList<>();
    private List<Publicacion> listaMisCursos = new ArrayList<>();
    private List<Publicacion> listaTodosCursos = new ArrayList<>();

    // Para guardar temporalmente qué tenemos comprado
    private List<Integer> misIdsCursos = new ArrayList<>();

    public Inicio() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        // 1. Vincular Vistas (IDs reales de tu XML)
        rvProximasClases = view.findViewById(R.id.rvProximasClases);
        rvContinuarCursos = view.findViewById(R.id.rvContinuarCursos);
        rvCursosImpartidos = view.findViewById(R.id.rvCursosImpartidos);

        // 2. Configurar LayoutManagers (Horizontales y Vertical)
        rvProximasClases.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvContinuarCursos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCursosImpartidos.setLayoutManager(new LinearLayoutManager(getContext())); // Vertical por defecto

        // 3. Inicializar Adaptadores
        adapterClases = new PublicacionAdapter(listaClases, this::irADetalle);
        adapterMisCursos = new PublicacionAdapter(listaMisCursos, this::irADetalle);
        adapterTodosCursos = new PublicacionAdapter(listaTodosCursos, this::irADetalle);

        rvProximasClases.setAdapter(adapterClases);
        rvContinuarCursos.setAdapter(adapterMisCursos);
        rvCursosImpartidos.setAdapter(adapterTodosCursos);

        // 4. Cargar Datos
        cargarHistorialYFiltrar(); // Primero historial para saber qué es "mío"
        cargarClases(); // Llenar rvProximasClases
        cargarCursosGenerales(); // Llenar rvCursosImpartidos
    }

    private void cargarHistorialYFiltrar() {
        int userId = sessionManager.getUserId();
        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);
        api.historialPorUsuario(userId).enqueue(new Callback<List<HistorialDto>>() {
            @Override
            public void onResponse(Call<List<HistorialDto>> call, Response<List<HistorialDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    misIdsCursos.clear();
                    List<Integer> idsServicios = new ArrayList<>();

                    for (HistorialDto h : response.body()) {
                        if (h.getCursoId() != null) misIdsCursos.add(h.getCursoId());
                        if (h.getServicioId() != null) idsServicios.add(h.getServicioId());
                    }

                    // Actualizamos adaptadores para que pinten verde lo comprado
                    adapterClases.setIdsComprados(idsServicios);
                    adapterTodosCursos.setIdsComprados(misIdsCursos);

                    // Ahora cargamos la lista de "Continuar Cursos" (solo los míos)
                    cargarMisCursosDesdeAPI();
                }
            }
            @Override public void onFailure(Call<List<HistorialDto>> call, Throwable t) {}
        });
    }

    private void cargarMisCursosDesdeAPI() {
        // Obtenemos TODOS los cursos y filtramos localmente los que están en 'misIdsCursos'
        CursoApi api = RetrofitClient.getClient().create(CursoApi.class);
        api.lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMisCursos.clear();
                    for (CursoDto c : response.body()) {
                        if (misIdsCursos.contains(c.getIdCurso())) {
                            listaMisCursos.add(convertirCurso(c));
                        }
                    }
                    adapterMisCursos.setIdsComprados(misIdsCursos); // Para que salgan verdes
                    adapterMisCursos.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<List<CursoDto>> call, Throwable t) {}
        });
    }

    private void cargarCursosGenerales() {
        CursoApi api = RetrofitClient.getClient().create(CursoApi.class);
        api.lista().enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTodosCursos.clear();
                    for (CursoDto c : response.body()) {
                        listaTodosCursos.add(convertirCurso(c));
                    }
                    adapterTodosCursos.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<List<CursoDto>> call, Throwable t) {}
        });
    }

    private void cargarClases() {
        ServicioApi api = RetrofitClient.getClient().create(ServicioApi.class);
        api.lista().enqueue(new Callback<List<ServicioDto>>() {
            @Override
            public void onResponse(Call<List<ServicioDto>> call, Response<List<ServicioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaClases.clear();
                    for (ServicioDto s : response.body()) {
                        Publicacion p = new Publicacion();
                        p.setIdOriginal(s.getServicioId());
                        p.setTipo(Publicacion.TIPO_CLASE);
                        p.setTitulo(s.getTitulo());
                        p.setDescripcion(s.getDescripcion());
                        p.setPrecio(s.getPrecio());
                        p.setIdAutor(s.getUsuarioId());
                        p.setAutor("Profesor ID: " + s.getUsuarioId());
                        p.setCalificacion("4.8");
                        p.setExtraInfo(s.getRequisitos());
                        listaClases.add(p);
                    }
                    adapterClases.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<List<ServicioDto>> call, Throwable t) {}
        });
    }

    private Publicacion convertirCurso(CursoDto c) {
        Publicacion p = new Publicacion();
        p.setIdOriginal(c.getIdCurso());
        p.setTipo(Publicacion.TIPO_CURSO);
        p.setTitulo(c.getTitulo());
        p.setDescripcion(c.getDescripcion());
        p.setPrecio(c.getPrecio());
        p.setIdAutor(c.getUsuarioId());
        p.setAutor("Profesor ID: " + c.getUsuarioId());
        p.setCalificacion("4.5");
        p.setExtraInfo(c.getFoto());
        return p;
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

        Navigation.findNavController(requireView()).navigate(R.id.action_inicio_to_detallePublicacion, bundle);
    }
}