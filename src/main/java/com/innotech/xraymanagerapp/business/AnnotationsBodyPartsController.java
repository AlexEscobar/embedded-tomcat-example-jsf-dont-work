package com.innotech.xraymanagerapp.business;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanel;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelCommunicationMessagesModel;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelFactory;
import com.innotech.xraymanagerapp.careray.CRPanelController;
import com.innotech.xraymanagerapp.controller.GeneralXrayController;
import com.innotech.xraymanagerapp.controller.GeneratorController;
import com.innotech.xraymanagerapp.controller.socket.SocketClient;
import com.innotech.xraymanagerapp.controller.socket.SocketClientController;
import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.dto.AnnotationsFacade;
import com.innotech.xraymanagerapp.controller.util.MoveXrayImage;
import com.innotech.xraymanagerapp.controller.ViewerController;
import com.innotech.xraymanagerapp.controller.util.constants.Urls;
import com.innotech.xraymanagerapp.dto.BodyPartsFacade;
import com.innotech.xraymanagerapp.dto.ImagesFacade;
import com.innotech.xraymanagerapp.dto.StudiesFacade;
import com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade;
import com.innotech.xraymanagerapp.model.AnimalSizes;
import com.innotech.xraymanagerapp.model.BodyPartViews;
import com.innotech.xraymanagerapp.model.BodyParts;
import com.innotech.xraymanagerapp.model.SelectedBodyPart;
import com.innotech.xraymanagerapp.model.Studies;
import java.beans.PropertyChangeEvent;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;
import org.primefaces.PrimeFaces;
import com.innotech.xraymanagerapp.business.generalxray.ImageAcquisitionDeviceInterface;
import com.innotech.xraymanagerapp.controller.util.SensorImageController;
import com.innotech.xraymanagerapp.dto.AcquistionDevicesFacade;
import com.innotech.xraymanagerapp.dto.RejectionReasonsFacade;
import java.util.Arrays;
import javax.annotation.PreDestroy;

@Stateless
public class AnnotationsBodyPartsController extends AbstractAnnotationsController<BodyPartsFacade, BodyParts> {

    @EJB
    private com.innotech.xraymanagerapp.dto.AnnotationsFacade ejbFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ImagesFacade ejbImagesFacade;
    @EJB
    private BodyPartsFacade ejbTeethNumberFacade;
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
    private PushContext panelMessageChannel;

    private CRPanelController crPanelController;

    protected List<BodyParts> bodyPartItems = null;
    protected List<BodyPartViews> bodyPartViewItems = null;

    private String currentSelectAnnotationOnImageMap;
    private boolean isGeneratorReady;
    private ImageAcquisitionDeviceInterface flatPanel;

    private final int PANEL_COMMUNICATION_TYPE = 2;// 1 = JNI through .dll, 2 = sockets

    public AnnotationsBodyPartsController() {
    }

    @PostConstruct
    @Override
    public void init() {
        socketClientPort = 1224;
        if (Objects.isNull(client)) {
            client = new SocketClient();
        } else {
            if (socketClientPort != client.SERVER_PORT) {
                client.closeConnection();
                client = new SocketClient();
            }
        }
        IS_GENERATOR_CONNECTED = JsfUtil.getIsGeneratorConnected();
        setViewerBusinessController(getViewerControllerBean());
        flatPanel = FlatPanelFactory.getCurrentPanelSingletonInstance(PANEL_COMMUNICATION_TYPE, getSocketClient(), socketClientPort);
        crPanelController = CRPanelController.getSingletonInstance(flatPanel);
        initializeProperties();
        initializeGeneratorProperties();
        setTodaysDate(JsfUtil.getStringDate(new Date(), "yyyyMMdd"));
        ExecutorService executor = Executors.newFixedThreadPool(1);

//        // execute the thread that waits for radiation to hit the panel
        executor.submit(() -> {
            performCarerayPanelSecuriryChecks();
        });
        executor.shutdown();
//        progressBar = null;
    }

    @Override
    public String preDestroy() {
        closeSensorCommunication();
        return new StringBuilder("pretty:").append(Urls.WORKLIST_PAGE).toString();
    }

    @PreDestroy
    public void destroyCom() {
        closeSensorCommunication();
        client = null;
    }

    @Override
    public void setViewerBusinessController(com.innotech.xraymanagerapp.business.ViewerBusinessController vc) {
        this.vc = vc;
    }


    public void sendMessage(FlatPanelCommunicationMessagesModel message) {
        panelMessageChannel.send(message);
    }

    @Override
    public ViewerController getViewerControllerBean() {
        return (ViewerController) JsfUtil.getSessionScopeBean("viewerController");
    }

    @Override
    public BodyParts getAnnotatePartById(String id) {
        if (getAnnotatePartItems() != null) {
            for (BodyParts partItem : bodyPartItems) {
                if (partItem.getId() == Integer.parseInt(id)) {
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
                if (item.getBodyPartsId() != null && item.getStatus()) {
                    selectedTeethItemsForJavascript.add(new SelectedBodyPart(item.getBodyPartsId().getId().toString(), true));
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
                for (BodyParts bodyPartItem : bodyPartItems) {
                    if (Objects.equals(bodyPartItem.getId().toString(), id)) {
                        for (Annotations item : items) {
                            // search for the annotation of this tooth in the annotation list (items)
                            if (item.getBodyPartsId() != null) {
                                if (Objects.equals(item.getBodyPartsId().getId().toString(), id)) {
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

    public void addTemporaryAnnotationOnImageMapSelection(String id) {
        if (currentSelectAnnotationOnImageMap != null && Objects.equals(currentSelectAnnotationOnImageMap, id)) {
            setTempBodyPartViews(new ArrayList());
            currentSelectAnnotationOnImageMap = null;
        } else {
            if (getAnnotatePartItems() != null) {
                for (BodyParts bodyPartItem : bodyPartItems) {
                    if (Objects.equals(bodyPartItem.getId().toString(), id)) {
                        addAddThreeViewsToTemporaryAnnotationsList(bodyPartItem);
                        break;
                    }
                }
            }
            currentSelectAnnotationOnImageMap = id;
        }
        setTemporarySelected(null);
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
            Logger.getLogger(AnnotationsDentalXrayController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void addAddThreeViewsToTemporaryAnnotationsList(BodyParts tn) {
// if there is an open study, creates a new annotation object
        setStudy(getCurrentStudyByCurrentPatient());
        if (getStudy() != null) {
            setTempBodyPartViews(new ArrayList());
            for (int i = 1; i <= getBodyPartViewItems().size(); i++) {
                Annotations annotation = initializeAnnotation(new Annotations());
                annotation.setBodyPartsId(tn);
                if (Objects.nonNull(animalSize)) {
                    annotation.setAnimalSizeId(new AnimalSizes(animalSize));
                }
                annotation.setId(i);
                annotation.setBodyPartViewId(getBodyPartViewItems().get(i - 1));
                addToTemporaryAnnotationsList(annotation);
            }
        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), ResourceBundle.getBundle("/Bundle").getString("NotCurrentStudy"));
        }

    }

    public void addToTemporaryAnnotationsList(Annotations ann) {
        getTempBodyPartViews().add(ann);
    }

    @Override
    public void initializeGeneratorProperties() {
        if (IS_GENERATOR_CONNECTED) {
            getGeneratorController().initializeVariables();
        }
    }

    public void clearTemporaryAnnotationsList() {
        try {
            setTempBodyPartViews(new ArrayList());
        } catch (NullPointerException ex) {
            Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, "{0} - AnnotationsController.clearTemporaryAnnotationsList()", ex.getMessage());
        }
    }

    public List<BodyPartViews> getBodyPartViewItems() {
        if (bodyPartViewItems == null) {
            bodyPartViewItems = ejbBodyPartViewFacade.findAll();
        }
        return bodyPartViewItems;
    }

    private AnnotationsFacade getFacade() {
        return ejbFacade;
    }
    
    @Override
    public void initImageSourcePath(){
        setImageSourcePath(SensorImageController.flatPanelSourceFilePath);
    }

    @Override
    public BodyPartsFacade getEjbTeethNumberFacade() {
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

    @Override
    public void initializeProperties() {
        if (bodyPartItems == null) {
            getAnnotatePartItems();
        }
        initImageSourcePath();
        selectedTeethItemsForJavascript = new ArrayList();
    }

    @Override
    public List<BodyParts> getAnnotatePartItems() {
        if (bodyPartItems == null) {
            Studies sc = getStudyByStudyList();
            if (sc != null) {
                bodyPartItems = getEjbTeethNumberFacade().findAll();
            }
        }
        return bodyPartItems;
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
    public void createDirect(BodyParts tn) {
// if there is an open study, creates a new annotation object
        setStudy(getCurrentStudyByCurrentPatient());
        if (getStudy() != null) {
            initializeSelected();
            getSelected().setBodyPartsId(tn);
            if (Objects.nonNull(animalSize)) {
                getSelected().setAnimalSizeId(new AnimalSizes(animalSize));
            }
            Annotations selectedBackup = getSelected();
            for (int i = 1; i < 4; i++) {
                getSelected().setBodyPartViewId(new BodyPartViews(i));
                persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AnnotationsCreated"));
                setSelected(selectedBackup);
            }
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
//        setStudy(getCurrentStudyByCurrentPatient());
//        if (getStudy() != null) {
//            initializeSelected();

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
//        } else {
//            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), ResourceBundle.getBundle("/Bundle").getString("NotCurrentStudy"));
//        }
        setCurrent(getItemsToShow().size() - 1);
        performSecurityChecks();
        return "";
    }

    @Override
    public List<Annotations> getItemsToShow() {
        if (itemsToShow == null) {
            if (getItems() != null) {
                itemsToShow = new ArrayList();
                for (Annotations item : items) {
                    if (item.getStatus() && item.getBodyPartsId() != null) {
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

    public void setItemsToShow(List<Annotations> items) {
        itemsToShow = items;
    }
    
    public Integer getAnimalSize() {
        return animalSize;
    }

    public void setAnimalSize(Integer animalSize) {
        this.animalSize = animalSize;
    }

    @Override
    public void startNextCurrent() {
        if (isOnXray) {
            setupGeneratorTechnique();
        }
    }

    public void setupGenerator() {
//        setSelectedAnnotatedArea();
        performSecurityChecks();
        if (isOnXray) {
            startWaitingForRadiation();
        }
        clearTemporaryAnnotationsList();
    }

    @Override
    public void performSecurityChecks() {
        setCurrent();
        setupGeneratorTechnique();
    }

    public Annotations getTemporarySelected() {
        return temporarySelected;
    }

    public void setTemporarySelected(Annotations temporarySelected) {
        this.temporarySelected = temporarySelected;
    }

    //// Generator controller section STARTS    
    public boolean getIsTechniqueAceptedByGenerator() {
        if (IS_GENERATOR_CONNECTED) {
            isGeneratorReady = getGeneratorController().compareSentAndResponseTechniques();
        } else {
            isGeneratorReady = true;
        }
        return isGeneratorReady;
    }

    public void saveNewGeneratorParametersForCurrentAnnotation() {
        Integer currentTechnique = 0;
        getGeneratorController().updateGeneratorTechniqueBySize(
                getSelected().getStudyId().getPatientId().getSpecieId().getId(),
                getAnimalSize(), getSelected().getBodyPartsId().getId(),
                getSelected().getBodyPartViewId().getId(), currentTechnique);
    }

    public void setupGeneratorTechnique() {
//        Integer currentTechnique = 0;
        boolean selectedAndSize = false;
        if (Objects.nonNull(getSelected())) {
            if (Objects.nonNull(getAnimalSize()) && Objects.nonNull(getSelected().getBodyPartsId())) {
//                isGeneratorReady = true;
                selectedAndSize = true;
                try {
                    if (IS_GENERATOR_CONNECTED) {
                        getGeneratorController().setupGeneratorTechniqueBySize(
                                getSelected().getStudyId().getPatientId().getSpecieId().getId(),
                                getAnimalSize(), getSelected().getBodyPartsId().getId(),
                                getSelected().getBodyPartViewId().getId());
                        Thread.sleep(500);
                        int counter = 0;
                        //                    waits one minute for a positive response from the generator
                        //                    if no positive response after the one minute, then the system notifies the user of the problem
                        getIsTechniqueAceptedByGenerator();
                        while (!getIsTechniqueAceptedByGenerator()) {
                            if (counter++ == 6) {
                                break;
                            }
                            Thread.sleep(1500);
                        }
                        System.out.println("isGeneratorReady;;;;;;;; " + isGeneratorReady);
                    } else {
                        isGeneratorReady = true;
                    }
                } catch (NullPointerException | InterruptedException ex) {
                    isGeneratorReady = false;
                    Logger.getLogger(GeneralXrayController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            } else {
                isGeneratorReady = false;
//                JsfUtil.addErrorMessage("animal size is not seleced");
            }
        } else {
            isGeneratorReady = false;
        }
        if (!isGeneratorReady) {
            isOnXray = false;
        }
        if (selectedAndSize && !isGeneratorReady) {
            isOnXray = false;
            setAnimalSize(null);
            JsfUtil.addErrorMessage("The generator did not receive the KV, MA, MS and MX parameters. DO NOT SHOOT AN X-RAY.");
            updateCurrentView();
            updateGrowl();
        }
    }

    public void setupGeneratorFromAnimalSizeSelection() {
        setAnimalSize();
        setupGeneratorTechnique();
        if (isOnXray) {
            startWaitingForRadiation();
        }
    }

    public void setAnimalSize() {
        if (Objects.nonNull(itemsToShow)) {
            itemsToShow.forEach((annotations) -> {
                annotations.setAnimalSizeId(new AnimalSizes(getAnimalSize()));
                updateSelected();
            });
        }
    }

    public GeneratorController getGeneratorController() {
        return (GeneratorController) JsfUtil.getSessionScopeBean("generatorController");
    }
    ////// Generator controller section ENDS

    ///// Panel controller section
    public int openPanelDevice() {
        return crPanelController.connectDetector();
    }

    @Override
    public int closePanelDevice() {
        isButtonStartPushed = false;
        return crPanelController.disconnectDetector();
    }

    @Override
    public void stopCommunication() {
        crPanelController.stopCommunication();
    }

    public int startRad() {
        openPanelDevice();
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
        setIsPanelReady(crPanelController.triage());
        return new AsyncResult<>(getIsPanelReady());
    }

    //CareRay
    @Asynchronous
    public Future<Boolean> performCarerayPanelSecuriryChecks() {
        System.out.println("!!!! Calling triage() ");
        setIsPanelReady(crPanelController.triage());
        return new AsyncResult<>(getIsPanelReady());
    }

    public int getPanelCurrentImageProcessingState() {
        return crPanelController.getCurrentProcessState();
    }

    ///// Panel controller section ENDS
    @Override
    public void startWaitingForRadiation() {

        try {

            boolean isSensorConnected = client.getIsAlive();
            boolean isGeneratorOn = true;
            if (IS_GENERATOR_CONNECTED) {
                isGeneratorOn = GeneratorController.getGp().isPw();
            }
//            if sensor connected and file watch running
            if (isGeneratorOn) {
                if (isSensorConnected) {
                    if (getIsTechniqueAceptedByGenerator() || !IS_GENERATOR_CONNECTED) {
                        if (performCarerayPanelSecuriryChecks().get()) {// calls triage function

//                if annotation list has at least one annotation selected
                            if (verifyAnnotationList() || isOnXray) {
                                xrayProcessManual();
                            } else {
                                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), ResourceBundle.getBundle("/Bundle").getString("AnnotationListEmpty"));
                                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("AnnotationListEmpty"));
                            }
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Panel security checks have failed");
                            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), "Panel security checks have failed");
                        }
                    } else {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "The Generator has not received the technique for the current annotation");
                        JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), "The Generator did not received the technique for the current annotation");
                    }

                } else {
                    isOnXray = false;
                    closePanelDevice();
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
                    JsfUtil.addErrorMessage("Check the panel socket server: ", ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
                }

            } else {
                isOnXray = false;
                closePanelDevice();
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Generator is OFF, Please turn it ON and try again");
                JsfUtil.addErrorMessage("Generator is OFF", "Please turn it ON and try again");
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("Error"), ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
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
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
            isOnXray = false;
        }
    }

    private boolean startImageAcquisitionProcess() {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // executes the thread that waits for radiation to hit the panel
        executor.submit(() -> {
            startRad();
        });
        executor.shutdown();

        int counter = 0;

        setCurrentValue(0);
        updateXrayButtons();
        isButtonStartPushed = true;
        int currentImageProcessingState = getPanelCurrentImageProcessingState();
        while (currentImageProcessingState != 1) {
            try {
                // true if the panel is ready and waiting for radiation
                if (counter++ == 50) {// try ten times to get the positive status. total waiting time is 50 seconds
                    break;
                }
                Thread.sleep(2000);
                currentImageProcessingState = getPanelCurrentImageProcessingState();
            } catch (InterruptedException ex) {
                Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        setCurrentValue(getPanelCurrentImageProcessingState());
        isOnXray = getPanelCurrentImageProcessingState() == 1;
        setProgressBar(0);
        if (!isOnXray) {
            closePanelDevice();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SensorProcessFailed"));
        }
        return isOnXray;
    }

    public void updateAllUIValues() {
        if (isOnXray) {
            try {
                // add here generator and panel security checks, if something fails breaks all the process and show a blocking error mesage

                if (FlatPanel.continueRad) {
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
                    if (processValue < 7) {
                        if (processValue == 2 && !isReceivingRadiation) {
                            isReceivingRadiation = true;
                        }
                        isInImageProcess = false;
                    }

                    System.out.println("CURRENT Panel status : " + processValue + " - " + isInImageProcess);
                    // if image was taken correctly then add the incoming image to the selected annotation on the annotations list on the x-ray page
                    if (processValue == 8 && !isInImageProcess) {
                        setCurrentImageId(addImageToCurrentAnnotation(SOURCE_IMAGE_NAME).getId());

                        setCurrentValue(processValue);
                        if (PANEL_COMMUNICATION_TYPE == 1) {
                        }
                    }

                    // 
                    if (progressBar != 100) {
                        if (getCurrentValue() != processValue) {
                            calculateProgressBarPercentage(processValue);
                            setCurrentValue(processValue);
                            isReceivingRadiation = false;
                        }
                    }
                } else {
                    System.out.println("isOnXray will be set to false because: ");
                    isOnXray = false;
                    if (!FlatPanel.continueRad) {
                        System.out.println("Flat Panel has errors");
                        JsfUtil.addErrorMessage("Flat Panel has errors");
                    }

                }
            } catch (NullPointerException e) {
                isOnXray = false;
                Logger.getLogger(GeneralXrayController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            } catch (IOException ex) {
                Logger.getLogger(AnnotationsBodyPartsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void updateAll() {
        updateXrayButtons();
        updateGrowl();
    }

    @Override
    public void getImageAcquisitionDeviceCurrentProcessStage() {
        boolean isGeneratorOn = true;
        if (IS_GENERATOR_CONNECTED) {
            isGeneratorOn = GeneratorController.getGp().isPw();

            if (!isGeneratorOn) {
                System.out.println("isOnXray will be set to false because: ");
                isOnXray = false;
                System.out.println("Generator is OFF, Please Turn it ON to continue");
                JsfUtil.addErrorMessage("Generator is OFF, Please Turn it ON to continue");
            }
        }
        updateXrayButtons();
    }

    @Override
    public Integer getProgressBar() {
        getImageAcquisitionDeviceCurrentProcessStage();

        Integer progress = super.getProgressBar();
        if(progress != null && progress > 0) {
            updateXrayButtons();
        }
        System.out.println("progress:" + progress);
        return progress;
    }

    public boolean isIsGeneratorReady() {
        return isGeneratorReady;
    }

    public void setIsGeneratorReady(boolean isGeneratorReady) {
        this.isGeneratorReady = isGeneratorReady;
    }    

    @Override
    public ImagesFacade getEjbImagesFacade() {
        return ejbImagesFacade;
    }

    @Override
    public RejectionReasonsFacade getEjbRejectionsFacade() {
        return ejbRejectionsFacade;
    }

    @Override
    public AcquistionDevicesFacade getEjbAcquisitionDevicesFacade() {
        return ejbAcquisitionDevicesFacade;
    }
}
