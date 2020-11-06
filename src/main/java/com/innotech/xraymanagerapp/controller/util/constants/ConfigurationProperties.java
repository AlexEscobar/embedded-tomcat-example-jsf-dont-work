/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util.constants;

/**
 * This class contains the names of the configuration properties on the database
 * configuration table. With this names we can easily find the correspond value
 * by calling the Configuration map on configurationController bean
 *
 * @author Alexander Escobar L.
 */
public class ConfigurationProperties {

    public static final String XRAY_DESTINATION_PATH = "XrayDestinationPath";// XrayDestinationPath property name on the BD configuration table
    public static final String HDR_SENSOR_PATH = "HDRSensorPath";// HDRSensorPath property name on the BD configuration table
    public static final String APP_SERVER_NAME = "ServerIp";// HDRSensorPath property name on the BD configuration table
}
