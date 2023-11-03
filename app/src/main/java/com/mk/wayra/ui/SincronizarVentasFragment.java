package com.mk.wayra.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mk.wayra.R;
import com.mk.wayra.model.Pasaje;
import com.mk.wayra.ui.adapter.PasajeAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SincronizarVentasFragment extends Fragment {
    private List<Pasaje> pasajes;
    private RecyclerView rvPasajesVendidos;
    private PasajeAdapter adapter;
    private Button btnConfirmar;

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
        btnConfirmar = view.findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(v->{});

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
}

