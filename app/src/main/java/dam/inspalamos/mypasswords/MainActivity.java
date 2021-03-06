package dam.inspalamos.mypasswords;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.security.NoSuchAlgorithmException;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

     SharedPreferences shared_preferences;

     EditText usuari;
     EditText contrasenya1;
     EditText contrasenya2;
     TextView contrasenya_oblidada;
     Button bt_accedir;
     Button bt_reset;

     ImageView logo_sa, logo_e, logo_passw, logo_rds, logo_clau;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.pantalla_inici);

         shared_preferences = PreferenceManager.getDefaultSharedPreferences(this);
         final boolean es_primera_vegada = shared_preferences.getBoolean("primera_vegada", true);

         usuari = (EditText) findViewById(R.id.usuari);
         contrasenya1 = (EditText) findViewById(R.id.contrasenya1);
         contrasenya2 = (EditText) findViewById(R.id.contrasenya2);
         contrasenya_oblidada = (TextView) findViewById(R.id.contrasenya_oblidada);
         bt_accedir = (Button) findViewById(R.id.bt_entrar);
         bt_reset = (Button) findViewById(R.id.bt_reset);

         logo_sa = (ImageView) findViewById(R.id.logo_sa);
         logo_e = (ImageView) findViewById(R.id.logo_e);
         logo_passw = (ImageView) findViewById(R.id.logo_passw);
         logo_rds = (ImageView) findViewById(R.id.logo_rds);
         logo_clau = (ImageView) findViewById(R.id.logo_clau);

         logo_sa.setVisibility(INVISIBLE);
         logo_e.setVisibility(INVISIBLE);
         logo_passw.setVisibility(INVISIBLE);
         logo_rds.setVisibility(INVISIBLE);

         logo_clau.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animacio));



         logo_clau.getAnimation().setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 Log.i("ANIMEND","sfsdf");

                 logo_sa.setAnimation(AnimationUtils.loadAnimation(logo_sa.getContext(), R.anim.apareixer));
                 logo_sa.getAnimation().setAnimationListener(anim_listener(logo_sa));


                 logo_e.setAnimation(AnimationUtils.loadAnimation(logo_e.getContext(), R.anim.apareixer));
                 logo_e.getAnimation().setAnimationListener(anim_listener(logo_e));

                 logo_passw.setAnimation(AnimationUtils.loadAnimation(logo_passw.getContext(), R.anim.apareixer));
                 logo_passw.getAnimation().setAnimationListener(anim_listener(logo_passw));

                 logo_rds.setAnimation(AnimationUtils.loadAnimation(logo_rds.getContext(), R.anim.apareixer));
                 logo_rds.getAnimation().setAnimationListener(anim_listener(logo_rds));
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });

         logo_clau.startAnimation(logo_clau.getAnimation());

         //és la primera vegada que s'executa l'aplicació
         if (es_primera_vegada) {
             usuari.setVisibility(VISIBLE); //usuari per recuperar la contrasenya
             contrasenya2.setVisibility(VISIBLE);
             contrasenya_oblidada.setVisibility(GONE);
             bt_reset.setVisibility(GONE);
         } else {
             contrasenya_oblidada.setVisibility(VISIBLE);
             findViewById(R.id.ic_usuari).setVisibility(GONE);
             bt_reset.setVisibility(VISIBLE);
             usuari.setVisibility(GONE);
             contrasenya2.setVisibility(GONE);
         }

         contrasenya_oblidada.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (!es_primera_vegada) {
                     try {
                         String contrasenya_recuperada = shared_preferences.getString("master_password", "");
                         String usuari_enviar          = shared_preferences.getString("usuari", "");
                         new SendMailTask(MainActivity.this).execute("mypassord123@gmail.com",
                                 "contrasenya", usuari_enviar, "Recuperació de la contrasenya", "La teva contrasenya és '" + contrasenya_recuperada + "'");

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
                 } else {
                     String master_password = shared_preferences.getString("master_password", "");

                     //la contrasenya introduida ha de coincidir amb la master password
                     if (contrasenya1.getText().toString().equals(master_password)) {
                         //obre la llista d'aplicacions
                         Intent intent = new Intent(MainActivity.this, MyPasswords.class);
                         startActivity(intent);
                     } else {
                         //YoYo.with(Techniques.Shake).onEnd(animation-> contrasenya1.setText("")).playOn(contrasenya1);
                         contrasenya1.setText("");
                         YoYo.with(Techniques.Shake).duration(100).playOn(contrasenya1);
                     }
                 }
             }
         });

         bt_reset.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 DBHelper dbHelper = null;
                 try {
                     dbHelper = new DBHelper(getApplicationContext(), "MyPasswords", null, 1);
                 } catch (NoSuchAlgorithmException e) {
                     e.printStackTrace();
                 }
                 SharedPreferences.Editor editor = shared_preferences.edit();
                 editor.putBoolean("primera_vegada", true);
                 editor.putString("master_password", ""); //guarda la master password
                 editor.putString("usuari", "safePassword123");
                 editor.apply();
                 dbHelper.eliminar_info();
                 finish();
                 startActivity(getIntent());
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

     public Animation.AnimationListener anim_listener(View v) {

         Animation.AnimationListener listener = new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                v.setVisibility(VISIBLE);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         };

         return listener;
     }

}
