package com.example.kp.brutarievascau;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import java.io.IOException;
import java.io.OutputStream;
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
     View view;
     ProgressBar pb= null;
     double totalcutva=0;
     String denFisier = null;

     final String formatdate = "ddMMyyy";
     final String formatdate2 = "dd-MM-yyy";
     SimpleDateFormat sdate = new SimpleDateFormat(formatdate);
     SimpleDateFormat sdate2 = new SimpleDateFormat(formatdate2);

     static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
     static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

     String scope = "oauth2:https://www.googleapis.com/auth/gmail.send";
     Account acount;

     File file=null;
     OutputStream outputStream=null;

     final static String TO = "dacian.capitan@rdsor.ro";
     final static String FROM = "me";
     final static String SUBJECT = "Email de test din android!!!";
     final static String BODYTEXT = "weeeeeeeeeeeeeee";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_preview_comanda);
                DBhelper db = new DBhelper(getBaseContext());
                db.openDB();

                int numar_comanda = getIntent().getIntExtra("NumarComanda", 0);
                comandaNoua = db.getNrAntet(Integer.toString(numar_comanda));
                final java.util.Date date = new java.util.Date();
                date.setTime(comandaNoua.get_data());

                Button btnSendEmail = (Button) findViewById(R.id.btnSendEmailCom);
                ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

                final NetworkInfo mobile = manager.getActiveNetworkInfo();
                pb= (ProgressBar) findViewById(R.id.progressBar);
                pb.setVisibility(View.INVISIBLE);

                if (Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED)) {
                    Calendar calendar = Calendar.getInstance();
                    //Date now = new Date(calendar.getTimeInMillis());

                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    path.mkdirs();
                    denFisier = "F_17314580_numarfactura_"+sdate2.format(date)+"_"+sdate.format(date)+".xml";
                    file = new File(path, denFisier);
                }


                totalcutva = comandaNoua.get_valTotal()*0.09;

                final List<Client> listClient = db.listAllClients();
                lstdetalii=db.getDetalii(Integer.toString(numar_comanda));
                if(lstdetalii !=null){
                        arrayAdapter = new ArrayComanda(getBaseContext(), R.layout.custom_nrlinii_listview, lstdetalii);

                        ListView listView = (ListView) findViewById(R.id.listViewDetaliiComanda);
                        listView.addHeaderView(setHeader());

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                detaliiJoin detalii = (detaliiJoin) parent.getItemAtPosition(position);
                                if(detalii!=null) {
                                    DialogFragment df = EditDialogFragment.newInstance(detalii.getNrcomanda(), 1, detalii.getLinie(), detalii.getCantitate(),lstdetalii.size());
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
                                    DialogFragment df = EditDialogFragment.newInstance(detalii.getNrcomanda(), 0, detalii.getLinie(), 0,lstdetalii.size());
                                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    df.show(ft, "DiagDelete");
                                }
                                return true;
                            }
                        });

                        listView.setAdapter(arrayAdapter);
                        db.closeDB();
                }
                /**
                Button b = (Button) findViewById(R.id.);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                */
                btnSendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mobile !=null) {
                            if(mobile.isConnected()) {
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
                                                           // serializer.startTag(null,"ClientNrRegCom").text(findClient.getNrReg()).endTag(null,"ClientNrRegCom");
                                                            //serializer.startTag(null,"ClientAdresa").text(findClient.getAdresa()).endTag(null,"ClientAdresa");
                                                          //  serializer.startTag(null, "ClientBanca").endTag(null,"ClientBanca");
                                                           // serializer.startTag(null,"ClientIBAN").text(findClient.getIban()).endTag(null,"ClientIBAN");
                                                        }

                                                        serializer.startTag(null,"FacturaNumar").text("1").endTag(null, "FacturaNumar");
                                                        serializer.startTag(null,"FacturaData").text(sdate2.format(date)).endTag(null, "FacturaData");
                                                        serializer.startTag(null,"FacturaScadenta").text(sdate2.format(date)).endTag(null, "FacturaScadenta");
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
                                                                              serializer.startTag(null,"LinieNrCrt").text(""+linii.getLinie()).endTag(null, "LinieNrCrt");
                                                                              serializer.startTag(null,"Descriere").text(linii.getDenProdus()).endTag(null, "Descriere");
                                                                              serializer.startTag(null, "CodArticolFurnizor").text(""+linii.getCdProdus()).endTag(null, "CodArticolFurnizor");
                                                                              serializer.startTag(null,"CodArticolClient").endTag(null, "CodArticolClient");
                                                                              serializer.startTag(null,"CodBare").endTag(null, "CodBare");
                                                                              serializer.startTag(null, "InformatiiSuplimentare").endTag(null, "InformatiiSuplimentare");
                                                                              serializer.startTag(null,"UM").text("BUC").endTag(null, "UM");
                                                                              serializer.startTag(null,"Cantitate").text(""+linii.getCantitate()).endTag(null, "Cantitate");
                                                                              serializer.startTag(null,"Pret").text(""+linii.getPret()).endTag(null, "Pret");
                                                                              serializer.startTag(null,"Valoare").text(""+linii.getValoare()).endTag(null, "Valoare");
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

                                } catch (XmlPullParserException xml){
                                    xml.printStackTrace();
                                } catch (IOException io){
                                    io.printStackTrace();
                                }

                                String[] accountTypes = new String[]{"com.google"};
                                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                        accountTypes, false, null, null, null, null);
                                startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
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
                new SendMail(TO,FROM,SUBJECT,BODYTEXT).execute();

            }else  {
               //
            }
        }else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
            Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
        }
    }



    /** Implementeaza Interfata din Fragment Dialog  *************************************/
    @Override
    public void onEdit(int nrcomanda,int nrlinie,int cant,int codpr,double pretprodus) {
                DBhelper db = new DBhelper(getBaseContext());
                db.openDB();
                ListView listView1 = (ListView) findViewById(R.id.listViewDetaliiComanda);
               //Toast.makeText(getBaseContext(), "" + pretprodus, Toast.LENGTH_LONG).show();
                if((db.updatePreview(nrcomanda, nrlinie, cant, pretprodus,codpr) > 0)) {
                        comandaNoua = db.getNrAntet(Integer.toString(nrcomanda));
                        listView1.removeHeaderView(view);
                        lstdetalii.clear();
                        lstdetalii.addAll(db.getDetalii(Integer.toString(nrcomanda)));
                        arrayAdapter.notifyDataSetChanged();
                        listView1.addHeaderView(setHeader());
                }
                db.closeDB();
    }


    @Override
    public void onDelete(int nrcomanda,int nrlinie) {
        Toast.makeText(getBaseContext(), "Sterge Comanda!!! " + nrlinie, Toast.LENGTH_LONG).show();
    }


    /**Genereaza Lista HEADER ***********************************************************/
    public  View setHeader(){
                ViewGroup parent = (ListView) findViewById(R.id.listViewDetaliiComanda);
                LayoutInflater inflater = getLayoutInflater();
                view=inflater.inflate(R.layout.list_header_detalii,parent,false);

                java.util.Date date = new java.util.Date();
                date.setTime(comandaNoua.get_data());

                if(comandaNoua.get_client()!="") {
                    TextView denumire = (TextView) view.findViewById(R.id.header_beneficiar);
                    TextView nrcom = (TextView) view.findViewById(R.id.header_nr_comanda);
                    TextView datac = (TextView) view.findViewById(R.id.dtacomanda);
                    TextView valTotal = (TextView) view.findViewById(R.id.header_valoare_totala);

                    denumire.setText(comandaNoua.get_client());
                    nrcom.setText(Integer.toString(comandaNoua.get_nrCom()));
                    datac.setText(sdate2.format(date));
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

                    denProdus.setText(detalii.getDenProdus());
                    cantitateProdus.setText(Integer.toString(detalii.getCantitate()));
                    pretProdus.setText(Double.toString(detalii.getPret()));
                    valProdus.setText(Double.toString(detalii.getValoare()));
                    return  convertView;
                }
    }


    /**Creaza Fereastra Dialog Custom ***************************************************/
    public static class EditDialogFragment extends DialogFragment {
                int initNrCom;
                int statusCom;
                int nr_linie;
                int cantitate;
                int codprodus;
                double pret;
                int FULL;
                onEditDeleteDialogDetalii callback;

                /** Empty constructor *****************************/
                public EditDialogFragment() {
                }


                /** Retunreaza o noua fereastra cu argument*/
                static EditDialogFragment newInstance(int nrcomanda,int status,int nrlinie,int cantitateCom,int full){
                          EditDialogFragment f = new EditDialogFragment();
                          Bundle args = new Bundle();
                          if(status==0) {
                             args.putInt("Status",0);
                          }else{
                             args.putInt("Status",1);
                          }
                          args.putInt("NR_LINIE",nrlinie);
                          args.putInt("NR_COMANDA", nrcomanda);
                          args.putInt("CANTITATE",cantitateCom);
                          args.putInt("FULL",full);
                          f.setArguments(args);
                          return f;
                }

                @Override
                public void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        initNrCom = getArguments().getInt("NR_COMANDA");
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
                        Spinner spiner = (Spinner) v.findViewById(R.id.spinnerPreviewDialog);
                        ComandaNouaProdus.CustomSpinnerAdapter previewSpinnderAdapter = new ComandaNouaProdus.CustomSpinnerAdapter(getContext(),R.layout.custom_spinner_produse,ProdusePreview);

                        if(FULL==ProdusePreview.size()){
                            spiner.setEnabled(false);
                        }
                        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                CoduriProduse itemSelected;
                                itemSelected = (CoduriProduse) parent.getItemAtPosition(position);
                                codprodus = itemSelected.getID();
                                pret = itemSelected.getPret();
                                Toast.makeText(getContext(), "" + codprodus, Toast.LENGTH_LONG).show();
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
                                       callback.onEdit(initNrCom, nr_linie, Integer.parseInt(valCant.getText().toString().trim()), codprodus,pret);
                                       dialog.dismiss();
                                   }else{
                                       Toast.makeText(getContext(), "Nu ati introdus cantitatea!!!", Toast.LENGTH_LONG).show();
                                       return;
                                   }
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


    public void sendEmail(){
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
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
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb.setVisibility(View.INVISIBLE);
            if(messageId!=null){
                Toast.makeText(getBaseContext(), "Mail trimis cu succes !!!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getBaseContext(), "Eroare Trimitere mesaj !!!", Toast.LENGTH_LONG).show();
            }
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
                mimeBodyPart.setContent("testbody", "text/plain");
                mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(fileDir + "/"+filename);

                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(filename);
                String contentType = URLConnection.guessContentTypeFromName(filename);

                mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + filename + "\"");
                mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

                multipart.addBodyPart(mimeBodyPart);

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

            }catch (MessagingException | FileNotFoundException me){
                me.printStackTrace();
            }catch (IOException io){
                io.printStackTrace();
            } catch (UserRecoverableAuthException e) {
                Intent intent = ((UserRecoverableAuthException)e).getIntent();
                startActivityForResult(intent,
                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
              //e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
