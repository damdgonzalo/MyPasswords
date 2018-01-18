package dam.inspalamos.mypasswords;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class DBHelper extends SQLiteOpenHelper{

//-- camps -----------------------------------------------------------------------------------------


     static Cipher cipher;
     static KeyGenerator key_generator;
     static Key secret_key;

     private final String TAULA_APLICACIONS = "aplicacions_credencials";
     private String crear_taula = "CREATE TABLE " + TAULA_APLICACIONS + " (aplicacio TEXT NOT NULL, usuari TEXT NOT NULL, contrasenya TEXT NOT NULL, PRIMARY KEY(aplicacio,usuari))";

//-- constructor -----------------------------------------------------------------------------------

     public DBHelper(Context context, String nom_bd, SQLiteDatabase.CursorFactory factory, int version) throws NoSuchAlgorithmException {
          super(context, nom_bd, factory, version);
          /*try {
               key_generator = KeyGenerator.getInstance("AES");
               key_generator.init(128);
               secret_key = key_generator.generateKey();
               cipher = Cipher.getInstance("AES");
          } catch (NoSuchPaddingException e) {
               e.printStackTrace();
          }*/

     }

//-- mètodes ---------------------------------------------------------------------------------------

     @Override
     public void onCreate (SQLiteDatabase bd)  {
          bd.execSQL(crear_taula);

          //comença amb algunes aplicacions per defecte
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('Twitter','usuari2','" + encriptar_contrasenya("contrasenya2", secret_key) + "')");
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('Facebook','usuari1','" + encriptar_contrasenya("contrasenya1", secret_key) + "')");
     }

     @Override
     public void onUpgrade(SQLiteDatabase bd, int i, int i1) {
          bd.execSQL("DROP TABLE taula_base");
          bd.execSQL(crear_taula);
     }

//--------------------------------------------------------------------------------------------------

     public void afegir_compte(Aplicacio app)  {
          SQLiteDatabase bd = this.getWritableDatabase();
          //guarda la contrasenya encriptada
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('" + app.getNom_app() + "','" + app.getUsuari() + "','" + encriptar_contrasenya(app.getContrasenya(), secret_key) + "')");
     }


     public void eliminar_compte(Aplicacio app) {
          SQLiteDatabase bd = this.getWritableDatabase();
          bd.execSQL("DELETE FROM " + TAULA_APLICACIONS + " WHERE aplicacio='" + app.getNom_app() + "' AND usuari='" + app.getUsuari() + "'");
     }

     public void eliminar_info() {
          SQLiteDatabase bd = this.getWritableDatabase();
          bd.execSQL("DELETE FROM "+ TAULA_APLICACIONS);

          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('Twitter','usuari2','" + encriptar_contrasenya("contrasenya2", secret_key) + "')");
          bd.execSQL("INSERT INTO " + TAULA_APLICACIONS + " VALUES ('Facebook','usuari1','" + encriptar_contrasenya("contrasenya1", secret_key) + "')");

     }


     public void actualitzar_compte(String aplicacio, String usuari_nou, String usuari_vell, String contrasenya) {
          SQLiteDatabase bd = this.getWritableDatabase();
          bd.execSQL("UPDATE " + TAULA_APLICACIONS + " SET usuari='" + usuari_nou + "', contrasenya='" + encriptar_contrasenya(contrasenya, secret_key) + "' WHERE aplicacio='" + aplicacio + "' AND usuari='" + usuari_vell + "'");
     }

//--------------------------------------------------------------------------------------------------

     //llegeix el que hi ha a la taula d'aplicacions
     public List<Aplicacio> get_aplicacions() {

          SQLiteDatabase bd = this.getReadableDatabase();
          Cursor c = bd.rawQuery("SELECT * FROM " + TAULA_APLICACIONS, null);

          List<Aplicacio> aplicacions = new ArrayList<>(); //retorna aquesta llista

          c.moveToFirst();

          do { //llegeix les linies de la taula d'aplicacions
               Aplicacio app = new Aplicacio();

               app.setNom_app(c.getString(c.getColumnIndex("aplicacio")));

               app.setUsuari(c.getString(c.getColumnIndex("usuari")));

               String contrasenya_encriptada = c.getString(c.getColumnIndex("contrasenya"));
               app.setContrasenya(desencriptar_contrasenya(contrasenya_encriptada, secret_key)); //desencripta la contrasenya

               aplicacions.add(app);
          } while (c.moveToNext());

          c.close();

          return aplicacions;
     }

//-- encriptar/desencriptar contrasenya ------------------------------------------------------------

     private static String encriptar_contrasenya(String contrasenya, Key secret_key) {

          String contrasenya_encriptada = contrasenya;
          /*try {
               byte[] contrasenya_bytes = contrasenya.getBytes();
               cipher.init(Cipher.ENCRYPT_MODE, secret_key);

               byte[] encrypted_bytes  = cipher.doFinal(contrasenya_bytes);

               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Base64.Encoder encoder = Base64.getEncoder();
                    Log.i("ENCRYPT/DECRYPT", "Encriptada: --" + encoder.encodeToString(encrypted_bytes) + "--");
                    contrasenya_encriptada =  encoder.encodeToString(encrypted_bytes);
               }

          } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
               e.printStackTrace();
               Log.i("ENCRYPT/DECRYPT", "Fail encrypt.");
          }*/

          return contrasenya_encriptada;
     }

     private static String desencriptar_contrasenya(String contrasenya_encriptada, Key secret_key) {
          String contrasenya_desencriptada = contrasenya_encriptada;

          /*try {
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    byte[] encrypted_bytes = decoder.decode(contrasenya_encriptada);

                    cipher.init(Cipher.DECRYPT_MODE, secret_key);

                    byte[] decrypted_bytes = cipher.doFinal(encrypted_bytes);
                    contrasenya_desencriptada =  new String(decrypted_bytes);

                    Log.i("ENCRYPT/DECRYPT", "Desencriptada: --" + contrasenya_desencriptada + "--");

               }
          } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
               e.printStackTrace();
               Log.i("ENCRYPT/DECRYPT", "Fail decrypt.");
          }*/

          return contrasenya_desencriptada;
     }
}
