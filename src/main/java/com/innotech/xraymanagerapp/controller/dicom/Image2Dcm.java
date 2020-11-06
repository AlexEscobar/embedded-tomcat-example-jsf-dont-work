/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.ImageSize;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ImageToDicom;
import static com.pixelmed.dicom.ImageToDicom.generateDICOMPixelModuleFromConsumerImageFile;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Alexander Escobar L.
 */
public class Image2Dcm {

    private short rows;
    private short columns;
  

    public ViewDicomTags createDicom(File jpgFile, File dcmFile, ViewDicomTags tags, ImageSize imageSize) throws IOException, DicomException {
        System.out.println("Creating DICOM:: " + jpgFile.getAbsolutePath() + " - dcmFile: " + dcmFile.getCanonicalPath());
        System.out.println("Tags to be created: ");
        System.out.println(tags.toString());
        

        /**
         * *
         * 1.2.840.10008.5.1.4.1.1.1 CR Image Storage
         * 1.2.840.10008.5.1.4.1.1.1.1 Digital X-Ray Image Storage – for
         * Presentation 1.2.840.10008.5.1.4.1.1.1.1.1	Digital X-Ray Image
         * Storage – for Processing 1.2.840.10008.5.1.4.1.1.1.2 Digital
         * Mammography X-Ray Image Storage – for Presentation
         * 1.2.840.10008.5.1.4.1.1.1.2.1	Digital Mammography X-Ray Image Storage
         * – for Processing 1.2.840.10008.5.1.4.1.1.1.3 Digital Intra – oral
         * X-Ray Image Storage – for Presentation 1.2.840.10008.5.1.4.1.1.1.3.1
         * Digital Intra – oral X-Ray Image Storage – for Processing
         */
        String SOPClassUID = DefaultTags.acceptedSOPClassUID.DIGITAL_INTRAORAL_XRAY_IMAGE_STORAGE_FOR_PRESENTATION.getSopClassUID();
        

        AttributeList list = generateDICOMPixelModuleFromConsumerImageFile(jpgFile.getCanonicalPath());
        //generate the DICOM file from the jpeg file and the other attributes supplied
        ImageToDicom imageToDicom = new ImageToDicom(); //instance number

        imageToDicom.addMainAttributes(tags.getStudyId() + "", tags.getId() + "", tags.getId() + "", DefaultTags.MODALITY, SOPClassUID, DefaultTags.MANUFACTURER,
                tags.getInstitutionName(), tags.getSOPInstanceUID(), tags.getPixelSpacing(), list, DefaultTags.IMAGE_TYPE, DefaultTags.IMAGE_TYPE_);
        imageToDicom.addPatientAttributes(tags.getPatientName(), tags.getPatientSex(), tags.getPatientSpecie(),
                JsfUtil.formatDateWithPattern(tags.getPatientBirthdate(), DefaultTags.DATE_FORMAT), JsfUtil.getAge(tags.getPatientBirthdate(), true),
                tags.getPatientId(), tags.getPatientAccessionCode(), tags.getOwnerName(), list);
        imageToDicom.addStudyAttributes(tags.getStudyId() + "", tags.getStudyInstanceUID(),
                JsfUtil.formatDateWithPattern(tags.getStudyDate(), DefaultTags.DATE_FORMAT), tags.getStudyDescription(), list);
        if (tags.getSeriesInstanceUID() != null) {
            imageToDicom.addSeriesAttributes(tags.getSeriesInstanceUID(), JsfUtil.formatDateWithPattern(tags.getSeriesDate(), DefaultTags.DATE_FORMAT), tags.getSeriesDescription(), list);
        } else {
            imageToDicom.addSeriesDefault(tags.getStudyId() + "", tags.getId() + "", list);
        }

        imageToDicom.createDicomFromImage(list, dcmFile.getCanonicalPath(), DefaultTags.TRANSFER_SYNTAX, DefaultTags.SOURCE_APPLICATION_ENTITY_TITLE);
        rows = ImageToDicom.getRowsCalculated();
        columns = ImageToDicom.getColumnsCalculated();
        imageSize.setRows(rows);
        imageSize.setColumns(columns);
        System.out.println("setting rows and columns tags: ROWS("+tags.getImageRows() + "( - COlS("+tags.getImageColumns()+")");
        return tags;
    }

    public short getRows() {
        return rows;
    }

    public void setRows(short rows) {
        this.rows = rows;
    }

    public short getColumns() {
        return columns;
    }

    public void setColumns(short columns) {
        this.columns = columns;
    }

    public static void main(String args[]) {
        System.out.println(TagFromName.StudyInstanceUID);
    }
}
