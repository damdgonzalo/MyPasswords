package dam.inspalamos.mypasswords;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class adapter_llista_apps extends BaseAdapter {

//-- data ------------------------------------------------------------------------------------------

     private List<Aplicacio> llista_apps = new ArrayList<>();
     private List<String> llista_apps_triar = new ArrayList<>();

     private Context context;
     private LayoutInflater inflater;

     private boolean es_aplicacio = true;

//-- constructor -----------------------------------------------------------------------------------

     //L'adapter s'utilitza en el llistat de comptes, i en el llistat d'aplicacions a afegir
     //els dos necessiten una icona i un nom

     public adapter_llista_apps(List<Aplicacio> llista_apps, Context context) {
          this.llista_apps = llista_apps;
          this.context = context;
          inflater = LayoutInflater.from(context);
          es_aplicacio = true;
     }

     public adapter_llista_apps(Context context, List<String> llista_apps_triar) {
          this.llista_apps_triar = llista_apps_triar;
          this.context = context;
          inflater = LayoutInflater.from(context);
          es_aplicacio = false;
     }

//-- métodos ---------------------------------------------------------------------------------------

     @Override
     public View getView(final int position, View convertView, ViewGroup parent) {
          //ViewHolder holder = null;
          // if (convertView == null) { ...
          // holder = new ViewHolder();
          convertView = inflater.inflate(R.layout.row_llista_apps,null);

          ImageView icon = convertView.findViewById(R.id.app_icon);
          TextView nom = convertView.findViewById(R.id.app_nom);

          //...} else convertView.getTag(holder);

          if (es_aplicacio) nom.setText(llista_apps.get(position).getNom_app());
          else nom.setText(llista_apps_triar.get(position));

          int id_icon = 0;

          //l'icona canvia segons l'aplicació
          if (nom.getText().toString().equals("WhatsApp")) id_icon = R.drawable.ic_whatsapp;
          else if (nom.getText().toString().equals("Facebook")) id_icon = R.drawable.ic_no_app;
          else if (nom.getText().toString().equals("Twitter")) id_icon = R.drawable.ic_twitter;
          else if (nom.getText().toString().equals("Google")) id_icon = R.drawable.ic_no_app;
          else if (nom.getText().toString().equals("Correu electrònic")) id_icon = R.drawable.ic_no_app;
          else if  (nom.getText().toString().equals("OneDrive")) id_icon = R.drawable.ic_no_app;
          else if (nom.getText().toString().equals("Compte de Firefox")) id_icon = R.drawable.ic_no_app;
          else id_icon = R.drawable.ic_no_app;

          icon.setImageDrawable(ContextCompat.getDrawable(context, id_icon));
          return convertView;
     }

     //---------------------------------------------------------------------------------------------

     @Override
     public int getCount() {return llista_apps.size();}

     @Override
     public Aplicacio getItem(int position){return llista_apps.get(position);}

     @Override
     public long getItemId(int position){return position;}

}