package com.example.saber_share.fragmentos.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.saber_share.R;

public class CuentaAutentificacion extends Fragment {

    public CuentaAutentificacion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cuenta_autentificacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnIniciarSesion = view.findViewById(R.id.btnIniciarSesion);
        Button btnRegistrar = view.findViewById(R.id.btnRegistrar);

        btnIniciarSesion.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_cuentaAutentificacion_to_inicioSesion)
        );

        btnRegistrar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_cuentaAutentificacion_to_registroSesion)
        );
    }
}