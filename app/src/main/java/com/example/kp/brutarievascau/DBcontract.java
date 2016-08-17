package com.example.kp.brutarievascau;

/**
 * Created by kp on 06/12/15.
 */
public class DBcontract {

    public DBcontract() {
    }

     public final static  String  TABLE_PRODUSE = "produse";
     public final static  String  TABLE_ANTET_COMANDA = "antet_comanda";
     public final static  String  TABLE_DETALII_COMANDA = "detalii_comanda";
     public final static  String  TABLE_MAGAZINE = "magazine";
     public final static  String  TABLE_USER = "user";
     public final static  String  TABLE_EMAIL_COMENZI = "email_comenzi";
     // public key id //////////////////////////////////////////////////////////////

     public final static  String  KEY_ID = "_id";


     // def. elemente tablou produse ///////////////////////////////////////////////

     public final static String KEY_PRODUSE_NUME = "denumire";
     public final static String KEY_PRODUSE_COD_PRODUS = "cod_produs";
     public final static String KEY_PRODUSE_PRET = "pret";


     // def. campuri antet comanda /////////////////////////////////////////////////

     public final static String KEY_ANTET_COMANDA_NR_COMANDA = "nr_com";
     public final static String KEY_ANTET_COMANDA_ID_CLIENT = "id_client";
     public final static String KEY_ANTET_COMANDA_DATA_COMANDA = "data_com";
     public final static String KEY_ANTET_COMANDA_TVA = "tva";
     public final static String KEY_ANTET_COMANDA_GREUTATE = "greutate";


     // def. campuri detalii comanda ///////////////////////////////////////////////

     public final static String KEY_DETALII_COMANDA_NR_LINIE = "nr_linie";
     public final static String KEY_DETALII_COMANDA_NR_COMANDA = "nr_com";
     public final static String KEY_DETALII_COMANDA_ID_PRODUS = "id_produs";
     public final static String KEY_DETALII_COMANDA_CANTITATE = "cantitate";
     public final static String KEY_DETALII_COMANDA_PRET = "pret";
     public final static String KEY_DETALII_COMANDA_VALOARE = "valoare";
     public final static String KEY_DETALII_COMANDA_TVA = "tva";

     // def. campuri email_comenzi
     public final static String KEY_EMAIL_COMENZI_DATA_COMANDA = "data_comanda";
     public final static String KEY_EMAIL_COMENZI_NR_COMANDA = "nr_comanda";
     public final static String KEY_EMAIL_COMENZI_STARE_COMANDA = "stare_comanda";
     public final static String KEY_EMAIL_COMENZI_ID_CLIENT = "id_client";
     // def campuri magazin  ///////////////////////////////////////////////////////

     public final static String KEY_MAGAZINE_NUME = "nume";
     public final static String KEY_MAGAZINE_INFO = "info";
     public final static String KEY_MAGAZINE_CIF = "cif";
     public final static String KEY_MAGAZINE_NR_REGCOM = "nr_regcom";
     public final static String KEY_MAGAZINE_ADRESA = "adresa";
     public final static String KEY_MAGAZINE_IBAN = "iban";


     // def user ////////////////////////////////////////////////////////////////

     public final static String KEY_USER_NUME = "nume";
     public final static String KEY_USER_PAROLA = "parola";
     public final static String KEY_USER_MASINA = "masina";
     public final static String KEY_USER_STATUS = "status";


    // Create Tables ///////////////////////////////////////////////////////////

    public final static String CREATE_TABLE_PRODUSE =
            "CREATE TABLE " +
                    "IF NOT EXISTS " + TABLE_PRODUSE +
                    " ( " +
                          KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "+
                          KEY_PRODUSE_COD_PRODUS + " TEXT, " +
                          KEY_PRODUSE_NUME + " TEXT, " +
                          KEY_PRODUSE_PRET + " REAL "+" )";

    public final static String CREATE_TABLE_MAGAZINE =
            "CREATE TABLE " +
                    "IF NOT EXISTS " + TABLE_MAGAZINE +
                    " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "+
                    KEY_MAGAZINE_NUME + " TEXT, " +
                    KEY_MAGAZINE_ADRESA + " TEXT, " +
                    KEY_MAGAZINE_CIF + " TEXT, " +
                    KEY_MAGAZINE_IBAN + " TEXT, " +
                    KEY_MAGAZINE_NR_REGCOM + " TEXT, " +
                    KEY_MAGAZINE_INFO + " TEXT "+")";


    public final static String CREATE_TABLE_ANTET_COMANDA =
            "CREATE TABLE " +
                     "IF NOT EXISTS " + TABLE_ANTET_COMANDA +
                     " (" +
                          KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "+
                          KEY_ANTET_COMANDA_DATA_COMANDA + " INTEGER, " +
                          KEY_ANTET_COMANDA_ID_CLIENT + " INTEGER, " +
                          KEY_ANTET_COMANDA_TVA + " REAL, " +
                          KEY_ANTET_COMANDA_GREUTATE + " INTEGER, " +
                          KEY_ANTET_COMANDA_NR_COMANDA + " INTEGER NOT NULL UNIQUE, " +
                          " FOREIGN KEY ("+ KEY_ANTET_COMANDA_ID_CLIENT + ")" +
                          " REFERENCES "+ TABLE_MAGAZINE + " (" + KEY_ID + "))";


    public final static String CREATE_TABLE_EMAIL_COMENZI =
            "CREATE TABLE " +
                    "IF NOT EXISTS " + TABLE_EMAIL_COMENZI +
                    " (" +
                          KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
                          KEY_EMAIL_COMENZI_DATA_COMANDA + " INTEGER, " +
                          KEY_EMAIL_COMENZI_NR_COMANDA + " INTEGER, "+
                          KEY_EMAIL_COMENZI_ID_CLIENT + " INTEGER, "+
                          KEY_EMAIL_COMENZI_STARE_COMANDA + " INTEGER )";


    public final static String CREATE_TABLE_DETALII_COMANDA =
            "CREATE TABLE " +
                    "IF NOT EXISTS " + TABLE_DETALII_COMANDA +
                    " ( " +
                          KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "+
                          KEY_DETALII_COMANDA_NR_LINIE + " INTEGER, " +
                          KEY_DETALII_COMANDA_NR_COMANDA + " INTEGER, " +
                          KEY_DETALII_COMANDA_ID_PRODUS + " INTEGER, " +
                          KEY_DETALII_COMANDA_CANTITATE + " INTEGER, " +
                          KEY_DETALII_COMANDA_PRET + " REAL, " +
                          KEY_DETALII_COMANDA_VALOARE + " REAL, " +
                          KEY_DETALII_COMANDA_TVA + " REAL, " +
                          " FOREIGN KEY ("+ KEY_DETALII_COMANDA_NR_COMANDA + ") " +
                          " REFERENCES "+ TABLE_ANTET_COMANDA + " (" + KEY_ANTET_COMANDA_NR_COMANDA + ") ON DELETE CASCADE ON UPDATE CASCADE, " +
                          " FOREIGN KEY ("+ KEY_DETALII_COMANDA_ID_PRODUS + ") " +
                          " REFERENCES "+ TABLE_PRODUSE + " (" + KEY_ID + "))";



    public final static String CREATE_TABLE_USER =
            "CREATE TABLE " +
                    "IF NOT EXISTS " + TABLE_USER +
                    " (" +
                         KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, "+
                         KEY_USER_NUME + " TEXT, " +
                         KEY_USER_PAROLA + " TEXT, " +
                         KEY_USER_MASINA + " TEXT, " +
                         KEY_USER_STATUS + " INTEGER "+")";

}
