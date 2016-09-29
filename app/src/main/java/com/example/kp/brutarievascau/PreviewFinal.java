package com.example.kp.brutarievascau;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import java.util.Calendar;
import java.util.List;

public class PreviewFinal extends AppCompatActivity implements onEditDeleteFinalDialog{
    List<ComandaFinala> listaFinala;
    ListView listView;
    CustomEmailAdapter customEmailAdapter;
    long dataComanda;


    @Override
    public void onDelete(long iData, String iCodProdus) {
        DBhelper db = new DBhelper(getBaseContext());
        db.openDB();
        //Toast.makeText(getBaseContext(), "Acum sterg " + iCodProdus, Toast.LENGTH_LONG).show();
        if(db.deleteFinalCOmanda(iData,iCodProdus)>0){
            listaFinala.clear();
            listaFinala.addAll(db.listAllFinalComenzi(iData));
            //customEmailAdapter = new CustomEmailAdapter(getBaseContext(),R.layout.custom_produse_finale,listaFinala);
            customEmailAdapter.notifyDataSetChanged();
            listView.setAdapter(customEmailAdapter);
        }else{
            Toast.makeText(getBaseContext(), "nu am sters nimic", Toast.LENGTH_LONG).show();
        }
        db.closeDB();
    }

    @Override
    public boolean onEdit(long iData, int iCantitate, String iCodPRodus, String iDenumire,String iNewProdus) {
        DBhelper db = new DBhelper(getBaseContext());
        db.openDB();
        //Toast.makeText(getBaseContext(), "select : " + iCantitate, Toast.LENGTH_LONG).show();

            if(checkDuplicates(iCantitate,iData,iNewProdus,iCodPRodus)){
                Toast.makeText(getBaseContext(), "Acest produs este deja introdus!!!", Toast.LENGTH_LONG).show();
                db.closeDB();
                return true;
            }else{
                if(db.updateFinalPreview(iData,iNewProdus,iCodPRodus,iDenumire,iCantitate)>0){
                    listaFinala.clear();
                    listaFinala.addAll(db.listAllFinalComenzi(dataComanda));
                    //customEmailAdapter = new CustomEmailAdapter(getBaseContext(),R.layout.custom_produse_finale,listaFinala);
                    customEmailAdapter.notifyDataSetChanged();
                    listView.setAdapter(customEmailAdapter);
                    db.closeDB();
                    return false;
                }else{
                    Toast.makeText(getBaseContext(), "Nu am updatat!!!", Toast.LENGTH_LONG).show();
                    return true;
                }
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_final);

        dataComanda = getIntent().getLongExtra("DATACOMANDA",0);

        /**Open DB */
        DBhelper db = new DBhelper(getBaseContext());
        db.openDB();

        //Calendar calendar = Calendar.getInstance();
        listaFinala = db.listAllFinalComenzi(dataComanda);
        listView = (ListView) findViewById(R.id.FinalListView);

        if(!listaFinala.isEmpty()){
              if(listView !=null){
                  listaFinala.clear();
                  listaFinala.addAll(db.listAllFinalComenzi(dataComanda));
                  customEmailAdapter = new CustomEmailAdapter(getBaseContext(),R.layout.custom_produse_finale,listaFinala);
                  customEmailAdapter.notifyDataSetChanged();
                  listView.setAdapter(customEmailAdapter);
              }

              listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(getBaseContext(),"You've got an event la pozitia "+position,Toast.LENGTH_SHORT).show();
                        //Log.i( LOG_TAG, "Item click");
                        ComandaFinala comandaFinala = (ComandaFinala) parent.getItemAtPosition(position);
                        if(comandaFinala!=null) {
                            DialogFragment df = FinalEditDialogFragment.newInstance(dataComanda,comandaFinala.getCodProdus(),comandaFinala.getDenumire(),comandaFinala.getCantitate());
                            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            df.show(ft, "DiagEdit");
                        }
                    }
              });

              listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ComandaFinala comandaFinala = (ComandaFinala) parent.getItemAtPosition(position);
                        if(comandaFinala!=null) {
                            DialogFragment df = DeleteDialogFragmentFinal.newInstance(comandaFinala.getData(),comandaFinala.getCodProdus());
                            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            df.show(ft, "DiagDelete");
                        }
                        return true;
                    }
              });

        }

        db.closeDB();
    }


    public static class DeleteDialogFragmentFinal extends DialogFragment{
         long dataFinala;
         String codProdus;

        onEditDeleteFinalDialog callback;

        public DeleteDialogFragmentFinal() {
        }

        /** Retunreaza o noua fereastra cu argument*/
        static DeleteDialogFragmentFinal newInstance(long pData,String pCodProdus){
            DeleteDialogFragmentFinal f = new DeleteDialogFragmentFinal();
            Bundle args = new Bundle();
            args.putLong("DATA_COMANDA", pData);
            args.putString("COD_PRODUS",pCodProdus);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            dataFinala = getArguments().getLong("DATA_COMANDA");
            codProdus = getArguments().getString("COD_PRODUS");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new android.app.AlertDialog.Builder(getActivity())
                    .setTitle("Stergere Produs")
                    .setMessage("Doriti sa stergeti acest produs din lista ?")
                    .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                callback.onDelete(dataFinala,codProdus);
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
                callback = (onEditDeleteFinalDialog) activity;
            }catch (ClassCastException ce){
                throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
            }
        }
    }

    public static class FinalEditDialogFragment extends DialogFragment {

        int cantitate=0;
        String cod_produs="";
        String denumire="";
        int item =0;
        String selected_produs="";
        long data_Comanda;
        final String[] articole = {"FRANZELA ALBA 2KG","ROTUNDA ALBA 2KG","FRANZELA ALBA 1KG","ROTUNDA ALBA 1KG","FRANZELA ALBA 0.8KG","ROTUNDA ALBA 0.8KG","PAINE IMPATURATA 0.8KG"};

        onEditDeleteFinalDialog callback;

        /** Empty constructor *****************************/
        public FinalEditDialogFragment() {

        }

        static FinalEditDialogFragment newInstance(long pData,String pCodProdus, String pDenumire, int pCantitate){
            FinalEditDialogFragment f = new FinalEditDialogFragment();
            Bundle args = new Bundle();
            args.putString("CODPRODUS",pCodProdus);
            args.putString("DENUMIRE", pDenumire);
            args.putInt("CANTITATE", pCantitate);
            args.putLong("DATACOM",pData);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);

             cantitate = getArguments().getInt("CANTITATE");
             cod_produs = getArguments().getString("CODPRODUS");
             denumire  = getArguments().getString("DENUMIRE");
             data_Comanda = getArguments().getLong("DATACOM");
        }

        @Override
        public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_edit, container, false);
            final EditText valCant = (EditText) v.findViewById(R.id.editCantitateDialog);
            valCant.setHint(Integer.toString(cantitate));

            DBhelper db = new DBhelper(getContext());
            db.openDB();

            List<CoduriProduse> ProdusePreview = db.listAllProducts();
            for(int i=0;i<articole.length;i++){
                CoduriProduse articolNou = new CoduriProduse();
                articolNou.setPr_codprodus("00abc"+i);
                articolNou.setPr_denumire(articole[i]);
                ProdusePreview.add(articolNou);
            }

            final Spinner spiner= (Spinner) v.findViewById(R.id.spinnerPreviewDialog);
            ComandaNouaProdus.CustomSpinnerAdapter previewSpinnderAdapter = new ComandaNouaProdus.CustomSpinnerAdapter(getContext(),R.layout.custom_spinner_produse,ProdusePreview);

            for(int seli=0;seli<previewSpinnderAdapter.getCount(); seli++){
                if(previewSpinnderAdapter.getItem(seli).getPr_codprodus().contains(cod_produs)){
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

            /** Modificare Selectie produs din interior dialog*/
            spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                CoduriProduse itemSelected;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    itemSelected = (CoduriProduse) parent.getItemAtPosition(position);
                    selected_produs = itemSelected.getPr_codprodus();
                    denumire = itemSelected.getPr_denumire();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    return;
                }
            });

            spiner.setAdapter(previewSpinnderAdapter);
            final Dialog dialog = getDialog();

            /**Implementeaza Buton Save din Dialog */
            Button btnSave = (Button) v.findViewById(R.id.btnSavePreviewDialog);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!valCant.getText().toString().trim().isEmpty()) {

                        if(callback.onEdit(data_Comanda, Integer.parseInt(valCant.getText().toString().trim()),cod_produs,denumire,selected_produs)){

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
                callback = (onEditDeleteFinalDialog) activity;
            }catch (ClassCastException ce){
                throw new ClassCastException("Calling Fragment must implement onEditDelete Interface");
            }
        }
    }

    public boolean checkDuplicates(int paramCanti,long paramDate,String paramProdusSelectat,String paramOldProdus) {
        DBhelper dBhelper = new DBhelper(getBaseContext());
        dBhelper.openDB();

        boolean findit=false;

        List<ComandaFinala> lstFinal = dBhelper.listAllFinalComenzi(paramDate);

        if (lstFinal.isEmpty()) {
            return false;
        } else {
            if (paramCanti != 0) {
                if(paramProdusSelectat.contains(paramOldProdus)){
                    return false;
                }else {
                    for (ComandaFinala item : lstFinal) {
                        if (item.getCodProdus().contains(paramProdusSelectat)) {
                            findit = true;
                        }
                    }
                    return findit;
                }
            } else {
                dBhelper.closeDB();
                return true;
            }
        }

    }

    private class CustomEmailAdapter extends ArrayAdapter<ComandaFinala> {
        private Context myContext;
        private int res;

        public CustomEmailAdapter(Context context, int resource, List<ComandaFinala> objects) {
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

            final ComandaFinala comandaFinala = getItem(position);

            final TextView txtDenum = (TextView) convertView.findViewById(R.id.previewFinalDenumire);
            final TextView txtCant = (TextView) convertView.findViewById(R.id.previewFinalCantitate);

            txtDenum.setMaxLines(2);
            txtDenum.setText(comandaFinala.getDenumire());
            txtCant.setText(Integer.toString(comandaFinala.getCantitate()));

            return convertView;
        }
    }
}
