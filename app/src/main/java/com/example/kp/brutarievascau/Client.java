package com.example.kp.brutarievascau;

/**
 * Created by kp on 26/12/15.
 */
public class Client {

        int c_id;
        String c_nume;
        String c_adresa;
        String c_info;
        String c_cif;
        String c_nrreg;
        String c_iban;

        // Empty constructor;
        public Client() {
        }


        // Set Fields ////////////////////////////
        public void setNume(String nume){
            this.c_nume = nume.toString();
        }
        public void setAdresa(String adresa){
            this.c_adresa = adresa.toString();
        }
        public void setInfoUser(String info){
            this.c_info = info.toString();
        }
        public void setCif(String cif){
            this.c_cif = cif.toString();
        }
        public void setNrReg(String nrreg){
            this.c_nrreg = nrreg.toString();
        }
        public void setIban(String iban){
            this.c_iban = iban.toString();
        }
        public void setId(int idclient) {this.c_id=idclient;}


        // Get Fields //////////////////////////////
        public String getNume(){
            return this.c_nume;
        }
        public String getAdresa(){
            return this.c_adresa;
        }
        public String getInfo(){
            return this.c_info;
        }
        public String getCif(){
            return this.c_cif;
        }
        public String getNrReg(){
            return this.c_nrreg;
        }
        public String getIban(){
            return this.c_iban;
        }
        public int getID() {return this.c_id;}


}
