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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EmailComanda extends AppCompatActivity {

    CustomEmailAdapter adapter;
    List<ComenziEmail> lista;
    ComenziEmail comenzi;
    int[] tabLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_comanda);

        Button btnSendAll = (Button) findViewById(R.id.btnSendAllMails);

        Date date = new Date();

        DBhelper dBhelper = new DBhelper(getBaseContext());
        lista = dBhelper.listALlEmailComenzi(date.getTime());

        tabLista = new int[lista.size()];
        Arrays.fill(tabLista, 0);
        //int i=0;
        //for (int item: tabLista){
        //    item = lista.get(i).getStare();
        //    i++;
        //}

        if (!lista.isEmpty()) {
            adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);

            ListView lst = (ListView) findViewById(R.id.listViewEmailComanda);
            lst.setAdapter(adapter);

            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    comenzi = (ComenziEmail) parent.getItemAtPosition(position);

                    //tabLista[position] = adapter.tabListPosition[position];
                    Toast.makeText(getBaseContext(), "Valoare tab:" + position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), PreviewComanda.class);
                    intent.putExtra("NumarComanda", comenzi.getNrComanda());
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(getBaseContext(), "Lista Goala", Toast.LENGTH_LONG).show();
        }

        if (btnSendAll != null) {
            btnSendAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) findViewById(R.id.cautaDupaData);
                    editText.setText("" + tabLista[0]);
                }
            });
        }
    }

    private class CustomEmailAdapter extends ArrayAdapter<ComenziEmail> {
        private Context myContext;
        private int res;
        int[] tabListPosition;

        public CustomEmailAdapter(Context context, int resource, List<ComenziEmail> objects) {
            super(context, resource, objects);
            this.myContext = context;
            this.res = resource;
            this.tabListPosition = new int[objects.size()];
            Arrays.fill(this.tabListPosition, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(res, parent, false);
            }

            ComenziEmail comenziEmail = getItem(position);

            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            final TextView textView = (TextView) convertView.findViewById(R.id.EmailComandaListView);
            textView.setText("Comanda: " + comenziEmail.getNrComanda());
            //if(textView!=null && comenziEmail!=null){
            //Toast.makeText(myContext, "format data " + comenziEmail.getDataComanda(), Toast.LENGTH_LONG).show();
            //}

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if (cb.isChecked()) {
                        tabLista[position] = 1;
                    } else {
                        tabLista[position] = 0;
                    }
                    //Toast.makeText(myContext, "Pozitia este: " + position +" Tab este acum: "+tabListPosition[position], Toast.LENGTH_SHORT).show();
                }
            });

            if (comenziEmail.getStare() == 0) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
                checkBox.setEnabled(false);
            }

            return convertView;
        }
    }
}
