package com.example.kp.brutarievascau;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComandaNouaProdus extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

         AntetComanda antetObj;
         detaliiJoin detaliiObj;
         int idProdus;
         int nrlinie=1;
         Button btnShowComanda;
         Button btnAdaugaProdus;
         Button btnSave;
         int listcount;
         int position_selected=0;
         ArrayList<Integer> lastItemSelected = new ArrayList<Integer>();

         @Override
          protected void onCreate(Bundle savedInstanceState) {
                  super.onCreate(savedInstanceState);
                  setContentView(R.layout.activity_comanda_noua_produs);

                  btnShowComanda = (Button) findViewById(R.id.viewdetailcomanda);
                  btnAdaugaProdus = (Button) findViewById(R.id.butonAddProdusSpiner);
                  btnSave = (Button) findViewById(R.id.savecomandspinner);
                  Button btnSaveALL = (Button) findViewById(R.id.btnSaveAll);

                  List<AntetComanda> insertNrCom;
                  List<detaliiJoin> insertLinie;



                  EditText focusField = (EditText) findViewById(R.id.spinerfieldcantitate);

                  btnAdaugaProdus.setEnabled(false);
                  focusField.requestFocus();
                  Intent intent=getIntent();
                  int ClientId = intent.getIntExtra("ClientID",0);
                  //Toast.makeText(getBaseContext(), "Client ID "+ClientId, Toast.LENGTH_LONG).show();

                  DBhelper dBhelper= new DBhelper(getBaseContext());
                  dBhelper.openDB();
                  final List<CoduriProduse> lstcp = dBhelper.listAllProducts();

                  /**Cauta dupa numar comanda daca exista sau pune 1*/
                  insertNrCom = dBhelper.getAntet();
                       if (insertNrCom.isEmpty()) {
                             Calendar calendar = Calendar.getInstance();
                             java.util.Date now = new java.util.Date(calendar.getTimeInMillis());
                             //final String formatdate = "dd.MM.yyy";
                             //SimpleDateFormat sdate = new SimpleDateFormat(formatdate);
                             //sdate.format(now);
                             antetObj = new AntetComanda();
                             antetObj.setNrCom(1);
                             antetObj.setDataCom(now.getTime());
                             antetObj.setClientCom(ClientId);
                             //Toast.makeText(getBaseContext(), "Prima comanda!!!", Toast.LENGTH_LONG).show();
                       }else{
                             Calendar calendar = Calendar.getInstance();
                             java.util.Date now = new java.util.Date(calendar.getTimeInMillis());
                             //final String formatdate = "dd.MM.yyy";
                             //SimpleDateFormat sdate = new SimpleDateFormat(formatdate);

                             antetObj = new AntetComanda();
                             antetObj.setNrCom(insertNrCom.get(insertNrCom.size() - 1).getNrCom() + 1);
                             antetObj.setClientCom(ClientId);
                             antetObj.setDataCom(now.getTime());
                             //Toast.makeText(getBaseContext(), "Comanda Nr: " + antetObj.getNrCom() , Toast.LENGTH_LONG).show();
                       }

                  insertLinie = dBhelper.getDetalii(Integer.toString(antetObj.getNrCom()));

                  if ( insertLinie == null || insertLinie.isEmpty() ) {
                        //Toast.makeText(getBaseContext(), "Nu aM detaLIII!!", Toast.LENGTH_LONG).show();
                         detaliiObj = new detaliiJoin();
                         detaliiObj.setLinie(nrlinie);
                  }

                  Spinner spinner = (Spinner) findViewById(R.id.spinnerAdapterProduse);
                  CustomSpinnerAdapter cstSpinner = new CustomSpinnerAdapter(getBaseContext(),R.layout.custom_spinner_produse,lstcp);
                  spinner.setAdapter(cstSpinner);
                  spinner.setOnItemSelectedListener(this);

                  EditText field_cantitate = (EditText) findViewById(R.id.spinerfieldcantitate);
                  field_cantitate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View v, boolean hasFocus) {
                          if (!hasFocus) {
                              // doSave();
                          }
                      }
                  });


                  btnSave.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          doSave();
                      }
                  });

                  btnSaveALL.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          doSaveALl();
                      }
                  });

                  dBhelper.closeDB();

                  btnShowComanda.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                        //Toast.makeText(getBaseContext(), " listcount "+ listcount + " listarray "+ lstcp.size(), Toast.LENGTH_LONG).show();
                         Intent intent = new Intent(getBaseContext(), PreviewComanda.class);
                         intent.putExtra("NumarComanda", antetObj.getNrCom());
                         startActivity(intent);
                     }
                  });

                  final Button btnAddProd = (Button) findViewById(R.id.butonAddProdusSpiner);
                  final EditText valF1 = (EditText) findViewById(R.id.spinervaloareproduse);
                  btnAddProd.setOnClickListener(new View.OnClickListener(){
                     @Override
                     public void onClick(View v) {
                         EditText cantF = (EditText) findViewById(R.id.spinerfieldcantitate);
                         cantF.setEnabled(true);
                         cantF.setText("");
                         btnSave.setEnabled(true);
                         btnAddProd.setEnabled(false);
                         valF1.setText("");
                     }
                  });
          }

    private void doSaveALl() {
        DBhelper dBhelper = new DBhelper(getBaseContext());

        if(antetObj!=null){
            ComenziEmail comenzi = new ComenziEmail();
            comenzi.setNrComanda(antetObj.getNrCom());
            comenzi.setDataComanda(antetObj.getDataCom());
            comenzi.setStare(0);

            dBhelper.insertEmailComanda(comenzi);
            dBhelper.closeDB();
        }
        //java.util.Date date = new java.util.Date();
        //Toast.makeText(getBaseContext(), " format data " + date.getTime(), Toast.LENGTH_LONG).show();
    }


    public static class CustomSpinnerAdapter extends ArrayAdapter<CoduriProduse> implements SpinnerAdapter{
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
                      TextView pretprodus = (TextView) convertView.findViewById(R.id.spiner_pret_produs);

                      denprodus.setText(produse.getPr_denumire());
                      pretprodus.setText(produse.getPret().toString());

                      return convertView;
                  }
          } // end custom class



          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  CoduriProduse itemSelected;

                  position_selected = position;

                  itemSelected = (CoduriProduse)parent.getItemAtPosition(position);
                  idProdus = itemSelected.getID();
                  EditText txtDen = (EditText) findViewById(R.id.SpinnerDenumireProdus);
                  EditText txtPret = (EditText) findViewById(R.id.SpinnerPretProduse);
                  EditText cantF = (EditText) findViewById(R.id.spinerfieldcantitate);
                  EditText valF = (EditText) findViewById(R.id.spinervaloareproduse);

                  txtDen.setText(itemSelected.getPr_denumire());
                  txtPret.setText(itemSelected.getPret().toString());

                  cantF.setText("");
                  valF.setText("");
                  valF.setFocusable(false);
                  txtPret.setFocusable(false);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
               return;
          }

    /**
     *   Implementeaza Butonul Salveaza comanda
      */
       public void doSave(){
           EditText cantF = (EditText) findViewById(R.id.spinerfieldcantitate);
           EditText pretF = (EditText) findViewById(R.id.SpinnerPretProduse);
           EditText valF = (EditText) findViewById(R.id.spinervaloareproduse);

           if(!checkDuplicates(position_selected)) {
                       btnAdaugaProdus.setEnabled(true);
                       btnSave.setEnabled(false);
                       cantF.setEnabled(false);

                       int cant = Integer.parseInt(cantF.getText().toString().trim());
                       double pret = Double.parseDouble(pretF.getText().toString().trim());
                       double val = cant * pret;

                       valF.setText(String.valueOf(douaZeci(val)));

                       DBhelper dBhelper = new DBhelper(getBaseContext());
                       dBhelper.addAntet(antetObj);
                       //detaliiObj.setLinie(insertLinie.get(insertLinie.size() - 1).getLinie() + 1);

                       if (detaliiObj != null && antetObj != null) {
                               detaliiObj.setNrcomanda(antetObj.getNrCom());
                               detaliiObj.setCodprodus(idProdus);
                               detaliiObj.setCantitate(cant);
                               detaliiObj.setPret(pret);
                               detaliiObj.setValoare(douaZeci(val));
                               if (nrlinie > 1) {
                                   detaliiObj.setLinie(nrlinie);
                               }
                               dBhelper.addDetalii(detaliiObj);
                       }
                       nrlinie++;
           }else {
               Toast.makeText(getBaseContext(), " Ati mai introdus acest produs!!! ", Toast.LENGTH_LONG).show();
               return;
           }
       }

    public boolean checkDuplicates(int position){
            if(lastItemSelected.isEmpty()) {
                //Toast.makeText(getBaseContext(), " Nu ai selectat niimic ", Toast.LENGTH_LONG).show();
                lastItemSelected.add(position);
                listcount = 1;
                return false;
            }else {
                if(lastItemSelected.contains(position)==false){
                    lastItemSelected.add(position);
                    Toast.makeText(getBaseContext(), " Am introdus "+ position, Toast.LENGTH_SHORT).show();
                    listcount++;
                    return false;
                }else{
                    Toast.makeText(getBaseContext(), " Nu incercati sa adaugati acelasi produs de doua ori!!! ", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
    }


    public double douaZeci(double d)
    {
            BigDecimal bd = new BigDecimal(d);
            bd=bd.setScale(2,BigDecimal.ROUND_DOWN);
            return bd.doubleValue();
    }

}
