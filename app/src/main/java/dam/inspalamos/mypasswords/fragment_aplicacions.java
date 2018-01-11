package dam.inspalamos.mypasswords;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;


public class fragment_aplicacions extends Fragment {

     DBHelper bd;
     adapter_llista_apps adapter;
     List<Aplicacio> aplicacions;


     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.llista_aplicacions, container, false);

          bd = new DBHelper(getContext(), "MyPasswords", null, 1);

          final RecyclerView listview_apps  = (RecyclerView) v.findViewById(R.id.listview_apps);
          ImageButton bt_afegir_app          = (ImageButton) v.findViewById(R.id.bt_afegir_app);

          aplicacions = bd.get_aplicacions();
          adapter = new adapter_llista_apps(aplicacions, getContext());

          listview_apps.setLayoutManager(new LinearLayoutManager(getContext()));

          listview_apps.setAdapter(adapter);

     //-- canviar fragments ------------------------------------------------------------------------

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

               LayoutInflater inflater = getActivity().getLayoutInflater();
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

     public class adapter_llista_apps extends RecyclerSwipeAdapter<adapter_llista_apps.SimpleViewHolder> {

//-- data ------------------------------------------------------------------------------------------

          private List<Aplicacio> llista_apps = new ArrayList<>();

          private Context context;


//-- constructor -----------------------------------------------------------------------------------

          public adapter_llista_apps(List<Aplicacio> llista_apps, Context context) {
               this.llista_apps = llista_apps;
               this.context = context;
          }

//-- métodos ---------------------------------------------------------------------------------------


          @Override
          public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
               View view;
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_llista_apps, parent, false);

               return new SimpleViewHolder(view);
          }

          @Override
          public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
               holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

               holder.bt_borrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         mItemManger.removeShownLayouts(holder.swipeLayout);
                         llista_apps.remove(position);
                         notifyItemRemoved(position);
                         notifyItemRangeChanged(position, llista_apps.size());
                         mItemManger.closeAllItems();
                    }
               });


               holder.app_linea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         Bundle args = new Bundle();
                         args.putSerializable("app", llista_apps.get(position));

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

               try {
                    int id_icon;

                    if (holder.app_nom.getText().toString().equals("WhatsApp")) id_icon = R.drawable.ic_whatsapp;
                    else if (holder.app_nom.getText().toString().equals("Facebook")) id_icon = R.drawable.ic_no_app;
                    else if (holder.app_nom.getText().toString().equals("Twitter")) id_icon = R.drawable.ic_twitter;
                    else if (holder.app_nom.getText().toString().equals("Google")) id_icon = R.drawable.ic_no_app;
                    else if (holder.app_nom.getText().toString().equals("Correu electrònic")) id_icon = R.drawable.ic_no_app;
                    else if  (holder.app_nom.getText().toString().equals("OneDrive")) id_icon = R.drawable.ic_no_app;
                    else if (holder.app_nom.getText().toString().equals("Compte de Firefox")) id_icon = R.drawable.ic_no_app;
                    else id_icon = R.drawable.ic_no_app;

                    holder.app_nom.setText(llista_apps.get(position).getNom_app());
                    holder.app_icon.setImageDrawable(ContextCompat.getDrawable(context, id_icon));

               } catch (IndexOutOfBoundsException | NullPointerException ignored) {}
          }

          //---------------------------------------------------------------------------------------------

          public class SimpleViewHolder extends RecyclerView.ViewHolder {
               SwipeLayout swipeLayout;
               ImageView app_icon;
               TextView app_nom;
               ImageButton bt_borrar;
               LinearLayout app_linea;

               public SimpleViewHolder(View itemView) {
                    super(itemView);
                    swipeLayout    = (SwipeLayout) itemView.findViewById(R.id.swipe);
                    app_icon       = (ImageView) itemView.findViewById(R.id.app_icon);
                    app_nom        = (TextView) itemView.findViewById(R.id.app_nom);
                    bt_borrar      = (ImageButton) itemView.findViewById(R.id.eliminar_linea_boton);
                    app_linea      = (LinearLayout) itemView.findViewById(R.id.app_linea);
               }
          }

          @Override
          public int getItemCount() {
               return llista_apps.size();
          }

          @Override
          public int getSwipeLayoutResourceId(int position) {
               return R.id.swipe;
          }
     }
}
