/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.google.gson.Gson;
import com.innotech.xraymanagerapp.controller.dicom.DefaultTags;
import com.innotech.xraymanagerapp.controller.email.EmailSend;
import com.innotech.xraymanagerapp.controller.export.XrayExport;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.XmlReader;
import com.innotech.xraymanagerapp.controller.viewer.StudyAnnotationsModel;
import com.innotech.xraymanagerapp.model.EmailConfig;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.Users;
import com.innotech.xraymanagerapp.model.ViewDicomTags;
import com.innotech.xraymanagerapp.model.dicom.SeriesTag;
import com.innotech.xraymanagerapp.model.dicom.StudiesEndpointModel;
import com.innotech.xraymanagerapp.model.dicom.StudyTag;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class DicomViewerController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ViewDicomTagsFacade ejbViewDicomTagsFacade;

    @EJB
    private ViewerBusinessController ejbViewerBusinessController;

    @EJB
    private com.innotech.xraymanagerapp.dto.DicomServersFacade dicomServersController;

    @EJB
    private com.innotech.xraymanagerapp.dto.EmailConfigFacade emailController;

    @EJB
    private com.innotech.xraymanagerapp.dto.EmailConfigFacade ejbEmailFacade;

    @EJB
    private com.innotech.xraymanagerapp.business.StudyAnnotationStateBusinessController ejbStudyAnnotationsFacade;


    public List<ViewDicomTags> getTagsFromStoredXML(String studyId) {
        return ejbViewDicomTagsFacade.getListByStudyUID(studyId);
    }

    public ViewDicomTags getTagsToShowFromDatabase(Integer imageId) {
        return ejbViewDicomTagsFacade.findByViewImageId(imageId);// list of tags 
    }

    public String getStudiesAsJson(String studyId, String serviceUrl) {
        String json = new Gson().toJson(getStudyTagsList(studyId, serviceUrl));
        return json;
    }

    public String getSeriesTagsAsJson(ViewDicomTags series) {
        String json = new Gson().toJson(getSeriesTags(series));
//        System.out.println("The Json on DicomViewerController class getSeriesTagsAsJson(): " + json);
        return json;
    }

    public List<StudyTag> getStudyTagsList(String studyId, String serviceUrl) {
        List<ViewDicomTags> viewDicomTagsList = getTagsFromStoredXML(studyId);
        List<StudyTag> studyTags = new ArrayList();
        for (ViewDicomTags viewDicomTags : viewDicomTagsList) {
            studyTags.add(getStudyTags(viewDicomTags, serviceUrl));
        }
        return studyTags;
    }

    public StudyTag getStudyTags(ViewDicomTags tags, String serviceUrl) {
        StudyTag studyTag1 = new StudyTag();
        StudiesEndpointModel st1 = new StudiesEndpointModel();
        List<String> valuesList = new ArrayList(1);

        // tag 00080005
        st1.setVr("CS");
        valuesList.add("ISO_IR 100");
        st1.setValue(valuesList);
        studyTag1.set00080005(st1);

        // tag 00080054
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("AE");
        valuesList.add("IMIXrayManager");
        st1.setValue(valuesList);
        studyTag1.set00080054(st1);

        // tag 00080060
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("CS");
        valuesList.add("DX");
        st1.setValue(valuesList);
        studyTag1.set00080060(st1);

        // tag 0008103E
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(tags.getSeriesDescription());
        st1.setValue(valuesList);
        studyTag1.set0008103E(st1);

        // tag 00081190
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UR");
        valuesList.add(new StringBuilder(serviceUrl).append("/").append(tags.getSeriesInstanceUID()).toString());
        st1.setValue(valuesList);
        studyTag1.set00081190(st1);

        // tag 0020000D
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(tags.getStudyInstanceUID());
        st1.setValue(valuesList);
        studyTag1.set0020000D(st1);

        // tag 0020000E
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(tags.getSeriesInstanceUID());
        st1.setValue(valuesList);
        studyTag1.set0020000E(st1);

        // tag 00200011 - 	Number of Series Related Instances
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("IS");
        valuesList.add("1");
        st1.setValue(valuesList);
        studyTag1.set00200011(st1);

        return studyTag1;
    }

    public SeriesTag getSeriesTags(ViewDicomTags tags) {
        SeriesTag seriesTag1 = new SeriesTag();
        StudiesEndpointModel st1 = new StudiesEndpointModel();
        List<String> valuesList = new ArrayList(1);

        // tag 00080005
//        st1.setVr("CS");
//        valuesList.add("206");
//        st1.setValue(valuesList);
//        seriesTag1.set00020000(st1);
        // tag 00080054
        st1.setVr("UI");
        valuesList.add(DefaultTags.acceptedSOPClassUID.DIGITAL_INTRAORAL_XRAY_IMAGE_STORAGE_FOR_PRESENTATION.getSopClassUID());
        st1.setValue(valuesList);
        seriesTag1.setSOPClassUID(st1);

        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(tags.getSeriesInstanceUID());
        st1.setValue(valuesList);
        seriesTag1.setMediaStorageSOPInstanceUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(DefaultTags.TRANSFER_SYNTAX);
        st1.setValue(valuesList);
        seriesTag1.setTransferSyntaxUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(DefaultTags.IMPLEMENTATION_CLASS_UID);
        st1.setValue(valuesList);
        seriesTag1.setImplementationClassUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("AE");
        valuesList.add(DefaultTags.SOURCE_APPLICATION_ENTITY_TITLE);
        st1.setValue(valuesList);
        seriesTag1.setSourceApplicationEntityTitle(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("CS");
        valuesList.add(DefaultTags.IMAGE_TYPE + "/" + DefaultTags.IMAGE_TYPE_);
        st1.setValue(valuesList);
        seriesTag1.setImageType(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(DefaultTags.INSTANCE_CREATOR_UID);
        st1.setValue(valuesList);
        seriesTag1.setInstanceCreatorUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(DefaultTags.acceptedSOPClassUID.DIGITAL_INTRAORAL_XRAY_IMAGE_STORAGE_FOR_PRESENTATION.getSopClassUID());
        st1.setValue(valuesList);
        seriesTag1.setSOPClassUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(tags.getSeriesInstanceUID());
        st1.setValue(valuesList);
        seriesTag1.setSOPInstanceUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("DA");
        valuesList.add(JsfUtil.formatDateWithPattern(tags.getStudyDate(), DefaultTags.DATE_FORMAT));
        st1.setValue(valuesList);
        seriesTag1.setStudyDate(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("DA");
        valuesList.add(JsfUtil.formatDateWithPattern(tags.getSeriesDate(), DefaultTags.DATE_FORMAT));
        st1.setValue(valuesList);
        seriesTag1.setSeriesDate(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("DA");
        valuesList.add(JsfUtil.formatDateWithPattern(tags.getStudyDate(), DefaultTags.DATE_FORMAT));
        st1.setValue(valuesList);
        seriesTag1.setStudyTime(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("DA");
        valuesList.add(JsfUtil.formatDateWithPattern(tags.getSeriesDate(), DefaultTags.DATE_FORMAT));
        st1.setValue(valuesList);
        seriesTag1.setSeriesTime(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("SH");
        valuesList.add(tags.getPatientAccessionCode());
        st1.setValue(valuesList);
        seriesTag1.setAccessionNumber(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("CS");
        valuesList.add(DefaultTags.MODALITY);
        st1.setValue(valuesList);
        seriesTag1.setModality(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(DefaultTags.MANUFACTURER);
        st1.setValue(valuesList);
        seriesTag1.setManufacturer(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(tags.getInstitutionName());
        st1.setValue(valuesList);
        seriesTag1.setInstitutionName(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(tags.getStudyDescription());
        st1.setValue(valuesList);
        seriesTag1.setStudyDescription(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(tags.getSeriesDescription());
        st1.setValue(valuesList);
        seriesTag1.setSeriesDescription(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("PN");
        valuesList.add(tags.getPatientName());
        st1.setValue(valuesList);
        seriesTag1.setPatientName(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(tags.getPatientId());
        st1.setValue(valuesList);
        seriesTag1.setPatientId(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("CS");
        valuesList.add(tags.getPatientSex());
        st1.setValue(valuesList);
        seriesTag1.setPatientSex(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(tags.getPatientSpecie());
        st1.setValue(valuesList);
        seriesTag1.setPatientSpeciesDescription(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("PN");
        valuesList.add(tags.getOwnerName());
        st1.setValue(valuesList);
        seriesTag1.setResponsiblePerson(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add("Owner");
        st1.setValue(valuesList);
        seriesTag1.setResponsiblePersonRole(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(tags.getStudyInstanceUID());
        st1.setValue(valuesList);
        seriesTag1.setStudyInstanceUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("UI");
        valuesList.add(tags.getSeriesInstanceUID());
        st1.setValue(valuesList);
        seriesTag1.setSeriesInstanceUID(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("SH");
        valuesList.add(tags.getStudyId() + "");
        st1.setValue(valuesList);
        seriesTag1.setStudyId(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("IS");
        valuesList.add("1");
        st1.setValue(valuesList);
        seriesTag1.setSeriesNumber(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("US");
        valuesList.add("1");
        st1.setValue(valuesList);
        seriesTag1.setSamplesPerPixel(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("CS");
        valuesList.add("MONOCHROME2");
        st1.setValue(valuesList);
        seriesTag1.setPhotometricInterpretation(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("US");
        System.out.println("ROWS::::::::::::::::: "+tags.getImageRows());
        valuesList.add(String.valueOf(tags.getImageRows()));
        st1.setValue(valuesList);
        seriesTag1.setRows(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);
        System.out.println("COLUMNS::::::::::::::::: "+tags.getImageColumns());
        st1.setVr("US");
        valuesList.add(String.valueOf(tags.getImageColumns()));
        st1.setValue(valuesList);
        seriesTag1.setColumns(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("DS");
        valuesList.add(tags.getPixelSpacing());
        st1.setValue(valuesList);
        seriesTag1.setPixelSpacing(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("US");
        valuesList.add("8");
        st1.setValue(valuesList);
        seriesTag1.setBitsAllocated(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("US");
        valuesList.add("8");
        st1.setValue(valuesList);
        seriesTag1.setBitsStored(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("US");
        valuesList.add("7");
        st1.setValue(valuesList);
        seriesTag1.setHighBit(st1);

        //
        st1 = new StudiesEndpointModel();
        valuesList = new ArrayList(1);

        st1.setVr("LO");
        valuesList.add(String.valueOf(tags.getId()));
        st1.setValue(valuesList);
        seriesTag1.setClinicalTrialSeriesID(st1);

        return seriesTag1;
    }

    public ViewDicomTags getDicomTagsByImageDicomTags(ViewDicomTags dicomTags) {
        try {
            String tagsAsJson = dicomTags.getDicomTags();
            if (tagsAsJson != null) {
                boolean isXrayServer = JsfUtil.isXrayLocalServer();
                if (!isXrayServer) {
                    dicomTags = getTagsToShowFromXmlForCloudServer(new XmlReader().createTagList(tagsAsJson), tagsAsJson);
                } else {
                    dicomTags = getTagsToShowFromXml(new XmlReader().createTagList(tagsAsJson), tagsAsJson);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return dicomTags;
    }

    public ViewDicomTags getTagsToShowFromXml(HashMap<String, com.innotech.xraymanagerapp.model.DicomTags> tagList, String json) {
        ViewDicomTags tts = new ViewDicomTags();
        try {
            tts.setPatientId(tagList.get("00100020").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            tts.setPatientName(tagList.get("00100010").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            tts.setPatientSex(tagList.get("00100040").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientSpecie(tagList.get("00102201").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientBirthdate(JsfUtil.convertToDateFromString(tagList.get("00100030").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDate(JsfUtil.convertToDateFromString(tagList.get("00080020").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setSeriesDate(JsfUtil.convertToDateFromString(tagList.get("00080021").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDescription(tagList.get("00081030").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102297") != null) {
                if (tagList.get("00102297").getTagValue() != null) {
                    tts.setOwnerName(tagList.get("00102297").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080018") != null) {
                if (tagList.get("00080018").getTagValue() != null) {
                    tts.setImagePattern(tagList.get("00080018").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0008103E") != null) {
                if (tagList.get("0008103E").getTagValue() != null) {
                    tts.setSeriesDescription(tagList.get("0008103E").getTagValue());
                } else if (tagList.get("00181030") != null) {
                    tts.setSeriesDescription(tagList.get("00181030").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {

            tts.setInstitutionName(tagList.get("00080080").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (Objects.nonNull(tagList.get("00280030"))) {
                tts.setPixelSpacing(tagList.get("00280030").getTagValue());
            } else {
                if (Objects.nonNull(tagList.get("00181164"))) {
                    tts.setPixelSpacing(tagList.get("00181164").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setFullJsonString(json);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        return tts;
    }

    public ViewDicomTags getTagsToShowFromXmlForCloudServer(HashMap<String, com.innotech.xraymanagerapp.model.DicomTags> tagList, String json) {
        ViewDicomTags tts = new ViewDicomTags();
        try {
            tts.setPatientId(tagList.get("00100020").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientName(tagList.get("00100010").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientSex(tagList.get("00100040").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setPatientSpecie(tagList.get("00102201").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDate(JsfUtil.convertToDateFromString(tagList.get("00080020").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setSeriesDate(JsfUtil.convertToDateFromString(tagList.get("00080021").getTagValue(), "yyyyMMdd"));
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            tts.setStudyDescription(tagList.get("00081030").getTagValue());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00100030") != null) {
                if (tagList.get("00100030").getTagValue() != null) {
                    tts.setPatientBirthdate(JsfUtil.convertToDateFromString(tagList.get("00100030").getTagValue(), "yyyyMMdd"));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080050") != null) {
                tts.setPatientAccessionCode(tagList.get("00080050").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102201") != null) {
                tts.setPatientSpecie(tagList.get("00102201").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102201") != null) {
                tts.setPatientSpecie(tagList.get("00102201").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0020000D") != null) {
                tts.setStudyInstanceUID(tagList.get("0020000D").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080018") != null) {
                tts.setsOPInstanceUID(tagList.get("00080018").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0020000E") != null) {
                tts.setSeriesInstanceUID(tagList.get("0020000E").getTagValue());
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00102297") != null) {
                if (tagList.get("00102297").getTagValue() != null) {
                    tts.setOwnerName(tagList.get("00102297").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("00080018") != null) {
                if (tagList.get("00080018").getTagValue() != null) {
                    tts.setImagePattern(tagList.get("00080018").getTagValue());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (tagList.get("0008103E") != null) {
                if (tagList.get("0008103E").getTagValue() != null) {
                    tts.setSeriesDescription(tagList.get("0008103E").getTagValue());
                } else if (tagList.get("00181030") != null) {
                    tts.setSeriesDescription(tagList.get("00181030").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        try {
            if (Objects.nonNull(tagList.get("00280030"))) {
                tts.setPixelSpacing(tagList.get("00280030").getTagValue());
            } else {
                if (Objects.nonNull(tagList.get("00181164"))) {
                    tts.setPixelSpacing(tagList.get("00181164").getTagValue());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }

        try {
            if (tagList.get("00080080").getTagValue().toLowerCase().contains("cambie animal hospital") || tagList.get("00080080").getTagValue().toLowerCase().contains("anderson animal hospital")) {
                tts.setInstitutionName("Vancouver South Animal Hospital");
            } else {
                tts.setInstitutionName(tagList.get("00080080").getTagValue());
            }
            tts.setFullJsonString(json);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        return tts;
    }

    public String dicomServersList() {
        return new Gson().toJson(dicomServersController.findAll());
    }

    public String sendDicom(String imageList[], int selectedDicomServerId) {
        String resultMessages = "No dicom file has been sent";
        String imagesList = Arrays.toString(imageList);

//        System.out.println("Images list: " + imagesList);
        ejbViewerBusinessController.init();
        ejbViewerBusinessController.setImagesList(imagesList.replace("[", "").replace("]", ""));
        ejbViewerBusinessController.setSelectedDicomServer(dicomServersController.find(selectedDicomServerId));
        if (ejbViewerBusinessController.dicomSendByQueue()) {
            int count = ejbViewerBusinessController.getImagesList().length();
            resultMessages = count + " dicom files have been successfully sent to the server: " + ejbViewerBusinessController.getSelectedDicomServer().getDescription();
        }
        return resultMessages;
    }

    public String emailList(Users user) {
        return new Gson().toJson(emailController.findByStatusAndClinic(user.getClinicId().getId(), user.getId()));
    }

    public String sendEmail(String emailRequestData) {

        EmailSend send = new Gson().fromJson(emailRequestData, EmailSend.class);
        EmailConfig ec = ejbEmailFacade.find(send.getSenderId());

        String imagesList = Arrays.toString(send.getImageIdList());

        ejbViewerBusinessController.init();
        ejbViewerBusinessController.setEmailSelected(ec);
        ejbViewerBusinessController.setEmailType(send.getEmailType());
        ejbViewerBusinessController.setCustomerEmail(send.getEmailReceiver());
        ejbViewerBusinessController.setEmailBody(send.getEmailBody());
        ejbViewerBusinessController.setImagesList(imagesList.replace("[", "").replace("]", ""));

        return ejbViewerBusinessController.sendSelectedImagesEmail();
    }

    public Object[] xrayExport(String xrayExportRequestData) {

        XrayExport send = new Gson().fromJson(xrayExportRequestData, XrayExport.class);

        String imagesList = Arrays.toString(send.getImageIdList());

        ejbViewerBusinessController.init();
        ejbViewerBusinessController.setImagesList(imagesList.replace("[", "").replace("]", ""));
        ejbViewerBusinessController.setImageExportFormat(send.getImageFormat());

        Object[] imagesAsZipAndDicomTags = new Object[2];
        imagesAsZipAndDicomTags[0] = ejbViewerBusinessController.zipBytesSelectedImages();
        imagesAsZipAndDicomTags[1] = ejbViewerBusinessController.getDicomTags();
        return imagesAsZipAndDicomTags;
    }

    public String saveAnnotationsData(int studyId, String annotations, Users user) {
        ejbStudyAnnotationsFacade.setStudyId(new Studies(studyId));
        ejbStudyAnnotationsFacade.setJsonState(String.valueOf(annotations));
//        ejbStudyAnnotationsFacade.setViewportState(String.valueOf(annotations.getViewportAnnotations()));
        ejbStudyAnnotationsFacade.setUserId(user);
        if (ejbStudyAnnotationsFacade.create()) {
            return "Annotations data saved successfully";
        }
        return "Annotations did not get saved";
    }

    public String saveAnnotationsViewport(int studyId, String annotations, Users user) {
        ejbStudyAnnotationsFacade.setStudyId(new Studies(studyId));
//        ejbStudyAnnotationsFacade.setJsonState(String.valueOf(annotations.getDataAnnotations()));
        ejbStudyAnnotationsFacade.setViewportState(String.valueOf(annotations));
        ejbStudyAnnotationsFacade.setUserId(user);
        if (ejbStudyAnnotationsFacade.create()) {
            return "Annotations vewport saved successfully";
        }
        return "Annotations did not get saved";
    }

    public String deleteSelectedImages(String imageList[]) {
        String resultMessages = "No image has been deleted";
        String imagesList = Arrays.toString(imageList);

//        System.out.println("Images list to delete: " + imagesList);
        ejbViewerBusinessController.init();
        ejbViewerBusinessController.setImagesList(imagesList.replace("[", "").replace("]", ""));
        int deletedCount = ejbViewerBusinessController.deleteSelectedImages();
        if(deletedCount > 0){
            resultMessages = new StringBuilder(deletedCount).append(" images has been successfully deleted.").toString();            
        }
        return resultMessages;
    }

    public String getAnnotations(int studyId) {
        return new Gson().toJson(ejbStudyAnnotationsFacade.find(studyId));
    }

    public static void main(String[] args) {

        //Testing email send
        EmailSend send = new EmailSend();
        send.setSenderId(1);
        String[] imagesIdList = {"8680"};
        send.setImageIdList(imagesIdList);
        send.setEmailType("jpg");
        send.setEmailReceiver("alex@imixray.com");
        send.setEmailSubject("The email subject for a test");
        String emailDefaultMessage = "<p>Hello,"
                + "</p><p><br></p><p>Attached please find the requested x-rays for"
                + "<strong>Fido test patient</strong>. "
                + "Taken on <strong>September 24th, 2020</strong></p>"
                + "<p>Complete medical records to follow.</p><p><br>"
                + "</p><p>This is a test body message</p><p><br></p><p>Regards,</p><p><br>"
                + "</p><p><strong>Innotech Animal Hospital</strong></p>"
                + "<p>124 Garden Avenue, North Vancouver</p>"
                + "<p>7783170211</p><p><br></p>";
        send.setEmailBody(emailDefaultMessage);

        System.out.println(new Gson().toJson(send));
        // Testing export images
        XrayExport export = new XrayExport();
        export.setImageFormat("jpg");
        String[] imagesIdListToExport = {"8680"};
        export.setImageIdList(imagesIdList);
        System.out.println(new Gson().toJson(export));

        // Testing StudyAnnotationsModel 
        StudyAnnotationsModel annotations = new StudyAnnotationsModel();
        annotations.setStudyId(5955);
        annotations.setDataAnnotations("{\n"
                + "    \"allTools\": [\n"
                + "        {\n"
                + "            \"visible\": true,\n"
                + "            \"active\": false,\n"
                + "            \"invalidated\": false,\n"
                + "            \"handles\": {\n"
                + "                \"start\": {\n"
                + "                    \"x\": 1211.673889948382,\n"
                + "                    \"y\": 1218.7826086956522,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"end\": {\n"
                + "                    \"x\": 1423.6579106176137,\n"
                + "                    \"y\": 1388.1242236024846,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"initialRotation\": 0,\n"
                + "                \"textBox\": {\n"
                + "                    \"active\": false,\n"
                + "                    \"hasMoved\": false,\n"
                + "                    \"movesIndependently\": false,\n"
                + "                    \"drawnIndependently\": true,\n"
                + "                    \"allowedOutsideImage\": true,\n"
                + "                    \"hasBoundingBox\": true,\n"
                + "                    \"x\": 1423.6579106176137,\n"
                + "                    \"y\": 1303.4534161490683,\n"
                + "                    \"boundingBox\": {\n"
                + "                        \"width\": 128.75,\n"
                + "                        \"height\": 25,\n"
                + "                        \"left\": 493.8315430314967,\n"
                + "                        \"top\": 601.7158982204994\n"
                + "                    }\n"
                + "                }\n"
                + "            },\n"
                + "            \"uuid\": \"24473075-8ff0-4d37-ac88-5553dc1b77d9\",\n"
                + "            \"PatientID\": \"132uuy\",\n"
                + "            \"StudyInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423\",\n"
                + "            \"SeriesInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"SOPInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"frameIndex\": 0,\n"
                + "            \"imagePath\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_0\",\n"
                + "            \"lesionNamingNumber\": 1,\n"
                + "            \"userId\": null,\n"
                + "            \"toolType\": \"EllipticalRoi\",\n"
                + "            \"_id\": \"e818ab6a-543a-a520-c066-2fc0adfe486f\",\n"
                + "            \"timepointId\": \"TimepointId\",\n"
                + "            \"measurementNumber\": 4,\n"
                + "            \"cachedStats\": {\n"
                + "                \"area\": 10.333054679252408,\n"
                + "                \"count\": 28121,\n"
                + "                \"mean\": 221.9957327264322,\n"
                + "                \"variance\": 365.12227473870735,\n"
                + "                \"stdDev\": 19.10817298275027,\n"
                + "                \"min\": 205,\n"
                + "                \"max\": 255\n"
                + "            },\n"
                + "            \"unit\": \"\",\n"
                + "            \"viewport\": {\n"
                + "                \"scale\": 0.4151256293855777,\n"
                + "                \"translation\": {\n"
                + "                    \"x\": -151.1307997066968,\n"
                + "                    \"y\": -42.58586539969277\n"
                + "                },\n"
                + "                \"voi\": {\n"
                + "                    \"windowWidth\": 255,\n"
                + "                    \"windowCenter\": 127\n"
                + "                },\n"
                + "                \"invert\": false,\n"
                + "                \"pixelReplication\": false,\n"
                + "                \"rotation\": 0,\n"
                + "                \"hflip\": false,\n"
                + "                \"vflip\": false,\n"
                + "                \"labelmap\": false,\n"
                + "                \"displayedArea\": {\n"
                + "                    \"tlhc\": {\n"
                + "                        \"x\": 1,\n"
                + "                        \"y\": 1\n"
                + "                    },\n"
                + "                    \"brhc\": {\n"
                + "                        \"x\": 2816,\n"
                + "                        \"y\": 2304\n"
                + "                    },\n"
                + "                    \"rowPixelSpacing\": 0.0192582,\n"
                + "                    \"columnPixelSpacing\": 0.0190678,\n"
                + "                    \"presentationSizeMode\": \"NONE\"\n"
                + "                }\n"
                + "            }\n"
                + "        },\n"
                + "        {\n"
                + "            \"visible\": true,\n"
                + "            \"active\": false,\n"
                + "            \"invalidated\": false,\n"
                + "            \"handles\": {\n"
                + "                \"start\": {\n"
                + "                    \"x\": 1163.4957034326478,\n"
                + "                    \"y\": 1023.2049689440994,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"end\": {\n"
                + "                    \"x\": 1840.3992239787162,\n"
                + "                    \"y\": 1216.3975155279504,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"textBox\": {\n"
                + "                    \"active\": false,\n"
                + "                    \"hasMoved\": false,\n"
                + "                    \"movesIndependently\": false,\n"
                + "                    \"drawnIndependently\": true,\n"
                + "                    \"allowedOutsideImage\": true,\n"
                + "                    \"hasBoundingBox\": true,\n"
                + "                    \"x\": 1840.3992239787162,\n"
                + "                    \"y\": 1216.3975155279504,\n"
                + "                    \"boundingBox\": {\n"
                + "                        \"width\": 82.1500015258789,\n"
                + "                        \"height\": 25,\n"
                + "                        \"left\": 991.1555872674896,\n"
                + "                        \"top\": 496.78914900307905\n"
                + "                    }\n"
                + "                }\n"
                + "            },\n"
                + "            \"uuid\": \"c3b00a72-7d0d-4dc1-9415-b9b979c291a1\",\n"
                + "            \"_measurementServiceId\": \"3350ff4d-82f4-5595-1a5b-870f12b3d1af\",\n"
                + "            \"PatientID\": \"132uuy\",\n"
                + "            \"StudyInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423\",\n"
                + "            \"SeriesInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"SOPInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"frameIndex\": 0,\n"
                + "            \"imagePath\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_0\",\n"
                + "            \"lesionNamingNumber\": 2,\n"
                + "            \"userId\": null,\n"
                + "            \"toolType\": \"Length\",\n"
                + "            \"_id\": \"5ff1ddf1-02e0-6664-d52b-8ec481a08880\",\n"
                + "            \"timepointId\": \"TimepointId\",\n"
                + "            \"measurementNumber\": 1,\n"
                + "            \"length\": 13.4325963777649,\n"
                + "            \"unit\": \"mm\",\n"
                + "            \"viewport\": {\n"
                + "                \"scale\": 0.4151256293855777,\n"
                + "                \"translation\": {\n"
                + "                    \"x\": -151.1307997066968,\n"
                + "                    \"y\": -42.58586539969277\n"
                + "                },\n"
                + "                \"voi\": {\n"
                + "                    \"windowWidth\": 255,\n"
                + "                    \"windowCenter\": 127\n"
                + "                },\n"
                + "                \"invert\": false,\n"
                + "                \"pixelReplication\": false,\n"
                + "                \"rotation\": 0,\n"
                + "                \"hflip\": false,\n"
                + "                \"vflip\": false,\n"
                + "                \"labelmap\": false,\n"
                + "                \"displayedArea\": {\n"
                + "                    \"tlhc\": {\n"
                + "                        \"x\": 1,\n"
                + "                        \"y\": 1\n"
                + "                    },\n"
                + "                    \"brhc\": {\n"
                + "                        \"x\": 2816,\n"
                + "                        \"y\": 2304\n"
                + "                    },\n"
                + "                    \"rowPixelSpacing\": 0.0192582,\n"
                + "                    \"columnPixelSpacing\": 0.0190678,\n"
                + "                    \"presentationSizeMode\": \"NONE\"\n"
                + "                }\n"
                + "            }\n"
                + "        },\n"
                + "        {\n"
                + "            \"visible\": true,\n"
                + "            \"active\": false,\n"
                + "            \"invalidated\": false,\n"
                + "            \"handles\": {\n"
                + "                \"start\": {\n"
                + "                    \"x\": 1671.7755711736459,\n"
                + "                    \"y\": 1004.1242236024844,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"end\": {\n"
                + "                    \"x\": 1291.167897699344,\n"
                + "                    \"y\": 937.3416149068323,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"textBox\": {\n"
                + "                    \"active\": false,\n"
                + "                    \"hasMoved\": false,\n"
                + "                    \"movesIndependently\": false,\n"
                + "                    \"drawnIndependently\": true,\n"
                + "                    \"allowedOutsideImage\": true,\n"
                + "                    \"hasBoundingBox\": true,\n"
                + "                    \"x\": 1671.7755711736459,\n"
                + "                    \"y\": 1004.1242236024844,\n"
                + "                    \"boundingBox\": {\n"
                + "                        \"width\": 73.16666793823242,\n"
                + "                        \"height\": 25,\n"
                + "                        \"left\": 789.9262052066833,\n"
                + "                        \"top\": 240.94036324005356\n"
                + "                    }\n"
                + "                }\n"
                + "            },\n"
                + "            \"uuid\": \"1e02122a-674e-4ae4-b666-ce9e7122ebe9\",\n"
                + "            \"_measurementServiceId\": \"edee3343-d69d-7f44-2304-b6a68ef8ac96\",\n"
                + "            \"PatientID\": \"132uuy\",\n"
                + "            \"StudyInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423\",\n"
                + "            \"SeriesInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"SOPInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"frameIndex\": 0,\n"
                + "            \"imagePath\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_0\",\n"
                + "            \"lesionNamingNumber\": 3,\n"
                + "            \"userId\": null,\n"
                + "            \"toolType\": \"Length\",\n"
                + "            \"_id\": \"e8089c5d-f881-5ed7-27cb-8118afff7cc4\",\n"
                + "            \"timepointId\": \"TimepointId\",\n"
                + "            \"measurementNumber\": 2,\n"
                + "            \"length\": 7.3704294112963,\n"
                + "            \"unit\": \"mm\",\n"
                + "            \"viewport\": {\n"
                + "                \"scale\": 0.4151256293855777,\n"
                + "                \"translation\": {\n"
                + "                    \"x\": -151.1307997066968,\n"
                + "                    \"y\": -42.58586539969277\n"
                + "                },\n"
                + "                \"voi\": {\n"
                + "                    \"windowWidth\": 255,\n"
                + "                    \"windowCenter\": 127\n"
                + "                },\n"
                + "                \"invert\": false,\n"
                + "                \"pixelReplication\": false,\n"
                + "                \"rotation\": 0,\n"
                + "                \"hflip\": false,\n"
                + "                \"vflip\": false,\n"
                + "                \"labelmap\": false,\n"
                + "                \"displayedArea\": {\n"
                + "                    \"tlhc\": {\n"
                + "                        \"x\": 1,\n"
                + "                        \"y\": 1\n"
                + "                    },\n"
                + "                    \"brhc\": {\n"
                + "                        \"x\": 2816,\n"
                + "                        \"y\": 2304\n"
                + "                    },\n"
                + "                    \"rowPixelSpacing\": 0.0192582,\n"
                + "                    \"columnPixelSpacing\": 0.0190678,\n"
                + "                    \"presentationSizeMode\": \"NONE\"\n"
                + "                }\n"
                + "            }\n"
                + "        },\n"
                + "        {\n"
                + "            \"visible\": true,\n"
                + "            \"active\": false,\n"
                + "            \"invalidated\": false,\n"
                + "            \"handles\": {\n"
                + "                \"start\": {\n"
                + "                    \"x\": 1317.6659002829979,\n"
                + "                    \"y\": 1287.9503105590063,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"end\": {\n"
                + "                    \"x\": 1435.702457246547,\n"
                + "                    \"y\": 1230.7080745341616,\n"
                + "                    \"highlight\": true,\n"
                + "                    \"active\": false\n"
                + "                },\n"
                + "                \"textBox\": {\n"
                + "                    \"active\": false,\n"
                + "                    \"hasMoved\": false,\n"
                + "                    \"movesIndependently\": false,\n"
                + "                    \"drawnIndependently\": true,\n"
                + "                    \"allowedOutsideImage\": true,\n"
                + "                    \"hasBoundingBox\": true,\n"
                + "                    \"x\": 1435.702457246547,\n"
                + "                    \"y\": 1230.7080745341616,\n"
                + "                    \"boundingBox\": {\n"
                + "                        \"width\": 73.16666793823242,\n"
                + "                        \"height\": 25,\n"
                + "                        \"left\": 508.2050703215541,\n"
                + "                        \"top\": 514.0373817511481\n"
                + "                    }\n"
                + "                }\n"
                + "            },\n"
                + "            \"uuid\": \"3814dc0a-da22-4a8e-b35b-a6bf312abfc8\",\n"
                + "            \"_measurementServiceId\": \"d1c0a857-43fd-7e7b-42e0-0bf2013ebd7c\",\n"
                + "            \"PatientID\": \"132uuy\",\n"
                + "            \"StudyInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423\",\n"
                + "            \"SeriesInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"SOPInstanceUID\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706\",\n"
                + "            \"frameIndex\": 0,\n"
                + "            \"imagePath\": \"1.2.811.0.2808090.8.6.0.00210.2.4.20200902092423_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_1.2.811.0.2808090.8.6.0.00210.2.4.20200902092706_0\",\n"
                + "            \"lesionNamingNumber\": 4,\n"
                + "            \"userId\": null,\n"
                + "            \"toolType\": \"Length\",\n"
                + "            \"_id\": \"9e55ac3b-3af0-0f35-20c7-eddcd14a3ea9\",\n"
                + "            \"timepointId\": \"TimepointId\",\n"
                + "            \"measurementNumber\": 3,\n"
                + "            \"length\": 2.506169603583639,\n"
                + "            \"unit\": \"mm\",\n"
                + "            \"viewport\": {\n"
                + "                \"scale\": 0.4151256293855777,\n"
                + "                \"translation\": {\n"
                + "                    \"x\": -151.1307997066968,\n"
                + "                    \"y\": -42.58586539969277\n"
                + "                },\n"
                + "                \"voi\": {\n"
                + "                    \"windowWidth\": 255,\n"
                + "                    \"windowCenter\": 127\n"
                + "                },\n"
                + "                \"invert\": false,\n"
                + "                \"pixelReplication\": false,\n"
                + "                \"rotation\": 0,\n"
                + "                \"hflip\": false,\n"
                + "                \"vflip\": false,\n"
                + "                \"labelmap\": false,\n"
                + "                \"displayedArea\": {\n"
                + "                    \"tlhc\": {\n"
                + "                        \"x\": 1,\n"
                + "                        \"y\": 1\n"
                + "                    },\n"
                + "                    \"brhc\": {\n"
                + "                        \"x\": 2816,\n"
                + "                        \"y\": 2304\n"
                + "                    },\n"
                + "                    \"rowPixelSpacing\": 0.0192582,\n"
                + "                    \"columnPixelSpacing\": 0.0190678,\n"
                + "                    \"presentationSizeMode\": \"NONE\"\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    ]\n"
                + "}");
        annotations.setViewportAnnotations("{\"1.3.6.1.4.1.25403.345050719074.3824.20170125112931.12\":{\"scale\":12.107696062288364,\"translation\":{\"x\":-10.121853034363028,\"y\":93.64511190045181},\"voi\":{\"windowWidth\":350,\"windowCenter\":50},\"invert\":false,\"pixelReplication\":false,\"rotation\":0,\"hflip\":false,\"vflip\":false,\"labelmap\":false,\"displayedArea\":{\"tlhc\":{\"x\":1,\"y\":1},\"brhc\":{\"x\":512,\"y\":512},\"rowPixelSpacing\":2,\"columnPixelSpacing\":2,\"presentationSizeMode\":\"NONE\"}}}");

        System.out.println(new Gson().toJson(annotations));
    }
}
