package com.example.kp.brutarievascau;

/**
 * Created by kp on 10/01/16.
 */
public class CoduriProduse {
    public CoduriProduse() {
        //Empty
    }

    int produs_id;
    String pr_denumire;
    String pr_codprodus;
    double pret;
    // Set Fields ////////////////////////////
    public void setPr_denumire(String denumire) {this.pr_denumire = denumire.toString();}
    public void setPr_id(int id) {this.produs_id=id;}
    public void setPr_codprodus(String codprodus) {this.pr_codprodus = codprodus.toString();}
    public void setPret(double pretprodus) {this.pret = pretprodus;}

    // Get Fields //////////////////////////////
    public int getID() {return this.produs_id;}
    public String getPr_denumire() {return this.pr_denumire.toString();}
    public String getPr_codprodus() { return this.pr_codprodus.toString();}
    public Double getPret(){ return this.pret;}
}
