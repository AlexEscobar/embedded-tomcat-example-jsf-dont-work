/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.model.ExportEmailModel;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.Studies;
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
import java.util.HashMap;
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
public class FileSenderClient implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.business.ConfigurationBusinessController ejbConfigurationBusinessController;

    private WebTarget webTarget;
    private Client client;
    private static Response response;
    private static String BASE_URI;
    private final String createXRayEndpointURI = "/webresources/dicomSendService/createXrayFile";

    public FileSenderClient() {
    }

//    @PostConstruct
    public void init() {
        BASE_URI = "http://"+ejbConfigurationBusinessController.getConfigurationMap().get(ConfigurationProperties.APP_SERVER_NAME);
//        BASE_URI = "http://localhost:8080";
        client = javax.ws.rs.client.ClientBuilder.newBuilder().build();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 20000);
        webTarget = client.target(BASE_URI).path(createXRayEndpointURI);
        System.out.println("webtarget: " + webTarget.getUri().getPath());
    }

    public FileSenderClient(String baseUri, String endPointURI) {
        BASE_URI = baseUri;
        client = javax.ws.rs.client.ClientBuilder.newBuilder().build();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 20000);
        webTarget = client.target(BASE_URI).path(endPointURI);
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

    public Response getImageListByStudy(Studies study) {
        init();
        study = new Studies(study.getId());
        setWebTarget(client.target(BASE_URI).path("/webresources/dicomSendService/getImageList"));
        Response r = create_JSON(study);
        return r;
    }

    public void testFileCreation() {
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
            FileSenderClient fsc = new FileSenderClient("http://192.168.0.36:8080/", "/webresources/dicomSendService/createXrayFile");
            File convertedFile = fsc.sendFile(lo).readEntity(File.class);

            String imageBase64Response = convertImageToBase64(convertedFile);
            System.out.println("Worked::::: " + imageBase64Response);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSenderClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileSenderClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(FileSenderClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void testGetImagesList() {
        Studies study = new Studies(7011);
//        String send = new Gson().toJson(study);
        FileSenderClient fsc = new FileSenderClient("http://192.168.0.36:8080/", "/webresources/dicomSendService/getImageList");
        String imagesMap = fsc.getImageListByStudy(study).readEntity(String.class);
        System.out.println(imagesMap);
    }

    public static void main(String args[]) {
        testGetImagesList();
    }

    public static String convertImageToBase64(File file) {
        try {
            Path folder = Paths.get(file.getAbsolutePath());
            File imageSource = folder.toFile();
            byte[] currentXrayImage = Files.readAllBytes(imageSource.toPath());
            return Base64.getEncoder().encodeToString(currentXrayImage);
        } catch (IOException ex) {
            Logger.getLogger(FileSenderClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return "";
    }

    public void setWebTarget(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

}
