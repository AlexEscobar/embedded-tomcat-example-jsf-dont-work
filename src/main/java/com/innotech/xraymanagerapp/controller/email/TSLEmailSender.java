/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.email;

import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import java.io.File;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author Alexander Escobar L.
 */
public class TSLEmailSender implements Serializable {

    /**
     * Send an email with attached files.
     *
     * @param hostName smtp.googlemail.com
     * @param smtpPort 465
     * @param emailUser tech@fmsmeds.com
     * @param emailPassword the password of tech@fmsmeds.com
     * @param from iMiEmailTest@gmail.com
     * @param to tech@fmsmeds.com
     * @param subject TestMail
     * @param fullMessage This is a test mail ... :-
     * @param files File array that contains the images to get attached
     * @param imagePatientName
     * @param imageId
     * @return
     */
    public static boolean sendTSLEmail(String hostName, int smtpPort, final String emailUser,
            final String emailPassword, String from, String to, String subject, String fullMessage, List<File> files, String imagePatientName, int imageId) {

        try {
            if (from == null) {
                from = "xrayimages@mail.com";
            }
            addTrustCert();
            // Get system properties
            Properties props = System.getProperties();
            props.put("mail.smtp.host", hostName);
            props.put("mail.smtps.auth", "true");
//            props.put("mail.smtp.ssl.enable", "true");
//            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(props, null);

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO, to);

            message.setSubject(subject);

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setContent(fullMessage, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);

            for (File file : files) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(file.getName());
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);

            Transport tr = session.getTransport("smtps");
            tr.connect(hostName, emailUser, emailPassword);
            tr.sendMessage(message, message.getAllRecipients());
            System.out.println("Mail Sent Successfully");
            tr.close();
            files.forEach((file) -> {
                file.delete();
            });
            return true;

        } catch (NullPointerException | MessagingException ex) {
//            JsfUtil.addErrorMessage(ex.getMessage());
            Logger.getLogger(TSLEmailSender.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    public static boolean checkEmailConnection(String hostName, int smtpPort, final String emailUser,
            final String emailPassword) {
        try {
            System.out.println("In email check connection: " + hostName + " - Port: " + smtpPort + " - User: " + emailUser + " - " + emailPassword);
            // Get system properties
            Properties props = System.getProperties();
            props.put("mail.smtp.host", hostName);
            props.put("mail.smtps.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
//            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(props, null);

            MimeMessage message = new MimeMessage(session);

//            message.setFrom(new InternetAddress(from));
//            message.setRecipients(Message.RecipientType.TO, to);
//            message.setSubject(subject);
            BodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            System.out.println("Before check...");
            Transport tr = session.getTransport("smtps");
            System.out.println("Before connect...");
            tr.connect(hostName, emailUser, emailPassword);
//            tr.sendMessage(message, message.getAllRecipients());
            System.out.println("Mail Sent Successfully");
            tr.close();
            System.out.println("EMail closed...");

            return true;

        } catch (MessagingException ex) {
//            JsfUtil.addErrorMessage(ex, ex.getMessage());
            Logger.getLogger(TSLEmailSender.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    private static void addTrustCert() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            Logger.getLogger(TSLEmailSender.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        checkEmailConnection("smtp.gmail.com", 465, "imi.dental.test@gmail.com", "iMi104**");
    }
}
