package com.example.kp.brutarievascau;

import java.math.BigDecimal;

/**
 * Created by kp on 27/07/16.
 */
public class Aviz {
    public Aviz() {
    }

    String denumire="";
    String cod_produs="";
    double pret;
    int cantitate;
    double valoare;
    double tva;
    double pret_vanzare;
    double pretnou;
    double valnou;

    public void setCodprodus(String CodProdus)   { this.cod_produs = CodProdus; }
    public void setDenumireProdus(String DenumireProdus) {this.denumire = DenumireProdus;}
    public void setCantitate(int Cantitate)   { this.cantitate = Cantitate; }
    public void setPret(double Pret)     { this.pret = Pret; }
    public void setValoare(double Valoare)  { this.valoare = Valoare; }
    public void setTva(double Tva){
        this.tva = douaZeci(Tva);
    }
    public void setPretVanzare(double PretVanzare)     { this.pret_vanzare = douaZeci(PretVanzare); }
    public void setPretNou(double PretNou)     { this.pretnou = douaZeci(PretNou); }
    public void setValnou(double ValoaneNoua)     { this.valnou = douaZeci(ValoaneNoua); }


    public String getCodprodus()   { return this.cod_produs; }
    public String getDenProdus() { return this.denumire;}
    public int getCantitate()   { return this.cantitate; }
    public double getPret()     { return this.pret; }
    public double getValoare()  { return this.valoare; }
    public double getTva()      { return this.tva; }
    public double getPretVanzare() { return this.pret_vanzare; }
    public double getPretNou() {return this.pretnou; }
    public double getValoareNoua() { return this.valnou;}

    public double douaZeci(double d)
    {
        BigDecimal bd = new BigDecimal(d);
        bd=bd.setScale(2,BigDecimal.ROUND_DOWN);
        return bd.doubleValue();
    }
}
