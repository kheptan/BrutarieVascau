package com.example.kp.brutarievascau;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static android.R.attr.data;

public class EmailComanda extends AppCompatActivity implements onDeleteEmailCOmanda,MyDialogInterface  {

    CustomEmailAdapter adapter;
    List<ComenziEmail> lista;
    ComenziEmail comenzi;
    ListView lst;
    ProgressBar progressBar= null;
    List<Aviz> listaaviz;
    List<File> fileLists = new ArrayList<File>();
    Calendar PickUpDate;
    public final static String TAG  = "MyActivity";
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    String scope = "oauth2:https://www.googleapis.com/auth/gmail.send";
    public Account acount;
    //final static String DESTINATAR = "capitandacian@gmail.com";
    final static String DESTINATAR = "office@brutariavascau.ro";
    final static String FROM = "me";
    final static String SUBJECT = "Email comanda :";
    final String BODYTEXT_ANTET = "Total Comanda : \n\r";
    String BODYTEXT="";

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_GET_ACCOUNTS = 113;



    @Override
    public void onDelete(int nrcomanda) {
        DBhelper db = new DBhelper(getBaseContext());
        db.openDB();
        Calendar calendar = Calendar.getInstance();

        if(PickUpDate!=null && !lista.isEmpty()) {
            calendar = PickUpDate;
        }

            if(db.deleteEmailComanda(nrcomanda)>0) {
                lista.clear();
                listaaviz.clear();
                fileLists.clear();
                lista.addAll(db.listALlEmailComenzi(calendar.getTime().getTime()));
                listaaviz = db.getAvize(calendar.getTime().getTime());
                adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
                lst.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }else{
                Toast.makeText(getBaseContext(), "nu am sters nimic", Toast.LENGTH_LONG).show();
            }
        db.closeDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_email_comanda);


                Button btnSendAll = (Button) findViewById(R.id.btnSendAllMails);
                progressBar = (ProgressBar) findViewById(R.id.progressBarEmailComanda);
                progressBar.setVisibility(View.INVISIBLE);
                //EditText pickDate = (EditText) findViewById(R.id.editDatePickUpDialog);

                ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                final NetworkInfo mobile = manager.getActiveNetworkInfo();

                Calendar calendar = Calendar.getInstance();

                boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_STORAGE);
                }

                /**File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File[] filesToDelete;
                if (Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED)){

                    try {
                        filesToDelete = path.listFiles();
                        if(filesToDelete!=null && filesToDelete.length>1){
                            for(File itemfile : filesToDelete){
                                if(itemfile.getName().contains("F_17314580") || itemfile.getName().contains("Aviz") ){
                                    itemfile.delete();
                                }
                            }
                        }
                    } catch (Exception e){
                       Log.e(TAG,"A aparut o eroare la stergere fisier",e.getCause());
                       Toast.makeText(getBaseContext(),"Eroare stergere fisiere : "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //
                }*/

                DBhelper dBhelper = new DBhelper(getBaseContext());
                lista = dBhelper.listALlEmailComenzi(calendar.getTime().getTime());
                //listaaviz = dBhelper.getAvize(calendar.getTime().getTime());
                //adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
                lst = (ListView) findViewById(R.id.listViewEmailComanda);
                //Toast.makeText(getBaseContext(), "List obj"+ lst.getAdapter().isEmpty(), Toast.LENGTH_LONG).show();
                if (!lista.isEmpty()) {
                        if(lst !=null) {
                            lista.clear();
                            //listaaviz.clear();
                            lista.addAll(dBhelper.listALlEmailComenzi(calendar.getTime().getTime()));
                            listaaviz = dBhelper.getAvize(calendar.getTime().getTime());
                            adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
                            adapter.notifyDataSetChanged();
                            lst.setAdapter(adapter);

                            //adapter.notifyDataSetChanged();
                        }
                        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                comenzi = (ComenziEmail) parent.getItemAtPosition(position);
                                Intent intent = new Intent(getBaseContext(), PreviewComanda.class);
                                intent.putExtra("NumarComanda", comenzi.getNrComanda());
                                intent.putExtra("Client",comenzi.getClientID());
                                startActivity(intent);
                            }
                        });
                        lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                comenzi = (ComenziEmail) parent.getItemAtPosition(position);
                                DialogFragment df = DeleteDialogFragment.newInstance(comenzi.getNrComanda());
                                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                df.show(ft, "DeleteDialog");
                                return true;
                            }
                        });
                } else {
                        Toast.makeText(getBaseContext(), "Lista Goala", Toast.LENGTH_LONG).show();
                }

                if (btnSendAll != null) {
                         btnSendAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mobile !=null) {
                                if (mobile.isConnected()) {
                                    /**sendmail();*/
                                    //pickacount();

                                    getEmailPermision();


                                }
                            }else{
                                Toast.makeText(getBaseContext(), "NU AVETI ACCES LA INTERNET!!! ", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
                }

                EditText editText = (EditText) findViewById(R.id.editDatePickUpDialog);
                if (editText != null) {
                        editText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                DialogFragment newFragment = new MyDialog();
                                newFragment.show(fragmentTransaction,"test");
                            }
                        });
                }
    }


    public void delAllFiles() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] filesToDelete;
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)){

            try {
                filesToDelete = path.listFiles();
                if(filesToDelete!=null && filesToDelete.length>1){
                    for(File itemfile : filesToDelete){
                        if(itemfile.getName().contains("F_17314580") || itemfile.getName().contains("Aviz") ){
                            itemfile.delete();
                        }
                    }
                }
            } catch (Exception e){
                Log.e(TAG,"A aparut o eroare la stergere fisier",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare stergere fisiere : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
            //
        }
    }


    public void getEmailPermision()  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},REQUEST_GET_ACCOUNTS);
        } else {
            Toast.makeText(getBaseContext(), "acuma ar trebui sa am account setat.", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
                    delAllFiles();

                }
                return;
            }
            case REQUEST_GET_ACCOUNTS: {
                //Toast.makeText(getBaseContext(), "am ajuns ub get accounts", Toast.LENGTH_LONG).show();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String[] accountTypes = new String[]{"com.google"};
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                            accountTypes, false, null, null, null, null);
                    startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
                } else {

                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                //super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == REQUEST_CODE_PICK_ACCOUNT) {
                    if (resultCode == RESULT_OK) {
                        acount = new Account(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                                data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                               /**new SendMultipleMail(TO,FROM,SUBJECT,BODYTEXT_ANTET+BODYTEXT,fileLists).execute();*/
                               Toast.makeText(getBaseContext(), acount.toString(), Toast.LENGTH_LONG).show();
                               sendmail();
                    }else  {
                        Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
                    }
                }else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
                    Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
                }
    }

   public void pickacount(){
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    public void sendmail() {

                DBhelper dBhelper = new DBhelper(getBaseContext());
                dBhelper.openDB();
                Calendar calendar = Calendar.getInstance();
                if(PickUpDate!=null && !lista.isEmpty()) {
                       calendar = PickUpDate;
                    //Toast.makeText(getBaseContext(),"pickup date este valabil in sendmail!!!", Toast.LENGTH_LONG).show();
                }

                if(lista.isEmpty()){
                    //Toast.makeText(getBaseContext(),"daca lista este goala in sendmail!!", Toast.LENGTH_LONG).show();
                      lista.addAll(dBhelper.listALlEmailComenzi(calendar.getTime().getTime()));
                       listaaviz = dBhelper.getAvize(calendar.getTime().getTime());

                }else{
                    lista.clear();
                    if(listaaviz!=null) {
                        listaaviz.clear();
                    }

                    fileLists.clear();
                    lista.addAll(dBhelper.listALlEmailComenzi(calendar.getTime().getTime()));
                    listaaviz = dBhelper.getAvize(calendar.getTime().getTime());
                    adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
                    lst.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                File[] files = new File[lista.size()];

                if (lista.size()>0) {
                     int count=0;
                     for(ComenziEmail item: lista){
                           files[count] = new File(createFile(item.getNrComanda(),calendar.getTime().getTime()));
                           fileLists.add(files[count]);
                           count++;
                     }
                    File AvizFile = new File(createAvizFile(calendar.getTime().getTime()));
                    fileLists.add(AvizFile);
                    new SendMultipleMail(DESTINATAR,FROM,SUBJECT,BODYTEXT_ANTET+BODYTEXT,fileLists).execute();
                    /**
                        String[] accountTypes = new String[]{"com.google"};
                        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                accountTypes, false, null, null, null, null);
                        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
                    */
                } else {
                     Toast.makeText(getBaseContext(),"Nu sunt comezni de trimis sau lista este goala!!!", Toast.LENGTH_LONG).show();
                }



    }

    public String createAvizFile(long dataaviz){
        DBhelper dBhelper = new DBhelper(getBaseContext());
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdate = new SimpleDateFormat("dd-MM-yyy");
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = null;
        BODYTEXT="";
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            try {
                String[] splitTab = acount.name.split("[@._]");
                String split="";

                if(splitTab[1].contains("gmail")){
                    split = splitTab[0];
                }else{
                    split = splitTab[1];
                }
                file = new File(path.getAbsolutePath(),"Aviz_"+split+"_"+sdate.format(dataaviz)+".txt");
                //OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                //outputStream.write();
                FileWriter fileWriter = new FileWriter(file);

                for(Aviz linii : listaaviz){
                        fileWriter.write("\""+linii.getCodprodus()+"\"");
                        fileWriter.write(",");
                        fileWriter.write("\"" + linii.getDenProdus() +"\"");
                        fileWriter.write(",");
                        fileWriter.write("\"BUC\"");
                        fileWriter.write(",");
                        fileWriter.write(Integer.toString(linii.getCantitate())+".0000");
                        fileWriter.write(",");
                        fileWriter.write(String.valueOf(patruzeci(linii.getPret()))+"00");
                        fileWriter.write(",");
                        fileWriter.write(String.valueOf(linii.getValoare()));
                        fileWriter.write(",");
                        fileWriter.write("0.0000");
                        fileWriter.write(",");
                        fileWriter.write("\"\"");
                        fileWriter.write(",");
                        fileWriter.write("\"\"");
                        fileWriter.write(",");
                        fileWriter.write("0.0000");
                        fileWriter.write(",");
                        fileWriter.write("0.00");
                        fileWriter.write("\r\n");
                        BODYTEXT += linii.getCodprodus()+" |"+linii.getDenProdus() + " Cantitate totala : " + linii.getCantitate() + "\r\n";
                }
                fileWriter.flush();
                fileWriter.close();
                } catch (IOException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare creare avize fisiere : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getBaseContext(), "Nu pot scrie in director fisierul comanda!!!", Toast.LENGTH_SHORT).show();
            file = new File(path.getAbsolutePath(),"filerror.err");
        }
        return file.getAbsolutePath();
    }


    public String createFile(int comanda,long dateparam){
                DBhelper dBhelper = new DBhelper(getBaseContext());
                dBhelper.openDB();
                ComandaJoin comandaJoin;
                List<detaliiJoin> detaliiJoin;
                List<Client> listClient;
                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat sdate = new SimpleDateFormat("ddMMyyy");
                SimpleDateFormat sdate2 = new SimpleDateFormat("dd-MM-yyy");

                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = null;

                if (Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED)) {
                            try {
                                String[] splitTab = acount.name.split("[@._]");
                                String split="";

                                if(splitTab[1].contains("gmail")){
                                    split = splitTab[0];
                                }else{
                                    split = splitTab[1];
                                }
                                file = new File(path.getAbsolutePath(),"F_17314580_"+comanda+"_"+sdate2.format(dateparam)+"_"+split+".xml");
                                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

                                XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                                        System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
                                XmlSerializer serializer = factory.newSerializer();

                                serializer.setOutput(outputStream,"UTF-8");
                                serializer.startDocument("UTF-8", null);

                                listClient = dBhelper.listAllClients();
                                comandaJoin = dBhelper.getNrAntet(Integer.toString(comanda));
                                detaliiJoin = dBhelper.getDetalii(Integer.toString(comanda));

                                Client findClient=null;
                                for(Client item : listClient){
                                    if (item.getID()==comandaJoin.get_id_client()){
                                        findClient = item;
                                    }
                                }
                                //Toast.makeText(getBaseContext(), "client = " , Toast.LENGTH_SHORT).show();
                                serializer.startTag(null, "Facturi");
                                serializer.startTag(null, "Factura");
                                serializer.startTag(null, "Antet");
                                serializer.startTag(null,"FurnizorNume").text("S.C. ALEXPAN S.R.L").endTag(null, "FurnizorNume");
                                serializer.startTag(null,"FurnizorCIF").text("RO17314580").endTag(null,"FurnizorCIF");
                                serializer.startTag(null,"FurnizorNrRegCom").text("J05/489/2005").endTag(null, "FurnizorNrRegCom");
                                serializer.startTag(null,"FurnizorCapital").text("884232.00").endTag(null, "FurnizorCapital");
                                serializer.startTag(null,"FurnizorAdresa").text("VASCAU str. CRISULUI nr. 1 jud. BIHOR").endTag(null,"FurnizorAdresa");
                                serializer.startTag(null,"FurnizorBanca").text("BANC POST").endTag(null, "FurnizorBanca");
                                serializer.startTag(null,"FurnizorIBAN").text("RO32BPOS05306505272RON01").endTag(null,"FurnizorIBAN");
                                serializer.startTag(null,"FurnizorInformatiiSuplimentare").text("Banca BANC POST IBAN RO32BPOS05306505272RON01").endTag(null, "FurnizorInformatiiSuplimentare");

                                if(findClient!=null){
                                    serializer.startTag(null,"ClientNume").text(findClient.getNume()).endTag(null,"ClientNume");
                                    serializer.startTag(null, "ClientInformatiiSuplimentare").endTag(null,"ClientInformatiiSuplimentare");
                                    serializer.startTag(null,"ClientCIF").text(findClient.getCif()).endTag(null,"ClientCIF");
                                    serializer.startTag(null,"ClientNrRegCom").text(findClient.getNrReg()).endTag(null,"ClientNrRegCom");
                                    serializer.startTag(null,"ClientAdresa").text(findClient.getAdresa()).endTag(null,"ClientAdresa");
                                    //  serializer.startTag(null, "ClientBanca").endTag(null,"ClientBanca");
                                    // serializer.startTag(null,"ClientIBAN").text(findClient.getIban()).endTag(null,"ClientIBAN");
                                }

                                serializer.startTag(null,"FacturaNumar").text("1").endTag(null, "FacturaNumar");
                                serializer.startTag(null,"FacturaData").text(sdate2.format(calendar.getTime())).endTag(null, "FacturaData");
                                serializer.startTag(null,"FacturaScadenta").text(sdate2.format(calendar.getTime())).endTag(null, "FacturaScadenta");
                                serializer.startTag(null,"FacturaTaxareInversa").text("Nu").endTag(null, "FacturaTaxareInversa");
                                serializer.startTag(null,"FacturaTVAIncasare").text("Nu").endTag(null, "FacturaTVAIncasare");
                                serializer.startTag(null, "FacturaInformatiiSuplimentare").endTag(null, "FacturaInformatiiSuplimentare");
                                serializer.startTag(null,"FacturaMoneda").text("RON").endTag(null, "FacturaMoneda");
                                serializer.startTag(null,"FacturaCotaTVA").text("TVA (9%)").endTag(null, "FacturaCotaTVA");
                                serializer.startTag(null,"FacturaGreutate").text("0.000").endTag(null, "FacturaGreutate");
                                serializer.endTag(null, "Antet");

                                serializer.startTag(null, "Detalii");
                                serializer.startTag(null, "Continut");
                                for(detaliiJoin linii : detaliiJoin){
                                    serializer.startTag(null, "Linie");
                                    serializer.startTag(null,"LinieNrCrt").text(""+linii.getLinie()).endTag(null, "LinieNrCrt");
                                    serializer.startTag(null,"Descriere").text(linii.getDenProdus()).endTag(null, "Descriere");
                                    serializer.startTag(null, "CodArticolFurnizor").text(linii.getCodProdus()).endTag(null, "CodArticolFurnizor");
                                    serializer.startTag(null,"CodArticolClient").endTag(null, "CodArticolClient");
                                    serializer.startTag(null,"CodBare").endTag(null, "CodBare");
                                    serializer.startTag(null, "InformatiiSuplimentare").endTag(null, "InformatiiSuplimentare");
                                    serializer.startTag(null,"UM").text("BUC").endTag(null, "UM");
                                    serializer.startTag(null,"Cantitate").text(""+linii.getCantitate()).endTag(null, "Cantitate");
                                    serializer.startTag(null,"Pret").text(""+linii.getPret()).endTag(null, "Pret");
                                    serializer.startTag(null,"Valoare").text(""+linii.getValoare()).endTag(null, "Valoare");
                                    serializer.startTag(null,"ProcTVA").text("9").endTag(null, "ProcTVA");
                                    serializer.startTag(null,"TVA").text(""+linii.getTva()).endTag(null, "TVA");
                                    serializer.endTag(null,"Linie");
                                }
                                serializer.endTag(null, "Continut");

                                serializer.startTag(null, "txtObservatii1");
                                serializer.text("DECLARATIE DE CONFORMITATE:Noi.SC ALEXPAN SRL," +
                                        "declaram pe proprie raspundere ca produsele livrate cu " +
                                        "prezentul document sunt in conformitate cu standardele de " +
                                        "firma si sunt fabricate sub control sanitar-veterinar");
                                serializer.endTag(null, "txtObservatii1");
                                serializer.endTag(null, "Detalii");

                                serializer.startTag(null, "Sumar");
                                serializer.startTag(null,"TotalValoare").text(""+comandaJoin.get_valTotal()).endTag(null, "TotalValoare");
                                serializer.startTag(null,"TotalTVA").text(""+douaZeci(comandaJoin.get_valTotal()*0.09)).endTag(null, "TotalTVA");
                                serializer.startTag(null,"Total").text(""+(douaZeci((comandaJoin.get_valTotal()*0.09)+comandaJoin.get_valTotal()))).endTag(null, "Total");
                                serializer.endTag(null, "Sumar");

                                serializer.startTag(null, "Observatii");
                                serializer.startTag(null,"txtObservatii").text("").endTag(null, "txtObservatii");
                                serializer.startTag(null, "SoldClient").endTag(null,"SoldClient");
                                serializer.startTag(null,"ModalitatePlata").endTag(null,"ModalitatePlata");
                                serializer.endTag(null,"Observatii");
                                serializer.endTag(null, "Factura");
                                serializer.endTag(null, "Facturi");
                                serializer.endDocument();

                                serializer.flush();
                                outputStream.close();;



                            } catch (IOException e) {
                                //Log.e(TAG,"EROARE");
                                Log.e(TAG,"A aparut o eroare la creare xml",e.getCause());
                                Toast.makeText(getBaseContext(),"Eroare creare xml : "+e.getMessage(), Toast.LENGTH_LONG).show();

                            } catch (XmlPullParserException e){
                                Log.e(TAG,"A aparut o eroare la xml",e.getCause());
                                Toast.makeText(getBaseContext(),"Eroare creare xml : "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                }else {
                    Toast.makeText(getBaseContext(), "Nu pot scrie in director fisierul comanda!!!", Toast.LENGTH_SHORT).show();
                    file = new File(path.getAbsolutePath(),"filerror.err");

                }
                dBhelper.closeDB();
                return file.getAbsolutePath();
    }

    /** SendMultipleEmails**/
    public class SendMultipleMail extends AsyncTask<Void,Void,Void> {
                final String to;
                final String from;
                final String subject;
                final String btext;
                final List<File> importFileLists;



                Calendar calendar = Calendar.getInstance();
                /**Init Variables*/
                String token;
                String messageId;
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                HttpTransport transport = new NetHttpTransport();
                Gmail service;

                /**Init Constructor*/
                public SendMultipleMail(String pto,String pfrom,String psubject,String pbody,List<File> files) {
                    this.to = pto;
                    this.from = pfrom;
                    this.subject = psubject;
                    this.btext = pbody;
                    this.importFileLists = files;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar.setVisibility(View.VISIBLE);
                    DBhelper db = new DBhelper(getBaseContext());
                    db.openDB();
                    if(PickUpDate!=null) {
                        calendar = PickUpDate;
                    }
                        lista = db.listALlEmailComenzi(Calendar.getInstance().getTime().getTime());

                    //Toast.makeText(getBaseContext(),"In preExecute aveti lista cu " + importFileLists.size(), Toast.LENGTH_LONG).show();
                }

                @Override
                        protected Void doInBackground(Void... params) {
                          try  {
                                    token= GoogleAuthUtil.getToken(getBaseContext(),acount,scope);
                                    MimeMessage email = new MimeMessage(session);
                                    InternetAddress tAddress = new InternetAddress(to);
                                    InternetAddress fAddress = new InternetAddress(from);
                                    email.setFrom(new InternetAddress(from));
                                    email.addRecipient(javax.mail.Message.RecipientType.TO,
                                            new InternetAddress(to));
                                    email.setSubject(SUBJECT);

                                    /**Atachement Multiple Files */
                                    MimeBodyPart mimeBodyPart = new MimeBodyPart();
                                    mimeBodyPart.setContent(BODYTEXT, "text/plain");
                                    mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

                                    Multipart multipart = new MimeMultipart();
                                    multipart.addBodyPart(mimeBodyPart);

                                    for(File filename : importFileLists) {
                                        MimeBodyPart atachepart = new MimeBodyPart();
                                            try {
                                                atachepart.attachFile(filename.getAbsoluteFile());
                                            } catch (MessagingException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            String contentType = URLConnection.guessContentTypeFromName(filename.getName());
                                            atachepart.setHeader("Content-Type", contentType + "; name=\"" + filename.getName() + "\"");
                                            atachepart.setHeader("Content-Transfer-Encoding", "base64");
                                            multipart.addBodyPart(atachepart);
                                    }

                                    email.setContent(multipart);
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    email.writeTo(bytes);
                                    String encodedEmail = Base64.encodeToString(bytes.toByteArray(), Base64.URL_SAFE);
                                    Message message = new Message();
                                    message.setRaw(encodedEmail);
                                    GoogleCredential credential = new GoogleCredential().setAccessToken(token);
                                    Message message1 = message;
                                    service = new Gmail(transport,jsonFactory,credential);
                                    message1 = service.users().messages().send("me",message1).execute();
                                    messageId=message1.getId();

                          } catch  (IOException e) {
                              Log.e(TAG,"A aparut o eroare ",e.getCause());

                              //Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
                          } catch (UserRecoverableAuthException e) {

                              Log.e(TAG,"A aparut o eroare la autentificare gmail",e);
                              e.printStackTrace();
                              startActivityForResult(e.getIntent(),REQUEST_CODE_PICK_ACCOUNT);
                              //Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
                          } catch (AddressException e) {

                              Log.e(TAG,"A aparut o eroare de adresa ",e.getCause());
                              //Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
                          } catch (MessagingException e) {
                              Log.e(TAG,"A aparut o eroare messageindex",e.getCause());
                              //Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
                          } catch (GoogleAuthException e) {
                              e.printStackTrace();
                          }
                    return null;
                        }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    //Toast.makeText(getBaseContext(), token.toString(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    if(messageId!=null){
                        Toast.makeText(getBaseContext(), "Mail trimis cu succes !!!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "Eroare Trimitere mesaj !!!", Toast.LENGTH_SHORT).show();
                    }
                    DBhelper dBhelper = new DBhelper(getBaseContext());
                    //Toast.makeText(getBaseContext(),"MEssageID : " + messageId, Toast.LENGTH_SHORT).show();

                    Calendar calendar = Calendar.getInstance();
                    if(PickUpDate!=null) {
                        calendar = PickUpDate;
                    }

                    for(File item: fileLists){
                        if(item.delete()){
                            //Toast.makeText(getBaseContext(),"Acum sterg : " + item.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    fileLists.clear();
                    lista.clear();
                    dBhelper.updateEmailList(calendar.getTime().getTime());
                    lista = dBhelper.listALlEmailComenzi(calendar.getTime().getTime());
                    lista.addAll(dBhelper.listALlEmailComenzi(calendar.getTime().getTime()));
                    listaaviz.clear();
                    listaaviz = dBhelper.getAvize(Calendar.getInstance().getTime().getTime());
                    adapter.notifyDataSetChanged();

                }
    }

    /** Create Adapter */
    private class CustomEmailAdapter extends ArrayAdapter<ComenziEmail> {
        private Context myContext;
        private int res;

        public CustomEmailAdapter(Context context, int resource, List<ComenziEmail> objects) {
                        super(context, resource, objects);
                        this.myContext = context;
                        this.res = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = inflater.inflate(res, parent, false);
                        }

                        final ComenziEmail comenziEmail = getItem(position);
                        DBhelper dBhelper = new DBhelper(getBaseContext());
                        dBhelper.openDB();
                        String client = dBhelper.listClient(comenziEmail.getClientID());

                        //final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                        final TextView textView = (TextView) convertView.findViewById(R.id.EmailComandaListView);

                        if(comenziEmail.getStare()==1) {
                            textView.setText(client + ": "+ comenziEmail.getNrComanda() + "este trimisa deja");
                            textView.setEnabled(false);
                        }else{
                            textView.setText(client +": "+comenziEmail.getNrComanda());
                        }
                        /**if (comenziEmail.getStare() == 0) {
                            checkBox.setChecked(false);
                        } else {
                            checkBox.setChecked(true);
                            checkBox.setEnabled(false);
                        }
                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CheckBox cb = (CheckBox) v;
                                tabNumarCom[position] = comenziEmail.getNrComanda();
                                if (cb.isChecked()) {
                                    tabLista[position] = 1;
                                } else {
                                    tabLista[position] = 0;
                                }
                            }
                        });*/
                        return convertView;
        }
    }

    public static class DeleteDialogFragment extends DialogFragment{
                int initNrCom;

                onDeleteEmailCOmanda callback;

                public DeleteDialogFragment() {
                }

                /** Retunreaza o noua fereastra cu argument*/
                static DeleteDialogFragment newInstance(int nrcomanda){
                    DeleteDialogFragment f = new DeleteDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("NR_COMANDA", nrcomanda);
                    f.setArguments(args);
                    return f;
                }

                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    initNrCom = getArguments().getInt("NR_COMANDA");
                }

                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("Stergere Produs")
                            .setMessage("Doriti sa stergeti comanda ?")
                            .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //callback.onDelProdusItem(codprodus);
                                    callback.onDelete(initNrCom);
                                }
                            })
                            .setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create();
                }

                @Override
                public void onAttach(Activity activity) {
                    super.onAttach(activity);
                    try{
                        callback = (onDeleteEmailCOmanda) activity;
                    }catch (ClassCastException ce){
                        throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
                    }
                }
    }

    public double douaZeci(double d)
    {
        BigDecimal bd = new BigDecimal(d);
        bd=bd.setScale(2,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
    public double patruzeci(double d)
    {
        BigDecimal bd = new BigDecimal(d);
        bd=bd.setScale(4,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static class MyDialog extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

                MyDialogInterface callback;

                public MyDialog() {}

                static MyDialog newInstance(){
                    MyDialog f = new MyDialog();
                    //Bundle args = new Bundle();
                    //args.putInt("NR_COMANDA", nrcomanda);
                    //f.setArguments(args);
                    return f;
                }
                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    return new DatePickerDialog(getActivity(),this,year,month,day);
                }
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //setDateString(year,monthOfYear,dayOfMonth);
                    //dialogInterface.onDatePick(MyDialog.this);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    callback.onDatePick(calendar);
                }
                @Override
                public void onAttach(Activity activity) {
                    super.onAttach(activity);
                    try{
                        callback = (MyDialogInterface) activity;
                    }catch (ClassCastException ce){
                        throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
                    }
                }
    }

    @Override
    public void onDatePick(Calendar PickerDate) {
        PickUpDate = PickerDate;
        DBhelper dBhelper = new DBhelper(getBaseContext());
        dBhelper.openDB();

        lista = dBhelper.listALlEmailComenzi(PickUpDate.getTime().getTime());

        //Toast.makeText(getBaseContext(), "ceas : " + lista.size(), Toast.LENGTH_LONG).show();
        if (!lista.isEmpty()) {
            lista.clear();
            if(listaaviz!=null){
                 listaaviz.clear();
            }
            lista.addAll(dBhelper.listALlEmailComenzi(PickUpDate.getTime().getTime()));
            //listaaviz = dBhelper.getAvize(PickUpDate.getTime());

            adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
            adapter.notifyDataSetChanged();
            lst.setAdapter(adapter);
            dBhelper.closeDB();
        } else {
            if(listaaviz!=null){
            listaaviz.clear();
            }
            fileLists.clear();

            dBhelper.closeDB();
            Toast.makeText(getBaseContext(), "Nu am gasit nici o comanda petru data selectata!!!", Toast.LENGTH_LONG).show();
        }
    }
}

