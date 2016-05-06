package com.example.kp.brutarievascau;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmailComanda extends AppCompatActivity  {

    EmailComandaAdapter adapter;
    List<ComenziEmail> lista;
    ComenziEmail comenzi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_comanda);


        Date date = new Date();

        DBhelper dBhelper = new DBhelper(getBaseContext());
        lista = dBhelper.listALlEmailComenzi(date.getTime());
        if(!lista.isEmpty()){
              adapter = new EmailComandaAdapter(getBaseContext(),R.layout.mail_comanda_listview,lista);
              adapter.notifyDataSetChanged();

              ListView lst = (ListView) findViewById(R.id.listViewEmailComanda);
              lst.setAdapter(adapter);
              lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      comenzi = (ComenziEmail) parent.getItemAtPosition(position);
                      Intent intent = new Intent(getBaseContext(), PreviewComanda.class);
                      intent.putExtra("NumarComanda", comenzi.getNrComanda() );
                      startActivity(intent);
                  }
              });
        }else{
            Toast.makeText(getBaseContext(), "Lista Goala", Toast.LENGTH_LONG).show();
        }

    }
}
