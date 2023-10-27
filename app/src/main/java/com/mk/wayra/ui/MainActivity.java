package com.mk.wayra.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.mk.wayra.R;
import com.mk.wayra.util.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // La base de datos y las tablas son creadas
        dbHelper = new DatabaseHelper(this);

        replaceFragment(new HomeFragment());

        // Crear un callback para onBackPressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Obtener el FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Si hay algún Fragment en la pila de retroceso
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    // Quitar el Fragment superior de la pila de retroceso
                    fragmentManager.popBackStack();
                } else {
                    // Deshabilitar este callback antes de llamar a finish()
                    setEnabled(false);
                    // Cerrar la actividad
                    finish();
                }
            }
        };
        // Registrar el callback al onBackPressedDispatcher de la actividad
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment);
        fragmentTransaction.commit();
    }
}