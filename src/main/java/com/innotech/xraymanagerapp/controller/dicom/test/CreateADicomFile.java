/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom.test;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ImageToDicom;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Hi
 */
public class CreateADicomFile extends ImageToDicom {

    public CreateADicomFile(String inputFile, String outputFile, String patientName, String patientID,
            String studyID, String seriesNumber, String instanceNumber, String modality, String sopClass, AttributeList list) throws IOException, DicomException {
        super(inputFile, outputFile, patientName, patientID, studyID, seriesNumber, instanceNumber, modality, sopClass);

        ImageToDicom imageToDicom = new ImageToDicom(inputFile, outputFile, patientName, patientID, studyID, seriesNumber, instanceNumber, modality, sopClass);
    }

    public static void main(String[] args) {
        String scJpegFilePath = "D:\\DicomSent\\3197PID_cat204TID_969AID_1580172219540.png";
        String newDicomFile = "D:\\DicomSent\\SaravananLex.dcm";
        try {

            //generate the DICOM file from the jpeg file and the other attributes supplied
            ImageToDicom imageToDicom = new ImageToDicom(scJpegFilePath, //path to existing JPEG image 
                    newDicomFile, //output DICOM file with full path
                    "Saravanan Subramanian", //name of patient
                    "12121221", //patient id
                    "2323232322", //study id
                    "3232323232", //series number
                    "42423232234"); //instance number

            AttributeList list = generateDICOMPixelModuleFromConsumerImageFile(scJpegFilePath);

//            Attribute atr = new Attribute();
            //now, dump the contents of the DICOM file to the console
//            AttributeList list = new AttributeList();

            list.read(newDicomFile);
//            list.
            System.out.println(list.toString());

            String content = list.toString();
            File file = new File("D:\\DicomSent\\filename.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(content);
                // Be sure to close BufferedWriter
            }

        } catch (DicomException | IOException e) {
            e.printStackTrace();
        }
    }

}
