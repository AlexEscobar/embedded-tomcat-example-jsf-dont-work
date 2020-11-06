/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;

/**
 * Create jpeg to dcm only in transferSyntax = UID.JPEGBaseline1
 * @author Dimitri
 */
public class Jpgdcm {
    private static int  jpgLen; 
   private String transferSyntax = UID.JPEGBaseline1;
       public  Jpgdcm(File file, File fileOutput) {
	try {   
            jpgLen = (int) file.length();  
                    
            
            
	BufferedImage jpegImage = ImageIO.read(file);  
	/*
	 * We open a try block and then read our Jpeg into a BufferedImage through ImageIO.read() method. If this results in an invalid image so we may throw a new exception. 
	 * Else, we’ve got a valid image and therefore valuable information about it. 
	 * The BufferedImage class has a lot of useful methods for retrieving image data, then let’s save the number of color components (samples per pixel) of our image, the bits
	 *  per pixel and the bits allocated:

	 */
	
	  if (jpegImage == null) throw new Exception("Invalid file."); 
	 /* We open a try block and then read our Jpeg into a BufferedImage through ImageIO.read() method. 
	  * If this results in an invalid image so we may throw a new exception. 
	  * Else, we’ve got a valid image and therefore valuable information about it.
	  * The BufferedImage class has a lot of useful methods for retrieving image data, 
	  * then let’s save the number of color components (samples per pixel) of our image, 
	  * the bits per pixel and the bits allocated: */
	  
	     int colorComponents = jpegImage.getColorModel().getNumColorComponents();  
	     int bitsPerPixel = jpegImage.getColorModel().getPixelSize();  
	     int bitsAllocated = (bitsPerPixel / colorComponents);  
	     int samplesPerPixel = colorComponents; 
	 /*It’s time to start building our Dicom dataset:*/
    
	    Attributes dicom = new Attributes();  
	     dicom.setString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");  
	     dicom.setString(Tag.PhotometricInterpretation, VR.CS, samplesPerPixel == 3 ? "YBR_FULL_422" : "MONOCHROME2"); 
	     
	 /*The first line creates a new basic Dicom object defined by dcm4che2 toolkit. 
	  *The next one puts header information for Specific Character Set: ISO_IR 100 – it’s the same for ISO-8859-1 – the code for Latin alphabet.
	  *Finally, the last line puts header information for photometric interpretation (read with or without colors). 
	  *So if our image has samples per pixel equals to 3, it has colors (YBR_FULL_422), else it’s a grayscale image (MONOCHROME2).
	  The following lines add integer values to our Dicom header. Note that all of them comes from BufferedImage methods. 
	  These values are mandatory when encapsulating. For more information you can check Part 3.5 of Dicom Standard. */

	     dicom.setInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);           
	     dicom.setInt(Tag.Rows, VR.US, jpegImage.getHeight());  
	     dicom.setInt(Tag.Columns, VR.US, jpegImage.getWidth());  
	     dicom.setInt(Tag.BitsAllocated, VR.US, bitsAllocated);  
	     dicom.setInt(Tag.BitsStored, VR.US, bitsAllocated);  
	     dicom.setInt(Tag.HighBit, VR.US, bitsAllocated-1);  
	     dicom.setInt(Tag.PixelRepresentation, VR.US, 0); 
	     /*Also, our Dicom header needs information about date and time of creation:*/
	     dicom.setDate(Tag.InstanceCreationDate, VR.DA, new Date());  
	     dicom.setDate(Tag.InstanceCreationTime, VR.TM, new Date());  
	     /* Every Dicom file has a unique identifier. 
	      * Here we’re generating study, series and Sop instances UIDs. 
	      * You may want to modify these values, but you should to care about their uniqueness. 
	      */
	     dicom.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());  
	     dicom.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());  
	     dicom.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());  
	     dicom.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());
             dicom.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());   
             dicom.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
	     
             dicom.setString(Tag.PerformingPhysicianName, VR.PN, "Jean");
             dicom.setString(Tag.AdmittingDiagnosesDescription, VR.LO, "CHU");
                Sequence seq= dicom.newSequence(Tag.AnatomicRegionSequence,0); 
                    Attributes dicom2 = new Attributes();  
                    
                    dicom2.setString(Tag.CodingSchemeDesignator, VR.SH, "SRT");
                    dicom2.setString(Tag.CodeValue, VR.SH, "T-AA000");
                    dicom2.setString(Tag.CodeMeaning, VR.LO, "Eye");              
                seq.add(dicom2);
             
             
             
             
             
             /*Our Dicom header is almost done. 
	      * The following command initiates Dicom metafile information considering JPEGBaseline1 as transfer syntax.
	      * This means this file has Jpeg data encapsulated instead common medical image pixel data. 
	      * The most common Jpeg files use a subset of the Jpeg standard called baseline Jpeg. 
	      * A baseline Jpeg file contains a single image compressed with the baseline discrete cosine transformation (DCT) and Huffman encoding.
	      */
	    // dicom.initFileMetaInformation(UID.JPEGBaseline1);  
	     /*After initiate the header we can open an output stream for saving our Dicom dataset as follows:*/
	 
              Attributes fmi = new Attributes();
              
                 fmi.setString(Tag.ImplementationVersionName, VR.SH, "DCM4CHE3"); 
                  fmi.setString(Tag.ImplementationClassUID, VR.UI, UIDUtils.createUID());
                  fmi.setString(Tag.TransferSyntaxUID, VR.UI, transferSyntax);
                  fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, transferSyntax);
                  fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI,UIDUtils.createUID());
                  fmi.setString(Tag.FileMetaInformationVersion, VR.OB, "1"); 
                  fmi.setInt(Tag.FileMetaInformationGroupLength, VR.UL, dicom.size()+fmi.size());
                  
                  
             
             
             
	     DicomOutputStream dos = new DicomOutputStream(fileOutput);  
	     dos.writeDataset(fmi, dicom);  
	     dos.writeHeader(Tag.PixelData, VR.OB, -1); 
	     
	     /*
	      * The Item Tag (FFFE,E000) is followed by a 4 byte item length field encoding the explicit number of bytes of the item.
	      * The first item in the sequence of items before the encoded pixel data stream shall be a basic item with length equals to zero:
	      */
	     dos.writeHeader(Tag.Item, null, 0);
	     /*The next Item then keeps the length of our Jpeg file. */
	     /* 
	     According to Gunter from dcm4che team we have to take care that  
	     the pixel data fragment length containing the JPEG stream has  
	     an even length. 
	     */  
	       
	     dos.writeHeader(Tag.Item, null, (jpgLen+1)&~1);  
	     /* Now all we have to do is to fill this item with bytes taken from our Jpeg file*/
	     FileInputStream fis = new FileInputStream(file);  
	     BufferedInputStream bis = new BufferedInputStream(fis);  
	     DataInputStream dis = new DataInputStream(bis);  
	     
	     byte[] buffer = new byte[65536];         
	     int b;  
	     while ((b = dis.read(buffer)) > 0) {  
	         dos.write(buffer, 0, b);  
	     }  
	     /*Finally, the Dicom Standard tells that we have to put a last Tag: 
	      * a Sequence Delimiter Item (FFFE,E0DD) with length equals to zero.*/

	  
	     if ((jpgLen&1) != 0) dos.write(0);   
	       dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);  
	       dos.close();  
	   

	} catch (Exception e) {
			}

   }
     public static void main(String args[]) throws IOException {
          File jpgFile = new File("D:\\DicomSent\\2.25.190318177288460239506709736887191182730.jpg");
            File dcmFile = new File("D:\\DicomSent\\1.2.826.0.1.3680043.11.127dddddd.dcm");
            
            new Jpgdcm(jpgFile, dcmFile);
     }
    
}