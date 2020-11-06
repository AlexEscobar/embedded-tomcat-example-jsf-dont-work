/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.ViewerController;
import com.innotech.xraymanagerapp.controller.dicom.ImageWriter;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.MoveXrayImage;
import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.model.ExportEmailModel;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.ImageSize;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class ViewerRESTfulBusinessController {

    @EJB
    private ConfigurationBusinessController ejbConfigurationFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.AcquistionDevicesFacade ejbAcquisitionDevicesFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ImagesFacade ejbImagesFacade;
//    @EJB
//    private com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade ejbViewDicomTagsFacade;
    @EJB
    private ViewerBusinessController vc;

    private HashMap<Integer, ExportEmailModel> imagesAsFile;

    private List<Images> imageList;

    private Studies currentStudy;

    /**
     * Writes an image on the server file system
     *
     * @param image Image object that contains the image in base64 format,
     * usually this is a bitmap (.bmp) file
     * @param fileFormat
     * @return
     */
    public File createImage(Images image, String fileFormat) {
        String xrayDestinationPath = ejbConfigurationFacade.getConfigurationMap().get(ConfigurationProperties.XRAY_DESTINATION_PATH);
        String fullImagePath = new StringBuilder(xrayDestinationPath).append(image.getImagePath()).append("/").toString();
        String imageName = image.getPattern().replace(".jpg", "").replace(".bmp", "");
        return ImageWriter.createImage(image, fullImagePath, imageName, fileFormat);
    }

    /**
     * Creates the Dicom file base on the received image
     *
     * @param newXrayImage
     * @throws IOException
     */
    public void createOtherFiles(Images newXrayImage) throws IOException {
        final Integer finalImageId = newXrayImage.getId();
        // ************************* create the missing required files (dicom, move bitmap) *****************************
        moveImagesToXrayImagesDirectory(getDicomTags(finalImageId), newXrayImage);
        // finish endpoint second call
    }

    /**
     *
     * @param destinationFileName
     * @param imagePath
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public boolean moveImageToDestinationPath(String destinationFileName, String imagePath, File file) throws IOException {
        return MoveXrayImage.convertAndCopyImageToXrayPath(destinationFileName, imagePath, 100, file);
    }

    public Images updateImage(Images image) {
        if (image != null) {
            try {
                image = ejbImagesFacade.edit(image);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, msg);
                } 
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        return image;
    }

    public Images updateRowsAndColumns(ImageSize imageSize,Images imageOld) {    
        short rowCount = imageSize.getRows();
        short columnCount = imageSize.getColumns();
        Images image = ejbImagesFacade.find(imageOld.getId());
        image.setImageRows(rowCount);
        image.setImageColumns(columnCount);
        return updateImage(image);
    }

    public synchronized ViewDicomTags getDicomTags(Integer imageId) {
        ViewDicomTags dicomTags = vc.getDicomTagsByImageId(imageId);
        return dicomTags;
    }

    public void moveImagesToXrayImagesDirectory(ViewDicomTags dicomTags, Images newXrayImage) {
//        String rawFileName = "image.raw";
        System.out.println("BEFORE START THE EXECUTOR THREAD*********************************************************************************");
        // create a thread to create the dicom file and move both the orignal bmp and raw files into the x-ray folder
        String xrayDestinationPath = ejbConfigurationFacade.getConfigurationMap().get(ConfigurationProperties.XRAY_DESTINATION_PATH);
        createDicomFile(dicomTags, xrayDestinationPath);
        updateRowsAndColumns(vc.getImageSize(), newXrayImage);
        System.out.println("EXECUTOR THREAD Finished****************************************************************************************");
    }

    public synchronized void createDicomFile(ViewDicomTags dicomTags, String xrayDestinationPath) {
        System.out.println("Creating DICOM...#################################################################################################");
        vc.createSingleDicomFile(xrayDestinationPath, dicomTags, true, false, false, 0);
        System.out.println("DICOM Finished...#################################################################################################");
    }

    ///********** retrieving the images list by study ****************************///
    public HashMap<Integer, ExportEmailModel> getBase64ImageListByStudy(Studies study) {
        setCurrentStudy(study);
        return convertImageListToBase64(study);
    }

    private HashMap<Integer, ExportEmailModel> convertImageListToBase64(Studies study) {
        imagesAsFile = new HashMap();
        try {
            int counter = 0;
            getImagesFromStudies();
            for (Images exportModel : imageList) {
                try {
                    if (counter == 0) {
                        counter++;
                    }
                    
                    ExportEmailModel eem = vc.getExportEmailModel(exportModel);
                    eem.setImageAsBase64(addXrayImageToImagesObject(exportModel));
                    imagesAsFile.put(exportModel.getId(), eem);
                } catch (NullPointerException | IOException | ParserConfigurationException | SAXException ex) {
                    Logger.getLogger(ViewerController.class
                            .getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
                }
            }
        } catch (NullPointerException e) {
            Logger.getLogger(ViewerController.class
                    .getName()).log(Level.SEVERE, e.getMessage());

        }
        return imagesAsFile;
    }

    private List<Images> getImagesFromStudies() {
        System.out.println("getting new images BEFORE SHOWING THEM... size: ");
        imageList = ejbImagesFacade.findByStudyId("Images.findByStudyId", "studyId", getCurrentStudy().getId());
        if (imageList.isEmpty()) {
            imageList = ejbImagesFacade.findByStudyId("Images.findByStudyIdAnnotation", "studyId", getCurrentStudy().getId());
        }
        return imageList;
    }

    private String addXrayImageToImagesObject(Images image) {
        // getting the image location directory
        String fullPath = new StringBuilder(ejbConfigurationFacade.getConfigurationMap().get(ConfigurationProperties.XRAY_DESTINATION_PATH))
                .append("/")
                .append(image.getImagePath())
                .append("/")
                .append(image.getPattern()).toString();
        File f = new File(fullPath);
        return JsfUtil.convertImageToBase64(f);
    }

    public Studies getCurrentStudy() {
        return currentStudy;
    }

    public void setCurrentStudy(Studies currentStudy) {
        this.currentStudy = currentStudy;
    }

}
