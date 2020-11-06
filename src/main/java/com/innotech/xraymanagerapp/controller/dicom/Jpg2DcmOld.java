/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2002-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Gunter Zeilinger <gunterze@gmail.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.model.dicom.DicomTags;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gunter zeilinger<gunterze@gmail.com>
 * @author Hesham Elbadawi <bsdreko@gmail.com>
 * @author Alexander Escobar L.
 *
 */
public class Jpg2DcmOld implements Serializable {

    static final Logger LOG = LoggerFactory.getLogger(Jpg2DcmOld.class);

    private static final String DESCRIPTION = "Encapsulate JPEG Image into DICOM Object.\nOptions:";

    private static final String EXAMPLE = "--\nExample 1: Encapulate JPEG Image verbatim with default values "
            + "for mandatory DICOM attributes into DICOM Secondary Capture Image:"
            + "\n$ jpg2dcm -c jpg2dcm.xml image.jpg image.dcm"
            + "\n--\nExample 2: Encapulate JPEG Image without application segments "
            + "and additional DICOM attributes to mandatory defaults into DICOM "
            + "Image Object:"
            + "\n$ jpg2dcm --no-appn -c patattrs.cfg homer.jpg image.dcm"
            + "\n--\nExample 3: Encapulate MPEG2 Video with specified DICOM "
            + "attributes into DICOM Video Object:"
            + "\n$ jpg2dcm --mpeg -c mpg2dcm.xml video.mpg video.dcm";

    private static final String LONG_OPT_CHARSET = "charset";

    private static final String OPT_CHARSET_DESC = "Specific Character Set code string, ISO_IR 100 by default";

    private static final String OPT_AUGMENT_CONFIG_DESC = "Specifies DICOM attributes included additional to mandatory defaults";

    private static final String OPT_REPLACE_CONFIG_DESC = "Specifies DICOM attributes included instead of mandatory defaults";

    private static final String LONG_OPT_TRANSFER_SYNTAX = "transfer-syntax";

    private static final String OPT_TRANSFER_SYNTAX_DESC = "Transfer Syntax; 1.2.840.10008.1.2.4.50 (JPEG Baseline) by default.";

    private static final String LONG_OPT_MPEG = "mpeg";

    private static final String OPT_MPEG_DESC = "Same as --transfer-syntax 1.2.840.10008.1.2.4.100 (MPEG2).";

    private static final String LONG_OPT_UID_PREFIX = "uid-prefix";

    private static final String OPT_UID_PREFIX_DESC = "Generate UIDs with given prefix, 1.2.40.0.13.1.<host-ip> by default.";

    private static final String LONG_OPT_NO_APPN = "no-appn";

    private static final String OPT_NO_APPN_DESC = "Exclude application segments APPn from JPEG stream; "
            + "encapsulate JPEG stream verbatim by default.";

    private static final String OPT_HELP_DESC = "Print this message";

    private static final String OPT_VERSION_DESC = "Print the version information and exit";

    private static final int FF = 0xff;

    private static final int SOF = 0xc0;

    private static final int DHT = 0xc4;

    private static final int DAC = 0xcc;

    private static final int SOI = 0xd8;

    private static final int SOS = 0xda;

    private static final int APP = 0xe0;

    private static String charset = "ISO_IR 100";

    private static String transferSyntax = UID.JPEGBaseline1;

    private static byte[] buffer = new byte[8192];

    private static int jpgHeaderLen;

    private static int jpgLen;

    private static boolean noAPPn = false;

    public Jpg2DcmOld() {
    }

    public final void setCharset(String charset) {
        Jpg2DcmOld.charset = charset;
    }

    private void setTransferSyntax(String uid) {
        Jpg2DcmOld.transferSyntax = uid;
    }

    private void setNoAPPn(boolean noAPPn) {
        Jpg2DcmOld.noAPPn = noAPPn;
    }

    public static void setAttributes(Attributes attrs, File jpgSource, DicomTags tags) {
        try {
            BufferedImage vf = ImageIO.read(jpgSource);
            attrs.setString(Tag.PatientName, VR.AE, tags.getPatientName());
            attrs.setString(Tag.PatientSex, VR.CS, tags.getPatientSex());
            attrs.setString(Tag.PatientID, VR.CS, tags.getPatientID());
            attrs.setString(Tag.PatientBirthDate, VR.AS, JsfUtil.getStringDate(tags.getPatientBirthDate(), "yyyyMMdd"));
            attrs.setString(Tag.StudyDate, VR.AS, JsfUtil.getStringDate(tags.getStudyDate(), "yyyyMMdd"));
            attrs.setString(Tag.SeriesDate, VR.AS, JsfUtil.getStringDate(tags.getSeriesDate(), "yyyyMMdd"));
            attrs.setString(Tag.StudyTime, VR.AS, JsfUtil.getStringDate(tags.getStudyDate(), "HHmmss"));//"101010");
            attrs.setString(Tag.SeriesTime, VR.AS, JsfUtil.getStringDate(tags.getSeriesDate(), "HHmmss"));
            attrs.setString(Tag.StudyDescription, VR.AS, tags.getStudyDescription());
            attrs.setString(Tag.SeriesDescription, VR.AS, tags.getSeriesDescription()); 
            attrs.setString(Tag.Modality, VR.CS, tags.getModality());
            attrs.setInt(Tag.Columns, VR.US, vf.getWidth());
            attrs.setInt(Tag.Rows, VR.US, vf.getHeight());
            attrs.setInt(Tag.InstanceNumber, VR.US, 1);
            attrs.setInt(Tag.SamplesPerPixel, VR.IS, 3);
            attrs.setString(Tag.PhotometricInterpretation, VR.CS, tags.getPhotometricInterpretation());
            attrs.setInt(Tag.BitsAllocated, VR.IS, 8);
            attrs.setInt(Tag.BitsStored, VR.IS, 8);
            attrs.setInt(Tag.NumberOfFrames, VR.IS, tags.getNumberOfFrames());
            attrs.setInt(Tag.SeriesNumber, VR.IS, 2);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Jpg2DcmOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a DICOM file from a JPEG file with the given tags.
     * @param jpgFile
     * @param dcmFile
     * @param tags
     * @throws IOException 
     */
    public static void convert(File jpgFile, File dcmFile, DicomTags tags) throws IOException {
        DataInputStream jpgInput = getJpgInputStream(jpgFile);
        Attributes attrs = convertProcess(jpgFile, jpgInput);
        setAttributes(attrs, jpgFile, tags);
        setDicom(attrs, dcmFile, jpgInput);
    }

    /**
     * Creates a DICOM file from a JPEG file with default required tags.
     * @param jpgFile
     * @param dcmFile
     * @throws IOException 
     */
    public static void convert(File jpgFile, File dcmFile)
            throws IOException {
        DataInputStream jpgInput = getJpgInputStream(jpgFile);
        Attributes attrs = convertProcess(jpgFile, jpgInput);
        setDicom(attrs, dcmFile, jpgInput);
    }

    private static Attributes setDicom(Attributes attrs, File dcmFile, DataInputStream jpgInput) throws IOException {
        Attributes fmi = attrs.createFileMetaInformation(transferSyntax);
        try (DicomOutputStream dos = new DicomOutputStream(dcmFile)) {
            dos.writeDataset(fmi, attrs);
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
        }
        return fmi;
    }

    private static Attributes convertProcess(File jpgFile, DataInputStream jpgInput) throws IOException {
        jpgHeaderLen = 0;
        jpgLen = (int) jpgFile.length();
        Attributes attrs = new Attributes();
        attrs.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
//            try {
//                if (cl.hasOption("mpeg") && cl.hasOption("c"))
//                    attrs = SAXReader.parse(cl.getOptionValue("c"));
//                else if (cl.hasOption("c"))
//                    attrs = SAXReader.parse(cl.getOptionValue("c"));
//            } catch (Exception e) {
//                throw new FileNotFoundException(
//                        "Configuration XML file not found");
//            }
        attrs.setString(Tag.SpecificCharacterSet, VR.CS, charset);
        if (noAPPn || missingRowsColumnsSamplesPMI(attrs)) {
            readHeader(attrs, jpgInput);
        }
        ensureUS(attrs, Tag.BitsAllocated, 8);
        ensureUS(attrs, Tag.BitsStored, attrs.getInt(Tag.BitsAllocated,
                (buffer[jpgHeaderLen] & 0xff) > 8 ? 16 : 8));
        ensureUS(
                attrs,
                Tag.HighBit,
                attrs.getInt(Tag.BitsStored, (buffer[jpgHeaderLen] & 0xff)) - 1);
        ensureUS(attrs, Tag.PixelRepresentation, 0);
        ensureUID(attrs, Tag.StudyInstanceUID);
        ensureUID(attrs, Tag.SeriesInstanceUID);
        ensureUID(attrs, Tag.SOPInstanceUID);
        Date now = new Date();
        attrs.setDate(Tag.InstanceCreationDate, VR.DA, now);
        attrs.setDate(Tag.InstanceCreationTime, VR.TM, now);

        return attrs;
    }

    private static DataInputStream getJpgInputStream(File jpgFile) throws FileNotFoundException {
        DataInputStream jpgInput = new DataInputStream(new BufferedInputStream(
                new FileInputStream(jpgFile)));
        return jpgInput;
    }

    private static boolean missingRowsColumnsSamplesPMI(Attributes attrs) {
        return !(attrs.containsValue(Tag.Rows)
                && attrs.containsValue(Tag.Columns)
                && attrs.containsValue(Tag.SamplesPerPixel) && attrs
                .containsValue(Tag.PhotometricInterpretation));
    }

    private static void readHeader(Attributes attrs, DataInputStream jpgInput)
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

    private static void ensureUID(Attributes attrs, int tag) {
        if (!attrs.containsValue(tag)) {
            attrs.setString(tag, VR.UI, UIDUtils.createUID());
        }
    }

    private static void ensureUS(Attributes attrs, int tag, int val) {
        if (!attrs.containsValue(tag)) {
            attrs.setInt(tag, VR.US, val);
        }
    }

    public static void main(String[] args) {

        try {
            
            File jpgFile = new File("D:\\ExportedImages\\Horse_test7_MonAug19095006PDT2019_cat307.jpg");
            File dcmFile = new File("D:\\ExportedImages\\Horse_test7_MonAug19095006PDT2019_cat307.dcm");
            long start = System.currentTimeMillis();
            Jpg2DcmOld.convert(jpgFile, dcmFile);
            long fin = System.currentTimeMillis();
            LOG.info("Encapsulated " + jpgFile + " to " + dcmFile + " in "
                    + (fin - start) + "ms.");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Jpg2DcmOld.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
