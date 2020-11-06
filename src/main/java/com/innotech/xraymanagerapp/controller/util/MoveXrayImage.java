/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import com.innotech.xraymanagerapp.controller.directory.DirectoryManager;
import com.innotech.xraymanagerapp.controller.export.ImageFormatConverter;
import static com.innotech.xraymanagerapp.controller.util.SensorImageController.xrayDestinationPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander Escobar L.
 */
public class MoveXrayImage {

    private static boolean isTifFormat;
    // Informs to xray view the image acquisition status: 0 = standby, 1 = image acquisition started, 2 = converting, 3 = finished, to accurately show the imageProcessing progress (progressBar)
    private static final String JPEG = "jpg";
    private static final String PNG = "png";
    private static final String BMP = "bmp";
    private static final String TIF = "tif";
    private static final String RAW = "raw";
    private static final String TXT = "txt";

    public static String moveFileToXrayPath(String destinationFileName, String sourceFileName, String imagePath, long seconds, String targetFormat, boolean isPanel) {
        try {
            destinationFileName = destinationFileName + seconds;
            String imageName = new StringBuilder(destinationFileName).append(".").append(targetFormat).toString();
            File file = null;// get the raw file

            if (isPanel) {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.flatPanelSourceFilePath, sourceFileName);// get the raw file
            } else {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.dentalSensorSourceFilePath, sourceFileName);// get the raw file
            }
            try (InputStream inputStream = SensorImageController.getInputImage(file)) {
                imageName = SensorImageController.saveImageToXrayFolder(inputStream, destinationFileName, imagePath, targetFormat);
                Thread.sleep(300);
//                file.delete();
            } catch (IOException ex) {
                Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

//            file.delete();
            return imageName;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

    public static String moveFileToXrayPath(String destinationFileName, String sourceFileName, String imagePath, long seconds, String targetFormat,
            String sourcePath) {
        try {
            destinationFileName = destinationFileName + seconds;
            String imageName = new StringBuilder(destinationFileName).append(".").append(targetFormat).toString();
//            File file = SensorImageController.getImageFromSourcePath(sourceFileName);// get the raw file

            File file = new File(sourcePath, sourceFileName);
            try (InputStream inputStream = SensorImageController.getInputImage(file)) {
                imageName = SensorImageController.saveImageToXrayFolder(inputStream, destinationFileName, imagePath, targetFormat);
                Thread.sleep(300);
//                file.delete();
            } catch (IOException ex) {
                Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

//            file.delete();
            return imageName;
        } catch (InterruptedException ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

    public static String copyImageToXrayPath(String destinationFileName, String sourceFileName, String imagePath, long seconds, boolean isPanel) {
        try {
            String destinationFormat = JPEG;
            destinationFileName = destinationFileName + seconds;
            String imageName = new StringBuilder(destinationFileName).append(".").append(destinationFormat).toString();
            System.out.println("Manual debug, destinationFileName... " + destinationFileName);
            System.out.println("Manual debug, sourceFileName... " + sourceFileName);
            File file = null;// get the raw file

            if (isPanel) {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.flatPanelSourceFilePath, sourceFileName);// get the raw file
            } else {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.dentalSensorSourceFilePath, sourceFileName);// get the raw file
            }
            if (isTifFormat) {
                System.out.println("Manual debug, isTif = " + isTifFormat);
                convertImgToPng(file, SensorImageController.xrayDestinationPath, destinationFormat, "", destinationFileName);
            } else {
                System.out.println("Manual debug, isTif = " + isTifFormat);

                /*
                // old method which we have errors because the process here is create the virtual file, copy into the destination folder and delete the file
                // some times the file gets locked and the JVM could not release the file. This is a big issue  because the solution to this lock is restart the web server
                    Path sourcePath = SensorImageController.getSourceImagePath(sourceFileName);
                    imageName = SensorImageController.saveImageToXrayFolder(sourcePath, destinationFileName);
                
                 */
                try (InputStream inputStream = SensorImageController.getInputImage(file)) {
                    imageName = SensorImageController.saveImageToXrayFolder(inputStream, destinationFileName, imagePath, destinationFormat);
                    Thread.sleep(300);
                    //file.delete();
                } catch (IOException ex) {
                    Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
//            file.delete();
            return imageName;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * Converts an image in JPEG format and moves it to a given folder, with a
     * given name.If the destination folder does not exists, it creates it.
     *
     * @param destinationFileName The new file name without extension
     * @param sourceFileName source file name plus extension. E.g, image.bmp
     * @param imagePath the path where the image will be stored
     * @param seconds Last part of the image name
     * @param isPanel true if the origin of the image is a flat panel, false for
     * dental sensors
     * @param sizeReductionPercentage The reduction percentage for the .jpg
     * image form 1 to 99
     * @return
     */
    public static String convertAndCopyImageToXrayPath(String destinationFileName, String sourceFileName, String imagePath,
            long seconds, boolean isPanel, int sizeReductionPercentage) {
        try {
            String destinationFormat = JPEG;
            destinationFileName = destinationFileName + seconds;
            String imageName = new StringBuilder(destinationFileName).append(".").append(destinationFormat).toString();
            System.out.println("Manual debug, destinationFileName... " + destinationFileName);
            System.out.println("Manual debug, sourceFileName... " + sourceFileName);
            File file = null;// get the raw file

            if (isPanel) {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.flatPanelSourceFilePath, sourceFileName);// get the raw file
            } else {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.dentalSensorSourceFilePath, sourceFileName);// get the raw file
            }

            if (isTifFormat) {
//                System.out.println("Manual debug, isTif = " + isTifFormat);
                convertImgToPng(file, SensorImageController.xrayDestinationPath, destinationFormat, "", destinationFileName);
            } else {
                System.out.println("Manual debug, isTif = " + isTifFormat);
                System.out.println("Manual debug, xrayDestinationPath = " + xrayDestinationPath);
                String fullImagePath = new StringBuilder(xrayDestinationPath).append(imagePath).append("/").toString();
                System.out.println("Manual debug, xrayDestinationPath = " + xrayDestinationPath);

//                create the directory if does not exist
                DirectoryManager.createDirectory(fullImagePath);

//                convert and move the image to the x-ray folder
                ImageFormatConverter.convert(
                        file, fullImagePath, destinationFormat,
                        "",
                        destinationFileName, true, sizeReductionPercentage
                );

            }
//            file.delete();
            return imageName;
        } catch (IOException ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * Converts an image in JPEG format and moves it to a given folder, with a
     * given name.If the destination folder does not exists, it creates it.
     *
     * @param destinationFileName The new file name without extension
     * @param sourceFileName source file name plus extension. E.g, image.bmp
     * @param imagePath the path where the image will be stored
     * @param seconds Last part of the image name
     * @param isPanel true if the origin of the image is a flat panel, false for
     * dental sensors
     * @param sourcePath The device output path, this is the place where the
     * system is looking for the source x-ray image
     * @param sizeReductionPercentage The reduction percentage for the .jpg
     * image form 1 to 99
     * @return
     */
    public static boolean convertAndCopyImageToXrayPath(String destinationFileName, String sourceFileName, String imagePath,
            long seconds, boolean isPanel, String sourcePath, int sizeReductionPercentage) {
        try {
            String destinationFormat = JPEG;
            System.out.println("Manual debug, destinationFileName... " + destinationFileName);
            System.out.println("Manual debug, sourceFileName... " + sourceFileName);
            File file = null;// get the raw file

            if (isPanel) {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.flatPanelSourceFilePath, sourceFileName);// get the raw file
            } else {
                file = SensorImageController.getImageFromSourcePath(SensorImageController.dentalSensorSourceFilePath, sourceFileName);// get the raw file
            }

            if (isTifFormat) {
//                System.out.println("Manual debug, isTif = " + isTifFormat);
                convertImgToPng(file, SensorImageController.xrayDestinationPath, destinationFormat, "", destinationFileName);
            } else {
                System.out.println("Manual debug, isTif = " + isTifFormat);
                System.out.println("Manual debug, xrayDestinationPath = " + xrayDestinationPath);
                String fullImagePath = new StringBuilder(xrayDestinationPath).append(imagePath).append("/").toString();
                System.out.println("Manual debug, xrayDestinationPath = " + xrayDestinationPath);

//                create the directory if does not exist
                DirectoryManager.createDirectory(fullImagePath);

//                convert and move the image to the x-ray folder
                return ImageFormatConverter.convert(
                        file, fullImagePath, destinationFormat,
                        "",
                        destinationFileName, true, sizeReductionPercentage
                );

            }
//            file.delete();
        } catch (Exception ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Converts an image in JPEG format and moves it to a given folder, with a
     * given name.If the destination folder does not exists, it creates it.
     *
     * @param destinationFileName The new file name without extension
     * @param imagePath the path where the image will be stored
     * @param sizeReductionPercentage The reduction percentage for the .jpg
     * image form 1 to 99
     * @param file
     * @return
     */
    public static boolean convertAndCopyImageToXrayPath(String destinationFileName, String imagePath,
            int sizeReductionPercentage, File file) {
        try {
            String destinationFormat = JPEG;
            System.out.println("Manual debug, destinationFileName... " + destinationFileName);//destinationFileName=  only the file name without path
//            File file = null;

            if (isTifFormat) {
//                System.out.println("Manual debug, isTif = " + isTifFormat);
                convertImgToPng(file, SensorImageController.xrayDestinationPath, destinationFormat, "", destinationFileName);
            } else {
                System.out.println("Manual debug, isTif = " + isTifFormat);
                System.out.println("Manual debug, xrayDestinationPath = " + xrayDestinationPath);//xrayDestinationPath= D://XrayImages
                String fullImagePath = new StringBuilder(xrayDestinationPath).append(imagePath).append("/").toString();
                System.out.println("Manual debug, xrayDestinationPath = " + xrayDestinationPath);

//                create the directory if does not exist
                DirectoryManager.createDirectory(fullImagePath);

//                convert and move the image to the x-ray folder
                return ImageFormatConverter.convert(
                        file, fullImagePath, destinationFormat,
                        "",
                        destinationFileName, true, sizeReductionPercentage
                );
            }
        } catch (Exception ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    public static String moveFileToXrayPath(String destinationFileName, String sourceFileName, String imagePath, String targetFormat, File file) {
        try {
            String imageName = new StringBuilder(destinationFileName).append(".").append(targetFormat).toString();

            try (InputStream inputStream = SensorImageController.getInputImage(file)) {
                imageName = SensorImageController.saveImageToXrayFolder(inputStream, destinationFileName, imagePath, targetFormat);
                Thread.sleep(300);
//                file.delete();
            } catch (IOException ex) {
                Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

//            file.delete();
            return imageName;
        } catch (InterruptedException ex) {
            Logger.getLogger(MonitorDirectory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

    private static boolean convertImgToPng(File sourceImage, String tempPath, String format, String imageText, String fileName) {
//        System.out.println("Manual debug..." + sourceImage.getAbsolutePath() + " - " + tempPath + " - " + format + " - " + fileName);
        return ImageFormatConverter.convertTiffToPng(sourceImage, format, tempPath, fileName);
    }
}
