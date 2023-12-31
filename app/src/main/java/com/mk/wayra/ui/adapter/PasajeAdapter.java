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

public class PasajeAdapter extends RecyclerView.Adapter<PasajeAdapter.PasajeViewHolder> {
    private Context context;
    private List<Pasaje> pasajes;

    public PasajeAdapter(Context context, List<Pasaje> pasajes) {
        this.context = context;
        this.pasajes = pasajes;
    }

    public void setPasajes(List<Pasaje> pasajes) {
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
            String fechaString = String.valueOf(pasaje.getFecha());
            String fechaAmigable;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Para Android Oreo (8.0) o superior, usa java.time.LocalDate
                DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fecha = LocalDate.parse(fechaString, formatoEntrada);

                DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd MMM. yyyy", new Locale("es", "ES"));
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

                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                fechaAmigable = formatoSalida.format(fecha);
            }
            actvFecha.setText(fechaAmigable);
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