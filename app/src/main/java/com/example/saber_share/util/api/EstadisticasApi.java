package com.example.saber_share.util.api;

import com.example.saber_share.model.EstadisticasDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EstadisticasApi {
    @GET("estadisticas/{id}")
    Call<EstadisticasDto> obtenerMisEstadisticas(@Path("id") int usuarioId);
}