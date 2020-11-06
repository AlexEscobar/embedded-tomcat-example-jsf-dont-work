/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

/**
 *
 * @author Hi
 */
import com.innotech.xraymanagerapp.business.AnnotationsDentalXrayController;
import com.innotech.xraymanagerapp.dto.AnnotationsFacade;
import com.innotech.xraymanagerapp.controller.export.ImageFormatConverter;
import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.model.Images;
import io.methvin.watcher.DirectoryWatcher;
import io.methvin.watcher.hashing.FileHasher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import static java.nio.file.StandardWatchEventKinds.*;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

/**
 *
 * @author Alexander Escobar Luna
 */
@Named("sensorFileRunnerBean")
@ViewScoped
public class SensorFileRunner extends Observable implements Runnable, Serializable {

    private static AnnotationsFacade ejbFacade;
    private volatile Thread thread;
    private WatchService watchService;
    private FacesContext fc;
    private static Integer userId;
    private static final String THREAD_NAME = "ezSensor_fileListener";
    private boolean isTifFormat;
    // Informs to xray view the image acquisition status: 0 = standby, 1 = image acquisition started, 2 = converting, 3 = finished, to accurately show the imageProcessing progress (progressBar)
    private Integer imageAcquisitionStatus;
    private boolean imageDone;// Only true when a image has been correctly processed
    private static final String JPG = "jpg";
    private static final String PNG = "png";
    private static final String BMP = "bmp";
    private static final String TIF = "tif";
    private static final String RAW = "raw";
    private static final String TXT = "txt";
    private int counter = 0;
    private long fileModificationInstance = 0L;

    private Path directoryToWatch;
    private DirectoryWatcher watcher;

    public SensorFileRunner() {
//        imageAcquisitionStatus = 0;
        System.out.println("on the default sensor runner constructor");
    }

    public SensorFileRunner(FacesContext fc, Integer userId, AnnotationsFacade ejbFacade) {
        try {
            System.out.println("on the overloaded sensor runner constructor");
            imageAcquisitionStatus = 0;
            SensorFileRunner.ejbFacade = ejbFacade;
            SensorFileRunner.userId = userId;
            this.fc = fc;
            JsfUtil.setShareableObject(this, "sensorBean");// add this bean to the session map in order to the other beans can access it
            watchService = FileSystems.getDefault().newWatchService();
            SensorImageController.initializePathVariables();
            directoryToWatch = Paths.get(SensorImageController.getDentalSensorSourceFilePath());
            Paths.get(SensorImageController.getDentalSensorSourceFilePath()).register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(SensorFileRunner.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * When this function is invoked, the observer in the class
 AnnotationsDentalXrayController will get notified of imageAcquisitionStatus
 variable status change
     */
    private void notifyIMIObservers() {
        setChanged();
        notifyObservers(imageAcquisitionStatus);
    }

    /**
     * Start a worker thread to listen for directory changes.
     */
    public void startThread() {
        thread = new Thread(this);
        thread.setName(THREAD_NAME);
        thread.start();
    }

    /**
     * Flag worker thread to stop gracefully.
     */
    public void stopThread() {
        if (thread != null) {
            Thread runningThread = thread;
            thread = null;
            runningThread.interrupt();
        }
    }

    long firstTimeStampSeconds = System.currentTimeMillis();
    long secondTimeStampSeconds = System.currentTimeMillis();// STARTS WITH FIVE MORE SECONDS FOR THE FIRST ITERATION

    @Override
    public void run() {

        System.out.println("staring the thread.... run()...");
//        getImageFromSensorOutputFile();
        getImageFromSensorWatchService();
        System.out.println("finishing the thread.... run()...");

    }

    /**
     * Check if the watch service is running properly by sending a notification
     * to the annotations controller.The watch service is triggered when a
 change on the watchChecker.txt file is detected, this change is performed
 by the annotations controller on x-ray button click. This way the system
 validates that the watch service is running and an x-ray can be taken
 without the risk that the image won't be grabbed
     *
     * @param fileName
     */
    public void checkWatchService(String fileName) {
        if (fileName.endsWith(TXT)) {
            imageAcquisitionStatus = 50;
            notifyIMIObservers();
        }
    }

    public void getImageFromSensorWatchService() {
        while (true) {
            try {
                WatchKey watchKey = null;
                watchKey = watchService.take();
//                System.out.println("acquisition status on the watch service... " + imageAcquisitionStatus + " - counter: " + counter);
                imageAcquisitionStatus = 0;
                if (watchKey != null) {
                    for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                        if (watchEvent.kind() == ENTRY_CREATE ) {
                            String fileNameOldWatcher = watchEvent.context().toString();
                            System.out.println("Modified.....................................old watcher: "+watchEvent.kind()+" - "+counter + " - "+fileNameOldWatcher);
                            if (counter++ == 0) {
                                System.out.println("Modified process OLD WATCHer.....................................Counter... " + counter + " - "+fileNameOldWatcher);
                                imageAcquisitionStatus = 30;// 30 = new image created
                                notifyIMIObservers();
                                //process();
                            }
                            break;
                        } if (watchEvent.kind() == ENTRY_MODIFY ) {
                            String fileNameOldWatcher = watchEvent.context().toString();
                            checkWatchService(fileNameOldWatcher);
                        }else{
                            counter = 0;
                            break;                            
                        }
                    }
                    if (!watchKey.reset()) {
                        System.out.println("watcher has not been reseted");
                        break;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SensorFileRunner.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
            }
        }
    }

    public void getImageFromSensorOutputFile() {
//        System.out.println("staring the thread.... run()...");
        while (true) {

            try {
                this.watcher = DirectoryWatcher.builder()
                        .path(directoryToWatch) // or use paths(directoriesToWatch)
                        .listener(event -> {
                                 String  fileNameNewWatcher = event.path().getFileName().toString();
                            switch (event.eventType()) {
                                case CREATE:
                                    System.out.println("On creation .....................................new watcher on create: "+fileNameNewWatcher);
                                    if (counter++ == 0 && fileNameNewWatcher.equals("image.bmp")) {
                                        System.out.println("Modified process.....................................Counter... " + counter + "  -  "+fileNameNewWatcher);
                                        imageAcquisitionStatus = 30;// 30 = new image created
                                        notifyIMIObservers();
//                                        process();
                                    }
                                    break;
                                case MODIFY:
                                    checkWatchService(fileNameNewWatcher);
                                    System.out.println("Modified.....................................new watcher: "+ fileNameNewWatcher);
                                    break;
                                case DELETE:
                                    System.out.println("Deleted.....................................new watcher: "+fileNameNewWatcher);
                                    counter = 0;
                                    break;
                            }
                        })
                        //.fileHasher(FileHasher.LAST_MODIFIED_TIME)
                        //.fileHashing(false) // defaults to true
                        // .logger(logger) // defaults to LoggerFactory.getLogger(DirectoryWatcher.class)
                        //                        .watchService(watchService) // defaults based on OS to either JVM WatchService or the JNA macOS WatchService
                        .build();
                watcher.watch();

            } catch (IOException ex) {
                try {
                    watcher.close();
                } catch (IOException ex1) {
                    Logger.getLogger(SensorFileRunner.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(SensorFileRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
//            System.out.println("finishing the thread.... run()...");
        }
    }

    public List<Annotations> getItemsToShowFromAnnotations() {
        try {
            Map<String, Object> viewMap = fc.getViewRoot().getViewMap();
            Object viewScopedBean = viewMap.get("annotationsDentalXrayController");
            AnnotationsDentalXrayController ac = (AnnotationsDentalXrayController) viewScopedBean;
            if (ac != null) {
                return ac.getItemsToShow();
            }
        } catch (Exception e) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        return new ArrayList();
    }

    public void persistNewImage(Annotations annotation) {
        try {
            ejbFacade.edit(annotation);
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

    private boolean convertImgToPng(File sourceImage, String tempPath, String format, String imageText, String fileName) {
//        System.out.println("Manual debug..." + sourceImage.getAbsolutePath() + " - " + tempPath + " - " + format + " - " + fileName);
        return ImageFormatConverter.convertTiffToPng(sourceImage, format, tempPath, fileName);
    }

    public synchronized Integer getImageAcquisitionStatus() {
        return imageAcquisitionStatus;
    }

    public synchronized void setImageAcquisitionStatus(Integer imageAcquisitionStatus) {
        this.imageAcquisitionStatus = imageAcquisitionStatus;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
