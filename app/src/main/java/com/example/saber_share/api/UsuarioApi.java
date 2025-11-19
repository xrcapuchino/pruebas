package com.example.saber_share.api;

import com.example.saber_share.dto.UsuarioDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsuarioApi {

    // GET /Amaury/api/usuario?user=...
    @GET("usuario")
    Call<List<UsuarioDto>> login(
            @Query("user") String user
    );

    // POST /Amaury/api/usuario
    @POST("usuario")
    Call<UsuarioDto> registrar(
            @Body UsuarioDto usuario
    );
}