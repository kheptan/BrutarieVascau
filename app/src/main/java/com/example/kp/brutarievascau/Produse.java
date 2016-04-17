package com.example.kp.brutarievascau;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;



import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;


public class Produse extends AppCompatActivity
        implements FragmentSwitcher {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_produse);

            TopProduse top = new TopProduse();
            Bundle bundle = new Bundle();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.AdaugaProduse,top,"test123");
            fragmentTransaction.commit();
    }


    /**
     *  Fragment 1  Lista Produse
     * */
    public static class TopProduse extends ListFragment implements CustomDialogFragment.DeleteDialogFragment {
              ProduseAdaptor produseAdaptor;
              List<CoduriProduse> listaproduse;
              CoduriProduse obProduse;

              @Override
              public void onActivityCreated(Bundle savedInstanceState) {
                   super.onActivityCreated(savedInstanceState);

                   DBhelper dBhelper = new DBhelper(getActivity());
                   dBhelper.openDB();
                   listaproduse = dBhelper.listAllProducts();
                   produseAdaptor = new ProduseAdaptor(getActivity(),R.layout.produse_view_item,listaproduse);
                   produseAdaptor.notifyDataSetChanged();
                   setListAdapter(produseAdaptor);

                   /** IMPLEMENTEAZA ONLONG CLICK */
                   getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                          @Override
                          public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                obProduse = (CoduriProduse) getListView().getItemAtPosition(position);
                                dialogDelete(obProduse.getID());
                                return true;
                          }
                   });

                   dBhelper.closeDB();
              }

              @Override
              public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                   return inflater.inflate(R.layout.custom_listfragment,container,false);
              }

             @Override
             public void onListItemClick(ListView l, View v, int position, long id) {
                   super.onListItemClick(l, v, position, id);

             }

             /**
             * Implementeaza Interfata
             * @param id
             */
             @Override
             public void onDelProdusItem(long id) {
                 Toast.makeText(getActivity(), "Sterg Produs - > " , Toast.LENGTH_SHORT).show();
             }

             public void dialogDelete(long idProd){
                  FragmentManager fm = getActivity().getSupportFragmentManager();
                  CustomDialogFragment cdf = CustomDialogFragment.newInstance(idProd);
                  cdf.setTargetFragment(this,0);
                  cdf.show(fm,"CasutaDialog");
             }


    }



    /**
     * Fragment 2  Adauga Produs Nou
     * */
     public static class FragmentAddProduse extends Fragment implements View.OnClickListener{
             FragmentSwitcher fragmentSwitcher;

             public double douaZeci(double d)
              {
                BigDecimal bd = new BigDecimal(d);
                bd=bd.setScale(2,BigDecimal.ROUND_DOWN);
                return bd.doubleValue();
              }

             @Override
             public void onActivityCreated(Bundle savedInstanceState) {
                   super.onActivityCreated(savedInstanceState);
             }

             @Override
             public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                  View view = inflater.inflate(R.layout.custom_listfragment_produse,container,false);
                  Button btnTemp = (Button) view.findViewById(R.id.buttonAddProdusebtn);
                  btnTemp.setOnClickListener(this);
                  return view;
             }

             @Override
             public void onAttach(Context context) {
                  super.onAttach(context);
                  if(getActivity() instanceof Context) {
                         fragmentSwitcher = (FragmentSwitcher) (Activity) context;
                  }
             }

            @Override
            public void onDetach() {
                  super.onDetach();
                  fragmentSwitcher=null;
            }

            @Override
            public void onClick(View v) {
                  if(v.getId() == R.id.buttonAddProdusebtn){
                        View getButtonview = getActivity().findViewById(R.id.buttonAddProduse);
                        CoduriProduse cpr= new CoduriProduse();
                        DBhelper produsDB = new DBhelper(getActivity());
                        double d;

                        EditText editCodPr = (EditText)getActivity().findViewById(R.id.codProdusFrg);
                        EditText editDenumire = (EditText)getActivity().findViewById(R.id.denumireProdusFrg);
                        EditText editPret = (EditText) getActivity().findViewById(R.id.PretProdusFrg);
                        d=Double.parseDouble(editPret.getText().toString().trim());

                        cpr.setPr_codprodus(Integer.parseInt(editCodPr.getText().toString().trim()));
                        cpr.setPr_denumire(editDenumire.getText().toString().trim());
                        cpr.setPret(douaZeci(d));
                      Toast.makeText(getActivity(), " "+douaZeci(d) , Toast.LENGTH_SHORT).show();
                        produsDB.addProdus(cpr);
                        if(fragmentSwitcher == null) {
                            Toast.makeText(getActivity(), "FragmentSwitcher este null" , Toast.LENGTH_SHORT).show();
                        }else {
                            fragmentSwitcher.activeazaButon(getButtonview);
                        }
                  }
            }
    }



    /**
     *  Implementeaza interfata ReplaceFragment
     */
    public void ReplaceFragment(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(view.isEnabled()) {
            FragmentAddProduse frg = new FragmentAddProduse();
            fragmentTransaction.replace(R.id.AdaugaProduse, frg);
            fragmentTransaction.commit();
            view.setEnabled(false);
        }else{
            TopProduse frgtop = new TopProduse();
            fragmentTransaction.replace(R.id.AdaugaProduse,frgtop);
            fragmentTransaction.commit();
            view.setEnabled(true);
        }
    }

    @Override
    public void activeazaButon(View view) {
         ReplaceFragment(view);
    }



    /**
     * Metoda din interior ListFragment pt OnLongClick
     */

}
