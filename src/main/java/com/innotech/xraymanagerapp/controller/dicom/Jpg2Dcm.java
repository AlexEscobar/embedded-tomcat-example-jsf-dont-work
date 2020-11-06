package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.DicomTags;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.dcm4che3.imageio.codec.jpeg.JPEG;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.imageio.codec.XPEGParser;
import org.dcm4che3.imageio.codec.jpeg.JPEGParser;
import org.dcm4che3.imageio.codec.mp4.MP4Parser;
import org.dcm4che3.imageio.codec.mpeg.MPEG2Parser;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.io.SAXReader;
import static org.dcm4che3.tool.common.CLIUtils.rb;
import org.dcm4che3.util.StreamUtils;
import org.dcm4che3.util.UIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author gunter zeilinger<gunterze@gmail.com>
 * @author Hesham Elbadawi <bsdreko@gmail.com>
 *
 */
public class Jpg2Dcm {

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
    private static final int BUFFER_SIZE = 8162;

    private Attributes staticMetadata = new Attributes();
    private byte[] buf = new byte[BUFFER_SIZE];

    private static final ElementDictionary DICT = ElementDictionary.getStandardElementDictionary();

    public Jpg2Dcm() {
    }

    public final void setCharset(String charset) {
        Jpg2Dcm.charset = charset;
    }

    private void setTransferSyntax(String uid) {
        Jpg2Dcm.transferSyntax = uid;
    }

    private void setNoAPPn(boolean noAPPn) {
        Jpg2Dcm.noAPPn = noAPPn;
    }

    public static File createFileFromDicomHeader(File jpgFile, File dcmFile, File dcmHeaderFile, ViewDicomTags tags) throws IOException {
        File fileJpg = jpgFile;
        File fileDicomFinal = dcmFile;
        File fileDicomTipo = dcmHeaderFile;
        BufferedImage jpg = ImageIO.read(fileJpg);

        BufferedImage bImage = ImageIO.read(fileJpg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
//        byte[] b = bos.toByteArray();

        //Convert the image to a byte array
        DataBufferByte buff = (DataBufferByte) jpg.getData().getDataBuffer();
        byte[] buffbytes = buff.getData(0);
        byte[] b = new byte[5 * buff.getData(0).length];
        for (int j = 0; j < 5; j++) {
            System.arraycopy(buffbytes, 0, b, j * buffbytes.length, buffbytes.length);
        }

        Attributes meta;
        Attributes attribs;
        try ( //Copy a header
                DicomInputStream dis = new DicomInputStream(fileDicomTipo)) {
            meta = dis.readFileMetaInformation();
            attribs = dis.readDataset(-1, Tag.PixelData);
            DataInputStream jpgInput = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(jpgFile)));
            attribs = readHeader(attribs, jpgInput);
        }

        //Change the rows and columns
        /*Also, our Dicom header needs information about date and time of creation:*/
        attribs.setDate(Tag.InstanceCreationDate, VR.DA, new Date());
        attribs.setDate(Tag.InstanceCreationTime, VR.TM, new Date());
        attribs.setString(Tag.Modality, VR.CS, "DX");
        /* Every Dicom file has a unique identifier. 
    * Here we’re generating study, series and Sop instances UIDs. 
    * You may want to modify these values, but you should to care about their uniqueness. 
         */
        ViewDicomTags dicomTags = (ViewDicomTags) tags;

        String dateFormat = "yyyyMMdd";
        String dateTimeFormat = "HHmmss";
        attribs.setString(Tag.PatientName, VR.PN, dicomTags.getPatientName());
        attribs.setString(Tag.PatientSex, VR.CS, dicomTags.getPatientSex());
        attribs.setString(Tag.PatientID, VR.LO, dicomTags.getPatientId());
        if (dicomTags.getPatientBirthdate() != null) {
            attribs.setString(Tag.PatientBirthDate, VR.DA, JsfUtil.formatDateWithPattern(dicomTags.getPatientBirthdate(), dateFormat));
            attribs.setString(Tag.PatientAge, VR.AS, JsfUtil.getAge(dicomTags.getPatientBirthdate(), true));
        }
        attribs.setString(Tag.AccessionNumber, VR.CS, dicomTags.getPatientAccessionCode());

        // Study
        attribs.setString(Tag.StudyInstanceUID, VR.ST, dicomTags.getStudyInstanceUID());
        attribs.setString(Tag.StudyID, VR.SH, dicomTags.getStudyId() + "");
        attribs.setString(Tag.StudyDate, VR.DA, JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), dateFormat));
        attribs.setString(Tag.StudyTime, VR.TM, JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), dateTimeFormat));
        attribs.setString(Tag.StudyDescription, VR.LO, dicomTags.getStudyDescription().substring(0, 20));

        // Series
        attribs.setString(Tag.SeriesInstanceUID, VR.UI, dicomTags.getSeriesInstanceUID());
        attribs.setString(Tag.SeriesDate, VR.DA, JsfUtil.formatDateWithPattern(dicomTags.getSeriesDate(), dateFormat));
        attribs.setString(Tag.SeriesTime, VR.TM, JsfUtil.formatDateWithPattern(dicomTags.getSeriesDate(), dateTimeFormat));
        attribs.setString(Tag.SeriesDescription, VR.LO, dicomTags.getSeriesDescription());
        attribs.setString(Tag.SOPInstanceUID, VR.UI, dicomTags.getSOPInstanceUID());

        attribs.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.1.3");

        //Write the file                
        attribs.setBytes(Tag.PixelData, VR.OW, b);
        try (DicomOutputStream dcmo = new DicomOutputStream(fileDicomFinal)) {
            dcmo.writeFileMetaInformation(meta);
            attribs.writeTo(dcmo);
        }
        return dcmFile;
    }

    public void convert(String xml, File jpgFile, File dcmFile)
            throws IOException, ParserConfigurationException, SAXException {
        ContentType fileType = ContentType.probe(jpgFile.toPath());
        Attributes attrs = SAXReader.parse(StreamUtils.openFileOrURL(xml));
        attrs.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
        attrs.setString(Tag.SpecificCharacterSet, VR.CS, charset);

        ensureUS(attrs, Tag.BitsAllocated, 8);
        ensureUS(attrs, Tag.BitsStored, attrs.getInt(Tag.BitsAllocated,
                (buffer[jpgHeaderLen] & 0xff) > 8 ? 16 : 8));
        ensureUS(attrs, Tag.HighBit, attrs.getInt(Tag.BitsStored, (buffer[jpgHeaderLen] & 0xff)) - 1);
        ensureUS(attrs, Tag.PixelRepresentation, 0);
        ensureUID(attrs, Tag.StudyInstanceUID);
        ensureUID(attrs, Tag.SeriesInstanceUID);
        ensureUID(attrs, Tag.SOPInstanceUID);
        Date now = new Date();
        attrs.setDate(Tag.InstanceCreationDate, VR.DA, now);
        attrs.setDate(Tag.InstanceCreationTime, VR.TM, now);
        attrs.setString(Tag.Modality, VR.CS, "DX");
//        fileMetadata.addAll(staticMetadata);
        supplementMissingValue(attrs, Tag.SOPClassUID, fileType.getSOPClassUID(true));
        try (SeekableByteChannel channel = Files.newByteChannel(jpgFile.toPath());
                DicomOutputStream dos = new DicomOutputStream(dcmFile)) {
            XPEGParser parser = fileType.newParser(channel);
            parser.getAttributes(attrs);
            dos.writeDataset(attrs.createFileMetaInformation(parser.getTransferSyntaxUID()), attrs);
            dos.writeHeader(Tag.PixelData, VR.OB, -1);
            dos.writeHeader(Tag.Item, null, 0);
            if (noAPPn && parser.getPositionAfterAPPSegments() > 0) {
                copyPixelData(channel, parser.getPositionAfterAPPSegments(), dos,
                        (byte) 0xFF, (byte) JPEG.SOI);
            } else {
                copyPixelData(channel, parser.getCodeStreamPosition(), dos);
            }
            dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
        }
        System.out.println(MessageFormat.format("converted", jpgFile, dcmFile));
    }

    public static File convert(File jpgFile, File dcmFile, Object tags)
            throws IOException {
        jpgHeaderLen = 0;
        jpgLen = (int) jpgFile.length();

        try (DataInputStream jpgInput = new DataInputStream(new BufferedInputStream(
                new FileInputStream(jpgFile)))) {
            Attributes attrs = new Attributes();
            attrs.setString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
            attrs.setString(Tag.SpecificCharacterSet, VR.CS, charset);
            if (noAPPn || missingRowsColumnsSamplesPMI(attrs)) {
                if (tags instanceof ViewDicomTags) {
                    readHeader(attrs, jpgInput, (ViewDicomTags) tags);
                } else {
                    readHeader(attrs, jpgInput, (DicomTags) tags);
                }
            }

            ensureUS(attrs, Tag.BitsAllocated, 8);
            ensureUS(attrs, Tag.BitsStored, attrs.getInt(Tag.BitsAllocated,
                    (buffer[jpgHeaderLen] & 0xff) > 8 ? 16 : 8));
            ensureUS(attrs, Tag.HighBit, attrs.getInt(Tag.BitsStored, (buffer[jpgHeaderLen] & 0xff)) - 1);
            ensureUS(attrs, Tag.PixelRepresentation, 0);
            ensureUID(attrs, Tag.StudyInstanceUID);
            ensureUID(attrs, Tag.SeriesInstanceUID);
            ensureUID(attrs, Tag.SOPInstanceUID);
            Date now = new Date();
            attrs.setDate(Tag.InstanceCreationDate, VR.DA, now);
            attrs.setDate(Tag.InstanceCreationTime, VR.TM, now);
            attrs.setString(Tag.Modality, VR.CS, "DX");

            ViewDicomTags dicomTags = (ViewDicomTags) tags;

            String dateFormat = "yyyyMMdd";
            String dateTimeFormat = "HHmmss";
            attrs.setString(Tag.PixelSpacing,VR.DS, "0.0190678\\0.0192582");
            attrs.setString(Tag.PatientName, VR.PN, dicomTags.getPatientName());
            attrs.setString(Tag.PatientSex, VR.CS, dicomTags.getPatientSex());
            attrs.setString(Tag.PatientID, VR.LO, dicomTags.getPatientId());
            if (dicomTags.getPatientBirthdate() != null) {
                attrs.setString(Tag.PatientBirthDate, VR.DA, JsfUtil.formatDateWithPattern(dicomTags.getPatientBirthdate(), dateFormat));
                attrs.setString(Tag.PatientAge, VR.AS, JsfUtil.getAge(dicomTags.getPatientBirthdate(), true));
            }
            attrs.setString(Tag.AccessionNumber, VR.CS, dicomTags.getPatientAccessionCode());

            // Study
            attrs.setString(Tag.StudyInstanceUID, VR.ST, dicomTags.getStudyInstanceUID());
            attrs.setString(Tag.StudyID, VR.SH, dicomTags.getStudyId() + "");
            attrs.setString(Tag.StudyDate, VR.DA, JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), dateFormat));
            attrs.setString(Tag.StudyTime, VR.TM, JsfUtil.formatDateWithPattern(dicomTags.getStudyDate(), dateTimeFormat));
            attrs.setString(Tag.StudyDescription, VR.LO, dicomTags.getStudyDescription().substring(0, 20));

            // Series
            attrs.setString(Tag.SeriesInstanceUID, VR.UI, dicomTags.getSeriesInstanceUID());
            attrs.setString(Tag.SeriesDate, VR.DA, JsfUtil.formatDateWithPattern(dicomTags.getSeriesDate(), dateFormat));
            attrs.setString(Tag.SeriesTime, VR.TM, JsfUtil.formatDateWithPattern(dicomTags.getSeriesDate(), dateTimeFormat));
            attrs.setString(Tag.SeriesDescription, VR.LO, dicomTags.getSeriesDescription());
            attrs.setString(Tag.SOPInstanceUID, VR.UI, dicomTags.getSOPInstanceUID());

//            attrs.setString(Tag.SOPClassUID, VR.UI, "1.2.840.10008.5.1.4.1.1.1.3");
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
        }
        return dcmFile;
    }

    private void copyPixelData(SeekableByteChannel channel, long position, DicomOutputStream dos, byte... prefix)
            throws IOException {
        long codeStreamSize = channel.size() - position + prefix.length;
        dos.writeHeader(Tag.Item, null, (int) ((codeStreamSize + 1) & ~1));
        dos.write(prefix);
        channel.position(position);
        copy(channel, dos);
        if ((codeStreamSize & 1) != 0) {
            dos.write(0);
        }
    }

    private void copy(ByteChannel in, OutputStream out) throws IOException {
        ByteBuffer bb = ByteBuffer.wrap(buf);
        int read;
        while ((read = in.read(bb)) > 0) {
            out.write(buf, 0, read);
            bb.clear();
        }
    }

    private static void supplementMissingValue(Attributes metadata, int tag, String value) {
        if (!metadata.containsValue(tag)) {
            metadata.setString(tag, DICT.vrOf(tag), value);
        }
    }

    private static boolean missingRowsColumnsSamplesPMI(Attributes attrs) {
        return !(attrs.containsValue(Tag.Rows)
                && attrs.containsValue(Tag.Columns)
                && attrs.containsValue(Tag.SamplesPerPixel) && attrs
                .containsValue(Tag.PhotometricInterpretation));
    }

    private static void setAttributes(Attributes attrs, DicomTags tags) {
        try {

            attrs.setString(Tag.PatientName, VR.AE, tags.getPatientName());
            attrs.setString(Tag.PatientSex, VR.CS, tags.getPatientSex());
            attrs.setString(Tag.PatientID, VR.CS, tags.getPatientID());
            attrs.setString(Tag.StudyDescription, VR.ST, tags.getStudyDescription().substring(0, 20));
//            attrs.setString(Tag.StudyDescription, VR.AS, tags.getStudyDescription());
//            attrs.setString(Tag.SeriesDescription, VR.AS, tags.getSeriesDescription()); 
            attrs.setString(Tag.Modality, VR.CS, tags.getModality());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private static void setAttributes(Attributes attrs, ViewDicomTags tags) {
        try {
            String dateFormat = "yyyyMMdd";
            String dateTimeFormat = "HHmmss";
            attrs.setString(Tag.PatientName, VR.AE, tags.getPatientName());
            attrs.setString(Tag.PatientSex, VR.CS, tags.getPatientSex());
            attrs.setString(Tag.PatientID, VR.CS, tags.getPatientId());
            attrs.setString(Tag.PatientAge, VR.CS, JsfUtil.getAge(tags.getPatientBirthdate(), true));
            attrs.setString(Tag.AccessionNumber, VR.CS, tags.getPatientAccessionCode());

            // Study
            attrs.setString(Tag.StudyInstanceUID, VR.ST, tags.getStudyInstanceUID());
            attrs.setString(Tag.StudyID, VR.ST, tags.getStudyId() + "");
            attrs.setString(Tag.StudyDate, VR.ST, JsfUtil.formatDateWithPattern(tags.getStudyDate(), dateFormat));
            attrs.setString(Tag.StudyTime, VR.ST, JsfUtil.formatDateWithPattern(tags.getStudyDate(), dateTimeFormat));
            attrs.setString(Tag.StudyDescription, VR.ST, tags.getStudyDescription().substring(0, 40));

            // Series
            attrs.setString(Tag.SeriesInstanceUID, VR.ST, tags.getSeriesInstanceUID());
            attrs.setString(Tag.SeriesDate, VR.ST, JsfUtil.formatDateWithPattern(tags.getSeriesDate(), dateFormat));
            attrs.setString(Tag.SeriesTime, VR.ST, JsfUtil.formatDateWithPattern(tags.getSeriesDate(), dateTimeFormat));
            attrs.setString(Tag.SeriesDescription, VR.ST, tags.getSeriesDescription());
            attrs.setString(Tag.SOPInstanceUID, VR.ST, tags.getSOPInstanceUID());

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private static void readHeader(Attributes attrs, DataInputStream jpgInput, DicomTags tags) throws IOException {
        readHeader(attrs, jpgInput);
        setAttributes(attrs, tags);
    }

    private static void readHeader(Attributes attrs, DataInputStream jpgInput, ViewDicomTags tags) throws IOException {
        readHeader(attrs, jpgInput);
        setAttributes(attrs, tags);
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

    private enum ContentType {
        IMAGE_JPEG {
            @Override
            String getSampleMetadataFile(boolean photo) {
                return photo
                        ? "resource:vlPhotographicImageMetadata.xml"
                        : "resource:secondaryCaptureImageMetadata.xml";
            }

            @Override
            String getSOPClassUID(boolean photo) {
                return photo
                        ? UID.VLPhotographicImageStorage
                        : UID.SecondaryCaptureImageStorage;
            }

            @Override
            XPEGParser newParser(SeekableByteChannel channel) throws IOException {
                return new JPEGParser(channel);
            }
        },
        VIDEO_MPEG {
            @Override
            XPEGParser newParser(SeekableByteChannel channel) throws IOException {
                return new MPEG2Parser(channel);
            }
        },
        VIDEO_MP4 {
            @Override
            XPEGParser newParser(SeekableByteChannel channel) throws IOException {
                return new MP4Parser(channel);
            }
        };

        static ContentType probe(Path path) throws IOException {
            String type = Files.probeContentType(path);
            if (type == null) {
                throw new IllegalArgumentException(
                        MessageFormat.format("File type not supported", path));
            }
            switch (type.toLowerCase()) {
                case "image/jpeg":
                case "image/jp2":
                    return ContentType.IMAGE_JPEG;
                case "video/mpeg":
                    return ContentType.VIDEO_MPEG;
                case "video/mp4":
                case "video/quicktime":
                    return ContentType.VIDEO_MP4;
            }
            throw new IllegalArgumentException(
                    MessageFormat.format("unsupported-content-type", type, path));
        }

        String getSampleMetadataFile(boolean photo) {
            return "resource:vlPhotographicImageMetadata.xml";
        }

        String getSOPClassUID(boolean photo) {
            return UID.VideoPhotographicImageStorage;
        }

        abstract XPEGParser newParser(SeekableByteChannel channel) throws IOException;
    }

    public static void main(String[] args) {
        try {
            Jpg2Dcm jpg2Dcm = new Jpg2Dcm();

            @SuppressWarnings("rawtypes")
//            List argList = cl.getArgList();
            File jpgFile = new File("D:\\DicomSent\\2.25.190318177288460239506709736887191182730.jpg");
            File dcmFile = new File("D:\\DicomSent\\1.2.826.0.1.3680043.11.127.dcm");
//            String xml = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?><NativeDicomModel xml:space=\"preserve\"><DicomAttribute keyword=\"FileMetaInformationVersion\" tag=\"00020001\" vr=\"OB\"><InlineBinary>AAE=</InlineBinary></DicomAttribute><DicomAttribute keyword=\"MediaStorageSOPClassUID\" tag=\"00020002\" vr=\"UI\"><Value number=\"1\">1.2.840.10008.5.1.4.1.1.1.1</Value></DicomAttribute><DicomAttribute keyword=\"MediaStorageSOPInstanceUID\" tag=\"00020003\" vr=\"UI\"><Value number=\"1\">1.2.276.0.7230010.3.0.3.5.1.11336438.1510188914</Value></DicomAttribute><DicomAttribute keyword=\"TransferSyntaxUID\" tag=\"00020010\" vr=\"UI\"><Value number=\"1\">1.2.840.10008.1.2.1</Value></DicomAttribute><DicomAttribute keyword=\"ImplementationClassUID\" tag=\"00020012\" vr=\"UI\"><Value number=\"1\">1.2.826.0.1.3680043.9.3811.1.5.0</Value></DicomAttribute><DicomAttribute keyword=\"ImplementationVersionName\" tag=\"00020013\" vr=\"SH\"><Value number=\"1\">PYNETDICOM_150</Value></DicomAttribute><DicomAttribute keyword=\"SpecificCharacterSet\" tag=\"00080005\" vr=\"CS\"><Value number=\"1\">ISO_IR 192</Value></DicomAttribute><DicomAttribute keyword=\"ImageType\" tag=\"00080008\" vr=\"CS\"><Value number=\"1\">ORIGINAL</Value><Value number=\"2\">PRIMARY</Value></DicomAttribute><DicomAttribute keyword=\"SOPClassUID\" tag=\"00080016\" vr=\"UI\"><Value number=\"1\">1.2.840.10008.5.1.4.1.1.1.1</Value></DicomAttribute><DicomAttribute keyword=\"SOPInstanceUID\" tag=\"00080018\" vr=\"UI\"><Value number=\"1\">1.2.276.0.7230010.3.0.3.5.1.11336438.1510188914</Value></DicomAttribute><DicomAttribute keyword=\"StudyDate\" tag=\"00080020\" vr=\"DA\"><Value number=\"1\">20200227</Value></DicomAttribute><DicomAttribute keyword=\"SeriesDate\" tag=\"00080021\" vr=\"DA\"><Value number=\"1\">20200227</Value></DicomAttribute><DicomAttribute keyword=\"AcquisitionDate\" tag=\"00080022\" vr=\"DA\"><Value number=\"1\">20200227</Value></DicomAttribute><DicomAttribute keyword=\"ContentDate\" tag=\"00080023\" vr=\"DA\"><Value number=\"1\">20200227</Value></DicomAttribute><DicomAttribute keyword=\"StudyTime\" tag=\"00080030\" vr=\"TM\"><Value number=\"1\">122851</Value></DicomAttribute><DicomAttribute keyword=\"SeriesTime\" tag=\"00080031\" vr=\"TM\"><Value number=\"1\">122851</Value></DicomAttribute><DicomAttribute keyword=\"AcquisitionTime\" tag=\"00080032\" vr=\"TM\"><Value number=\"1\">123823</Value></DicomAttribute><DicomAttribute keyword=\"ContentTime\" tag=\"00080033\" vr=\"TM\"><Value number=\"1\">123823</Value></DicomAttribute><DicomAttribute keyword=\"AccessionNumber\" tag=\"00080050\" vr=\"SH\"><Value number=\"1\">HSNT000341</Value></DicomAttribute><DicomAttribute keyword=\"Modality\" tag=\"00080060\" vr=\"CS\"><Value number=\"1\">DX</Value></DicomAttribute><DicomAttribute keyword=\"ConversionType\" tag=\"00080064\" vr=\"CS\"><Value number=\"1\">DV</Value></DicomAttribute><DicomAttribute keyword=\"Manufacturer\" tag=\"00080070\" vr=\"LO\"><Value number=\"1\">Santu</Value></DicomAttribute><DicomAttribute keyword=\"InstitutionName\" tag=\"00080080\" vr=\"LO\"><Value number=\"1\">Humane Society of North Texas</Value></DicomAttribute><DicomAttribute keyword=\"ReferringPhysicianName\" tag=\"00080090\" vr=\"PN\"/><DicomAttribute keyword=\"StationName\" tag=\"00081010\" vr=\"SH\"><Value number=\"1\">AETitle</Value></DicomAttribute><DicomAttribute keyword=\"StudyDescription\" tag=\"00081030\" vr=\"LO\"><Value number=\"1\">Feline_Extremities_Hind</Value></DicomAttribute><DicomAttribute keyword=\"PerformingPhysicianName\" tag=\"00081050\" vr=\"PN\"><PersonName number=\"1\"><Alphabetic><FamilyName>Administrator</FamilyName></Alphabetic></PersonName></DicomAttribute><DicomAttribute keyword=\"PerformingPhysicianIdentificationSequence\" tag=\"00081052\" vr=\"SQ\"><Item number=\"1\"><DicomAttribute keyword=\"PersonIdentificationCodeSequence\" tag=\"00401101\" vr=\"SQ\"><Item number=\"1\"><DicomAttribute keyword=\"CodeValue\" tag=\"00080100\" vr=\"SH\"><Value number=\"1\">admin</Value></DicomAttribute></Item></DicomAttribute></Item></DicomAttribute><DicomAttribute keyword=\"OperatorsName\" tag=\"00081070\" vr=\"PN\"><PersonName number=\"1\"><Alphabetic><FamilyName>Administrator</FamilyName></Alphabetic></PersonName></DicomAttribute><DicomAttribute keyword=\"ReferencedImageSequence\" tag=\"00081140\" vr=\"SQ\"><Item number=\"1\"><DicomAttribute keyword=\"ReferencedSOPClassUID\" tag=\"00081150\" vr=\"UI\"><Value number=\"1\">1.2.840.10008.5.1.4.1.1.1.1</Value></DicomAttribute><DicomAttribute keyword=\"ReferencedSOPInstanceUID\" tag=\"00081155\" vr=\"UI\"><Value number=\"1\">1.2.276.0.7230010.3.0.3.5.1.11336438.1510188914</Value></DicomAttribute></Item></DicomAttribute><DicomAttribute keyword=\"PatientName\" tag=\"00100010\" vr=\"PN\"><PersonName number=\"1\"><Alphabetic><FamilyName>mckenna</FamilyName></Alphabetic></PersonName></DicomAttribute><DicomAttribute keyword=\"PatientID\" tag=\"00100020\" vr=\"LO\"><Value number=\"1\">HSNT000341</Value></DicomAttribute><DicomAttribute keyword=\"PatientBirthDate\" tag=\"00100030\" vr=\"DA\"><Value number=\"1\">20200227</Value></DicomAttribute><DicomAttribute keyword=\"PatientSex\" tag=\"00100040\" vr=\"CS\"><Value number=\"1\">O</Value></DicomAttribute><DicomAttribute keyword=\"OtherPatientIDs\" tag=\"00101000\" vr=\"LO\"><Value number=\"1\">43690847</Value></DicomAttribute><DicomAttribute keyword=\"PatientAge\" tag=\"00101010\" vr=\"AS\"><Value number=\"1\">0D</Value></DicomAttribute><DicomAttribute keyword=\"PatientSize\" tag=\"00101020\" vr=\"DS\"/><DicomAttribute keyword=\"PatientWeight\" tag=\"00101030\" vr=\"DS\"/><DicomAttribute keyword=\"PatientSpeciesDescription\" tag=\"00102201\" vr=\"LO\"/><DicomAttribute keyword=\"PatientSexNeutered\" tag=\"00102203\" vr=\"CS\"/><DicomAttribute keyword=\"PatientBreedDescription\" tag=\"00102292\" vr=\"LO\"/><DicomAttribute keyword=\"ResponsiblePerson\" tag=\"00102297\" vr=\"PN\"><PersonName number=\"1\"><Alphabetic><FamilyName>hsnt</FamilyName></Alphabetic></PersonName></DicomAttribute><DicomAttribute keyword=\"ResponsiblePersonRole\" tag=\"00102298\" vr=\"CS\"><Value number=\"1\">OWNER</Value></DicomAttribute><DicomAttribute keyword=\"PatientComments\" tag=\"00104000\" vr=\"LT\"/><DicomAttribute tag=\"00115000\" vr=\"LO\"><Value number=\"1\">RAW</Value></DicomAttribute><DicomAttribute keyword=\"BodyPartExamined\" tag=\"00180015\" vr=\"CS\"><Value number=\"1\">LEG</Value></DicomAttribute><DicomAttribute keyword=\"KVP\" tag=\"00180060\" vr=\"DS\"><Value number=\"1\">51.000000</Value></DicomAttribute><DicomAttribute keyword=\"ProtocolName\" tag=\"00181030\" vr=\"LO\"><Value number=\"1\">Hindlimb DV</Value></DicomAttribute><DicomAttribute keyword=\"ExposureTime\" tag=\"00181150\" vr=\"IS\"><Value number=\"1\">63.000000</Value></DicomAttribute><DicomAttribute keyword=\"XRayTubeCurrent\" tag=\"00181151\" vr=\"IS\"><Value number=\"1\">100.000000</Value></DicomAttribute><DicomAttribute keyword=\"Exposure\" tag=\"00181152\" vr=\"IS\"><Value number=\"1\">6.300000</Value></DicomAttribute><DicomAttribute keyword=\"ImageAndFluoroscopyAreaDoseProduct\" tag=\"0018115E\" vr=\"DS\"><Value number=\"1\">0.000000</Value></DicomAttribute><DicomAttribute keyword=\"ImagerPixelSpacing\" tag=\"00181164\" vr=\"DS\"><Value number=\"1\">0.139000</Value><Value number=\"2\">0.139000</Value></DicomAttribute><DicomAttribute keyword=\"BodyPartThickness\" tag=\"001811A0\" vr=\"DS\"><Value number=\"1\">0.000000</Value></DicomAttribute><DicomAttribute keyword=\"CompressionForce\" tag=\"001811A2\" vr=\"DS\"><Value number=\"1\">0.000000</Value></DicomAttribute><DicomAttribute keyword=\"ExposureIndex\" tag=\"00181411\" vr=\"DS\"><Value number=\"1\">1226.109985</Value></DicomAttribute><DicomAttribute keyword=\"StudyInstanceUID\" tag=\"0020000D\" vr=\"UI\"><Value number=\"1\">1.2.276.0.7230010.3.0.3.5.1.11336428.473813523</Value></DicomAttribute><DicomAttribute keyword=\"SeriesInstanceUID\" tag=\"0020000E\" vr=\"UI\"><Value number=\"1\">1.2.276.0.7230010.3.0.3.5.1.11336438.283174419</Value></DicomAttribute><DicomAttribute keyword=\"StudyID\" tag=\"00200010\" vr=\"SH\"/><DicomAttribute keyword=\"SeriesNumber\" tag=\"00200011\" vr=\"IS\"><Value number=\"1\">3</Value></DicomAttribute><DicomAttribute keyword=\"InstanceNumber\" tag=\"00200013\" vr=\"IS\"><Value number=\"1\">3</Value></DicomAttribute><DicomAttribute keyword=\"SamplesPerPixel\" tag=\"00280002\" vr=\"US\"><Value number=\"1\">1</Value></DicomAttribute><DicomAttribute keyword=\"PhotometricInterpretation\" tag=\"00280004\" vr=\"CS\"><Value number=\"1\">MONOCHROME1</Value></DicomAttribute><DicomAttribute keyword=\"NumberOfFrames\" tag=\"00280008\" vr=\"IS\"><Value number=\"1\">1</Value></DicomAttribute><DicomAttribute keyword=\"Rows\" tag=\"00280010\" vr=\"US\"><Value number=\"1\">1104</Value></DicomAttribute><DicomAttribute keyword=\"Columns\" tag=\"00280011\" vr=\"US\"><Value number=\"1\">980</Value></DicomAttribute><DicomAttribute keyword=\"PixelSpacing\" tag=\"00280030\" vr=\"DS\"><Value number=\"1\">0.139000</Value><Value number=\"2\">0.139000</Value></DicomAttribute><DicomAttribute keyword=\"BitsAllocated\" tag=\"00280100\" vr=\"US\"><Value number=\"1\">16</Value></DicomAttribute><DicomAttribute keyword=\"BitsStored\" tag=\"00280101\" vr=\"US\"><Value number=\"1\">16</Value></DicomAttribute><DicomAttribute keyword=\"HighBit\" tag=\"00280102\" vr=\"US\"><Value number=\"1\">15</Value></DicomAttribute><DicomAttribute keyword=\"PixelRepresentation\" tag=\"00280103\" vr=\"US\"><Value number=\"1\">0</Value></DicomAttribute><DicomAttribute keyword=\"WindowCenter\" tag=\"00281050\" vr=\"DS\"><Value number=\"1\">7781</Value></DicomAttribute><DicomAttribute keyword=\"WindowWidth\" tag=\"00281051\" vr=\"DS\"><Value number=\"1\">12967</Value></DicomAttribute><DicomAttribute keyword=\"RescaleIntercept\" tag=\"00281052\" vr=\"DS\"><Value number=\"1\">0</Value></DicomAttribute><DicomAttribute keyword=\"RescaleSlope\" tag=\"00281053\" vr=\"DS\"><Value number=\"1\">1</Value></DicomAttribute><DicomAttribute keyword=\"RescaleType\" tag=\"00281054\" vr=\"LO\"><Value number=\"1\">US</Value></DicomAttribute><DicomAttribute keyword=\"WindowCenterWidthExplanation\" tag=\"00281055\" vr=\"LO\"><Value number=\"1\">NORMAL</Value></DicomAttribute><DicomAttribute keyword=\"OrganDose\" tag=\"00400316\" vr=\"DS\"><Value number=\"1\">0.000000</Value></DicomAttribute><DicomAttribute keyword=\"PixelData\" tag=\"7FE00010\" vr=\"OW\"><BulkData uri=\"file:/home/tech/imidental/IMIStoreSCP/IMIUploader/Queue/1.2.276.0.7230010.3.0.3.5.1.11336438.1510188914.dcm?offset=1702&amp;length=2163840\"/></DicomAttribute></NativeDicomModel>";
            String xml = "D:\\DicomSent\\xmlTest.xml";
            long start = System.currentTimeMillis();
            jpg2Dcm.convert(xml, jpgFile, dcmFile);
            long fin = System.currentTimeMillis();
            System.out.println("Encapsulated " + jpgFile + " to " + dcmFile + " in "
                    + (fin - start) + "ms.");
        } catch (IOException | ParserConfigurationException | SAXException e) {
            java.util.logging.Logger.getLogger(Jpg2Dcm.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
