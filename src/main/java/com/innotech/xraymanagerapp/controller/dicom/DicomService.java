/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.model.Users;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.LoaderObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Alexander Escobar L.
 */
@Path("/dicomService")
public class DicomService {

    @EJB
    private com.innotech.xraymanagerapp.business.DicomViewerController ejbViewerController;

    @EJB
    private com.innotech.xraymanagerapp.business.ViewerRESTfulBusinessController ejbViewerRestBusinessController;

    @Context
    private UriInfo context;
    @EJB
    private com.innotech.xraymanagerapp.dto.UsersFacade ejbUsersFacade;

    private final String xrayImagesPath = "D:\\XrayImages\\";

    long firstTimeStampSeconds = System.currentTimeMillis();
    long secondTimeStampSeconds = System.currentTimeMillis();

    /**
     * Creates a new instance of DicomService
     */
    public DicomService() {
    }

    private Users getRequestUser(String token) {
        return ejbUsersFacade.getUserByToken(token);
    }

    @GET
    @Path("/dcmTest/{imageId}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response findDcmTest(
            @PathParam("imageId") Integer id
    ) {
        System.out.println("Calling the dicom test service : " + id);
        File file = new File("D:\\ExportedImages\\10PID_cat206TID_415AID_6890579853755188909.dcm");
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                //                .header("Content-Disposition", "attachment; filename=\"" + folder.getFileName() + "\"") //optional

                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .build();

    }

    /**
     * Retrieves the series of a study in json format based on the study ID
     *
     * @param headers
     * @param id
     * @return a JSON with the series of a study.
     */
    @GET
    @Path("/studies/{StudyID}/series")
    @Produces("application/dicom+json")
    public Response getStudy(@Context HttpHeaders headers, @PathParam("StudyID") String id) {
        try {
//            String xAuth = headers.getHeaderString("Authorization");
//            Users loggedUser = getRequestUser(xAuth);
//            if (Objects.isNull(loggedUser)) {
//                return Response.status(Response.Status.UNAUTHORIZED).build();//401
//            }
            //TODO return proper representation object
            String studyAsJson = ejbViewerController.getStudiesAsJson(id, context.getAbsolutePath().toString());

//            System.out.println("The studyList as json in the service: Study Id = " + id);
            return Response.ok(studyAsJson, "application/dicom+json")
                    //                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (EJBException | NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves the series of a study in json format based on the study ID
     *
     * @param headers
     * @param studyId
     * @param seriesId
     * @return a JSON with the series of a study.
     */
    @GET
    @Path("/studies/{StudyID}/series/{seriesID}/metadata")
    @Produces("application/dicom+json")
    public Response getSeriesMetadata(@Context HttpHeaders headers, @PathParam("StudyID") String studyId, @PathParam("seriesID") String seriesId) {
        try {
            String xAuth = headers.getHeaderString("Authorization");
            Users loggedUser = getRequestUser(xAuth);
            if (Objects.isNull(loggedUser)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();//401
            }
            //TODO return proper representation object
            HashMap<String, ViewDicomTags> seriesMap = getTagsMap(studyId);
            String studyAsJson = "";
            if (Objects.nonNull(seriesMap) && !seriesMap.isEmpty()) {
                ViewDicomTags vdt = seriesMap.get(seriesId);
                System.out.println("ROWS and COLUMNS:::::::: " + vdt.getImageRows() + " - " + vdt.getImageColumns());
                studyAsJson = ejbViewerController.getSeriesTagsAsJson(vdt);

                return Response.ok(studyAsJson, "application/dicom+json")
                        //                    .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();//401
            }
//            File file = new File(seriesMap.get(seriesId).getImagePath());
//            System.out.println("Thos the Tags objec: "+ViewerUtils.getJpgLocalTagsToShowList().get(id).toString());
        } catch (EJBException | NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Retrieves the series of a study in json format based on the study ID
     *
     * @param headers
     * @param studyId
     * @param seriesId
     * @param instanceID
     * @return a JSON with the series of a study.
     */
    @GET
    @Path("/studies/{StudyID}/series/{seriesID}/instances/{instanceID}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getSeriesInstance(@Context HttpHeaders headers, @PathParam("StudyID") String studyId, @PathParam("seriesID") String seriesId, @PathParam("instanceID") String instanceID) {
        try {

            //TODO return proper representation object
            HashMap<String, ViewDicomTags> seriesMap = getTagsMap(studyId);
//            String studyAsJson = ejbViewerController.getSeriesTagsAsJson(seriesMap.get(seriesId));
            ViewDicomTags tags = seriesMap.get(instanceID);
            String imagePattern = tags.getImagePattern().replace(".jpg", "");
            File file = new File(new StringBuilder(xrayImagesPath)
                    .append(tags.getImagePath())
                    .append("/")
                    .append(imagePattern).append(".dcm").toString());
//            System.out.println("Thos the Tags objec: "+ViewerUtils.getJpgLocalTagsToShowList().get(id).toString());

            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    //                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .build();
        } catch (EJBException | NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * PUT method for updating or creating an instance of DicomService
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    private HashMap<String, ViewDicomTags> getTagsMap(String studyId) {
        HashMap<String, ViewDicomTags> seriesMap = new HashMap();
        for (ViewDicomTags viewDicomTags : ejbViewerController.getTagsFromStoredXML(studyId)) {
//            viewDicomTags = ejbViewerController.getDicomTagsByImageDicomTags(viewDicomTags);
            seriesMap.put(viewDicomTags.getSeriesInstanceUID(), viewDicomTags);
        }
        return seriesMap;
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
            if (ex.getMessage().contains("does already exist")) {
                return Response.status(Response.Status.PRECONDITION_FAILED).build();//412
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();//500
            }
        }
    }
}
