package dam.inspalamos.mypasswords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.pantalla_inici);

          EditText contrasenya1          = (EditText) findViewById(R.id.contrasenya1);
          EditText contrasenya2          = (EditText) findViewById(R.id.contrasenya2);
          TextView contrasenya_oblidada  = (TextView) findViewById(R.id.contrasenya_oblidada);
          Button bt_accedir              = (Button) findViewById(R.id.bt_entrar);

          contrasenya2.setVisibility(View.GONE); //al principi no és visible

          bt_accedir.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, MyPasswords.class);
                    startActivity(intent);
               }
          });
     }
}