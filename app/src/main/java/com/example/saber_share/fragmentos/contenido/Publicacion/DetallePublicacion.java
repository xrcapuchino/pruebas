package com.example.saber_share.fragmentos.contenido.Publicacion;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.saber_share.R;
import com.example.saber_share.model.HistorialDto;
import com.example.saber_share.model.Publicacion;
import com.example.saber_share.util.api.CursoApi;
import com.example.saber_share.util.api.HistorialApi;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.ServicioApi;
import com.example.saber_share.util.local.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallePublicacion extends Fragment {

    private int idAutor;
    private int idOriginal;
    private String tipo, titulo, descripcion, autor, extra, calificacion;
    private double precio;

    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAutor = getArguments().getInt("idAutor");
            idOriginal = getArguments().getInt("idOriginal");
            tipo = getArguments().getString("tipo");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            precio = getArguments().getDouble("precio");
            autor = getArguments().getString("autor");
            extra = getArguments().getString("extra");
            calificacion = getArguments().getString("calificacion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_detalle_publicacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        int miId = sessionManager.getUserId();

        // 1. Llenar datos visuales
        ((TextView) view.findViewById(R.id.tvDetalleTitulo)).setText(titulo);
        ((TextView) view.findViewById(R.id.tvDetalleDescripcion)).setText(descripcion);
        ((TextView) view.findViewById(R.id.tvDetallePrecio)).setText(String.format("$ %.2f", precio));
        ((TextView) view.findViewById(R.id.tvDetalleAutor)).setText(autor);
        ((TextView) view.findViewById(R.id.tvDetalleCalif)).setText(calificacion + " ★");

        TextView tvLabelExtra = view.findViewById(R.id.tvLabelExtra);
        TextView tvExtra = view.findViewById(R.id.tvDetalleExtra);
        TextView tvTituloDesc = view.findViewById(R.id.tvTituloDescripcion); // Asegúrate de tener este ID en XML

        // Lógica visual dinámica (Curso vs Clase)
        if (Publicacion.TIPO_CURSO.equals(tipo)) {
            tvLabelExtra.setText("Archivo del curso:");
            tvExtra.setText(extra != null ? extra : "No disponible");
            if (tvTituloDesc != null) tvTituloDesc.setText("Acerca de este curso");
        } else {
            tvLabelExtra.setText("Requisitos para la clase:");
            tvExtra.setText(extra != null ? extra : "Sin requisitos");
            if (tvTituloDesc != null) tvTituloDesc.setText("Acerca de esta clase");
        }

        // 2. Control de Paneles
        LinearLayout panelDueno = view.findViewById(R.id.panelDueno);
        LinearLayout panelCliente = view.findViewById(R.id.panelClienteCompra);
        LinearLayout panelAlumno = view.findViewById(R.id.panelAlumnoAcceso);
        Button btnAccion = view.findViewById(R.id.btnAccionPrincipal);

        view.findViewById(R.id.fabAtras).setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        if (miId == idAutor) {
            // SOY EL DUEÑO
            panelDueno.setVisibility(View.VISIBLE);
            panelCliente.setVisibility(View.GONE);
            panelAlumno.setVisibility(View.GONE);
            configurarPanelDueno(view);
        } else {
            // SOY CLIENTE (Validamos si ya compré)
            verificarSiYaCompre(miId, panelCliente, panelAlumno, btnAccion);
        }
    }

    // --- LÓGICA DE VERIFICACIÓN DE COMPRA ---
    private void verificarSiYaCompre(int miId, View panelCompra, View panelAcceso, Button btnAccion) {
        // Estado inicial: Asumimos que no lo tiene (mostramos botón de compra)
        panelCompra.setVisibility(View.VISIBLE);
        panelAcceso.setVisibility(View.GONE);
        configurarBotonCompra(btnAccion);

        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);
        api.historialPorUsuario(miId).enqueue(new Callback<List<HistorialDto>>() {
            @Override
            public void onResponse(Call<List<HistorialDto>> call, Response<List<HistorialDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean yaLoTengo = false;
                    for (HistorialDto h : response.body()) {
                        if (Publicacion.TIPO_CURSO.equals(tipo)) {
                            if (h.getCursoId() != null && h.getCursoId() == idOriginal) yaLoTengo = true;
                        } else {
                            if (h.getServicioId() != null && h.getServicioId() == idOriginal) yaLoTengo = true;
                        }
                    }

                    if (yaLoTengo) {
                        panelCompra.setVisibility(View.GONE);
                        panelAcceso.setVisibility(View.VISIBLE);

                        // Configurar botón "Ver Contenido"
                        requireView().findViewById(R.id.btnVerContenido).setOnClickListener(v ->
                                Toast.makeText(getContext(), "Abriendo contenido...", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
            @Override public void onFailure(Call<List<HistorialDto>> call, Throwable t) {}
        });
    }

    private void configurarBotonCompra(Button btnAccion) {
        if (Publicacion.TIPO_CURSO.equals(tipo)) {
            btnAccion.setText("Comprar Curso - " + String.format("$ %.2f", precio));
            btnAccion.setOnClickListener(v -> iniciarCompra());
        } else {
            btnAccion.setText("Ver Horarios Disponibles");
            btnAccion.setOnClickListener(v -> irAAgenda());
        }
    }

    // --- LÓGICA DEL DUEÑO ---
    private void configurarPanelDueno(View view) {
        // Editar
        view.findViewById(R.id.btnEditarCurso).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idOriginal", idOriginal);
            bundle.putString("tipo", tipo);
            bundle.putString("titulo", titulo);
            bundle.putString("descripcion", descripcion);
            bundle.putDouble("precio", precio);
            bundle.putString("extra", extra);
            Navigation.findNavController(v).navigate(R.id.action_detallePublicacion_to_editarPublicacion, bundle);
        });

        // Eliminar
        view.findViewById(R.id.btnEliminar).setOnClickListener(v -> mostrarDialogoConfirmacion());

        // Ver Alumnos (CORREGIDO)
        view.findViewById(R.id.btnVerAlumnos).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idOriginal", idOriginal);
            bundle.putString("tipo", tipo);
            Navigation.findNavController(v).navigate(R.id.action_detallePublicacion_to_verAlumnos, bundle);
        });

        // Gestionar Agenda (Solo si es Clase)
        View btnAgenda = view.findViewById(R.id.btnGestionarAgenda);
        if (Publicacion.TIPO_CURSO.equals(tipo)) {
            btnAgenda.setVisibility(View.GONE);
        } else {
            btnAgenda.setVisibility(View.VISIBLE);
            btnAgenda.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("servicioId", idOriginal);
                bundle.putInt("profesorId", idAutor);
                Navigation.findNavController(v).navigate(R.id.action_detallePublicacion_to_gestionarAgenda, bundle);
            });
        }
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Publicación")
                .setMessage("¿Estás seguro? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> ejecutarEliminacion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarEliminacion() {
        if (Publicacion.TIPO_CURSO.equals(tipo)) {
            CursoApi api = RetrofitClient.getClient().create(CursoApi.class);
            api.deleteCurso(idOriginal).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Curso eliminado", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ServicioApi api = RetrofitClient.getClient().create(ServicioApi.class);
            api.deleteServicio(idOriginal).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Clase eliminada", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // --- ACCIONES DE COMPRA ---
    private void iniciarCompra() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Compra")
                .setMessage("¿Deseas comprar '" + titulo + "' por $" + precio + "?")
                .setPositiveButton("Comprar", (dialog, which) -> realizarPagoCurso())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void realizarPagoCurso() {
        HistorialDto compra = new HistorialDto();
        compra.setFechapago(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        compra.setPago(precio);
        compra.setUsuarioId(sessionManager.getUserId());
        compra.setCursoId(idOriginal);

        HistorialApi api = RetrofitClient.getClient().create(HistorialApi.class);
        api.crear(compra).enqueue(new Callback<HistorialDto>() {
            @Override
            public void onResponse(Call<HistorialDto> call, Response<HistorialDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Compra exitosa!", Toast.LENGTH_LONG).show();
                    // Actualizamos la UI inmediatamente
                    requireView().findViewById(R.id.panelClienteCompra).setVisibility(View.GONE);
                    requireView().findViewById(R.id.panelAlumnoAcceso).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Error en la compra: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<HistorialDto> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void irAAgenda() {
        Bundle bundle = new Bundle();
        bundle.putInt("servicioId", idOriginal);
        bundle.putInt("profesorId", idAutor);
        bundle.putString("titulo", titulo);
        bundle.putDouble("precio", precio); // Enviamos el precio para el historial
        Navigation.findNavController(requireView()).navigate(R.id.action_detallePublicacion_to_agendarClase, bundle);
    }
}