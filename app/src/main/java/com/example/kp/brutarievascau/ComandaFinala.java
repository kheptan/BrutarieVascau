package com.example.kp.brutarievascau;

/**
 * Created by kp on 27/09/16.
 */

public class ComandaFinala {

    long   dataFinala;
    String denumireProdus;
    String codProdus;
    int    cantitate;


    public ComandaFinala() {
    }

    public void setData(long pData){
        this.dataFinala = pData;
    }
    public void setDenumire(String pDenumire){
        this.denumireProdus = pDenumire;
    }
    public void setCantitate(int pCant){
        this.cantitate = pCant;
    }
    public void setCodProdus(String pCodProdus) { this.codProdus = pCodProdus;}

    public long getData(){
        return this.dataFinala;
    }
    public String getDenumire(){
        return this.denumireProdus;
    }
    public int getCantitate(){
        return this.cantitate;
    }
    public String getCodProdus(){ return this.codProdus; }

}
