package com.example.kp.brutarievascau;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability ga = GoogleApiAvailability.getInstance();
        ConnectionResult con = new ConnectionResult(ga.isGooglePlayServicesAvailable(getBaseContext()));
        if(con.isSuccess()) {
            Toast.makeText(getBaseContext(), "pot acces play", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void trimiteComandaNoua(View view) {
        Intent intent = new Intent(this, ListaMagazine.class);
        startActivity(intent);

    }

    public void addclientnou(View view) {
        Intent intent = new Intent(this,ClientNou.class);
        startActivity(intent);
    }

    public void listallclients(View view) {
        Intent intent = new Intent(this,ListaMagazine.class);
        startActivity(intent);
    }

    public void listaProduse(View view) {
        Intent intent = new Intent(this,Produse.class);
        startActivity(intent);
    }

    public void iesireprogram(View view){
         finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
