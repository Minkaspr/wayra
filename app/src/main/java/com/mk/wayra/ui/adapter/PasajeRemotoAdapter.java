package com.mk.wayra.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mk.wayra.R;
import com.mk.wayra.model.Pasaje;

import java.util.List;

public class PasajeRemotoAdapter extends RecyclerView.Adapter<PasajeRemotoAdapter.PasajeRemotoViewHolder>{
    private List<Pasaje> pasajesList;
    private Context context;

    public PasajeRemotoAdapter(Context context, List<Pasaje> pasajesList) {
        this.context = context;
        this.pasajesList = pasajesList;
    }

    public class PasajeRemotoViewHolder extends RecyclerView.ViewHolder {
        public TextView actvDatosPersonales, actvOrigen, actvDestino, actvHora, actvFecha;

        public PasajeRemotoViewHolder(View view) {
            super(view);
            actvDatosPersonales = view.findViewById(R.id.actvDatosPersonales);
            actvOrigen = view.findViewById(R.id.actvOrigen);
            actvDestino = view.findViewById(R.id.actvDestino);
            actvHora = view.findViewById(R.id.actvHora);
            actvFecha = view.findViewById(R.id.actvFecha);
        }
    }

    @NonNull
    @Override
    public PasajeRemotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pasaje_list, parent, false);

        return new PasajeRemotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PasajeRemotoViewHolder holder, int position) {
        Pasaje pasaje = pasajesList.get(position);
        String nombreApellido =  context.getResources().getString(R.string.ipl_datos_pers, pasaje.getPrimerNom(), pasaje.getApePaterno());
        String origen = pasaje.getOrigen();
        String destino = pasaje.getDestino();
        String hora = pasaje.getHora();
        String fecha = pasaje.getFecha();
        holder.actvDatosPersonales.setText(nombreApellido);
        holder.actvOrigen.setText(origen);
        holder.actvDestino.setText(destino);
        holder.actvHora.setText(hora);
        holder.actvFecha.setText(fecha);
    }

    @Override
    public int getItemCount() {
        return pasajesList.size();
    }
}
