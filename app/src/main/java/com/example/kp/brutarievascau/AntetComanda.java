package com.example.kp.brutarievascau;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    long dataCom;

    public void setIdCom(int idcom)          { this.idCom = idcom; }
    public void setNrCom(int nrcom)          { this.nrCom = nrcom; }
    public void setClientCom(int idclient)   { this.clientCom = idclient; }
    public void setDataCom(long d)         { this.dataCom = d; }

    public int getNrCom()       { return this.nrCom; }
    public int getClientCom()   { return this.clientCom; }
    public long getIdCom()      { return this.idCom; }
    public long getDataCom(){
        //Date date = new Date();
        //date.setTime(dataCom);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MM-y");
        //return simpleDateFormat.format(date);
        return this.dataCom;
    }
}
