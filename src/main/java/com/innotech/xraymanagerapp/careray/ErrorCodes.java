package com.innotech.xraymanagerapp.careray;

import java.io.Serializable;

/**
 *
 * @author Alexander Escobar L.
 */
public class ErrorCodes  implements Serializable{
//    0#    No Error

    public enum CRPanelError {
        ERROR_1("There was an Error (1)"),
        ERROR_50000("Not reply from the socket server"),
        ERROR_500("Cannot open the detector, make sure it is connected"),
        ERROR_501("Cannot open the detector, make sure it is connected"),
        ERROR_502("Cannot open the detector, make sure it is connected"),
        ERROR_504("Detector timeout."),
        ERROR_1000("Unable to connect detector. Possible causes include, but not limited to, employment of incompatible SDK versions, wrong IP settings, network issue"),
        ERROR_1001("Unable to disconnect detector."),
        ERROR_1002("Warning: detector already connected."),
        ERROR_1003("Warning: detector already disconnected."),
        ERROR_1004("Error reconnecting socket."),
        ERROR_1005("Error closing socket."),
        ERROR_1006("Error initializing Winsock."),
        ERROR_1007("Error creating socket."),
        ERROR_1008("Error getting socket options."),
        ERROR_1009("Error setting socket options."),
        ERROR_1010("Error sending data."),
        ERROR_1011("Error receiving data."),
        ERROR_1012("Detector disconnected."),
        ERROR_1013("Mismatched versions between detector and SDK."),
        ERROR_1014("Wrong command ID"),
        ERROR_1015("Wrong version. This error will be obsolete in the future release."),
        ERROR_1016("Wrong parameter in a command package sent from API to detector."),
        ERROR_1017("A NULL pointer must not be passed to a function. Software allocation memory failed."),
        ERROR_1018("Error reading configuration file. The file may have had an error or the file does not match with the API version."),
        ERROR_1019("Error writing configuration file. Modifying configuration files in CareRay folder failed."),
        ERROR_1020("Wrong user parameter. Passed the wrong parameters."),
        ERROR_1021("Unspecified check mode. Select the check mode as one of RAD|BINNING|PREVIEW|FLUOROSCOPY (for future version)."),
        ERROR_1022("Error loading DLL files. Dll files and CareRay folder are not in the same directory or dll files are missing."),
        ERROR_1023("Error freeing DLL files. "),
        ERROR_1024("Error creating an event."),
        ERROR_1025("Error terminating an event."),
        ERROR_1026("Mismatched function ID."),
        ERROR_1027("Error getting function address."),
        ERROR_1028("Error creating a thread for image acquisition."),
        ERROR_1029("Error creating a real-time thread."),
        ERROR_1030("Error creating a heartbeat thread."),
        ERROR_1031("Error creating a thread for offset image acquisition."),
        ERROR_1032("Error creating a thread for image calibration."),
        ERROR_1033("Error creating a logger thread."),
        ERROR_1034("Error terminating a thread."),
        ERROR_1035("Error allocating memory. Program allocation of memory failed."),
        ERROR_1036("Warning: job aborted, image acquisition in progress. Video thread is running and program is getting the image."),
        ERROR_1037("Error acquiring an image. Many reasons including network connection in abnormal state or program version error can cause this."),
        ERROR_1038("CareRay configuration directory was not found."),
        ERROR_1039("No calibration file found in the specific path, or wrong path. Detector calibration is recommended."),
        ERROR_1040("Error reading file."),
        ERROR_1041("Error writing file."),
        ERROR_1042("Error exchanging files with detector."),
        ERROR_1043("Warning: job aborted, fluoroscopic image acquisition in process."),
        ERROR_1044("Warning: job aborted, fluoroscopic image acquisition stopped."),
        ERROR_1045("Warning: job aborted, fluoroscopic image recording in process."),
        ERROR_1046("Warning: job aborted, fluoroscopic image recording stopped."),
        ERROR_1047("Index out of bounds."),
        ERROR_1048("Error acquiring fluoroscopic images."),
        ERROR_1049("Error recording fluoroscopic images."),
        ERROR_1050("Error receiving image header."),
        ERROR_1051("Detector does not support the select mode. Select the check mode as one of RAD|BINNING|PREVIEW|FLUOROSCOPY (for future version)."),
        ERROR_1052("Unspecified fluoroscopy mode. Fluoroscopy mode parameter had not been set."),
        ERROR_1053("Warning: job aborted, calibration in process."),
        ERROR_1054("Warning: job aborted, the thread for offset image acquisition in process."),
        ERROR_1055("Warning: job aborted, the thread for offset image acquisition stopped. This warning will be obsolete in a future release."),
        ERROR_1056("Error: calibration process interrupted."),
        ERROR_1057("String or array must not be empty."),
        ERROR_1058("Unspecified gain image. This error will be obsolete in a future release."),
        ERROR_1059("No machine ID found in the detector..."),
        ERROR_1060("Unqualified mean value of the image. This error will be obsolete in a future release."),
        ERROR_1061("Unqualified standard deviation of the image."),
        ERROR_1062("Unqualified image uniformity."),
        ERROR_1063("Unqualified mean values of the image strips."),
        ERROR_1064("Unqualified row correlated noise (RCN) of the image."),
        ERROR_1065("Unqualified image linearity."),
        ERROR_1066("Unqualified mean value of the image. This error will be obsolete in a future release."),
        ERROR_1067("Unsynchronized detector. This error will be obsolete in a future release."),
        ERROR_1068("Part of the detector panel does not receive X-rays. Make sure the tube, the collimator, and the detector are properly aligned and the generated X-rays illuminate the entire panel."),
        ERROR_1069("Tube misaligned."),
        ERROR_1070("Offset calibration requires more than 2 (inclusive) offset images."),
        ERROR_1071("Gain calibration requires a minimum of 5 (inclusive) different doses and a maximum number of 9 (inclusive)."),
        ERROR_1072("Gain calibration requires acquiring more than 2 (inclusive) images at each dose."),
        ERROR_1073("Wrong string for setting portable kV."),
        ERROR_1074("Information of the bad pixels does not pair with the detector. Bad pixels are not obtained from this detector. Machine ID error when adding bad pixels."),
        ERROR_1075("A newer calibration file on the detector must not be overwritten with an older version. Calibration files in detector is newer."),
        ERROR_1076("Calibration files does not pair with the detector. The files were not generated by the current detector."),
        ERROR_1077("No calibration files exist on the PC and file download from the detector also failed. Open the detector with NDT to obtain the calibration files."),
        ERROR_1078("Error creating AEC acquisition thread."),
        ERROR_1079("One key fit result unacceptable."),
        ERROR_1080("Error creating monitor thread. "),
        ERROR_1081("Error creating clear image thread."),
        ERROR_1082("Auto Sync input dark image error."),
        ERROR_1083("Auto Sync input images sequence error."),
        ERROR_1084("Auto Sync input images error, can't find reasonable bad indexes. "),
        ERROR_1085("Auto Sync boundary indexes error, can't find reasonable boundary indexes."),
        ERROR_1086("The order to do polyfit should not be bigger than 6."),
        ERROR_1087("The intensity of X-rays received by detector is not uniform enough."),
        ERROR_1088("The image SNR of one key calibration is low! Please check (1) the detector, (2) the shutter shadow, and (3) the uniformity of exposure."),
        ERROR_1089("Cannot find an average dark / offset image that corresponds to the exposure time currently employed. In order to improve the quality of the resulting RAD image, it is highly recommended to walk through the calibration process to obtain an average dark / offset image with current exposure time. If you decide to skip the calibration step, the average dark / offset image with the default 500 ms exposure time will be used instead, and the image quality is expected to be degraded."),
        ERROR_1090("The effect of calibration is excellent."),
        ERROR_1091("The effect of calibration is good."),
        ERROR_1092("The effect of calibration is not good enough. It is recommended to do the calibration again with the condition suggested."),
        ERROR_1093("Failed to load calibration files generated by one shot calibration. It is strongly recommended to perform one shot calibration before acquisition."),
        ERROR_1094("The image was false triggered and useless.");

        private final String errorDescription;

        CRPanelError(String levelCode) {
            this.errorDescription = levelCode;
        }

        public String getErrorDescription() {
            return this.errorDescription;
        }
    }

}
