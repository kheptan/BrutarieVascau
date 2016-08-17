package com.example.kp.brutarievascau;

/**
 * Created by kp on 10/02/16.
 */
public interface onEditDeleteDialogDetalii {
    boolean onEdit(int nrcomanda,int nrlinie,int cant,int codpr,double pretprodus,int codVechi);
    void onDelete(int nrcomanda,int nrlinie);
    void onAdd(int nrcomanda,int cant,int codpr,double pretprodus);
}
