package com.example.kp.brutarievascau;

/**
 * Created by kp on 25/01/16.
 */
public class DetaliiComanda {
    public DetaliiComanda() {
        //empty constructor
    }

    int linie;
    int nrcomanda;
    int codprodus;
    int cantitate;
    double pret;
    double valoare;
    double tva;

    public void setLinie(int ln)       { this.linie = ln; }
    public void setNrcomanda(int nc)   { this.nrcomanda = nc; }
    public void setCodprodus(int cp)   { this.codprodus = cp; }
    public void setCantitate(int ca)   { this.cantitate = ca; }
    public void setPret(double pr)     { this.pret = pr; }
    public void setValoare(double vl)  { this.valoare = vl; }
    public void setTva(double tv)      { this.tva = tv; }

    public int getLinie()       { return this.linie; }
    public int getNrcomanda()   { return this.nrcomanda; }
    public int getCodprodus()   { return this.codprodus; }
    public int getCantitate()   { return this.cantitate; }
    public double getPret()     { return this.pret; }
    public double getValoare()  { return this.valoare; }
    public double getTva()      { return this.tva; }

}
