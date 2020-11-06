/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import com.innotech.xraymanagerapp.model.DicomTags;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author Alexander Escobar L.
 */
public class DicomTagsGenerator {

    // Patient Tags
    private static final String PATIENT_ID = "00100020";
    private static final String PATIENT_NAME = "00100010";
    private static final String PATIENT_AGE = "";
    private static final String PATIENT_SEX = "00100040";
    private static final String PATIENT_BIRTHDATE = "00100030";
    private static final String ACCESSION_CODE = "00080050";
    private static final String PATIENT_SPECIE = "00102201";
    private static final String PATIENT_BREED = "00102292";
    private static final String OWNER_NAME = "00102297";

    // Study Tags
    private static final String STUDY_ID = "00200010";
    private static final String STUDY_DATE = "00080020";
    private static final String STUDY_TIME = "00080030";
    private static final String STUDY_DESCRIPTION = "00081030";
    private static final String SERIES_DATE = "00080021";
    private static final String SERIES_TIME = "00080031";
    private static final String SERIES_DESCRIPTION = "0008103E";
    private static final String ACQUISITION_DATE = "00080022";
    private static final String ACQUISITION_TIME = "00080032";
    private static final String INSTITUTION_NAME = "00080080";
    private static final String INSTITUTION_ADDRESS = "00080081";

    // UID DICOM Tags    
    private static final String FileMetaInformationGroupLength = "00020000";//		164
    private static final String FileMetaInformationVersion = "00020001";//
    private static final String MediaStorageSOPClassUID = "00020002";// 1.2.840.10008.5.1.4.1.1.1.1
    private static final String MediaStorageSOPInstanceUID = "00020003";// 1.2.826.0.1.3680043.2.876.12915.2.5.1.20181218134341.2.14
    private static final String TransferSyntaxUID = "00020010";// 1.2.840.10008.1.2.1
    private static final String ImplementationClassUID = "00020012";// 1.2.3.4711.4
    private static final String SpecificCharacterSet = "00080005";// ISO_IR 100
    private static final String ImageType = "00080008";// ORIGINAL\PRIMARY
    private static final String InstanceCreationDate = "00080012";// 20181218
    private static final String InstanceCreationTime = "00080013";// 134341
    private static final String SOPClassUID = "00080016";// 1.2.840.10008.5.1.4.1.1.1.1
    private static final String SOPInstanceUID = "00080018";// 1.2.826.0.1.3680043.2.876.12915.2.5.1.20181218134341.2.14
    private static final String ContentDate = "00080023";// 20181218
    private static final String ContentTime = "00080033";// 143040
    private static final String Modality = "00080060";// DX
    private static final String PresentationIntentType = "00080068";// FOR PRESENTATION
    private static final String Manufacturer = "00080070";// Oehm und Rehbein GmbH

    private final static String ReferringPhysicianName = "00080090";//		
    private final static String StudyDescription = "00081030";//		Hind extremities, Vertebrae / Pelvis
    private final static String SeriesDescription = "0008103E";//		Stifle LAT
    private final static String OperatorsName = "00081070";//		
    private final static String AnatomicRegionSequence = "00082218";//		
    
    // Patient tags
    private final static String PatientName = "00100010";//		dhaliwal pepsi
    private final static String PatientID = "00100020";//		5982
    private final static String PatientBirthDate = "00100030";//		
    private final static String PatientSex = "00100040";//		O
    ////////////////////
    
    private final static String AccessionNumber	= "00080050";//167c344dc3d
    private final static String PregnancyStatus = "001021C0";//		4
    private final static String PatientSpeciesDescription = "00102201";//		Dog
    private final static String PatientSexNeutered = "00102203";//		
    private final static String UnknownTagAndData = "00102204";//	
    private final static String PatientBreedDescription = "00102292";//		
    private final static String BreedRegistrationNumber = "00102295";//		
    private final static String ResponsiblePerson = "00102297";//		dhaliwal
    private final static String ResponsiblePersonRole = "00102298";//	ResponsiblePersonRole	OWNER
    private final static String ResponsibleOrganization = "00102299";//		
    private final static String PatientComments = "00104000";//		
    private final static String BodyPartExamined = "00180015";//		STIFLE
    private final static String SoftwareVersions = "00181020";//		5.1.43-143
    private final static String ImagerPixelSpacing = "00181164";//		0.154\0.154
    private final static String AcquisitionDeviceProcessingDescription = "00181400";//		COP3: Hind extremities
    private final static String RelativeXRayExposure = "00181405";//		1733
    private final static String ExposureIndex = "00181411";//		100
    private final static String TargetExposureIndex = "00181412";//		630
    private final static String DeviationIndex = "00181413";//		-8
    private final static String Sensitivity = "00186000";//		1733
    private final static String DetectorTemperature = "00187001";//		29.0
    private final static String DetectorType = "00187004";//		SCINTILLATOR
    private final static String DetectorID = "0018700A";//		C07RK0N-095
    private final static String DetectorManufacturerName = "0018702A";//		CareRay
    private final static String DetectorManufacturerModelName = "0018702B";//		CareView-1500L
    private final static String StudyInstanceUID = "0020000D";//		1.2.826.0.1.3680043.2.876.12915.2.5.1.20181218133046.0.42
    private final static String SeriesInstanceUID = "0020000E";//		1.2.826.0.1.3680043.2.876.12915.2.5.1.20181218133122.1.110
    private final static String StudyID = "00200010";//		167c344dc3d
    private final static String SeriesNumber = "00200011";//		2
    private final static String InstanceNumber = "00200013";//		1
    private final static String Laterality = "00200060";//	Laterality	
    private final static String ImageComments = "00204000";//	ImageComments	
    private final static String SamplesPerPixel = "00280002";//	SamplesPerPixel	1
    private final static String PhotometricInterpretation = "00280004";//		MONOCHROME2
    private final static String Rows = "00280010";//		1515
    private final static String Columns = "00280011";//		1418
    private final static String PixelAspectRatio = "00280034";//		1\1
    private final static String BitsAllocated = "00280100";//		16
    private final static String BitsStored = "00280101";//		12
    private final static String HighBit = "00280102";//		11
    private final static String PixelRepresentation = "00280103";//		0
    private final static String SmallestImagePixelValue = "00280106";//		0
    private final static String LargestImagePixelValue = "00280107";//		4095
    private final static String BurnedInAnnotation = "00280301";//		NO
    private final static String PixelIntensityRelationship = "00281040";//		LIN
    private final static String PixelIntensityRelationshipSign = "00281041";//		-1
    private final static String WindowCenter = "00281050";//		643
    private final static String WindowWidth = "00281051";//		2930
    private final static String RescaleIntercept = "00281052";//		0
    private final static String RescaleSlope = "00281053";//		1
    private final static String RescaleType = "00281054";//		US
    private final static String PrivateCreator = "00290010";//		OR Technology Image Process Data
    private final static String UnknownTag = "00290101";//	Unknown Tag & Data	<?xml version="1.0" encoding="UTF-8"?>

    private com.innotech.xraymanagerapp.model.DicomTags dicomTags;
    
    public DicomTagsGenerator(){
    }

    /**
     * Builds the map to create the dicom table that will be shown on the viewer
     * @param imageId
     * @return 
     */
    public List<DicomTags> buildDicomTags(Integer imageId) {
        List<DicomTags> dicomTagsList = new ArrayList();
        DicomTags dicomTags;
//        TagsToShow tags = ejbTagsFacade.getList(imageId);
//        
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientName, "PatientName", tags.getPatientName()));
//        dicomTagsList.add(new DicomTags(PatientSex, "PatientSex", tags.getPatientSex().equals("0")?"F":"M"));
//        dicomTagsList.add(new DicomTags(PatientBirthDate, "PatientBirthDate", tags.getPatientBirthdate()));
//        dicomTagsList.add(new DicomTags(AccessionNumber, "AccessionNumber", tags.getPatientAccessionCode()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));
//        dicomTagsList.add(new DicomTags(PatientID, "PatientID", tags.getPatientId()));

        return null;
    }
    
    /**
     * Gets only the tags to show on the viewer
     * @param imageId
     * @return 
     */
    public ViewDicomTags getTagsToShow(Integer imageId){
        return null;//ejbTagsFacade.getList(imageId);
    }

}
