package com.example.saber_share.util.api;

import com.example.saber_share.model.UsuarioDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioApi {

    // 1. Obtener todos los usuarios
    @GET("usuario")
    Call<List<UsuarioDto>> getAll();

    // 2. Obtener un usuario por su ID (NECESARIO PARA PERFIL)
    @GET("usuario/{id}")
    Call<UsuarioDto> getById(@Path("id") int id);

    // 3. Login (Filtrar por username)
    @GET("usuario")
    Call<List<UsuarioDto>> login(@Query("user") String user);

    // 4. Buscar por correo
    @GET("usuario")
    Call<List<UsuarioDto>> BuscaCorreo(@Query("correo") String correo);

    // 5. Registrar usuario nuevo
    @POST("usuario")
    Call<UsuarioDto> registrar(@Body UsuarioDto usuario);

    // 6. Actualizar usuario
    @PUT("usuario/{id}")
    Call<UsuarioDto> update(@Path("id") int id, @Body UsuarioDto usuario);
}