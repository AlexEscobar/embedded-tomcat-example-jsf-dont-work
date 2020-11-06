/* Copyright (c) 2001-2019, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */
package com.pixelmed.dicom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.imageio.ImageIO;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.ImageReader;
//import javax.imageio.ImageReadParam;
//import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import java.util.Iterator;
import java.util.Locale;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import com.pixelmed.utils.StringUtilities;
import com.pixelmed.utils.XPathQuery;

import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

/**
 * <p>
 * A class for converting RGB consumer image format input files (anything JIIO
 * can recognize) into DICOM images of a specified SOP Class, or single or multi
 * frame DICOM Secondary Capture images.</p>
 *
 * @author	dclunie
 */
public class ImageToDicom {

    private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicom/ImageToDicom.java,v 1.24 2019/02/24 14:16:44 dclunie Exp $";

    private static final Logger slf4jlogger = LoggerFactory.getLogger(ImageToDicom.class);
    
    private static short rowsCalculated;
    
    private static short columnsCalculated;

    // the following should work but does not return text values for nodes, which seem to be added as values of nodes ... is the JIIO metadata tree in some way incorrectly formed ? :(
    //private static String dumpTree(Node tree) {
    //	java.io.StringWriter out = new java.io.StringWriter();
    //	try {
    //		javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(tree);
    //		javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(out);
    //		javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
    //		java.util.Properties outputProperties = new java.util.Properties();
    //		outputProperties.setProperty(javax.xml.transform.OutputKeys.METHOD,"xml");
    //		outputProperties.setProperty(javax.xml.transform.OutputKeys.INDENT,"yes");
    //		outputProperties.setProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION,"yes");
    //		outputProperties.setProperty(javax.xml.transform.OutputKeys.ENCODING,"UTF-8");	// the default anyway
    //		transformer.setOutputProperties(outputProperties);
    //		transformer.transform(source, result);
    //	}
    //	catch (Exception e) {
    //		slf4jlogger.error("",e);
    //	}
    //	return out.toString();
    //}
    private static String dumpTree(Node node, int indent) {
        StringBuffer str = new StringBuffer();

        //for (int i=0; i<indent; ++i) str.append("    ");
        //short nodeType = node.getNodeType();
        //str.append("NodeType = "+Integer.toString(nodeType)+"\n");
        String elementName = node.getNodeName();
        for (int i = 0; i < indent; ++i) {
            str.append("    ");
        }
        str.append("<");
        str.append(elementName);
        if (node.hasAttributes()) {
            NamedNodeMap attrs = node.getAttributes();
            for (int j = 0; j < attrs.getLength(); ++j) {
                Node attr = attrs.item(j);
                if (attr != null) {
                    str.append(" ");
                    str.append(attr.getNodeName());
                    str.append("=\"");
                    str.append(attr.getNodeValue());
                    str.append("\"");
                }
            }
        }
        str.append(">");

        String nodeValue = node.getNodeValue();			// element nodes should not have values, per the Jaavdoc of org.w3c.dom.Node, yet in JPEG metadata, this is where the text is :(
        if (nodeValue != null) {
            str.append(nodeValue);
        }

        str.append("\n");

        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            str.append(dumpTree(child, indent + 1));
        }

        for (int i = 0; i < indent; ++i) {
            str.append("    ");
        }
        str.append("</");
        str.append(elementName);
        str.append(">\n");
        return str.toString();
    }

    private static String dumpTree(Node node) {
        return dumpTree(node, 0);
    }

    protected static String getCompressionType(Node metadata) {
        String compressionType = null;
        try {
            // the following should work but returns nothing ... is the JIIO metadata tree in some way incorrectly formed ? :(
            //compressionType = XPathFactory.newInstance().newXPath().evaluate("/javax_imageio_1.0/Compression/CompressionTypeName/@value",metadata);
            compressionType = XPathQuery.getNamedAttributeValueOfElementNode((Node) (XPathFactory.newInstance().newXPath().evaluate("//CompressionTypeName", metadata, XPathConstants.NODE)), "value");
        } catch (javax.xml.xpath.XPathExpressionException e) {
            slf4jlogger.error("", e);
        }
        return compressionType;
    }

    protected static short getBitsPerSample(Node metadata) {
        short bitsPerSample = 0;
        try {
            //String bitsPerSampleString = XPathFactory.newInstance().newXPath().evaluate("/javax_imageio_1.0//Data/BitsPerSample/@value",metadata);
            String bitsPerSampleString = XPathQuery.getNamedAttributeValueOfElementNode((Node) (XPathFactory.newInstance().newXPath().evaluate("//BitsPerSample", metadata, XPathConstants.NODE)), "value");
            if (bitsPerSampleString != null && bitsPerSampleString.length() > 0) {
                bitsPerSample = (short) (Integer.parseInt(bitsPerSampleString));
            }
        } catch (NumberFormatException e) {
            slf4jlogger.error("", e);
        } catch (javax.xml.xpath.XPathExpressionException e) {
            slf4jlogger.error("", e);
        }
        return bitsPerSample;
    }

    /**
     * <p>
     * Read a consumer image format input file (anything JIIO can recognize),
     * and create a single frame DICOM Image Pixel Module.</p>
     *
     * @param	inputFile	a consumer format image file (e.g., 8 or &gt; 8 bit
     * JPEG, JPEG 2000, GIF, etc.)
     * @param	list	an existing (possibly empty) attribute list, if null, a new
     * one will be created; may already include "better" image pixel module
     * attributes to use
     * @return	attribute list with Image Pixel Module (including Pixel Data)
     * added
     * @throws	IOException	if an I/O error occurs
     * @throws	DicomException	if error in DICOM encoding
     */
    public static AttributeList generateDICOMPixelModuleFromConsumerImageFile(String inputFile, AttributeList list) throws IOException, DicomException {
        return generateDICOMPixelModuleFromConsumerImageFile(new File(inputFile), list);
    }

    /**
     * <p>
     * Read a consumer image format input file (anything JIIO can recognize),
     * and create a single frame DICOM Image Pixel Module.</p>
     *
     * @param	inputFile	a consumer format image file (e.g., 8 or &gt; 8 bit
     * JPEG, JPEG 2000, GIF, etc.)
     * @param	list	an existing (possibly empty) attribute list, if null, a new
     * one will be created; may already include "better" image pixel module
     * attributes to use
     * @return	attribute list with Image Pixel Module (including Pixel Data)
     * added
     * @throws	IOException	if an I/O error occurs
     * @throws	DicomException	if error in DICOM encoding
     */
    public static AttributeList generateDICOMPixelModuleFromConsumerImageFile(File inputFile, AttributeList list) throws IOException, DicomException {
        int numberOfFrames = 0;
        BufferedImage src = null;
        Node metadataTree = null;
        ImageReader reader = null;
        FileImageInputStream fiis = new FileImageInputStream(inputFile);
        Iterator readers = ImageIO.getImageReaders(fiis);
        if (readers.hasNext()) {
            reader = (ImageReader) readers.next();	// assume 1st supplied reader is the "best" one to use :(
        }
        if (reader != null) {
            slf4jlogger.info("generateDICOMPixelModuleFromConsumerImageFile(): Using reader {} {} {}", reader.getOriginatingProvider().getDescription(Locale.US), reader.getOriginatingProvider().getVendorName(), reader.getOriginatingProvider().getVersion());
            reader.setInput(fiis);
            try {
                numberOfFrames = reader.getNumImages(true/*allowSearch*/);
            } catch (Exception e) {	// IOException or IllegalStateException
                numberOfFrames = 1;
            }
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): numberOfFrames = "+numberOfFrames);

            //ImageTypeSpecifier wantImageTypeSpecifier = null;
            //{
            //	Iterator<ImageTypeSpecifier> i = reader.getImageTypes(0);	// for the first (or only) frame
            //	while (i.hasNext()) {
            //		ImageTypeSpecifier its = i.next();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): ImageTypeSpecifier = "+its);
            //		ColorModel cm = its.getColorModel();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): \tColorModel = "+cm);
            //		ColorSpace cs = cm.getColorSpace();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): \tColorSpace = "+cs);
            //		if (cs != null && cs instanceof ICC_ColorSpace) {
            //			ICC_Profile profile = ((ICC_ColorSpace)cs).getProfile();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): \tICC_Profile = "+profile);
            //			int profileClass = profile.getProfileClass();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): \tICC_Profile class = "+profileClass);
            //			if (profileClass == ICC_Profile.CLASS_INPUT || profileClass == ICC_Profile.CLASS_COLORSPACECONVERSION) {	// i.e., not CLASS_DISPLAY
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): \tUsing this one as wanted");
            //				wantImageTypeSpecifier = its;
            //				break;
            //			}
            //		}
            //	}
            //}
            //ImageReadParam param = new ImageReadParam();
            //if (wantImageTypeSpecifier != null) {
            //	param.setDestinationType(wantImageTypeSpecifier);
            //}
            //src = reader.read(0,param);						// start with first (or only) frame
            src = reader.read(0);								// start with first (or only) frame
            IIOMetadata metadata = reader.getImageMetadata(0);
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): metadata = "+metadata);
            if (metadata != null) {
                String[] formatNames = metadata.getMetadataFormatNames();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): formatNames = "+StringUtilities.toString(formatNames));
                if (formatNames != null) {
                    for (String formatName : formatNames) {
                        if (formatName != null) {
                            if (formatName.equals("javax_imageio_1.0")) {
                                metadataTree = metadata.getAsTree(formatName);
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): "+formatName+" tree = "+dumpTree(metadataTree));
                            } else {
                                Node otherMetadataTree = metadata.getAsTree(formatName);
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): "+formatName+" tree = "+dumpTree(otherMetadataTree));
                            }
                        }
                    }
                }
            }
            try {
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): Calling dispose() on reader");
                reader.dispose();
            } catch (Exception e) {
                slf4jlogger.error("", e);
            }
        }
        if (src == null) {
            throw new DicomException("Unrecognized image file type");
        }
//com.pixelmed.display.BufferedImageUtilities.describeImage(src,System.err);
        int srcWidth = src.getWidth();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcWidth = "+srcWidth);
        int srcHeight = src.getHeight();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcHeight = "+srcHeight);

        SampleModel srcSampleModel = src.getSampleModel();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcSampleModel = "+srcSampleModel);
        int srcDataType = srcSampleModel.getDataType();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcDataType = "+srcDataType);
        Raster srcRaster = src.getRaster();
        DataBuffer srcDataBuffer = srcRaster.getDataBuffer();
        int srcNumBands = srcRaster.getNumBands();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcNumBands = "+srcNumBands);
        int srcPixels[] = null; // to disambiguate SampleModel.getPixels() method signature
        srcPixels = srcSampleModel.getPixels(0, 0, srcWidth, srcHeight, srcPixels, srcDataBuffer);
        int srcPixelsLength = srcPixels.length;
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcPixelsLength = "+srcPixelsLength);
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcWidth*srcHeight*srcNumBands = "+srcWidth*srcHeight*srcNumBands);

        byte[] iccProfileData = null;
        //{
        //	ColorModel cm = src.getColorModel();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): ColorModel = "+cm);
        //	if (cm != null) {
        //		ColorSpace cs = cm.getColorSpace();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): ColorSpace = "+cs);
        //		if (cs != null && cs instanceof ICC_ColorSpace) {
        //			ICC_Profile profile = ((ICC_ColorSpace)cs).getProfile();
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): ICC_Profile = "+profile);
        //			if (profile != null) {
        //				iccProfileData = profile.getData();
        //				if (iccProfileData != null && iccProfileData.length >= 16) {
        //					// change whatever profile class is to "scnr"
        //					iccProfileData[12] = (char)('s');
        //					iccProfileData[13] = (char)('c');
        //					iccProfileData[14] = (char)('n');
        //					iccProfileData[15] = (char)('r');
        //				}
        //				{
        //					FileOutputStream fos = new FileOutputStream("crap.icc");
        //					fos.write(iccProfileData);
        //					fos.close();
        //				}
        //			}
        //		}
        //	}
        //}

        short rows = (short) srcHeight;
        short columns = (short) srcWidth;

        Attribute pixelData = null;
        short bitsAllocated = 0;
        short bitsStored = 0;
        short highBit = 0;
        short samplesPerPixel = (short) srcNumBands;
        short pixelRepresentation = 0;
        String photometricInterpretation = srcNumBands == 3 ? "RGB" : (srcNumBands == 1 ? "MONOCHROME2" : "");		// have no way to detect MONOCHROME1 :(
        short planarConfiguration = 0;	// by pixel

        if (srcDataBuffer instanceof DataBufferByte) {
            int dstPixelsLength = srcWidth * srcHeight * srcNumBands * numberOfFrames;
            byte dstPixels[] = new byte[dstPixelsLength];
            int dstIndex = 0;
            int frame = 0;
            boolean moreFrames = true;
            while (moreFrames) {
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): copying 8 bit pixel data frame = "+frame);
                for (int srcIndex = 0; srcIndex < srcPixelsLength;) {
                    dstPixels[dstIndex++] = (byte) (srcPixels[srcIndex++]);
                }
                if (++frame < numberOfFrames) {
                    src = reader.read(frame);
                    // assume same srcWidth,srcHeight, etc. as first frame
                    srcPixels = null; // to disambiguate SampleModel.getPixels() method signature
                    srcPixels = src.getSampleModel().getPixels(0, 0, srcWidth, srcHeight, srcPixels, src.getRaster().getDataBuffer());
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcPixels.length = "+srcPixels.length);
                } else {
                    moreFrames = false;
                }
            }
            pixelData = new OtherByteAttribute(TagFromName.PixelData);
            pixelData.setValues(dstPixels);

            // do not bother to check metadata - assume always 8 bits :(
            bitsAllocated = 8;
            bitsStored = 8;
            highBit = 7;
        } else if (srcDataBuffer instanceof DataBufferShort || srcDataBuffer instanceof DataBufferUShort) {
            int dstPixelsLength = srcWidth * srcHeight * srcNumBands * numberOfFrames;
            short dstPixels[] = new short[dstPixelsLength];
            int dstIndex = 0;
            int frame = 0;
            boolean moreFrames = true;
            while (moreFrames) {
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): copying 16 bit pixel data frame = "+frame);
                for (int srcIndex = 0; srcIndex < srcPixelsLength;) {
                    dstPixels[dstIndex++] = (short) (srcPixels[srcIndex++]);
                }
                if (++frame < numberOfFrames) {
                    src = reader.read(frame);
                    // assume same srcWidth,srcHeight, etc. as first frame
                    srcPixels = null; // to disambiguate SampleModel.getPixels() method signature
                    srcPixels = src.getSampleModel().getPixels(0, 0, srcWidth, srcHeight, srcPixels, src.getRaster().getDataBuffer());
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): srcPixels.length = "+srcPixels.length);
                } else {
                    moreFrames = false;
                }
            }
            pixelData = new OtherWordAttribute(TagFromName.PixelData);
            pixelData.setValues(dstPixels);

            short bitsPerSample = getBitsPerSample(metadataTree);
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): bitsPerSample = "+bitsPerSample);
            if (bitsPerSample == 0) {	// e.g., not present in JPEG images :(
                String compressionType = getCompressionType(metadataTree);
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): compressionType = "+compressionType);
                if (compressionType != null && compressionType.equals("JPEG")) {
                    bitsPerSample = 12;
//System.err.println("ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile(): JPEG, so setting bitsPerSample = "+bitsPerSample);
                }
            }

            bitsAllocated = (short) (((bitsPerSample - 1) / 8 + 1) * 8);
            bitsStored = bitsPerSample;
            highBit = (short) (bitsPerSample - 1);
            pixelRepresentation = (short) (srcDataBuffer instanceof java.awt.image.DataBufferShort ? 1 : 0);		// hmmm ... assumes JIIO codec distinguishes signed vs. unsigned in this manner :(
        } else {
            throw new DicomException("Unsupported pixel data form (" + srcNumBands + " bands)");
        }

        if (list == null) {
            list = new AttributeList();
        }
        if (pixelData != null) {
            list.put(pixelData);

            {
                int existingBitsStored = Attribute.getSingleIntegerValueOrDefault(list, TagFromName.BitsStored, -1);
                // only add it if not already present ... externally specified value is better than JIIO decoder
                if (existingBitsStored == -1) {
                    {
                        Attribute a = new UnsignedShortAttribute(TagFromName.BitsStored);
                        a.addValue(bitsStored);
                        list.put(a);
                    }
                }
                int existingHighBit = Attribute.getSingleIntegerValueOrDefault(list, TagFromName.HighBit, -1);
                if (existingHighBit == -1) {
                    if (existingBitsStored != -1) {
                        highBit = (short) (existingBitsStored - 1);		// override assumed high bit with one less than externally specified BitsStored
                    }
                    {
                        Attribute a = new UnsignedShortAttribute(TagFromName.HighBit);
                        a.addValue(highBit);
                        list.put(a);
                    }
                }
            }

            {
                int existingPixelRepresentation = Attribute.getSingleIntegerValueOrDefault(list, TagFromName.PixelRepresentation, -1);
                // only add it if not already present ... externally specified value is better than JIIO decoder
                if (existingPixelRepresentation == -1) {
                    {
                        Attribute a = new UnsignedShortAttribute(TagFromName.PixelRepresentation);
                        a.addValue(pixelRepresentation);
                        list.put(a);
                    }
                }
            }

            {
                String existingPhotometricInterpretation = Attribute.getSingleStringValueOrNull(list, TagFromName.PhotometricInterpretation);
                // only add it if not already present ... externally specified value is better than JIIO decoder
                if (existingPhotometricInterpretation == null) {
                    {
                        Attribute a = new CodeStringAttribute(TagFromName.PhotometricInterpretation);
                        a.addValue(photometricInterpretation);
                        list.put(a);
                    }
                }
            }

            {
                list.remove(TagFromName.BitsAllocated);
                Attribute a = new UnsignedShortAttribute(TagFromName.BitsAllocated);
                a.addValue(bitsAllocated);
                list.put(a);
            }
            {
                list.remove(TagFromName.Rows);
                Attribute a = new UnsignedShortAttribute(TagFromName.Rows);
                a.addValue(rows);
                list.put(a);
            }
            {
                list.remove(TagFromName.Columns);
                Attribute a = new UnsignedShortAttribute(TagFromName.Columns);
                a.addValue(columns);
                list.put(a);
            }

            list.remove(TagFromName.NumberOfFrames);
            if (numberOfFrames > 1) {
                Attribute a = new IntegerStringAttribute(TagFromName.NumberOfFrames);
                a.addValue(numberOfFrames);
                list.put(a);
            }

            {
                list.remove(TagFromName.SamplesPerPixel);
                Attribute a = new UnsignedShortAttribute(TagFromName.SamplesPerPixel);
                a.addValue(samplesPerPixel);
                list.put(a);
            }

            list.remove(TagFromName.PlanarConfiguration);
            if (samplesPerPixel > 1) {
                Attribute a = new UnsignedShortAttribute(TagFromName.PlanarConfiguration);
                a.addValue(planarConfiguration);
                list.put(a);
            }

            if (iccProfileData != null) {
                Attribute a = new OtherByteAttribute(TagFromName.ICCProfile);
                a.setValues(iccProfileData);	// will be padded to even length on write if necessary
                list.put(a);
            }
        }
        
        rowsCalculated = rows;
        columnsCalculated = columns;
        
        return list;
    }

    /**
     * <p>
     * Read a consumer image format input file (anything JIIO can recognize),
     * and create a single frame DICOM Image Pixel Module.</p>
     *
     * @param	inputFile	a consumer format image file (e.g., 8 or &gt; 8 bit
     * JPEG, JPEG 2000, GIF, etc.)
     * @return	a new attribute list with Image Pixel Module (including Pixel
     * Data) added
     * @throws	IOException	if an I/O error occurs
     * @throws	DicomException	if error in DICOM encoding
     */
    public static AttributeList generateDICOMPixelModuleFromConsumerImageFile(String inputFile) throws IOException, DicomException {
        return generateDICOMPixelModuleFromConsumerImageFile(inputFile, null);
    }

    /**
     * <p>
     * Read a consumer image format input file (anything JIIO can recognize),
     * and create a single or multi frame DICOM Secondary Capture image.</p>
     *
     * @param	inputFile	consumer image format input file
     * @param	outputFile	DICOM output image
     * @param	patientName	patient name
     * @param	patientID	patient ID
     * @param	studyID	study ID
     * @param	seriesNumber	series number
     * @param	instanceNumber	instance number
     * @throws	IOException	if an I/O error occurs
     * @throws	DicomException	if error in DICOM encoding
     */
    public ImageToDicom(String inputFile, String outputFile, String patientName, String patientID, String studyID, String seriesNumber, String instanceNumber)
            throws IOException, DicomException {
        this(inputFile, outputFile, patientName, patientID, studyID, seriesNumber, instanceNumber, null, null);
    }

    /**
     * <p>
     * Read a consumer image format input file (anything JIIO can recognize),
     * and create a DICOM image of the specified SOP Class, or a single or multi
     * frame DICOM Secondary Capture image.</p>
     *
     * @param	inputFile	consumer image format input file
     * @param	outputFile	DICOM output image
     * @param	patientName	patient name
     * @param	patientID	patient ID
     * @param	studyID	study ID
     * @param	seriesNumber	series number
     * @param	instanceNumber	instance number
     * @param	modality	may be null
     * @param	sopClass	may be null
     * @throws	IOException	if an I/O error occurs
     * @throws	DicomException	if error in DICOM encoding
     */
    public ImageToDicom(String inputFile, String outputFile, String patientName, String patientID, String studyID, String seriesNumber,
            String instanceNumber, String modality, String sopClass)
            throws IOException, DicomException {

    }

    public ImageToDicom() {
    }

    public AttributeList addMainAttributes(String studyID, String seriesNumber, String instanceNumber, String modality, String sopClass,
            String manufacturer, String institutionName, String SOPInstanceUID, String pixelSpacing, AttributeList list, String imageType1, String imageType2) throws DicomException {

        // various Type 1 and Type 2 attributes for mandatory SC modules ...
        UIDGenerator u = new UIDGenerator();
        {
            Attribute a = new LongStringAttribute(TagFromName.Manufacturer);
            a.addValue(manufacturer);
            list.put(a);
        }
        {
            Attribute a = new LongStringAttribute(TagFromName.InstitutionName);
            a.addValue(institutionName);
            list.put(a);
        }
        {
            Attribute a = new CodeStringAttribute(TagFromName.PatientOrientation);
            list.put(a);
        }
        {
            Attribute a = new CodeStringAttribute(TagFromName.Laterality);
            list.put(a);
        }
        {
            Attribute a = new CodeStringAttribute(TagFromName.BurnedInAnnotation);
            a.addValue("YES");
            list.put(a);
        }
        {
            Attribute a = new CodeStringAttribute(TagFromName.ImageType);
            a.addValue(imageType1);
            a.addValue(imageType2);
            list.put(a);
        }

        {
            Attribute a = new UniqueIdentifierAttribute(TagFromName.InstanceCreatorUID);
            a.addValue(VersionAndConstants.instanceCreatorUID);
            list.put(a);
        }
        {
            Attribute a = new UniqueIdentifierAttribute(TagFromName.SOPInstanceUID);
            if (SOPInstanceUID == null) {
                a.addValue(u.getNewSOPInstanceUID(studyID, seriesNumber, instanceNumber));
            } else {
                a.addValue(SOPInstanceUID);
            }
            list.put(a);
        }

        {
            Attribute a = new UniqueIdentifierAttribute(TagFromName.SOPClassUID);
            a.addValue(sopClass);
            list.put(a);
        }

        if (SOPClass.isSecondaryCaptureImageStorage(sopClass)) {
            {
                Attribute a = new CodeStringAttribute(TagFromName.ConversionType);
                a.addValue("WSD");
                list.put(a);
            }
        }
        {
            Attribute a = new UniqueIdentifierAttribute(TagFromName.InstanceCreatorUID);
            a.addValue(VersionAndConstants.instanceCreatorUID);
            list.put(a);
        }
        {
            Attribute a = new DecimalStringAttribute(TagFromName.PixelSpacing);
            a.addValue(pixelSpacing);
            list.put(a);
        }
        int numberOfFrames = Attribute.getSingleIntegerValueOrDefault(list, TagFromName.NumberOfFrames, 1);
        int samplesPerPixel = Attribute.getSingleIntegerValueOrDefault(list, TagFromName.SamplesPerPixel, 1);
        
        
        
        if (sopClass == null) {
            // if modality were not null, could actually attempt to guess SOP Class based on modality here :(
            sopClass = SOPClass.SecondaryCaptureImageStorage;
            if (numberOfFrames > 1) {
                if (samplesPerPixel == 1) {
                    int bitsAllocated = Attribute.getSingleIntegerValueOrDefault(list, TagFromName.BitsAllocated, 1);
                    if (bitsAllocated == 8) {
                        sopClass = SOPClass.MultiframeGrayscaleByteSecondaryCaptureImageStorage;
                    } else if (bitsAllocated == 16) {
                        sopClass = SOPClass.MultiframeGrayscaleWordSecondaryCaptureImageStorage;
                    }
                } else if (samplesPerPixel == 3) {
                    sopClass = SOPClass.MultiframeTrueColorSecondaryCaptureImageStorage;
                }
                // no current mechanism in generateDICOMPixelModuleFromConsumerImageFile() for creating MultiframeSingleBitSecondaryCaptureImageStorage, only 8 or 16
            }
        }

        if (numberOfFrames > 1) {
            {
                AttributeTagAttribute a = new AttributeTagAttribute(TagFromName.FrameIncrementPointer);
                a.addValue(TagFromName.PageNumberVector);
                list.put(a);
            }
            {
                Attribute a = new IntegerStringAttribute(TagFromName.PageNumberVector);
                for (int page = 1; page <= numberOfFrames; ++page) {
                    a.addValue(page);
                }
                list.put(a);
            }
        }

        if (SOPClass.isMultiframeSecondaryCaptureImageStorage(sopClass)) {
            if (samplesPerPixel == 1) {
                {
                    Attribute a = new CodeStringAttribute(TagFromName.PresentationLUTShape);
                    a.addValue("IDENTITY");
                    list.put(a);
                }
                {
                    Attribute a = new DecimalStringAttribute(TagFromName.RescaleSlope);
                    a.addValue("1");
                    list.put(a);
                }
                {
                    Attribute a = new DecimalStringAttribute(TagFromName.RescaleIntercept);
                    a.addValue("0");
                    list.put(a);
                }
                {
                    Attribute a = new LongStringAttribute(TagFromName.RescaleType);
                    a.addValue("US");
                    list.put(a);
                }
            }
        }
        if (modality == null) {
            // could actually attempt to guess modality based on SOP Class here :(
            modality = "DX";
        }
        {
            Attribute a = new CodeStringAttribute(TagFromName.Modality);
            a.addValue(modality);
            list.put(a);
        }

        return list;
    }

    public AttributeList addPatientAttributes(String patientName, String patientSex, String patientSpecies, String patientBirthDate, String patientAge, String patientID, String accessionNumber, String ownerName, AttributeList list) throws DicomException {
        {
            Attribute a = new PersonNameAttribute(TagFromName.PatientName);
            a.addValue(patientName);
            list.put(a);
        }
        {
            Attribute a = new LongStringAttribute(TagFromName.PatientID);
            a.addValue(patientID);
            list.put(a);
        }

        if (accessionNumber != null) {
            Attribute a = new ShortStringAttribute(TagFromName.AccessionNumber);
            a.addValue(accessionNumber);
            list.put(a);
        }
        if (patientBirthDate != null) {
            {
                Attribute a = new DateAttribute(TagFromName.PatientBirthDate);
                a.addValue(patientBirthDate);
                list.put(a);
            }
            {
                Attribute a = new DateAttribute(TagFromName.PatientAge);
                a.addValue(patientAge);
                list.put(a);
            }
        }
        if (patientSex != null) {

            {
                Attribute a = new CodeStringAttribute(TagFromName.PatientSex);
                a.addValue(patientSex);
                list.put(a);
            }
        }
        if (patientSpecies != null) {

            {
                Attribute a = new CodeStringAttribute(TagFromName.PatientSpeciesDescription);
                a.addValue(patientSpecies);
                list.put(a);
            }
        }
//        {
//            Attribute a = new PersonNameAttribute(TagFromName.ReferringPhysicianName);
//            a.addValue("Dr. Frankenstein");
//            list.put(a);
//        }

        if (ownerName != null) {
            {
                Attribute a = new CodeStringAttribute(TagFromName.ResponsiblePerson);
                a.addValue(ownerName);
                list.put(a);
            }
            {
                Attribute a = new CodeStringAttribute(TagFromName.ResponsiblePersonRole);
                a.addValue("Owner");
                list.put(a);
            }
        }

        return list;
    }

    public AttributeList addStudyAttributes(String studyID, String studyUID, String studyDateTime, String studyDescription, AttributeList list) throws DicomException {

        {
            Attribute a = new UniqueIdentifierAttribute(TagFromName.StudyInstanceUID);
            a.addValue(studyUID);
            list.put(a);
        }
        {
            Attribute a = new ShortStringAttribute(TagFromName.StudyID);
            a.addValue(studyID);
            list.put(a);
        }
        if (studyDescription != null) {
            Attribute a = new CodeStringAttribute(TagFromName.StudyDescription);
            a.addValue(studyDescription);
            list.put(a);
        }
        {
            Attribute a = new DateAttribute(TagFromName.StudyDate);
            a.addValue(studyDateTime);
            list.put(a);
        }
        {
            Attribute a = new TimeAttribute(TagFromName.StudyTime);
            a.addValue(studyDateTime);
            list.put(a);
        }
        return list;
    }

    public AttributeList addSeriesAttributes(String seriesUID, String seriesDateTime, String seriesDescription, AttributeList list) throws DicomException {

        {
            Attribute a = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
            a.addValue(seriesUID);
            list.put(a);
        }
        if (seriesDescription != null) {
            Attribute a = new CodeStringAttribute(TagFromName.SeriesDescription);
            a.addValue(seriesDescription);
            list.put(a);
        }
        {
            Attribute a = new DateAttribute(TagFromName.SeriesDate);
            a.addValue(seriesDateTime);
            list.put(a);
        }
        {
            Attribute a = new TimeAttribute(TagFromName.SeriesTime);
            a.addValue(seriesDateTime);
            list.put(a);
        }

        return list;
    }

    public AttributeList addSeriesDefault(String studyID, String seriesNumber, AttributeList list) throws DicomException {
        UIDGenerator u = new UIDGenerator();
        Attribute a = new UniqueIdentifierAttribute(TagFromName.SeriesInstanceUID);
        a.addValue(u.getNewSeriesInstanceUID(studyID, seriesNumber));
        list.put(a);
        return list;
    }

    /**
     * Creates a Dicom file based on AttributeList and a given TransferSyntax
     *
     * @param list
     * @param outputFile full path of the dicom file E.g.,
     * D:\images\dicomFile.dcm
     * @param transferSyntax E.g., TransferSyntax.ExplicitVRLittleEndian
     * @throws IOException
     * @throws DicomException
     */
    public final void createDicomFromImage(AttributeList list, String outputFile, String transferSyntax, String sourceAppEntityTitle)
            throws IOException, DicomException {
        CodingSchemeIdentification.replaceCodingSchemeIdentificationSequenceWithCodingSchemesUsedInAttributeList(list);
        list.insertSuitableSpecificCharacterSetForAllStringValues();	// (001158)
        FileMetaInformation.addFileMetaInformation(list, transferSyntax, sourceAppEntityTitle);
        list.write(outputFile, transferSyntax, true, true);
    }

    public static short getRowsCalculated() {
        return rowsCalculated;
    }

    public static void setRowsCalculated(short rowsCalculated) {
        ImageToDicom.rowsCalculated = rowsCalculated;
    }

    public static short getColumnsCalculated() {
        return columnsCalculated;
    }

    public static void setColumnsCalculated(short columnsCalculated) {
        ImageToDicom.columnsCalculated = columnsCalculated;
    }
    
    

    /**
     * <p>
     * Read a consumer image format input file (anything JIIO can recognize),
     * and create an image of the specified SOP Class, or a single or multi
     * frame DICOM Secondary Capture image.</p>
     *
     * @param	arg	seven, eight or nine parameters, the inputFile, outputFile,
     * patientName, patientID, studyID, seriesNumber, instanceNumber, and
     * optionally the modality, and SOP Class
     */
    public static void main(String arg[]) {
        String modality = null;
        String sopClass = null;
        try {
            if (arg.length == 7) {
            } else if (arg.length == 8) {
                modality = arg[7];
            } else if (arg.length == 9) {
                modality = arg[7];
                sopClass = arg[8];
            } else {
                System.err.println("Error: Incorrect number of arguments");
                System.err.println("Usage: ImageToDicom inputFile outputFile patientName patientID studyID seriesNumber instanceNumber [modality [SOPClass]]");
                System.exit(1);
            }
            new ImageToDicom(arg[0], arg[1], arg[2], arg[3], arg[4], arg[5], arg[6], modality, sopClass);
        } catch (Exception e) {
            slf4jlogger.error("", e);	// use SLF4J since may be invoked from script
        }
    }
}
