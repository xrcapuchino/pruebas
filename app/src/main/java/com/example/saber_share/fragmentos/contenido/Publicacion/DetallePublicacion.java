package com.example.saber_share.fragmentos.contenido.Publicacion;

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
import com.example.saber_share.model.Publicacion;
import com.example.saber_share.util.local.SessionManager;

public class DetallePublicacion extends Fragment {

    private int idAutor;
    // Necesitamos guardar el ID original de la publicación para poder editarla
    private int idOriginal;
    private String tipo, titulo, descripcion, autor, extra;
    private double precio;
    private String calificacion;

    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Recibimos todos los datos del bundle
            idAutor = getArguments().getInt("idAutor");
            // IMPORTANTE: Asegúrate de recibir el idOriginal desde el Adapter
            idOriginal = getArguments().getInt("idOriginal");
            tipo = getArguments().getString("tipo"); // CURSO o CLASE
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            precio = getArguments().getDouble("precio");
            autor = getArguments().getString("autor");
            extra = getArguments().getString("extra"); // Archivo o Requisitos
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

        // Mostrar Archivo o Requisitos según tipo
        TextView tvLabelExtra = view.findViewById(R.id.tvLabelExtra);
        TextView tvExtra = view.findViewById(R.id.tvDetalleExtra);

        if (Publicacion.TIPO_CURSO.equals(tipo)) {
            tvLabelExtra.setText("Archivo del curso:");
            tvExtra.setText(extra != null ? extra : "No disponible");
        } else {
            tvLabelExtra.setText("Requisitos para la clase:");
            tvExtra.setText(extra != null ? extra : "Sin requisitos");
        }

        // 2. Lógica de Roles (Paneles)
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
            // SOY CLIENTE (Lógica simplificada por ahora)
            boolean yaComprado = false; // Aquí iría la consulta a la API de historial

            if (yaComprado) {
                panelAlumno.setVisibility(View.VISIBLE);
                panelCliente.setVisibility(View.GONE);
                configurarPanelAlumno(view);
            } else {
                panelCliente.setVisibility(View.VISIBLE);
                panelAlumno.setVisibility(View.GONE);

                if (Publicacion.TIPO_CURSO.equals(tipo)) {
                    btnAccion.setText("Comprar Curso - " + String.format("$ %.2f", precio));
                    btnAccion.setOnClickListener(v -> iniciarCompra());
                } else {
                    btnAccion.setText("Agendar Clase");
                    btnAccion.setOnClickListener(v -> irAAgenda());
                }
            }
        }
    }

    private void configurarPanelDueno(View view) {
        // Lógica para el botón "Editar"
        view.findViewById(R.id.btnEditarCurso).setOnClickListener(v -> {
            // Preparamos el paquete para enviar al fragmento de edición
            Bundle bundle = new Bundle();
            bundle.putInt("idOriginal", idOriginal); // ID para el PUT en la API
            bundle.putString("tipo", tipo);
            bundle.putString("titulo", titulo);
            bundle.putString("descripcion", descripcion);
            bundle.putDouble("precio", precio);
            bundle.putString("extra", extra);

            // Navegamos al fragmento de edición (Asegúrate de tener esta acción en main_nav.xml)
            Navigation.findNavController(v).navigate(R.id.action_detallePublicacion_to_editarPublicacion, bundle);
        });

        view.findViewById(R.id.btnVerAlumnos).setOnClickListener(v ->
                Toast.makeText(getContext(), "Ver lista de alumnos (Próximamente)", Toast.LENGTH_SHORT).show());
    }

    private void configurarPanelAlumno(View view) {
        view.findViewById(R.id.btnVerContenido).setOnClickListener(v -> {
            if (Publicacion.TIPO_CURSO.equals(tipo)) {
                Toast.makeText(getContext(), "Abriendo archivo: " + extra, Toast.LENGTH_LONG).show();
                // Aquí lanzaríamos un Intent para abrir el PDF/Video
            }
        });
    }

    private void iniciarCompra() {
        Toast.makeText(getContext(), "Iniciando pasarela de pago...", Toast.LENGTH_SHORT).show();
        // Navegar a fragmento de pago
    }

    private void irAAgenda() {
        Toast.makeText(getContext(), "Abriendo calendario...", Toast.LENGTH_SHORT).show();
        // Navegar a fragmento de agenda
    }
}