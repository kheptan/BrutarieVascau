package com.example.kp.brutarievascau;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.kp.brutarievascau.R.id.textViewZeroClients;

public class ListaMagazine extends AppCompatActivity implements DialogListenerFunctions {

    List<Client> list;
    UserAdapter ua;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_magazine);
        //Toast.makeText(getBaseContext(), "Test upgrade APK", Toast.LENGTH_LONG).show();
        DBhelper dBhelper = new DBhelper(getBaseContext());
        txt = (TextView) findViewById(R.id.textViewZeroClients);

        dBhelper.openDB();


        /** Creaza lista cu clienti  (LIST)*/

        list = dBhelper.listAllClients();

        /** Creaza Adapterul  */
        ua= new UserAdapter(getBaseContext(),R.layout.client_view_item,list);
        ua.notifyDataSetChanged();
        ListView listView = (ListView) findViewById(R.id.listClients);

        //ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        LayoutInflater ly = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = ly.inflate(R.layout.listamagazine_header_listview,listView,false);
        listView.addHeaderView(header);
        /**
         * La apasare lunga pe client       */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Client fc = (Client) parent.getItemAtPosition(position);
                showdialog(fc.getID(), fc.getNume(), position);
                return true;
            }
        });


        /**
         * ONCLICK cu trimitere la ADD NEW PRODUCTS
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client cl = (Client)parent.getItemAtPosition(position);
                Intent intent =  new Intent(getApplicationContext(),ComandaNouaProdus.class);
                intent.putExtra("ClientID",cl.getID());
                startActivity(intent);
            }
        });


        /**  Seteaza adapterul si inchide baza de date */
        listView.setAdapter(ua);
        dBhelper.closeDB();
    }


    /**   Implementeaza functia ShowDialog       */
    public void showdialog(int idc,String name,int position){
        InfoDialog infodialog = InfoDialog.newInstance(idc,name,position);
        infodialog.show(getSupportFragmentManager(),"dialog");
    }


    /**  Implementeaza metodele din Interfata   -  Buton Yes/No ***/
    @Override
    public void onDialogPositiveClick(DialogFragment dialog,int idclient,int itemPosition) {
        DBhelper dBhelper = new DBhelper(getBaseContext());
        dBhelper.openDB();
        dBhelper.deleteClient(idclient);
        Client cl = list.get(itemPosition);
        list.remove(cl);
        if(list.isEmpty()) {
          txt.setText("Nu sunt clienti introdusi in baza de date!!!");
        }
        ua.notifyDataSetChanged();
        dBhelper.closeDB();
    }

}
