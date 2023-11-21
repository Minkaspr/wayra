package com.mk.wayra.ui;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.wayra.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConstanciaFragment extends Fragment {
    private TextView tvFecha;
    private TextView tvPasajero;
    private TextView tvNumIdentidad;
    private TextView tvOrigen;
    private TextView tvDestino;
    private TextView tvCosto;
    private String currentDateTime;
    private Bitmap bitmap;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_constancia, container, false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        currentDateTime = dateFormat.format(new Date());

        tvFecha = view.findViewById(R.id.tvFecha);
        tvPasajero = view.findViewById(R.id.tvPasajero);
        tvNumIdentidad = view.findViewById(R.id.tvNumIdentidad);
        tvOrigen = view.findViewById(R.id.tvOrigen);
        tvDestino = view.findViewById(R.id.tvDestino);
        tvCosto = view.findViewById(R.id.tvCosto);
        Button btnDescargar = view.findViewById(R.id.btnDescargar);
        Button btnCompartir = view.findViewById(R.id.btnCompartir);
        Button btnVolverAtras = view.findViewById(R.id.btnVolverAtras);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String primerNom = bundle.getString("primerNom");
            String apePaterno = bundle.getString("apePaterno");
            String numIdentidad = bundle.getString("numIdentidad");
            String origen = bundle.getString("origen");
            String destino = bundle.getString("destino");
            double precio = bundle.getDouble("precio");
            String fecha = bundle.getString("fecha");
            String hora = bundle.getString("hora");

            SimpleDateFormat dateFormatDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //SimpleDateFormat dateFormatNew = new SimpleDateFormat("dd MMM., hh:mm a", Locale.getDefault());
            SimpleDateFormat dateFormatNew = new SimpleDateFormat("dd MMM, hh:mm a", new Locale("es", "ES"));
            Date date = null;
            try {
                date = dateFormatDB.parse(fecha + " " + hora);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String fechaHora = dateFormatNew.format(date);
            fechaHora = fechaHora.toLowerCase();
            tvFecha.setText(fechaHora);
            tvPasajero.setText(primerNom + " " + apePaterno);
            tvNumIdentidad.setText(numIdentidad);
            tvOrigen.setText(origen);
            tvDestino.setText(destino);
            tvCosto.setText(String.valueOf(precio));
        }

        btnVolverAtras.setOnClickListener(v->{
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnDescargar.setOnClickListener(v -> {
            String fechaHora = tvFecha.getText().toString();
            String nombrePasajero = tvPasajero.getText().toString();
            String numIdentidad = tvNumIdentidad.getText().toString();
            String origen = tvOrigen.getText().toString();
            String destino = tvDestino.getText().toString();
            String costo = tvCosto.getText().toString();

            bitmap = crearImagenConTexto(fechaHora, nombrePasajero, numIdentidad, origen, destino, costo);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permiso no concedido, solicitarlo.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Permiso ya concedido, guardar la imagen.
                guardarImagen(bitmap);
            }
        });

        btnCompartir.setOnClickListener(v->{
            String fechaHora = tvFecha.getText().toString();
            String nombrePasajero = tvPasajero.getText().toString();
            String numIdentidad = tvNumIdentidad.getText().toString();
            String origen = tvOrigen.getText().toString();
            String destino = tvDestino.getText().toString();
            String costo = tvCosto.getText().toString();

            Bitmap bitmap = crearImagenConTexto(fechaHora, nombrePasajero, numIdentidad, origen, destino, costo);

            // Guarda la imagen en el directorio de archivos externos de la aplicación
            try {
                File appDir = getActivity().getExternalFilesDir(null);
                String fileName = "constancia_temp_" + currentDateTime + ".png";
                File imageFile = new File(appDir, fileName);

                if (imageFile.exists()) {
                    imageFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                // Intent para compartir la imagen
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, "Compartir imagen"));

                // Programa la eliminación de la imagen para que ocurra después de 1 minuto
                new Handler().postDelayed(() -> {
                    if (imageFile.exists()) {
                        if (imageFile.delete()) {
                            System.out.println("Archivo eliminado");
                        } else {
                            System.out.println("El archivo no pudo ser eliminado");
                        }
                    }
                }, 60000);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, guardar la imagen.
                    guardarImagen(bitmap);
                } else {
                    // Permiso denegado, mostrar una explicación al usuario.
                    Toast.makeText(getActivity(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private Bitmap crearImagenConTexto(String fechaHora, String nombrePasajero, String numIdentidad, String origen, String destino, String costo) {

        int ancho = 400;
        int alto = 500;
        int relleno = 16;

        // Crea un bitmap vacío y un canvas para dibujar en el bitmap
        Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Cambia el fondo a blanco
        canvas.drawColor(Color.WHITE);

        // Crea un objeto Paint para dibujar el texto
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        paint.setAntiAlias(true);

        // Crea un objeto Paint para dibujar el texto en negrita
        Paint paintNegrita = new Paint();
        paintNegrita.setColor(Color.BLACK);
        paintNegrita.setTextSize(16);
        paintNegrita.setAntiAlias(true);
        paintNegrita.setTypeface(Typeface.DEFAULT_BOLD);

        // Crea un objeto Paint para dibujar el texto grande
        Paint paintGrande = new Paint();
        paintGrande.setColor(Color.BLACK);
        paintGrande.setTextSize(20);
        paintGrande.setAntiAlias(true);
        paintGrande.setTypeface(Typeface.DEFAULT_BOLD);

        // Crea un objeto Paint para dibujar el texto pequeño
        Paint paintPequeno = new Paint();
        paintPequeno.setColor(Color.BLACK);
        paintPequeno.setTextSize(14);
        paintPequeno.setAntiAlias(true);

        // Calcula la altura de una línea de texto normal-grande-pequeño
        float alturaTexto = paint.getTextSize();
        float alturaTextoGrande = paintGrande.getTextSize();
        float alturaTextoPequeno = paintPequeno.getTextSize();

        // Calcula el espacio normal y grande entre las filas
        float espacio = alturaTexto + 8;
        float espacioGrande = alturaTexto + 16;

        // Calcula la altura total de las filas desde la fila 2 hasta la fila 9
        float alturaTotal = alturaTextoGrande + 7 * espacio + espacioGrande + 2 * alturaTextoPequeno;

        // Calcula la posición y inicial para centrar verticalmente las filas
        float yInicial = (alto - alturaTotal) / 2;

        // Define las coordenadas iniciales (x, y)
        float x = relleno;
        float y = paint.getTextSize() + relleno;

        // Fila 1
        canvas.drawText("Wayra", x, y, paint);
        float xFechaHora = ancho - paint.measureText(fechaHora) - relleno;
        canvas.drawText(fechaHora, xFechaHora, y, paint);

        // Espacio
        //y += paint.getTextSize() + 32;
        y = yInicial;

        // Fila 2
        String pasajeVendido = "Pasaje Vendido";
        float xPasajeVendido = (ancho - paintGrande.measureText(pasajeVendido)) / 2;
        canvas.drawText(pasajeVendido, xPasajeVendido, y, paintGrande);

        // Espacio
        y += paint.getTextSize() + 32;

        // Define la separación entre los textos
        float separacionTexto = 8;

        // Fila 3
        String pasajeroTexto = "Pasajero:";
        String nomPasajero = nombrePasajero;
        if (nombrePasajero.length() > 18) {
            nomPasajero = nombrePasajero.substring(0, 18) + "...";
        }
        float anchoPasajero = paintNegrita.measureText(pasajeroTexto);
        float xPasajeroTexto = ((float)ancho / 2) - (separacionTexto / 2) - anchoPasajero;
        float xNombrePasajero = ((float)ancho / 2) + (separacionTexto / 2);
        canvas.drawText(pasajeroTexto, xPasajeroTexto, y, paintNegrita);
        canvas.drawText(nomPasajero, xNombrePasajero, y, paint);

        // Espacio
        y += paint.getTextSize() + 8;

        // Fila 4
        String identidadTexto = "Num. de Identidad:";
        float anchoIdentidad = paintNegrita.measureText(identidadTexto);
        float xIdentidadTexto = ((float)ancho  / 2) - (separacionTexto / 2) - anchoIdentidad;
        float xNumIdentidad = ((float)ancho  / 2) + (separacionTexto / 2);
        canvas.drawText(identidadTexto, xIdentidadTexto, y, paintNegrita);
        canvas.drawText(numIdentidad, xNumIdentidad, y, paint);

        // Espacio
        y += paint.getTextSize() + 8;

        // Fila 5
        String origenTexto = "Origen:";
        float anchoOrigenTexto = paintNegrita.measureText(origenTexto);
        float xOrigenTexto = ((float)ancho  / 2) - (separacionTexto / 2) - anchoOrigenTexto;
        float xOrigen = ((float)ancho  / 2) + (separacionTexto / 2);
        canvas.drawText(origenTexto, xOrigenTexto, y, paintNegrita);
        canvas.drawText(origen, xOrigen, y, paint);

        // Espacio
        y += paint.getTextSize() + 8;

        // Fila 6
        String destinoTexto = "Destino:";
        float anchoDestinoTexto = paintNegrita.measureText(destinoTexto);
        float xDestinoTexto = ((float)ancho  / 2) - (separacionTexto / 2) - anchoDestinoTexto;
        float xDestino = ((float)ancho  / 2) + (separacionTexto / 2);
        canvas.drawText(destinoTexto, xDestinoTexto, y, paintNegrita);
        canvas.drawText(destino, xDestino, y, paint);

        // Espacio
        y += paint.getTextSize() + 8;

        // Fila 7
        String costoTexto = "Costo:";
        float anchoCostoTexto = paintNegrita.measureText(costoTexto);
        float xCostoTexto = ((float)ancho  / 2) - (separacionTexto / 2) - anchoCostoTexto;
        float xCosto = ((float)ancho  / 2) + (separacionTexto / 2);
        canvas.drawText(costoTexto, xCostoTexto, y, paintNegrita);
        canvas.drawText(costo, xCosto, y, paint);

        // Espacio
        y += paint.getTextSize() + 16;

        // Fila 8
        String mensajeP1 = "¡Que el camino te sonría!";
        float xMensajeP1 = (ancho - paintPequeno .measureText(mensajeP1)) / 2;
        canvas.drawText(mensajeP1, xMensajeP1, y, paintPequeno );

        // Espacio
        y += paint.getTextSize() + 8;

        // Fila 9
        String mensajeP2 = "Buen viaje";
        float xMensajeP2 = (ancho - paintPequeno .measureText(mensajeP2)) / 2;
        canvas.drawText(mensajeP2, xMensajeP2, y, paintPequeno );

        return bitmap;
    }

    // Método para guardar la imagen en un directorio público
    private void guardarImagen(Bitmap bitmap) {
        // Guarda la imagen en el directorio de imágenes públicas de la aplicación
        try {
            File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Wayra");
            if (!appDir.exists()) {
                if (!appDir.mkdirs()) {
                    Log.d("Wayra", "No se pudo crear el directorio");
                    return;
                }
            }
            String fileName = "constancia_" + currentDateTime + ".png";
            File imageFile = new File(appDir, fileName);

            if (imageFile.exists()) {
                Toast.makeText(getActivity(), "El archivo ya fue descargado", Toast.LENGTH_SHORT).show();
            } else {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toast.makeText(getActivity(), "Imagen guardada en " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                // Escanea el archivo para que aparezca en la galería
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri fileUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(fileUri);
                getActivity().sendBroadcast(mediaScanIntent);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
