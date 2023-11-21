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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        String fechaString = pasaje.getFecha();
        String fechaAmigable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Para Android Oreo (8.0) o superior, usa java.time.LocalDate
            DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(fechaString, formatoEntrada);

            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("EEEE, dd MMM. yyyy", new Locale("es", "ES"));
            fechaAmigable = fecha.format(formatoSalida);
        } else {
            // Para versiones anteriores a Android Oreo, usa java.util.Date
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date fecha = null;
            try {
                fecha = formatoEntrada.parse(fechaString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat formatoSalida = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("es", "ES"));
            fechaAmigable = formatoSalida.format(fecha);
        }
        holder.actvDatosPersonales.setText(nombreApellido);
        holder.actvOrigen.setText(origen);
        holder.actvDestino.setText(destino);
        holder.actvHora.setText(hora);
        holder.actvFecha.setText(fechaAmigable);
    }

    @Override
    public int getItemCount() {
        return pasajesList.size();
    }
}
