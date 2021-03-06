package com.example.kp.brutarievascau;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by kp on 01/12/15.
 */
public class DBhelper  {

        /**
         *  Declare Static Fields
         */
        private final static String DATABASE_NAME  = "DBhelper";
        private final static Integer DATABASE_VERSION = 1;
        private final Context localContext;


        DatabaseHelper localDbHelper;
        SQLiteDatabase localSqliteDB;


        /**
         *  CONSTRUCTOR INITIALISATION
         */
        public DBhelper(Context ctx){
            this.localContext = ctx;
        }


        /**
         *   Declare static inner class for Database Initialization
         */
        private static class DatabaseHelper extends SQLiteOpenHelper {
            DatabaseHelper (Context localContext) {
                 super (localContext,DATABASE_NAME,null,DATABASE_VERSION);
            }
            @Override
            public void onCreate(SQLiteDatabase sqldb){
                try {
                    sqldb.execSQL(DBcontract.CREATE_TABLE_PRODUSE);
                    sqldb.execSQL(DBcontract.CREATE_TABLE_MAGAZINE);
                    sqldb.execSQL(DBcontract.CREATE_TABLE_ANTET_COMANDA);
                    sqldb.execSQL(DBcontract.CREATE_TABLE_DETALII_COMANDA);
                    sqldb.execSQL(DBcontract.CREATE_TABLE_USER);
                    sqldb.execSQL(DBcontract.CREATE_TABLE_EMAIL_COMENZI);
                    sqldb.execSQL(DBcontract.CREATE_TABLE_COMANDA_FINALA);

                } catch (SQLException se) {
                    Log.e("SQLite - getNewes", se.getMessage());
                    Log.e("SQLite - getNewes", se.getMessage());
                    Log.e("SQLite - getNewes", se.getMessage());
                    Log.e("SQLite - getNewes", se.getMessage());
                    Log.e("SQLite - getNewes", se.getMessage());
                    Log.e("SQLite - getNewes", se.getMessage());
                    Log.e("err comanda finala", se.getMessage());
                }
            }
            @Override
            public void onUpgrade (SQLiteDatabase db,int oldVersion, int newVersion){
                db.execSQL("DROP TABLE IF EXISTS " + DBcontract.TABLE_MAGAZINE);
                db.execSQL("DROP TABLE IF EXISTS " + DBcontract.TABLE_PRODUSE);
                db.execSQL("DROP TABLE IF EXISTS " + DBcontract.TABLE_ANTET_COMANDA);
                db.execSQL("DROP TABLE IF EXISTS " + DBcontract.TABLE_DETALII_COMANDA);
                db.execSQL("DROP TABLE IF EXISTS " + DBcontract.TABLE_USER);
                db.execSQL("DROP TABLE IF EXISTS " + DBcontract.TABLE_COMANDA_FINALA);
                // create new tables
                onCreate(db);
            }
        }


        /**
         *  OPEN DATABASE FOR READ AND WRITE
         */
    public void openDB () throws SQLException {
                    if(localSqliteDB!=null && localSqliteDB.isOpen()) {
                        localSqliteDB.close();
                    }
                    try {
                            this.localDbHelper = new DatabaseHelper(localContext);
                            this.localSqliteDB = localDbHelper.getWritableDatabase();
                    } catch (SQLException se) {
                            se.printStackTrace();
                            se.printStackTrace();
                    }
    }

   /**
         *  CLOSE DATABASES;
   */
    public void closeDB () {
                    if(localSqliteDB.isOpen()) {
                            localSqliteDB.close();
                    }
    }

    /** ADD NEW CLIENT IN DATABASE
    */
    public void  addClient(Client client){
                    this.openDB();
                    ContentValues cVal = new ContentValues();
                    cVal.put(DBcontract.KEY_MAGAZINE_NUME,client.getNume());
                    cVal.put(DBcontract.KEY_MAGAZINE_ADRESA,client.getAdresa());
                    cVal.put(DBcontract.KEY_MAGAZINE_CIF, client.getCif());
                    cVal.put(DBcontract.KEY_MAGAZINE_IBAN,client.getIban());
                    cVal.put(DBcontract.KEY_MAGAZINE_NR_REGCOM, client.getNrReg());
                    cVal.put(DBcontract.KEY_MAGAZINE_INFO,client.getInfo());

                    try {
                        final long insertId = this.localSqliteDB.insertOrThrow(DBcontract.TABLE_MAGAZINE, null, cVal);
                        //Toast.makeText(localContext, "Am adaugat magazinul:  "+client.getID(), Toast.LENGTH_LONG).show();
                    }catch (SQLException se){
                        se.printStackTrace();
                    }

                    this.closeDB();
    }

    /**
     *     Adauga un produs nou
     * @param codProdus
     */
    public void addProdus(CoduriProduse codProdus) {
                    this.openDB();
                    ContentValues prCval = new ContentValues();
                    prCval.put(DBcontract.KEY_PRODUSE_COD_PRODUS, codProdus.getPr_codprodus());
                    prCval.put(DBcontract.KEY_PRODUSE_NUME,codProdus.getPr_denumire());
                    prCval.put(DBcontract.KEY_PRODUSE_PRET, codProdus.getPret());
                    try {
                        final long insertId = this.localSqliteDB.insertOrThrow(DBcontract.TABLE_PRODUSE, null, prCval);
                        //Toast.makeText(localContext, "Am adaugat produsul:  "+codProdus.getPr_denumire(), Toast.LENGTH_LONG).show();
                    }catch (SQLException se){
                        se.printStackTrace();
                    }
                    this.closeDB();
    }



    public void addAntet(AntetComanda antet){
                    this.openDB();
                    ContentValues valori = new ContentValues();
                    valori.put(DBcontract.KEY_ANTET_COMANDA_ID_CLIENT,antet.getClientCom());
                    valori.put(DBcontract.KEY_ANTET_COMANDA_NR_COMANDA, antet.getNrCom());
                    valori.put(DBcontract.KEY_ANTET_COMANDA_DATA_COMANDA,antet.getDataCom());
                    try {
                        final long insertAntet = this.localSqliteDB.insertOrThrow(DBcontract.TABLE_ANTET_COMANDA,null,valori);
                        //Toast.makeText(localContext, "Am adaugat Antet  ", Toast.LENGTH_LONG).show();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                    this.closeDB();
    }


    /** Adauga DEtalii Comanda */
    public void addDetalii(detaliiJoin detalii){
                     this.openDB();
                     //Toast.makeText(localContext, "Am adaugat Antet  ", Toast.LENGTH_LONG).show();
                     ContentValues val = new ContentValues();

                     val.put(DBcontract.KEY_DETALII_COMANDA_NR_LINIE,detalii.getLinie());
                     val.put(DBcontract.KEY_DETALII_COMANDA_NR_COMANDA, detalii.getNrcomanda());
                     val.put(DBcontract.KEY_DETALII_COMANDA_ID_PRODUS, detalii.getCodprodus());
                     //val.put(DBcontract.KEY_DETALII_COMANDA_ID_PRODUS,detalii.getCodProdus());
                     val.put(DBcontract.KEY_DETALII_COMANDA_CANTITATE, detalii.getCantitate());
                     val.put(DBcontract.KEY_DETALII_COMANDA_PRET,detalii.getPret());
                     val.put(DBcontract.KEY_DETALII_COMANDA_VALOARE, detalii.getValoare());
                     val.put(DBcontract.KEY_DETALII_COMANDA_TVA, detalii.getTva());

                     try {
                         final long insDetalii = this.localSqliteDB.insertOrThrow(DBcontract.TABLE_DETALII_COMANDA, null, val);
                         Toast.makeText(localContext, "Produs adaugat cu succes!!!", Toast.LENGTH_SHORT).show();
                     }catch (SQLException e){
                         e.printStackTrace();
                     }
    }

    /**Adauga Comanda Finala */
    public void addComandaFinala(ComandaFinala comFin){
                     this.openDB();

                     ContentValues val = new ContentValues();
                     val.put(DBcontract.KEY_COMANDA_FINALA_DATA,comFin.getData());
                     val.put(DBcontract.KEY_COMANDA_FINALA_COD_PRODUS,comFin.getCodProdus());
                     val.put(DBcontract.KEY_COMANDA_FINALA_DENUMIRE_PRODUS,comFin.getDenumire());
                     val.put(DBcontract.KEY_COMANDA_FINALA_CANTITATE,comFin.getCantitate());
                     try{
                         final long insertCF = this.localSqliteDB.insertOrThrow(DBcontract.TABLE_COMANDA_FINALA,null,val);
                         Toast.makeText(localContext, " Produs adaugat cu succes!", Toast.LENGTH_SHORT).show();
                     }catch (SQLException ex){
                         Log.e("err insert comfinal",ex.getMessage());
                     }
                     this.closeDB();
    }


    /**Adauga Email_Comenzi */
    public void insertEmailComanda(ComenziEmail comenziEmail){
                     this.openDB();

                     ContentValues val = new ContentValues();
                     val.put(DBcontract.KEY_EMAIL_COMENZI_DATA_COMANDA, comenziEmail.getDataComanda());
                     val.put(DBcontract.KEY_EMAIL_COMENZI_NR_COMANDA, comenziEmail.getNrComanda());
                     val.put(DBcontract.KEY_EMAIL_COMENZI_STARE_COMANDA, comenziEmail.getStare());
                     val.put(DBcontract.KEY_EMAIL_COMENZI_ID_CLIENT,comenziEmail.getClientID());
                     try {
                         final long insertCom = this.localSqliteDB.insertOrThrow(DBcontract.TABLE_EMAIL_COMENZI,null,val);
                         //Toast.makeText(localContext, " Aceasta comanda este deja salvata !!! "+ insertCom, Toast.LENGTH_SHORT).show();
                     }catch (SQLException sq){
                         sq.printStackTrace();
                     }
    }
    public int deleteEmailComanda(int numarComanda){
        String whereClause = DBcontract.TABLE_EMAIL_COMENZI + "."+ DBcontract.KEY_EMAIL_COMENZI_NR_COMANDA + "= ?";
        String whereClause2 = DBcontract.TABLE_ANTET_COMANDA + "." + DBcontract.KEY_ANTET_COMANDA_NR_COMANDA +"= ?";
        String whereClause3 = DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_NR_COMANDA +"= ?";
        String[] args_selection = { Integer.toString(numarComanda) };

        this.openDB();

        final int delCom = localSqliteDB.delete(DBcontract.TABLE_EMAIL_COMENZI,whereClause,args_selection);
        final int delCom2 = localSqliteDB.delete(DBcontract.TABLE_ANTET_COMANDA,whereClause2,args_selection);
        final int delCom3 = localSqliteDB.delete(DBcontract.TABLE_DETALII_COMANDA,whereClause3,args_selection);
        return delCom;

    }

    public int deleteFinalCOmanda(long delData, String delProdus){
        this.openDB();
        SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(delData);

        String whereClause = "strftime('%d%m%Y', "+DBcontract.KEY_COMANDA_FINALA_DATA + "/1000,'unixepoch') =? and "+ DBcontract.KEY_COMANDA_FINALA_COD_PRODUS + " = '" + delProdus +"'";
        String[] args_selection = { sd.format(calendar.getTime()) };
        final int delelted = localSqliteDB.delete(DBcontract.TABLE_COMANDA_FINALA,whereClause,args_selection);
        return delelted;

    }

    public int updateFinalPreview(long dataPreview,String CodPreview,String OldCodPreview,String DenumirePreview,int CantitatePreview){
                    ContentValues value = new ContentValues();
                    this.openDB();
                    SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dataPreview);

                    String whereClause = "strftime('%d%m%Y', "+DBcontract.KEY_COMANDA_FINALA_DATA + "/1000,'unixepoch') =? and "+ DBcontract.KEY_COMANDA_FINALA_COD_PRODUS + " = '" + OldCodPreview +"'";
                    String[] args_selection = { sd.format(calendar.getTime()) };

                    value.put(DBcontract.KEY_COMANDA_FINALA_COD_PRODUS,CodPreview);
                    value.put(DBcontract.KEY_COMANDA_FINALA_CANTITATE,CantitatePreview);
                    value.put(DBcontract.KEY_COMANDA_FINALA_DENUMIRE_PRODUS,DenumirePreview);
                    if(CantitatePreview>0) {
                        return localSqliteDB.update(DBcontract.TABLE_COMANDA_FINALA, value, whereClause,args_selection);
                                //new String[]{sd.format(calendar.getTime()), String.valueOf(CantitatePreview)});
                    }else{
                        return 0;
                    }
    }

    /** Update Lista Email */
    public void updateEmailList(long now){
                    SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(now);
                    String whereClause = "strftime('%d%m%Y', "+DBcontract.KEY_EMAIL_COMENZI_DATA_COMANDA + "/1000,'unixepoch') = ? and " + DBcontract.KEY_EMAIL_COMENZI_STARE_COMANDA + "=0";
                    String[] args_selection = { sd.format(calendar.getTime()) };

                    ContentValues values = new ContentValues();
                    values.put(DBcontract.KEY_EMAIL_COMENZI_STARE_COMANDA,1);

                    this.openDB();
                    int result=localSqliteDB.update(DBcontract.TABLE_EMAIL_COMENZI,values,whereClause,args_selection);
                    /**if(result<1){
                        Toast.makeText(localContext, "Nu am updatat nimic", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(localContext, "Update reusit", Toast.LENGTH_SHORT).show();
                    }*/
    }

    /**Delete one line product */

    public int deleteLineProduct(int nrcom,int line){
                     String whereClause = DBcontract.KEY_DETALII_COMANDA_NR_COMANDA + "= ? and " + DBcontract.KEY_DETALII_COMANDA_NR_LINIE + " = "+line;
                     String[] args_selection = { Integer.toString(nrcom) };

                     this.openDB();

                     final int delete = localSqliteDB.delete(DBcontract.TABLE_DETALII_COMANDA, whereClause, args_selection);
                     return delete;

    }

    /**Listeaza Comenzile pt o datta*/
    public List<ComenziEmail> listALlEmailComenzi(long dataComanda){
                    SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dataComanda);

                    String[] pFields = {"_id","data_comanda","nr_comanda","stare_comanda","id_client"};
                    String whereClause = "strftime('%d%m%Y', "+DBcontract.TABLE_EMAIL_COMENZI+ "." + DBcontract.KEY_EMAIL_COMENZI_DATA_COMANDA + "/1000,'unixepoch') = ?";
                    String[] args_selection = { sd.format(calendar.getTime()) };
                    //Toast.makeText(localContext, "data:"+ sd.format(dataComanda), Toast.LENGTH_LONG).show();
                    //Toast.makeText(localContext, "data:"+ sd.format(dataComanda), Toast.LENGTH_LONG).show();
                    this.openDB();

                    List<ComenziEmail> list = new ArrayList<ComenziEmail>();
                    Cursor cursor = localSqliteDB.query(DBcontract.TABLE_EMAIL_COMENZI,pFields,whereClause,args_selection,null,null,"data_comanda");

                    if(cursor.moveToFirst() || cursor.getCount()>0){
                        do {
                               ComenziEmail comenziEmail = new ComenziEmail();
                               comenziEmail.setId(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_ID)));
                               comenziEmail.setDataComanda(cursor.getLong(cursor.getColumnIndex(DBcontract.KEY_EMAIL_COMENZI_DATA_COMANDA)));
                               comenziEmail.setNrComanda(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_EMAIL_COMENZI_NR_COMANDA)));
                               comenziEmail.setStare(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_EMAIL_COMENZI_STARE_COMANDA)));
                               comenziEmail.setClientID(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_EMAIL_COMENZI_ID_CLIENT)));
                               list.add(comenziEmail);
                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                    return list;
    }

    public List<ComandaFinala> listAllFinalComenzi(long dataFinala){
                    this.openDB();
                    SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dataFinala);

                    String[] pFields = {"_id","data_finala_total_comanda","denumire_produs","cod_produs","cantitate"};
                    String whereClause = "strftime('%d%m%Y', "+DBcontract.TABLE_COMANDA_FINALA+ "." + DBcontract.KEY_COMANDA_FINALA_DATA + "/1000,'unixepoch') = ?";
                    String[] args_selection = { sd.format(calendar.getTime()) };

                    List<ComandaFinala> lista = new ArrayList<ComandaFinala>();
                    Cursor cursor =  localSqliteDB.query(DBcontract.TABLE_COMANDA_FINALA,pFields,whereClause,args_selection,null,null,null);
                    if(cursor.moveToFirst() || cursor.getCount()>0){
                        do {
                            ComandaFinala comFin = new ComandaFinala();
                            comFin.setCodProdus(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_COMANDA_FINALA_COD_PRODUS)));
                            comFin.setDenumire(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_COMANDA_FINALA_DENUMIRE_PRODUS)));
                            comFin.setData(cursor.getLong(cursor.getColumnIndex(DBcontract.KEY_COMANDA_FINALA_DATA)));
                            comFin.setCantitate(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_COMANDA_FINALA_CANTITATE)));
                            lista.add(comFin);
                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                    return lista;
    }
    /**
     *  Listeaza toate produsele */
    public List<CoduriProduse> listAllProducts(){
                     String[] pFields = {"_id","cod_produs","denumire","pret"};
                    this.openDB();
                    String by_order = "produse.denumire ASC";

                    List<CoduriProduse> codProduse = new ArrayList<CoduriProduse>();
                    Cursor cursor = localSqliteDB.query(DBcontract.TABLE_PRODUSE,pFields,null,null,null,null,by_order,null);
                    if (cursor.moveToFirst() || cursor.getCount()>0) {
                        do {
                            CoduriProduse produse = new CoduriProduse();
                            produse.setPr_id(cursor.getInt((cursor.getColumnIndex(DBcontract.KEY_ID))));
                            produse.setPr_codprodus(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_PRODUSE_COD_PRODUS)));
                            produse.setPr_denumire(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_PRODUSE_NUME)));
                            produse.setPret(cursor.getDouble(cursor.getColumnIndex(DBcontract.KEY_PRODUSE_PRET)));
                            codProduse.add(produse);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    return codProduse;
    }



    /** Select Antet */
    public List<AntetComanda> getAntet(){
                   String[] antets = {"_id","nr_com"};
                   //this.openDB();

                    List<AntetComanda> lstAntet = new ArrayList<AntetComanda>();
                    Cursor cursor = localSqliteDB.query(DBcontract.TABLE_ANTET_COMANDA,antets,null,null,null,null,null,null);
                    if (cursor.moveToFirst()|| cursor.getCount()>0){
                        do {
                            AntetComanda antet = new AntetComanda();
                            antet.setIdCom(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_ID)));
                            antet.setNrCom(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_ANTET_COMANDA_NR_COMANDA)));
                            lstAntet.add(antet);
                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                    return lstAntet;
    }



    public ComandaJoin getNrAntet(String nr_comanda){
                    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

                    String tables = DBcontract.TABLE_MAGAZINE
                            +  " INNER JOIN " + DBcontract.TABLE_ANTET_COMANDA
                            +  " ON ("+DBcontract.TABLE_MAGAZINE + "."+DBcontract.KEY_ID
                            +  " = " + DBcontract.TABLE_ANTET_COMANDA + "." + DBcontract.KEY_ANTET_COMANDA_ID_CLIENT
                            +  ")";

                    String subquery =
                            " (" +
                               "SELECT " + DBcontract.TABLE_MAGAZINE + "." + DBcontract.KEY_MAGAZINE_NUME + " FROM " + DBcontract.TABLE_MAGAZINE +
                                " WHERE " +  DBcontract.TABLE_MAGAZINE + "." + DBcontract.KEY_ID + " = " +  DBcontract.TABLE_ANTET_COMANDA + "." + DBcontract.KEY_ANTET_COMANDA_ID_CLIENT +
                                " AND " +  DBcontract.TABLE_ANTET_COMANDA + "." + DBcontract.KEY_ANTET_COMANDA_NR_COMANDA + " = " + nr_comanda +
                            ") AS numeclient";

                    String subquery2 =" (SELECT sum(detalii_comanda.valoare) FROM detalii_comanda WHERE detalii_comanda.nr_com = " + nr_comanda + ") AS total_valoare ";

                    String whereClause = DBcontract.KEY_ANTET_COMANDA_NR_COMANDA + " = ?";
                    String[]  args_selection = { nr_comanda };
                    String[] antets = {subquery,"nr_com","data_com","id_client",subquery2};

                    qb.setTables(tables);
                    Cursor cursor = qb.query(localSqliteDB,antets,whereClause,args_selection,null,null,null);

                    ComandaJoin comjoin = new ComandaJoin();
                    if(cursor.moveToFirst() || cursor.getCount()>=1) {
                        comjoin.set_client(cursor.getString(cursor.getColumnIndex("numeclient")));
                        comjoin.set_nrcomanda(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_ANTET_COMANDA_NR_COMANDA)));
                        comjoin.set_data(cursor.getLong(cursor.getColumnIndex(DBcontract.KEY_ANTET_COMANDA_DATA_COMANDA)));
                        comjoin.setValTotal(douaZeci(cursor.getDouble(cursor.getColumnIndex("total_valoare"))));
                        comjoin.set_id_client(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_ANTET_COMANDA_ID_CLIENT)));
                    } else{
                        Toast.makeText(localContext, "Cursor Gol !!!", Toast.LENGTH_LONG).show();
                    }
                    cursor.close();
                    return comjoin;
    }

    public List<Aviz> getAviz(String nrcomanda){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SimpleDateFormat sd = new SimpleDateFormat("ddLLy");

        this.openDB();

        String tables = DBcontract.TABLE_ANTET_COMANDA
                +  " INNER JOIN " + DBcontract.TABLE_DETALII_COMANDA
                +  " ON ("+DBcontract.TABLE_ANTET_COMANDA + "."+DBcontract.KEY_ANTET_COMANDA_NR_COMANDA
                +  " = " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_NR_COMANDA
                +  ") INNER JOIN " + DBcontract.TABLE_PRODUSE
                +  " ON ("+DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_ID
                +  " = " +DBcontract.TABLE_DETALII_COMANDA + "."+DBcontract.KEY_DETALII_COMANDA_ID_PRODUS
                +  ") ";
        String subquery =
                "( " +
                        "SUM(cantitate)" +
                        ") as totalcantitate";

        String groupBy = DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_PRODUSE_NUME;
        String whereClause = DBcontract.TABLE_DETALII_COMANDA + "."+DBcontract.KEY_DETALII_COMANDA_NR_COMANDA + " = ?";
        String[]  args_selection = { nrcomanda };
        String[] antets = { "cod_produs","denumire",subquery,"produse.pret","valoare" };

        qb.setTables(tables);
        Cursor cursor = qb.query(localSqliteDB,antets,whereClause,args_selection,groupBy,null,null);
        List<Aviz> listAviz = new ArrayList<Aviz>();
        if(cursor.moveToFirst() || cursor.getCount()>=1) {
            do {
                Aviz aviznou = new Aviz();
                aviznou.setCodprodus(cursor.getString(cursor.getColumnIndex("cod_produs")));
                aviznou.setDenumireProdus(cursor.getString(cursor.getColumnIndex("denumire")));
                aviznou.setCantitate(cursor.getInt(cursor.getColumnIndex("totalcantitate")));
                aviznou.setPret(patruzeci(cursor.getDouble(cursor.getColumnIndex("pret"))));
                aviznou.setValoare(cursor.getDouble(cursor.getColumnIndex("valoare")));
                listAviz.add(aviznou);
            }while(cursor.moveToNext());

        }else {
            //Toast.makeText(localContext, "Cursor Gol !!!", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        return listAviz;

    }


    public List<Aviz> getAvize(long datacomanda){
                    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                    SimpleDateFormat sd = new SimpleDateFormat("ddLLy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(datacomanda);
                    this.openDB();

                    String tables = DBcontract.TABLE_ANTET_COMANDA
                            +  " INNER JOIN " + DBcontract.TABLE_DETALII_COMANDA
                            +  " ON ("+DBcontract.TABLE_ANTET_COMANDA + "."+DBcontract.KEY_ANTET_COMANDA_NR_COMANDA
                            +  " = " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_NR_COMANDA
                            +  ") INNER JOIN " + DBcontract.TABLE_PRODUSE
                            +  " ON ("+DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_ID
                            +  " = " +DBcontract.TABLE_DETALII_COMANDA + "."+DBcontract.KEY_DETALII_COMANDA_ID_PRODUS
                            +  ") ";


                    String subquery =
                            "( " +
                                    "SUM(cantitate)" +
                             ") as totalcantitate";
                    String subquery2 =
                            "( " +
                                    "SUM(valoare)" +
                             ") as totalvaloare";

                    String groupBy = DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_PRODUSE_NUME;
                    String whereClause = "strftime('%d%m%Y', "+DBcontract.KEY_ANTET_COMANDA_DATA_COMANDA + "/1000,'unixepoch') = ?";
                   // String whereClause = DBcontract.KEY_ANTET_COMANDA_DATA_COMANDA + " = ?";
                    String[]  args_selection = { sd.format(calendar.getTime()) };
                    String[] antets = { "cod_produs","denumire",subquery,"produse.pret",subquery2 };

                    qb.setTables(tables);
                    List<Aviz> listAviz = new ArrayList<Aviz>();

                    Cursor cursor = qb.query(localSqliteDB,antets,whereClause,args_selection,groupBy,null,null);

                     if(cursor.moveToFirst() || cursor.getCount()>=1) {
                        do {
                            Aviz aviznou = new Aviz();
                            aviznou.setCodprodus(cursor.getString(cursor.getColumnIndex("cod_produs")));
                            aviznou.setDenumireProdus(cursor.getString(cursor.getColumnIndex("denumire")));
                            aviznou.setCantitate(cursor.getInt(cursor.getColumnIndex("totalcantitate")));
                            aviznou.setPret(patruzeci(cursor.getDouble(cursor.getColumnIndex("pret"))));
                            aviznou.setValoare(patruzeci(cursor.getDouble(cursor.getColumnIndex("totalvaloare"))));
                            listAviz.add(aviznou);
                        }while(cursor.moveToNext());

                    }else {
                        //Toast.makeText(localContext, "Cursor Gol !!!", Toast.LENGTH_LONG).show();
                    }
                    cursor.close();
                    return listAviz;

    }
    /** Detalii Inner JOIN get all **/
    public List<detaliiJoin> getDetalii(String nr_comanda) {
        //Toast.makeText(localContext, " !!! "+ nr_comanda, Toast.LENGTH_LONG).show();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String tables = DBcontract.TABLE_PRODUSE
                + " INNER JOIN " + DBcontract.TABLE_DETALII_COMANDA
                + " ON (" + DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_ID
                + " = " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_ID_PRODUS
                + ")";

        String subquery =
                "( " +
                        "SELECT " + DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_PRODUSE_NUME + "  FROM " + DBcontract.TABLE_PRODUSE +
                        " WHERE " + DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_ID + " = " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_ID_PRODUS +
                        " AND " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_NR_COMANDA + " = " + nr_comanda +
                        ") as c1 ";

        String subquery2 =
                "( " +
                        "SELECT " + DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_PRODUSE_COD_PRODUS + "  FROM " + DBcontract.TABLE_PRODUSE +
                        " WHERE " + DBcontract.TABLE_PRODUSE + "." + DBcontract.KEY_ID + " = " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_ID_PRODUS +
                        " AND " + DBcontract.TABLE_DETALII_COMANDA + "." + DBcontract.KEY_DETALII_COMANDA_NR_COMANDA + " = " + nr_comanda +
                        ") as c2 ";

        String whereClause = DBcontract.KEY_DETALII_COMANDA_NR_COMANDA + " = ?";
        String[] args_selection = {nr_comanda};
        String order_by = "detalii_comanda.nr_linie ASC";

        String[] antets = {"detalii_comanda.pret", "nr_com", "id_produs", "nr_linie", "cantitate", "valoare", "tva", subquery, subquery2};

        qb.setTables(tables);


        List<detaliiJoin> listDetalii = new ArrayList<detaliiJoin>();
        Cursor cursor = qb.query(localSqliteDB, antets, whereClause, args_selection, null, null,order_by);

        if(cursor.moveToFirst() || cursor.getCount()>=1){
                do {
                    detaliiJoin dJ = new detaliiJoin();
                    dJ.setNrcomanda(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_NR_COMANDA)));
                    dJ.setCodprodus(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_ID_PRODUS)));
                    dJ.setLinie(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_NR_LINIE)));
                    dJ.setCantitate(cursor.getInt(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_CANTITATE)));
                    dJ.setPret(cursor.getDouble(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_PRET)));
                    dJ.setValoare(cursor.getDouble(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_VALOARE)));
                    dJ.setTva(cursor.getDouble(cursor.getColumnIndex(DBcontract.KEY_DETALII_COMANDA_TVA)));
                    dJ.setDenumireProdus(cursor.getString(cursor.getColumnIndex("c1")));
                    dJ.setCodProdus(cursor.getString(cursor.getColumnIndex("c2")));
                    listDetalii.add(dJ);
                } while (cursor.moveToNext());

        } else {

        }
        cursor.close();
        return listDetalii;
    }

    /**List a Client */
    public String listClient(int id){
        String[] pFields = {"_id","nume"};
        String whereClause = "_id = ?";
        String[] args_selection = { String.valueOf(id) };

        this.openDB();
        Cursor cursor = localSqliteDB.query(DBcontract.TABLE_MAGAZINE,pFields,whereClause,args_selection,null,null,null);

        if(cursor.moveToFirst() || cursor.getCount()>0) {
            return cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_NUME));
        }else {
            return "";
        }


    }
    /**
     * LIST ALL CLIENTS FROM DATABASE;
     */
    public List<Client> listAllClients(){
                    String[] pFields = {"_id","nume","adresa","iban","nr_regcom","cif","info"};
                    this.openDB();
                    String order_by = "magazine.nume ASC";
                    List<Client> mag = new ArrayList<Client>();

                    Cursor cursor = localSqliteDB.query(DBcontract.TABLE_MAGAZINE,pFields,null,null,null,null,order_by,null);
                    if (cursor.moveToFirst() || cursor.getCount()>0) {
                        do {
                            Client client = new Client();
                            client.setId(cursor.getInt((cursor.getColumnIndex(DBcontract.KEY_ID))));
                            client.setNume(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_NUME)));
                            client.setAdresa(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_ADRESA)));
                            client.setCif(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_CIF)));
                           // client.setIban(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_IBAN)));
                            client.setNrReg(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_NR_REGCOM)));
                           // client.setInfoUser(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_MAGAZINE_INFO)));

                            //client.setPr_denumire(cursor.getString(cursor.getColumnIndex(DBcontract.KEY_PRODUSE_NUME)));
                            //client.setPret(cursor.getDouble(cursor.getColumnIndex(DBcontract.KEY_PRODUSE_PRET)));

                            mag.add(client);
                        } while (cursor.moveToNext());
                    }else{
                        Toast.makeText(localContext, "Nu sunt clienti adaugati", Toast.LENGTH_SHORT).show();
                    }

                    cursor.close();
                    return mag;
    }



    public void deleteClient(long idclient){
                    localSqliteDB.delete(DBcontract.TABLE_MAGAZINE,DBcontract.KEY_ID+ " = ?",new String[] { String.valueOf(idclient)});
                    this.closeDB();
    }



    public int updatePreview(int nrcom,int linie,int cantitate,double pret,int codprodus){
                   if(nrcom!=0 || linie!=0 || cantitate>0 || codprodus!=0 || pret>0) {
                       ContentValues value = new ContentValues();
                       double val = patruzeci(cantitate*pret);
                       value.put(DBcontract.KEY_DETALII_COMANDA_CANTITATE, cantitate);
                       value.put(DBcontract.KEY_DETALII_COMANDA_PRET,pret);
                       value.put(DBcontract.KEY_DETALII_COMANDA_VALOARE,val);
                       value.put(DBcontract.KEY_DETALII_COMANDA_ID_PRODUS,codprodus);
                       //Toast.makeText(localContext, "Nr com: " + nrcom + " Linie" + linie + "cant" + cantitate, Toast.LENGTH_LONG).show();
                       return localSqliteDB.update(DBcontract.TABLE_DETALII_COMANDA, value, DBcontract.KEY_DETALII_COMANDA_NR_COMANDA + " =?"
                               + " and " + DBcontract.KEY_DETALII_COMANDA_NR_LINIE + " =?", new String[]{String.valueOf(nrcom), String.valueOf(linie)});
                   }else{
                       return 0;
                   }

    }
    public void deleteProdus(int idprodus){
                    localSqliteDB.delete(DBcontract.TABLE_PRODUSE,DBcontract.KEY_ID+ " = ?",new String[] { String.valueOf(idprodus)});
                    this.closeDB();
    }

    public void deleteAllProdus(){
                    localSqliteDB.delete(DBcontract.TABLE_PRODUSE,null,null);
                    this.closeDB();
    }


    public double douaZeci(double d)
    {
                    BigDecimal bd = new BigDecimal(d);
                    bd=bd.setScale(2,BigDecimal.ROUND_HALF_UP);
                    return bd.doubleValue();
    }
    public double patruzeci(double d)
    {
        BigDecimal bd = new BigDecimal(d);
        bd=bd.setScale(4,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

}
