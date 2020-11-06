/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import com.pixelmed.dicom.TransferSyntax;

/**
 *
 * @author Hi
 */
public class DefaultTags {

    public enum acceptedSOPClassUID {
        CR_IMAGES_TORAGE("1.2.840.10008.5.1.4.1.1.1"),
        DIGITAL_XRAY_IMAGE_STORAGE_FOR_PRESENTATION("1.2.840.10008.5.1.4.1.1.1.1"),
        DIGITAL_XRAY_IMAGE_STORAGE_FOR_PROCESSING("1.2.840.10008.5.1.4.1.1.1.1.1"),
        DIGITAL_INTRAORAL_XRAY_IMAGE_STORAGE_FOR_PRESENTATION("1.2.840.10008.5.1.4.1.1.1.3"),
        DIGITAL_INTRAORAL_XRAY_IMAGE_STORAGE_FOR_PROCESSING("1.2.840.10008.5.1.4.1.1.1.3.1");

        private final String sopClassUID;

        acceptedSOPClassUID(String envUrl) {
            this.sopClassUID = envUrl;
        }

        public String getSopClassUID() {
            return sopClassUID;
        }
    }

    public static String MODALITY = "DX";
    public static String TRANSFER_SYNTAX = TransferSyntax.ExplicitVRLittleEndian;
    public static String IMPLEMENTATION_CLASS_UID = "1.3.6.1.4.1.5962.99.2";
    public static String INSTANCE_CREATOR_UID = "1.3.6.1.4.1.5962.99.3";
    public static String MANUFACTURER = "Innotech Medical Industries Corp.";
    public static String SOURCE_APPLICATION_ENTITY_TITLE = "IMIDICOMSTORAGE";
    public static String IMAGE_TYPE = "DERIVED";
    public static String IMAGE_TYPE_ = "SECONDARY";

    public static String DATE_FORMAT = "yyyyMMdd";
    public static String DATE_TIME_FORMAT = "HHmmss";
}
