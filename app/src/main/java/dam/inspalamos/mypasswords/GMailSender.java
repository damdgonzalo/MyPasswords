package dam.inspalamos.mypasswords;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSender extends javax.mail.Authenticator {

     private String usuari;
     private String contrasenya;
     private Session sessio;

//--------------------------------------------------------------------------------------------------
/*
     static {
          Security.addProvider(new dam.inspalamos.mypasswords.JSSEProvider());
     }*/

     public GMailSender(final String usuari, final String contrasenya) {
          this.usuari = usuari;
          this.contrasenya = contrasenya;

          Properties propietats = new Properties();
          /*propietats.setProperty("mail.transport.protocol", "smtp");
          propietats.setProperty("mail.host", "smtp.gmail.com");
          propietats.put("mail.smtp.auth", "true");
          propietats.put("mail.smtp.port", "465");
          propietats.put("mail.smtp.socketFactory.port", "465");
          propietats.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
          propietats.put("mail.smtp.socketFactory.fallback", "false");
          propietats.setProperty("mail.smtp.quitwait", "false");*/

          propietats.put ("mail.smtp.host", "smtp.gmail.com");
          propietats.put ("mail.smtp.socketFactory.port", "465");
          propietats.put ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
          propietats.put ("mail.smtp.port", "465");

          //sessio = Session.getDefaultInstance(propietats, this);
          sessio = Session.getDefaultInstance(propietats, new Authenticator() {
               @Override
               protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuari, contrasenya);
               }
          });
     }

    /* protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(usuari, contrasenya);
     }*/

//--------------------------------------------------------------------------------------------------

     public void sendMail(String subject, String body, String sender, String recipients) throws Exception {
          try {
               MimeMessage message = new MimeMessage(sessio);
               /*DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));

               message.setSender(new InternetAddress(sender));
               message.setSubject(subject);
               message.setDataHandler(handler);

               if (recipients.indexOf(',')>0) message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
               else message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

               Transport.send(message);*/

               message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
               message.setSubject(subject);
               message.setText(body);

               Transport.send(message);
          } catch (Exception e) {
          }
     }

//--------------------------------------------------------------------------------------------------


}

