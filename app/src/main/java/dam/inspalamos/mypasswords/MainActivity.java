package dam.inspalamos.mypasswords;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

     SharedPreferences shared_preferences;

     EditText usuari;
     EditText contrasenya1;
     EditText contrasenya2;
     TextView contrasenya_oblidada;
     Button bt_accedir;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.pantalla_inici);

          shared_preferences = PreferenceManager.getDefaultSharedPreferences(this);
          final boolean es_primera_vegada = shared_preferences.getBoolean("primera_vegada", true);

          usuari                   = (EditText) findViewById(R.id.usuari);
          contrasenya1             = (EditText) findViewById(R.id.contrasenya1);
          contrasenya2             = (EditText) findViewById(R.id.contrasenya2);
          contrasenya_oblidada     = (TextView) findViewById(R.id.contrasenya_oblidada);
          bt_accedir               = (Button) findViewById(R.id.bt_entrar);

          //és la primera vegada que s'executa l'aplicació
          if (es_primera_vegada) {
               usuari.setVisibility(VISIBLE); //usuari per recuperar la contrasenya
               contrasenya2.setVisibility(VISIBLE);
               contrasenya_oblidada.setVisibility(GONE);
          }

          else {
               contrasenya_oblidada.setVisibility(VISIBLE);
               findViewById(R.id.ic_usuari).setVisibility(GONE);
               usuari.setVisibility(GONE);
               contrasenya2.setVisibility(GONE);
          }

          contrasenya_oblidada.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if (!es_primera_vegada) {
                         try {
                              String contrasenya_recuperada = shared_preferences.getString("master_password", "");
                              String usuari_enviar = shared_preferences.getString("usuari", "");

                              GMailSender sender = new GMailSender("usuari@gmail.com", "contrasenya");

                              sender.sendMail("Recuperació contrasenya",
                                              "La teva contrasenya és: " + contrasenya_recuperada,
                                              "damdgonzalo@gmail.com",
                                              usuari_enviar);

                              Toast.makeText(getApplicationContext(), "S'ha enviat un correu al teu compte.", Toast.LENGTH_SHORT).show();
                         } catch (Exception e) {
                              Toast.makeText(getApplicationContext(), "No s'ha pogut enviar el correu.", Toast.LENGTH_SHORT).show();
                         }
                    }
               }
          });

          bt_accedir.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if (es_primera_vegada) {
                         //l'usuari no ha d'estar buit i les contrasenyes han de coincidir
                         if (contrasenya1.getText().toString().equals(contrasenya2.getText().toString())
                                 && !usuari.getText().toString().equals("")) {

                              SharedPreferences.Editor editor = shared_preferences.edit();
                              editor.putBoolean("primera_vegada", false);
                              editor.putString("master_password", contrasenya1.getText().toString()); //guarda la master password
                              editor.putString("usuari", usuari.getText().toString());
                              editor.apply();

                              //obre la llista d'aplicacions
                              Intent intent = new Intent(MainActivity.this, MyPasswords.class); //no afegeix la activitat principal al backstack
                              startActivity(intent);
                         }
                    }
                    else {
                         String master_password = shared_preferences.getString("master_password","");

                         //la contrasenya introduida ha de coincidir amb la master password
                         if (contrasenya1.getText().toString().equals(master_password)) {
                              //obre la llista d'aplicacions
                              Intent intent = new Intent(MainActivity.this, MyPasswords.class);
                              startActivity(intent);
                         }
                    }
               }
          });
     }

     @Override
     public void onRestart() {
          super.onRestart();
          contrasenya_oblidada.setVisibility(VISIBLE);
          findViewById(R.id.ic_usuari).setVisibility(GONE);
          usuari.setVisibility(GONE);
          contrasenya2.setVisibility(GONE);
     }


}