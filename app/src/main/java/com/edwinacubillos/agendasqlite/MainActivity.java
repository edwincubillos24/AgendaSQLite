package com.edwinacubillos.agendasqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText eNombre, eCorreo, eTelefono;
    String nombre, correo, telefono;

    ContactosSQLiteHelper contactosSQLiteHelper;
    SQLiteDatabase dbContactos;
    ContentValues dataBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactosSQLiteHelper = new ContactosSQLiteHelper(this,"ContactosDB",null,1);
        dbContactos = contactosSQLiteHelper.getWritableDatabase();

        eNombre = (EditText) findViewById(R.id.eNombre);
        eCorreo = (EditText) findViewById(R.id.eCorreo);
        eTelefono = (EditText) findViewById(R.id.eTelefono);
    }

    public void onClick(View view) {
        int id = view.getId();

        nombre = eNombre.getText().toString();
        correo = eCorreo.getText().toString();
        telefono = eTelefono.getText().toString();

        dataBD = new ContentValues();

        switch(id){
            case R.id.bGuardar:
                //OPCION 1
                dataBD.put("nombre",nombre);
                dataBD.put("correo",correo);
                dataBD.put("telefono",telefono);
                dbContactos.insert("Contactos",null,dataBD);

                //OPCION 2
                dbContactos.execSQL("INSERT INTO Contactos VALUES(null, '"+nombre+"', '"+telefono+"', '"+correo+"')");

                break;
            case R.id.bBuscar:
                Cursor cursor = dbContactos.rawQuery("SELECT * FROM Contactos WHERE nombre='"+nombre+"'",null);

                if (cursor.moveToFirst()){
                    eTelefono.setText(cursor.getString(2));
                    eCorreo.setText(cursor.getString(3));
                }

                break;
            case R.id.bModificar:
                //OPCION 1
                dataBD.put("correo",correo);
                dataBD.put("telefono",telefono);
                dbContactos.update("Contactos",dataBD,"nombre='"+nombre+"'",null);

                //OPCION 2
                dbContactos.execSQL("UPDATE Contactos SET telefono='"+telefono+"', correo='"+correo+"' WHERE nombre='"+nombre+"'");

                break;
            case R.id.bEliminar:
                //OPCION 1
                dbContactos.delete("Contactos","nombre='"+nombre+"'",null);

                //OPCION 2
                dbContactos.execSQL("DELETE FROM Contactos WHERE nombre='"+nombre+"'");

                break;
        }
    }
}
