package com.example.saber_share.util.api;

import com.example.saber_share.model.OpinionServicioDto;
import com.example.saber_share.model.OpinionesCursoDto;
import java.util.List; // Agrega esto si te falta
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET; // Agrega esto
import retrofit2.http.POST;
import retrofit2.http.Path; // Agrega esto

public interface OpinionApi {

    // --- CORRECCIÓN: Quitamos "Saber_Share/api/" porque RetrofitClient ya lo tiene ---

    // SERVICIOS (Clases 1 a 1)
    @GET("opinion_servicio/servicio/{id}")
    Call<List<OpinionServicioDto>> getOpinionesServicio(@Path("id") int idServicio);

    @POST("opinion_servicio")
    Call<OpinionServicioDto> calificarServicio(@Body OpinionServicioDto dto);

    // CURSOS (Videos)
    @GET("opiniones_curso/curso/{id}")
    Call<List<OpinionesCursoDto>> getOpinionesCurso(@Path("id") int idCurso);

    @POST("opiniones_curso")
    Call<OpinionesCursoDto> calificarCurso(@Body OpinionesCursoDto dto);
}