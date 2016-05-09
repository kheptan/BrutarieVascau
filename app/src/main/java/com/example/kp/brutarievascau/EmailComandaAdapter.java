package com.example.kp.brutarievascau;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kp on 01/05/16.
 */
public class EmailComandaAdapter extends ArrayAdapter<ComenziEmail> {
    private Context myContext;
    private int res;
    int[] tabListPosition;

    public EmailComandaAdapter(Context context, int resource, List<ComenziEmail> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.res = resource;
        this.tabListPosition = new int[objects.size()];
        Arrays.fill(this.tabListPosition,0);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res,parent,false);
        }

        ComenziEmail comenziEmail = getItem(position);

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        final TextView textView = (TextView) convertView.findViewById(R.id.EmailComandaListView);
        textView.setText("Comanda: " +comenziEmail.getNrComanda());
        //if(textView!=null && comenziEmail!=null){
        //Toast.makeText(myContext, "format data " + comenziEmail.getDataComanda(), Toast.LENGTH_LONG).show();
        //}

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if(cb.isChecked()){
                    tabListPosition[position] = 1;
                }else {
                    tabListPosition[position] = 0;
                }
                //Toast.makeText(myContext, "Pozitia este: " + position +" Tab este acum: "+tabListPosition[position], Toast.LENGTH_SHORT).show();
            }
        });

        if(comenziEmail.getStare()==0){
            checkBox.setChecked(false);
        }else {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }

        return convertView;
    }



    //private View getCustomView(int poz,View convert, ViewGroup group){

    //}
}
