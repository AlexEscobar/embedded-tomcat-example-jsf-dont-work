/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.controller.StudiesController;
import com.innotech.xraymanagerapp.controller.dicom.Image2Dcm;
import com.innotech.xraymanagerapp.controller.dicom.StoreTool;
import com.innotech.xraymanagerapp.controller.email.TSLEmailSender;
import com.innotech.xraymanagerapp.controller.export.ImageFormatConverter;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.SensorImageController;
import com.innotech.xraymanagerapp.controller.ViewerController;
import com.innotech.xraymanagerapp.controller.dicom.FileSenderClient;
import com.innotech.xraymanagerapp.controller.util.XmlReader;
import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.model.BodyPartViews;
import com.innotech.xraymanagerapp.model.BodyParts;
import com.innotech.xraymanagerapp.model.DicomServers;
import com.innotech.xraymanagerapp.model.EmailConfig;
import com.innotech.xraymanagerapp.model.ExportEmailModel;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.TeethNumbers;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.ImageSize;
import com.pixelmed.dicom.DicomException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.IncompatibleConnectionException;
import org.primefaces.model.StreamedContent;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class ViewerBusinessController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ImagesFacade ejbImagesFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.AnnotationsFacade ejbAnnotationsFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade ejbViewDicomTagsFacade;
    @EJB
    private com.innotech.xraymanagerapp.business.ConfigurationBusinessController ejbConfigurationBusinessController;

    @EJB
    private com.innotech.xraymanagerapp.business.EmailBusinessController ejbEmailBusinessController;
    @EJB
    private FileSenderClient ejbFileSenderClient;

    public static String sourceFilePath;
    public static String xrayDestinationPath;
    public static String tempPath;
    public static String exportPath;
    public static String dicomSendPath;
//    public static String fileName = "test1.bmp";
    public static File file;
    private static HashMap<Integer, File> jpgImageList;
    private static String jpgImageAnnotationState;
    private static InputStream inputImage;
    private String server;
    private Integer port;
    private String description;
    private StudiesController sc;
    private List<String> images;
    private String customerEmail;
    private String emailBody;
    private String emailType = "jpg";
    private String imageExportFormat = "jpg";
    private List<Images> imageList;
    private HashMap<Integer, ExportEmailModel> imagesAsFile;
    private StreamedContent imagesZip;
    private String imagesList;
    protected DicomServers selectedDicomServer;
    private TeethNumbers selectedTooth; // the actual annotation of the current tooth
    private static final String DATE_FORMAT = "yyyyMMdd";
    protected boolean isLoadingImagesFromPageload;
    private String patientName;// will be used by the email subject and for the images name
    private ViewDicomTags dicomTags;
    private String currentXrayImage;
    private Studies currentStudy;
    private ImageSize imageSize;

    public ViewDicomTags getDicomTags() {
        return dicomTags;
    }
    private String[] deletedImageIdsList;

    public static Map getJpgImageList() {
        return jpgImageList;
    }

    public static void setJpgImageList(HashMap aJpgImageList) {
        jpgImageList = aJpgImageList;
    }

    public void doubleClickRedirection() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/viewer/Create.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getJpgImageAnnotationState() {
        return jpgImageAnnotationState;
    }

    public static void setJpgImageAnnotationState(String aJpgImageAnnotationState) {
        jpgImageAnnotationState = aJpgImageAnnotationState;
    }

//
//    @PostConstruct
    public void init() {
        initializeLists();
        isLoadingImagesFromPageload = true;
        selectedDicomServer = new DicomServers();
        initializePathVariables();

        // clears the temporary path where images that shown in the dicom viewer are located
//        JsfUtil.cleanDirectory(tempPath, "jpg");
    }

    /**
     * Initializes all the image lists to force re-query to fill up image lists
     * with the database values. useful when we know there are changes on the
     * x-ray images list and want to get those new images.
     */
    public void initializeLists() {
        imageList = new ArrayList();
        images = new ArrayList();
        imagesAsFile = new HashMap();
        isLoadingImagesFromPageload = false;
    }

    public void initializePathVariables() {
        System.out.println("ejbConfigurationBusinessController: " + ejbConfigurationBusinessController);
        System.out.println("ejbConfigurationBusinessController.getConfigurationMap().get(\"SourceFilePath\")" + ejbConfigurationBusinessController.getConfigurationMap().get("SourceFilePath"));
        if (sourceFilePath == null) {
            sourceFilePath = ejbConfigurationBusinessController.getConfigurationMap().get("SourceFilePath");
        }
        if (xrayDestinationPath == null) {
            xrayDestinationPath = ejbConfigurationBusinessController.getConfigurationMap().get("XrayDestinationPath");
        }
        if (tempPath == null) {
            tempPath = ejbConfigurationBusinessController.getConfigurationMap().get("TempPath");
        }
        if (exportPath == null) {
            exportPath = ejbConfigurationBusinessController.getConfigurationMap().get("ExportPath");
        }
        if (dicomSendPath == null) {
            dicomSendPath = ejbConfigurationBusinessController.getConfigurationMap().get("DicomSendPath");
        }
    }

    public void setEmailSelected(EmailConfig email) {
        ejbEmailBusinessController.setSelected(email);
    }

    /**
     * Convert the current x-ray image in a bytes map
     *
     * @return The converted image in a base 64 String
     */
    public String getCurrentXrayImage() {
        return currentXrayImage;
    }

    public Images getCurrentImage() {
        getImagesFromStudies();
        Images currentImage = null;
        
        if (imageList != null && !imageList.isEmpty()) {
            currentImage = imageList.get(imageList.size() - 1);// gets the last image on the ImageList
        } else if (getImagesFromStudies() != null) {
            if (!imageList.isEmpty()) {
                currentImage = imageList.get(imageList.size() - 1);// gets the last image on the ImageList                            
            }
        }
        try {
            String imagePath = currentImage.getPattern();
            if (currentImage.getImagePath() != null) {
                //                String imageName = currentImages.getPattern().replace(".bmp", ".jpg");
                String imageName = currentImage.getPattern();
                imagePath = new StringBuilder(currentImage.getImagePath()).append("/").append(imageName).toString();
            }
            Path folder = Paths.get(new StringBuilder(xrayDestinationPath + "/" + imagePath).toString());
            byte[] currentXrayImage;
            currentXrayImage = Files.readAllBytes(folder);
            String imageStr = Base64.getEncoder().encodeToString(currentXrayImage);
            currentImage.setMimeType(imageStr);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        if(currentImage != null) {
            System.out.println("Current Image " + currentImage);
        } else {
            System.out.println("current Image is null");
        }
        return currentImage;
    }

    public void setCurrentXrayImage(String currentXrayImage) {
        this.currentXrayImage = currentXrayImage;
    }

    /**
     * Converts the image array in Json format to be used in the view JavaScript
     * that creates the gallery.
     *
     * @return String representation of the array in Json format.
     */
    public String getImagesAsJson() {
//        annotationsAsJson = new Gson().toJson(selectedTeethItemsForJavascript);

        isLoadingImagesFromPageload = true;
        ejbFileSenderClient.init();
        Response response = ejbFileSenderClient.getImageListByStudy(getCurrentStudy());
        String jsonString = response.readEntity(String.class);
//        String jsonString = new Gson().toJson(convertImageListToBase64());
        isLoadingImagesFromPageload = false;
//        System.out.println("the json:::: " + jsonString);
        return jsonString;
    }

    /**
     * Converts the last image taken in Json format to be used in the view
     * JavaScript that creates the gallery.
     *
     * @param imageId
     * @return String representation of the image in Json format.
     */
    public String getImageAsJson(int imageId) {
//        annotationsAsJson = new Gson().toJson(selectedTeethItemsForJavascript);

        isLoadingImagesFromPageload = true;
        String jsonString = new Gson().toJson(convertLastXrayImageToBase64(imageId));
        isLoadingImagesFromPageload = false;
        return jsonString;
    }

    /**
     * Converts the last image taken in Json format to be used in the view
     * JavaScript that creates the gallery.
     *
     * @param exportModel
     * @return String representation of the image in Json format.
     */
    public String getImageAsJson(Images exportModel) {
//        annotationsAsJson = new Gson().toJson(selectedTeethItemsForJavascript);

        isLoadingImagesFromPageload = true;
        String jsonString = new Gson().toJson(convertLastXrayImageToBase64(exportModel));
        isLoadingImagesFromPageload = false;
        return jsonString;
    }

    public ViewDicomTags getTagsToShowFromXml(HashMap<String, com.innotech.xraymanagerapp.model.DicomTags> tagList, String json) {
        ViewDicomTags tts = new ViewDicomTags();
        try {
            tts.setPatientId(tagList.get("00100020").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            tts.setPatientName(tagList.get("00100010").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            tts.setPatientSex(tagList.get("00100040").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientSpecie(tagList.get("00102201").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientBirthdate(JsfUtil.convertToDateFromString(tagList.get("00100030").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDate(JsfUtil.convertToDateFromString(tagList.get("00080020").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setSeriesDate(JsfUtil.convertToDateFromString(tagList.get("00080021").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDescription(tagList.get("00081030").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102297") != null) {
                if (tagList.get("00102297").getTagValue() != null) {
                    tts.setOwnerName(tagList.get("00102297").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080018") != null) {
                if (tagList.get("00080018").getTagValue() != null) {
                    tts.setImagePattern(tagList.get("00080018").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0008103E") != null) {
                if (tagList.get("0008103E").getTagValue() != null) {
                    tts.setSeriesDescription(tagList.get("0008103E").getTagValue());
                } else if (tagList.get("00181030") != null) {
                    tts.setSeriesDescription(tagList.get("00181030").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {

            tts.setInstitutionName(tagList.get("00080080").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (Objects.nonNull(tagList.get("00280030"))) {
                tts.setPixelSpacing(tagList.get("00280030").getTagValue());
            } else {
                if (Objects.nonNull(tagList.get("00181164"))) {
                    tts.setPixelSpacing(tagList.get("00181164").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setFullJsonString(json);
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        return tts;
    }

    public ViewDicomTags getTagsToShowFromXmlForCloudServer(HashMap<String, com.innotech.xraymanagerapp.model.DicomTags> tagList, String json) {
        ViewDicomTags tts = new ViewDicomTags();
        try {
            tts.setPatientId(tagList.get("00100020").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientName(tagList.get("00100010").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientSex(tagList.get("00100040").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientSpecie(tagList.get("00102201").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDate(JsfUtil.convertToDateFromString(tagList.get("00080020").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setSeriesDate(JsfUtil.convertToDateFromString(tagList.get("00080021").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDescription(tagList.get("00081030").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00100030") != null) {
                if (tagList.get("00100030").getTagValue() != null) {
                    tts.setPatientBirthdate(JsfUtil.convertToDateFromString(tagList.get("00100030").getTagValue(), "yyyyMMdd"));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080050") != null) {
                tts.setPatientAccessionCode(tagList.get("00080050").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102201") != null) {
                tts.setPatientSpecie(tagList.get("00102201").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102201") != null) {
                tts.setPatientSpecie(tagList.get("00102201").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0020000D") != null) {
                tts.setStudyInstanceUID(tagList.get("0020000D").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080018") != null) {
                tts.setsOPInstanceUID(tagList.get("00080018").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0020000E") != null) {
                tts.setSeriesInstanceUID(tagList.get("0020000E").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102297") != null) {
                if (tagList.get("00102297").getTagValue() != null) {
                    tts.setOwnerName(tagList.get("00102297").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080018") != null) {
                if (tagList.get("00080018").getTagValue() != null) {
                    tts.setImagePattern(tagList.get("00080018").getTagValue());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(ViewerController.class
                    .getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0008103E") != null) {
                if (tagList.get("0008103E").getTagValue() != null) {
                    tts.setSeriesDescription(tagList.get("0008103E").getTagValue());
                } else if (tagList.get("00181030") != null) {
                    tts.setSeriesDescription(tagList.get("00181030").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class
                    .getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (Objects.nonNull(tagList.get("00280030"))) {
                tts.setPixelSpacing(tagList.get("00280030").getTagValue());
            } else {
                if (Objects.nonNull(tagList.get("00181164"))) {
                    tts.setPixelSpacing(tagList.get("00181164").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, e.getMessage());
        }

        try {
            if (tagList.get("00080080").getTagValue().toLowerCase().contains("cambie animal hospital") || tagList.get("00080080").getTagValue().toLowerCase().contains("anderson animal hospital")) {
                tts.setInstitutionName("Vancouver South Animal Hospital");
            } else {
                tts.setInstitutionName(tagList.get("00080080").getTagValue());
            }
            tts.setFullJsonString(json);
        } catch (Exception e) {
            Logger.getLogger(ViewerController.class
                    .getName()).log(Level.SEVERE, e.getMessage());
        }
        return tts;
    }

    public ViewDicomTags getTagsToShowFromDatabase(Integer imageId) {
        return ejbViewDicomTagsFacade.findByViewImageId(imageId);// list of tags 
    }

    public String getSeriesDescription(Images exportModel) {
        String seriesDescription;
        if (exportModel.getAnnotationsId() != null) {
            if (Objects.nonNull(exportModel.getAnnotationsId().getTeethNumberId())) {
                TeethNumbers tn = exportModel.getAnnotationsId().getTeethNumberId();
                seriesDescription = new StringBuilder(tn.getName()).append(" ").append(tn.getNumber()).append(" ").append(tn.getSide()).toString();
            } else if (Objects.nonNull(exportModel.getAnnotationsId().getBodyPartsId()) && Objects.nonNull(exportModel.getAnnotationsId().getBodyPartViewId())) {
                BodyParts tn = exportModel.getAnnotationsId().getBodyPartsId();
                BodyPartViews tnv = exportModel.getAnnotationsId().getBodyPartViewId();
                seriesDescription = new StringBuilder(tn.getDescription()).append(" ").append(tnv.getAbbreviation()).toString();
            } else {
                seriesDescription = "No DescriptionProvided";
            }
        } else {
            seriesDescription = exportModel.getSeriesDescription();
        }
        return seriesDescription;
    }

    private void replaceTagsToImage(Images exportModel, Annotations newAnnotation) {
        // the tags converted in an object (model.TagsToShow), that will be shown on the viewer
        ViewDicomTags vdt = getTagsToShowFromDatabase(exportModel.getId());
        String seriesDescription = new StringBuilder(newAnnotation.getTeethNumberId().getName())
                .append(" ")
                .append(newAnnotation.getTeethNumberId().getNumber()).
                append(" ").
                append(newAnnotation.getTeethNumberId().getName()).toString();
        vdt.setSeriesDescription(seriesDescription);
    }

    private String getSex(Images exportModel) {
        if (exportModel.getStudyId().getPatientId().getSex() != null) {
            return exportModel.getStudyId().getPatientId().getSex() ? "M" : "F";
        }
        return "";
    }

    private String getStudyDate(Images exportModel) {
        if (exportModel.getStudyId().getExternalDate() != null) {
            return exportModel.getStudyId().getExternalDate();
        }
        return "";
    }

    private String getBirthDate(Images exportModel) {
        if (exportModel.getStudyId().getPatientId().getBirthDate() != null) {
            return JsfUtil.getAge(exportModel.getStudyId().getPatientId().getBirthDate(), false);
        }
        return "";
    }

    public ExportEmailModel getExportEmailModel(Images exportModel) throws NullPointerException, ParserConfigurationException, SAXException, IOException {
        String seriesDescription = getSeriesDescription(exportModel);
        String sex = getSex(exportModel);
        String studyDate = getStudyDate(exportModel);
        String birthDate = getBirthDate(exportModel);
        ViewDicomTags vdt;
        if (Objects.nonNull(exportModel.getDicomTags())) {
            vdt = getTagsToShowFromXml(new XmlReader().createTagList(exportModel.getDicomTags()), exportModel.getDicomTags());
            if (Objects.isNull(vdt.getAcquisitionDate())) {
                vdt.setAcquisitionDate(vdt.getSeriesDate());
            }
        } else {
            vdt = getTagsToShowFromDatabase(exportModel.getId());
        }
        ExportEmailModel exportEmailModel = new ExportEmailModel(
                exportModel.getId(),
                seriesDescription,
                exportModel.getImageFile(),
                exportModel.getPattern(),
                exportModel.getImageAnnotationStateList(),
                vdt,
                exportModel.getStudyId().getPatientId().getName(),
                birthDate,
                studyDate,
                sex
        //                ,imageSource
        );
        return exportEmailModel;
    }

    private ExportEmailModel convertLastXrayImageToBase64(int imageId) {
        try {
            Images exportModel = ejbImagesFacade.find(imageId);
            return getExportEmailModel(exportModel);
        } catch (NullPointerException | ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    private ExportEmailModel convertLastXrayImageToBase64(Images exportModel) {
        try {
            return getExportEmailModel(exportModel);
        } catch (NullPointerException | ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    private String getEmailPredefinedMessage(String emailBodyB, String patientName, String studyDate, String clinicName, String clinicAddress, String clinicPhone) {
        try {
            if (ejbEmailBusinessController.getSelected().getEmailMessage() != null) {
                String fullMessage = ejbEmailBusinessController.getSelected().getEmailMessage().replace("@MESSAGE", emailBodyB);
                fullMessage = fullMessage
                        .replace("@PATIENT_NAME", patientName)
                        .replace("@STUDY_DATE", studyDate)
                        .replace("@CLINIC_NAME", clinicName)
                        .replace("@CLINIC_ADDRESS", clinicAddress)
                        .replace("@CLINIC_PHONE", clinicPhone);
                return fullMessage;
            }
        } catch (NullPointerException e) {
        }
        return "";
    }

    private String getEmailPredefinedSubject(String patientName) {
        try {
            if (ejbEmailBusinessController.getSelected().getEmailSubject() != null) {
                return ejbEmailBusinessController.getSelected().getEmailSubject().replace("@PATIENT_NAME", patientName);
            }
        } catch (NullPointerException e) {
        }
        return "";
    }

    /**
     * Sends an e-mail with the given files
     *
     * @param filesToSend The list of files to be sent
     * @return
     */
    public String emailSend(List<File> filesToSend) {
        String returnMessage;
        String emailBodyB = emailBody == null ? "" : emailBody;

        String clinicAddress = dicomTags.getClinicAddress() == null ? "" : dicomTags.getClinicAddress();
        String clinicPhonenumber = dicomTags.getClinicPhoneNumber() == null ? "" : dicomTags.getClinicPhoneNumber();

        emailBodyB = getEmailPredefinedMessage(emailBodyB, dicomTags.getPatientName(),
                JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), "MMM dd, yyyy"),
                dicomTags.getInstitutionName(),
                clinicAddress,
                clinicPhonenumber);

        String emailSubject = getEmailPredefinedSubject(dicomTags.getPatientName());

        if (TSLEmailSender.sendTSLEmail(ejbEmailBusinessController.getSelected().getHostName(), ejbEmailBusinessController.getSelected().getSmtpPort(), ejbEmailBusinessController.getSelected().getEmailUser(),
                ejbEmailBusinessController.getSelected().getEmailPassword(), ejbEmailBusinessController.getSelected().getEmailFrom(), customerEmail,
                emailSubject, emailBodyB, filesToSend, dicomTags.getPatientName(), dicomTags.getId())) {
            returnMessage = ResourceBundle.getBundle("/Bundle").getString("EmailSuccess");
            Logger.getLogger(ViewerBusinessController.class
                    .getName()).log(Level.INFO, returnMessage);
//            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EmailSuccess"));
//                org.apache.commons.io.FileUtils.cleanDirectory(new File(tempPath));
        } else {
            returnMessage = ResourceBundle.getBundle("/Bundle").getString("EmailError");
            Logger.getLogger(ViewerBusinessController.class
                    .getName()).log(Level.INFO, returnMessage);
//            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EmailError"));
        }
        return returnMessage;
    }

    private String[] validateSelectedmageList() {
        if (imagesList != null) {
            String[] selectedImagesList = imagesList.replace(" ", "").split(",");
            if (selectedImagesList.length > 0) {
                return selectedImagesList;
            }
        }
//        JsfUtil.addErrorMessage("Must select at least one image to export");
        throw new NullPointerException("Must select at least one image.");
    }

    /**
     * Sends an e-mail with the selected images in the chosen format.The format
     * is chosen by the user on the dicom viewer
     *
     * @return
     */
    public String sendSelectedImagesEmail() {
        String returnMessge = ResourceBundle.getBundle("/Bundle").getString("EmailError");
        dicomTags = new ViewDicomTags();
        if (ejbEmailBusinessController != null) {
            if (ejbEmailBusinessController.getSelected() != null) {
                List<File> convertedImages = new ArrayList();
                if (emailType.toLowerCase().equals("dicom")) {
                    Queue<File> dicomQueue = createDicomQueue(exportPath, false, true);
                    if (dicomQueue != null) {
                        while (!dicomQueue.isEmpty()) {
                            convertedImages.add(dicomQueue.poll());
                        }
                    }
                } else {
                    convertedImages = createImageList(exportPath, emailType);
                }
                if (convertedImages.size() > 0) {
                    returnMessge = emailSend(convertedImages);
                    JsfUtil.cleanDirectory(exportPath, emailType);
                }
            }
        }
        return returnMessge;
    }

    /**
     * Creates and returns a list with files. This files could be x-ray images
     * (JPEG) or DICOM.
     *
     * @return The file list
     */
    public List<File> imagesToDownloadList() {
        List<File> filesImages = new ArrayList();
        dicomTags = new ViewDicomTags();
        if (validateSelectedmageList() != null) {
            if (imageExportFormat.toLowerCase().equals("dicom")) {
                Queue<File> dicomQueue = createDicomQueue(exportPath, false, true);
                if (dicomQueue != null) {
                    while (!dicomQueue.isEmpty()) {
                        // send the dicom file by using dcm4che3 library version 5.18
                        filesImages.add(dicomQueue.poll());
                    }
                }
            } else {
                filesImages = createImageList(exportPath, imageExportFormat);
            }
        }
        return filesImages;
    }

    /**
     * Exports as a zip file only the selected images of the selected study
     *
     * @return
     */
    public byte[] zipBytesSelectedImages() {
        List<File> convertedImages = imagesToDownloadList();
        if (convertedImages.size() > 0) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ZipOutputStream zos = new ZipOutputStream(baos);) {

                convertedImages.forEach((p) -> {
                    try {
                        ZipEntry entry = new ZipEntry(p.getName());
                        zos.putNextEntry(entry);
                        byte[] currentXrayImage_ = Files.readAllBytes(p.toPath());
                        zos.write(currentXrayImage_);
                        zos.closeEntry();
                    } catch (IOException ex) {
                        Logger.getLogger(ViewerController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                });
                zos.close();

                return baos.toByteArray();
            } catch (NullPointerException | IOException ex) {
                Logger.getLogger(ViewerController.class
                        .getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }

    public boolean sendDicomFile(File dicom) {
        try {
            if (selectedDicomServer != null) {
                File baseDirectory;
                Device device;
                Connection conn;
                StoreTool dct;
                String testDescription = description;//"Description of test by IMI dicomDestinationFile senddd";
                String host = selectedDicomServer.getHost();//"127.0.0.1";//dicomserver.uk.co
                int port_ = selectedDicomServer.getPort();//11112;// or 104
                String aeTitle = selectedDicomServer.getAeTitle();//"IMIDICOMSTORAGE";
//                if the file name ends with the extension .exists means this is a dicom file that was already created and weonly need to send it from the 
//                  current file path instead of the temporary path
                String dicomFileName = dicom.getName();
                if (dicomFileName.endsWith(".exists")) {
                    dicomFileName = dicomFileName.replace(".exists", "");
                    String path = dicom.getPath().replace(dicomFileName, "").replace(".exists", "");
                    baseDirectory = new File(path);
                } else {
                    baseDirectory = new File(dicomSendPath);
                }

                System.out.println("DICOM Server: " + host);
                System.out.println("DICOM Port: " + port_);
                System.out.println("DICOM Server AETitle: " + aeTitle);
                device = new Device("storescu");
                String sourceAETitle = selectedDicomServer.getDescription();
                conn = new Connection();
                device.addConnection(conn);

                dct = new StoreTool(host, port_, aeTitle, baseDirectory, device, sourceAETitle, conn);
                String[] fileNmes = {dicomFileName};
                dct.store(testDescription, fileNmes);
                return true;
            }
            return false;
        } catch (NullPointerException | IOException | InterruptedException | IncompatibleConnectionException | GeneralSecurityException ex) {
            JsfUtil.addErrorMessage(ex, ex.getMessage());
            Logger.getLogger(SensorImageController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String getImageFormatedName(ViewDicomTags dicomTags, int counter) {
        String patientId = dicomTags.getPatientId();
        String patientNameExport = dicomTags.getPatientName();
        String studyDate = "";
        if (dicomTags.getStudyDate() != null) {
            studyDate = JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), DATE_FORMAT);
        }
        String imageAnnotation = dicomTags.getSeriesDescription() == null ? "" : dicomTags.getSeriesDescription();
        String separator = "_";
        String imageName = new StringBuilder(patientId)
                .append(separator)
                .append(patientNameExport)
                .append(separator)
                .append(studyDate)
                .append(separator)
                .append(imageAnnotation)
                .append(separator)
                .append(counter).toString();
        return imageName;

    }

    /**
     * Creates a file list with the images that were selected on the Dicom
     * Viewer
     *
     * @param dicomDestinationPath Temporary path were the image will be stored
     * before being downloaded or e-mailed or exported
     * @param imageFormat Format that the images will be converted to could be:
     * JPEG, BMP, PNG or TIF
     * @return The converted image list
     */
    public List<File> createImageList(String dicomDestinationPath, String imageFormat) {
        List<File> filesImages = new ArrayList();
        try {
            String[] selectedImageIdsList = validateSelectedmageList();
            int counter = 0;
            for (String integer : selectedImageIdsList) {
                dicomTags = getDicomTagsByImageId(Integer.parseInt(integer.trim()));

                String imagePath = xrayDestinationPath;
                // If there is not image path means the path is xrayDestinationPath (old way)

                boolean isJpeg = dicomTags.getImagePattern().endsWith(".jpg");
                if (dicomTags.getImagePath() != null) {
                    imagePath = new StringBuilder(xrayDestinationPath).append(dicomTags.getImagePath()).toString();// image path exists (new way path with petName and study dateTime)
                }

                // if the image was acquired from an external dicom file, meas that the image has already full tags and we could use those tags instead of the ones we obtain from the databaSE
                if (dicomTags.getDicomTags() != null) {
                    dicomTags = getDicomTagsByImageDicomTags(dicomTags);
                }
                //System.out.println("Image Name::::: "+dicomTags.getImagePattern().replace(".bmp", "").replace(".jpg", ""));
                String imageName = dicomTags.getImagePattern().replace(".bmp", "").replace(".jpg", "");
                String newImageName = getImageFormatedName(dicomTags, ++counter);

                String imageEmbeededTags;
                imageEmbeededTags = dicomTags.getInstitutionName() == null ? "" : dicomTags.getInstitutionName() + "\n";
                imageEmbeededTags += JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), DATE_FORMAT) + "\n"
                        + dicomTags.getPatientName() + "\n";

                imageEmbeededTags += dicomTags.getSeriesDescription() == null ? "" : dicomTags.getSeriesDescription() + "\n";
                imageEmbeededTags += dicomTags.getPatientBirthdate() == null ? "" : JsfUtil.formatDateWithPattern(dicomTags.getPatientBirthdate(), DATE_FORMAT) + "\n";
                imageEmbeededTags += dicomTags.getPatientBirthdate() == null ? "" : JsfUtil.getAge(dicomTags.getPatientBirthdate(), false) + "\n";
                imageEmbeededTags += dicomTags.getPatientSex() == null ? "" : dicomTags.getPatientSex() + "\n";
                imageEmbeededTags += dicomTags.getPatientSpecie() == null ? "" : dicomTags.getPatientSpecie();

                File image;
                String currentImageFormat = isJpeg ? ".jpg" : ".bmp";

                if (imageFormat.contains("tif")) {
                    image = ImageFormatConverter.convertFile(
                            new File(imagePath + "/" + imageName + currentImageFormat), dicomDestinationPath, imageFormat,
                            imageEmbeededTags,
                            newImageName, 100
                    );
                } else {
                    image = ImageFormatConverter.convertAndGet(
                            new File(imagePath + "/" + imageName + currentImageFormat), dicomDestinationPath, imageFormat,
                            imageEmbeededTags,
                            newImageName
                    );

                }

                patientName = dicomTags.getPatientName();
                filesImages.add(image);
            }
        } catch (NullPointerException ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
            Logger.getLogger(SensorImageController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return filesImages;
    }

    /**
     * Creates DICOM files on a given folder and put them in a queue
     *
     * @param dicomDestinationPath
     * @param isToDicomSend flag that defines if this function is being called
     * from dicom send feature
     * @param isOnDicomExport flag that defines if this function is being called
     * from dicom export feature
     * @return The queue with the the created DICOM files
     */
    public Queue<File> createDicomQueue(String dicomDestinationPath, boolean isToDicomSend, boolean isOnDicomExport) {
        String[] selectedImageIdsList = validateSelectedmageList();
        if (selectedImageIdsList != null) {
            Queue<File> dicomQueue = new LinkedList();
            int counter = 0;
            for (String imageId : selectedImageIdsList) {
                try {
                    dicomTags = getDicomTagsByImageId(Integer.parseInt(imageId.trim()));

                    String dicomPath = xrayDestinationPath;
                    // If there is not image path means the path is xrayDestinationPath (old way)

                    String tags = dicomTags.getDicomTags();
                    boolean isFromMetron = false;

                    if (tags != null) {
                        isFromMetron = tags.startsWith(("Metron..."));
                    }
                    // if the image was acquired from an external dicom file, meas that the image has already full tags and we could use those tags instead of the ones we obtain from the databaSE
                    if (dicomTags.getDicomTags() != null && !isFromMetron) {
                        if (dicomTags.getImagePath() != null) {
                            dicomPath = new StringBuilder(xrayDestinationPath).append(dicomTags.getImagePath()).toString();// image path exists (new way path with petName and study dateTime)
                        }
                        dicomTags = getDicomTagsByImageDicomTags(dicomTags);
                        dicomTags.setImagePath(dicomPath);
                    }

                    patientName = dicomTags.getPatientName();
//                    Adds the dicom file that will be sent or exported, to the dicom queue.
                    dicomQueue.add(createSingleDicomFile(dicomDestinationPath, dicomTags, false, isToDicomSend, isOnDicomExport, ++counter));

                } catch (NumberFormatException ex) {
                    Logger.getLogger(ViewerController.class
                            .getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            return dicomQueue;
        }
        return null;
    }

    /**
     * Creates a JPEG file and a DICOM file in a given folder file are acquired
     * based on a given imageId.n path.imageId.
     *
     * @param dicomDestinationPath where the DICOM will be stored
     * @param dicomTags The Tags for the the DICOM
     * @param isOnImageCreation Should be true if this function is being called
     * on image x-ray creation
     * @param isToDicomSend flag that defines if this function is being called
     * from dicom send feature
     * @param isOnDicomExport flag that defines if this function is being called
     * from dicom export feature
     * @param counter
     * @return The DICOM file
     */
    public File createSingleDicomFile(String dicomDestinationPath, ViewDicomTags dicomTags,
            boolean isOnImageCreation, boolean isToDicomSend, boolean isOnDicomExport, int counter) {
        try {
            String imageName = dicomTags.getImagePattern().replace(".bmp", "").replace(".jpg", "");
            String newImageName = getImageFormatedName(dicomTags, counter);
            System.out.println("IMage Name: " + imageName);

//          Creates an empty dicom file
            File dicom;

            // png source image to create the DICOM file
            String destinationPathh = dicomDestinationPath;
            if (!isOnImageCreation) {
                destinationPathh = xrayDestinationPath;
            }
            String imagePath = destinationPathh;

            // If there is not image path means the path is xrayDestinationPath (old way)
            if (dicomTags.getImagePath() != null) {
                if (!dicomTags.getImagePath().contains(destinationPathh)) {
                    imagePath = new StringBuilder(destinationPathh).append(dicomTags.getImagePath()).toString();// image path exists (new way path with petName and study dateTime)
                } else {
                    imagePath = dicomTags.getImagePath();
                }
            }
            boolean isLocalServer = ejbConfigurationBusinessController.getIsXrayServer();
            if (dicomTags.getFullJsonString().equals("") || !isLocalServer) {

                String fileFormat = ".bmp";
                String tags = dicomTags.getDicomTags();
                boolean isFromMetron = false;

                if (tags != null) {
                    isFromMetron = tags.startsWith(("Metron..."));
                }
                if (!isLocalServer || isFromMetron || isOnImageCreation) {
                    fileFormat = ".jpg";
                }
                String sourceImage = imagePath + "/" + imageName + fileFormat;

//                   Creates a jpeg image and stores it in the dicom queue folder (dicomSendPath)
//                  If the function is called on x-ray image creation the destination path is the same imagePath
                File jpg;
                if (isOnImageCreation) {
                    dicomDestinationPath = imagePath + "/";
                    jpg = new File(dicomDestinationPath + imageName + ".jpg");
                } else {
                    jpg = ImageFormatConverter.convertAndGet(
                            new File(sourceImage), dicomDestinationPath, "jpg",
                            "",
                            imageName
                    );
                }
//                System.out.println("Before create the dicom file.....");
                if (isOnDicomExport)// change dicom name
                {
                    dicom = new File(dicomDestinationPath + newImageName + ".dcm");
                } else {
                    dicom = new File(dicomDestinationPath + imageName + ".dcm");
                }
//                    Fills the dicom file based on the image(JPG) and the tags
                Image2Dcm image2dcm = new Image2Dcm();
                imageSize = new ImageSize();
                image2dcm.createDicom(jpg, dicom, dicomTags, imageSize);
            } else {
                if (isToDicomSend) {
                    dicom = new File(imagePath + "/" + imageName + ".dcm.exists");
                } else {
                    dicom = new File(imagePath + "/" + imageName + ".dcm");
                }

            }
            return dicom;
        } catch (IOException | DicomException ex) {
            Logger.getLogger(ViewerController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean dicomSendByQueue() {
        boolean isSent = false;
        try {
            System.out.println("dicomSendPath: " + dicomSendPath);
            dicomTags = new ViewDicomTags();
            Queue<File> dicomQueue = createDicomQueue(dicomSendPath, true, false);
            if (dicomQueue != null) {
                while (!dicomQueue.isEmpty()) {
                    // send the dicom file by using dcm4che3 library version 5.18
                    if (!(isSent = sendDicomFile(dicomQueue.poll()))) {
                        break;
                    }
                }
            }

            if (isSent) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "All selected Dicom files were sent successfully");
                JsfUtil.cleanDirectory(dicomSendPath, "dcm");
                JsfUtil.cleanDirectory(dicomSendPath, "jpg");
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error sending Dicom files, Select a DICOM Server");
            }

        } catch (NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error sending Dicom files, Select at least one image to be sent: {0}", e.getMessage());
        }
        return isSent;
    }

    public ViewDicomTags getDicomTagsByImageDicomTags(ViewDicomTags dicomTags) {
        try {
            String tagsAsJson = dicomTags.getDicomTags();
            if (tagsAsJson != null) {
                boolean isXrayServer = JsfUtil.isXrayLocalServer();
                if (!isXrayServer) {
                    dicomTags = getTagsToShowFromXmlForCloudServer(new XmlReader().createTagList(tagsAsJson), tagsAsJson);
                } else {
                    dicomTags = getTagsToShowFromXml(new XmlReader().createTagList(tagsAsJson), tagsAsJson);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ViewerController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return dicomTags;
    }

    public ViewDicomTags getDicomTagsByImageId(Integer imageId) {
        return ejbViewDicomTagsFacade.findByViewImageId(imageId);
    }

    /**
     * Used when this class is no on the EJB context especially when the viewer
     * is open directly from the study list and not from the x-ray page
     *
     * @param imageId
     * @param ejbViewDicomTagsFacade
     * @return
     */
    public ViewDicomTags getDicomTagsByImageId(Integer imageId, com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade ejbViewDicomTagsFacade) {
        return ejbViewDicomTagsFacade.findByViewImageId(imageId);
    }

    public List<String> getImages() {
        getCurrentStudyImages();
        System.out.println("getting new images... size: " + images.size());
        return images;
    }

    private void getCurrentStudyImages() {
        if (images.size() < 1) {
            images = new ArrayList();
            getImagesFromStudies().forEach((currentImages) -> {
                String imagePath = currentImages.getPattern();
                if (currentImages.getImagePath() != null) {
//                String imageName = currentImages.getPattern().replace(".bmp", ".jpg");
                    String imageName = currentImages.getPattern();
                    imagePath = new StringBuilder(currentImages.getImagePath()).append("/").append(imageName).toString();
                }
                images.add(imagePath);
            });

        }
    }

    private List<Images> getImagesFromStudies() {
        System.out.println("getting new images BEFORE SHOWING THEM... size: " + images.size());
        imageList = ejbImagesFacade.findByStudyId("Images.findByStudyId", "studyId", getCurrentStudy().getId());
        if (imageList.isEmpty()) {
            imageList = ejbImagesFacade.findByStudyId("Images.findByStudyIdAnnotation", "studyId", getCurrentStudy().getId());
        }
        return imageList;
    }

    private void addXrayImageToImagesObject(Images image) {
        // getting the image location directory
        String fullPath = new StringBuilder(ejbConfigurationBusinessController.getConfigurationMap().get(ConfigurationProperties.XRAY_DESTINATION_PATH))
                .append("/")
                .append(image.getImagePath())
                .append("/")
                .append(image.getPattern()).toString();
        File f = new File(fullPath);
        image.setImageFile(JsfUtil.convertImageToBase64(f));
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public static boolean deleteImageFromSourcePath(String fileName) throws IOException {
        return Files.deleteIfExists(getImageFromSourcePath(fileName).toPath()); //surround it in try catch block
    }

    public static File getImageFromSourcePath(String fileName) throws IOException {
        file = new File(sourceFilePath, fileName);
        return file;
    }

    public static InputStream getInputImage(File file) throws IOException {
        inputImage = new FileInputStream(file);
        return inputImage;
    }

    public static void setInputImage(InputStream aInputImage) {
        inputImage = aInputImage;
    }

    public static String getSourceFilePath() {
        return sourceFilePath;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getImageExportFormat() {
        return imageExportFormat;
    }

    public void setImageExportFormat(String imageExportFormat) {
        this.imageExportFormat = imageExportFormat;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StreamedContent getImagesZip() {
        return imagesZip;
    }

    public void setImagesZip(StreamedContent imagesZip) {
        this.imagesZip = imagesZip;
    }

    public String getImagesList() {
        return imagesList;
    }

    public void setImagesList(String imagesList) {
        this.imagesList = imagesList;
    }

    public void storeDicom(StoreTool dct, String testDescription, File dicom)
            throws IOException, InterruptedException, IncompatibleConnectionException, GeneralSecurityException {
        String[] fileNmes = {dicom.getPath()};
        dct.store(testDescription, fileNmes);
    }

    public boolean checkDicomServerConnection(StoreTool dct) {
        ApplicationEntity ae = dct.getAE();
        boolean isServerReady = dct.isServerReady(dct.getStoreSCU(ae), ae);
        return isServerReady;
    }

    /**
     * Reads all the dicom files inside a given folder.
     *
     * @param folderPath
     * @return A list with the full path of each file found.
     */
    public static List<String> readAllFiles(String folderPath) {
        List<String> result = new ArrayList();
        try (Stream<Path> walk = Files.walk(Paths.get(folderPath))) {

            result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".dcm")).collect(Collectors.toList());

            result.forEach(System.out::println);

        } catch (IOException e) {
        }
        return result;
    }

//    public static void main(String[] args) {
//        ReadAllDicomFiles.readAllFiles("D:\\ImageLoaderWSImages\\dicomQueue");
//    }
    public static void main(String[] args) throws IOException {

//        ViewerBusinessController vc = new ViewerBusinessController();
//        vc.testDicomCreationAndSend();
        //vc.testDicomCreationAndSend();
//        System.out.println("Is listening? " + vc.testDicomServerConnection());
//        for (String currentFile : readAllFiles("B:\\Innotech\\Dicoms\\Queue")) {
//            vc.testDicomSend(currentFile);
//            break;
//        }
    }

    public DicomServers getSelectedDicomServer() {
        return selectedDicomServer;
    }

    public void setSelectedDicomServer(DicomServers selectedDicomServer) {
        this.selectedDicomServer = selectedDicomServer;
    }

    /**
     * * Edit and delete selected images from the viewer
     */
    /**
     * @return the quantity of deleted images
     */
    public int deleteSelectedImages() {
        int quantity = 0;
        try {
            String[] selectedImageIdsList = validateSelectedmageList();
            deletedImageIdsList = selectedImageIdsList;
            for (String integer : selectedImageIdsList) {
                quantity += ejbImagesFacade.deleteSelectedImageById(Integer.parseInt(integer));//change the image status in database
                imagesAsFile.remove(Integer.parseInt(integer));// remove the image from the current image map to avoid filling the map again throught a database query
            }
            if (quantity > 0) {
                JsfUtil.addSuccessMessage(quantity + " Images have been deleted");
            } else {
                JsfUtil.addErrorMessage("No Images have been deleted");
            }
        } catch (NumberFormatException | NullPointerException e) {
            JsfUtil.addErrorMessage(e.getMessage() + ": No Images have been updated. Please select at least one image");
        }
        return quantity;
    }

    public String editSelectedImages() {//
        int quantity = 0;
        try {

            if (selectedTooth != null) {
                String[] selectedImageIdsList = validateSelectedmageList();
                initializeLists();
                isLoadingImagesFromPageload = true;
                for (String integer : selectedImageIdsList) {
//                gets the selected image
                    Images image = ejbImagesFacade.find(Integer.parseInt(integer));
                    if (image != null) {
                        // gets the annotations list of the current study
                        List<Annotations> annotationsList = ejbAnnotationsFacade.findByStudyId(image.getStudyId().getId());
                        boolean annotationExists = false;
                        for (Annotations annotations : annotationsList) {
//                        if the annotation for the selected tooth exists, then adds the image to that annotation
                            if (annotations.getTeethNumberId().getId().equals(selectedTooth.getId())) {
                                annotations.setStatus(true);
                                image.setAnnotationsId(annotations);
                                ejbImagesFacade.edit(image);
                                annotationExists = true;
                                quantity++;
                                break;
                            }
                        }
//                            if the annotation does not exist for the current study, then creates the annotation and adds it to the selected image
                        if (!annotationExists) {
                            Annotations ann = new Annotations();
                            ann.setTeethNumberId(selectedTooth);
                            ann.setCreationDate(new Date());
                            ann.setStatus(true);
                            ann.setStudyId(image.getStudyId());
                            ann.setUserId(AuthenticationUtils.getLoggedUser());
                            ann.setIsCurrent(false);
                            ann.setIsDone(true);
                            image.setAnnotationsId(ann);
                            ejbImagesFacade.edit(image);
                            replaceTagsToImage(image, ann);
                            quantity++;

                        }
                    }
//                quantity += ejbImagesFacade.editSelectedImageById(Integer.parseInt(integer), selectedTooth.getId());
                }
            }
            if (quantity > 0) {
                JsfUtil.addSuccessMessage(quantity + " Images have been updated");
                //updatXrayView();

            } else {
                JsfUtil.addErrorMessage("No Images have been updated");
            }
        } catch (NumberFormatException | NullPointerException e) {
            JsfUtil.addErrorMessage(e.getMessage() + ": No Images have been updated");
        }
        String uri = JsfUtil.getPreviousUri();
        if (uri.contains("x-ray") || uri.contains("annotations")) {
            return "pretty:x-ray";
        } else {
            return "pretty:viewer";
        }
    }

    public TeethNumbers getSelectedTooth() {
        return selectedTooth;
    }

    public void setSelectedTooth(TeethNumbers selectedTooth) {
        this.selectedTooth = selectedTooth;
    }

    public Studies getCurrentStudy() {
        return currentStudy;
    }

    public void setCurrentStudy(Studies currentStudy) {
        this.currentStudy = currentStudy;
    }

    public ImageSize getImageSize() {
        return imageSize;
    }
}
