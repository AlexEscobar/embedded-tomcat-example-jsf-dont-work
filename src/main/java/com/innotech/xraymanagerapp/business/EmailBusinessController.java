/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.EmailConfigController;
import com.innotech.xraymanagerapp.controller.email.TSLEmailSender;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.dto.EmailConfigFacade;
import com.innotech.xraymanagerapp.model.EmailConfig;
import com.innotech.xraymanagerapp.model.Users;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class EmailBusinessController implements Serializable {
    
    @EJB
    private com.innotech.xraymanagerapp.dto.EmailConfigFacade ejbFacade;
    private List<EmailConfig> items = null;
    private EmailConfig selected;

    public EmailBusinessController() {
    }

    // get the logged user
    public Users getLoggedUser() {
        FacesContext cont = FacesContext.getCurrentInstance();
        return AuthenticationUtils.getLoggedUserByLogingSessionBean(cont);
    }

    public EmailConfig getSelected() {
        return selected;
    }

    public void setSelected(EmailConfig selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EmailConfigFacade getFacade() {
        return ejbFacade;
    }

    public EmailConfig prepareCreate() {
        selected = new EmailConfig();
        selected.setEmailSubject("Requested x-ray images for @PATIENT_NAME");
        String emailDefaultMessage = "<p>Hello,"
                + "</p><p><br></p><p>Attached please find the requested x-rays for"
                + "<strong> @PATIENT_NAME</strong>. "
                + "Taken on <strong>@STUDY_DATE.</strong></p>"
                + "<p>Complete medical records to follow.</p><p><br>"
                + "</p><p>@MESSAGE</p><p><br></p><p>Regards,</p><p><br>"
                + "</p><p><strong>@CLINIC_NAME</strong></p>"
                + "<p>@CLINIC_ADDRESS</p>"
                + "<p>@CLINIC_PHONE</p><p><br></p>";
        selected.setEmailMessage(emailDefaultMessage);
        initializeEmbeddableKey();
        return selected;
    }

    public boolean checkEmailConfiguration() {
        if (TSLEmailSender.checkEmailConnection(selected.getHostName(), selected.getSmtpPort(), selected.getEmailUser(), selected.getEmailPassword())) {
            JsfUtil.addSuccessMessage("Email configuration succeeded...");
            return true;
        }
        JsfUtil.addErrorMessage("Error in Email configuration");
        return false;
    }

    public void create() {
        if (checkEmailConfiguration()) {
            selected.setStatus(true);
            selected.setCreationDate(new Date());
            persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EmailConfigCreated"));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        }
    }

    public void update() {
        persist(JsfUtil.PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmailConfigUpdated"));
    }

    public void destroy() {
        selected.setStatus(false);
        persist(JsfUtil.PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmailConfigDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<EmailConfig> getItems() {
        if (items == null) {
            Users user = AuthenticationUtils.getLoggedUser();
            if (user != null) {
                if (user.getUsername().equals("root")) {
                    items = getFacade().findAll();
                } else {
                    items = getFacade().findEmailByClinicId(user.getClinicId().getId());
                }
            }
        }
        return items;
    }

    private void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
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

    public EmailConfig getEmailConfig(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<EmailConfig> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<EmailConfig> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<EmailConfig> getItemsAvailableByUserOrClinic() {

        try {
            Users user = getLoggedUser();
            if (user != null) {
                if (user.getUsername().equals("root")) {
                    // if the user is root user, retrieve the whole emails
                    return getFacade().findAll();
                } else {
                    return getFacade().findEmailByUserId(user.getId(), user.getClinicId().getId());
                }
            }
        } catch (NullPointerException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @FacesConverter(forClass = EmailConfig.class)
    public static class EmailConfigControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmailConfigController controller = (EmailConfigController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "emailConfigController");
            return controller.getEmailConfig(getKey(value));
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
            if (object instanceof EmailConfig) {
                EmailConfig o = (EmailConfig) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), EmailConfig.class.getName()});
                return null;
            }
        }

    }

}
