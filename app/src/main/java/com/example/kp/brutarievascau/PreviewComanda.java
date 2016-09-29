package com.example.kp.brutarievascau;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class PreviewComanda extends AppCompatActivity implements onEditDeleteDialogDetalii{

     public ComandaJoin comandaNoua;
     public ArrayComanda arrayAdapter;
     public List<detaliiJoin> lstdetalii;
     List<Aviz> aviz;
     View view;
     ProgressBar pb= null;
     double totalcutva=0;
     String denFisier = "";
     int NrLinie=1;
     ListView listView;
     List<Client> listClient;
     private final static String TAG = "MyActivity";
     final String formatdate = "ddMMyyy";
     final String formatdate2 = "dd-MM-yyy";
     SimpleDateFormat sdate = new SimpleDateFormat(formatdate);
     SimpleDateFormat sdate2 = new SimpleDateFormat(formatdate2);
     Calendar emailDate;
     static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
     static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

     String scope = "oauth2:https://www.googleapis.com/auth/gmail.send";
     Account acount;
     final String BODYTEXT_ANTET = "Total Comanda : \n\r";
     String BODYTEXT = "";
     File file=null;
     File fileAviz= null;
     OutputStream outputStream=null;
     int NumarComanda;
     int IdClient;

     final static String DESTINATAR = "office@brutariavascau.ro";
     //final static String TO = "dacian.capitan@rdsor.ro";
     final static String FROM = "me";
     final static String SUBJECT = "Comanda Email";
     //final static String BODYTEXT = "weeeeeeeeeeeeeee";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_preview_comanda);
                DBhelper db = new DBhelper(getBaseContext());
                db.openDB();

                NumarComanda = getIntent().getIntExtra("NumarComanda", 0);
                IdClient = getIntent().getIntExtra("Client",0);

                comandaNoua = db.getNrAntet(Integer.toString(NumarComanda));
                Calendar calendar = Calendar.getInstance();
                calendar.getTime().setTime(comandaNoua.get_data());
                aviz = db.getAviz(Integer.toString(NumarComanda));
                emailDate = calendar;
                listClient = db.listAllClients();
                Button btnSendEmail = (Button) findViewById(R.id.btnSendEmailCom);
                Button btnAddNEwProdus = (Button) findViewById(R.id.btnAddPreviewProdus);
                if(IdClient==0){
                    btnAddNEwProdus.setEnabled(false);
                }
                ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

                final NetworkInfo mobile = manager.getActiveNetworkInfo();
                pb= (ProgressBar) findViewById(R.id.progressBar);
                pb.setVisibility(View.INVISIBLE);


                totalcutva = comandaNoua.get_valTotal()*0.09;
                lstdetalii=db.getDetalii(Integer.toString(NumarComanda));
                if(lstdetalii !=null){

                        if(listView!=null){
                            listView.removeHeaderView(view);
                            lstdetalii.clear();
                            lstdetalii.addAll(db.getDetalii(Integer.toString(NumarComanda)));
                            arrayAdapter.notifyDataSetChanged();
                            listView.setAdapter(arrayAdapter);
                            listView.addHeaderView(setHeader());
                        }else {
                            arrayAdapter = new ArrayComanda(getBaseContext(), R.layout.custom_nrlinii_listview, lstdetalii);
                            listView = (ListView) findViewById(R.id.listViewDetaliiComanda);
                            listView.addHeaderView(setHeader());
                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(getBaseContext(),"You've got an event la pozitia "+position,Toast.LENGTH_SHORT).show();
                                //Log.i( LOG_TAG, "Item click");
                                detaliiJoin detalii = (detaliiJoin) parent.getItemAtPosition(position);
                                if(detalii!=null) {
                                    DialogFragment df = EditDialogFragment.newInstance(detalii.getNrcomanda(),detalii.getCodprodus(),1, detalii.getLinie(), detalii.getCantitate(),lstdetalii.size());
                                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    df.show(ft, "DiagEdit");
                                }
                            }
                        });

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                detaliiJoin detalii = (detaliiJoin) parent.getItemAtPosition(position);
                                if(detalii!=null) {
                                    DialogFragment df = DeleteDialogFragment.newInstance(detalii.getNrcomanda(),detalii.getLinie());
                                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    df.show(ft, "DiagDelete");
                                }
                                return true;
                            }
                        });

                        db.closeDB();
                }

                btnAddNEwProdus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment dialogFragment = AddDialogFragment.newInstance(NumarComanda,IdClient);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        dialogFragment.show(ft,"Dialog Add");
                    }
                });

                btnSendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mobile !=null) {
                            if(mobile.isConnected()) {
                                //Toast.makeText(getBaseContext(), "avem atitea elemente" + lstdetalii.size(), Toast.LENGTH_LONG).show();
                                //sendmail();
                                pickacount();
                            }
                        }else{
                            Toast.makeText(getBaseContext(), "NU AVETI ACCES LA INTERNET!!!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                acount = new Account(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                        sendmail();

            }else  {
               //
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

    public void sendmail(){
        //Toast.makeText(getBaseContext(), "inceput sendmail + " + lstdetalii.size(), Toast.LENGTH_LONG).show();
        //aviz = db1.getAviz(Integer.toString(NumarComanda));
        //lstdetalii=db1.getDetalii(Integer.toString(NumarComanda));
        Calendar calendar = Calendar.getInstance();
        calendar.getTime().setTime(comandaNoua.get_data());

        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            path.mkdirs();

            String[] splitTab = acount.name.split("[@._]");
            String split="";

            if(splitTab[1].contains("gmail")){
                split = splitTab[0];
            }else{
                split = splitTab[1];
            }
            denFisier = "F_17314580_"+comandaNoua.get_nrCom()+"_"+sdate2.format(calendar.getTime())+"_"+split+".xml";
            file = new File(path, denFisier);
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                    System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
            XmlSerializer serializer = factory.newSerializer();
            outputStream = new BufferedOutputStream(new FileOutputStream(file));

            serializer.setOutput(outputStream,"UTF-8");
            serializer.startDocument("UTF-8", null);

            Client findClient=null;
            for(Client item : listClient){
                if (item.getID()==comandaNoua.get_id_client()){
                    findClient = item;
                }
            }

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
            serializer.startTag(null,"FacturaData").text(sdate2.format(emailDate.getTime())).endTag(null, "FacturaData");
            serializer.startTag(null,"FacturaScadenta").text(sdate2.format(emailDate.getTime())).endTag(null, "FacturaScadenta");
            serializer.startTag(null,"FacturaTaxareInversa").text("Nu").endTag(null, "FacturaTaxareInversa");
            serializer.startTag(null,"FacturaTVAIncasare").text("Nu").endTag(null, "FacturaTVAIncasare");
            serializer.startTag(null, "FacturaInformatiiSuplimentare").endTag(null, "FacturaInformatiiSuplimentare");
            serializer.startTag(null,"FacturaMoneda").text("RON").endTag(null, "FacturaMoneda");
            serializer.startTag(null,"FacturaCotaTVA").text("TVA (9%)").endTag(null, "FacturaCotaTVA");
            serializer.startTag(null,"FacturaGreutate").text("0.000").endTag(null, "FacturaGreutate");
            serializer.endTag(null, "Antet");

            serializer.startTag(null, "Detalii");
            serializer.startTag(null, "Continut");

            for(detaliiJoin linii : lstdetalii){
                serializer.startTag(null, "Linie");
                //Toast.makeText(getBaseContext(), "loop detalii !!! "+ linii.getCodprodus(), Toast.LENGTH_LONG).show();
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
            serializer.startTag(null,"TotalValoare").text(""+comandaNoua.get_valTotal()).endTag(null, "TotalValoare");
            serializer.startTag(null,"TotalTVA").text(""+totalcutva).endTag(null, "TotalTVA");
            serializer.startTag(null,"Total").text(""+totalcutva+comandaNoua.get_valTotal()).endTag(null, "Total");
            serializer.endTag(null, "Sumar");

            serializer.startTag(null, "Observatii");
            serializer.startTag(null,"txtObservatii").text("").endTag(null, "txtObservatii");
            serializer.startTag(null, "SoldClient").endTag(null,"SoldClient");
            serializer.startTag(null,"ModalitatePlata").endTag(null,"ModalitatePlata");
            serializer.endTag(null,"Observatii");
            serializer.endTag(null, "Factura");
            serializer.endDocument();

            serializer.flush();
            outputStream.close();

            fileAviz = new File(createAvizFile(emailDate.getTime().getTime()));

            new SendMail(DESTINATAR,FROM,SUBJECT,BODYTEXT_ANTET+BODYTEXT).execute();

        } catch (XmlPullParserException e){
            Log.e(TAG,"A aparut o eroare ",e.getCause());
            Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Log.e(TAG,"A aparut o eroare ",e.getCause());
            Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    /** Implementeaza Interfata din Fragment Dialog  *************************************/
    @Override
    public boolean onEdit(int nrcomanda,int nrlinie,int cant,int codpr,double pretprodus,int codVechi) {
                DBhelper db = new DBhelper(getBaseContext());
                db.openDB();
                if(checkDuplicates(cant,codpr,codVechi)) {
                    Toast.makeText(getBaseContext(), "Acest produs este deja introdus!!!", Toast.LENGTH_LONG).show();
                    db.closeDB();
                    return true;
                }else{
                    if((db.updatePreview(nrcomanda, nrlinie, cant, pretprodus,codpr) > 0)) {
                            comandaNoua = db.getNrAntet(Integer.toString(nrcomanda));
                            aviz = db.getAviz(Integer.toString(NumarComanda));
                            listView.removeHeaderView(view);
                            lstdetalii.clear();
                            lstdetalii.addAll(db.getDetalii(Integer.toString(nrcomanda)));
                            arrayAdapter.notifyDataSetChanged();
                            listView.addHeaderView(setHeader());
                            db.closeDB();
                            return false;
                    }else{
                           return true;
                    }

                }
    }

    @Override
    public void onDelete(int nrcomanda,int nrlinie) {
        DBhelper db = new DBhelper(getBaseContext());
        db.openDB();
                //Toast.makeText(getBaseContext(), "" + pretprodus, Toast.LENGTH_LONG).show();
        if((db.deleteLineProduct(nrcomanda, nrlinie) > 0)) {
                    comandaNoua = db.getNrAntet(Integer.toString(nrcomanda));
                    aviz = db.getAviz(Integer.toString(NumarComanda));
                    listView.removeHeaderView(view);
                    lstdetalii.clear();
                    lstdetalii.addAll(db.getDetalii(Integer.toString(nrcomanda)));
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                    listView.addHeaderView(setHeader());

        }
        db.closeDB();
    }

    @Override
    public void onAdd(int nrcomanda, int cant, int codpr, double pretprodus) {
                DBhelper db = new DBhelper(getBaseContext());
                db.openDB();
                //Toast.makeText(getBaseContext(), "Acum linie = " + NrLinie, Toast.LENGTH_LONG).show();
                if(checkDuplicates(cant,codpr,0)) {

                }else{
                    NrLinie=NrLinie+1;
                    //Toast.makeText(getBaseContext(), "Acest produs este deja introdus!!!", Toast.LENGTH_LONG).show();
                    double val = patruzeci(cant * pretprodus);

                    detaliiJoin detalii = new detaliiJoin();

                    detalii.setCodprodus(codpr);
                    detalii.setPret(patruzeci(pretprodus));
                    detalii.setNrcomanda(NumarComanda);
                    detalii.setCantitate(cant);
                    detalii.setValoare(patruzeci(val));
                    detalii.setTva(detalii.getValoare()*0.09);
                    detalii.setLinie(NrLinie);

                    db.addDetalii(detalii);

                    aviz = db.getAviz(Integer.toString(NumarComanda));
                    comandaNoua = db.getNrAntet(Integer.toString(nrcomanda));
                    listView.removeHeaderView(view);
                    lstdetalii.clear();
                    lstdetalii.addAll(db.getDetalii(Integer.toString(nrcomanda)));
                    arrayAdapter.notifyDataSetChanged();
                    listView.addHeaderView(setHeader());

                }
        db.closeDB();
    }

    /**Genereaza Lista HEADER ***********************************************************/
    public  View setHeader(){
                ViewGroup parent = (ListView) findViewById(R.id.listViewDetaliiComanda);
                LayoutInflater inflater = getLayoutInflater();
                view=inflater.inflate(R.layout.list_header_detalii,parent,false);

                Calendar calendar = Calendar.getInstance();
                calendar.getTime().setTime(comandaNoua.get_data());

                if(comandaNoua.get_client()!="") {
                    TextView denumire = (TextView) view.findViewById(R.id.header_beneficiar);
                    TextView nrcom = (TextView) view.findViewById(R.id.header_nr_comanda);
                    TextView datac = (TextView) view.findViewById(R.id.dtacomanda);
                    TextView valTotal = (TextView) view.findViewById(R.id.header_valoare_totala);

                    denumire.setText(comandaNoua.get_client());
                    nrcom.setText(Integer.toString(comandaNoua.get_nrCom()));
                    datac.setText(sdate2.format(calendar.getTime()));
                    valTotal.setText(Double.toString(comandaNoua.get_valTotal()));
                }
                return view;
    }

    /** Extinde ArrayAdapter pt generare Detaliile in ListView* *************************/
    public static class ArrayComanda extends ArrayAdapter<detaliiJoin> {
                public Context ctx;
                public int resource_id;
                //public AntetComanda nrAntet;
                //public List<detaliiJoin> liniiComanda;

                public ArrayComanda(Context context, int resource, List<detaliiJoin> lista) {
                    super(context, resource, lista);
                    this.ctx = context;
                    this.resource_id = resource;
                    //this.liniiComanda = lista;
                }


                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(resource_id,parent,false);
                    }

                    detaliiJoin detalii = getItem(position);

                    TextView denProdus = (TextView) convertView.findViewById(R.id.detalii_idProdus);
                    TextView cantitateProdus = (TextView) convertView.findViewById(R.id.detalii_CantitateProdus);
                    TextView pretProdus = (TextView) convertView.findViewById(R.id.detalii_PretProdus);
                    TextView valProdus = (TextView) convertView.findViewById(R.id.detalii_ValoareProdus);

                    denProdus.setMaxLines(2);
                    denProdus.setText(detalii.getDenProdus());
                    cantitateProdus.setText(Integer.toString(detalii.getCantitate()));
                    pretProdus.setText(Double.toString(detalii.getPret()));
                    valProdus.setText(Double.toString(detalii.getValoare()));
                    return  convertView;
                }
    }

    public String createAvizFile(long dataaviz){
        DBhelper dBhelper = new DBhelper(getBaseContext());
        //Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdate = new SimpleDateFormat("dd-MM-yyy");
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = null;
        BODYTEXT = "";
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
                for(Aviz linii : aviz) {
                    fileWriter.write("\"" + linii.getCodprodus() + "\"");
                    fileWriter.write(",");
                    fileWriter.write("\"" + linii.getDenProdus() + "\"");
                    fileWriter.write(",");
                    fileWriter.write("\"BUC\"");
                    fileWriter.write(",");
                    fileWriter.write(Integer.toString(linii.getCantitate()) + ".0000");
                    fileWriter.write(",");
                    fileWriter.write(String.valueOf(linii.getPret()) + "00");
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
                    BODYTEXT += linii.getCodprodus() + " |" + linii.getDenProdus() + " Cantitate totala : " + linii.getCantitate() + "\r\n";
                }
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare scriere aviz : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getBaseContext(), "Nu pot scrie in director fisierul comanda!!!", Toast.LENGTH_SHORT).show();
            file = new File(path.getAbsolutePath(),"filerror.err");
        }
        return file.getAbsolutePath();
    }

/** Creaza Dialog Adaugare Produse */
    public static class AddDialogFragment extends DialogFragment {
                int numarComanda;
                int idClient;
                int codprodus;
                double pret;

                onEditDeleteDialogDetalii callback;

                public AddDialogFragment() {
                }

                static AddDialogFragment newInstance(int nrcomanda,int idclient) {
                      AddDialogFragment dialog = new AddDialogFragment();
                      Bundle args = new Bundle();
                      args.putInt("COMANDA",nrcomanda);
                      args.putInt("IDCLIENT",idclient);
                      dialog.setArguments(args);
                      return  dialog;
                }

                @Override
                public void onCreate(@Nullable Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    numarComanda = getArguments().getInt("COMANDA");
                    idClient = getArguments().getInt("IDCLIENT");
                }

                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    final int cantitate=0;
                    LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                    View v = layoutInflater.inflate(R.layout.preview_add_produse,null);
                    final EditText cantiEdit = (EditText) v.findViewById(R.id.editTextCantitatePreviewProdusNou);
                    DBhelper db = new DBhelper(getContext());
                    db.openDB();

                    List<CoduriProduse> ProdusePreview = db.listAllProducts();
                    final Spinner spiner= (Spinner) v.findViewById(R.id.spinnerPreviewAddProduse);
                    ComandaNouaProdus.CustomSpinnerAdapter previewSpinnderAdapter = new ComandaNouaProdus.CustomSpinnerAdapter(getContext(),R.layout.custom_spinner_produse,ProdusePreview);
                    spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        CoduriProduse itemSelected;
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            itemSelected = (CoduriProduse) parent.getItemAtPosition(position);
                            codprodus = itemSelected.getID();
                            pret = itemSelected.getPret();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            return;
                        }
                    });

                    spiner.setAdapter(previewSpinnderAdapter);
                    db.closeDB();

                    return new AlertDialog.Builder(getActivity())
                            .setTitle("Adauga produs la comanda")
                            .setView(v)
                            .setPositiveButton("Adauga", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!cantiEdit.getText().toString().trim().isEmpty()) {
                                            callback.onAdd(numarComanda,Integer.parseInt(cantiEdit.getText().toString().trim()),codprodus,pret);
                                    }
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
                        callback = (onEditDeleteDialogDetalii) activity;
                    }catch (ClassCastException ce){
                        throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
                    }
                }
}

    /**Creaza Fereastra Dialog Custom Dialog ***************************************************/
    public static class EditDialogFragment extends DialogFragment {
                int initNrCom;
                int statusCom;
                int nr_linie;
                int cantitate;
                int cod_produs;
                int selected_produs;
                double pret;
                int FULL;
                int seli=0;
                int item =0;
                onEditDeleteDialogDetalii callback;


                /** Empty constructor *****************************/
                public EditDialogFragment() {

                }
                static EditDialogFragment newInstance(int nrcomanda,int codprodus,int status,int nrlinie,int cantitateCom,int full){
                          EditDialogFragment f = new EditDialogFragment();
                          Bundle args = new Bundle();
                          if(status==0) {
                             args.putInt("Status",0);
                          }else{
                             args.putInt("Status",1);
                          }
                          args.putInt("NR_LINIE",nrlinie);
                          args.putInt("NR_COMANDA", nrcomanda);
                          args.putInt("COD_PRODUS",codprodus);
                          args.putInt("CANTITATE",cantitateCom);
                          args.putInt("FULL",full);
                          f.setArguments(args);
                          return f;
                }

                @Override
                public void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        initNrCom = getArguments().getInt("NR_COMANDA");
                        cod_produs = getArguments().getInt("COD_PRODUS");
                        statusCom = getArguments().getInt("Status");
                        nr_linie = getArguments().getInt("NR_LINIE");
                        cantitate = getArguments().getInt("CANTITATE");
                        FULL = getArguments().getInt("FULL");
                }

                @Override
                public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
                        View v = inflater.inflate(R.layout.dialog_edit, container, false);
                        final EditText valCant = (EditText) v.findViewById(R.id.editCantitateDialog);
                        valCant.setHint(Integer.toString(cantitate));

                        DBhelper db = new DBhelper(getContext());
                        db.openDB();

                        List<CoduriProduse> ProdusePreview = db.listAllProducts();
                        final Spinner spiner= (Spinner) v.findViewById(R.id.spinnerPreviewDialog);
                        ComandaNouaProdus.CustomSpinnerAdapter previewSpinnderAdapter = new ComandaNouaProdus.CustomSpinnerAdapter(getContext(),R.layout.custom_spinner_produse,ProdusePreview);

                        if(FULL==ProdusePreview.size()){
                            spiner.setEnabled(false);
                        }

                        //int[] tablou = new int[3];

                        for(int seli=0;seli<previewSpinnderAdapter.getCount(); seli++){
                            //Toast.makeText(getContext(), "item :"+previewSpinnderAdapter.getItem(seli).getID(), Toast.LENGTH_SHORT).show();
                                      if(cod_produs==previewSpinnderAdapter.getItem(seli).getID()){
                                          item=seli;
                                          break;
                                      }
                        }
                    spiner.post(new Runnable() {
                        @Override
                        public void run() {
                            spiner.setSelection(item);
                        }
                    });
                        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                CoduriProduse itemSelected;

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    itemSelected = (CoduriProduse) parent.getItemAtPosition(position);
                                    selected_produs = itemSelected.getID();
                                    pret = itemSelected.getPret();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                    return;
                                }
                        });

                        spiner.setAdapter(previewSpinnderAdapter);
                        final Dialog dialog = getDialog();

                        Button btnSave = (Button) v.findViewById(R.id.btnSavePreviewDialog);
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                   if(!valCant.getText().toString().trim().isEmpty()) {

                                             if(callback.onEdit(initNrCom, nr_linie, Integer.parseInt(valCant.getText().toString().trim()), selected_produs,pret,cod_produs)){

                                             }else{
                                                 dialog.dismiss();
                                             }
                                   }else{
                                       Toast.makeText(getContext(), "Nu ati introdus cantitatea!!!", Toast.LENGTH_LONG).show();
                                       return;
                                   }
                            }
                        });

                        Button btnCancel = (Button) v.findViewById(R.id.btnCancelPreviewDialog);
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                  dialog.dismiss();
                             }
                        });
                        db.closeDB();
                        return v;
                }


                @Override
                public void onAttach(Activity activity) {
                      super.onAttach(activity);
                      try{
                         callback = (onEditDeleteDialogDetalii) activity;
                      }catch (ClassCastException ce){
                         throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
                      }
                }
    }



    /**Creaza Fereastra Delete Custom Dialog */
    public static class DeleteDialogFragment extends DialogFragment{
                int initNrCom;
                int nr_linie;
                onEditDeleteDialogDetalii callback;

                //Empty constructor
                public DeleteDialogFragment() {
                }

                /** Retunreaza o noua fereastra cu argument*/
                static DeleteDialogFragment newInstance(int nrcomanda,int nrlinie){
                    DeleteDialogFragment f = new DeleteDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("NR_LINIE",nrlinie);
                    args.putInt("NR_COMANDA", nrcomanda);
                    f.setArguments(args);
                    return f;
                }

                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    initNrCom = getArguments().getInt("NR_COMANDA");
                    nr_linie = getArguments().getInt("NR_LINIE");
                }

                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return new AlertDialog.Builder(getActivity())
                            .setTitle("Stergere Produs")
                            .setMessage("Doriti sa stergeti produsul: ")
                            .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //callback.onDelProdusItem(codprodus);
                                    callback.onDelete(initNrCom,nr_linie);

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
                        callback = (onEditDeleteDialogDetalii) activity;
                    }catch (ClassCastException ce){
                        throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
                    }
                }
    }


    public boolean checkDuplicates(int cantitate,int produs,int cod_vechi) {
        DBhelper dBhelper = new DBhelper(getBaseContext());
        dBhelper.openDB();
        int counter=1;
        boolean findit=false;
        detaliiJoin LastObject = new detaliiJoin();
        List<detaliiJoin> mata = dBhelper.getDetalii(Integer.toString(NumarComanda));

        //Toast.makeText(getBaseContext(), "Ultima linie este :" + LastObject.getLinie(), Toast.LENGTH_LONG).show();
        if (mata.isEmpty()) {
            return  false;
        } else {
            LastObject = mata.get(mata.size()-1);
            NrLinie = LastObject.getLinie();
            if(cantitate!=0){
                if(cod_vechi==produs){
                    return false;
                }else{
                    for(detaliiJoin item : mata ){
                        if (item.getCodprodus() == produs) {
                            findit = true;
                        }
                    }
                    return findit;
                }
            }else {return true; }
        }
        //return findit;
    }


    public class SendMail extends AsyncTask<Void,Void,Void> {
        final String to;
        final String from;
        final String subject;
        final String btext;


        final String fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        final String filename = denFisier;
        String token;
        String messageId;


        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        HttpTransport transport = new NetHttpTransport();
        Gmail service;

        public SendMail(String pto,String pfrom,String psubject,String pbody) {
            this.to = pto;
            this.from = pfrom;
            this.subject = psubject;
            this.btext = pbody;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DBhelper dBhelper = new DBhelper(getBaseContext());
            dBhelper.openDB();
            lstdetalii.addAll(dBhelper.getDetalii(Integer.toString(NumarComanda)));
            pb.setVisibility(View.VISIBLE);
            dBhelper.closeDB();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb.setVisibility(View.INVISIBLE);
            DBhelper dBhelper = new DBhelper(getBaseContext());
            dBhelper.openDB();

            listView.removeHeaderView(view);
            lstdetalii.clear();
            lstdetalii.addAll(dBhelper.getDetalii(Integer.toString(NumarComanda)));
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            listView.addHeaderView(setHeader());

            if(messageId!=null){
                Toast.makeText(getBaseContext(), "Mail trimis cu succes !!!", Toast.LENGTH_LONG).show();
                if(fileAviz.exists() && file.exists()){
                    fileAviz.delete();
                    file.delete();
                }
            }else{
                Toast.makeText(getBaseContext(), "Eroare Trimitere mesaj !!!", Toast.LENGTH_LONG).show();
            }
            dBhelper.closeDB();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                token=GoogleAuthUtil.getToken(getBaseContext(),acount,scope);

                //File file = new File(fileDir,filename);
                //FileInputStream is = new FileInputStream(file);

                MimeMessage email = new MimeMessage(session);
                InternetAddress tAddress = new InternetAddress(to);
                InternetAddress fAddress = new InternetAddress(from);
                email.setFrom(new InternetAddress(from));
                email.addRecipient(javax.mail.Message.RecipientType.TO,
                        new InternetAddress(to));
                email.setSubject(subject);
                //email.setText(btext);
                /**
                 *  PARTEA DE UPLOAD CU ATASAMENT ***************************************************************/

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(btext, "text/plain");
                mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                MimeBodyPart atachepart = new MimeBodyPart();
                MimeBodyPart atachepart1 = new MimeBodyPart();
                try {
                    atachepart.attachFile(file.getAbsoluteFile());
                    atachepart1.attachFile(fileAviz.getAbsoluteFile());
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /**
                mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(fileDir + "/"+filename);

                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(filename);
                */
                String contentType = URLConnection.guessContentTypeFromName(file.getName());

                atachepart.setHeader("Content-Type", contentType + "; name=\"" + file.getName() + "\"");
                atachepart.setHeader("Content-Transfer-Encoding", "base64");
                multipart.addBodyPart(atachepart);

                String contentType1 = URLConnection.guessContentTypeFromName(fileAviz.getName());
                atachepart1.setHeader("Content-Type", contentType1 + "; name=\"" + fileAviz.getName() + "\"");
                atachepart1.setHeader("Content-Transfer-Encoding", "base64");
                multipart.addBodyPart(atachepart1);

                email.setContent(multipart);
                /**
                 *  END UPLOAD *********************************************************************************
                 */
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

            }catch (MessagingException | FileNotFoundException e){
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }catch (IOException e){
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (UserRecoverableAuthException e) {
                Intent intent = ((UserRecoverableAuthException)e).getIntent();
                startActivityForResult(intent,
                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
              //e.printStackTrace();
            } catch (GoogleAuthException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
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


}
