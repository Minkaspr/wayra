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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PasajesVentasFragment extends Fragment {

    private AutoCompleteTextView actvOrigen;
    private AutoCompleteTextView actvDestino;
    private TextInputLayout tilDestino, tilPrimNombre, tilSegNombre, tilApePaterno, tilApeMaterno, tilNumIdentidad, tilTelefono, tilFecha, tilHora;
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
        tilFecha = view.findViewById(R.id.tilFecha);
        tilHora = view.findViewById(R.id.tilHora);
        Calendar calendar = Calendar.getInstance();
        // Momento actual
        Date currentTime = calendar.getTime();

        SimpleDateFormat dateFormatDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormatDB = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat nomFechaHora = new SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault());

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
        View view = inflater.inflate(R.layout.fragment_pasajes_ventas, container, false);

        tilPrimNombre = view.findViewById(R.id.tilPrimNombre);
        tietPrimNombre = view.findViewById(R.id.tietPrimNombre);
        tilSegNombre = view.findViewById(R.id.tilSegNombre);
        tietSegNombre = view.findViewById(R.id.tietSegNombre);
        tilApePaterno = view.findViewById(R.id.tilApePaterno);
        tietApePaterno = view.findViewById(R.id.tietApePaterno);
        tilApeMaterno = view.findViewById(R.id.tilApeMaterno);
        tietApeMaterno = view.findViewById(R.id.tietApeMaterno);
        actvOrigen = view.findViewById(R.id.actvOrigen);
        actvDestino = view.findViewById(R.id.actvDestino);
        tilDestino = view.findViewById(R.id.tilDestino);
        tietPrecio = view.findViewById(R.id.tietPrecio);
        tilNumIdentidad = view.findViewById(R.id.tilNumIdentidad);
        tietNumIdentidad = view.findViewById(R.id.tietNumIdentidad);
        tilTelefono = view.findViewById(R.id.tilTelefono);
        tietTelefono = view.findViewById(R.id.tietTelefono);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);

        tietPrimNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarPalabra(userInput, tilPrimNombre);
                btnConfirmar.setEnabled(validarCampos());
            }
            @Override
            public void afterTextChanged(Editable userInput) {}
        });
        tietPrimNombre.setOnFocusChangeListener((v, hasFocus) -> tilPrimNombre.setCounterEnabled(hasFocus));

        tietSegNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarPalabrasOpcional(tietSegNombre, tilSegNombre);
                btnConfirmar.setEnabled(validarCampos());
            }
            @Override
            public void afterTextChanged(Editable userInput) {}
        });
        tietSegNombre.setOnFocusChangeListener((v, hasFocus) -> tilSegNombre.setCounterEnabled(hasFocus));

        tietApePaterno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarPalabras(tietApePaterno, tilApePaterno);
                btnConfirmar.setEnabled(validarCampos());
            }
            @Override
            public void afterTextChanged(Editable userInput) {}
        });
        tietApePaterno.setOnFocusChangeListener((v, hasFocus) -> tilApePaterno.setCounterEnabled(hasFocus));

        tietApeMaterno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarPalabras(tietApeMaterno, tilApeMaterno);
                btnConfirmar.setEnabled(validarCampos());
            }
            @Override
            public void afterTextChanged(Editable userInput) {}
        });
        tietApeMaterno.setOnFocusChangeListener((v, hasFocus) -> tilApeMaterno.setCounterEnabled(hasFocus));

        tietNumIdentidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarNumeros(userInput, tilNumIdentidad, 12);
                btnConfirmar.setEnabled(validarCampos());
            }

            @Override
            public void afterTextChanged(Editable userInput) {}
        });
        tietNumIdentidad.setOnFocusChangeListener((v, hasFocus) -> tilNumIdentidad.setCounterEnabled(hasFocus));

        tietTelefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence userInput, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                validarNumerosOpcional(userInput, tilTelefono, 7,10);
                btnConfirmar.setEnabled(validarCampos());
            }

            @Override
            public void afterTextChanged(Editable userInput) {}
        });
        tietTelefono.setOnFocusChangeListener((v, hasFocus) -> tilTelefono.setCounterEnabled(hasFocus));

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
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void validarPalabra(CharSequence entradaUsuario, TextInputLayout tilCampo) {
        String texto = entradaUsuario.toString();
        if (texto.isEmpty()) {
            tilCampo.setError("Campo obligatorio");
        } else if (texto.contains(" ") && !texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$")) {
            tilCampo.setError("Espacio y carácter no permitido");
        } else if (texto.contains(" ")) {
            tilCampo.setError("Espacio no permitido");
        } else if (!texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+$")) {
            tilCampo.setError("Carácter no permitido");
        } else if (texto.length() >= 32) {
            tilCampo.setError("La entrada es demasiado larga");
        } else {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        }
    }

    private void validarPalabras(TextInputEditText tietCampo, TextInputLayout tilCampo) {
        CharSequence entradaUsuario = tietCampo.getText();
        if (entradaUsuario == null) {
            tilCampo.setError("Campo obligatorio");
            return;
        }
        String texto = entradaUsuario.toString();
        if (texto.isEmpty()) {
            tilCampo.setError("Campo obligatorio");
        } else if (texto.startsWith(" ") || !texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+( [a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+)*$")) {
            tilCampo.setError("Entrada inválida");
        } else if (texto.length() >= 36) {
            tilCampo.setError("La entrada es demasiado larga");
        } else {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        }
    }

    private void validarPalabrasOpcional(TextInputEditText tietCampo, TextInputLayout tilCampo) {
        CharSequence entradaUsuario = tietCampo.getText();
        if (entradaUsuario == null) {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
            return;
        }
        String texto = entradaUsuario.toString();
        if (texto.isEmpty()) {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        } else if (texto.startsWith(" ") || !texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+( [a-zA-ZáéíóúÁÉÍÓÚüÜñÑ]+)*$")) {
            tilCampo.setError("Entrada inválida");
        } else if (texto.length() >= 36) {
            tilCampo.setError("La entrada es demasiado larga");
        } else {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        }
    }

    private void validarNumeros(CharSequence entradaUsuario, TextInputLayout tilCampo, int longitudMaxima) {
        if (entradaUsuario == null) {
            tilCampo.setError("Campo obligatorio");
            return;
        }
        String texto = entradaUsuario.toString();
        if (texto.isEmpty()) {
            tilCampo.setError("Campo obligatorio");
        } else if (!texto.matches("\\d+") || texto.length() > longitudMaxima) {
            tilCampo.setError("Entrada inválida");
        } else {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        }
    }

    private void validarNumerosOpcional(CharSequence entradaUsuario, TextInputLayout tilCampo, int longitudMinima, int longitudMaxima) {
        if (entradaUsuario == null) {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
            return;
        }
        String texto = entradaUsuario.toString();
        if (texto.isEmpty()) {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        } else if (!texto.matches("\\d+") || texto.length() < longitudMinima){
            tilCampo.setError("Minimo " + longitudMinima + " dígitos");
        } else if (!texto.matches("\\d+") || texto.length() >= longitudMaxima) {
            tilCampo.setError("Entrada inválida");
        } else {
            tilCampo.setError(null);
            tilCampo.setErrorEnabled(false);
        }
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
        return !tietPrimNombre.getText().toString().isEmpty() && tilPrimNombre.getError() == null &&
                (!tietSegNombre.getText().toString().isEmpty() && tilSegNombre.getError() == null || tietSegNombre.getText().toString().isEmpty()) &&
                !tietApePaterno.getText().toString().isEmpty() && tilApePaterno.getError() == null &&
                !tietApeMaterno.getText().toString().isEmpty() && tilApeMaterno.getError() == null &&
                !tietNumIdentidad.getText().toString().isEmpty() && tilNumIdentidad.getError() == null &&
                (!tietTelefono.getText().toString().isEmpty() && tilTelefono.getError() == null || tietTelefono.getText().toString().isEmpty()) &&
                idOrigen != -1 &&
                idDestino != -1 &&
                idOrigen != idDestino;
    }

    private void guardarDatos() throws JSONException {
        JSONObject datos = new JSONObject();
        try {
            datos.put("primerNom", tietPrimNombre.getText().toString());
            String segundoNom = tietSegNombre.getText() != null ? tietSegNombre.getText().toString() : null;
            if (segundoNom != null && segundoNom.isEmpty()) {segundoNom = null;}
            datos.put("segundoNom", segundoNom);
            datos.put("apePaterno", tietApePaterno.getText().toString());
            datos.put("apeMaterno", tietApeMaterno.getText().toString());
            datos.put("numIdentidad", tietNumIdentidad.getText().toString());
            datos.put("telefono", tietTelefono.getText().toString());
            datos.put("origen", origen);
            datos.put("destino", destino);
            datos.put("precio", Double.parseDouble(tietPrecio.getText().toString()));
            datos.put("fecha", fechaActualBD);
            datos.put("hora", horaActualBD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = datos.toString(4);
        String nombreArchivo = "pasaje_" + nomFechaHoraFichero + ".json";

        // Obtener una referencia a la carpeta "wayra" (y crearla si no existe)
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