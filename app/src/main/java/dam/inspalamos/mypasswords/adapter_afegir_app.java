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

public class adapter_afegir_app extends BaseAdapter {

//-- data ------------------------------------------------------------------------------------------

     private List<String> llista_apps_triar = new ArrayList<>();

     private Context context;
     private LayoutInflater inflater;

//-- constructor -----------------------------------------------------------------------------------

     //L'adapter s'utilitza en el llistat de comptes, i en el llistat d'aplicacions a afegir
     //els dos necessiten una icona i un nom


     public adapter_afegir_app(Context context, List<String> llista_apps_triar) {
          this.llista_apps_triar = llista_apps_triar;
          this.context = context;
          inflater = LayoutInflater.from(context);
     }

//-- métodos ---------------------------------------------------------------------------------------


     @Override
     public int getCount() {return 0;}

     @Override
     public Object getItem(int i) {
          return null;
     }

     @Override
     public long getItemId(int i) {
          return 0;
     }

     @Override
     public View getView(final int position, View convertView, ViewGroup parent) {
          //ViewHolder holder = null;
          // if (convertView == null) { ...
          // holder = new ViewHolder();
          convertView = inflater.inflate(R.layout.row_llista_apps,null);

          ImageView icon = convertView.findViewById(R.id.app_icon);
          TextView nom = convertView.findViewById(R.id.app_nom);

          //...} else convertView.getTag(holder);

          nom.setText(llista_apps_triar.get(position));

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
}