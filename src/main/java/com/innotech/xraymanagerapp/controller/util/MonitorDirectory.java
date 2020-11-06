/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import com.innotech.xraymanagerapp.business.AnnotationsDentalXrayController;
import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.model.Images;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Alexander Escobar Luna
 */
public class MonitorDirectory implements Serializable {

    @Asynchronous
    public static void monitorXrayImageSourcehhPath(String path) throws IOException,
            InterruptedException {
        Path faxFolder = Paths.get(path);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
//        watchService.
        boolean valid = true;
        List<Annotations> annList = getItemsToShowFromAnnotations();
        int counter = 0;
        try {

            while (true) {
                WatchKey watchKey = watchService.take();

                for (WatchEvent event : watchKey.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                        String fileName = event.context().toString();
                        System.out.println("File Created:" + fileName);
                        //from here sent the new file to a file list
                        // get annotationsToShow from annotationsDentalXrayController to add this image file to an annotation
                        if (annList.size() > counter) {
                            // get the current annotation 
                            Annotations current = annList.get(counter);
                            if (current.getImagesList() == null) {
                                current.setImagesList(new ArrayList());
//                                addImageToCurrentAnnotation(current, fileName);
                            } else {
//                                addImageToCurrentAnnotation(current, fileName);
                            }
                            // increment counter only in the call of the next annotation because the current one already has its images
                        }
                    }
                }
                valid = watchKey.reset();
                if (!valid) {
                    break;
                }

            }
        } catch (java.lang.IllegalStateException e) {
            System.out.println("blooooooooo");
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, null, e);
        }

    }

   
    public static List<Annotations> getItemsToShowFromAnnotations() {
        AnnotationsDentalXrayController ac = (AnnotationsDentalXrayController) JsfUtil.getViewScopedBean("annotationsDentalXrayController");
        if (ac != null) {
            return ac.getItemsToShow();
        }
        return new ArrayList();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        MonitorDirectory.monitorXrayImageSourcePath("D:\\XrayImages/");
           new MonitorDirectory().cplusplusTest();
    }
    
    public native void cplusplusTest();
      
}
