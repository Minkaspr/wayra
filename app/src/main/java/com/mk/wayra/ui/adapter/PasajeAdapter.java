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

public class PasajeAdapter extends RecyclerView.Adapter<PasajeAdapter.PasajeViewHolder> {
    private Context context;
    private List<Pasaje> pasajes;

    public PasajeAdapter(Context context, List<Pasaje> pasajes) {
        this.context = context;
        this.pasajes = pasajes;
    }

    class PasajeViewHolder extends RecyclerView.ViewHolder {
        private TextView actvDatosPersonales, actvOrigen, actvDestino, actvHora, actvFecha;

        public PasajeViewHolder(View itemView) {
            super(itemView);
            actvDatosPersonales = itemView.findViewById(R.id.actvDatosPersonales);
            actvOrigen = itemView.findViewById(R.id.actvOrigen);
            actvDestino = itemView.findViewById(R.id.actvDestino);
            actvHora = itemView.findViewById(R.id.actvHora);
            actvFecha = itemView.findViewById(R.id.actvFecha);
        }

        public void bind(Pasaje pasaje) {
            String nombreApellido = context.getResources().getString(R.string.ipl_datos_pers, pasaje.getPrimerNom(), pasaje.getApePaterno());
            actvDatosPersonales.setText(nombreApellido);
            actvOrigen.setText(pasaje.getOrigen());
            actvDestino.setText(pasaje.getDestino());
            actvHora.setText(String.valueOf(pasaje.getHora()));
            actvFecha.setText(String.valueOf(pasaje.getFecha()));
        }
    }

    @NonNull
    @Override
    public PasajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pasaje_list, parent, false);
        return new PasajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PasajeViewHolder holder, int position) {
        Pasaje pasaje = pasajes.get(position);
        holder.bind(pasaje);
    }

    @Override
    public int getItemCount() {
        return pasajes.size();
    }
}