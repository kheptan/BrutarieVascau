package com.example.kp.brutarievascau;

import android.graphics.Path;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClientNou extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_nou);

        Button btnInsertXml = (Button) findViewById(R.id.buttonImportClientXml);
        btnInsertXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertXmlClient();
            }
        });
    }

    private void insertXmlClient() {
        File path = null;
        try {
            path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"clienti.xml");
            if(readPath(path)){
                //fisier importat cu succes
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private boolean readPath(File path) throws IOException {
        if(path!=null){
            InputStream is = null;
            try{
                is = new BufferedInputStream( new FileInputStream(path));
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                xpp.setInput(is,null);
                xpp.nextTag();
                readFeed(xpp);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                is.close();
            }
            return true;
        }else{
            return false;
        }
    }



    private void readFeed(XmlPullParser xpp) throws XmlPullParserException {
        Client client = new Client();
        int eventType = xpp.getEventType();
        int reverse =0;

        try {
            xpp.require(XmlPullParser.START_TAG,null,"VFPData");
             while(xpp.next() != XmlPullParser.END_TAG){
                 if(xpp.getEventType() != XmlPullParser.START_TAG){
                     continue;
                 }
                 String name = xpp.getName();
                 if(name.equals("c_xml")){

                     skip(xpp);
                 }else {
                      skip(xpp);  
                 }
             }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
