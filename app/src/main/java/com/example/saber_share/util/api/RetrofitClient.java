package com.example.saber_share.util.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // CAMBIAR POR TU IP (No uses localhost ni 127.0.0.1 si usas celular físico o emulador)
    // Emulador: 10.0.2.2
    // Celular físico: Tu IP de la compu (ej. 192.168.1.50)
    private static final String BASE_URL = "http://10.0.2.2:8080/Saber_Share/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Crear el interceptor (El espía)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Nivel BODY muestra todo: cabeceras, JSON enviado y recibido
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Añadirlo al cliente HTTP
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 3. Construir Retrofit con ese cliente
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // <--- Importante: agregar el cliente aquí
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}