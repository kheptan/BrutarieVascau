package com.example.kp.brutarievascau;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ProduseAdaptor extends ArrayAdapter<CoduriProduse> {
         private Context myContext;
         private int res;
         private List<CoduriProduse> lstProduse;

         public ProduseAdaptor(Context context, int resource, List<CoduriProduse> objects) {
                   super(context, resource, objects);
                   this.lstProduse = objects;
                   this.myContext = context;
                   this.res = resource;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
                   if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(res,parent,false);
                   }

                   CoduriProduse produse = getItem(position);

                   //TextView codpr = (TextView) convertView.findViewById(R.id.textCodProduse);
                   TextView denprod = (TextView) convertView.findViewById(R.id.textDenumireProdus);
                   //TextView pretprod = (TextView) convertView.findViewById(R.id.textPret);
                   //codpr.setText(produse.getPr_codprodus());
                   denprod.setText(produse.getPr_denumire());
                   //pretprod.setText(produse.getPret().toString());

                   return convertView;
         }
}
