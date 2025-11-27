package com.example.saber_share.util.api;

import com.example.saber_share.model.ServicioDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServicioApi {

    @GET("servicio")
    Call<List<ServicioDto>> lista();
    @POST("servicio")
    Call<ServicioDto> crearServicio(@Body ServicioDto servicio);
}
