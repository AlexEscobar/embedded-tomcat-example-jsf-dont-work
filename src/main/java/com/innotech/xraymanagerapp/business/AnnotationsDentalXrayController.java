/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.google.gson.Gson;
import static com.innotech.xraymanagerapp.business.AbstractAnnotationsController.SENSOR_TIMEOUT;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanel;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelCommunicationMessagesModel;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelFactory;
import com.innotech.xraymanagerapp.business.generalxray.ImageAcquisitionDeviceInterface;
import com.innotech.xraymanagerapp.careray.CRPanelController;
import com.innotech.xraymanagerapp.controller.GeneralXrayController;
import com.innotech.xraymanagerapp.controller.StudiesController;
import com.innotech.xraymanagerapp.controller.socket.SocketClient;
import com.innotech.xraymanagerapp.controller.ViewerController;
import com.innotech.xraymanagerapp.controller.socket.SocketClientController;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.SensorImageController;
import com.innotech.xraymanagerapp.controller.util.constants.Urls;
import com.innotech.xraymanagerapp.dto.AcquistionDevicesFacade;
import com.innotech.xraymanagerapp.dto.AnnotationsFacade;
import com.innotech.xraymanagerapp.dto.ImagesFacade;
import com.innotech.xraymanagerapp.dto.RejectionReasonsFacade;
import com.innotech.xraymanagerapp.dto.StudiesFacade;
import com.innotech.xraymanagerapp.dto.TeethNumbersFacade;
import com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade;
import com.innotech.xraymanagerapp.ezsensor.UsbConnectionValidator;
import com.innotech.xraymanagerapp.model.AnimalSizes;
import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.RejectionReasons;
import com.innotech.xraymanagerapp.model.SelectedTeethNumber;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.TeethNumbers;
import com.innotech.xraymanagerapp.model.dicom.DicomUtils;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Hi
 */
@Stateless
public class AnnotationsDentalXrayController extends AbstractAnnotationsController<TeethNumbersFacade, TeethNumbers> implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.AnnotationsFacade ejbFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ImagesFacade ejbImagesFacade;
    @EJB
    private TeethNumbersFacade ejbTeethNumberFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.StudiesFacade ejbStudiesFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.BodyPartViewsFacade ejbBodyPartViewFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade ejbViewDicomTagsFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.AcquistionDevicesFacade ejbAcquisitionDevicesFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.RejectionReasonsFacade ejbRejectionsFacade;
    @Inject
    @Push
    private PushContext sensorMessageChannel;

    private CRPanelController crPanelController;
//    @EJB
//    CRPanelController crPanelController;
    private RejectionReasons[] selectedRejectionReasons;
    private List<RejectionReasons> rejectionReasons;

    protected List<TeethNumbers> teethItems = null;

    private boolean isGeneratorReady;
    private boolean isPanelReady;
    private ImageAcquisitionDeviceInterface imageAcquisitionDevice;
    private final int PANEL_COMMUNICATION_TYPE = 2;// 1 = JNI through .dll, 2 = sockets
    private String msg;

    @PostConstruct
    @Override
    public void init() {
        socketClientPort = 1225;

        if (Objects.isNull(client)) {
            client = new SocketClient();
        } else {
            if (socketClientPort != client.SERVER_PORT) {
                client.closeConnection();
                client = new SocketClient();
            }
        }
        setViewerBusinessController(getViewerControllerBean());
//        System.out.println("Socket client dental: " + client);
        imageAcquisitionDevice = FlatPanelFactory.getCurrentPanelSingletonInstance(PANEL_COMMUNICATION_TYPE, getSocketClient(), socketClientPort);
        crPanelController = CRPanelController.getSingletonInstance(imageAcquisitionDevice);
        initializeProperties();
        isHDRSensor = JsfUtil.getSensorConfiguration();
        setTodaysDate(JsfUtil.getStringDate(new Date(), "yyyyMMdd"));
        ExecutorService executor = Executors.newFixedThreadPool(1);

//        // execute the thread that waits for radiation to hit the panel
        executor.submit(() -> {
            performCarerayPanelSecuriryChecks();
        });
        executor.shutdown();
        addImageDTOToContextMap();
//        progressBar = null;
    }

    @PreDestroy
    public void predestroy() {
        closeSensorCommunication();
        client = null;
    }

    @Override
    public void stopSensorAndOpenViewer() {
        closeSensorCommunication();
        try {
            StudiesController petC = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
            petC.openOHIFViewer();
        } catch (Exception ex) {
            Logger.getLogger(AbstractAnnotationsController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void addImageDTOToContextMap() {
        FacesContext cont = FacesContext.getCurrentInstance();
        ExternalContext extCont = cont.getExternalContext();
        extCont.getSessionMap().put("imageFacade", ejbImagesFacade);
    }

    /**
     *
     * Updates the current annotations view on new image detected
     *
     */
    @Override
    public void updateCurrentView() {
        try {
            System.out.println("Updating view...");
            PrimeFaces instance = PrimeFaces.current();
            List<String> idsToUpdate = new ArrayList(10);
            idsToUpdate.add("AnnotationsCreateForm:xrayCurrentContainer");// updates the current image container
            idsToUpdate.add("AnnotationsCreateForm:pollGrid");// updates the current image container
            idsToUpdate.add("XrayActionForm");
            instance.ajax().update(idsToUpdate);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void updateXrayButtons() {
        try {
            PrimeFaces instance = PrimeFaces.current();
            instance.ajax().update("XrayActionForm");
        } catch (NullPointerException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);

        }

    }

    public com.innotech.xraymanagerapp.dto.ImagesFacade getImageDTOFromContextMap() {
        FacesContext cont = FacesContext.getCurrentInstance();
        ExternalContext extCont = cont.getExternalContext();
        return (com.innotech.xraymanagerapp.dto.ImagesFacade) extCont.getSessionMap().get("imageFacade");
    }

    @Override
    public ViewerController getViewerControllerBean() {
        return (ViewerController) JsfUtil.getSessionScopeBean("viewerController");
    }

    @Override
    public void setViewerBusinessController(com.innotech.xraymanagerapp.business.ViewerBusinessController vc) {
        this.vc = vc;
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

    public void notifyUI(String message) {
//        System.out.println("Message received from panel: " + message);
        updateAllUIValues();
    }

    public void sendMessage(FlatPanelCommunicationMessagesModel message) {
        sensorMessageChannel.send(message);
    }

    /**
     * Function called from the UI path
     * /annotations/sensorCommunicationComponent/sensorCommunication.xhtml
     * (remoteCommand: showNotifications) It is triggered from the method
     * sensorMessageChannel.send(message) above
     */
    public void reportSensorDisconnectionErrors() {
        isOnXray = false;
        stopProgressBar();
        String headerMessage = "There was an error with the sensor";
        String bodyMessage = "Check the USB connection.";
        Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, "{0} - {1} - {2}", new Object[]{headerMessage, bodyMessage, msg});
        JsfUtil.addErrorMessage("There was an error with the sensor", msg);
        updateAll();
    }

    @Override
    public TeethNumbers getAnnotatePartById(String id) {
        if (getAnnotatePartItems() != null) {
            for (TeethNumbers partItem : teethItems) {
                if (partItem.getId().equals(id)) {
                    return partItem;
                }
            }
        }
        return null;
    }

    @Override
    public void setSelectedTeethItemsFromDb() {
        if (items != null && selectedTeethItemsForJavascript.size() < 1) {
            for (Annotations item : items) {
                // Add only those items with status = true = activate
                if (item.getTeethNumberId() != null && item.getStatus()) {
                    selectedTeethItemsForJavascript.add(new SelectedTeethNumber(item.getTeethNumberId().getId(), true));
                }
            }
        }
    }

    /**
     * Searches for the selected tooth in the Annotation item list, if is found
     * it will change the status depending on the current status. if is not it
     * will create an annotation for this selected tooth.
     *
     * @param id The Id of the part to annotate
     */
    @Override
    public void createOrUpdateOnImageMapSelection(String id) {
        if (getItems() != null) {
            boolean exists = false;
            if (getAnnotatePartItems() != null) {
                for (TeethNumbers bodyPartItem : teethItems) {
                    if (Objects.equals(bodyPartItem.getId(), id)) {
                        for (Annotations item : items) {
                            // search for the annotation of this tooth in the annotation list (items)
                            if (item.getTeethNumberId() != null) {
                                if (Objects.equals(item.getTeethNumberId().getId(), id)) {
                                    setSelected(item);
                                    //update the status
                                    destroy();
                                    exists = true;
//                                    break;
                                }
                            }

                        }
                        if (exists) {
                            break;
                        } else {
                            createDirect(bodyPartItem);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds color to the corresponding body part selected from the annotation
     * list to the animal body part chart on the x-ray view
     */
    public void setSelectedAnnotatedArea() {
        try {
            String javaScriptToExcute = new StringBuilder("setColorToSelectedArea(")
                    .append(selected.getBodyPartsId().getId())
                    .append(")")
                    .toString();
            PrimeFaces.current().executeScript(javaScriptToExcute);
        } catch (java.lang.NullPointerException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void clearTemporaryAnnotationsList() {
        try {
            setTempBodyPartViews(new ArrayList());
        } catch (NullPointerException ex) {
            Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, "{0} - AnnotationsController.clearTemporaryAnnotationsList()", ex.getMessage());
        }
    }

    private AnnotationsFacade getFacade() {
        return ejbFacade;
    }

    @Override
    public void initImageSourcePath() {
        setImageSourcePath(SensorImageController.dentalSensorSourceFilePath);
    }

    @Override
    public String buildImageFullName() {
        //copy the image into the x-ray images folder
        return getSelected().getStudyId().getPatientId().getId() + "_" + getSelected().getStudyId().getPatientId().getName().replace(" ", "_").replace(",", "") + "_"
                + "AN_" + "_" + getSelected().getTeethNumberId().getName().replace(" ", "_") + getSelected().getTeethNumberId().getNumber().replace(" ", "_") + "_SID_" + getSelected().getStudyId().getId() + "_";
    }

    @Override
    public Images buildAndPersistImage(String imageName, String imagePath) {
        Date d = new Date();
        Images newXrayImage = new Images(null, imageName, d, true, getSelected(), getSelected().getUserId(), getSelected().getStudyId());
        newXrayImage.setSeriesInstanceUID(DicomUtils.getSeriesInstanceUID() + JsfUtil.formatDateWithPattern(d, "yyyyMMddHHmmss"));
        newXrayImage.setsOPInstanceUID(DicomUtils.getSopInstanceUID() + JsfUtil.formatDateWithPattern(d, "yyyyMMddHHmmss"));
        newXrayImage.setImagePath(imagePath);
        newXrayImage.setColumnPixelSpacing(columnPixelSpacing);
        newXrayImage.setRowPixelSpacing(rowPixelSpacing);
        // Persist the image path on the database and get the id
        newXrayImage = persistNewImage(newXrayImage);
        return newXrayImage;
    }

    @Override
    public TeethNumbersFacade getEjbTeethNumberFacade() {
        return ejbTeethNumberFacade;
    }

    @Override
    public AnnotationsFacade getAnnotationsFacade() {
        return getFacade();
    }

    @Override
    public ImagesFacade getImagesFacade() {
        return ejbImagesFacade;
    }

    @Override
    public StudiesFacade getStudiesFacade() {
        return ejbStudiesFacade;
    }

    @Override
    public ViewDicomTagsFacade getViewDicomTagsFacade() {
        return ejbViewDicomTagsFacade;
    }

    private void initializeSensors() {
        // initializes the sensor controller depending on the current sensor defined on the configuration
        if (isHDRSensor) {
            columnPixelSpacing = "0.0192582";
            rowPixelSpacing = "0.0190678";
//            The HDR sensor should be initialize in order to interact with the registry values the sensor system sets up during its process
//            initializeHDRSensorCommunication();
        } else {
            columnPixelSpacing = "0.03016352";
            rowPixelSpacing = "0.03013699";
//            ezSensorKiller();
        }
    }

    @Override
    public void initializeProperties() {

        if (teethItems == null) {
            getAnnotatePartItems();
        }
        initializeSensors();
        initImageSourcePath();
        selectedTeethItemsForJavascript = new ArrayList();
    }

    @Override
    public List<TeethNumbers> getAnnotatePartItems() {
        if (teethItems == null) {
            Studies sc = getStudyByStudyList();
            if (sc != null) {
                teethItems = getEjbTeethNumberFacade().findAll();
            }
        }
        return teethItems;
    }

    @Override
    public String getAnnotationsAsJson() {
        setSelectedTeethItemsFromDb();
//        annotationsAsJson = new Gson().toJson(selectedTeethItemsForJavascript);
        return new Gson().toJson(selectedTeethItemsForJavascript);
    }

    /**
     * Create an annotation on direct selection over the image map
     *
     * @param tn
     */
    @Override
    public void createDirect(TeethNumbers tn) {
// if there is an open study, creates a new annotation object
        setStudy(getCurrentStudyByCurrentPatient());
        if (getStudy() != null) {
            initializeSelected();
            getSelected().setTeethNumberId(tn);
            Annotations selectedBackup = getSelected();
            persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AnnotationsCreated"));
            setSelected(selectedBackup);
            if (!JsfUtil.isValidationFailed()) {///
                setSelected(null);
                itemsToShow = null;
                items = null;    // Invalidate list of items to trigger re-query.
                initializeProperties();
            }
        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), ResourceBundle.getBundle("/Bundle").getString("NotCurrentStudy"));
        }

    }

    @Override
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

    @Override
    public List<Annotations> getItemsToShow() {
        if (itemsToShow == null) {
            if (getItems() != null) {
                itemsToShow = new ArrayList();
                for (Annotations item : items) {
                    if (item.getStatus() && item.getTeethNumberId() != null) {
                        itemsToShow.add(item);
                    }
                }
                if (itemsToShow.isEmpty()) {
                    itemsToShow = null;
                }
            }
        }
        return itemsToShow;
    }

    public Integer getAnimalSize() {
        return animalSize;
    }

    public void setAnimalSize(Integer animalSize) {
        this.animalSize = animalSize;
    }

    @Override
    public void startNextCurrent() {
    }

    public Annotations getTemporarySelected() {
        return temporarySelected;
    }

    public void setTemporarySelected(Annotations temporarySelected) {
        this.temporarySelected = temporarySelected;
    }

    ///// Panel controller section
    public int openPanelDevice() {
        return crPanelController.connectDetector();
    }

    @Override
    public int closePanelDevice() {
        return crPanelController.disconnectDetector();
    }

    @Override
    public void stopCommunication() {
        crPanelController.stopCommunication();
    }

    public int startPanelWaitingRadiationLoop() {
        crPanelController.connectDetector();
        return crPanelController.startRad();
    }

    public void stopPanelWaitingRadiationLoop() {
        crPanelController.setContinueRad(false);
    }

    public String getPanelDeviceErrorMessage(int errorCode) {
        String panelDeviceMessage = crPanelController.translateError(errorCode);
        System.out.println("Panel device message::::::::: " + panelDeviceMessage + ". - Panel device message Code::::::::: " + errorCode);
        return panelDeviceMessage;
    }

    public boolean getPanelDeviceWaitingStatus() {
        return crPanelController.getWaitingStatus();
    }

    public boolean getPanelDeviceContinueRad() {
        return crPanelController.getContinueRad();
    }

    public boolean getExecuteNextProc() {
        return crPanelController.getExecuteNextProc();
    }

    public void simulateRadiation() {
        crPanelController.simulateRadiation();
    }

    //CareRay
    @Asynchronous
    public Future<Boolean> startCommunicationWithPanel() {
        System.out.println("Connecting with the panel...");
        isPanelReady = crPanelController.triage();
        return new AsyncResult<>(isPanelReady);
    }

    //CareRay
//    @Asynchronous
    public Boolean performCarerayPanelSecuriryChecks() {
        System.out.println("!!!! Calling triage() ");
        isPanelReady = crPanelController.triage();
        return isPanelReady;
    }

    public int getPanelCurrentImageProcessingState() {
        return crPanelController.getCurrentProcessState();
    }

    private boolean checkHDRSensorConnection() {
        return UsbConnectionValidator.checkHDRSensorUsbConnection();
//        return new EZSensorUSBChecker().detectios() == 1;
    }

    private boolean checkEZSensorConnection() {
        return UsbConnectionValidator.checkEZSensorUsbConnection();
//        return new EZSensorUSBChecker().detectios() == 1;
    }

    /**
     * Selects or deselects all available teeth on the teeth chart image
     */
    public void selectDeselectTeeth() {
        itemsToShow = null;
        if (isSelectAll) {
            annotateAllTeethChart();
        } else {
            deselectAll();
        }
    }

    /**
     * Returns all the teeth on the TeethNumber
     *
     * @return
     */
    public List<TeethNumbers> getTeethItems() {
        if (teethItems == null) {
            Studies sc = getStudyByStudyList();
            if (sc != null) {
                teethItems = getEjbTeethNumberFacade().findBySpecies(sc.getPatientId().getSpecieId().getId());
            }
        }
        return teethItems;
    }

    /**
     * Selects all available teeth and creates annotations for them.
     *
     * @return
     */
    public String annotateAllTeethChart() {
        if (getTeethItems() != null) {
            for (TeethNumbers teethItem : teethItems) {
                TeethNumbers tn = teethItem;
                boolean isFound = false;
                if (getItems() != null) {
                    for (Annotations item : items) {
                        TeethNumbers teethNumberId = item.getTeethNumberId();
                        String tnId = tn.getId();
                        if (teethNumberId != null && tnId.equals(teethNumberId.getId())) {
                            isFound = true;
                            item.setStatus(true);
                            break;
                        }
                    }
                    if (!isFound) {
                        initializeSelected();
                        selected.setTeethNumberId(teethItem);
                        items.add(selected);
                        selectedTeethItemsForJavascript.add(new SelectedTeethNumber(teethItem.getId(), true));
                    }
                }
            }
            persistMultiEntities(items);
            if (!JsfUtil.isValidationFailed()) {
                selected = null;
                itemsToShow = null;
                items = null;    // Invalidate list of items to trigger re-query.
                initializeProperties();
            }
        }

        return new StringBuilder("pretty:").append(Urls.XRAY_PAGE).toString();
    }

    ///// Panel controller section ENDS
    @Override
    public void startWaitingForRadiation() {

        try {
            msg = "";
            boolean isSensorConnected;
//            boolean existCalibrationFiles;
            if (isHDRSensor) {

//          Native call to windows device manager to chechk if the sensor is physically connected.
                isSensorConnected = checkHDRSensorConnection();
//                existCalibrationFiles = validateCalibrationFiles();
            } else {
                if (!isWatchService) {
                    writeIntoFile(String.valueOf(new Random().nextInt(1000)));// if this is successfull sets isWatchService to true
                    try {
                        Thread.sleep(200);// it takes a while mean the file writer writes and the watch service detects the change
                    } catch (InterruptedException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
                    }
                }
                isSensorConnected = checkEZSensorConnection();
//                existCalibrationFiles = true;// does not check for calibration files when the sensor is different og HDR because we only have controll over HDR sensors.
            }

//            if sensor connected
//            if (isSensorConnected) {
                if (true) {
                if (!isPanelReady) {
                    isPanelReady = performCarerayPanelSecuriryChecks();
                    Thread.sleep(500);
                    startPanelWaitingRadiationLoop();
                    Thread.sleep(500);
                }
                if (isPanelReady) {// calls triage function

//                if annotation list has at least one annotation selected
                    if (verifyAnnotationList() || isOnXray) {
                        xrayProcessManual();
//                startService();
                    } else {
                        stopProgressBar();
                        JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("AnnotationListEmpty"));
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("AnnotationListEmpty"));
                    }
                } else {
                    stopProgressBar();
                    if (FlatPanel.error.contains("Error")) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, FlatPanel.error);
                        JsfUtil.addErrorMessage(FlatPanel.error, "Try disconnect and connect the sensor back to the USB Port, then click start again");
                    }

                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Sensor security checks have failed");
                    JsfUtil.addErrorMessage("Sensor security checks have failed");
                }
            } else {
//            stopService();
                stopProgressBar();
                isOnXray = false;
                closePanelDevice();
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
                JsfUtil.addErrorMessage("Sensor is not connected to the USB port, please connect the Sensor and try again");
            }

        } catch (InterruptedException ex) {
            stopProgressBar();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    @SuppressWarnings("empty-statement")
    private void xrayProcessAuto() {
        for (Annotations annotations : itemsToShow) {
            if (!annotations.getIsDone()) {
                selected = annotations;
                // uses compare function to know if the sent technique is accepted by the generator
                if (isOnXray == true) {//check here for the panel security checks response
                    setInterval(SENSOR_TIMEOUT);
                    activeIndex = 1;
                    isNewImage = false;
                    // Start panel image acquisition
                    while (!isNewImage && isOnXray) {
                    }// waits for the image to show up or the timer to finish
                }
            }
            if (!isOnXray) {
                // show error message
                break;
            }
        }
    }

    private void xrayProcessManual() {
        itemsToShow = null;
        items = null;
        if (setCurrent()) {
            setInterval(SENSOR_TIMEOUT);
            activeIndex = 1;
            System.out.println("Generator READY::::::::::::::");
            // Start panel image acquisition
            startImageAcquisitionProcess();
        } else {
            stopProgressBar();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            isOnXray = false;
        }
    }

    private boolean startImageAcquisitionProcess() {
//        int panelResponseCode = openPanelDevice();
//        System.out.println("The panel response: " + getPanelDeviceErrorMessage(panelResponseCode));
//        if (panelResponseCode == 0 || panelResponseCode == 1002) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // execute the thread that waits for radiation to hit the panel
        executor.submit(() -> {
            startPanelWaitingRadiationLoop();// calls start rad, sends <RAD>
        });
        executor.shutdown();

        int counter = 0;

        setCurrentValue(0);
        int currentImageProcessingState = getPanelCurrentImageProcessingState();// get the <CPS> value that could be any state between 1 to 8
        while (currentImageProcessingState != 1) {
            try {
                // true if the panel is ready and waiting for radiation
                if (counter++ == 10) {// try 5 times to get the positive status. total waiting time is 5 seconds
                    break;
                }
                Thread.sleep(1000);
                currentImageProcessingState = getPanelCurrentImageProcessingState();
                startPanelWaitingRadiationLoop();
            } catch (InterruptedException ex) {
                Logger.getLogger(AnnotationsBodyPartsController.class
                        .getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        System.out.println("Just out the while loop. Total seconds: (" + FlatPanel.error + ") - counter:" + counter);

        setCurrentValue(currentImageProcessingState);
        isOnXray = currentImageProcessingState == 1;
        setProgressBar(0);
        if (!isOnXray) {
            stopProgressBar();
            closePanelDevice();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            updateAll();
        }
        System.out.println("Is On x-ray?::: " + isOnXray);
        return isOnXray;
    }

    @Override
    public void updateAllUIValues() {
        if (isOnXray) {
            try {
                // add here generator and panel security checks, if something fails breaks all the process and show a blocking error mesage
                if (isHDRSensor) {
//                    if (!checkHDRSensorConnection()) {
//                        sensorKiller();
//                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, FlatPanel.error, "Sensor is not connected to the USB port, please connect the Sensor and try again");
//                        JsfUtil.addErrorMessage("Sensor is not connected to the USB port, please connect the Sensor and try again");
//                    }
                    if (FlatPanel.error.contains("Error")) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, FlatPanel.error);
                        JsfUtil.addErrorMessage(FlatPanel.error, "Try disconnect and connect the sensor back to the USB Port, then click start again");
                    }
                    System.out.println("FlatPanel.continueRad: " + FlatPanel.continueRad + " - FlatPanel.currentProcessState: " + FlatPanel.currentProcessState);
                    if (FlatPanel.continueRad) {
//                if (true && getIsTechniqueAceptedByGenerator()) {

                        // current stage on the image processing dll
                        int processValue = 0;
                        switch (PANEL_COMMUNICATION_TYPE) {
                            case 1:
                                processValue = getPanelCurrentImageProcessingState();
                                break;
                            case 2:
                                processValue = FlatPanel.currentProcessState;
                                break;

                        }
//                    if (PANEL_COMMUNICATION_TYPE == 1) {
                        if (processValue < 7) {

                            if (processValue == 2 && !isReceivingRadiation) {
//                            setCurrentValue(processValue);
//                        updateXrayButtons();// updates the buttons in order to show the moving wheel that indicates that radiation was received by the panel
                                isReceivingRadiation = true;
                            }
                            isInImageProcess = false;
                        }
//                        
//                    }

//                    System.out.println("CURRENT Panel status : " + getCurrentValue() + " - " + isInImageProcess);
                        // if image was taken correctly then add the incoming image to the selected annotation on the annotations list on the x-ray page
                        if (processValue == 8 && !isInImageProcess) {
                            addImageToCurrentAnnotation(SOURCE_IMAGE_NAME);

                            setCurrentValue(processValue);
                            if (PANEL_COMMUNICATION_TYPE == 1) {
                            }
                            //progressBar = 100;
                        }

                        // 
                        if (progressBar != 100) {
                            if (getCurrentValue() != processValue) {
                                calculateProgressBarPercentage(processValue);
//                            if (PANEL_COMMUNICATION_TYPE == 1) {
                                setCurrentValue(processValue);
//                            }
                                isReceivingRadiation = false;
                            }
//
//                        if (processValue == 8) {
//                            setProgressBar(0);
//                        }
                        }
//                    System.out.println("Value from the Panel current stats : " + processValue);
//                    System.out.println("Progress bar value: " + progressBar);
                    } else {
                        System.out.println("isOnXray will be set to false because: ");
                        isOnXray = false;
                        if (!FlatPanel.continueRad) {
                            System.out.println("Sensor device has errors");
//                            JsfUtil.addErrorMessage("Sensor device has errors");
                        }

                    }
                }
            } catch (NullPointerException e) {
                System.out.println("isOnXray will be set to false because the exception: " + e.getMessage());
                isOnXray = false;
                Logger.getLogger(GeneralXrayController.class
                        .getName()).log(Level.SEVERE, e.getMessage(), e);
            } catch (IOException ex) {
                Logger.getLogger(AnnotationsDentalXrayController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void updateAll() {
        updateXrayButtons();
        updateGrowl();
    }

    @Override
    public void getImageAcquisitionDeviceCurrentProcessStage() {

//        updateAllUIValues();
//        updateXrayButtons();
    }

    @Override
    public Integer getProgressBar() {
//        getImageAcquisitionDeviceCurrentProcessStage();
//        System.out.println("FlatPanel current values: \n" + FlatPanel.error);
        System.out.println("Is OnXray progresa bar: " + isOnXray + " - " + progressBar);
//        if (!isOnXray) {
//            stopProgressBar();
//        }
//        updateAllUIValues();
//        updateAll();

        updateXrayButtons();
        return super.getProgressBar();
    }

    public void stopProgressBar() {
        System.out.println("canceling progressbar process...");
        PrimeFaces.current().executeScript("stopProgressBar();");
        System.out.println("progressbar process canceled...");
    }

    public boolean isIsGeneratorReady() {
        return isGeneratorReady;
    }

    public void setIsGeneratorReady(boolean isGeneratorReady) {
        this.isGeneratorReady = isGeneratorReady;
    }

    public boolean isIsPanelReady() {
        return isPanelReady;
    }

    @Override
    public ImagesFacade getEjbImagesFacade() {
        return ejbImagesFacade;
    }

    @Override
    public RejectionReasonsFacade getEjbRejectionsFacade() {
        return ejbRejectionsFacade;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public AcquistionDevicesFacade getEjbAcquisitionDevicesFacade() {
        return ejbAcquisitionDevicesFacade;
    }
}
