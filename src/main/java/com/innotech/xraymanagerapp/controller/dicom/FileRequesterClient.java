/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.dicom.LoaderObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientProperties;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class FileRequesterClient implements Serializable{

    @EJB
    private com.innotech.xraymanagerapp.business.ConfigurationBusinessController ejbConfigurationBusinessController;
    
    private WebTarget webTarget;
    private Client client;
    private static Response response;
    private static String BASE_URI;
    private static String CREATE_XRAY_FILE_SERVICE_URL = "/webresources/dicomSendService/createXrayFile";

    public FileRequesterClient() {
    }
    
//    @PostConstruct
    public void init(){
//        BASE_URI = ejbConfigurationBusinessController.getConfigurationMap().get(ConfigurationProperties.APP_SERVER_NAME);
        BASE_URI = "http://localhost:8080";
        client = javax.ws.rs.client.ClientBuilder.newBuilder().build();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 20000);
        webTarget = client.target(BASE_URI).path(CREATE_XRAY_FILE_SERVICE_URL);
        System.out.println("webtarget: "+webTarget.getUri().getPath());
    }

    public FileRequesterClient(String baseUri) {
        BASE_URI = baseUri;
        client = javax.ws.rs.client.ClientBuilder.newBuilder().build();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 20000);
        webTarget = client.target(BASE_URI).path("/webresources/dicomSendService/createXrayFile");
    }

    private SSLContext getSSLContext() {
        // for alternative implementation checkout org.glassfish.jersey.SslConfigurator
        javax.net.ssl.TrustManager x509 = new javax.net.ssl.X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                return;
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                return;
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new javax.net.ssl.TrustManager[]{x509}, null);
        } catch (java.security.GeneralSecurityException ex) {
        }
        return ctx;
    }

    public Response create_XML(Object requestEntity) throws ClientErrorException {
        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
        return response;
    }

    public Response create_JSON(Object requestEntity) throws ClientErrorException {
        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON, javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Type", "application/json")
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        return response;
    }

    /**
     * Sends the x-ray file object (image.BMP), to the REST endpoint that is
     * storing the file on the server file systems
     *
     * @param loader
     * @return the image converted in JPEG format or error message is something
     * wrong happened
     */
    public Response sendFile(LoaderObject loader) {
        Response r = create_JSON(loader);
        return r;
    }

    public static void main(String args[]) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("C:\\Users\\Hi\\Desktop\\imageBase64ForTesting.txt");
            String imageBase64 = IOUtils.toString(fis, "UTF-8");
            Images img = new Images(9915);
//            img.setCreationDate();
            img.setSOPInstanceUID("1.2.840.10008.5.1.4.1.1.1.3.20201020110438");
            img.setPattern("1.2.840.10008.5.1.4.1.1.1.3.20201020110438_thisisthe_name.jpg");
            img.setImageFile(imageBase64);
            img.setImagePath("BONNIE, FRANK, GREG_061318\\20201020-095918_test");
//            File f = createImage(img, "D:\\XrayImages\\", ".bmp");
            LoaderObject lo = new LoaderObject();
            lo.setImage(img);

//            String send = new Gson().toJson(lo);
//            System.out.println(send);
            FileRequesterClient fsc = new FileRequesterClient("http://192.168.0.36:8080/");
            File convertedFile = fsc.sendFile(lo).readEntity(File.class);

            String imageBase64Response = convertImageToBase64(convertedFile);
            System.out.println("Worked::::: " + imageBase64Response);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileRequesterClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileRequesterClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(FileRequesterClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String convertImageToBase64(File file) {
        try {
            Path folder = Paths.get(file.getAbsolutePath());
            File imageSource = folder.toFile();
            byte[] currentXrayImage = Files.readAllBytes(imageSource.toPath());
            return Base64.getEncoder().encodeToString(currentXrayImage);
        } catch (IOException ex) {
            Logger.getLogger(FileRequesterClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return "";
    }
}
