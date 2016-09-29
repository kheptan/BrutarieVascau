package com.example.kp.brutarievascau;

/**
 * Created by kp on 27/09/16.
 */

public interface onEditDeleteFinalDialog {
    boolean onEdit(long iData,int iCantitate,String iCodPRodus,String iDenumire,String iNewProdus);
    void onDelete(long iData,String iCodProdus);
}
