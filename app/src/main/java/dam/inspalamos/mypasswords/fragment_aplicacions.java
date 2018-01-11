package dam.inspalamos.mypasswords;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class fragment_aplicacions extends Fragment {

     DBHelper bd;
     adapter_llista_apps adapter;
     List<Aplicacio> aplicacions;


     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.llista_aplicacions, container, false);

          bd = new DBHelper(getContext(), "MyPasswords", null, 1);

          final ListView listview_apps  = (ListView) v.findViewById(R.id.listview_apps);
          ImageButton bt_afegir_app          = (ImageButton) v.findViewById(R.id.bt_afegir_app);

          aplicacions = bd.get_aplicacions();
          adapter = new adapter_llista_apps(aplicacions, getContext());

          listview_apps.setAdapter(adapter);

     //-- canviar fragments ------------------------------------------------------------------------

          listview_apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle args = new Bundle();
                    args.putSerializable("app", aplicacions.get(i));

                    fragment_credencials credencials = new fragment_credencials();
                    credencials.setArguments(args);

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                         getFragmentManager().beginTransaction()
                                 .replace(R.id.frame_credencials, credencials)
                                 .commit();
                    }
                    else {
                         getFragmentManager().beginTransaction()
                                 .replace(R.id.frame_aplicacions, credencials)
                                 .addToBackStack(null)
                                 .commit();
                    }
               }
          });

          bt_afegir_app.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    afegir_app(view);
               }
          });
          return v;
     }

//-- fi onCreateView -------------------------------------------------------------------------------

     private void afegir_app(View view) {
          LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          View v = inflater.inflate(R.layout.afegir_compte, null);

          //mostra un popup al mig de la pantalla
          final PopupWindow popup = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
          popup.showAtLocation(view, Gravity.CENTER, 0, 0);

          final Spinner triar_app             = (Spinner) v.findViewById(R.id.triar_app);

          final EditText usuari         = (EditText) v.findViewById(R.id.usuari);
          final EditText contrasenya1   = (EditText) v.findViewById(R.id.contrasenya1);
          final EditText contrasenya2   = (EditText) v.findViewById(R.id.contrasenya2);

          final TextView missatge_incorrecte  = (TextView) v.findViewById(R.id.missatge_error);
          Button bt_afegir              = (Button) v.findViewById(R.id.bt_guardar);

          missatge_incorrecte.setVisibility(View.GONE); //apareix quan l'usuari o la contrasenya és incorrecte

          //-----------------------------------------------------------------------------------

          String[] apps_default = {"Tria una aplicació", "WhatsApp", "Facebook", "Twitter", "Google", "Twitch", "Pinterest"};
          Integer[] icones = {0, R.drawable.ic_whatsapp, R.drawable.ic_no_app, R.drawable.ic_twitter, R.drawable.ic_no_app, R.drawable.ic_no_app};

          triar_app.setAdapter(new AdapterSpinner(getContext(), R.layout.row_llista_apps, apps_default, icones));

          //-- afegir a la base de dades ------------------------------------------------------

          bt_afegir.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    String str_usuari = usuari.getText().toString();
                    String str_contrasenya1 = contrasenya1.getText().toString();
                    String str_contrasenya2 = contrasenya2.getText().toString();
                    String aplicacio_triada = triar_app.getSelectedItem().toString();

                    //comprova que no es deixa cap camp buit i que les contrasenyes coincideixen
                    if (!str_usuari.equals("") && !str_contrasenya1.equals("") && !str_contrasenya2.equals("")) {

                         if (str_contrasenya1.equals(str_contrasenya2)) {
                              try {
                                   //afegeix l'aplicació a la base de dades
                                   Aplicacio app = new Aplicacio(aplicacio_triada, str_usuari, str_contrasenya1);

                                   bd.afegir_compte(aplicacio_triada, str_usuari, str_contrasenya1);

                                   aplicacions.add(app);
                                   adapter.notifyDataSetChanged();
                                   popup.dismiss(); //tenca el popup
                              } catch (android.database.sqlite.SQLiteConstraintException e) {
                                   //L'aplicació i l'usuari ja existeixen
                                   missatge_incorrecte.setText("El compte ja existeix.");
                                   missatge_incorrecte.setVisibility(View.VISIBLE);
                              }
                         }
                         else { //les contrasenyes no coincideixen
                              missatge_incorrecte.setText("Usuari o contrasenya incorrectes.");
                              missatge_incorrecte.setVisibility(View.VISIBLE);
                         }
                    }

                    else { //hi ha camps buits
                         missatge_incorrecte.setText("Usuari o contrasenya incorrectes.");
                         missatge_incorrecte.setVisibility(View.VISIBLE);
                    }
               }
          });

          popup.setFocusable(true);
          popup.update();
     }

//-- spinner adapter -------------------------------------------------------------------------------


     public class AdapterSpinner extends ArrayAdapter {
          String[] apps_default;
          Integer[] icones;

          public AdapterSpinner(Context context, int textViewResourceId, String[] apps_default, Integer[] icones) {
               super(context, textViewResourceId, apps_default);
               this.apps_default = apps_default;
               this.icones = icones;
          }


          public View getCustomView(int position, View convertView, ViewGroup parent) {

               LayoutInflater inflater = getLayoutInflater();
               View v = inflater.inflate(R.layout.row_llista_apps, null);

               TextView nom = (TextView) v.findViewById(R.id.app_nom);
               nom.setText(apps_default[position]);
               nom.setTextSize(15);

               ImageView icon = (ImageView) v.findViewById(R.id.app_icon);
               icon.setImageResource(icones[position]);

               if (position==0) {
                    icon.setVisibility(View.GONE); //el primer item no te icona
               }

               return v;
          }

          @Override
          public View getDropDownView (int position, View convertView, ViewGroup parent) {
               return getCustomView(position, convertView, parent);
          }

          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
               return getCustomView(position, convertView, parent);
          }
     }
}
