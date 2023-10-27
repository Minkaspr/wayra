package com.mk.wayra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mk.wayra.R;
import com.mk.wayra.model.Provincia;

import java.util.List;

public class ProvinciaAdapter extends ArrayAdapter<Provincia> {
    public ProvinciaAdapter(Context context, List<Provincia> provincias) {
        super(context, 0, provincias);
    }

    @Override
    public @NonNull View getView(int position, View view, @NonNull ViewGroup parent) {
        // Obtén el objeto Provincia en esta posición
        Provincia provincia = getItem(position);
        // Verifica si ya existe una vista reutilizable que podemos usar
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_provincia_list, parent, false);
        }
        TextView tvNombre = view.findViewById(R.id.tvNombreProvincia);
        if (provincia != null) {
            tvNombre.setText(provincia.getNombre());
        }
        // Devuelve la vista completa para que se muestre en la pantalla
        return view;
    }
}
