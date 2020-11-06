/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.controller.viewer.StudyAnnotationsModel;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.Users;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.LoaderObject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Alexander Escobar L.
 */
@Path("/dicomSendService")
public class OHIFViewerService {

    @EJB
    private com.innotech.xraymanagerapp.business.DicomViewerController ejbViewerController;
    @EJB
    private com.innotech.xraymanagerapp.dto.UsersFacade ejbUsersFacade;
    @EJB
    private com.innotech.xraymanagerapp.business.ViewerRESTfulBusinessController ejbViewerRestBusinessController;

    @Context
    private UriInfo context;

    private final String xrayImagesPath = "D:\\XrayImages\\";

    /**
     * Creates a new instance of DicomService
     */
    public OHIFViewerService() {
    }

    private Users getRequestUser(String token) {
        return ejbUsersFacade.getUserByToken(token);
    }

    //******************** Dicom send functions *******************//
    @GET
    @Path("/dicomServers")
    @Produces({MediaType.APPLICATION_JSON})
    public Response find(@Context HttpHeaders headers) {

        String xAuth = headers.getHeaderString("Authorization");
        System.out.println("Getting the dicom server list: " + xAuth);
        Users loggedUser = getRequestUser(xAuth);
        if (Objects.isNull(loggedUser)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();//401
        }
        //            java.nio.file.Path folder = Paths.get("D:\\ExportedImages\\10PID_cat206TID_415AID_6890579853755188909.dcm");
        String dicomServerList = ejbViewerController.dicomServersList();
        //        for (Map.Entry m : imageMap.entrySet()) {
        //            System.out.println(m.getKey() + " " + m.getValue());
        //        }
        return Response.ok(dicomServerList, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .build();

    }

    /**
     * POST method for sending a list of DICOM files to a selected SCU server
     *
     * @param headers
     * @param dicomSend
     * @return
     */
    @POST
//    @Path("test")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response dicomSend(@Context HttpHeaders headers, String dicomSend) {
        try {

            System.out.println("Dicom send JSON received " + dicomSend);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }
            DicomSend send = new Gson().fromJson(dicomSend, DicomSend.class);
//        return "";
            return Response.ok(ejbViewerController.sendDicom(send.getImageIdList(), send.getDicomServerId()), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    //****************** End dicom send ***********************//

    //****************** Email send functions ***************************//    
    /**
     * Returns a list of emails filtering by the user that is sending the
     * request. The user is obtained from the Authorization token upon the
     * header of the request
     *
     * @param headers The mandatory headers on the request are: content-type =
     * application/json and Authorization = encrypted token
     * @return
     */
    @GET
    @Path("/emailSenders")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getEmailListByUser(@Context HttpHeaders headers) {
        try {
            String xAuth = headers.getHeaderString("Authorization");
            System.out.println("Getting the email list: " + xAuth);
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }

            String emailList = ejbViewerController.emailList(loggedUser);
            return Response.ok(emailList, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * POST method for sending an email to the given receiver
     *
     * @param headers
     * @param emailSend
     * @return
     */
    @POST
    @Path("/sendEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response emailSend(@Context HttpHeaders headers, String emailSend) {
        try {
            System.out.println("Email send JSON received " + emailSend);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }

            return Response.ok(ejbViewerController.sendEmail(emailSend), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    //****************** End email send ***********************//

    //****************** X-ray export functions ***************************//   
    /**
     * POST method that returns a list of files(JPEG or DICOM) with DICOM tags
     * as labels
     *
     * @param headers
     * @param imagesSend
     * @return
     */
    @POST
    @Path("/export")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/zip", MediaType.TEXT_PLAIN})
    public Response xrayExport(@Context HttpHeaders headers, String imagesSend) {
        try {
            System.out.println("X-ray export JSON received " + imagesSend);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }
            Object[] controllerResponse = ejbViewerController.xrayExport(imagesSend);
            byte[] imageMap = (byte[]) controllerResponse[0];
            ViewDicomTags dicomTags = (ViewDicomTags) controllerResponse[1];

//        File newImage = imageMap.get(id);
            return Response.ok(imageMap, "application/zip")
                    .header("Content-Disposition", "attachment; filename=\"" + dicomTags.getPatientName() + "_" + dicomTags.getStudyId() + ".zip\"")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    //****************** End email send ***********************//

    //*********** Annotation save and send functions ***********//  
    /**
     * POST method for saving a JSON that contains all the annotations performed
     * on the OHIF Viewer
     *
     * @param headers
     * @param annotationsAsJson
     * @param studyId
     * @return
     */
    @POST
    @Path("/saveAnnotationsData/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response saveAnnotations(@Context HttpHeaders headers, String annotationsAsJson, @PathParam("id") Integer studyId) {
        try {
            System.out.println("Annotations as JSON received " + annotationsAsJson);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }

            return Response.ok(ejbViewerController.saveAnnotationsData(studyId, annotationsAsJson, loggedUser), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * POST method for saving a JSON that contains all the annotations performed
     * on the OHIF Viewer
     *
     * @param headers
     * @param annotationsAsJson
     * @param studyId
     * @return
     */
    @POST
    @Path("/saveAnnotationsViewport/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response saveAnnotationsViewport(@Context HttpHeaders headers, String annotationsAsJson, @PathParam("id") Integer studyId) {
        try {
            System.out.println("Annotations as JSON received " + annotationsAsJson);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }

            StudyAnnotationsModel send = new Gson().fromJson(annotationsAsJson, StudyAnnotationsModel.class);

            return Response.ok(ejbViewerController.saveAnnotationsViewport(studyId, annotationsAsJson, loggedUser), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * POST method for saving a JSON that contains all the annotations performed
     * on the OHIF Viewer
     *
     * @param headers
     * @param studyId
     * @return
     */
    @GET
    @Path("/getAnnotations/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAnnotations(@Context HttpHeaders headers, @PathParam("id") Integer studyId) {
        try {
            System.out.println("Annotations as JSON studyId received " + studyId);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }

            return Response.ok(ejbViewerController.getAnnotations(studyId), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    //************************************* END **********************************//

    /**
     * POST method for deleting a list of files.
     *
     * @param headers
     * @param dicomSend
     * @return
     */
    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteImages(@Context HttpHeaders headers, String dicomSend) {
        try {
            System.out.println("Images to delete: " + dicomSend);
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }
            DicomSend send = new Gson().fromJson(dicomSend, DicomSend.class);
//        return "";
            return Response.ok(ejbViewerController.deleteSelectedImages(send.getImageIdList()), MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    //****************************** Receive and storage image ****************************** 

    @POST
    @Path("/createXrayFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response doCreate(@Context HttpHeaders headers, LoaderObject entity) {
        try {
            String originFormat = ".bmp";
            File responseFile = ejbViewerRestBusinessController.createImage(entity.getImage(), originFormat);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            // execute the thread
            executor.submit(() -> {
                try {
                    ejbViewerRestBusinessController.createOtherFiles(entity.getImage());
                } catch (IOException ex) {
                    Logger.getLogger(DicomService.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            executor.shutdown();
            return Response.ok(responseFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    //                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), ex.getMessage()).build();//500
        }
    }

    @POST
    @Path("/getImageList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getImagesByStudy(@Context HttpHeaders headers, Studies study) {
        try {
            String jsonString = new Gson().toJson(ejbViewerRestBusinessController.getBase64ImageListByStudy(study));
            return Response.ok(jsonString, MediaType.APPLICATION_JSON)
                    //                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), ex.getMessage()).build();//500
        }
    }
}
