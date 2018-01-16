import java.security.Security;
import java.util.Properties;

public class GMailSender extends javax.mail.Authenticator {

     private String mailhost = "stmp.gmail.com";
     private String user;
     private String password;
     private String session;

     static {
          Security.addProvider(new com.provider.JSSEProvider());
     }

     public GMailSender(String user, String password) {
          this.user = user;
          this.password = password;

          Properties props = new Properties();
          props.
     }
}
