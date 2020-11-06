package com.innotech.xraymanagerapp.controller;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.model.ImageAnnotationState;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.ImageAnnotationStateFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;

import java.io.Serializable;
import java.util.ArrayList;
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

@Named("imageAnnotationStateController")
@SessionScoped
public class ImageAnnotationStateController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ImageAnnotationStateFacade ejbFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ImagesFacade ejbImageFacade;
    private List<ImageAnnotationState> items = null;
    private ImageAnnotationState selected;
    private Integer imageId;
    private String jsonState;

    public ImageAnnotationStateController() {
    }

    public void setImageId() {
        System.out.println("setting the image Id: " + imageId);
        if (selected == null) {
            prepareCreate();
        }
        if (selected != null) {
            if (imageId != null) {
                selected.setImageId(ejbImageFacade.find(imageId));
                selected.setState(jsonState);
            }
        }
    }

    public void getImageState() {
        if (imageId != null) {
            selected = getFacade().findByImageId(imageId);
        }
    }

    public List<ImageAnnotationState> getImageStateListByStudyId() {
//        StudiesController studyC = (StudiesController) JsfUtil.getSessionBean("studiesController");
//        if (studyC != null) {
//            if(studyC.getSelected() != null)
                return getFacade().findByStudyId(77);
//                return getFacade().findByStudyId(studyC.getSelected().getId());
//        }
//        return new ArrayList();
    }
    
    public String convertImageStateListAsJson() {
        String jsonObject = "";// will be the json representation on the ImageAnnotationState object without the state
        String jsonStateL = "";// only the state. It is in json format already, so that it is not possible to convert it as json because will provoque a stackOverFlow exception
//        for (ImageAnnotationState item : getImageStateListByStudyId()) {            
//            jsonStateL = item.getState();
//            item.setState(null);
//            jsonObject += new Gson().toJson(item);//+jsonStateL;
//        }
        return new Gson().toJson(getImageStateListByStudyId());
    }

    /**
     * Converts the ImageAnnotationState object to json format to be sent to the javascript dicom viewer
     * @param imageId
     * @return ImageAnnotationState as json
     */
    public String getImageStateAsJson(Integer imageId) {
        this.imageId = imageId;
        System.out.println("getting the ImageAnnotationState object with this imageId: " + imageId);
        getImageState();
        if (selected != null) {
            jsonState = selected.getState();
            selected.setState("");
            return new Gson().toJson(selected);
        }
        return "";
    }

    public ImageAnnotationState getSelected() {
        return selected;
    }

    public void setSelected(ImageAnnotationState selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ImageAnnotationStateFacade getFacade() {
        return ejbFacade;
    }

    public ImageAnnotationState prepareCreate() {
        selected = new ImageAnnotationState();
        selected.setStatus(true);
        selected.setCreationDate(new Date());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        System.out.println("Creating...");
        if (selected != null) {
            if (selected.getImageId() != null) {
                selected.setUserId(AuthenticationUtils.getLoggedUser());
                System.out.println("attempt to persist");
                persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ImageAnnotationStateCreated"));
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                }
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ImageAnnotationStateUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ImageAnnotationStateDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ImageAnnotationState> getItems() {
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
                    System.out.println("persisted yeeeeeeeh...");
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

    public ImageAnnotationState getImageAnnotationState(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ImageAnnotationState> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ImageAnnotationState> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public com.innotech.xraymanagerapp.dto.ImagesFacade getEjbImageFacade() {
        return ejbImageFacade;
    }

    public void setEjbImageFacade(com.innotech.xraymanagerapp.dto.ImagesFacade ejbImageFacade) {
        this.ejbImageFacade = ejbImageFacade;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getJsonState() {
        return jsonState;
    }

    public void setJsonState(String jsonState) {
        this.jsonState = jsonState;
    }

    @FacesConverter(forClass = ImageAnnotationState.class)
    public static class ImageAnnotationStateControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ImageAnnotationStateController controller = (ImageAnnotationStateController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "imageAnnotationStateController");
            return controller.getImageAnnotationState(getKey(value));
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
            if (object instanceof ImageAnnotationState) {
                ImageAnnotationState o = (ImageAnnotationState) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ImageAnnotationState.class.getName()});
                return null;
            }
        }

    }

}
