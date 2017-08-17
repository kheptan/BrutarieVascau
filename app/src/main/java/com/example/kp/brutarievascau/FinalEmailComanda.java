package com.example.kp.brutarievascau;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class FinalEmailComanda extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public int position_selected=0;
    public String ProdusSelectat="";
    public int CantitateSelectata=0;
    public String CodProdus="";
    ComandaFinala comfinal;

    final String[] articole = {"FRANZELA ALBA 2KG","ROTUNDA ALBA 2KG","FRANZELA ALBA 1KG","ROTUNDA ALBA 1KG","FRANZELA ALBA 0.8KG","ROTUNDA ALBA 0.8KG","PAINE IMPATURATA 0.8KG"};
    ProgressBar progressBar= null;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_GET_ACCOUNTS = 113;

    String scope = "oauth2:https://www.googleapis.com/auth/gmail.send";
    public final static String TAG  = "ErrSendEmail";
    Account acount;
    //final static String DESTINATAR = "dacian.capitan@rdsor.ro";
    final static String DESTINATAR = "office@brutariavascau.ro";
    final static String FROM = "me";
    final static String SUBJECT = "Email comanda :";
    final String BODYTEXT_ANTET = "Total Comanda : \n\r";
    String BODYTEXT="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_email_comanda);
        /** data de azi */
        final Calendar calendar = Calendar.getInstance();

        /**Creaza obiectul pt introducere produse */
        comfinal = new ComandaFinala();
        comfinal.setData(calendar.getTime().getTime());

        /** deschide baza de date  */
        DBhelper dBhelper= new DBhelper(getBaseContext());
        dBhelper.openDB();

        /** listeaza toate produsele */
        final List<CoduriProduse> lstcp = dBhelper.listAllProducts();
        for(int i=0;i<articole.length;i++){
              CoduriProduse articolNou = new CoduriProduse();
              articolNou.setPr_codprodus("00abc"+i);
              articolNou.setPr_denumire(articole[i]);
              lstcp.add(articolNou);
        }

        /** seteaza adaptor spinner */
        Spinner spinner = (Spinner) findViewById(R.id.spinnerAdapterProduseFinal);
        ComandaNouaProdus.CustomSpinnerAdapter cstSpinner = new ComandaNouaProdus.CustomSpinnerAdapter(getBaseContext(),R.layout.custom_spinner_produse,lstcp);
        spinner.setAdapter(cstSpinner);
        spinner.setOnItemSelectedListener(this);

        /** implementeaza buton adauga cu funtie*/
        final Button btnAdauga = (Button)findViewById(R.id.btnAddFinalProdus);
        btnAdauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();
            }
        });

        /** implementeaza buton vizualizare comanda finala*/
        final Button btnFinalPreview = (Button)findViewById(R.id.btnPreviewFinalComand);
        btnFinalPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PreviewFinal.class);
                intent.putExtra("DATACOMANDA", calendar.getTime().getTime());
                startActivity(intent);
            }
        });

        /** Ascunde Progress Bar*/
        progressBar = (ProgressBar) findViewById(R.id.progressBarFinal);
        progressBar.setVisibility(View.INVISIBLE);

        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo mobile = manager.getActiveNetworkInfo();

        Button sendMail = (Button) findViewById(R.id.btnSendFinalEmail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mobile !=null) {
                    if (mobile.isConnected()) {
                        /**sendmail();*/
                        //pickacount();
                        getEmailPermision();
                    }
                }else{
                    Toast.makeText(getBaseContext(), "NU AVETI ACCES LA INTERNET!!!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        dBhelper.closeDB();

    }

    public void getEmailPermision()  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},REQUEST_GET_ACCOUNTS);
        } else {
            pickacount();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
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

    public void doSave(){
        EditText editCantitate = (EditText) findViewById(R.id.editCantitateFinala);

        int cant = 0;
        if (!editCantitate.getText().toString().isEmpty()){
            cant = Integer.parseInt(editCantitate.getText().toString());
        }
        /** Daca nu e salvat nimic inca */
        if(!checkDuplicates(cant)) {
            if(!editCantitate.getText().toString().isEmpty()) {
                DBhelper dBhelper = new DBhelper(getBaseContext());

                if (comfinal != null) {
                    comfinal.setCantitate(cant);
                    comfinal.setCodProdus(CodProdus);
                    comfinal.setDenumire(ProdusSelectat);
                    dBhelper.addComandaFinala(comfinal);
                }
            }else {
                    Toast.makeText(getBaseContext(), " Nu ati introdus o cantitate !!! ", Toast.LENGTH_SHORT).show();
                    return;
                  }
        } else {
            Toast.makeText(getBaseContext(), " Ati mai introdus acest produs!!! ", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    public boolean checkDuplicates(int cantitate) {
        DBhelper dBhelper = new DBhelper(getBaseContext());
        dBhelper.openDB();

        boolean findit=false;
        Calendar calendar = Calendar.getInstance();

        List<ComandaFinala> lstFinal = dBhelper.listAllFinalComenzi(calendar.getTime().getTime());

        if (lstFinal.isEmpty()) {
            return false;
        } else {
            if (cantitate != 0) {
                for(ComandaFinala item : lstFinal ){
                    if (item.getCodProdus().contains(CodProdus)) { findit=true; }
                }
                dBhelper.closeDB();
                return findit;

            } else {
                dBhelper.closeDB();
                return true;
            }
        }

    }

    public void pickacount(){
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                acount = new Account(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                /**new SendMultipleMail(TO,FROM,SUBJECT,BODYTEXT_ANTET+BODYTEXT,fileLists).execute();*/

                sendmail();
            }else  {
                Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
            Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
        }
    }

    public void sendmail(){
        //Toast.makeText(getBaseContext(), "trimit mail", Toast.LENGTH_LONG).show();
        DBhelper dBhelper = new DBhelper(getBaseContext());
        dBhelper.openDB();
        Calendar calendar = Calendar.getInstance();

        List<ComandaFinala> listaProduse = dBhelper.listAllFinalComenzi(comfinal.getData());
        if(!listaProduse.isEmpty()){
            for(ComandaFinala item : listaProduse){
                BODYTEXT += item.getDenumire() + "  = " + item.getCantitate() + "\r\n";
            }
        }
        new AsyncMail(DESTINATAR,FROM,SUBJECT,BODYTEXT_ANTET+BODYTEXT).execute();
        dBhelper.closeDB();
    }

    public class AsyncMail extends AsyncTask<Void,Void,Void> {

        final String To;
        final String From;
        final String Subject;
        final String Btext;

        String token;
        String messageId;
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        HttpTransport transport = new NetHttpTransport();
        Gmail service;


        public AsyncMail(String to, String from, String subject, String body){
            To =to;
            From = from;
            Subject = subject;
            Btext = body;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            if(messageId!=null){
                Toast.makeText(getBaseContext(), "Mail trimis cu succes !!!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getBaseContext(), "Eroare Trimitere mesaj !!!", Toast.LENGTH_SHORT).show();
            }
            BODYTEXT="";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                token = GoogleAuthUtil.getToken(getBaseContext(), acount, scope);

                MimeMessage email = new MimeMessage(session);


                email.setFrom(new InternetAddress(From));
                email.addRecipient(javax.mail.Message.RecipientType.TO,
                        new InternetAddress(To));
                email.setSubject(SUBJECT);
                email.setText(Btext);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                email.writeTo(bytes);
                String encodedEmail = Base64.encodeToString(bytes.toByteArray(), Base64.URL_SAFE);
                Message message = new Message();
                message.setRaw(encodedEmail);
                GoogleCredential credential = new GoogleCredential().setAccessToken(token);
                Message message1 = message;
                service = new Gmail(transport, jsonFactory, credential);
                message1 = service.users().messages().send("me", message1).execute();
                messageId = message1.getId();
            }catch (UserRecoverableAuthException e) {

                Log.e(TAG,"A aparut o eroare la autentificare gmail",e);
                e.printStackTrace();
                startActivityForResult(e.getIntent(),REQUEST_CODE_PICK_ACCOUNT);
                //Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
            catch (GoogleAuthException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (AddressException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (MessagingException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(TAG,"A aparut o eroare ",e.getCause());
                Toast.makeText(getBaseContext(),"Eroare trimitere mail : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CoduriProduse itemSelected;
        position_selected = position;

        itemSelected = (CoduriProduse)parent.getItemAtPosition(position);

        ProdusSelectat = itemSelected.getPr_denumire();
        CodProdus = itemSelected.getPr_codprodus();

        EditText editCantitate = (EditText) findViewById(R.id.editCantitateFinala);
        editCantitate.setText("");
        //Toast.makeText(getBaseContext(), " Ati introdus produs:  " + CodProdus + "cu denumirea " + ProdusSelectat, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    return;
    }

    public static class CustomSpinnerAdapter extends ArrayAdapter<CoduriProduse> implements SpinnerAdapter {
        Context ctx;
        //List<CoduriProduse> lstProduse;

        int res;
        CustomSpinnerAdapter(Context context, int resource, List<CoduriProduse> objects){
            super(context, resource, objects);
            //this.lstProduse = objects;
            this.ctx = context;
            this.res = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(res,parent,false);
            }

            CoduriProduse produse = getItem(position);
            TextView denprodus = (TextView) convertView.findViewById(R.id.spiner_denumire_produs);
            //TextView pretprodus = (TextView) convertView.findViewById(R.id.spiner_pret_produs);

            denprodus.setText(produse.getPr_denumire());
            //pretprodus.setText(produse.getPret().toString());

            return convertView;
        }
    } // end custom class


}
