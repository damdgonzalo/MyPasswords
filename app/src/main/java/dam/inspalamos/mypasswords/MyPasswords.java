package dam.inspalamos.mypasswords;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MyPasswords extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          //quan s'obre l'aplicació per primera vegada
          if (savedInstanceState == null) {
               if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //carrega els dos fragments si la pantalla està en horitzontal
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.frame_aplicacions, new fragment_aplicacions())
                            .add(R.id.frame_credencials, new fragment_credencials())
                            .commit();
               }

               else {
                    //carrega només un fragment
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.frame_aplicacions, new fragment_aplicacions())
                            .commit();
               }
          }

          //quan es gira el dispositiu
          else {
               if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //per si es canvia l'orientació en el fragment de credencials i es prem "enrere",
                    //que no es torni a carregar el fragment d'aplicacions a sobre del que ja hi és
                    getSupportFragmentManager().popBackStack();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_aplicacions, new fragment_aplicacions())
                            .commit();
               }
          }
     }

     @Override
     public void onBackPressed() {
          //per tal que no torni sempre a la pantalla principal quan es prem [ENRERE] desde la llista d'apps
          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
               //si està al detall d'una app, torna a la llista
               getSupportFragmentManager().popBackStack();
          }
          else { //sino, surt de l'aplicació
               moveTaskToBack(true);
          }
     }
}
