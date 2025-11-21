package com.example.saber_share.cuenta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.saber_share.api.RetrofitClient;
import com.example.saber_share.api.UsuarioApi;
import com.example.saber_share.dto.UsuarioDto;
import com.example.saber_share.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroSesion extends Fragment implements View.OnClickListener {

    EditText etUsuario;
    EditText etNombre;
    EditText etApellido;
    EditText etCorreo;
    EditText etTelefono;
    EditText etPassword;

    // Botones
    Button btnRegistrarse;
    Button btnIniciarSesion; // Botón para "Ya tengo cuenta"

    private SessionManager sessionManager;

    public RegistroSesion() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_registro_sesion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        etUsuario   = view.findViewById(R.id.etUsuario);
        etNombre    = view.findViewById(R.id.etNombre);
        etApellido  = view.findViewById(R.id.etApellido);
        etCorreo    = view.findViewById(R.id.etCorreo);
        etTelefono  = view.findViewById(R.id.etTelefono);
        etPassword  = view.findViewById(R.id.etPassword);

        btnRegistrarse = view.findViewById(R.id.btnRegistrarse);
        btnIniciarSesion = view.findViewById(R.id.btnIniciarSesion);

        btnRegistrarse.setOnClickListener(this);
        btnIniciarSesion.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String opcion = ((Button)view).getText().toString();
        if (opcion.equals("Registrarse")) {
            validarYRegistrar();
        }
        else if (opcion.equals("Iniciar Sesion")) {
            Navigation.findNavController(view).navigate(R.id.action_registroSesion_to_inicioSesion);
        }
        else {
            Toast.makeText(getContext(), "No se encontró la opción: " + opcion, Toast.LENGTH_SHORT).show();
        }
    }

    private void validarYRegistrar() {
        String usuario  = etUsuario.getText().toString().trim();
        String nombre   = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String correo   = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(usuario)) {
            etUsuario.setError("Ingresa un usuario");
            return;
        }
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresa tu nombre");
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("Ingresa tu apellido");
            return;
        }
        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError("Ingresa tu correo");
            return;
        }
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("Ingresa tu teléfono");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Crea una contraseña");
            return;
        }

        btnRegistrarse.setEnabled(false);
        btnRegistrarse.setText("Validando");

        iniciarValidacionUsuario(usuario, nombre, apellido, correo, telefono, password);
    }

    private void iniciarValidacionUsuario(String user, String nom, String ape, String mail, String tel, String pass) {
        UsuarioApi api = RetrofitClient.getClient().create(UsuarioApi.class);
        api.login(user).enqueue(new Callback<List<UsuarioDto>>() {

            @Override
            public void onResponse(Call<List<UsuarioDto>> call, Response<List<UsuarioDto>> response) {
                // Si la lista NO está vacía, el usuario YA existe
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    desbloquearBoton();
                    etUsuario.setError("Este usuario ya está en uso");
                    etUsuario.requestFocus();
                } else {
                    // Si no existe, pasamos al PASO 2 (Correo)
                    validarCorreo(api, user, nom, ape, mail, tel, pass);
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioDto>> call, Throwable t) {
                desbloquearBoton();
                Toast.makeText(getContext(), "Error al validar usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void validarCorreo(UsuarioApi api, String user, String nom, String ape, String mail, String tel, String pass) {
        api.BuscaCorreo(mail).enqueue(new Callback<List<UsuarioDto>>() {
            @Override
            public void onResponse(Call<List<UsuarioDto>> call, Response<List<UsuarioDto>> response) {
                boolean correoExiste = false;
                if (response.isSuccessful() && response.body() != null) {
                    for (UsuarioDto u : response.body()) {
                        if (u.getCorreo() != null && u.getCorreo().equalsIgnoreCase(mail)) {
                            correoExiste = true;
                            break;
                        }
                    }
                }
                if (correoExiste) {
                    desbloquearBoton();
                    etCorreo.setError("Este correo ya está registrado");
                    etCorreo.requestFocus();
                } else {
                    Registro(api, user, nom, ape, mail, tel, pass);
                }
            }
            @Override
            public void onFailure(Call<List<UsuarioDto>> call, Throwable t) {
                desbloquearBoton();
                Toast.makeText(getContext(), "Error validando correo", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Registro(UsuarioApi api, String user, String nom, String ape, String mail, String tel, String pass) {
        btnRegistrarse.setText("Registrando...");

        UsuarioDto nuevoUsuario = new UsuarioDto();
        nuevoUsuario.setUser(user);
        nuevoUsuario.setNombre(nom);
        nuevoUsuario.setApellido(ape);
        nuevoUsuario.setCorreo(mail);
        nuevoUsuario.setTelefono(tel);
        nuevoUsuario.setPassword(pass);

        api.registrar(nuevoUsuario).enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                desbloquearBoton();

                if (response.isSuccessful() && response.body() != null) {
                    ingresarAApp(user, pass);
                } else {
                    Toast.makeText(getContext(), "Error del servidor al registrar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
                desbloquearBoton();
                Toast.makeText(getContext(), "Error de conexión final", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ingresarAApp(String usuario, String password) {
        sessionManager.createLoginSession(usuario, password);
        Toast.makeText(getContext(), "Bienvenido a Saber Share", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void desbloquearBoton() {
        if (btnRegistrarse != null) {
            btnRegistrarse.setEnabled(true);
            btnRegistrarse.setText("Registrarse");
        }
    }
}