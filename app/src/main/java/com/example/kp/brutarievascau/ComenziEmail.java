package com.example.kp.brutarievascau;

/**
 * Created by kp on 01/05/16.
 */
public class ComenziEmail {
    int id;
    long data_comanda;
    int nr_comanda;
    int stare_comanda;
    int client_id;

    public ComenziEmail() {
        //empty
    }

    public void setId(int ID) { this.id = ID;}
    public void setDataComanda(long DATA_COMANDA) {this.data_comanda = DATA_COMANDA;}
    public void setNrComanda(int NR_COMANDA) {this.nr_comanda = NR_COMANDA;}
    public void setStare(int STARE){ this.stare_comanda = STARE;}
    public void setClientID(int clientId) {this.client_id = clientId;}

    public int getId(){return this.id;}
    public long getDataComanda() {return this.data_comanda; }
    public int getNrComanda(){ return this.nr_comanda;}
    public int getStare(){return this.stare_comanda;}
    public int getClientID() { return this.client_id;}
}
