/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.business.AnnotationsDentalXrayController;
import com.innotech.xraymanagerapp.controller.ConfigurationController;
import com.innotech.xraymanagerapp.controller.EmailConfigController;
import com.innotech.xraymanagerapp.controller.PetPatientsController;
import com.innotech.xraymanagerapp.controller.dicom.Jpg2Dcm;
import com.innotech.xraymanagerapp.controller.dicom.StoreTool;
import com.innotech.xraymanagerapp.controller.directory.DirectoryManager;
import com.innotech.xraymanagerapp.controller.email.TSLEmailSender;
import com.innotech.xraymanagerapp.controller.export.ImageFormatConverter;
import com.innotech.xraymanagerapp.model.ExportEmailModel;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.StudyAnnotationState;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.DicomTags;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.IncompatibleConnectionException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * @author Alexander Escobar Note that the bean #{sensorImageController} is
 * @ApplicationScoped as it basically represents a stateless service. It could
 * be @RequestScoped, but then the bean would be recreated on every single
 * request, for nothing. It cannot be @ViewScoped, because at the moment the
 * browser needs to download the image, the server doesn't create a JSF page. It
 * can be
 * @SessionScoped, but then it's saved in memory, for nothing.
 */
@Named("sensorImageController")
@RequestScoped
public class SensorImageController implements Serializable {

    @EJB
    com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade ejbViewDicomTagsFacade;
    public static String dentalSensorSourceFilePath;
    public static String flatPanelSourceFilePath;
    public static String xrayDestinationPath;
    public static String tempPath;
    public static String exportPath;
    public static String dicomSendPath;
//    public static String fileName = "test1.png";
    public static File file;
    private static HashMap<Integer, File> jpgImageList;
    private static String jpgImageAnnotationState;
    private static InputStream inputImage;
    private String server;
    private Integer port;
    private String description;
    private DefaultStreamedContent sensorImage;
    private AnnotationsDentalXrayController ac;
    private List<String> images;
    private String customerEmail;
    private String emailBody;
    private String emailType;
    private String imageExportFormat;
    private static boolean isHDRSensor;// if true means current sensor is HDR otherwise, the current one is EZ

    public static Map getJpgImageList() {
        return jpgImageList;
    }

    public static void setJpgImageList(HashMap aJpgImageList) {
        jpgImageList = aJpgImageList;
    }

    public void doubleClickRedirection() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/viewer");
        } catch (IOException ex) {
            Logger.getLogger(SensorImageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getJpgImageAnnotationState() {
        return jpgImageAnnotationState;
    }

    public static void setJpgImageAnnotationState(String aJpgImageAnnotationState) {
        jpgImageAnnotationState = aJpgImageAnnotationState;
    }

//
    @PostConstruct
    public void init() {
        images = new ArrayList();
        initializePathVariables();
    }

    public static void initializePathVariables() {
        if (JsfUtil.getConfigurationMap().size() > 0) {
            isHDRSensor = JsfUtil.getSensorConfiguration();
            if (dentalSensorSourceFilePath == null) {
                System.out.println("SensorImageController.initializePathVariables(): " + isHDRSensor);
                if (isHDRSensor) {
                    dentalSensorSourceFilePath = JsfUtil.getConfigurationMap().get("HDRSensorPath");
                } else {
                    dentalSensorSourceFilePath = JsfUtil.getConfigurationMap().get("EZSourceFilePath");
                }
            }
            if (Objects.isNull(flatPanelSourceFilePath)) {
                flatPanelSourceFilePath = JsfUtil.getConfigurationMap().get("FlatPanelOutputPath");
            }
            if (xrayDestinationPath == null) {
                xrayDestinationPath = JsfUtil.getConfigurationMap().get("XrayDestinationPath");
            }
            if (tempPath == null) {
                tempPath = JsfUtil.getConfigurationMap().get("TempPath");
            }
            if (exportPath == null) {
                exportPath = JsfUtil.getConfigurationMap().get("ExportPath");
            }
            if (dicomSendPath == null) {
                dicomSendPath = JsfUtil.getConfigurationMap().get("DicomSendPath");
            }
        }
    }

    /**
     * get an image from a given path and name
     *
     * @return the image in StreamedContent format for the p:graphicImage
     * component to show the image from an external path
     */
    public StreamedContent getImage() {
        FacesContext context = FacesContext.getCurrentInstance();
        while (true) {
            // get the image name from the parameters on the create annotation form
            String filename = context.getExternalContext().getRequestParameterMap().get("filename");
            try {
//                    It will be showed on the html view
                sensorImage = new DefaultStreamedContent(new FileInputStream(new File(xrayDestinationPath, filename)));
                return sensorImage;

            } catch (Exception e) {
                // if error the loop won't finish, because the system is still looking for an image
                Logger.getLogger(SensorImageController.class
                        .getName()).log(Level.SEVERE, null, e);
            }
            break;
        }
//        }
        // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
        return new DefaultStreamedContent();
    }

    public String getXrayImage() {
        FacesContext context = FacesContext.getCurrentInstance();

        while (true) {
            // get the image name from the parameters on the create annotation form
            String filename = context.getExternalContext().getRequestParameterMap().get("filename");
            try {
                Path folder = Paths.get(xrayDestinationPath + "/test.png");
//                Path filePath = Files.createTempFile(folder, "test", "png");
                byte[] currentXrayImage = Files.readAllBytes(folder);
                String imageStr = Base64.getEncoder().encodeToString(currentXrayImage);
//                    It will be showed on the html view
//                sensorImage2 = new FileInputStream(new File(xrayDestinationPath, "test.png"));
                return imageStr;

            } catch (IOException e) {
//                 if error the loop won't finish, because the system is still looking for an image
                Logger.getLogger(SensorImageController.class
                        .getName()).log(Level.SEVERE, null, e);
                return "";
            }
        }

        // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
    }

    /**
     * Convert the current x-ray image in a bytes map
     *
     * @return The converted image in a base 64 String
     */
    public String getCurrentXrayImage() {
        String imageStr = "";
        try {
            if (getImages().size() > 0) {

                Path folder = Paths.get(new StringBuilder(xrayDestinationPath).append("/").append(images.get(images.size() - 1)).toString());
                byte[] currentXrayImage = Files.readAllBytes(folder);
                imageStr = Base64.getEncoder().encodeToString(currentXrayImage);

            }
        } catch (java.lang.ClassCastException | IOException e) {
            Logger.getLogger(SensorImageController.class
                    .getName()).log(Level.SEVERE, e.getMessage(), e);
        }
//        It will be showed on the html view
        return imageStr;
    }

    /**
     * Convert the image array in Json format to be used in the view JavaScript
     * that creates the gallery.
     *
     * @return String representation of the Json document.
     */
    @Asynchronous
    public String getImagesAsJson() {
//        annotationsAsJson = new Gson().toJson(selectedTeethItemsForJavascript);
        try {

            String jsonString = new Gson().toJson(convertImageListToBase64());
            System.out.println("converting the json: ");
            return jsonString;
        } catch (Exception e) {
            Logger.getLogger(SensorImageController.class
                    .getName()).log(Level.SEVERE, e.getMessage(), e.getCause());
            return null;
        }
    }

    public ViewDicomTags getTagsToShowFromDatabase(Integer imageId) {
        return ejbViewDicomTagsFacade.findByViewImageId(imageId);// list of tags 
    }

    private List<ExportEmailModel> convertImageListToBase64() {
        List<ExportEmailModel> imagesAsFile = new ArrayList();
        try {
//            getImagesFromAnnotations().forEach((exportModel) -> {
            for (Images exportModel : getImagesFromAnnotations()) {
                try {
                    String imagePath = new StringBuilder(xrayDestinationPath).append("/").append(exportModel.getPattern()).toString();

                    // If there is not image path means the path is xrayDestinationPath (old way)
                    if (exportModel.getImagePath() != null) // image path exists (new way path with petName and study dateTime)
                    {
                        String imageName = exportModel.getPattern().replace(".png", ".jpg");

                        imagePath = new StringBuilder(xrayDestinationPath).append(exportModel.getImagePath()).append("/").append(imageName).toString();
                    }
                    Path folder = Paths.get(imagePath);
                    File imageSource = folder.toFile();
                    if (exportModel.getImagePath() == null) // image path exists (new way path with petName and study dateTime)
                    {
                        imageSource = ImageFormatConverter.convertAndGet(imageSource, "", "jpg", "", imageSource.getName().replace(".png", "").replace(".jpg", ""));
                    }
                    byte[] currentXrayImage = Files.readAllBytes(imageSource.toPath());
                    imagesAsFile.add(new ExportEmailModel(exportModel.getId(),
                            exportModel.getAnnotationsId().getTeethNumberId().getId(),
                            Base64.getEncoder().encodeToString(currentXrayImage),
                            exportModel.getPattern(),
                            exportModel.getImageAnnotationStateList(), getTagsToShowFromDatabase(exportModel.getId())));

                } catch (NullPointerException | IOException ex) {
                    Logger.getLogger(SensorImageController.class
                            .getName()).log(Level.SEVERE, ex.getLocalizedMessage());

                }
            }
        } catch (NullPointerException e) {
            Logger.getLogger(SensorImageController.class
                    .getName()).log(Level.SEVERE, e.getLocalizedMessage());

        }
        return imagesAsFile;
    }

    private List<ExportEmailModel> imagesList() {
        List<ExportEmailModel> imagesAsFile = new ArrayList();
        try {
            getImagesFromAnnotations().forEach((exportModel) -> {
                String imagePath = new StringBuilder(xrayDestinationPath).append("/").append(exportModel.getPattern()).toString();

                // If there is not image path means the path is xrayDestinationPath (old way)
                if (exportModel.getImagePath() != null) // image path exists (new way path with petName and study dateTime)
                {
                    imagePath = new StringBuilder(xrayDestinationPath).append(exportModel.getImagePath()).append("/").append(exportModel.getPattern()).toString();
                }
                imagesAsFile.add(
                        new ExportEmailModel(
                                new File(imagePath, exportModel.getPattern()),
                                exportModel.getAnnotationsId(),
                                ZonedDateTime.ofInstant(exportModel.getCreationDate().toInstant(), JsfUtil.getZoneId())
                        )
                );
            });

        } catch (NullPointerException e) {
            Logger.getLogger(SensorImageController.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return imagesAsFile;
    }

    public void sendEmail() {
        EmailConfigController ecc = (EmailConfigController) JsfUtil.getSessionBean("emailConfigController");
        if (ecc != null) {
            if (ecc.getSelected() != null) {
                List<File> convertedImages = new ArrayList();
                int counter = 1;
                for (ExportEmailModel f : imagesList()) {

                    try {
                        String patientName = f.getAnnotations().getStudyId().getPatientId().getName();
                        String formatedDate = JsfUtil.formatDateWithPattern(JsfUtil.zoneDateTimeToDate(f.getImageDate()), "dd/MM/yyyy");
                        String patientAge = JsfUtil.getAge(f.getAnnotations().getStudyId().getPatientId().getBirthDate(), false);
                        String patientSex = f.getAnnotations().getStudyId().getPatientId().getSex() ? "M" : "F";
                        String toothName = f.getAnnotations().getTeethNumberId().getId();
                        if (emailType.contains("tif")) {
                            convertedImages.add(
                                    ImageFormatConverter.convertFile(
                                            f.getFile(), tempPath, emailType,
                                            patientName + "\n"
                                            + formatedDate
                                            + "\n"
                                            + patientAge
                                            + " - "
                                            + patientSex
                                            + "\n"
                                            + toothName,
                                            (counter++) + "_" + patientName.replace(" ", "_") + "_" + toothName, 90));
                        } else {
                            convertedImages.add(
                                    ImageFormatConverter.convertAndGet(
                                            f.getFile(), tempPath, emailType,
                                            patientName + "\n"
                                            + formatedDate
                                            + "\n"
                                            + patientAge
                                            + " - "
                                            + patientSex
                                            + "\n"
                                            + toothName,
                                            (counter++) + "_" + patientName.replace(" ", "_") + "_" + toothName));

                        }
                    } catch (NullPointerException e) {
                        Logger.getLogger(SensorImageController.class
                                .getName()).log(Level.SEVERE, null, e);
                    }
                }
                if (TSLEmailSender.sendTSLEmail(ecc.getSelected().getHostName(), ecc.getSelected().getSmtpPort(), ecc.getSelected().getEmailUser(),
                        ecc.getSelected().getEmailPassword(), ecc.getSelected().getEmailFrom(), customerEmail,
                        ecc.getSelected().getEmailSubject(), emailBody, convertedImages, "", 1)) {
                    try {
                        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EmailSuccess"));
                        org.apache.commons.io.FileUtils.cleanDirectory(new File(tempPath));

                    } catch (IOException ex) {
                        Logger.getLogger(SensorImageController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EmailError"));
                }
            }
        }
//      
    }

    public void exportImage() {
        boolean isOk = false;
        for (ExportEmailModel f : imagesList()) {
            try {
                String patientName = f.getAnnotations().getStudyId().getPatientId().getName();
                String formatedDate = JsfUtil.formatDateWithPattern(JsfUtil.zoneDateTimeToDate(f.getImageDate()), "dd/MM/yyyy");
                String patientAge = JsfUtil.getAge(f.getAnnotations().getStudyId().getPatientId().getBirthDate(), false);
                String patientSex = f.getAnnotations().getStudyId().getPatientId().getSex() ? "M" : "F";
                String toothName = f.getAnnotations().getTeethNumberId().getId();

                if (imageExportFormat.contains("dicom")) {
                    String imageName = f.getFile().getName().replace(".png", "");
                    File jpg = ImageFormatConverter.convertAndGet(
                            f.getFile(), exportPath, "jpg",
                            patientName + "\n"
                            + formatedDate
                            + "\n"
                            + patientAge
                            + " - "
                            + patientSex
                            + "\n"
                            + toothName,
                            f.getFile().getName().replace(".png", "")
                    );

                    File dicom = new File(exportPath + imageName + ".dcm");
                    Jpg2Dcm.convert(jpg, dicom, setDicomTags(f));
                } else {
                    isOk = ImageFormatConverter.exportImage(
                            f.getFile(), exportPath, imageExportFormat,
                            patientName + "\n"
                            + formatedDate
                            + "\n"
                            + patientAge
                            + " - "
                            + patientSex
                            + "\n"
                            + toothName,
                            patientName + "_" + f.getImageDate().toString().replace(" ", "") + "_" + toothName, 90);

                }
            } catch (NullPointerException e) {
            } catch (IOException ex) {
                Logger.getLogger(SensorImageController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (isOk) {
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ExportSuccess"));
        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("ExportError"));
        }
    }

    /**
     * Save image on the defined destination path
     *
     * @param input
     * @param filename
     * @param fileUniquePath
     * @param fileFormat
     * @return
     */
    public static String saveImageToXrayFolder(InputStream input, String filename, String fileUniquePath, String fileFormat) {
        try {
            Path fullPath = DirectoryManager.createDirectory(new StringBuilder(xrayDestinationPath).append(fileUniquePath).toString());
            Path folder = Paths.get(fullPath.toString(), filename + "." + fileFormat);
//        Path filePath = Files.createFile(folder);
//        Path filePath = Files.createTempFile(folder, filename, ".png");
//        Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(input, folder, StandardCopyOption.REPLACE_EXISTING);

            String imageFileName = folder.getFileName().toString();
//file.delete();
//        filePath.toFile().delete();
            input.close();
            return imageFileName;
        } catch (IOException ex) {
            Logger.getLogger(SensorImageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Move an image from source to destination path
     *
     * @param sourceFilePath
     * @param filename
     * @return
     * @throws IOException
     * @throws java.nio.file.FileSystemException : throw if the file is being
     * used by another process and the file move function could not be completed
     */
    public static String saveImageToXrayFolder(Path sourceFilePath, String filename) throws IOException, java.nio.file.FileSystemException {
        Path destinationFilePath = Paths.get(xrayDestinationPath, filename + ".png");
//        Path filePath = Files.createFile(folder);
//        Path filePath = Files.createTempFile(folder, filename, ".png");
//        Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        Files.move(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);

        String imageFileName = destinationFilePath.getFileName().toString();
        //file.delete();
//        filePath.toFile().delete();
        return imageFileName;
    }

    public void dicomSend() {
        File dicom = null;
        DicomTags tags;
        File baseDirectory;
        Device device;
        Connection conn;
        StoreTool dct;
        for (ExportEmailModel f : imagesList()) {
            try {
                String patientName = f.getAnnotations().getStudyId().getPatientId().getName();
                String formatedDate = JsfUtil.formatDateWithPattern(JsfUtil.zoneDateTimeToDate(f.getImageDate()), "dd/MM/yyyy");
                String patientAge = JsfUtil.getAge(f.getAnnotations().getStudyId().getPatientId().getBirthDate(), false);
                String patientSex = f.getAnnotations().getStudyId().getPatientId().getSex() ? "M" : "F";
                String toothName = f.getAnnotations().getTeethNumberId().getId();

                String imageName = f.getFile().getName().replace(".png", "");
                File jpg = ImageFormatConverter.convertAndGet(
                        f.getFile(), dicomSendPath, "jpg",
                        patientName + "\n"
                        + formatedDate
                        + "\n"
                        + patientAge
                        + " - "
                        + patientSex
                        + "\n"
                        + toothName,
                        f.getFile().getName().replace(".png", "")
                );
                dicom = new File(dicomSendPath + imageName + ".dcm");
                tags = setDicomTags(f);
                dicom = Jpg2Dcm.convert(jpg, dicom, tags);

                String testDescription = description;//"Description of test by IMI dicomDestinationFile senddd";
                String host = server = "www.dicomserver.co.uk";
                int port = 104;
                String aeTitle = "IMIDICOMSTORAGE";
                baseDirectory = new File(dicomSendPath);
                device = new Device("storescu");
                String sourceAETitle = "STORESCU";
                conn = new Connection();
                device.addConnection(conn);

                dct = new StoreTool(host, port, aeTitle, baseDirectory, device, sourceAETitle, conn);
                String[] fileNmes = {dicom.getName()};
                dct.store(testDescription, fileNmes);

            } catch (NullPointerException | IOException | InterruptedException | IncompatibleConnectionException | GeneralSecurityException e) {
                Logger.getLogger(SensorImageController.class
                        .getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public DicomTags setDicomTags(ExportEmailModel annotations) {
        DicomTags tags = new DicomTags();
        tags.setPatientID(annotations.getAnnotations().getStudyId().getPatientId().getId().toString());
        tags.setPatientName(annotations.getAnnotations().getStudyId().getPatientId().getName());
        tags.setPatientSex(annotations.getAnnotations().getStudyId().getPatientId().getSex() ? "M" : "F");
        tags.setPatientBirthDate(annotations.getAnnotations().getStudyId().getPatientId().getBirthDate());
        tags.setStudyDate(annotations.getAnnotations().getStudyId().getCreationDate());
        tags.setSeriesDate(annotations.getAnnotations().getStudyId().getCreationDate());
        tags.setStudyDescription(annotations.getAnnotations().getStudyId().getDescription());
        tags.setSeriesDescription("");
        tags.setModality("MR");
        tags.setPhotometricInterpretation("MONOCHROME2");
//        tags.setPhotometricInterpretation("YBR_FULL_422");
        tags.setSamplesPerPixel(3);
        tags.setNumberOfFrames(1);
        return tags;
    }

    /// change this concurrent operation
    public List<String> getImages() {
        getCurrentStudyImages();
        System.out.println("getting new images... size from sensorImageController: " + images.size());
        return images;
    }

    public int getImagesSize() {
        return images != null ? images.size() : 0;
    }

    public void getCurrentStudyImages() {
        images = new ArrayList();
        getImagesFromAnnotations().forEach((currentImages) -> {
            String imagePath = currentImages.getPattern();
            if (currentImages.getImagePath() != null) {
                imagePath = new StringBuilder(currentImages.getImagePath()).append("/").append(currentImages.getPattern()).toString();
            }
            images.add(imagePath);
        });
    }

    private List<Images> getImagesFromAnnotations() {
        if (ac == null) {
            ac = (AnnotationsDentalXrayController) JsfUtil.getViewScopedBean("annotationsDentalXrayController");
            if (ac != null) {
                return ac.checkImageListChanges();
            }
        } else {
            return ac.checkImageListChanges();
        }
        return new ArrayList();
    }

    /**
     * gets today's study of a patient on patient creation.
     *
     * @return
     */
    public Studies getCurrentStudyByCurrentPatient() {
        PetPatientsController petC = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        if (petC != null) {
            return petC.getCurrentStudy();
        }
        return null;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public static boolean deleteImageFromSourcePath(String imageSourcePath, String fileName) throws IOException {
        return Files.deleteIfExists(getImageFromSourcePath(imageSourcePath, fileName).toPath()); //surround it in try catch block
    }

    public static File getImageFromSourcePath(String imageSourcePath, String fileName) throws IOException {
        file = new File(imageSourcePath, fileName);
        return file;
    }

    public static Path getSourceImagePath(String imageSourcePath, String fileName) {
        return Paths.get(imageSourcePath + fileName);
    }

    public static InputStream getInputImage(File file) throws IOException {
        inputImage = new FileInputStream(file);
        return inputImage;
    }

    public static void setInputImage(InputStream aInputImage) {
        inputImage = aInputImage;
    }

    public static String getDentalSensorSourceFilePath() {
        System.out.println("SensorImageController.getDentalSensorSourceFilePath() sourceFilePath: " + dentalSensorSourceFilePath);
        return dentalSensorSourceFilePath;
    }

    public static String getFlatPAnelSourceFilePath() {
        System.out.println("SensorImageController.getFlatPanelFilePath() sourceFilePath: " + flatPanelSourceFilePath);
        return flatPanelSourceFilePath;
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

}
