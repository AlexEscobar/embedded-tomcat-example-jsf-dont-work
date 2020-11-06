package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.DicomServers;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.DicomServersFacade;
import com.innotech.xraymanagerapp.controller.dicom.StoreTool;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.SensorImageController;
import com.innotech.xraymanagerapp.model.Users;
import java.io.File;
import java.io.IOException;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.IncompatibleConnectionException;

@Named("dicomServersController")
@SessionScoped
public class DicomServersController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.DicomServersFacade ejbFacade;
    private List<DicomServers> items = null;
    private DicomServers selected;

    public DicomServersController() {
    }

    // get the logged user
    public Users getLoggedUser() {
        FacesContext cont = FacesContext.getCurrentInstance();
        return AuthenticationUtils.getLoggedUserByLogingSessionBean(cont);
    }
    public DicomServers getSelected() {
        return selected;
    }

    public void setSelected(DicomServers selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DicomServersFacade getFacade() {
        return ejbFacade;
    }

    public DicomServers prepareCreate() {
        selected = new DicomServers();
        initializeEmbeddableKey();
        return selected;
    }

    /**
     * Checks the remote PACS server connection to verify if the server is ready
     * to receive dicom files
     *
     * @return true if the server is ready, false otherwise.
     */
    public boolean checkServerReady() {

        StoreTool dct = createStoreTool();
        if (checkDicomServerConnection(dct)) {
            JsfUtil.addSuccessMessage("Connection to " + selected.getServerName() + " succeded...");
            return true;
        } else {
            JsfUtil.addErrorMessage("Connection failed...");
        }
        return false;
    }

    public boolean checkDicomServerConnection(StoreTool dct) {
        ApplicationEntity ae = dct.getAE();
        return dct.isServerReady(dct.getStoreSCU(ae), ae);
    }

    public StoreTool createStoreTool() {
        try {
            Device device;
            Connection conn;
            StoreTool dct;
            String host = selected.getHost();//"127.0.0.1";//dicomserver.uk.co
            int port = selected.getPort();//11112;// or 104
            String aeTitle = selected.getAeTitle();//"IMIDICOMSTORAGE";
//                if the file name ends with the extension .exists means this is a dicom file that was already created and weonly need to send it from the 
//                  current file path instead of the temporary path

            System.out.println("DICOM Server: " + host);
            System.out.println("DICOM Port: " + port);
            System.out.println("DICOM Server AETitle: " + aeTitle);
            device = new Device("storescu");
            String sourceAETitle = "STORESCU";
            conn = new Connection();
            device.addConnection(conn);

            dct = new StoreTool(host, port, aeTitle, new File(""), device, sourceAETitle, conn);
            return dct;
        } catch (NullPointerException ex) {
            JsfUtil.addErrorMessage(ex, ex.getMessage());
            Logger.getLogger(SensorImageController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public void create() {
        if (checkServerReady()) {
            selected.setCreationDate(new Date());
            selected.setStatus(true);
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DicomServersCreated"));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            JsfUtil.addErrorMessage("Server Connection failed...", "It is not possible to create the new server");
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DicomServersUpdated"));
    }

    public void destroy() {
        selected.setStatus(false);
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmailConfigDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<DicomServers> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public DicomServers getDicomServers(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<DicomServers> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<DicomServers> getItemsAvailableSelectOne() {
        Users user = getLoggedUser();
        if (user != null) {
            if (user.getUsername().equals("root")) {
                // if the user is root user, retrieve the whole emails
                return getFacade().findAll();
            }
        }
        return getFacade().findByStatus("DicomServers.findByStatus", "status");
    }

        @FacesConverter(forClass = DicomServers.class)
        public static class DicomServersControllerConverter implements Converter {

            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
                if (value == null || value.length() == 0) {
                    return null;
                }
                DicomServersController controller = (DicomServersController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "dicomServersController");
                return controller.getDicomServers(getKey(value));
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
                if (object instanceof DicomServers) {
                    DicomServers o = (DicomServers) object;
                    return getStringKey(o.getServerId());
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), DicomServers.class.getName()});
                    return null;
                }
            }

        }

    }
