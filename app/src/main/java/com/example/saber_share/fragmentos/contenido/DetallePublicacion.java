package com.example.saber_share.fragmentos.contenido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.saber_share.R;
import com.example.saber_share.util.local.SessionManager;

public class DetallePublicacion extends Fragment {

    private int idAutor;
    private String titulo, descripcion, autor;
    private double precio;

    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAutor = getArguments().getInt("idAutor");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            precio = getArguments().getDouble("precio");
            autor = getArguments().getString("autor");
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

        // Llenar datos
        ((TextView) view.findViewById(R.id.tvDetalleTitulo)).setText(titulo);
        ((TextView) view.findViewById(R.id.tvDetalleDescripcion)).setText(descripcion);
        ((TextView) view.findViewById(R.id.tvDetallePrecio)).setText(String.format("$ %.2f MXN", precio));
        ((TextView) view.findViewById(R.id.tvDetalleAutor)).setText(autor);

        // Lógica Dueño vs Cliente
        LinearLayout layoutCliente = view.findViewById(R.id.layoutAccionesCliente);
        LinearLayout layoutDueno = view.findViewById(R.id.layoutAccionesDueno);

        if (miId == idAutor) {
            // ES MI PUBLICACIÓN
            layoutCliente.setVisibility(View.GONE);
            layoutDueno.setVisibility(View.VISIBLE);

            view.findViewById(R.id.btnEditar).setOnClickListener(v -> {
                Toast.makeText(getContext(), "Ir a editar...", Toast.LENGTH_SHORT).show();
                // Navegar a fragmento de edición
            });

            view.findViewById(R.id.btnEliminar).setOnClickListener(v -> {
                // Lógica de eliminar (Llamada a API DELETE)
                Toast.makeText(getContext(), "Eliminando...", Toast.LENGTH_SHORT).show();
            });

        } else {
            // SOY CLIENTE
            layoutCliente.setVisibility(View.VISIBLE);
            layoutDueno.setVisibility(View.GONE);

            Button btnAccion = view.findViewById(R.id.btnAccionPrincipal);
            // Cambiar texto según si es curso o clase (necesitas recibir el 'tipo' en argumentos)
            btnAccion.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Procesando compra...", Toast.LENGTH_SHORT).show();
                // Ir a pasarela de pago
            });
        }
    }
}