package com.example.kp.brutarievascau;

/**
 * Created by kp on 07/02/16.
 */
public class ComandaJoin {
    public ComandaJoin() {
    }

int nr_comanda;
String client;
double valoare;
String data_com;
double valTotal;
int idclient;

public void set_nrcomanda(Integer nrcom){this.nr_comanda = nrcom;}
public void set_client(String client_){this.client = client_;}
public void set_valoare(double val){this.valoare = val;}
public void set_data(String datacom){this.data_com = datacom;}
public void setValTotal(double valtotal){this.valTotal = valtotal;}
public void set_id_client(int idClient){this.idclient = idClient;}

public int get_nrCom(){return this.nr_comanda;}
public String get_client(){return this.client;}
public double get_valoare(){return this.valoare;}
public String get_data(){return this.data_com;}
public double get_valTotal(){ return this.valTotal;}
public int get_id_client(){return this.idclient;}


}


