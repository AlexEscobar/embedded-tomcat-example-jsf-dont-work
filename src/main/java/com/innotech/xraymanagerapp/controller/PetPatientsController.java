package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.business.StudiesBusinessControllerImpl;
import com.innotech.xraymanagerapp.model.PetPatients;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.PetPatientsFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.model.Persons;
import com.innotech.xraymanagerapp.model.Species;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.WorkList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

public class PetPatientsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.PetPatientsFacade ejbFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.StudiesFacade ejbStudiesFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.WorkListFacade ejbWorklistFacade;
    @EJB
    private StudiesBusinessControllerImpl studiesController;
    private List<PetPatients> items = null;
    private List<PetPatients> itemsByOwner = null;
    private PetPatients selected;
    private PetPatients selected2;
    private Species specieId;
    private List<Integer> thicknessRange;
    private Studies currentStudy;
    private boolean confirm;
    private boolean enableCreateStudy;
    private boolean showItemsByOwner;// defines if the list of patients that is under the owner list on the worklist page is going to be shown or no

    // true if the next study is a dental or general. Used on the worklist - patient creation form by the add new study button
    // This variable is use for the system to know if is redirecting to the general or dental x-ray page.
    private boolean isNextDental;

    public PetPatientsController() {
    }

    public List<PetPatients> completeText(String query) {
        //System.out.println("The query inside the general search bar: " + query);
        String[] parameters = new String[3];
        StringTokenizer token = new StringTokenizer(query);

        try {
            int counter = 0;
            while (token.hasMoreElements()) {
                if (counter < 3) {
                    parameters[counter++] = token.nextToken();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }

        Integer clinicId = AuthenticationUtils.getLoggedUser().getClinicId().getId();
        if (parameters[0] != null) {
            itemsByOwner = getFacade().findByDifferentCriteria(clinicId, parameters[0], parameters[1], parameters[2]);
        }

        return itemsByOwner;
    }

    public PetPatients getSelected() {
        return selected;
    }

    public void setSelected(PetPatients selected) {
        this.selected = selected;
        showItemsByOwner = false;
    }

    private PetPatientsFacade getFacade() {
        return ejbFacade;
    }

    public PetPatients prepareCreate() {
        selected = new PetPatients();
        setNullItemsByOwner();
        return selected;
    }

    public void prepareCreateOnWorklistLoad() {
        selected = new PetPatients();
        setNullItemsByOwner();
        showItemsByOwner = false;
    }

    public PetPatients prepareCreateOnOwnerChange() {
        try {
//            if (selected != null) {
            Persons selectedOwner = selected.getOwnerId();
            selected = new PetPatients();
            selected.setOwnerId(selectedOwner);
//            }
        } catch (NullPointerException e) {
        }
        return selected;
    }

    public String createAndRedirectToXrayPage() {
        if (create()) {
            return JsfUtil.validateCalibrationFilesAndRedirectToXRayPage(isNextDental);
        }
        return "";
    }

    public boolean create() {
        boolean ok = false;
        selected.setUserId(AuthenticationUtils.getLoggedUser());
        selected.setEntryDate(new Date());
        selected.setStatus(true);
        if (Objects.isNull(selected.getAccessionCode())) {
            selected.setAccessionCode(generateAccessionNumber());
        }
        if (Objects.isNull(selected.getPatientId())) {
            selected.setPatientId(generatePatientId());
        }
        ok = persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PetPatientsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            addToWorkList(false);
            items = null;    // Invalidate list of items to trigger re-query.
        } else {
            return false;
        }
        return ok;
    }

    public String addAndRedirectToDentalXrayPage() {
        isNextDental = true;
        return addAndRedirectToXrayPage(false);
    }

    public String addAndRedirectToGeneralXrayPage() {
        isNextDental = false;
        return addAndRedirectToXrayPage(false);
    }

    public String addAndRedirectToDentalXrayPageFromStudyList() {
        isNextDental = true;
        return addAndRedirectToXrayPage(true);
    }

    public String addAndRedirectToGeneralXrayPageFromStudyList() {
        isNextDental = false;
        return addAndRedirectToXrayPage(true);
    }

    public String createAndRedirectToDentalXrayPage() {
        isNextDental = true;
        return createAndRedirectToXrayPage();
    }

    public String createAndRedirectToGeneralXrayPage() {
        isNextDental = false;
        return createAndRedirectToXrayPage();
    }

    public String addAndRedirectToXrayPage(boolean isFromStudyList) {
        addToWorkList(isFromStudyList);
        return JsfUtil.validateCalibrationFilesAndRedirectToXRayPage(isNextDental);
    }

    public void addToWorkList(boolean isFromStudyList) {
        if (isFromStudyList) {
            // if we are callig this function from the study list, then we get the patient of the selected study
            StudiesController study = getSelectedStudy();
            if (selected.getId() == null) {
                if (study != null) {
                    if (study.getSelected() != null) {
                        selected = study.getSelected().getPatientId();
                    }
                }
            }
        }

        if (selected.getId() != null) {
            currentStudy = null;
            currentStudy = getTodaysStudy();
//            is still null mean there is not today's study for this patient
            if (currentStudy == null) {
                currentStudy = createStudy();
            }
            setSelectedStudy();
        }
    }

    public StudiesController getSelectedStudy() {
        StudiesController petC = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
        return petC;
    }

    public Studies createStudy() {
        Studies study = new Studies();
        study.setCreationDate(new Date());
        study.setStatus(true);
        study.setDescription("Auto create study on patient creation");
        study.setIsClosed(false);
        study.setUserId(AuthenticationUtils.getLoggedUser());
        study.setPatientId(selected);

        study = studiesController.createOnPatientCreation(study);
        createWorklist(study);
        // if is a doctor profile redirects to x-ray page
        // on x-ray page get the current patient, last open study with today's creation date
        //redirects to annotation page
        return study;
    }

    public void createWorklist(Studies study) {
        WorkList workList = new WorkList();
        workList.setCreationDate(new Date());
        workList.setCurrentProcess((short) 1);//1 = waiting
        workList.setStatus(true);
        workList.setPatientId(selected);
        if (study != null) {
            workList.setStudyId(study.getId());
        }
        workList.setUserId(AuthenticationUtils.getLoggedUser());

        WorkListController workListController = new WorkListController();
        workList = workListController.createOnPatientCreation(workList, ejbWorklistFacade);
        getWorklistList(workList);
    }

    public void getWorklistList(WorkList workListSelected) {
        WorkListController workListController = getWorkListController();
        if (workListController != null) {
            workListController.setItemsNull();
            workListController.setSelected(workListSelected);
        }
    }

    private WorkListController getWorkListController() {
        return (WorkListController) JsfUtil.getSessionBean("workListController");
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PetPatientsUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("PetPatientsDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("PetPatientsUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            itemsByOwner = null;
        }
    }

    public LoginController getLoginController() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginController controller = (LoginController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginController");
        return controller;
    }

    public List<PetPatients> getItems() {
        try {
            if (items == null) {
                LoginController lc = getLoginController();
                if (lc != null) {
                    if (lc.getSelected() != null) {
                        if (lc.getSelected().getUsername().equals("root")) {
                            items = getFacade().findAll();
                        } else {
                            items = getFacade().findByStatusAndClinic("PetPatients.findByStatusAndClient", "status", lc.getSelected().getClinicId().getId());
                        }
                    }

                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return items;
    }

    public void initializeItemsOnOwnerSelected() {
        prepareCreateOnOwnerChange();
        setNullItemsByOwner();
    }

    public void setNullItemsByOwner() {
        itemsByOwner = null;
        currentStudy = null;
        showItemsByOwner = true;
    }

    public void onPetPatientListChange() {
        if (selected2 != null) {
            selected = selected2;
        } else {
            initializeItemsOnOwnerSelected();
        }
    }

    private boolean persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    selected = getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
                return true;
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex.getMessage());
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
        return false;
    }

    public String generateAccessionNumber() {
        Date today = new Date();
        String todaysDate = JsfUtil.formatDateWithPattern(today, "yyyyMMdd");
        long seed = System.nanoTime();
        Random generator = new Random(seed);
        int num = generator.nextInt() * 1000;
        return new StringBuilder(todaysDate).append(String.valueOf(num)).toString();
    }

    public String generatePatientId() {
        Date today = new Date();
        String todaysDate = JsfUtil.formatDateWithPattern(today, "yyyyMMdd");
        long seed = System.nanoTime();
        Random generator = new Random(seed);
        int num = generator.nextInt() * 500;
        return new StringBuilder(todaysDate).append(String.valueOf(num)).toString();
    }

    public PetPatients getPetPatients(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<PetPatients> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PetPatients> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public Species getSpecieId() {
        return specieId;
    }

    public void setSpecieId(Species specieId) {
        this.specieId = specieId;
    }

    public List<Integer> getThicknessRange() {
        if (thicknessRange == null) {
            thicknessRange = new ArrayList();
            for (int i = 1; i < 101; i++) {
                thicknessRange.add(i);
            }
        }
        return thicknessRange;
    }

    public List<PetPatients> getItemsByOwner() {
        if (showItemsByOwner) {
            if (itemsByOwner == null) {
                if (selected != null) {
                    if (selected.getOwnerId() != null) {
                        itemsByOwner = getFacade().findByOwnerId(selected.getOwnerId().getId());
                    }
                }
                if (itemsByOwner != null) {
                    if (itemsByOwner.size() > 0) {
                        selected2 = itemsByOwner.get(0);
                        onPetPatientListChange();

                    }
                } else {
                    prepareCreateOnWorklistLoad();
                }
            }
        }
        return itemsByOwner;
    }

    public void setItemsByOwner(List<PetPatients> itemsByOwner) {
        this.itemsByOwner = itemsByOwner;
    }

    public void setSelectedStudy() {
        StudiesController petC = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
        if (petC != null) {
            petC.setSelected(currentStudy);
        }
    }

    /**
     * return a study if there is a today's study for the current patient,
     * otherwise create one study for today to the current patient. All above on
     * patient creation or patient selected out of annotation page. If the
     * current page is annotation page, the system must ask the user if wants to
     * create a today's study for the selected patient
     *
     * @return
     */
    public Studies getCurrentStudy() {
//        confirm = true;
        try {

            String uri = JsfUtil.getPreviousUri();
//        if this action is executed from annotations page
            if (uri.contains("x-ray") || uri.contains("annotations")) {
                if (getTodaysStudy() == null) {
//                temporary line to create a study when there is not an existing one. It is temporary while confirmation popup is developed
                    setTodaysStudy();
//                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("NotStudyCreated"));
                }

            } else {// if this action is executed from another page get the active study of the current patient.

                if (getTodaysStudy() == null) {
                    WorkListController workListController = getWorkListController();
                    if (Objects.nonNull(workListController.getSelected().getStudyId())) {
                        currentStudy = ejbStudiesFacade.find(workListController.getSelected().getStudyId());
                    }
//                setTodaysStudy(false);
                }

            }
        } catch (java.lang.NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        if (currentStudy == null) {
            setTodaysStudy();
        }

        return currentStudy;
    }

    public Studies getTodaysStudy() {
        if (currentStudy == null) {
            if (selected != null) {
                if (selected.getId() != null) {
                    Date today = JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime());
                    Date tomorrow = JsfUtil.zoneDateTimeToDate(JsfUtil.getDaysAfter(1));
                    currentStudy = ejbStudiesFacade.findCurrentByOwnerId(selected.getId(), AuthenticationUtils.getLoggedUser().getId(), today, tomorrow);
                }
            }
        }

        return currentStudy;
    }

    public void setTodaysStudy() {
//      if current still null means there is not a todays study created, then the system creates one

        if (currentStudy == null) {
            if (selected != null) {
                if (selected.getId() != null) {
                    currentStudy = createStudy();//ejbStudiesFacade.setByOwnerId(selected.getId(), AuthenticationUtils.getLoggedUser().getId());
                    if (currentStudy != null) {
                        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("StudiesCreated"));
                    }
                }
            }
        }

    }

    /**
     * Searches the selected patient on the current worklist for the system to
     * decide whether enable the add study button on patient create
     *
     * @return true if the selected patient already has a today's study and is
     * on the worklist, false otherwise
     */
    public boolean isPatientOnWorklist() {
        if (selected != null) {
            WorkListController wlController = getWorkListController();
            if (wlController != null) {
                List<WorkList> worklistList = wlController.getItems();
                if (wlController.getItems() != null && !worklistList.isEmpty()) {
                    if (worklistList.stream().anyMatch((workListItem) -> (Objects.equals(workListItem.getPatientId().getId(), selected.getId())))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void setCurrentStudy(Studies currentStudy) {
        this.currentStudy = currentStudy;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public void showConfirmDialog() {
        Map<String, Object> options = new HashMap();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("/dialogs/CreateStudyConfirm", options, null);
    }

    public boolean isEnableCreateStudy() {
        enableCreateStudy = !isPatientOnWorklist();// enableCreateStudy is true if the selected patient is not in the worklist
        return enableCreateStudy;
    }

    public void setEnableCreateStudy(boolean enableCreateStudy) {
        this.enableCreateStudy = enableCreateStudy;
    }

    public boolean isShowItemsByOwner() {
        return showItemsByOwner;
    }

    public void setShowItemsByOwner(boolean showItemsByOwner) {
        this.showItemsByOwner = showItemsByOwner;
    }

    public PetPatients getSelected2() {
        return selected2;
    }

    public void setSelected2(PetPatients selected2) {
        this.selected2 = selected2;
    }

    public boolean isIsNextDental() {
        return isNextDental;
    }

    public void setIsNextDental(boolean isNextDental) {
        this.isNextDental = isNextDental;
    }

}
