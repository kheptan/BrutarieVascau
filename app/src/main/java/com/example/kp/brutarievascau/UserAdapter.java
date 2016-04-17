package com.example.kp.brutarievascau;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kp on 29/12/15.
 */
public class UserAdapter extends ArrayAdapter<Client> {
    private Context myContext;
    private int res;
    private List<Client> lstClients;

    public UserAdapter(Context context, int resource, List<Client> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.res = resource;
        this.lstClients = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res,parent,false);
        }

        Client cl = getItem(position);

        //TextView tId = (TextView) convertView.findViewById(R.id.textViewID);
        TextView tName = (TextView) convertView.findViewById(R.id.textViewName);

        //tId.setText("ID ");
        tName.setText(cl.getNume());

        return convertView ;
    }

}
