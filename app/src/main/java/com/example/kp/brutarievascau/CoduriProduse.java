package com.example.kp.brutarievascau;

/**
 * Created by kp on 10/01/16.
 */
public class CoduriProduse {
    public CoduriProduse() {
        //Empty
    }

    long produs_id;
    String pr_denumire;
    int pr_codprodus;
    double pret;
    // Set Fields ////////////////////////////
    public void setPr_denumire(String denumire) {this.pr_denumire = denumire.toString();}
    public void setPr_id(long id) {this.produs_id=id;}
    public void setPr_codprodus(int codprodus) {this.pr_codprodus = codprodus;}
    public void setPret(double pretprodus) {this.pret = pretprodus;}

    // Get Fields //////////////////////////////
    public long getID() {return this.produs_id;}
    public String getPr_denumire() {return this.pr_denumire.toString();}
    public int getPr_codprodus() { return this.pr_codprodus;}
    public Double getPret(){ return this.pret;}
}
