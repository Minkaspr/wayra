package com.mk.wayra.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mk.wayra.R;
import com.mk.wayra.controller.PasajeApiClient;
import com.mk.wayra.controller.PasajeApiService;
import com.mk.wayra.model.Pasaje;
import com.mk.wayra.ui.adapter.PasajeAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SincronizarVentasFragment extends Fragment {
    private List<Pasaje> pasajes;
    private RecyclerView rvPasajesVendidos;
    private PasajeAdapter adapter;
    private Button btnSincronizar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sincronizar_ventas, container, false);

        pasajes = new ArrayList<>();
        rvPasajesVendidos = view.findViewById(R.id.rvPasajesVendidos);
        rvPasajesVendidos.setLayoutManager(new LinearLayoutManager(getActivity()));

        pasajes = cargarPasajesDesdeArchivosJSON();
        adapter = new PasajeAdapter(getActivity(), pasajes);
        rvPasajesVendidos.setAdapter(adapter);
        btnSincronizar = view.findViewById(R.id.btnSincronizar);
        btnSincronizar.setOnClickListener(v->sincronizarPasajes());
        btnSincronizar.setEnabled(!pasajes.isEmpty());
        return view;
    }

    private List<Pasaje> cargarPasajesDesdeArchivosJSON() {
        List<Pasaje> pasajes = new ArrayList<>();

        Activity activity = getActivity();
        if (activity != null) {
            File carpeta = activity.getDir("wayra", Context.MODE_PRIVATE);
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getName().startsWith("pj_")) {
                        System.out.println("Archivo: " + archivo.getName());
                        try {
                            String json = leerArchivoJson(archivo);
                            if (!json.isEmpty()) {
                                Pasaje pasaje = convertirJsonAPasaje(json);
                                pasajes.add(pasaje);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return pasajes;
    }

    private String leerArchivoJson(File archivo) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(archivo));
        String linea;
        while ((linea = bufferedReader.readLine()) != null) {
            stringBuilder.append(linea).append('\n');
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private Pasaje convertirJsonAPasaje(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String primerNom = jsonObject.optString("primerNom");
        String apePaterno = jsonObject.optString("apePaterno");
        String origen = jsonObject.optString("origen");
        String destino = jsonObject.optString("destino");
        String fecha = jsonObject.optString("fecha");
        String hora = jsonObject.optString("hora");
        return new Pasaje(primerNom, apePaterno, origen, destino, fecha, hora);
    }

    private void sincronizarPasajes() {
        Activity activity = getActivity();
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_progreso_sincronizar, null);
            builder.setView(view);

            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            TextView tvProgress = view.findViewById(R.id.tvProgress);
            TextView tvProgressCount = view.findViewById(R.id.tvProgressCount);

            AlertDialog dialog = builder.create();
            dialog.show();

            new Thread(() -> {
                File carpeta = activity.getDir("wayra", Context.MODE_PRIVATE);
                File[] archivos = carpeta.listFiles();
                if (archivos != null) {
                    int total = archivos.length;
                    activity.runOnUiThread(() -> progressBar.setMax(total));
                    int progress = 0;
                    for (File archivo : archivos) {
                        if (archivo.getName().startsWith("pj_")) {
                            try {
                                String json = leerArchivoJson(archivo);
                                if (!json.isEmpty()) {
                                    Pasaje pasaje = convertirJsonCompletoAPasaje(json);
                                    enviarPasajePorPost(pasaje);
                                    archivo.delete();  // Elimina el archivo después de enviar el pasaje
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            progress++;
                            final int finalProgress = progress;
                            activity.runOnUiThread(() -> {
                                progressBar.setProgress(finalProgress);
                                tvProgress.setText(finalProgress * 100 / total + "%");
                                tvProgressCount.setText(finalProgress + "/" + total);
                            });
                        }
                    }
                }
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    pasajes = cargarPasajesDesdeArchivosJSON();
                    adapter.setPasajes(pasajes);
                    adapter.notifyDataSetChanged();

                    btnSincronizar.setEnabled(!pasajes.isEmpty());
                }, 3000);
            }).start();
        }
    }


    private Pasaje convertirJsonCompletoAPasaje(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        List<String> camposVacios = new ArrayList<>();

        String primerNom = obtenerTexto(jsonObject, "primerNom", camposVacios, false);
        String segundoNom = obtenerTexto(jsonObject, "segundoNom", camposVacios, true);
        String apePaterno = obtenerTexto(jsonObject, "apePaterno", camposVacios, false);
        String apeMaterno = obtenerTexto(jsonObject, "apeMaterno", camposVacios, false);
        String numIdentidad = obtenerTexto(jsonObject, "numIdentidad", camposVacios, false);
        String telefono = obtenerTexto(jsonObject, "telefono", camposVacios, true);
        String origen = obtenerTexto(jsonObject, "origen", camposVacios, false);
        String destino = obtenerTexto(jsonObject, "destino", camposVacios, false);
        String fecha = obtenerTexto(jsonObject, "fecha", camposVacios, false);
        String hora = obtenerTexto(jsonObject, "hora", camposVacios, false);
        Double precio = jsonObject.optDouble("precio");

        Pasaje pasaje = new Pasaje();
        pasaje.setPrimerNom(primerNom);
        pasaje.setSegundoNom(segundoNom);
        pasaje.setApePaterno(apePaterno);
        pasaje.setApeMaterno(apeMaterno);
        pasaje.setNumIdentidad(numIdentidad);
        pasaje.setTelefono(telefono);
        pasaje.setOrigen(origen);
        pasaje.setDestino(destino);
        pasaje.setFecha(fecha);
        pasaje.setHora(hora);
        pasaje.setPrecio(precio);

        if (!camposVacios.isEmpty()) {
            System.out.println("Los siguientes campos están vacíos: " + String.join(", ", camposVacios));
        }

        return pasaje;
    }

    private String obtenerTexto(JSONObject jsonObject, String nombreCampo, List<String> camposVacios, boolean esOpcional) {
        String texto = jsonObject.optString(nombreCampo);
        if (texto == null || texto.isEmpty()) {
            if (!esOpcional) {
                camposVacios.add(nombreCampo);
            }
            return null;
        }
        return texto;
    }


    private void enviarPasajePorPost(Pasaje pasaje) {
        PasajeApiService apiService = PasajeApiClient.getApiClient().create(PasajeApiService.class);

        Call<Void> call = apiService.createPasaje(pasaje);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PasajeInsBdFragment", "Pasaje creado con éxito");
                } else {
                    Log.e("ERROR API", "La solicitud no fue exitosa");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("ERROR API", "La solicitud falló");
            }
        });
    }
}

