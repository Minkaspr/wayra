package com.mk.wayra.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.mk.wayra.R;
import com.mk.wayra.ui.crudapi.PasajesSelBdFragment;

import java.io.File;

public class HomeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView mcvTicketSales = view.findViewById(R.id.mcvTicketSales);
        MaterialCardView mcvSynchronizeSales = view.findViewById(R.id.mcvSynchronizeSales);
        MaterialCardView mcvDataBase = view.findViewById(R.id.mcvDataBase);
        TextView tvCantidad = view.findViewById(R.id.tvCantidad);
        tvCantidad.setText(getResources().getString(R.string.fh_card_2_c_secund, contarArchivos("pj_")));

        mcvTicketSales.setOnClickListener(v -> replaceFragment(new PasajeVentaFragment()));
        mcvSynchronizeSales.setOnClickListener(v -> replaceFragment(new SincronizarVentasFragment()));
        mcvDataBase.setOnClickListener(v -> replaceFragment(new PasajesSelBdFragment()));
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.replaceFragment(fragment, true);
        }
    }

    private int contarArchivos(String prefijo) {
        int contador = 0;

        if(getActivity()!=null){
            File carpeta = getActivity().getDir("wayra", Context.MODE_PRIVATE);
            File[] archivos = carpeta.listFiles();
            for (File archivo : archivos) {
                if (archivo.getName().startsWith(prefijo)) {
                    contador++;
                }
            }
        }
        return contador;
    }
}