/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.DicomTags;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.BulkDataDescriptor;
import org.dcm4che3.io.ContentHandlerAdapter;
import org.dcm4che3.io.DicomEncodingOptions;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.io.DicomOutputStream;

/**
 *
 * @author Dimitri
 */
public class XmlDcm {

    private IncludeBulkData includeBulkData = IncludeBulkData.URI;
    private boolean catBlkFiles = false;
    private String blkFilePrefix = "blk";
    private String blkFileSuffix;
    private File blkDirectory;
    private Attributes blkAttrs;
    private String tsuid;
    private boolean withfmi;
    private boolean nofmi;
    private DicomEncodingOptions encOpts = DicomEncodingOptions.DEFAULT;
    private List<File> bulkDataFiles;
    private Attributes fmi;
    private Attributes dataset;
    private static byte[] buffer = new byte[8192];

    private static boolean noAPPn = false;
    private static final int BUFFER_SIZE = 8162;

    private static final int FF = 0xff;

    private static final int SOF = 0xc0;

    private static final int DHT = 0xc4;

    private static final int DAC = 0xcc;

    private static final int SOI = 0xd8;

    private static final int SOS = 0xda;

    private static final int APP = 0xe0;

    private static String charset = "ISO_IR 100";

    private static int jpgHeaderLen;

    private static int jpgLen;

    public final void setBulkDataDirectory(File blkDirectory) {
        this.blkDirectory = blkDirectory;
    }

    /**
     *
     * @param out
     * @throws IOException
     */
    public void writeTo(OutputStream out, File jpgFile) throws IOException {
        jpgHeaderLen = 0;
        jpgLen = (int) jpgFile.length();

        if (nofmi) {
            fmi = null;
        } else if (fmi == null
                ? withfmi
                : tsuid != null && !tsuid.equals(
                        fmi.getString(Tag.TransferSyntaxUID, null))) {
            fmi = dataset.createFileMetaInformation(tsuid);
        }

        DataInputStream jpgInput = new DataInputStream(new BufferedInputStream(
                new FileInputStream(jpgFile)));
        dataset.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
        dataset.setString(Tag.SpecificCharacterSet, VR.CS, charset);

        readHeader(dataset, jpgInput);

        DicomOutputStream dos = new DicomOutputStream(
                new BufferedOutputStream(out),
                fmi != null
                        ? UID.ExplicitVRLittleEndian
                        : tsuid != null
                                ? tsuid
                                : UID.ImplicitVRLittleEndian);
        dos.setEncodingOptions(encOpts);
        dos.writeDataset(fmi, dataset);
        dos.writeHeader(Tag.PixelData, VR.OB, -1);
//                if (!cl.hasOption("mpeg")) {
        dos.writeHeader(Tag.Item, null, 0);
        dos.writeHeader(Tag.Item, null, (jpgLen + 1) & ~1);
        dos.write(buffer, 0, jpgHeaderLen);
//                }
        int r;
        while ((r = jpgInput.read(buffer)) > 0) {
            dos.write(buffer, 0, r);
        }
//                if (!cl.hasOption("mpeg")) {
        if ((jpgLen & 1) != 0) {
            dos.write(0);
        }
//                }
        dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
        dos.finish();
        dos.flush();
    }

    private static Attributes readHeader(Attributes attrs, DataInputStream jpgInput)
            throws IOException {
        if (jpgInput.read() != FF || jpgInput.read() != SOI
                || jpgInput.read() != FF) {
            throw new IOException("JPEG stream does not start with FF D8 FF");
        }
        int marker = jpgInput.read();
        int segmLen;
        boolean seenSOF = false;
        buffer[0] = (byte) FF;
        buffer[1] = (byte) SOI;
        buffer[2] = (byte) FF;
        buffer[3] = (byte) marker;
        jpgHeaderLen = 4;
        while (marker != SOS) {
            segmLen = jpgInput.readUnsignedShort();
            if (buffer.length < jpgHeaderLen + segmLen + 2) {
                growBuffer(jpgHeaderLen + segmLen + 2);
            }
            buffer[jpgHeaderLen++] = (byte) (segmLen >>> 8);
            buffer[jpgHeaderLen++] = (byte) segmLen;
            jpgInput.readFully(buffer, jpgHeaderLen, segmLen - 2);
            if ((marker & 0xf0) == SOF && marker != DHT && marker != DAC) {
                seenSOF = true;
                int p = buffer[jpgHeaderLen] & 0xff;
                int y = ((buffer[jpgHeaderLen + 1] & 0xff) << 8)
                        | (buffer[jpgHeaderLen + 2] & 0xff);
                int x = ((buffer[jpgHeaderLen + 3] & 0xff) << 8)
                        | (buffer[jpgHeaderLen + 4] & 0xff);
                int nf = buffer[jpgHeaderLen + 5] & 0xff;
                attrs.setInt(Tag.SamplesPerPixel, VR.US, nf);
                if (nf == 3) {
                    attrs.setString(Tag.PhotometricInterpretation, VR.CS,
                            "YBR_FULL_422");
                    attrs.setInt(Tag.PlanarConfiguration, VR.US, 0);
                } else {
                    attrs.setString(Tag.PhotometricInterpretation, VR.CS,
                            "MONOCHROME2");
                }

                attrs.setInt(Tag.Rows, VR.US, y);
                attrs.setInt(Tag.Columns, VR.US, x);
                attrs.setInt(Tag.BitsAllocated, VR.US, p > 8 ? 16 : 8);
                attrs.setInt(Tag.BitsStored, VR.US, p);
                attrs.setInt(Tag.HighBit, VR.US, p - 1);
                attrs.setInt(Tag.PixelRepresentation, VR.US, 0);
            }
            if (noAPPn & (marker & 0xf0) == APP) {
                jpgLen -= segmLen + 2;
                jpgHeaderLen -= 4;
            } else {
                jpgHeaderLen += segmLen - 2;
            }
            if (jpgInput.read() != FF) {
                throw new IOException("Missing SOS segment in JPEG stream");
            }
            marker = jpgInput.read();
            buffer[jpgHeaderLen++] = (byte) FF;
            buffer[jpgHeaderLen++] = (byte) marker;
        }
        if (!seenSOF) {
            throw new IOException("Missing SOF segment in JPEG stream");
        }
        return attrs;
    }

    private static void growBuffer(int minSize) {
        int newSize = buffer.length << 1;
        while (newSize < minSize) {
            newSize <<= 1;
        }
        byte[] tmp = new byte[newSize];
        System.arraycopy(buffer, 0, tmp, 0, jpgHeaderLen);
        buffer = tmp;
    }

    public void delBulkDataFiles() {
        if (bulkDataFiles != null) {
            for (File f : bulkDataFiles) {
                f.delete();
            }
        }
    }

    public void parse(DicomInputStream dis) throws IOException {
        dis.setIncludeBulkData(includeBulkData);
//        if (blkAttrs != null)
//            dis.setBulkDataDescriptor(BulkDataDescriptor.valueOf(blkAttrs));
        dis.setBulkDataDirectory(blkDirectory);
        dis.setBulkDataFilePrefix(blkFilePrefix);
        dis.setBulkDataFileSuffix(blkFileSuffix);
        dis.setConcatenateBulkDataFiles(catBlkFiles);
        dataset = dis.readDataset(-1, -1);
        fmi = dis.getFileMetaInformation();
        bulkDataFiles = dis.getBulkDataFiles();
    }

    public void mergeXML(File fname) throws Exception {
        if (dataset == null) {
            dataset = new Attributes();
        }
        ContentHandlerAdapter ch = new ContentHandlerAdapter(dataset);
        parseXML(fname, ch);
        Attributes fmi2 = ch.getFileMetaInformation();
        if (fmi2 != null) {
            fmi = fmi2;
        }
    }

    public static Attributes parseXML(File fname) throws Exception {
        Attributes attrs = new Attributes();
        ContentHandlerAdapter ch = new ContentHandlerAdapter(attrs);
        parseXML(fname, ch);
        return attrs;
    }

    private static void parseXML(File fname, ContentHandlerAdapter ch)
            throws Exception {
        SAXParserFactory f = SAXParserFactory.newInstance();
        SAXParser p = f.newSAXParser();
        if (fname.equals("-")) {
            p.parse(System.in, ch);
        } else {
            p.parse(fname, ch);
        }
    }

    public static void main(String args[]) throws IOException {

        File jpgFile = new File("D:\\DicomSent\\2.25.190318177288460239506709736887191182730.jpg");
        File inputFile = new File("D:\\DicomSent\\xmlTest.xml");
        File fileOutput = new File("D:\\DicomSent\\test.dcm");

        XmlDcm dcm = new XmlDcm();
        try {
            dcm.mergeXML(inputFile);
        } catch (Exception ex) {
            Logger.getLogger(XmlDcm.class.getName()).log(Level.SEVERE, null, ex);
        }

        OutputStream out = new FileOutputStream(fileOutput);
        dcm.writeTo(out, jpgFile);

    }

}
