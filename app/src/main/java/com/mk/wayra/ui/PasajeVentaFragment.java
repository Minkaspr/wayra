package com.mk.wayra.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mk.wayra.R;
import com.mk.wayra.model.Provincia;
import com.mk.wayra.ui.adapter.ProvinciaAdapter;
import com.mk.wayra.util.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PasajeVentaFragment extends Fragment {

    private AutoCompleteTextView actvOrigen;
    private AutoCompleteTextView actvDestino;
    private TextInputLayout tilDestino;
    private TextInputLayout tilPrimNombre;
    private TextInputLayout tilSegNombre;
    private TextInputLayout tilApePaterno;
    private TextInputLayout tilApeMaterno;
    private TextInputLayout tilNumIdentidad;
    private TextInputLayout tilTelefono;
    private TextInputEditText tietPrecio, tietPrimNombre, tietSegNombre, tietApePaterno, tietApeMaterno, tietNumIdentidad, tietTelefono;
    private DatabaseHelper dbHelper;
    private Button btnConfirmar;
    private int idOrigen = -1;
    private int idDestino = -1;
    String origen,destino;
    String fechaActualBD, horaActualBD, nomFechaHoraFichero;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputLayout tilFecha = view.findViewById(R.id.tilFecha);
        TextInputLayout tilHora = view.findViewById(R.id.tilHora);
        Calendar calendar = Calendar.getInstance();
        // Momento actual
        Date currentTime = calendar.getTime();

        SimpleDateFormat dateFormatDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormatDB = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat nomFechaHora = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());

        // Datos para la BD
        fechaActualBD = dateFormatDB.format(currentTime);
        horaActualBD = timeFormatDB.format(currentTime);
        nomFechaHoraFichero = nomFechaHora.format(currentTime);

        SimpleDateFormat dateFormatFriendly = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        SimpleDateFormat timeFormatFriendly = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String fechaActualFriendly = dateFormatFriendly.format(currentTime);
        String horaActualFriendly = timeFormatFriendly.format(currentTime);

        tilFecha.setHintAnimationEnabled(false);
        tilHora.setHintAnimationEnabled(false);
        if (tilFecha.getEditText() != null) {
            tilFecha.getEditText().setText(fechaActualFriendly);
        }
        if (tilHora.getEditText() != null) {
            tilHora.getEditText().setText(horaActualFriendly);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pasaje_venta, container, false);

        tilPrimNombre = view.findViewById(R.id.tilPrimNombre);
        tietPrimNombre = view.findViewById(R.id.tietPrimNombre);
        observadorPalabra(tietPrimNombre, tilPrimNombre);

        tilSegNombre = view.findViewById(R.id.tilSegNombre);
        tietSegNombre = view.findViewById(R.id.tietSegNombre);
        observadorPalabras(tietSegNombre, tilSegNombre, true);

        tilApePaterno = view.findViewById(R.id.tilApePaterno);
        tietApePaterno = view.findViewById(R.id.tietApePaterno);
        observadorPalabras(tietApePaterno, tilApePaterno, false);

        tilApeMaterno = view.findViewById(R.id.tilApeMaterno);
        tietApeMaterno = view.findViewById(R.id.tietApeMaterno);
        observadorPalabras(tietApeMaterno, tilApeMaterno, false);

        tietNumIdentidad = view.findViewById(R.id.tietNumIdentidad);
        tilNumIdentidad = view.findViewById(R.id.tilNumIdentidad);
        observadorNumeros(tietNumIdentidad, tilNumIdentidad, false);

        tietTelefono = view.findViewById(R.id.tietTelefono);
        tilTelefono = view.findViewById(R.id.tilTelefono);
        observadorNumeros(tietTelefono, tilTelefono, true);

        actvOrigen = view.findViewById(R.id.actvOrigen);
        actvDestino = view.findViewById(R.id.actvDestino);
        tilDestino = view.findViewById(R.id.tilDestino);
        tietPrecio = view.findViewById(R.id.tietPrecio);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);

        configurarAdaptadores();

        actvOrigen.setOnItemClickListener((parent, v, position, id) -> {
            Provincia provinciaSeleccionada = (Provincia) parent.getItemAtPosition(position);
            idOrigen = provinciaSeleccionada.getId();
            origen = provinciaSeleccionada.getNombre();
            actualizarPrecio();
            btnConfirmar.setEnabled(validarCampos());
        });

        actvDestino.setOnItemClickListener((parent, v, position, id) -> {
            Provincia provinciaSeleccionada = (Provincia) parent.getItemAtPosition(position);
            idDestino = provinciaSeleccionada.getId();
            destino = provinciaSeleccionada.getNombre();
            actualizarPrecio();
            btnConfirmar.setEnabled(validarCampos());
        });

        btnConfirmar.setOnClickListener(v -> {
            try {
                guardarDatos();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            /*FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.getSupportFragmentManager().popBackStack();
            }*/
            enviarDatos();
        });

        return view;
    }

    private void observadorPalabra(TextInputEditText tiet, TextInputLayout til) {
        tiet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarPalabra(userInput, til);
            }
            @Override
            public void afterTextChanged(Editable userInput) {
                btnConfirmar.setEnabled(validarCampos());
            }
        });
        tiet.setOnFocusChangeListener((v, hasFocus) -> til.setCounterEnabled(hasFocus));
    }

    private void observadorPalabras(TextInputEditText tiet, TextInputLayout til, boolean esOpcional) {
        tiet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarPalabras(tiet, til,esOpcional);
            }
            @Override
            public void afterTextChanged(Editable userInput) {
                btnConfirmar.setEnabled(validarCampos());
            }
        });
        tiet.setOnFocusChangeListener((v, hasFocus) -> til.setCounterEnabled(hasFocus));
    }

    private void observadorNumeros(TextInputEditText tiet, TextInputLayout til, boolean esOpcional) {
        tiet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                if (esOpcional) {
                    validarNumeros(userInput, til,7,9,true);
                } else {
                    validarNumeros(userInput, til,8,12,false);
                }
            }
            @Override
            public void afterTextChanged(Editable userInput) {
                btnConfirmar.setEnabled(validarCampos());
            }
        });
        tiet.setOnFocusChangeListener((v, hasFocus) -> til.setCounterEnabled(hasFocus));
    }

    private void validarPalabra(CharSequence entradaUsuario, TextInputLayout tilCampo) {
        String texto = entradaUsuario.toString();
        boolean hayError;
        if (texto.isEmpty()) {
            tilCampo.setError("Obligatorio");
            hayError = true;
        } else if (texto.contains(" ") && !texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$")) {
            tilCampo.setError("Espacio y carácter no permitido");
            hayError = true;
        } else if (texto.contains(" ")) {
            tilCampo.setError("Espacio no permitido");
            hayError = true;
        } else if (!texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+$")) {
            tilCampo.setError("Carácter no permitido");
            hayError = true;
        } else if (texto.length() > 32) {
            tilCampo.setError("La entrada es demasiado larga");
            hayError = true;
        } else {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
            hayError = false;
        }
        tilCampo.setCounterEnabled(!hayError);
    }

    private void validarPalabras(TextInputEditText tietCampo, TextInputLayout tilCampo, boolean esOpcional) {
        CharSequence entradaUsuario = tietCampo.getText();
        boolean hayError;

        if (entradaUsuario == null || entradaUsuario.toString().isEmpty()) {
            if (!esOpcional) {
                tilCampo.setError("Obligatorio");
                hayError = true;
            } else {
                tilCampo.setError(null);
                tilCampo.setErrorEnabled(false);
                hayError = false;
            }
        } else {
            String texto = entradaUsuario.toString();
            if (texto.startsWith(" ") || !texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+( [a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+)*$")) {
                tilCampo.setError("Entrada inválida");
                hayError = true;
            } else if (texto.length() >= 36) {
                tilCampo.setError("La entrada es demasiado larga");
                hayError = true;
            } else {
                tilCampo.setError(null);
                tilCampo.setErrorEnabled(false);
                hayError = false;
            }
        }
        tilCampo.setCounterEnabled(!hayError);
    }

    private void validarNumeros(CharSequence entradaUsuario, TextInputLayout tilCampo, int longitudMinima, int longitudMaxima, boolean esOpcional) {
        boolean hayError;

        if (entradaUsuario == null || entradaUsuario.toString().isEmpty()) {
            if (!esOpcional) {
                tilCampo.setError("Obligatorio");
                hayError = true;
            } else {
                tilCampo.setError(null);
                tilCampo.setErrorEnabled(false);
                hayError = false;
            }
        } else {
            String texto = entradaUsuario.toString();
            if (!texto.matches("\\d+") || texto.length() < longitudMinima){
                tilCampo.setError("Minimo " + longitudMinima + " dígitos");
                hayError = true;
            } else if (!texto.matches("\\d+") || texto.length() > longitudMaxima) {
                tilCampo.setError("Entrada inválida");
                hayError = true;
            } else {
                tilCampo.setError(null);
                tilCampo.setErrorEnabled(false);
                hayError = false;
            }
        }
        tilCampo.setCounterEnabled(!hayError);
    }

    private void configurarAdaptadores() {
        List<Provincia> provincias = dbHelper.obtenerProvincias();
        ProvinciaAdapter adapter = new ProvinciaAdapter(requireContext(), provincias);
        actvOrigen.setAdapter(adapter);
        actvDestino.setAdapter(adapter);
    }

    private void actualizarPrecio() {
        if (idOrigen != -1 && idDestino != -1) {
            if (idOrigen == idDestino) {
                tietPrecio.setText(getString(R.string.precio_default));
                tilDestino.setError("Destino no permitido");
            } else {
                double precio;
                if (idOrigen < idDestino) {
                    precio = dbHelper.obtenerPrecioViaje(idOrigen, idDestino, "tarifas_ida");
                } else {
                    precio = dbHelper.obtenerPrecioViaje(idOrigen, idDestino, "tarifas_vuelta");
                }
                tietPrecio.setText(String.valueOf(precio));
                tilDestino.setError(null);
                tilDestino.setErrorEnabled(false);
            }
        }
    }

    private boolean validarCampos() {
        return tietPrimNombre.getText() != null && !tietPrimNombre.getText().toString().isEmpty() && tilPrimNombre.getError() == null &&
                (tietSegNombre.getText() != null && !tietSegNombre.getText().toString().isEmpty() && tilSegNombre.getError() == null || tietSegNombre.getText() == null || tietSegNombre.getText().toString().isEmpty()) &&
                tietApePaterno.getText() != null && !tietApePaterno.getText().toString().isEmpty() && tilApePaterno.getError() == null &&
                tietApeMaterno.getText() != null && !tietApeMaterno.getText().toString().isEmpty() && tilApeMaterno.getError() == null &&
                tietNumIdentidad.getText() != null && !tietNumIdentidad.getText().toString().isEmpty() && tilNumIdentidad.getError() == null &&
                (tietTelefono.getText() != null && !tietTelefono.getText().toString().isEmpty() && tilTelefono.getError() == null || tietTelefono.getText() == null || tietTelefono.getText().toString().isEmpty()) &&
                idOrigen != -1 &&
                idDestino != -1 &&
                idOrigen != idDestino;
    }

    private void guardarDatos() throws JSONException {
        JSONObject datos = new JSONObject();
        List<String> camposVacios = new ArrayList<>();
        try {
            datos.put("primerNom", obtenerTexto(tietPrimNombre, "primerNom", camposVacios, false));
            datos.put("segundoNom", obtenerTexto(tietSegNombre, "segundoNom", camposVacios, true));
            datos.put("apePaterno", obtenerTexto(tietApePaterno, "apePaterno", camposVacios,false));
            datos.put("apeMaterno", obtenerTexto(tietApeMaterno, "apeMaterno", camposVacios,false));
            datos.put("numIdentidad", obtenerTexto(tietNumIdentidad, "numIdentidad", camposVacios,false));
            datos.put("telefono", obtenerTexto(tietTelefono, "telefono", camposVacios, true));
            datos.put("origen", origen);
            datos.put("destino", destino);
            String precioStr = obtenerTexto(tietPrecio, "precio", camposVacios,false);
            double precio = precioStr != null ? Double.parseDouble(precioStr) : 0.0;
            datos.put("precio", precio);
            datos.put("fecha", fechaActualBD);
            datos.put("hora", horaActualBD);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!camposVacios.isEmpty()) {
            System.out.println("Los siguientes campos están vacíos: " + String.join(", ", camposVacios));
            return;
        }

        String json = datos.toString(4);
        String nombreArchivo = "pj_" +nomFechaHoraFichero + "_" + obtenerTexto(tietNumIdentidad, "numIdentidad", camposVacios,false) + ".json";

        // Obtener una referencia a la carpeta "wayra" (y crearla si no existe)
        if(getActivity()!=null){
            File carpeta = getActivity().getDir("wayra", Context.MODE_PRIVATE);
            File archivo = new File(carpeta, nombreArchivo);

            try {
                FileOutputStream fos = new FileOutputStream(archivo);
                fos.write(json.getBytes());
                fos.close();
                Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String obtenerTexto(TextInputEditText tiet, String nombreCampo, List<String> camposVacios, boolean esOpcional) {
        if (tiet.getText() != null && !tiet.getText().toString().isEmpty()) {
            return tiet.getText().toString();
        } else {
            if (!esOpcional) {
                camposVacios.add(nombreCampo);
            }
            return null;
        }
    }

    private void enviarDatos() {
        // Captura los datos
        String primerNom = tietPrimNombre.getText().toString();
        String apePaterno = tietApePaterno.getText().toString();
        String numIdentidad = tietNumIdentidad.getText().toString();
        String origen = this.origen; // Asegúrate de que 'origen' esté inicializado
        String destino = this.destino; // Asegúrate de que 'destino' esté inicializado
        String precioStr = tietPrecio.getText().toString();
        double precio = precioStr.isEmpty() ? 0.0 : Double.parseDouble(precioStr);
        String fecha = fechaActualBD; // Asegúrate de que 'fechaActualBD' esté inicializado
        String hora = horaActualBD; // Asegúrate de que 'horaActualBD' esté inicializado

        // Crea un Bundle y coloca los datos en él
        Bundle bundle = new Bundle();
        bundle.putString("primerNom", primerNom);
        bundle.putString("apePaterno", apePaterno);
        bundle.putString("numIdentidad", numIdentidad);
        bundle.putString("origen", origen);
        bundle.putString("destino", destino);
        bundle.putDouble("precio", precio);
        bundle.putString("fecha", fecha);
        bundle.putString("hora", hora);

        // Crea una instancia del otro fragmento
        ConstanciaFragment constanciaFragment = new ConstanciaFragment();

        // Coloca el Bundle en los argumentos del fragmento
        constanciaFragment.setArguments(bundle);
        replaceFragment(constanciaFragment);
    }


    private void replaceFragment(Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.replaceFragment(fragment, true);
        }
    }
}