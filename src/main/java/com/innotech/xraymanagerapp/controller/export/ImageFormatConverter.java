/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.export;

import ij.IJ;
import ij.ImagePlus;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import ij.io.Opener;
import ij.process.ImageConverter;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander Escobar Luna
 */
public class ImageFormatConverter implements Serializable {

    private static final IMIFileOpener opener = new IMIFileOpener();

    public static boolean exportImage(File sourceImage, String tempPath, String format, String imageText, String fileName, int sizeReductionPercentage) {
        fileName = fileName.replace(" ", "_").replace(":", "");
        return convert(sourceImage, tempPath, format, imageText, fileName, false, sizeReductionPercentage);
    }

    /**
     * Converts an image, saves it on the file system in a different format.
     * e.g. .png to .jpg, reads the converted image from the file system and
     * return it
     *
     * @param sourceImage
     * @param tempPath
     * @param format
     * @param imageText
     * @param fileName
     * @param sizeReductionPercentage The reduction percentage for the .jpg image form 1 to 99
     * @return
     */
    public static File convertFile(File sourceImage, String tempPath, String format, String imageText, String fileName, int sizeReductionPercentage) {
        fileName = fileName.replace(" ", "_").replace(":", "");
        // convert and save the file with a given text and format
        convert(sourceImage, tempPath, format, imageText, fileName, false, sizeReductionPercentage);

        // get the just converted file.
        return getConvertedFile(tempPath, fileName + "." + format);
    }

    public static File getConvertedFile(String path, String fileName) {
        return new File(path, fileName);
    }

    /**
     * Converts an image in a given format e.g.jpg, bmp tiff and png, and saves
 it.
     *
     * @param sourceImage
     * @param tempPath
     * @param format
     * @param imageText
     * @param fileName
     * @param isFromSensor true if the image comes directly from the sensor
     * @param sizeReductionPercentage The reduction percentage for the .jpg image form 1 to 99
     * @return
     */
    public static boolean convert(File sourceImage, String tempPath, String format, String imageText, String fileName, boolean isFromSensor, int sizeReductionPercentage) {
        // get the image from the file located in the OS
        System.out.println("convert(sourceImage, tempPath, format, imageText, fileName, isFromSensor): " + sourceImage.toString() + " - " + tempPath + " - " + format + " - " + imageText + " - " + fileName + " - " + isFromSensor);
        ImagePlus imp = opener.openUsingImageIO(sourceImage);
        if (Objects.isNull(imp)) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageFormatConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            imp = opener.openUsingImageIO(sourceImage);
        }
        if (Objects.nonNull(imp)) {

            System.out.println("convert() IMP: " + imp);
            System.out.println("convert() IMP title: " + imp.getTitle());
            BufferedImage bi = imp.getBufferedImage();
//        imp.s
            StringBuilder imageName = new StringBuilder(tempPath).append(fileName);
            // adds text to the image inside ImagePlus object
            if (!isFromSensor) {
                setTextToImage(imageText, bi.getGraphics());
            } else {
                bi = getScaleImage(bi, sizeReductionPercentage);
            }
//        else {
//            IJ.saveAs(imp, format, imageName.append(".").append(format).toString());
//            return true;
//        }
            // saves the image in the given format (bm, jpeg. png, tiff...)
            if (format.contains("tif")) {
                return IJ.saveAsTiff(imp, imageName.append(".").append(format).toString());
            } else {
                try {
//                    IJ.saveAs(imp, format, imageName.append(".").append(format).toString());
                    javax.imageio.ImageIO.write(bi, format, new File(imageName.append(".").append(format).toString()));
//                    bi = getScaleImage(bi, 25);
//                    javax.imageio.ImageIO.write(bi, format, new File(imageName.append("_thumbnail.").append(format).toString()));
                    return true;
                } catch (Exception ex) {
                    Logger.getLogger(ImageFormatConverter.class.getName()).log(Level.SEVERE, null, ex.getMessage());
                    return false;
                }
            }
        }
        return false;
    }

    public static BufferedImage getScaleImage(BufferedImage img, int reductionPercentage) {
        int w = img.getWidth();
        int h = img.getHeight();
        int newW = (w * reductionPercentage) / 100;// reduce 40% of the width of the image
        int newH = (h * reductionPercentage) / 100;// reduce 40% of the hight of the image
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_FAST);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * Converts an image in a given format. e.g. .png to .jpg and returns it
     *
     * @param sourceImage
     * @param tempPath
     * @param format
     * @param imageText
     * @param fileName
     * @return
     */
    public static File convertAndGet(File sourceImage, String tempPath, String format, String imageText, String fileName) {
        try {
            System.out.println("Creating the JPEG before the dicom creation: TempPath:: " + tempPath + " - Source image::" + sourceImage.getCanonicalPath());
            ImagePlus imp = opener.openUsingImageIO(sourceImage);
            BufferedImage image = imp.getBufferedImage();
//            image = scale(image, 200, 275);
            // In memory temp file
            File newImage = new File(tempPath == null ? "" : tempPath + fileName + "." + format);

            // adds text to the image inside ImagePlus object
            setTextToImage(imageText, image.getGraphics());

            javax.imageio.ImageIO.write(image, format, newImage);
//            BufferedImage convertedImage = ImageIO.read(
//                    new ByteArrayInputStream(baos.toByteArray()));
            return newImage;
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(ImageFormatConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static BufferedImage getBufferedImage(ImagePlus imp) {
        return imp.getBufferedImage();
    }

    public static ImagePlus openSavedImage(String sourceImage, String tempPath) {
        return opener.openImage(tempPath, sourceImage);
    }

    public static void setTextToImage(String text, Graphics g) {
        String[] tagsArray = text.split("\n");
        g.setFont(g.getFont().deriveFont(20f));
        int x = 25;
        for (int i = 0; i < tagsArray.length; i++) {
            g.drawString(tagsArray[i].trim(), 10, x + (i * 25));
        }
        g.dispose();
    }

    public static boolean convertTiffToPng(File image, String format, String destinationPath, String imageName) {
        try {
//        ImagePlus imp = opener.openUsingImageIO(image);
            System.out.println("Manual debug in ImageFormatConverter.convertTiffToPng() function 1 ..." + image.getAbsolutePath() + " - " + destinationPath + "-" + format + "-" + imageName);
            ImagePlus imp = new Opener().openImage(image.getAbsolutePath());
            System.out.println("before image converter");
//        Converting 16 bit image to 8bit image by using imageJ lib
            new ImageConverter(imp).convertToGray8();
            System.out.println("after image converter");
            BufferedImage bi = imp.getBufferedImage();
            System.out.println("Manual debug in ImageFormatConverter.convertTiffToPng() function 2 ...");
            StringBuilder imageFullName = new StringBuilder(destinationPath).append("\\").append(imageName);

            System.out.println("Manual debug, imageFullName + format..." + imageFullName + "." + format);
            javax.imageio.ImageIO.write(bi, format, new File(imageFullName.append(".").append(format).toString()));
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ImageFormatConverter.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            return false;
        }

    }

    public static String getFileNameWithoutExtension(String nameExtension) {
        return nameExtension.substring(0, nameExtension.indexOf("."));
    }

    public static void createBMPFile() {
        try {
            //Create file for the source
            File input = new File("D:\\XrayImages\\10PID_cat104TID_345AID_885686026155628764.png");

//Read the file to a BufferedImage
            BufferedImage image = javax.imageio.ImageIO.read(input);

//Create a file for the output
            BufferedImage bi = javax.imageio.ImageIO.read(input);
            File outputfile = new File("D:\\EmailTemp/saved.bmp");
            javax.imageio.ImageIO.write(bi, "BMP", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFormatConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BufferedImage scale(BufferedImage img, int targetWidth, int targetHeight) {

        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int w = img.getWidth();
        int h = img.getHeight();

        int prevW = w;
        int prevH = h;

        do {
            if (w > targetWidth) {
                w /= 2;
                w = (w < targetWidth) ? targetWidth : w;
            }

            if (h > targetHeight) {
                h /= 2;
                h = (h < targetHeight) ? targetHeight : h;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);

            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);

        if (g2 != null) {
            g2.dispose();
        }

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;

    }

    public static void main(String[] args) {
        File input = new File("C:\\HDRSensor\\FastEight.tif");
        convertTiffToPng(input, "png", "C:\\HDRSensor\\output\\", "TestImageddd");
//        convert(input, "C:\\HDRSensor\\output\\", "png", "", "testResultImage",true);
//        ImageFormatConverter.convert(input, "D:\\EmailTemp\\", "bmp", "Hola Alex \n como esta \n gracias nos vemos", input.getName(), false);
//        convertTiffToPng(input);
//        String xrayDestinationPath = "D:\\XrayImages";
//        String tempPath = "D:\\EmailTemp";
//        List<String> images = new ArrayList();
//        List<File> imagesAsFile = new ArrayList();
//        List<File> convertedImages = new ArrayList();
//        images.add("10PID_cat104TID_345AID_885686026155628764.png");
//        images.add("6PID_cat206TID_312AID_7218041681588822151.png");
//        images.add("6PID_cat204TID_311AID_3473384879805171424.png");
//        images.add("4PID_cat206TID_290AID_6421536291995377784.png");
//        images.add("4PID_cat206TID_290AID_1774503044475820482.png");
//        images.forEach((image) -> {
//            imagesAsFile.add(new File(xrayDestinationPath, image));
//        });
//
//        imagesAsFile.forEach((file) -> {
//            convertedImages.add(ImageFormatConverter.convertFile(file, tempPath, "jpeg", "The patient name \n and the age \n and the tooth name"));
//        });

//        TSLEmailSender.sendBasicEMail("smtp.googlemail.com", 465, "alexander.escobar@correounivalle.edu.co",
//                "alexeslu2809", "iMiEmailTest@gmail.com", "tech@fmsmeds.com", "This is the subject ", "The message is a text that is inside...");
//        TSLEmailSender.sendTSLEmail("smtp.gmail.com", 465, "tech@fmsmeds.com",
//                "FMS(0)tech", "iMiEmailTest@gmail.com", "alexander.escobar@correounivalle.edu.co", "Test3", "The message is a text that is inside...", convertedImages);
    }

}
