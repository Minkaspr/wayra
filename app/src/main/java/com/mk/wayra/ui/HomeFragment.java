package com.mk.wayra.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.mk.wayra.R;
import com.mk.wayra.ui.crudapi.PasajesSelBdFragment;

public class HomeFragment extends Fragment {

    private MaterialCardView mcvTicketSales, mcvSynchronizeSales, mcvDataBase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mcvTicketSales = view.findViewById(R.id.mcvTicketSales);
        mcvSynchronizeSales = view.findViewById(R.id.mcvSynchronizeSales);
        mcvDataBase = view.findViewById(R.id.mcvDataBase);
        //mcvTicketSales.setOnClickListener(v ->replaceFragment(new PasajesVentasFragment()));
        mcvTicketSales.setOnClickListener(v ->((MainActivity) getActivity()).replaceFragment(new PasajesVentasFragment(), true));
        mcvSynchronizeSales.setOnClickListener(v ->replaceFragment(new SincronizarVentasFragment()));
        mcvDataBase.setOnClickListener(v -> replaceFragment(new PasajesSelBdFragment()));
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}