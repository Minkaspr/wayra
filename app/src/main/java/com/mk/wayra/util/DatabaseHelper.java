package com.mk.wayra.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mk.wayra.model.Provincia;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tarifa.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla "provincias"
        String createProvinciasTable = "CREATE TABLE provincias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL" +
                ");";
        db.execSQL(createProvinciasTable);

        // Crear la tabla "tarifas_ida"
        String createTarifasIdaTable = "CREATE TABLE tarifas_ida (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "origen INTEGER NOT NULL," +
                "destino INTEGER NOT NULL," +
                "costo REAL NOT NULL," +
                "FOREIGN KEY (origen) REFERENCES provincias (id)," +
                "FOREIGN KEY (destino) REFERENCES provincias (id)" +
                ");";
        db.execSQL(createTarifasIdaTable);

        // Crear la tabla "tarifas_vuelta"
        String createTarifasVueltaTable = "CREATE TABLE tarifas_vuelta (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "origen INTEGER NOT NULL," +
                "destino INTEGER NOT NULL," +
                "costo REAL NOT NULL," +
                "FOREIGN KEY (origen) REFERENCES provincias (id)," +
                "FOREIGN KEY (destino) REFERENCES provincias (id)" +
                ");";
        db.execSQL(createTarifasVueltaTable);

        // Insertar 5 provincias
        String[] provincias = {"Lima", "Nazca", "Abancay", "Andahuaylas", "Cusco"};
        for (String provincia : provincias) {
            ContentValues values = new ContentValues();
            values.put("nombre", provincia);
            db.insert("provincias", null, values);
        }

        // Insertar tarifas de ida
        String insertTarifasIdaSQL = "INSERT INTO tarifas_ida (origen, destino, costo) VALUES " +
                "(1, 2, 35.00), " +
                "(1, 3, 60.00), " +
                "(1, 4, 95.00), " +
                "(1, 5, 120.00), " +
                "(2, 3, 35.00), " +
                "(2, 4, 60.00), " +
                "(2, 5, 95.00), " +
                "(3, 4, 35.00), " +
                "(3, 5, 60.00), " +
                "(4, 5, 35.00);";
        db.execSQL(insertTarifasIdaSQL);

        // Insertar tarifas de vuelta
        String insertTarifasVueltaSQL = "INSERT INTO tarifas_vuelta (origen, destino, costo) VALUES " +
                "(5, 4, 35.00), " +
                "(5, 3, 60.00), " +
                "(5, 2, 95.00), " +
                "(5, 1, 120.00), " +
                "(4, 3, 35.00), " +
                "(4, 2, 60.00), " +
                "(4, 1, 95.00), " +
                "(3, 2, 35.00), " +
                "(3, 1, 60.00), " +
                "(2, 1, 35.00);";
        db.execSQL(insertTarifasVueltaSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Provincia> obtenerProvincias() {
        List<Provincia> provincias = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT id, nombre FROM provincias";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                provincias.add(new Provincia(nombre, id));
            }
            cursor.close();
        }
        db.close();
        return provincias;
    }

    public double obtenerPrecioViaje(int idOrigen, int idDestino, String tabla) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT costo FROM " + tabla + " WHERE origen = ? AND destino = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idOrigen), String.valueOf(idDestino)});
        if (cursor != null && cursor.moveToFirst()) {
            double costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo"));
            cursor.close();
            return costo;
        }
        return -1;
    }
}
