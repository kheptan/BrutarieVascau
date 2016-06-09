package com.example.kp.brutarievascau;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EmailComanda extends AppCompatActivity implements MyDialogInterface {

    CustomEmailAdapter adapter;
    List<ComenziEmail> lista;
    ComenziEmail comenzi;
    ListView lst;
    Date newPickUpDate = null;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    String scope = "oauth2:https://www.googleapis.com/auth/gmail.send";
    Account acount;

    @Override
    public void onDatePick(DialogFragment dialog) {
        SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
        MyDialog myDialog = (MyDialog) dialog;
        Date datenew = myDialog.getdate();
        //ListView lst2 = (ListView) findViewById(R.id.listViewEmailComanda);
        newPickUpDate = datenew;
        DBhelper dBhelper = new DBhelper(getBaseContext());
        lista.clear();
        lista = dBhelper.listALlEmailComenzi(datenew.getTime());

        if(!lista.isEmpty()){
            adapter.clear();
            adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
            //lista.clear();
            //lista.addAll(dBhelper.listALlEmailComenzi(datenew.getTime()));
            //Toast.makeText(getBaseContext(),"nr inrregistrari" + lista.size(), Toast.LENGTH_LONG).show();
            lst.setAdapter(adapter);
            dBhelper.closeDB();
        }else {
            lst.setAdapter(adapter);
            dBhelper.closeDB();
            Toast.makeText(getBaseContext(),"Nu am gasit nici o comanda petru data selectata!!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_email_comanda);

                Button btnSendAll = (Button) findViewById(R.id.btnSendAllMails);

                Date date = new Date();

                DBhelper dBhelper = new DBhelper(getBaseContext());
                lista = dBhelper.listALlEmailComenzi(date.getTime());

                if (!lista.isEmpty()) {
                        adapter = new CustomEmailAdapter(getBaseContext(), R.layout.mail_comanda_listview, lista);
                        lst = (ListView) findViewById(R.id.listViewEmailComanda);
                        lst.setAdapter(adapter);

                        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                comenzi = (ComenziEmail) parent.getItemAtPosition(position);
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
                            sendmail();
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
                                MyDialog mydialog = MyDialog.newInstance();
                                mydialog.show(fragmentTransaction,"test");
                            }
                        });
                }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == REQUEST_CODE_PICK_ACCOUNT) {
                    if (resultCode == RESULT_OK) {
                        acount = new Account(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                                data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                        //new SendMail(TO,FROM,SUBJECT,BODYTEXT).execute();

                    }else  {
                        //
                    }
                }else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
                    Toast.makeText(getBaseContext(), "BAD REQUEST !!!", Toast.LENGTH_LONG).show();
                }
    }

    private void sendmail() {
                DBhelper dBhelper = new DBhelper(getBaseContext());
                Date date = new Date();
                if(newPickUpDate!=null) {
                    date = newPickUpDate;
                }
        
                /**
                ListView lst1 = (ListView) findViewById(R.id.listViewEmailComanda);
                dBhelper.updateEmailList(date.getTime());
                lista.clear();
                lista.addAll(dBhelper.listALlEmailComenzi(date.getTime()));
                adapter.notifyDataSetChanged();
                lst1.setAdapter(adapter);

                String[] accountTypes = new String[]{"com.google"};
                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                        accountTypes, false, null, null, null, null);
                startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);*/
                //dBhelper.closeDB();
    }

    /** SendMultipleEmails**/
    public class SendMultipleMail extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
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

                        //final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                        final TextView textView = (TextView) convertView.findViewById(R.id.EmailComandaListView);

                        if(comenziEmail.getStare()==1) {
                            textView.setText("Comanda: " + comenziEmail.getNrComanda() + "este trimisa deja");
                            textView.setEnabled(false);
                        }else{
                            textView.setText("Comanda: " + comenziEmail.getNrComanda());
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

    public static class MyDialog extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                MyDialogInterface dialogInterface;

                public static MyDialog newInstance(){
                    MyDialog myDialog = new MyDialog();
                    return myDialog;
                }

                private void setDateString(int y,int m,int d){
                    c.set(y,m,d);
                }

                private Date getdate(){
                   return c.getTime();
                }

                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return new DatePickerDialog(getActivity(),this,year,month,day);
                }

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                setDateString(year,monthOfYear,dayOfMonth);
                                dialogInterface.onDatePick(MyDialog.this);
                }

                @Override
                public void onAttach(Activity activity) {
                    super.onAttach(activity);
                    try {
                        dialogInterface = (MyDialogInterface) activity;
                    } catch (ClassCastException e) {
                        // The activity doesn't implement the interface, throw exception
                        throw new ClassCastException(activity.toString()
                                + " must implement NoticeDialogListener");
                    }
                }
    }
}

