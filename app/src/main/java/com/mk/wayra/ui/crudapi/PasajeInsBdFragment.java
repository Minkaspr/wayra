package com.mk.wayra.ui.crudapi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mk.wayra.R;
import com.mk.wayra.controller.PasajeApiClient;
import com.mk.wayra.controller.PasajeApiService;
import com.mk.wayra.model.Pasaje;
import com.mk.wayra.model.Provincia;
import com.mk.wayra.ui.adapter.ProvinciaAdapter;
import com.mk.wayra.util.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasajeInsBdFragment extends Fragment {
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
    String fechaActualBD, horaActualBD;
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

        // Datos para la BD
        fechaActualBD = dateFormatDB.format(currentTime);
        horaActualBD = timeFormatDB.format(currentTime);

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
        View view = inflater.inflate(R.layout.fragment_pasaje_ins_bd, container, false);
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
            createPasaje();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.getSupportFragmentManager().popBackStack();
            }
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

    public void createPasaje() {
        Pasaje nuevoPasaje = new Pasaje();
        nuevoPasaje.setPrimerNom(tietPrimNombre.getText().toString());
        nuevoPasaje.setApePaterno(tietApePaterno.getText().toString());
        nuevoPasaje.setApeMaterno(tietApeMaterno.getText().toString());
        nuevoPasaje.setNumIdentidad(tietNumIdentidad.getText().toString());
        nuevoPasaje.setOrigen(actvOrigen.getText().toString());
        nuevoPasaje.setDestino(actvDestino.getText().toString());
        nuevoPasaje.setFecha(fechaActualBD);
        nuevoPasaje.setHora(horaActualBD);
        nuevoPasaje.setPrecio(Double.parseDouble(tietPrecio.getText().toString()));

        // Verificar si los campos opcionales están vacíos antes de llamar a los métodos set
        if (!tietSegNombre.getText().toString().isEmpty()) {
            nuevoPasaje.setSegundoNom(tietSegNombre.getText().toString());
        }
        if (!tietTelefono.getText().toString().isEmpty()) {
            nuevoPasaje.setTelefono(tietTelefono.getText().toString());
        }

        PasajeApiService apiService = PasajeApiClient.getApiClient().create(PasajeApiService.class);

        Call<Void> call = apiService.createPasaje(nuevoPasaje);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PasajeInsBdFragment", "Pasaje creado con éxito");
                } else {
                    Log.e("ERROR API", "La solicitud no fue exitosa");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("PasajeInsBdFragment", "onFailure() called with: " + t.getMessage(), t);
            }
        });
    }
}