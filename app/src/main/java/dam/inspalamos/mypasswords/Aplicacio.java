package dam.inspalamos.mypasswords;


import java.io.Serializable;

public class Aplicacio implements Serializable {

//-- atributs --------------------------------------------------------------------------------------

     private String nom_app;
     private String usuari;
     private String contrasenya;

//-- constructors ----------------------------------------------------------------------------------

     public Aplicacio(String nom_app, String usuari, String contrasenya) {
          this.nom_app = nom_app;
          this.usuari = usuari;
          this.contrasenya = contrasenya;
     }

     public Aplicacio() {}

//-- mètodes ---------------------------------------------------------------------------------------

     //getters i setters
     public String getNom_app() {return nom_app;}
     public String getUsuari() {return usuari;}
     public String getContrasenya() {return contrasenya;}

     public void setNom_app(String nom_app) {this.nom_app = nom_app;}
     public void setUsuari(String usuari) {this.usuari = usuari;}
     public void setContrasenya(String contrasenya) {this.contrasenya = contrasenya;}
}
