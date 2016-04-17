package com.example.kp.brutarievascau;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by kp on 07/03/16.
 */
public class SendMail {

    /**Empty Constructor*/
    public SendMail() {
    }

    public static SendMail newInstance(){
        return new SendMail();
    }

    public static Intent pickAcount(){
        String[] accountTypes = new String[]{"com.google"};
        return AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
    }


    public static Account returnAcount(Intent intentData){
        return new Account(intentData.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                                        intentData.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
    }

    public static MimeMessage createEmailWithAttachment(String to, String from, String subject,
                   String fileDir, String filename)  throws MessagingException, IOException {

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);

            InternetAddress tAddress = new InternetAddress(to);
            InternetAddress fAddress = new InternetAddress(from);

            email.setFrom(new InternetAddress(from));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));
            email.setSubject(subject);


            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent("testbody", "text/plain");
            mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            mimeBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(fileDir + "/"+filename);

            mimeBodyPart.setDataHandler(new DataHandler(source));
            mimeBodyPart.setFileName(filename);
            String contentType = URLConnection.guessContentTypeFromName(filename);

            mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + filename + "\"");
            mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

            multipart.addBodyPart(mimeBodyPart);

            email.setContent(multipart);
            return email;
    }


    public static com.google.api.services.gmail.model.Message createMessageWithEmail(MimeMessage pemail)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        pemail.writeTo(bytes);
        String encodedEmail = android.util.Base64.encodeToString(bytes.toByteArray(), android.util.Base64.URL_SAFE);
        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);
        return message;
    }


    public static void sendMessage(String userId, MimeMessage email,String token,
                                   JsonFactory factory,HttpTransport httpTransport)
            throws MessagingException, IOException {
        Gmail service;
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);
        com.google.api.services.gmail.model.Message message = createMessageWithEmail(email);
        service = new Gmail(httpTransport,factory,credential);
        service.users().messages().send(userId,message).execute();

    }

    public static String getToken(Context ctx,Account acount)
            throws GoogleAuthException, IOException {
        String scope = "oauth2:https://www.googleapis.com/auth/gmail.send";
        return GoogleAuthUtil.getToken(ctx,acount,scope);

    }

}
