package dam.inspalamos.mypasswords;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class fragment_aplicacions extends Fragment {

     DBHelper bd;
     adapter_llista_apps adapter;
     List<Aplicacio> aplicacions;
     View view;

     Spinner triar_app;
     EditText altre_app;
     EditText usuari;
     EditText contrasenya1;
     EditText contrasenya2;
     TextView missatge_incorrecte;
     Button bt_afegir;


     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          view = inflater.inflate(R.layout.llista_aplicacions, container, false);

          bd = new DBHelper(getContext(), "MyPasswords", null, 1);

          final RecyclerView listview_apps  = (RecyclerView) view.findViewById(R.id.listview_apps);
          ImageButton bt_afegir_app         = (ImageButton) view.findViewById(R.id.bt_afegir_app);

          try {
               aplicacions = bd.get_aplicacions();
               adapter = new adapter_llista_apps(aplicacions, getContext());
          } catch (RuntimeException ignored) {
               //No hi ha cap aplicació al compte
          }

          listview_apps.setLayoutManager(new LinearLayoutManager(getContext()));
          listview_apps.setAdapter(adapter);


          bt_afegir_app.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    afegir_app();
               }
          });
          return view;
     }

//-- fi onCreateView -------------------------------------------------------------------------------


     private void afegir_app() {
          LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          View v = inflater.inflate(R.layout.afegir_compte, null);

          //mostra un dialog al mig de la pantalla
          final Dialog dialog = new Dialog(getContext());
          dialog.setContentView(v);
          dialog.show();

          //----------------------------------------------------------------------------------------

          triar_app            = (Spinner) v.findViewById(R.id.triar_app);

          altre_app           = (EditText) v.findViewById(R.id.custom_app);

          usuari              = (EditText) v.findViewById(R.id.usuari);
          contrasenya1        = (EditText) v.findViewById(R.id.contrasenya1);
          contrasenya2        = (EditText) v.findViewById(R.id.contrasenya2);

          missatge_incorrecte = (TextView) v.findViewById(R.id.missatge_error);
          bt_afegir                   = (Button) v.findViewById(R.id.bt_guardar);

          altre_app.setVisibility(GONE); //apareix quan no es tria una app predeterminada
          missatge_incorrecte.setVisibility(GONE); //apareix quan l'usuari o la contrasenya és incorrecte

          //-- spinner -----------------------------------------------------------------------------

          //apps que es mostraran a l'spinner per triar
          String[] apps_default = {"Facebook", "Google", "Instagram", "Snapchat", "Twitch",
                  "Twitter", "WhatsApp", "Youtube", "Altre", "Tria una aplicació"};

          Integer[] icones = {R.drawable.app_facebook_icon, R.drawable.app_google_icon, R.drawable.app_instagram_icon, R.drawable.app_snapchat_icon, R.drawable.app_twitch_icon,
                  R.drawable.app_twitter_icon, R.drawable.app_whatsapp_icon, R.drawable.app_youtube_icon, R.drawable.ic_no_app, 0};


          triar_app.setAdapter(new AdapterSpinner(getActivity().getBaseContext(), R.layout.row_llista_apps, apps_default, icones));

          //si no es tria una app predeterminada, s'ha d'especificar quina es vol afegir
          triar_app.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (triar_app.getSelectedItem().equals("Altre")) {
                         triar_app.setSelection(triar_app.getAdapter().getCount()-1);
                         altre_app.setVisibility(VISIBLE);
                    }
                    else {
                         altre_app.setVisibility(GONE);
                    }
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {}
          });

          triar_app.setSelection(triar_app.getAdapter().getCount()); //comença mostrant "Tria una aplicació"


          //-- afegir a la base de dades ----------------------------------------------------------------

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
                              Aplicacio app;

                              try {
                                   //mira si s'ha triat una app predeterminada, una altre, o cap
                                   if (aplicacio_triada.equals("Altre")) {
                                        app = new Aplicacio(altre_app.getText().toString(), str_usuari, str_contrasenya1);
                                   }
                                   else if (aplicacio_triada.equals("Tria una aplicació")) {
                                        throw new NullPointerException();
                                   }
                                   else {
                                        altre_app.setText(aplicacio_triada);
                                        app = new Aplicacio(aplicacio_triada, str_usuari, str_contrasenya1);
                                   }

                                   //afegeix l'aplicació a la base de dades
                                   bd.afegir_compte(app.getNom_app(), str_usuari, str_contrasenya1);

                                   aplicacions.add(app);
                                   adapter.notifyItemInserted(adapter.getItemCount());
                                   dialog.dismiss(); //tenca el popup

                              } catch (android.database.sqlite.SQLiteConstraintException e) {
                                   //ja existeix l'usuari per a l'aplicació triada
                                   missatge_incorrecte.setText("El compte ja existeix.");
                                   missatge_incorrecte.setVisibility(VISIBLE);
                              } catch (NullPointerException e) {
                                   //no s'ha triat cap aplicació
                                   missatge_incorrecte.setText("Tria una aplicació");
                                   missatge_incorrecte.setVisibility(VISIBLE);
                              }
                         }

                         else { //les contrasenyes no coincideixen
                              missatge_incorrecte.setText("Les contrasenyes no coincideixen.");
                              missatge_incorrecte.setVisibility(VISIBLE);
                         }
                    }

                    else { //hi ha camps buits
                         missatge_incorrecte.setText("No poden haver camps buits.");
                         missatge_incorrecte.setVisibility(VISIBLE);
                    }
               }
          });
     }


//-- adapters --------------------------------------------------------------------------------------


     public class AdapterSpinner extends ArrayAdapter {
          String[] apps_default;
          Integer[] icones;

          public AdapterSpinner(Context context, int textViewResourceId, String[] apps_default, Integer[] icones) {
               super(context, textViewResourceId, apps_default);
               this.apps_default = apps_default;
               this.icones = icones;
          }

          //l'spinner per triar aplicació a afegir mostra l'icona i el nom d'aquesta
          public View getCustomView(int position, View convertView, ViewGroup parent) {
               LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               View v = inflater.inflate(R.layout.row_afegir_app, null);

               TextView nom = (TextView) v.findViewById(R.id.app_nom);
               nom.setText(apps_default[position]);
               nom.setTextSize(15);

               ImageView icon = (ImageView) v.findViewById(R.id.app_icon);
               icon.setImageResource(icones[position]);

               if (position==super.getCount()) {
                    icon.setVisibility(GONE); //l'últim item no te icona ("Triar una aplicació")
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

          @Override
          //per que no es mostri l'últim element de l'spinner ("Triar una aplicació")
          public int getCount() {
               int count = super.getCount();
               return count>0 ? count-1 : count;
          }
     }

     public class adapter_llista_apps extends RecyclerSwipeAdapter<adapter_llista_apps.SimpleViewHolder> {

     //-- data -------------------------------------------------------------------------------------

          private List<Aplicacio> llista_apps = new ArrayList<>();

          private Context context;


     //-- constructor ------------------------------------------------------------------------------

          public adapter_llista_apps(List<Aplicacio> llista_apps, Context context) {
               this.llista_apps = llista_apps;
               this.context = context;
          }

     //-- mètodes ----------------------------------------------------------------------------------

          @Override
          public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
               View view;
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_llista_apps, parent, false);

               return new SimpleViewHolder(view);
          }

          @Override
          public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
               holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
               /*holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                         YoYo.with(Techniques.Tada).duration(500).playOn(layout.findViewById(R.id.eliminar_linea_boton));
                    }

               });*/

               holder.eliminar_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         bd.eliminar_compte(holder.app_nom.getText().toString(), holder.app_usuari.getText().toString());
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

               holder.app_nom.setText(llista_apps.get(position).getNom_app());
               holder.app_usuari.setText(llista_apps.get(position).getUsuari());

               try {
                    int id_icon;

                    if (holder.app_nom.getText().toString().equals("WhatsApp")) id_icon = R.drawable.app_whatsapp_icon;
                    else if (holder.app_nom.getText().toString().equals("Facebook")) id_icon = R.drawable.app_facebook_icon;
                    else if (holder.app_nom.getText().toString().equals("Twitter")) id_icon = R.drawable.app_twitter_icon;
                    else if (holder.app_nom.getText().toString().equals("Google")) id_icon = R.drawable.app_google_icon;
                    else if (holder.app_nom.getText().toString().equals("Youtube")) id_icon = R.drawable.app_youtube_icon;
                    else if (holder.app_nom.getText().toString().equals("Instagram")) id_icon = R.drawable.app_instagram_icon;
                    else if (holder.app_nom.getText().toString().equals("Twitch")) id_icon = R.drawable.app_twitch_icon;
                    else if (holder.app_nom.getText().toString().equals("Snapchat")) id_icon = R.drawable.app_snapchat_icon;
                    else id_icon = R.drawable.ic_no_app;

                    holder.app_icon.setImageDrawable(ContextCompat.getDrawable(context, id_icon));

               } catch (IndexOutOfBoundsException | NullPointerException ignored) {}
          }

          //---------------------------------------------------------------------------------------------

          public class SimpleViewHolder extends RecyclerView.ViewHolder {
               SwipeLayout swipeLayout;
               LinearLayout eliminar_app;
               ImageView app_icon;
               TextView app_nom;
               TextView app_usuari;
               LinearLayout app_linea;

               public SimpleViewHolder(View itemView) {
                    super(itemView);
                    swipeLayout    = (SwipeLayout) itemView.findViewById(R.id.swipe);
                    eliminar_app   = (LinearLayout) itemView.findViewById(R.id.eliminar_app);
                    app_icon       = (ImageView) itemView.findViewById(R.id.app_icon);
                    app_nom        = (TextView) itemView.findViewById(R.id.app_nom);
                    app_usuari     = (TextView) itemView.findViewById(R.id.app_usuari);
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
