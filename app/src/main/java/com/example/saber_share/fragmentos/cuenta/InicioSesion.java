package com.example.saber_share.fragmentos.cuenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.saber_share.MainActivity;
import com.example.saber_share.R;
import com.example.saber_share.model.UsuarioDto;
import com.example.saber_share.util.api.RetrofitClient;
import com.example.saber_share.util.api.UsuarioApi;
import com.example.saber_share.util.local.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesion extends Fragment {

    private EditText etUsuarioInput, etPassword;
    private Button btnIngresar, btnRegistrar;
    private SessionManager session;

    public InicioSesion() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cuenta_inicio_sesion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());

        // Vinculamos las vistas
        // Nota: En tu XML el ID sigue siendo "etCorreo", pero aquí lo trataremos como usuario
        etUsuarioInput = view.findViewById(R.id.etCorreo);
        etPassword = view.findViewById(R.id.etPassword);
        btnIngresar = view.findViewById(R.id.btnIniciarSesion);
        btnRegistrar = view.findViewById(R.id.btnRegistrarse);

        btnIngresar.setOnClickListener(v -> login());

        if (btnRegistrar != null) {
            btnRegistrar.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_inicioSesion_to_registroSesion)
            );
        }
    }

    private void login() {
        // Obtenemos el texto ingresado (Nombre de Usuario)
        String inputUser = etUsuarioInput.getText().toString().trim();
        String inputPass = etPassword.getText().toString().trim();

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);

        // Usamos lista() para traer todos y filtrar por NOMBRE DE USUARIO
        api.getAll().enqueue(new Callback<List<UsuarioDto>>() {
            @Override
            public void onResponse(Call<List<UsuarioDto>> call, Response<List<UsuarioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioDto usuarioEncontrado = null;

                    for(UsuarioDto u : response.body()) {
                        // --- CAMBIO AQUÍ: Comparamos getUser() en lugar de getCorreo() ---
                        if(u.getUser() != null && u.getUser().equalsIgnoreCase(inputUser) &&
                                u.getPassword() != null && u.getPassword().equals(inputPass)) {

                            usuarioEncontrado = u;
                            break;
                        }
                    }

                    if (usuarioEncontrado != null) {
                        if (usuarioEncontrado.getIdUsuario() != null) {
                            session.createLoginSession(
                                    usuarioEncontrado.getUser(),
                                    inputPass,
                                    usuarioEncontrado.getIdUsuario()
                            );

                            Toast.makeText(getContext(), "Bienvenido " + usuarioEncontrado.getNombre(), Toast.LENGTH_SHORT).show();
                            irAMain();
                        } else {
                            Toast.makeText(getContext(), "Error: ID de usuario inválido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error de servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                Log.e("LOGIN", "Error", t);
            }
        });
    }

    private void irAMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}