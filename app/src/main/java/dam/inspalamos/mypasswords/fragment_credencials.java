package dam.inspalamos.mypasswords;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class fragment_credencials extends Fragment {

     View v;
     DBHelper bd;

     Aplicacio aplicacio;

     TextView nom_app;
     TextView usuari_veure;
     TextView contrasenya_veure;
     EditText usuari_editar;
     EditText contrasenya_editar;

     ViewSwitcher vs_usuaris;
     ViewSwitcher vs_contrasenyes;

     //Button bt_guardar;

     boolean editar_visible = true;

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          v = inflater.inflate(R.layout.usuari_contrasenya, container, false);

          bd = new DBHelper(getContext(), "MyPasswords", null, 1);

          nom_app              = (TextView) v.findViewById(R.id.nom_aplicacio);
          usuari_veure         = (TextView) v.findViewById(R.id.usuari_veure);
          contrasenya_veure    = (TextView) v.findViewById(R.id.contrasenya_veure);
          usuari_editar        = (EditText) v.findViewById(R.id.usuari_editar);
          contrasenya_editar   = (EditText) v.findViewById(R.id.contrasenya_editar);

          //ImageButton bt_menu = (ImageButton) v.findViewById(R.id.bt_menu);
          //bt_guardar          = (Button) v.findViewById(R.id.guardar_credencials);

          vs_usuaris  = (ViewSwitcher) v.findViewById(R.id.switcher_usuaris);
          vs_contrasenyes  = (ViewSwitcher) v.findViewById(R.id.switcher_contrasenyes);

     //---------------------------------------------------------------------------------------------

          usuari_editar.setVisibility(View.GONE);
          contrasenya_editar.setVisibility(View.GONE);
          //bt_guardar.setVisibility(View.GONE);

          try {
               aplicacio = (Aplicacio) getArguments().getSerializable("app");

               nom_app.setText(aplicacio.getNom_app());
               usuari_veure.setText(aplicacio.getUsuari());
               contrasenya_veure.setText(aplicacio.getContrasenya());
          } catch (NullPointerException ignored) {
               //s'ha iniciat en horitzontal i el bundle està buit
          }

     //-- menú opcions -----------------------------------------------------------------------------


          /*bt_guardar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if (!editar_visible) {
                         editar_visible = true;

                         vs_usuaris.showNext();
                         vs_contrasenyes.showNext();

                         usuari_veure.setText(usuari_editar.getText().toString());
                         contrasenya_veure.setText(contrasenya_editar.getText().toString());

                         bt_guardar.setVisibility(View.GONE);
                    }
               }
          });*/

          return v;
     }

//-- fi onCreateView -------------------------------------------------------------------------------



     public void obrir_menu() {
          LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          View view = inflater.inflate(R.layout.opcions_credencials, null);

          //mostra un popup adalt a la dreta de la pantalla
          final PopupWindow popup = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
          popup.showAtLocation(v, Gravity.TOP|Gravity.END, 0, 0);


          TextView eliminar_app    = (TextView) view.findViewById(R.id.eliminar_app);
          TextView editar_compte = (TextView) view.findViewById(R.id.editar_credencials);

          if (!editar_visible) editar_compte.setVisibility(View.GONE);
          else editar_compte.setVisibility(View.VISIBLE);

          //---------------------------------------------------------------------------------------------

          eliminar_app.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    bd.eliminar_compte(nom_app.getText().toString(), usuari_veure.getText().toString());
               }
          });

               editar_compte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         if (editar_visible) {
                              vs_usuaris.showNext();
                              vs_contrasenyes.showNext();

                              usuari_editar.setText(usuari_veure.getText().toString());
                              contrasenya_editar.setText(contrasenya_veure.getText().toString());

                              editar_visible = false;
                              //bt_guardar.setVisibility(View.VISIBLE);
                              popup.dismiss(); //tenca el popup
                         }
                    }
               });


          popup.setFocusable(true);
          popup.update();
     }
}
