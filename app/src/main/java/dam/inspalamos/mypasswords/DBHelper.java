package dam.inspalamos.mypasswords;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper{

//-- camps -----------------------------------------------------------------------------------------

     private final String TAULA_APLICACIONS = "aplicacions_credencials";
     private String crear_taula = "CREATE TABLE " + TAULA_APLICACIONS + " (aplicacio TEXT NOT NULL, usuari TEXT NOT NULL, contrasenya TEXT NOT NULL, PRIMARY KEY(aplicacio,usuari))";

//-- constructor -----------------------------------------------------------------------------------

     public DBHelper(Context context, String nom_bd, SQLiteDatabase.CursorFactory factory, int version) {
          super(context, nom_bd, factory, version);
     }

//-- mètodes ---------------------------------------------------------------------------------------

     @Override
     public void onCreate(SQLiteDatabase bd) {
          bd.execSQL(crear_taula);

          //comença amb algunes aplicacions per defecte
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('WhatsApp','722509568','contrasenya1')");
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('Facebook','damdgonzalo@gmail.com','contrasenya2')");
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('Twitter','damdgonzalo@gmail.com','lmao?')");
     }

     @Override
     public void onUpgrade(SQLiteDatabase bd, int i, int i1) {
          bd.execSQL("DROP TABLE taula_base");
          bd.execSQL(crear_taula);
     }


     public void afegir_compte(String app, String usuari, String contrasenya) {
          SQLiteDatabase bd = this.getWritableDatabase();
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('" + app + "','" + usuari + "','" + contrasenya + "')");
     }

     public void eliminar_compte(String app, String usuari) {
          SQLiteDatabase bd = this.getWritableDatabase();
          bd.execSQL("DELETE FROM " + TAULA_APLICACIONS + " WHERE aplicacio='" + app + "' AND usuari='" + usuari + "'");
     }

     public void actualitzar_compte(String aplicacio, String usuari_nou, String usuari_vell, String contrasenya) {
          SQLiteDatabase bd = this.getWritableDatabase();
          bd.execSQL("UPDATE " + TAULA_APLICACIONS + " SET usuari='" + usuari_nou + "', contrasenya='" + contrasenya + "' WHERE aplicacio='" + aplicacio + "' AND usuari='" + usuari_vell + "'");
     }

     public List<Aplicacio> get_aplicacions() {
          SQLiteDatabase bd = this.getReadableDatabase();
          Cursor c = bd.rawQuery("SELECT * FROM " + TAULA_APLICACIONS, null);

          List<Aplicacio> aplicacions = new ArrayList<>();


          c.moveToFirst();

          do { //llegeix les linies de la taula d'aplicacions
               Aplicacio app = new Aplicacio();

               app.setNom_app(c.getString(c.getColumnIndex("aplicacio")));
               app.setUsuari(c.getString(c.getColumnIndex("usuari")));
               app.setContrasenya(c.getString(c.getColumnIndex("contrasenya")));

               aplicacions.add(app);
          } while (c.moveToNext());

          c.close();
          return aplicacions;
     }
}
