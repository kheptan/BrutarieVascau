package com.example.kp.brutarievascau;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientNou extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_nou);

        Button btnInsertXml = (Button) findViewById(R.id.buttonImportClientXml);
        btnInsertXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try {
                    //insertXmlClient();
                try {
                    getPermision();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //} catch (IOException e) {
                //    e.printStackTrace();
                //}
            }
        });
    }

    private void getPermision() throws IOException {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_STORAGE);
        }else{
            insertXmlClient();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_STORAGE ) {
            if (grantResults.length ==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    insertXmlClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void insertXmlClient() throws IOException {
        File file = null;
        InputStream is = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"clienti.xml");
            if(file!=null){
                is = new BufferedInputStream(new FileInputStream(file));
                new XmlAsync().execute(is);
                //fisier importat cu succes
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class XmlAsync extends AsyncTask<InputStream, Void, List<Client>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar pb = (ProgressBar) findViewById(R.id.progresBarClienti);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Client> doInBackground(InputStream... params) {
            List<Client> allclient = new ArrayList<Client>();
            try {
                allclient=parseXml(params[0]);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return allclient;
        }


        @Override
        protected void onPostExecute(List<Client> clients) {
            super.onPostExecute(clients);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progresBarClienti);
            pb.setVisibility(View.INVISIBLE);
            if(clients.size()>0){
                DBhelper clientDB = new DBhelper(getBaseContext());
                clientDB.openDB();
                for(Client item: clients){
                    clientDB.addClient(item);
                }
                //Toast.makeText(getApplicationContext(), "e mai mare"+clients.size(), Toast.LENGTH_SHORT).show();
                clientDB.closeDB();
            }
        }
    }

    private List<Client> parseXml(InputStream file) throws XmlPullParserException, IOException {
        final String ns = null;
        List<Client> clienti=null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(file, null);
            parser.nextTag();

            clienti = new ArrayList<Client>();
            parser.require(XmlPullParser.START_TAG, ns, "VFPData");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                if (name.equals("c_xml")) {
                    Client client = new Client();
                    /** READ */
                    parser.require(XmlPullParser.START_TAG, ns, "c_xml");
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        String subname = parser.getName();
                        if (subname.equals("denumire")) {
                            if (parser.next() == XmlPullParser.TEXT) {
                                client.setNume(parser.getText());
                                parser.nextTag();
                            }
                        } else if (subname.equals("cod_fiscal")) {
                            if (parser.next() == XmlPullParser.TEXT) {
                                client.setCif(parser.getText());
                                parser.nextTag();
                            }
                        }else if (subname.equals("reg_com")) {
                            if (parser.next() == XmlPullParser.TEXT) {
                                client.setNrReg(parser.getText());
                                parser.nextTag();
                            }
                        }else if (subname.equals("adresa")) {
                            if (parser.next() == XmlPullParser.TEXT) {
                                client.setAdresa(parser.getText());
                                parser.nextTag();
                            }
                        }
                        else {
                            skip(parser);
                        }
                    }

                    clienti.add(client);
                    //skip(parser);
                } else {
                    skip(parser);
                }
            }
        }catch (XmlPullParserException ex){
            ex.printStackTrace();
        }
        return clienti;
    }




    private void skip(XmlPullParser xpp) throws XmlPullParserException, IOException {
        if (xpp.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (xpp.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


    //****** Adauga CLient Nou ****** //////////////////////////////////////////////////////////////
    public void insertClientDB(View view){
        Client client = new Client();
        EditText txtNume = (EditText) findViewById(R.id.editNume);

        if(txtNume.toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Completati cimpul nume "+txtNume.getText().toString(), Toast.LENGTH_SHORT).show();
            txtNume.setFocusable(true);
            txtNume.requestFocus();
        }

        EditText txtAdresa = (EditText) findViewById(R.id.editAdresa);
        EditText txtCif = (EditText) findViewById(R.id.editCif);
        EditText txtInfo = (EditText) findViewById(R.id.editInfo);
        EditText txtIban = (EditText) findViewById(R.id.editIban);
        EditText txtNrReg = (EditText) findViewById(R.id.editNrRegCom);

        client.setNume(txtNume.getText().toString().trim());
        client.setAdresa(txtAdresa.getText().toString().trim());
        client.setCif(txtCif.getText().toString().trim());
        client.setIban(txtIban.getText().toString().trim());
        client.setNrReg(txtNrReg.getText().toString().trim());
        client.setInfoUser(txtInfo.getText().toString().trim());

        DBhelper clientDB = new DBhelper(getBaseContext());

        clientDB.openDB();
        clientDB.addClient(client);

        //Toast.makeText(getApplicationContext(), "am adaugat: "+txtNume.getText().toString(), Toast.LENGTH_SHORT).show();
        finish();
    }

}
