package com.innotech.xraymanagerapp.controller.util;

import com.innotech.xraymanagerapp.business.AbstractAnnotationsController;
import com.innotech.xraymanagerapp.controller.ConfigurationController;
import com.innotech.xraymanagerapp.controller.XrayController;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

public class JsfUtil implements Serializable {

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    public static boolean isValidationFailed() {
        return FacesContext.getCurrentInstance().isValidationFailed();
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    public static void addErrorMessages(List<String> messages) {
        try {
            messages.forEach((message) -> {
                addErrorMessage(message);
                Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, message);
            });
        } catch (Exception e) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, e.getMessage(), e);
        }
    }

    public static void addErrorMessage(String msg) {
        try {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, msg);
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        } catch (Exception e) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, e.getMessage(), e);
        }
    }

    public static void addErrorMessage(String headerMessage, String msg) {
        try {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, msg);
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, headerMessage, msg);
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        } catch (Exception e) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, e.getMessage(), e);
        }
    }

    public static void addSuccessMessage(String msg) {
        Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, msg);
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    public static void addSuccessMessage(String msg, FacesContext facesContext) {
        Logger.getLogger(JsfUtil.class.getName()).log(Level.INFO, msg);
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        facesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }

    public static enum PersistAction {
        CREATE,
        DELETE,
        UPDATE
    }

    /**
     * Redirect the user to an url
     *
     * @param url the url to be reached
     * @throws java.io.IOException
     */
    public static void redirect(String url) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public static Object getSessionBean(String beanName) {
        Object sessionBean = getHttpSession().getAttribute(beanName);
        return sessionBean;
    }

    public static HttpSession getHttpSession() {
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        return session;
    }

    public static Object getSessionScopeBean(String beanName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, beanName);

    }

    public static Object getViewScopedBean(String beanName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext externalContext = fc.getExternalContext();
        UIViewRoot uvr = fc.getViewRoot();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        Object viewScopedBean = viewMap.get(beanName);
        return viewScopedBean;
    }

    public static void removeViewScopedBean(String beanName) {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(beanName);
    }

    public static int[] getAge(String birthDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaNac = LocalDate.parse(birthDate, fmt);
        LocalDate ahora = LocalDate.now();

        Period periodo = Period.between(fechaNac, ahora);
//        System.out.printf("Tu edad es: %s años, %s meses y %s días",
//                periodo.getYears(), periodo.getMonths(), periodo.getDays());
        int[] data = new int[3];
        data[0] = periodo.getYears();
        data[1] = periodo.getMonths();
        data[2] = periodo.getDays();
        return data;
    }

    public static String getAge(Date date, boolean isDicom) {
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String birthDate = df.format(date);
            LocalDate fechaNac = LocalDate.parse(birthDate, fmt);
            LocalDate ahora = LocalDate.now();

            Period periodo = Period.between(fechaNac, ahora);
//        System.out.printf("Tu edad es: %s años, %s meses y %s días",
//                periodo.getYears(), periodo.getMonths(), periodo.getDays());
            String age = "";
            if (periodo.getYears() > 0) {
                age += periodo.getYears() + "y.";
            }
            if (periodo.getMonths() > 0) {
                age += periodo.getMonths() + "m.";
            }
            if (periodo.getDays() > 0) {
                age += periodo.getDays() + "d.";
            }

            if (isDicom) {
                return periodo.getYears() > 0 ? periodo.getYears() + "" : "0";
            }
            return age;
        } catch (NullPointerException e) {
        }
        return null;
    }

    public static String getDaysBefore(int quantity) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(getBeforeDays(quantity));
    }

    public static Date getBeforeDays(int daysBefore) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysBefore);
        return cal.getTime();
    }

    public static ZonedDateTime getBeforeDays(long daysBefore) {
        return getTodaysLocalDate().minusDays(daysBefore);
    }

    public static ZonedDateTime getDaysAfter(long daysAfter) {
        return getTodaysLocalDate().plusDays(daysAfter);
    }

    public static ZonedDateTime getTodaysLocalDateTime() {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

    public static ZonedDateTime getTodaysLocalDate() {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

    final static ZoneId id = ZoneId.systemDefault();

    public static ZoneId getZoneId() {
        return id;
    }

    public static Date zoneDateTimeToDate(ZonedDateTime date) {
        return Date.from(date.toInstant());
    }

    public static ZonedDateTime DateToZoneDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), getZoneId());
    }

    public static String getStringDate(Date date) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String birthDate = df.format(date);
        LocalDate fechaNac = LocalDate.parse(birthDate, fmt);
        LocalDate ahora = LocalDate.now();

        Period periodo = Period.between(fechaNac, ahora);
//        System.out.printf("Tu edad es: %s años, %s meses y %s días",
//                periodo.getYears(), periodo.getMonths(), periodo.getDays());
        return ahora.toString() + " " + ahora.atStartOfDay().getHour() + ":" + ahora.atStartOfDay().getMinute();
    }

    public static String getStringDate(Date date, String format) {

        DateFormat df = new SimpleDateFormat(format);//("dd/MM/yyyy");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);//("dd/MM/yyyy");
        String formtatedDate = df.format(date);
        return formtatedDate;
    }

    /**
     * Formats a given date
     *
     * @param date
     * @param format the new date format. Eg, "dd/MM/yyyy"
     * @return
     */
    public static String formatDateWithPattern(Date date, String format) {
        if (date != null) {
            try {

                DateFormat df = new SimpleDateFormat(format);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
                return df.format(date);
            } catch (NullPointerException e) {
            }
        }
        return null;
    }
    /**
     * Formats a given date
     *
     * @param date
     * @param format the new date format. Eg, "dd/MM/yyyy"
     * @return
     */
    public static String formatDateWithPattern(String date, String format) {
        if (date != null) {
            try {

                DateFormat df = new SimpleDateFormat(format);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
                return df.format(date);
            } catch (NullPointerException e) {
            }
        }
        return null;
    }

    public static String getCurrentUri() {
        String uri = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return uri;
    }

    public static String getPreviousUri() {
        String uri = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("referer");
        return uri;
    }

    /**
     * Inserts an object in the public session map
     *
     * @param o the Object to be stored
     * @param key the key to access the object
     */
    public static void setShareableObject(Object o, String key) {
        getSessionMap().put(key, o);
    }

    /**
     * gets an object that was inserted in the public session map
     *
     * @param key the key to search for the object
     * @return
     */
    public static Object getShareableObject(String key) {
        Object bean = getSessionMap().get(key);

        return bean;
    }

    /**
     * gets the session map for current session
     *
     * @return current session map
     */
    public static Map getSessionMap() {
        FacesContext cont = FacesContext.getCurrentInstance();
        ExternalContext extCont = cont.getExternalContext();
        return extCont.getSessionMap();
    }

    public static boolean getSensorConfiguration() {
        ConfigurationController cc = (ConfigurationController) getSessionScopeBean("configurationController");
        if (cc != null) {
            if (cc.getConfigurationMap() != null) {
                String sIsHdrSensor = cc.getConfigurationMap().get("Is_HDR_Sensor");
                if (sIsHdrSensor != null) {
                    if (sIsHdrSensor.toLowerCase().equals("yes")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean getIsGeneratorConnected() {
        ConfigurationController cc = (ConfigurationController) getSessionScopeBean("configurationController");
        if (cc != null) {
            return cc.isGeneratorConnected();
        }
        return false;
    }

    /**
     * If this application is being used as an x-ray server (Local installed
     * server) returns true, If it is working as a cloud viewer it returns
     * false.
     *
     * @return true If this application is being used as an x-ray server (Local
     * installed server), false if it is working as a cloud viewer.
     */
    public static boolean isXrayLocalServer() {

        if (getConfigurationMap() != null) {
            String sIsHdrSensor = getConfigurationMap().get("Is_XrayServer");
            if (sIsHdrSensor != null) {
                if (sIsHdrSensor.toLowerCase().equals("yes")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static HashMap<String, String> configurationMap = new HashMap();

    public static HashMap<String, String> getConfigurationMap() {
        if (configurationMap.size() < 1) {
            ConfigurationController cc = (ConfigurationController) getSessionScopeBean("configurationController");
            if (cc != null) {
                System.out.println("configuration bean is not null");
                return configurationMap = cc.getConfigurationMap();
            }
            System.out.println("configuration bean IS null");
        }
        return configurationMap;

    }

    public static Date convertToDateFromString(String stringDate, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

            return formatter.parse(stringDate);
        } catch (NullPointerException | ParseException ex) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    /**
     * Deletes all files with a given extension inside a given directory
     *
     * @param directoryPath
     * @param fileExtension
     * @return true if delete process was succeeded
     */
    public static boolean cleanDirectory(String directoryPath, String fileExtension) {
        try {

            Path rootPath = Paths.get(directoryPath);
            try (Stream<Path> walk = Files.walk(rootPath)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File -> {
                            if (File.getName().endsWith(fileExtension)) {
                                File.delete();
                            }
                        });
            } catch (IOException ex) {
                Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        return true;
    }

    public static String validateCalibrationFilesAndRedirectToXRayPage(boolean isRedirectingToDental) {
        XrayController xrayController = (XrayController) getSessionScopeBean("xrayController");
        if (Objects.nonNull(xrayController)) {
            if (isRedirectingToDental) {
                return xrayController.redirectToDentalXrayView();
            } else {
                return xrayController.redirectToGeneralXrayView();
            }
        }
        return "pretty:x-ray";
    }

    public static String convertImageToBase64(File file) {
        try {
            Path folder = Paths.get(file.getAbsolutePath());
            File imageSource = folder.toFile();
            byte[] currentXrayImage = Files.readAllBytes(imageSource.toPath());
            return Base64.getEncoder().encodeToString(currentXrayImage);
        } catch (IOException ex) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println("Today: " + JsfUtil.cleanDirectory("C:\\EzSensor\\BACKUP", ".bmp"));
    }
}
