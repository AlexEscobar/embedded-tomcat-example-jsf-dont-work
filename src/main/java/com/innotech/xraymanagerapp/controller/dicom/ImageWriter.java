/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.business.ViewerRESTfulBusinessController;
import com.innotech.xraymanagerapp.controller.directory.DirectoryManager;
import com.innotech.xraymanagerapp.controller.export.ImageFormatConverter;
import com.innotech.xraymanagerapp.model.Images;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Alexander Escobar
 */
public class ImageWriter {

    /**
     * Stores a base64 image on the file system
     *
     * @param base64Image Image file on base64 format
     * @param fileName The name of the file
     * @param destinationFolder The folder where the image is going to be stored
     * @param fileFormat could be .jpg or .bmp depending on the original file
     * @return
     */
    public static boolean writeImage(String base64Image, String fileName, String destinationFolder, String fileFormat) {
        try {
            DirectoryManager.createDirectory(destinationFolder);
            // Note preferred way of declaring an array variable
            //System.out.println(destinationFolder+"\\"+fileName+".jpg");
            byte[] data = Base64.getDecoder().decode(base64Image);
            try (OutputStream stream = new FileOutputStream(destinationFolder + "/" + fileName + fileFormat)) {

                stream.write(data);
                return true;
            }
//            saveImageToXrayFolder(getInputImage(image), imageDestinationPath);
        } catch (IOException ex) {
            Logger.getLogger(ImageWriter.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }
        return false;
    }

    public void testCreateImage() throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream("C:\\Users\\Hi\\Desktop\\imageBase64ForTesting.txt");
        String imageBase64 = IOUtils.toString(fis, "UTF-8");
        Images img = new Images();
        img.setCreationDate(new Date());
        img.setSOPInstanceUID("1.2.840.10008.5.1.4.1.1.1.3.20201020110438");
        img.setPattern("1.2.840.10008.5.1.4.1.1.1.3.20201020110438_thisisthe_name.jpg");
        img.setImageFile(imageBase64);
        img.setImagePath("BONNIE, FRANK, GREG_061318\\20201020-095918_test");
        File f = createImage(img, "D:\\XrayImages\\", ".bmp");
    }

    public static File createImage(Images image, String xrayDestinationPath, String fileFormat) {
        String fullImagePath = new StringBuilder(xrayDestinationPath).append(image.getImagePath()).append("/").toString();
        String imageName = image.getPattern().replace(".jpg", "");
        return createImage(image, fullImagePath, imageName, fileFormat);
    }

    public static File createImage(Images image, String fullImagePath, String imageName, String fileFormat) {
        File file = new File(fullImagePath + "/" + imageName + fileFormat);
        if (writeImage(image.getImageFile(), imageName, fullImagePath, fileFormat)) {// saves the bmp
            file = ImageFormatConverter.convertAndGet(file, fullImagePath, "jpg", "", imageName);// converts the bmp to jpg
        }
        return file;
    }

    public static void main(String args[]) {
        try {
            new ImageWriter().testCreateImage();
        } catch (IOException ex) {
            Logger.getLogger(ViewerRESTfulBusinessController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
