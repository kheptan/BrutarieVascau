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
import android.widget.Toast;

/**
 * Created by kp on 01/01/16.
 */
public class InfoDialog extends DialogFragment {

    int IDClient;
    String NAMEClient;
    int POZClient;
    public DialogListenerFunctions dlgLstener;


    public static InfoDialog newInstance(int id,String name,int poz){
        InfoDialog f = new InfoDialog();

        Bundle args = new Bundle();
        args.putInt("ID_MAG",id);
        args.putString("NAME",name);
        args.putInt("POSITION",poz);
        f.setArguments(args);
        return f;
    }

    public InfoDialog (){
     //empty constructor;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IDClient = getArguments().getInt("ID_MAG");
        NAMEClient = getArguments().getString("NAME");
        POZClient = getArguments().getInt("POSITION");
    }

    /**
     *  CREAZA INTERFATA DIALOG
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Alerta Stergere Client !!!")
                .setMessage("Stergeti  Clientul: " + NAMEClient + " ?")
                .setPositiveButton("Sterge",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Private Method for Delete
                                dlgLstener.onDialogPositiveClick(InfoDialog.this,IDClient,POZClient);
                                //Toast.makeText(getActivity().getBaseContext(), "Am apasat Ok si voi sterge Clientul :  ID= " + delCL, Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("Renunta",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                                dialog.cancel();
                            }
                        }
                )
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() instanceof Context) {
            Activity activity = (Activity) context;
            dlgLstener = (DialogListenerFunctions) activity;
        } else{
            Toast.makeText(getActivity().getBaseContext(), "ceva e gresit" , Toast.LENGTH_SHORT).show();
        }


    }
}
