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

import com.mk.wayra.R;
import com.mk.wayra.model.Pasaje;
import com.mk.wayra.ui.adapter.PasajeAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SincronizarVentasFragment extends Fragment {
    private List<Pasaje> pasajes;
    private RecyclerView rvPasajesVendidos;
    private PasajeAdapter adapter;

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

        return view;
    }

    private List<Pasaje> cargarPasajesDesdeArchivosJSON() {
        List<Pasaje> pasajes = new ArrayList<>();

        Activity activity = getActivity();
        if (activity != null) {
            File carpeta = activity.getDir("wayra", Context.MODE_PRIVATE);
            System.out.println("Carpeta: " + carpeta);
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                System.out.println("Archivos encontrados: " + archivos.length);
                for (File archivo : archivos) {
                    System.out.println("Archivo: " + archivo.getName());
                    try {
                        String json = leerArchivoJson(archivo);
                        if (!json.isEmpty()) {
                            System.out.println("JSON: " + json);
                            Pasaje pasaje = convertirJsonAPasaje(json);
                            System.out.println("Pasaje: " + pasaje);
                            pasajes.add(pasaje);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        System.out.println("Error al procesar el archivo: " + archivo.getName());
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

        /*System.out.println("Fecha y hora recuperadas: "+fecha+" - "+hora);
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        Date date = null;
        Time time = null;

        try {
            date = formatoFecha.parse(fecha);
            Date dateHora = formatoHora.parse(hora);
            if (dateHora != null) {
                time = new Time(dateHora.getTime());
            }
            System.out.println("Fecha: "+date);
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            System.out.println("Fecha: " + df.format(date));
            return new Pasaje(primerNom, apePaterno, origen, destino, date, time);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error al convertir la fecha y la hora");
            return null;
        }*/
        return new Pasaje(primerNom, apePaterno, origen, destino, fecha, hora);
    }
}

