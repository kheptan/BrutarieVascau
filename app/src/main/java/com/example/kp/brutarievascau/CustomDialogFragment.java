package com.example.kp.brutarievascau;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.Button;


public class CustomDialogFragment extends DialogFragment {
         CoduriProduse codprodus;
         DeleteDialogFragment callback;
         DBhelper db = new DBhelper(getActivity().getBaseContext());

         long id;


    public CustomDialogFragment() {
       //empty constructor;
    }


    public static CustomDialogFragment newInstance(long id){
        CustomDialogFragment f = new CustomDialogFragment();

            Bundle args = new Bundle();
            args.putLong("ID_MAG",id);
            f.setArguments(args);
            return f;
    }


    public interface DeleteDialogFragment{
             void onDelProdusItem(long id);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Stergere Produs")
                .setMessage("Doriti sa stergeti produsul:  ?")
                .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //callback.onDelProdusItem(codprodus);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id=getArguments().getLong("ID_MAG",0);

        try{
            callback = (DeleteDialogFragment) getTargetFragment();
        }catch (ClassCastException ce){
            throw new ClassCastException("Calling Fragment must implement DeleteDialogInterface");
        }
    }
}


