package com.edwinacubillos.agendasqlite;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainDActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText eID, eNombre, eCorreo, eTelefono;
    String idT,nombre, correo, telefono;
    Contacto contacto;
    int idC=0;

    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_d);

        eID = (EditText) findViewById(R.id.eId);
        eNombre = (EditText) findViewById(R.id.eNombre);
        eCorreo = (EditText) findViewById(R.id.eCorreo);
        eTelefono = (EditText) findViewById(R.id.eTelefono);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();

        idT = eID.getText().toString();
        nombre = eNombre.getText().toString();
        correo = eCorreo.getText().toString();
        telefono = eTelefono.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        switch(id){
            case R.id.bGuardar:
                contacto = new Contacto(String.valueOf(idC), nombre, telefono, correo);
                myRef = database.getReference("contactos").child(String.valueOf(idC));
                myRef.setValue(contacto);
                idC++;

            //    addContact(); php y SQL
                limpiar();
                break;
            case R.id.bBuscar:
                myRef = database.getReference("contactos");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(String.valueOf(idT)).exists()){
                            contacto = dataSnapshot.child(String.valueOf(idT)).getValue(Contacto.class);
                            eNombre.setText(contacto.getNombre());
                            eCorreo.setText(contacto.getCorreo());
                            eTelefono.setText(contacto.getTelefono());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            //    showContact(); php y SQL
                break;
            case R.id.bModificar:
                myRef = database.getReference("contactos").child(idT);

                Map<String, Object> nuevoNombre = new HashMap<>();
                nuevoNombre.put("nombre",nombre);
                myRef.updateChildren(nuevoNombre);

                Map<String, Object> nuevoTelefono = new HashMap<>();
                nuevoTelefono.put("telefono",telefono);
                myRef.updateChildren(nuevoTelefono);

                Map<String, Object> nuevoCorreo = new HashMap<>();
                nuevoCorreo.put("correo",correo);
                myRef.updateChildren(nuevoCorreo);

            //    updateContact(); php y SQL
                limpiar();
                break;
            case R.id.bEliminar:
                myRef = database.getReference("contactos").child(idT);
                myRef.removeValue();

            //    deleteContact();  php y SQL
                limpiar();
                break;
        }
    }

    private void limpiar() {
        eNombre.setText("");
        eCorreo.setText("");
        eTelefono.setText("");
    }

    private void showContact() {
        class ShowContact extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainDActivity.this,"Show...","Wait...",false,false);
            }

            @Override
            protected String doInBackground(Void... v) {
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequestParam(Config.URL_GET_CONTACT, nombre);
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showData(s);
                Toast.makeText(MainDActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
        ShowContact ae = new ShowContact();
        ae.execute();
    }

    private void showData(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject c = result.getJSONObject(0);
            String telefonoJ = c.getString("telefono");
            String correo = c.getString("correo");
            eTelefono.setText(telefonoJ);
            eCorreo.setText(correo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void deleteContact() {
        class DeleteContact extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainDActivity.this,"Delete...","Wait...",false,false);
            }

            @Override
            protected String doInBackground(Void... v) {
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequestParam(Config.URL_DELETE, nombre);
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(MainDActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
        DeleteContact ae = new DeleteContact();
        ae.execute();
    }

    private void updateContact() {
        class UpdateContact extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainDActivity.this,"Updating...","Wait...",false,false);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("nombre",nombre);
                params.put("telefono",telefono);
                params.put("correo",correo);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_UPDATE, params);
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(MainDActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
        UpdateContact ae = new UpdateContact();
        ae.execute();
    }

    private void addContact() {
        class AddContact extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainDActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("nombre",nombre);
                params.put("telefono",telefono);
                params.put("correo",correo);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD, params);
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(MainDActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
        AddContact ae = new AddContact();
        ae.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_d, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
