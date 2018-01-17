package dam.inspalamos.mypasswords;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class fragment_credencials extends Fragment {

     View v;
     DBHelper bd;

     Aplicacio aplicacio;

     ImageView app_icon;
     ImageView marc_app;

     TextView app_nom;
     TextView usuari_veure;
     TextView contrasenya_veure;
     EditText usuari_editar;
     EditText contrasenya_editar;

     ImageButton toggle_contrasenya;
     ImageButton bt_edit;
     ImageButton bt_cancelar;

     boolean veure_contrasenya = false;
     boolean esta_editant = false;

     /* facebook: #314a7e
      * google: #dc4335
      * instagram: #4060a5
      * snapchat: #faf707
      * twitch: #65459b
      * twitter: #339dc3
      * whatsapp: #4cad3f
      * youtube: #e52d27
*/

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          try {
               bd = new DBHelper(getContext(), "MyPasswords", null, 1);
          } catch (Exception ignored) {}

          try {
               v = inflater.inflate(R.layout.usuari_contrasenya, container, false);
               aplicacio = (Aplicacio) getArguments().getSerializable("app");


               app_icon            = (ImageView) v.findViewById(R.id.app_icon);
               marc_app            = (ImageView) v.findViewById(R.id.marc_app);

               app_nom             = (TextView) v.findViewById(R.id.app_nom);
               usuari_veure        = (TextView) v.findViewById(R.id.usuari_veure);
               contrasenya_veure   = (TextView) v.findViewById(R.id.contrasenya_veure);
               usuari_editar       = (EditText) v.findViewById(R.id.usuari_editar);
               contrasenya_editar  = (EditText) v.findViewById(R.id.contrasenya_editar);

               toggle_contrasenya  = (ImageButton) v.findViewById(R.id.toggle_contrasenya);
               bt_edit             = (ImageButton) v.findViewById(R.id.bt_edit);
               bt_cancelar         = (ImageButton) v.findViewById(R.id.bt_cancelar);

          //-- estat inicial -----------------------------------------------------------------------

               usuari_editar.setVisibility(View.INVISIBLE);
               contrasenya_editar.setVisibility(View.INVISIBLE);
               bt_cancelar.setVisibility(View.GONE); //es veu quan s'està editant

               app_nom.setText(aplicacio.getNom_app());
               usuari_veure.setText(aplicacio.getUsuari());
               contrasenya_veure.setText(aplicacio.getContrasenya());

               posar_icon();
               posar_color_marc();

          //----------------------------------------------------------------------------------------

               //editar credencials
               bt_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         if (!esta_editant) editar_credencials();
                         else guardar_crendencials();
                    }
               });

               //mostra/oculta la contrasenya
               toggle_contrasenya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         mostrar_ocultar_contrasenya();
                    }
               });

          } catch (NullPointerException ignored) {
               //s'ha iniciat en horitzontal i el bundle està buit
               v = inflater.inflate(R.layout.blank_xml, container, false);
          }

          return v;
     }

//-- fi onCreateView -------------------------------------------------------------------------------

     private void posar_icon() {
          String nom = aplicacio.getNom_app();

          if (nom.equals("Facebook")) app_icon.setImageResource(R.drawable.app_facebook_icon);
          else if (nom.equals("Google")) app_icon.setImageResource(R.drawable.app_google_icon);
          else if (nom.equals("Instagram")) app_icon.setImageResource(R.drawable.app_instagram_icon);
          else if (nom.equals("Snapchat")) app_icon.setImageResource(R.drawable.app_snapchat_icon);
          else if (nom.equals("Twitch")) app_icon.setImageResource(R.drawable.app_twitch_icon);
          else if (nom.equals("Twitter")) app_icon.setImageResource(R.drawable.app_twitter_icon);
          else if (nom.equals("WhatsApp")) app_icon.setImageResource(R.drawable.app_whatsapp_icon);
          else if (nom.equals("Youtube")) app_icon.setImageResource(R.drawable.app_youtube_icon);
          else app_icon.setImageResource(R.drawable.ic_no_app);
     }

//--------------------------------------------------------------------------------------------------

     private void posar_color_marc() {
          String nom = aplicacio.getNom_app();
          int style; //determina quin estil d'styles.xml s'ha d'agafar

          //el marc serà de diferents colors depenent de l'aplicació
          if (nom.equals("Facebook"))        style = R.style.FacebookTheme;
          else if (nom.equals("Google"))     style = R.style.GoogleTheme;
          else if (nom.equals("Instagram"))  style = R.style.InstagramTheme;
          else if (nom.equals("Snapchat"))   style = R.style.SnapchatTheme;
          else if (nom.equals("Twitch"))     style = R.style.TwitchTheme;
          else if (nom.equals("Twitter"))    style = R.style.TwitterTheme;
          else if (nom.equals("WhatsApp"))   style = R.style.WhatsAppTheme;
          else if (nom.equals("Youtube"))    style = R.style.YoutubeTheme;
          else                               style = R.style.OtherTheme; //color gris clar


          ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), style);
          Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.marc_app, wrapper.getTheme());
          marc_app.setImageDrawable(drawable);
     }

//--------------------------------------------------------------------------------------------------

     private void editar_credencials() {
          esta_editant = true;
          bt_cancelar.setVisibility(View.VISIBLE);
          bt_edit.setImageResource(R.drawable.ic_guardar); //canvia l'icona d'editar per la de guardar

          usuari_veure.setVisibility(View.INVISIBLE); //no View.GONE
          usuari_editar.setVisibility(View.VISIBLE);
          contrasenya_veure.setVisibility(View.INVISIBLE);
          contrasenya_editar.setVisibility(View.VISIBLE);

          usuari_editar.setText(usuari_veure.getText().toString());
          contrasenya_editar.setText(contrasenya_veure.getText().toString());

          //cancela editar crendencials, no es guarden els canvis
          bt_cancelar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    sortir_editar_credencials();
               }
          });
     }

//--------------------------------------------------------------------------------------------------

     private void guardar_crendencials() {
          String usuari_vell = usuari_veure.getText().toString();
          String usuari_nou = usuari_editar.getText().toString();

          usuari_veure.setText(usuari_nou);
          contrasenya_veure.setText(contrasenya_editar.getText().toString());

          bd.actualitzar_compte(app_nom.getText().toString(),usuari_nou, usuari_vell, contrasenya_veure.getText().toString());

          sortir_editar_credencials(); //tenca l'edició, torna a l'estat original
     }

//--------------------------------------------------------------------------------------------------

     private void sortir_editar_credencials() {
          esta_editant = false;
          bt_cancelar.setVisibility(View.GONE);
          bt_edit.setImageResource(R.drawable.ic_edit); //canvia l'icona de guardar per la d'editar

          usuari_editar.setVisibility(View.INVISIBLE);
          usuari_veure.setVisibility(View.VISIBLE);
          contrasenya_editar.setVisibility(View.INVISIBLE);
          contrasenya_veure.setVisibility(View.VISIBLE);
     }

//--------------------------------------------------------------------------------------------------

     private void mostrar_ocultar_contrasenya() {
          if (!veure_contrasenya) {
               contrasenya_veure.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
               contrasenya_editar.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
               veure_contrasenya = true;
               toggle_contrasenya.setImageResource(R.drawable.ic_amagar_contrasenya);
          }
          else {
               contrasenya_veure.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
               contrasenya_editar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
               veure_contrasenya = false;
               toggle_contrasenya.setImageResource(R.drawable.ic_veure_contrasenya);

          }
     }

//--------------------------------------------------------------------------------------------------

}
