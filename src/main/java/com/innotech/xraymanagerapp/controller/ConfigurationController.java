package com.innotech.xraymanagerapp.controller;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.model.Configuration;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.ConfigurationFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("configurationController")
@SessionScoped
public class ConfigurationController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ConfigurationFacade ejbFacade;
    private List<Configuration> items = null;
    private static HashMap<String, String> configurationMap;
    private Configuration selected;
    private String serverIp;
    private boolean isViewer;
    private boolean isXrayServer;

    public ConfigurationController() {
        JsfUtil.setShareableObject(this, "configurationBean");// add this bean to the session map in order to other beans can access it
    }
    
    public Configuration getSelected() {
        return selected;
    }

    public void setSelected(Configuration selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ConfigurationFacade getFacade() {
        if(ejbFacade == null){
            ejbFacade = new com.innotech.xraymanagerapp.dto.ConfigurationFacade();
        }
        return ejbFacade;
    }

    public Configuration prepareCreate() {
        selected = new Configuration();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setCreationDate(new Date());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Configuration> getItems() {
        if (items == null) {
            items = getFacade().findByStatus("Configuration.findByStatus", "status");
        }
        return items;
    }

    public HashMap<String, String> getConfigurationMap() {
        if (configurationMap == null) {
            configurationMap = new HashMap();
            getItems().forEach((item) -> {
                configurationMap.put(item.getParameterName(), item.getParameterValue());
            });
        }
        return configurationMap;
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

    public Configuration getConfiguration(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Configuration> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Configuration> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public String getServerIp() {
        if (serverIp == null) {
            serverIp = new Gson().toJson(getConfigurationMap().get("ServerIp"));
        }
        System.out.println("server ip as json from ConfigurationController... " + serverIp);
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * 
     * @return true if this application is a cloud Dicom viewer
     */
    public boolean isIsViewer() {
        String isV = getConfigurationMap().get("Is_Viewer");
        isViewer = false;
        if (isV != null) {
            if (isV.toLowerCase().contains("y")) {
                isViewer = true;
            }
        }

        return isViewer;
    }

    public void setIsViewer(boolean isViewer) {
        this.isViewer = isViewer;
    }

    /**
     * 
     * @return true if this application is a local x-ray image acquisition software.
     */
    public boolean getIsXrayServer() {
        String isV = getConfigurationMap().get("Is_XrayServer");
        isXrayServer = false;
        if (isV != null) {
            if (isV.toLowerCase().contains("y")) {
                isXrayServer = true;
            }
        }
        return isXrayServer;
    }

    public void setIsXrayServer(boolean isXrayServer) {
        this.isXrayServer = isXrayServer;
    }

    public boolean isHDRSensor() {
        if (getConfigurationMap() != null) {
            String sIsHdrSensor = getConfigurationMap().get("Is_HDR_Sensor");
            if (sIsHdrSensor != null) {
                if (sIsHdrSensor.toLowerCase().equals("yes")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isGeneratorConnected(){
        if (getConfigurationMap() != null) {
            String sIsHdrSensor = getConfigurationMap().get("Is_Generator_Connected");
            if (sIsHdrSensor != null) {
                if (sIsHdrSensor.toLowerCase().equals("yes")) {
                    return true;
                }
            }
        }
        return false;
    }

    @FacesConverter(forClass = Configuration.class)
    public static class ConfigurationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConfigurationController controller = (ConfigurationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "configurationController");
            return controller.getConfiguration(getKey(value));
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
            if (object instanceof Configuration) {
                Configuration o = (Configuration) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Configuration.class.getName()});
                return null;
            }
        }
    }
}
