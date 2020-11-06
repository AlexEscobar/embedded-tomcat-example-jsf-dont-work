package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.ImagesFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("imagesController")
@SessionScoped
public class ImagesController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ImagesFacade ejbFacade;
    private List<Images> items = null;
    private List<String> imageNames = null;
    private Images selected;

    @PostConstruct
  public void init(){
    imageNames = new ArrayList();
    imageNames.add("cat.png");
    imageNames.add("dog.png");
    imageNames.add("pig.png");
    imageNames.add("duck.png");
    imageNames.add("cow.png");
    imageNames.add("sheep.png");
    imageNames.add("rabbit.png");
    imageNames.add("goat.png");
    imageNames.add("horse.png");
    imageNames.add("chicken.png");
  }
    public ImagesController() {

    }

    public Images getSelected() {
        return selected;
    }

    public void setSelected(Images selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ImagesFacade getFacade() {
        return ejbFacade;
    }

    public Images prepareCreate() {
        selected = new Images();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ImagesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ImagesUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("ImagesDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("ImagesUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Images> getItems() {
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
                    selected = getFacade().edit(selected);
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

    public Images getImages(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Images> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Images> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<String> getImageNames() {
        return imageNames;
    }

    public void setImageNames(List<String> imageNames) {
        this.imageNames = imageNames;
    }

    @FacesConverter(forClass = Images.class)
    public static class ImagesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ImagesController controller = (ImagesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "imagesController");
            return controller.getImages(getKey(value));
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
            if (object instanceof Images) {
                Images o = (Images) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Images.class.getName()});
                return null;
            }
        }

    }

}
