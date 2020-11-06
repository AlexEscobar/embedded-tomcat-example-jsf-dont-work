package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.business.AnnotationsBodyPartsController;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Observes;
import org.primefaces.PrimeFaces;
//@ViewScoped
public class GeneralXrayController extends AnnotationsBodyPartsController implements Serializable {

    boolean outerLoop, innerLoop;

    public GeneralXrayController() {
    }

    public void initializeSensors() {
        // initializes the sensor controller depending on the current sensor defined on the configuration
        if (isIsHDRSensor()) {
            setColumnPixelSpacing("0.0192582");
            setRowPixelSpacing("0.0190678");
//            The HDR sensor should be initialize in order to interact with the registry values the sensor system sets up during its process
        } else {
            setColumnPixelSpacing("0.03016352");
            setRowPixelSpacing("0.03013699");
        }
    }

    /**
     * Returns true if the calibration files for the current sensor exist, false
     * otherwise. Checks only for the existence of calibration files if the
     * current sensor is HDR.
     *
     * @return true if the calibration files for the current sensor exist, false
     * otherwise
     */
    public boolean validateCalibrationFiles() {
        return true;
    }

    public void showCalibrationFilesSelectorModalDialog() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        System.out.println("attempt to open /annotations/UploadCalibrationFiles::: ");
        PrimeFaces.current().dialog().openDynamic("/annotations/UploadCalibrationFiles", options, null);
    }

    boolean isInImageAcquisition = false;

    /**
     * This function is called every 3 seconds for the progress bar function on
     * the annotations create.xml file
     *
     * @return
     */
    public Integer getProgressBarState() {
//        getImageAcquisitionDeviceCurrentProcessStage();
        return getProgressBarAndUpdateXrayButtons();
    }

    private Integer getProgressBarAndUpdateXrayButtons() {
        if (!isIsHDRSensor()) {// only for ez sensor in case it gets disconnected from the usb port
            updateXrayButtons();
        }
        return getProgressBar();
    }


    public void socketMessagesEventListener(@Observes FlatPanelEventListener event) {
        System.out.println("The event received from the c++ server getCurrentProcessState(): \n"+event.getCurrentProcessState());
        System.out.println("The event received from the c++ server toString(): \n"+event.toString());
    }
    
}
