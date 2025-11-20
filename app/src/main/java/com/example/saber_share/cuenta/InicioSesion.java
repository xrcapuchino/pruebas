package com.example.saber_share.cuenta;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saber_share.MainActivity;
import com.example.saber_share.R;
import com.example.saber_share.api.RetrofitClient;
import com.example.saber_share.api.UsuarioApi;
import com.example.saber_share.dto.UsuarioDto;
import com.example.saber_share.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesion extends Fragment implements View.OnClickListener {

    EditText etCorreo;
    EditText etPassword;
    Button btnIniciarSesion;
    Button btnRegistrarse;

    private SessionManager sessionManager;

    public InicioSesion() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        etCorreo = view.findViewById(R.id.etCorreo);
        etPassword = view.findViewById(R.id.etPassword);
        btnIniciarSesion = view.findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = view.findViewById(R.id.btnRegistrarse);

        btnIniciarSesion.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio_sesion, container, false);
    }
    @Override
    public void onClick(View view) {
        String opcion = ((Button)view).getText().toString();

        if (opcion.equals("Iniciar sesión")) {
            String correo = etCorreo.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(correo)) {
                etCorreo.setError("Ingresa tu correo");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Ingresa tu contraseña");
                return;
            }

            //bloquea el botoncito porque si no luego se corrompe la cosa esta
            btnIniciarSesion.setEnabled(false);
            btnIniciarSesion.setText("Cargando...");

            UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);
            Call<List<UsuarioDto>> call = api.login(correo);

            call.enqueue(new Callback<List<UsuarioDto>>() {
                @Override
                public void onResponse(Call<List<UsuarioDto>> call, Response<List<UsuarioDto>> response) {
                    btnIniciarSesion.setEnabled(true);
                    btnIniciarSesion.setText("Iniciar sesión");

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        UsuarioDto u = response.body().get(0);

                        if (password.equals(u.getPassword())) {
                            realizarLoginExitoso(u.getCorreo(), u.getPassword());
                        } else {
                            Toast.makeText(getContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<UsuarioDto>> call, Throwable t) {
                    // Restaurar botón y mostrar error
                    btnIniciarSesion.setEnabled(true);
                    btnIniciarSesion.setText("Iniciar sesión");
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
        else if (opcion.equals("Registrarse")){
            Navigation.findNavController(view).navigate(R.id.action_inicioSesion_to_registroSesion);

        }
        else {
            Toast.makeText(getContext(), "No se encontro opcion" + opcion, Toast.LENGTH_SHORT).show();
        }

    }

    private void realizarLoginExitoso(String correo, String password) {
        sessionManager.createLoginSession(correo, password);
        Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}