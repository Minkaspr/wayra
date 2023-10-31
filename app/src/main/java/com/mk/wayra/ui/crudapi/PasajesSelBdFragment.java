package com.mk.wayra.ui.crudapi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mk.wayra.R;
import com.mk.wayra.controller.PasajeApiClient;
import com.mk.wayra.controller.PasajeApiService;
import com.mk.wayra.model.Pasaje;
import com.mk.wayra.ui.MainActivity;
import com.mk.wayra.ui.adapter.PasajeRemotoAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasajesSelBdFragment extends Fragment {
    private RecyclerView rvPasajesVendidosBD;
    private PasajeRemotoAdapter adapter;
    private FloatingActionButton fabNuevoPasaje;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("BaseDatosFragment", "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_pasajes_sel_bd, container, false);
        rvPasajesVendidosBD = view.findViewById(R.id.rvPasajesVendidosBD);
        rvPasajesVendidosBD.setLayoutManager(new LinearLayoutManager(getActivity()));
        fabNuevoPasaje = view.findViewById(R.id.fabNuevoPasaje);
        //fabNuevoPasaje.setOnClickListener(v -> ((MainActivity) getActivity()).replaceFragment(new PasajeInsBdFragment(), true));

        PasajeApiService apiService = PasajeApiClient.getApiClient().create(PasajeApiService.class);

        Call<List<Pasaje>> call = apiService.getPasajes();
        call.enqueue(new Callback<List<Pasaje>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pasaje>> call, @NonNull Response<List<Pasaje>> response) {
                Log.d("BaseDatosFragment", "onResponse() called");
                if (response.isSuccessful()) {
                    List<Pasaje> pasajes = response.body();
                    if (pasajes != null) {
                        Log.d("RESPUESTA API", pasajes.toString());
                        adapter = new PasajeRemotoAdapter(getActivity(), pasajes);
                        rvPasajesVendidosBD.setAdapter(adapter);
                        for (Pasaje pasaje : pasajes) {
                            Log.d("Pasaje", "Nombre: " + pasaje.getPrimerNom() + ", Apellido: " + pasaje.getApePaterno() + ", Origen: " + pasaje.getOrigen() + ", Destino: " + pasaje.getDestino());
                        }
                    } else {
                        Log.e("ERROR API", "Pasajes es null");
                    }
                } else {
                    Log.e("ERROR API", "La solicitud no fue exitosa");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Pasaje>> call, @NonNull Throwable t) {
                Log.e("BaseDatosFragment", "onFailure() called with: " + t.getMessage(), t);
            }
        });
        return view;
    }
}