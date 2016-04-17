package com.example.kp.brutarievascau;

/**
 * Created by kp on 25/01/16.
 */
public class AntetComanda {
    public AntetComanda()
    {
        //empty
    }

    long idCom;
    int nrCom;
    int clientCom;
    String dataCom;

    public void setIdCom(int idcom)          { this.idCom = idcom; }
    public void setNrCom(int nrcom)          { this.nrCom = nrcom; }
    public void setClientCom(int idclient)   { this.clientCom = idclient; }
    public void setDataCom(String d)         { this.dataCom = d; }

    public int getNrCom()       { return this.nrCom; }
    public int getClientCom()   { return this.clientCom; }
    public long getIdCom()      { return this.idCom; }
    public String getDataCom()  { return this.dataCom; }
}
