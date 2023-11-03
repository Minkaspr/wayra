package com.mk.wayra.controller;

import com.mk.wayra.model.Pasaje;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PasajeApiService {
    @GET("pasajes")
    Call<List<Pasaje>> getPasajes();

    @GET("pasajes/{id}")
    Call<Pasaje> getPasaje(@Path("id") int id);

    @POST("pasajes")
    Call<Void> createPasaje(@Body Pasaje newPasaje);
}
