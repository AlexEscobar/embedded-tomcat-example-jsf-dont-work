package com.innotech.xraymanagerapp.business;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanel;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelCommunicationMessagesModel;
import com.innotech.xraymanagerapp.controller.PetPatientsController;
import com.innotech.xraymanagerapp.controller.StudiesController;
import com.innotech.xraymanagerapp.controller.WorkListController;
import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.ViewerController;
import com.innotech.xraymanagerapp.controller.dicom.FileSenderClient;
import com.innotech.xraymanagerapp.controller.socket.SocketClient;
import com.innotech.xraymanagerapp.controller.util.SensorImageController;
import com.innotech.xraymanagerapp.controller.util.constants.Sensors;
import com.innotech.xraymanagerapp.controller.util.constants.Urls;
import com.innotech.xraymanagerapp.model.AbstractSelectedAnnotatePart;
import com.innotech.xraymanagerapp.model.AcquistionDevices;
import com.innotech.xraymanagerapp.model.AnimalSizes;
import com.innotech.xraymanagerapp.model.ExportEmailModel;
import com.innotech.xraymanagerapp.model.ImageRejection;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.RejectionReasons;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.DicomUtils;
import com.innotech.xraymanagerapp.model.dicom.LoaderObject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.ws.rs.core.Response;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Hi
 * @param <F> Annotation Facade Class: Either TeethNumberFacade or
 * BodyPartsFacade
 * @param <E> Annotation Entity Class: Either TeethNumber or BodyParts
 */
public abstract class AbstractAnnotationsController<F, E> implements PropertyChangeListener, Serializable, Observer {

    public static boolean isIsAutomatic() {
        return isAutomatic;
    }

    public static void setIsAutomatic(boolean aIsAutomatic) {
        isAutomatic = aIsAutomatic;
    }

    protected ViewerBusinessController vc;

    protected List<Annotations> items = null;

    protected List<Annotations> itemsToShow = null;

    private List<Annotations> tempBodyPartViews = null;

    // to avoid Gson java.lang.StackOverflowError, this list is needed as a copy of selectedTeethItems
    // Only keeps IDs of TeethNumbers to check and automatic highlight the annotated tooth for the current study 
    protected List<AbstractSelectedAnnotatePart> selectedTeethItemsForJavascript = null;

    protected static final int TOTAL_PROGRESS = 10;
    protected static final int SENSOR_TIMEOUT = 270;
    protected Annotations selected = null;
    protected Studies study;
    protected boolean isSelectAll;
    protected boolean isOnXray;
    protected boolean isReceivingRadiation;
    protected boolean isInImageProcess;// true only between the time from receiving radiation and image acquisiotion.
    protected boolean isNewImage;//true when a new image arrives, false on process start and once the image is shown on the view
    protected boolean isButtonStartPushed;

    private static boolean isAutomatic;
    private int interval = 9999999;
    protected int activeIndex = 0;
    private String jasonTest;
    private boolean isOpenViewer;
    protected Integer progressBar;
    public boolean isHDRSensor;// if true means current sensor is HDR otherwise, the current one is EZ
    public boolean isWatchService;
    private boolean isOutputFolderReady;
    private String todaysDate;
    private int currentValue = -1;
    private String currentXrayImage;
    protected Images currentImage;
    public String columnPixelSpacing;
    public String rowPixelSpacing;

    public final static String EZ_SENSOR_NAME = Sensors.EZ_SENSOR_NAME;
    public final static String HDR_SENSOR_NAME = Sensors.HDR600_SENSOR_NAME;
    protected final boolean isHdrSensorDll = true;
    protected Integer currentImageId;// the image id of the x-ray that was just taken
    protected Integer rejectedImageId;
    protected boolean IS_GENERATOR_CONNECTED;// = ConfigurationBusinessController.IS_GENERATOR_CONNECTED;// whether the system is working with a high frecuency generator or not
    protected int socketClientPort;
    private RejectionReasons[] selectedRejectionReasons;
    private List<RejectionReasons> rejectionReasons;
    private boolean isPanelReady;
    protected boolean isSocketServerReady;
    protected final String SOURCE_IMAGE_NAME = "image.bmp";
    private String imageSourcePath;
    protected static SocketClient client;
    private boolean isInImageAcquisition = false;
    protected Annotations temporarySelected = null;
    protected Integer animalSize;

    // Abstract functions
    public abstract com.innotech.xraymanagerapp.dto.AnnotationsFacade getAnnotationsFacade();

    public abstract com.innotech.xraymanagerapp.dto.ImagesFacade getImagesFacade();

    public abstract com.innotech.xraymanagerapp.dto.StudiesFacade getStudiesFacade();

    public abstract com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade getViewDicomTagsFacade();

    public abstract com.innotech.xraymanagerapp.dto.ImagesFacade getEjbImagesFacade();

    public abstract com.innotech.xraymanagerapp.dto.RejectionReasonsFacade getEjbRejectionsFacade();

    public abstract com.innotech.xraymanagerapp.dto.AcquistionDevicesFacade getEjbAcquisitionDevicesFacade();

    @EJB
    private com.innotech.xraymanagerapp.business.ConfigurationBusinessController ejbConfigurationBusinessController;

    @EJB
    private FileSenderClient ejbFileSenderClient;

    public abstract void initializeProperties();

    public abstract void startNextCurrent();

    public abstract F getEjbTeethNumberFacade();

    public abstract List<Annotations> getItemsToShow();

    public abstract void setSelectedTeethItemsFromDb();

    public abstract String getAnnotationsAsJson();

    public abstract E getAnnotatePartById(String id);

    public abstract void startWaitingForRadiation();

    public abstract void getImageAcquisitionDeviceCurrentProcessStage();

    public abstract void setViewerBusinessController(ViewerBusinessController vc);

    public abstract ViewerController getViewerControllerBean();

    public abstract void initImageSourcePath();

    public abstract void createDirect(E tn);

    public abstract void createOrUpdateOnImageMapSelection(String id);

    /**
     * Returns all the teeth on the TeethNumber table
     *
     * @return
     */
    public abstract List<E> getAnnotatePartItems();

    public abstract int closePanelDevice();

    public abstract void stopCommunication();

    public AbstractAnnotationsController() {
    }

    @PostConstruct
    public void init() {
        System.out.println("Abstract Annotations controller on init() @PostConstruct");
        initializeProperties();
        isHDRSensor = JsfUtil.getSensorConfiguration();
        todaysDate = JsfUtil.getStringDate(new Date(), "yyyyMMdd");
//        progressBar = null;
    }

    /**
     * As @PreDestroy does not work with ViewScoped bean, this function is
     * called from the client side by the javaScript window.onunload function
     * that is declared in the annotations.Create.XHTML file.
     *
     * @return worklist path
     */
//    @PreDestroy
    public String preDestroy() {
        closeSensorCommunication();
        return new StringBuilder("pretty:").append(Urls.WORKLIST_PAGE).toString();
    }

    public boolean clearOutputFolder() {
        if (JsfUtil.isXrayLocalServer()) {
            try {

                if (isHDRSensor) {
                    isOutputFolderReady = true;
                    //isOutputFolderReady = JsfUtil.cleanDirectory(JsfUtil.getConfigurationMap().get("HDRSensorPath"), "bmp");

                } else {
                    isOutputFolderReady = JsfUtil.cleanDirectory(JsfUtil.getConfigurationMap().get("EZSourceFilePath"), "bmp");
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return isOutputFolderReady;
    }

    public String redirectToWorklist() {
        preDestroy();
        return new StringBuilder("pretty:").append(Urls.WORKLIST_PAGE).toString();
    }

    public String redirectToStudyList() {
        preDestroy();
        return new StringBuilder("pretty:").append(Urls.STUDYLIST_PAGE).toString();
    }


    public SocketClient getSocketClient() {
        client.setMessageListener(this);
        return client;
    }
    
    public void closeSensorCommunication() {
        sensorKiller();
        stopCommunication();
        getSocketClient().closeConnection();
    }
    
    /**
     * Notifies this class Whenever the imageAcquisitionStatus variable on the
     * EZSensorController class changes because of an event on the sensor output
     * folder. The event could be: A new image has been created, modified or
     * deleted.
     *
     * @param o Observable object. The class that is being observed, in this
     * case EZSensorController that is watching the sensor output folder through
     * a Java Watch service
     * @param arg The imageAcquisitionStatus value
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!isIsInImageAcquisition()) {
            try {
                int status = (int) arg;
                switch (status) {
                    case 50:
                        setIsWatchService(true);
                        break;
                    case 30:
                        if (!isIsHDRSensor()) {
                            setIsInImageAcquisition(true);
                            setProgressBar(12);
                            Thread.sleep(2500);
                            setProgressBar(17);
                            addImageToCurrentAnnotation(SOURCE_IMAGE_NAME);
                            setIsInImageAcquisition(false);
                        }
                        break;
                    default:
                        if (status == 10) {
                            calculateProgressBarPercentage(status);
                            try {
                                Thread.sleep(100L);
                                setProgressBar(100);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, ex.getMessage());
                            }
                        }
                        break;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            } catch (IOException ex) {
                Logger.getLogger(AnnotationsDentalXrayController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public abstract void updateAllUIValues();
    
    public abstract void sendMessage(FlatPanelCommunicationMessagesModel fpcmm);
    
    public void notifyUI(String message) {
//        System.out.println("Message received from panel: " + message);
        updateAllUIValues();
    }
    
    /**
     * Gets true or false Notification from SensorLauncher() class whether the
     * sensor .exe program has started correctly or not. This boolean value is
     * stored in the local variable isOnXray
     *
     * @param evt: The event with the boolean value that comes from the
     * SensorLauncher() class
     */
 
   @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
//            FlatPanel generatorResponsCe = (FlatPanel) evt.getNewValue();
            notifyUI("#######################################################$$$$##$#$#$$#$# \n "
                    + "Panel says: \n currentProcessState: " + FlatPanel.currentProcessState);
            FlatPanelCommunicationMessagesModel fpcmm = new FlatPanelCommunicationMessagesModel();
            fpcmm.setCalibrationFiles(FlatPanel.calibrationFiles);
            fpcmm.setDetectorConnected(FlatPanel.detectorConnected);
            fpcmm.setContinueRad(FlatPanel.continueRad);
            fpcmm.setIsPanelWaiting(FlatPanel.isPanelWaiting);
            fpcmm.setBattery(FlatPanel.battery);
            fpcmm.setConnection(FlatPanel.connection);
            fpcmm.setCurrentProcessState(FlatPanel.currentProcessState);
            fpcmm.setTempStatus(FlatPanel.tempStatus);
            fpcmm.setTemperature(FlatPanel.temperature);
            fpcmm.setCurrentCode(FlatPanel.currentCode);
            fpcmm.setError(FlatPanel.error);
            fpcmm.setInfo(FlatPanel.info);
            sendMessage(fpcmm);
        } catch (NumberFormatException ex) {
            Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized void calculateProgressBarPercentage(int currentValue) {
        System.out.println("currentValue calculateProgressBarPercentage(): " + currentValue);

        if (currentValue <= 10) {
            if (currentValue == 1) {
//                notifySensorWaitingForRadiation();
            } else {
                progressBar = 100 * currentValue / TOTAL_PROGRESS;
            }

        } else {
            progressBar = 0;
            isOnXray = false;
        }
    }

    /**
     * This function is called from the view side through a javaScript function
     * on progress bar completion
     */
    public void onComplete() {
        System.out.println("Progress bar on complete OK...");
        try {
            getViewerControllerBean().initializeLists();
        } catch (NullPointerException ex) {
            Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, ex.getMessage(), "Bean viewerController is null in current context");
        }
        setProgressBar(0);

        setInterval(SENSOR_TIMEOUT);
        updateCurrentView();
        updateThumbnail();
//        clearOutputFolder();
        initializeCurrentImage();

        setIsInImageProcess(false);
    }

    public void initializeCurrentImage() {
        currentImage = null;
        currentImageId = 0;
        currentXrayImage = "";
    }

    public void initializeGeneratorProperties() {
        if (IS_GENERATOR_CONNECTED) {
//            getGeneratorController().initializeVariables();
        }
    }

    /**
     *
     * Updates the current annotations view on new image detected
     *
     */
    public void updateCurrentView() {
        try {
            System.out.println("Updating view...");
            PrimeFaces instance = PrimeFaces.current();
            List<String> idsToUpdate = new ArrayList(10);
            idsToUpdate.add("AnnotationsCreateForm:xrayCurrentContainer");// updates the current image container
            idsToUpdate.add("AnnotationsCreateForm:pollGrid");// updates the current image container
            idsToUpdate.add("XrayActionForm:xrayActionButtons");
            if (FlatPanel.currentProcessState >= 500) {
                JsfUtil.addErrorMessage("Panel device was disconnected...");
                idsToUpdate.add(":growl");
            }
            instance.ajax().update(idsToUpdate);
        } catch (Exception ex) {
            Logger.getLogger(AbstractAnnotationsController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void updateThumbnail() {
        PrimeFaces.current().executeScript("addImageToThumbnail(" + getCurrentXrayImage(currentImage) + ");");
    }

    public void updateThumbnail(Images image) {
        PrimeFaces.current().executeScript("addImageToThumbnail(" + getCurrentXrayImage(image) + ");");
    }

    public void updateXrayButtons() {
        PrimeFaces instance = PrimeFaces.current();
        List<String> idsToUpdate = new ArrayList(2);
        idsToUpdate.add("XrayActionForm:xrayActionButtons");
        System.out.println("IN updateXrayButtons() line 457: isOnXray - " + isOnXray + "FlatPanel.currentProcessState : " + FlatPanel.currentProcessState);
        if (FlatPanel.currentProcessState >= 500 && isButtonStartPushed) {
            JsfUtil.addErrorMessage("Panel device was disconnected...");
            idsToUpdate.add(":growl");
            isButtonStartPushed = false;
        }
        instance.ajax().update(idsToUpdate);
    }

    public void updateGrowl() {
        PrimeFaces instance = PrimeFaces.current();
        instance.ajax().update(":growl");
    }

    public void notifySensorWaitingForRadiation() {
        //JsfUtil.addSuccessMessage("Sensor Ready waiting for radiation...");
        PrimeFaces instance = PrimeFaces.current();
//            idsToUpdate.add("xrayBody");// updates the whole body container. Reloads the whole xray page
        instance.ajax().update(":growl");
    }

    /**
     * Relaunches the EZ sensor .exe program when it auto stops every 5 minutes
     * if does not receive radiation.
     */
    public synchronized void relaunchAfterInterval() {
        sensorKiller();
        System.out.println("Relaunching Sensor process after interval... " + interval);
        startWaitingForRadiation();
        //JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessRestarted"));
    }

    /**
     * Stops the sensor process
     */
    public void sensorKiller() {
        setProgressBar(0);
        setSelected(null);
        setInterval(9999999);
        isOnXray = false;
        closePanelDevice();
        updateXrayButtons();

        getImagesFacade().resetCache();
        //initializeGeneratorProperties();
        //JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessFinished"));
    }

    public void stopSensorAndOpenViewer() {
        sensorKiller();
        try {
            StudiesController petC = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
            petC.openOHIFViewer();
        } catch (Exception ex) {
            Logger.getLogger(AbstractAnnotationsController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * verifies the annotation list to start acquiring new images
     *
     * @return true if annotationsList is not null and not empty
     */
    public boolean verifyAnnotationList() {
        return itemsToShow != null && !itemsToShow.isEmpty();
    }

    /**
     * Sets the isCurrent = true for the current Annotation object
     *
     * @return
     */
    public boolean setCurrent() {
        return setCurrent(0);
    }
     
    public boolean setCurrent(int currentIndex) {
        boolean returnValue = true;
        if (selected == null) {
            if (getItemsToShow() != null && itemsToShow.size() > 0) {
                selected = itemsToShow.get(currentIndex);
                if (selected == null) {
                    returnValue = false;
                }
            } else {
                returnValue = false;
            }
        } 
        return returnValue;
    }
    
    /**
     * Convert the current x-ray image in a bytes map
     *
     * @param image
     * @return The converted image in a base 64 String
     */
    public String getCurrentXrayImage(Images image) {
        ExportEmailModel eem = new ExportEmailModel();//56456-- use the current image along with the base64 file to update the thumbnail
        eem.setImageId(image.getId());
        eem.setToothName(vc.getSeriesDescription(image));
        eem.setImageAsBase64(vc.getCurrentXrayImage());
        currentXrayImage = new Gson().toJson(eem);
        return currentXrayImage;
    }

    /**
     * Checks if the current image list has the same size as n seconds before,
     * is so, calls the next annotation and updateSelected it as the current
     * one, so that the next x-ray image will be attached to that current
     * annotation.
     *
     * @return List of images that belongs to the current study
     */
    public List<Images> checkImageListChanges() {
        try {
//            the whole images for a study
            setStudy();

            List<Images> imageList = new ArrayList(0);
            if (study != null) {
//                imageList = ejbImagesFacade.getImagesByStudy(study.getId());
//                imageList = ejbImagesFacade.findByStudyId("Images.findByStudyIdAnnotation", "studyId", study.getId());// retrieves only active images that belongs to active annotations
                imageList = getImagesFacade().findByStudyId("Images.findByStudyId", "studyId", study.getId());// retrieves only active images without taking care of active annotations
            }
            if (isAutomatic) {
                selected = null;
                setCurrent();
            }

            return imageList;
        } catch (NullPointerException e) {
            Logger.getLogger(AbstractAnnotationsController.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
        return new ArrayList();
    }

    /**
     * Writes into a file that the Watch service is listening to check if the
     * x-ray output folder is being truly watched. Once a line is wrote intro
     * the file the watch service immediately sends a notification to this
     * observer through the override update() function implemented in this
     * class. The previous action sets the flag isOnXray to true in order to
     * notify the user that the system is at least watching the output folder.
     * This is the first check before allow the user to shoot an x-ray
     *
     * @param text: Text to write into the file
     */
    public void writeIntoFile(String text) {
        //        String str = "0";
        try {
            StringBuilder fullCheckerPath = new StringBuilder("");
            if (JsfUtil.getConfigurationMap() != null) {
                if (isHDRSensor) {
                    fullCheckerPath = new StringBuilder(JsfUtil.getConfigurationMap().get("HDRSensorPath")).append("watchChecker.txt");
                } else {
                    fullCheckerPath = new StringBuilder(JsfUtil.getConfigurationMap().get("EZSourceFilePath")).append("watchChecker.txt");
                }
            }
            Files.write(Paths.get(fullCheckerPath.toString()), text.getBytes());
            System.out.println("fullCheckerPath: " + fullCheckerPath);
        } catch (IOException ex) {
            Logger.getLogger(AbstractAnnotationsController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), ex.getLocalizedMessage());
        }
    }

    public String deselectAll() {
        if (getItems() != null) {
            for (Annotations item : items) {
                item.setStatus(false);
                selected = item;
                updateSelected();
            }
            selectedTeethItemsForJavascript = new ArrayList();
        }
        return new StringBuilder("pretty:").append(Urls.XRAY_PAGE).toString();
    }

    public Studies getCurrentStudy() {
        StudiesController studyC = (StudiesController) JsfUtil.getSessionBean("studiesController");
        if (studyC != null) {
            return studyC.getSelected();
        }
        return null;
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

    public PetPatientsController getSelectedPetPatient() {
        PetPatientsController petC = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        return petC;
    }

    public Annotations getSelected() {
        return selected;
    }

    public void setSelected(Annotations selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public Annotations prepareCreate() {
        selected = new Annotations();
        initializeEmbeddableKey();
        return selected;
    }

    public Annotations initializeAnnotation(Annotations annotation) {
        annotation.setStatus(true);
        annotation.setIsCurrent(false);
        annotation.setIsDone(false);
        annotation.setCreationDate(new Date());
        annotation.setUserId(AuthenticationUtils.getLoggedUser());
        annotation.setStudyId(study);
        return annotation;
    }
    
    public void initializeSelected() {
        prepareCreate();
        initializeAnnotation(selected);
    }

   public String create() {
        // if there is an open study, creates a new annotation object
        int id = temporarySelected.getId();
        selected = temporarySelected;
        if (Objects.nonNull(animalSize)) {
            getSelected().setAnimalSizeId(new AnimalSizes(animalSize));
        }
        getSelected().setId(null);
        persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AnnotationsCreated"));
        if (!JsfUtil.isValidationFailed()) {///
            setSelected(null);
            itemsToShow = null;
            items = null;    // Invalidate list of items to trigger re-query.
//            initializeProperties();
        }
        temporarySelected.setId(id);
        performSecurityChecks();
        return "";
    }

    public void performSecurityChecks() {
        setCurrent();
    }

    public void updateSelected() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AnnotationsUpdated"));
    }

    public void destroy() {
        String action;
//        boolean invalidateSelected = true;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("AnnotationsDeleted");
        } else {
            selected.setStatus(true);
//            invalidateSelected = false;
            action = ResourceBundle.getBundle("/Bundle").getString("AnnotationsUpdated");
        }

        persist(PersistAction.DELETE, action);
        setNullProperties();
    }

    //
    public void delete() {
        if (selected != null) {
            getAnnotationsFacade().remove(selected);

            selected = null; // Remove selection

            itemsToShow = null;
            items = null;
            tempBodyPartViews = null;
        }
    }

    public void setNullProperties() {

        if (!JsfUtil.isValidationFailed()) {
//            if(invalidateSelected)
            selected = null; // Remove selection
            itemsToShow = null;
//            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Annotations> getItems() {
        if (items == null || items.isEmpty()) {
            setStudy();
            if (study != null) {
                vc.setCurrentStudy(study);
                items = getAnnotationsFacade().findByStudyId(study.getId());
            }
        }
        return items;
    }

    /**
     * Gets the study that is selected on the worklist or on the StudyList to
     * show all the study properties on this x-ray view
     */
    public void setStudy() {
//        study = getStudyByCurrentSelectedWorklist();
        if (study == null) {
            study = getCurrentStudyByCurrentPatient();
        }
        if (study == null) {
            study = getStudyByStudyList();
        }
        PetPatientsController petC = getSelectedPetPatient();
        if (petC != null) {
//            if (petC.getSelected() == null) {
            if (study != null) {
                if (study.getPatientId() != null) {
                    petC.setSelected(study.getPatientId());
                }
            }
//            }
        }
//        }
    }

    /**
     * Returns the selected Study on the Worklist (by the worklist object that
     * is parent of the study)
     *
     * @return Studies object
     */
    public Studies getStudyByCurrentSelectedWorklist() {
        WorkListController wc = (WorkListController) JsfUtil.getSessionBean("workListController");
        if (wc != null) {
            if (wc.getSelected() != null) {
                if (wc.getSelected().getStudyId() != null) {
                    PetPatientsController petC = (PetPatientsController) JsfUtil.getSessionBean("petPatiensetStudytsController");
                    if (petC != null) {
                        if (Objects.equals(petC.getSelected().getId(), wc.getSelected().getPatientId().getId())) {
                            return getStudiesFacade().find(wc.getSelected().getStudyId());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the selected study on the studyList
     *
     * @return current selected study
     */
    public Studies getStudyByStudyList() {
        StudiesController sc = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
        if (sc != null) {
            return sc.getSelected();
        }
        return null;
    }

    public void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    selected = getAnnotationsFacade().edit(selected);
                } else {
                    getAnnotationsFacade().edit(selected);
                }
                //JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    /**
     * Persist a list of entities
     *
     * @param annotationList Entity list
     * @return true if the persistence was correct for the whole entities in the
     * list, false otherwise.
     */
    public boolean persistMultiEntities(List<Annotations> annotationList) {
        try {
            // perform persist action
            return getAnnotationsFacade().persistMulti(annotationList);
        } catch (EJBException ex) {
            // if errors show them and log them
            String msg = "";
            Throwable cause = ex.getCause();
            if (cause != null) {
                msg = cause.getLocalizedMessage();
            }
            if (msg.length() > 0) {
                JsfUtil.addErrorMessage(msg);
            } else {
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
        return false;
    }

    public Images persistNewImage(Images image) {
        if (image != null) {
            try {
                image = getImagesFacade().edit(image);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
        return image;
    }

    public Images buildAndPersistImage(String imageName, String imagePath) {
        Date d = new Date();
        Images newXrayImage = new Images(null, imageName, d, true, getSelected(), getSelected().getUserId(), getSelected().getStudyId());
        newXrayImage.setSeriesInstanceUID(DicomUtils.getSeriesInstanceUID() + JsfUtil.formatDateWithPattern(d, "yyyyMMddHHmmss"));
        newXrayImage.setsOPInstanceUID(DicomUtils.getSopInstanceUID() + JsfUtil.formatDateWithPattern(d, "yyyyMMddHHmmss"));
        newXrayImage.setImagePath(imagePath);
        newXrayImage.setColumnPixelSpacing(getCurrentPanel().getColumnCellSpacing());
        newXrayImage.setRowPixelSpacing(getCurrentPanel().getRowCellSpacing());
        // Persist the image path on the database and get the id
        newXrayImage = persistNewImage(newXrayImage);
        return newXrayImage;
    }

    public Images updateRowsAndColumns(ViewDicomTags dicomTags, Images image) {
        image.setImageRows(dicomTags.getImageRows());
        image.setImageColumns(dicomTags.getImageColumns());
        return persistNewImage(image);
    }

    public LoaderObject createImageLoaderObject(Images image) {
        LoaderObject loader = new LoaderObject();
        loader.setImage(image);
//        loader.setStudy(study);
//        loader.setAnnotationsList(itemsToShow);
        return loader;
    }

    public File getFileFromOutputPath(String sourceFileName) {
        try {
            return SensorImageController.getImageFromSourcePath(getImageSourcePath(), sourceFileName);// get the raw file
        } catch (IOException ex) {
            Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buildImagePath() {
        String studyDate = JsfUtil.formatDateWithPattern(getSelected().getStudyId().getCreationDate(), "yyyyMMdd-HHmmss");
        System.out.println("1- StudyDate: " + studyDate);
        return new StringBuilder("/")
                .append(getSelected().getStudyId().getPatientId().getName())
                .append("_")
                .append(getSelected().getStudyId().getPatientId().getPatientId())
                .append("/").append(studyDate).toString();
    }

    public String buildImageFullName() {
        //copy the image into the x-ray images folder
        return getSelected().getStudyId().getPatientId().getId() + "_" + getSelected().getStudyId().getPatientId().getName().replace(" ", "_") + "_"
                + getSelected().getBodyPartsId().getDescription() + "BP_" + getSelected().getBodyPartViewId().getAbbreviation() + "BPV_" + getSelected().getId() + "AID_";
    }

    public String buildImageFullPath(String destinationFileName, String imagePath, long seconds) {
        String destinationFormat = "jpg";
        destinationFileName = destinationFileName + seconds;
        String imageName = new StringBuilder(destinationFileName).append(".").append(destinationFormat).toString();
        return imageName;
    }

    /**
     * Inserts the image (sourceFileName) to the annotation list of the
     * currentAnnotation
     *
     * @param sourceFileName
     * @return image id
     */
    public Images addImageToCurrentAnnotation(String sourceFileName) throws IOException {

        try {
            setIsInImageProcess(true);
            String imagePath = buildImagePath();
            System.out.println("2- imagePath: " + imagePath);

            long seconds = System.currentTimeMillis();
            String imageFullName = buildImageFullName();

            System.out.println("3- imageFullName: " + imageFullName);
            Thread.sleep(100);
            String imageName = buildImageFullPath(imageFullName, imagePath, seconds);
            File file = getFileFromOutputPath(sourceFileName);
            String imageBase64 = JsfUtil.convertImageToBase64(file);

            System.out.println("4- imageName: " + imageName);

            // add the copied image to the annotation image list
            setProgressBar(80);

            // the local server creates the new image file and retrieves it.
            Images newXrayImage = buildAndPersistImage(imageName, imagePath);
            Images imageToSend = new Images(newXrayImage.getId());
            imageToSend.setImageFile(imageBase64);
            imageToSend.setPattern(newXrayImage.getPattern());
            imageToSend.setImagePath(newXrayImage.getImagePath());
            
            LoaderObject loader = createImageLoaderObject(imageToSend);
            ejbFileSenderClient.init();
            Response response = ejbFileSenderClient.sendFile(loader);

            File convertedFile = response.readEntity(File.class);
            vc.setCurrentXrayImage(JsfUtil.convertImageToBase64(convertedFile));

            // local call
            setCurrentImage(newXrayImage);
            setProgressBar(100);
            System.out.println("Final imageName full path: " + imageName);
        } catch (NullPointerException | InterruptedException ex) {
            setProgressBar(0);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "{0} - AnnotationsController.addImageToCurrentAnnotation()", ex.getMessage());
        }
        return getCurrentImage();
    }

    public synchronized ViewDicomTags getDicomTags(Integer imageId, ViewerBusinessController vc) {
        ViewDicomTags dicomTags = vc.getDicomTagsByImageId(imageId, getViewDicomTagsFacade());
        return dicomTags;
    }
   
    public void setCurrentXrayImageOnNewImageCreation(ViewDicomTags dicomTags, String xrayDestinationPath) {
        try {
            Path folder = Paths.get(new StringBuilder(xrayDestinationPath).append("/").append(dicomTags.getImagePath()).append("/").append(dicomTags.getImagePattern().replace(".png", ".jpg")).toString());
            byte[] currentXrayImageByte = Files.readAllBytes(folder);
            currentXrayImage = Base64.getEncoder().encodeToString(currentXrayImageByte);
        } catch (IOException ex) {
            Logger.getLogger(AbstractAnnotationsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Convert the image array in Json format to be used in the view JavaScript
     * that creates the gallery.
     *
     * @return String representation of the Json document.
     */
//    @Asynchronous
    public String getImagesAsJson() {
        List<ViewDicomTags> viewDicomTagsList = getViewDicomTagsFacade().getListByStudyId(getCurrentStudyByCurrentPatient().getId());
        if (viewDicomTagsList == null || viewDicomTagsList.isEmpty()) {
            return "";
        }
        String jsonString = new Gson().toJson(viewDicomTagsList);
        return jsonString;
    }

    public void initRejectionReasonList() {
        if (rejectionReasons == null) {
            rejectionReasons = getEjbRejectionsFacade().findAll();
        }
    }

    public RejectionReasons[] getSelectedRejectionReasons() {
        return selectedRejectionReasons;
    }

    public void setSelectedRejectionReasons(RejectionReasons[] selectedRejectionReasons) {
        this.selectedRejectionReasons = selectedRejectionReasons;
    }

    public List<RejectionReasons> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<RejectionReasons> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public RejectionReasons getRejectionReason(Integer key) {
        return getEjbRejectionsFacade().find(key);
    }

    public void rejectCurrentImage() {
        System.out.println("Current image id. The ONE to be rejected: " + currentImageId);
        if (Objects.nonNull(getCurrentImage())) {
            setCurrentImageId(getCurrentImage().getId());
        }
        if (currentImageId != null && currentImageId > 0) {
            try {

                Images imageToReject = getEjbImagesFacade().find(currentImageId);
                if (imageToReject != null) {
                    if (selectedRejectionReasons != null && selectedRejectionReasons.length > 0) {
                        List<ImageRejection> imageRejectionList = new ArrayList();
                        Date date = new Date();
                        for (RejectionReasons selectedRejectionReason : selectedRejectionReasons) {
                            ImageRejection imgRejection = new ImageRejection();
                            imgRejection.setRejectionReasonId(selectedRejectionReason);
                            imgRejection.setImageId(imageToReject);
                            imgRejection.setEntryDate(date);
                            imgRejection.setUserId(AuthenticationUtils.getLoggedUser());
                            imageRejectionList.add(imgRejection);
                        }
                        imageToReject.setImageRejectionList(imageRejectionList);
                        imageToReject.setStatus(false);
                        imageToReject.setIsRejected(true);
                        getEjbImagesFacade().edit(imageToReject);
                        if (!JsfUtil.isValidationFailed()) {///
                            currentImageId = null;
                            selectedRejectionReasons = null;
                            JsfUtil.addSuccessMessage("Image was successfully rejected.");

                            updateCurrentView();
                        }
                    }
                }
            } catch (EJBException ex) {
                JsfUtil.addErrorMessage("There was an error trying to reject the current image.");
            }
        }
    }

    public void changeIsOpenViewerState() {
        isOpenViewer = !isOpenViewer;
    }

    public Annotations getAnnotations(java.lang.Integer id) {
        return getAnnotationsFacade().find(id);
    }

    public List<Annotations> getItemsAvailableSelectMany() {
        return getAnnotationsFacade().findAll();
    }

    public List<Annotations> getItemsAvailableSelectOne() {
        return getAnnotationsFacade().findAll();
    }

    public boolean isIsSelectAll() {
        return isSelectAll;
    }

    public void setIsSelectAll(boolean isSelectAll) {
        this.isSelectAll = isSelectAll;
    }

    public boolean isIsOnXray() {
        return isOnXray;
    }

    public void setIsOnXray(boolean aIsOnXray) {
        isOnXray = aIsOnXray;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
//        System.out.println("NEW INTERVAL VALUE:::: " + interval);
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public String getJasonTest() {
        return jasonTest;
    }

    public void setJasonTest(String jasonTest) {
        this.jasonTest = jasonTest;
    }

    public boolean isIsOpenViewer() {
        return isOpenViewer;
    }

    public void setIsOpenViewer(boolean isOpenViewer) {
        this.isOpenViewer = isOpenViewer;
    }

    public void setProgressBar(Integer progressBar) {
        this.progressBar = progressBar;
    }

    public Integer getProgressBar() {
//        System.out.println("Progress Bar Value: " + progressBar);
        return progressBar;
    }

    public boolean isIsHDRSensor() {
        return isHDRSensor;
    }

    public void setIsHDRSensor(boolean isHDRSensor) {
        this.isHDRSensor = isHDRSensor;
    }

    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public boolean isIsOutputFolderReady() {
        return isOutputFolderReady;
    }

    public void setIsOutputFolderReady(boolean isOutputFolderReady) {
        this.isOutputFolderReady = isOutputFolderReady;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void setCurrentXrayImage(String currentXrayImage) {
        this.currentXrayImage = currentXrayImage;
    }

    public Integer getCurrentImageId() {
        return currentImageId;
    }

    public void setCurrentImageId(Integer currentImageId) {
        this.currentImageId = currentImageId;
    }

    public boolean isIsInImageAcquisition() {
        return isInImageAcquisition;
    }

    public void setIsInImageAcquisition(boolean isInImageAcquisition) {
        this.isInImageAcquisition = isInImageAcquisition;
    }

    public boolean isIsInImageProcess() {
        return isInImageProcess;
    }

    public void setIsInImageProcess(boolean isInImageProcess) {
        this.isInImageProcess = isInImageProcess;
    }

    public List<AbstractSelectedAnnotatePart> getSelectedTeethItemsForJavascript() {
        return selectedTeethItemsForJavascript;
    }

    public void setSelectedTeethItemsForJavascript(List<AbstractSelectedAnnotatePart> selectedTeethItemsForJavascript) {
        this.selectedTeethItemsForJavascript = selectedTeethItemsForJavascript;
    }

    public Studies getStudy() {
        return study;
    }

    public void setStudy(Studies study) {
        this.study = study;
    }

    public boolean isIsWatchService() {
        return isWatchService;
    }

    public void setIsWatchService(boolean isWatchService) {
        this.isWatchService = isWatchService;
    }

    public Images getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Images currentImage) {
        this.currentImage = currentImage;
    }

    public String getColumnPixelSpacing() {
        return columnPixelSpacing;
    }

    public void setColumnPixelSpacing(String columnPixelSpacing) {
        this.columnPixelSpacing = columnPixelSpacing;
    }

    public String getRowPixelSpacing() {
        return rowPixelSpacing;
    }

    public void setRowPixelSpacing(String rowPixelSpacing) {
        this.rowPixelSpacing = rowPixelSpacing;
    }

    public List<Annotations> getTempBodyPartViews() {
        return tempBodyPartViews;
    }

    public void setTempBodyPartViews(List<Annotations> tempBodyPartViews) {
        this.tempBodyPartViews = tempBodyPartViews;
    }

    public Integer getRejectedImageId() {
        return rejectedImageId;
    }

    public void setRejectedImageId(Integer rejectedImageId) {
        this.rejectedImageId = rejectedImageId;
    }

    public void setIsPanelReady(boolean isPanelReady) {
        this.isPanelReady = isPanelReady;
    }

    public boolean getIsPanelReady() {
        return isPanelReady;
    }

    public boolean isIsSocketServerReady() {
        return isSocketServerReady;
    }

    public void setIsSocketServerReady(boolean isSocketServerReady) {
        this.isSocketServerReady = isSocketServerReady;
    }

    public AcquistionDevices getCurrentPanel() {
        return getEjbAcquisitionDevicesFacade().getCurrentByType("Flat Panel");
    }

    public String getImageSourcePath() {
        return imageSourcePath;
    }

    public void setImageSourcePath(String imageSourcePath) {
        this.imageSourcePath = imageSourcePath;
    }

    @FacesConverter(forClass = Annotations.class)
    public static class AnnotationsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AbstractAnnotationsController controller = (AbstractAnnotationsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "annotationsController");
            return controller.getAnnotations(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Annotations) {
                Annotations o = (Annotations) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Annotations.class.getName()});
                return null;
            }
        }
    }

}
