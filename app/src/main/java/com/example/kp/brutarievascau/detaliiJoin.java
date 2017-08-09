package com.example.kp.brutarievascau;

import java.math.BigDecimal;

/**
 * Created by kp on 14/02/16.
 */
public class detaliiJoin {
    public detaliiJoin() {
        //empty constructor()
    }
    int linie;
    int nrcomanda;
    int codprodus;
    String codProdus;
    String denumire_produs;
    int cantitate;
    double pret;
    double valoare;
    double tva;


    public void setLinie(int ln)       { this.linie = ln; }
    public void setNrcomanda(int nc)   { this.nrcomanda = nc; }
    public void setCodprodus(int cp)   { this.codprodus = cp; }
    public void setDenumireProdus(String dp) {this.denumire_produs = dp;}
    public void setCantitate(int ca)   { this.cantitate = ca; }
    public void setPret(double pr)     { this.pret = pr; }
    public void setValoare(double vl)  { this.valoare = vl; }
    public void setTva(double vtva)    { this.tva = vtva; }
    public void setCodProdus(String cprodus) { this.codProdus = cprodus;}

    public int getLinie()       { return this.linie; }
    public int getNrcomanda()   { return this.nrcomanda; }
    public int getCodprodus()   { return this.codprodus; }
    public String getDenProdus() { return this.denumire_produs;}
    public int getCantitate()   { return this.cantitate; }
    public double getPret()     { return this.pret; }
    public double getValoare()  { return this.valoare; }
    public double getTva()      { return this.tva; }
    public String getCodProdus() { return this.codProdus; }

}

