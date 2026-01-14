package com.example.saber_share.util.api;

import com.example.saber_share.model.MensajeDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MensajeApi {
    @POST("mensaje")
    Call<MensajeDto> enviar(@Body MensajeDto mensaje);

    @GET("mensaje/chat")
    Call<List<MensajeDto>> verConversacion(@Query("u1") int u1, @Query("u2") int u2);

    // NUEVO: Para ver la lista de chats
    @GET("mensaje/bandeja/{miId}")
    Call<List<MensajeDto>> verBandeja(@Path("miId") int miId);
}