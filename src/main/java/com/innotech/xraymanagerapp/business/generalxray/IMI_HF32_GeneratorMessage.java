/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author Alexander Escobar L.
 */
public final class IMI_HF32_GeneratorMessage implements GeneratorMessageManagerInterface, SerialPortEventListener, Serializable {

    private static SerialPort serialPort;
    private static String COM_PORT;
    private static final String DELIMITER = "\003";// ETX command delimiter for this sensor
    private static final String COMMAND_SEPARATOR_DELIMITER = "\040";// ETX command delimiter for this sensor
    private String generatorResponse = "";
    private static IMI_HF32_GeneratorMessage singletonInstance = null;
    private PropertyChangeSupport support;
    private static IMI_HF32_Response generatorResponseObject;

    // Parameter Limits
    int kvMin = 40;
    int kvMax = 125;

    double mxMin = 0.1;
    double mxMax = 8000.0;

    double msMin = 1.0;
    double msMax = 10000.0;

    double maMin = 1.0;
    double maMax = 1000.0;

    private IMI_HF32_GeneratorMessage() {
    }

    private IMI_HF32_GeneratorMessage(PropertyChangeListener generatorMessageListenerInterface, String comPort) throws SerialPortException {
        if (Objects.isNull(comPort)) {
            throw new SerialPortException("Error", "NULL", "Invalid or NULL COM Port");
        } else {
            COM_PORT = comPort;
            initializeResponseQueue();

            support = new PropertyChangeSupport(this);
            addPropertyChangeListener(generatorMessageListenerInterface);
            generatorResponseObject = new IMI_HF32_Response();
        }
    }

    @Override
    public IMI_HF32_GeneratorMessage getInstance(AbstractGeneratorMessageListener generatorMessageListenerInterface, String comPort) throws SerialPortException {
        if (Objects.isNull(singletonInstance)) {
            singletonInstance = new IMI_HF32_GeneratorMessage(generatorMessageListenerInterface, comPort);
        }
        return singletonInstance;
    }

    public static IMI_HF32_GeneratorMessage getStaticInstance(AbstractGeneratorMessageListener generatorMessageListenerInterface, String comPort) throws SerialPortException {
        if (Objects.isNull(singletonInstance)) {
            singletonInstance = new IMI_HF32_GeneratorMessage(generatorMessageListenerInterface, comPort);
        }
        return singletonInstance;
    }

    private boolean initializeSerialPort() throws SerialPortException {

        try {
            serialPort = new SerialPort(COM_PORT);
            serialPort.openPort();//Open port
            serialPort.setParams(19200, 8, 1, 0);//Set params
            addEventListener();
            return true;
        } catch (java.lang.UnsatisfiedLinkError | java.lang.NoClassDefFoundError ex) {
            Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return false;
    }

    private void initializeResponseQueue() {
//        generatorResponseList = new LinkedList();
    }

    public void processGeneratorResponse(String response) {
//        System.out.println("processing response: " + response);
        if (response.startsWith("GS")) {
            // GS WS3 ET3 KV078 MX000200 MS0020000 MA00100 FO0 FS100 FI100 FN00
            // or with power command
            // GS WS1 ET0 KV057 MX000100 MS0000500 MA02000 FO1 FS100 FI100 FN00�PW0�
            String responseList[] = response.split(" ");
            IMI_HF32_Response.setTechniqueResponse(responseList[2]);
            IMI_HF32_Response.setKvResponse(responseList[3]);
            IMI_HF32_Response.setMxResponse(responseList[4]);
            IMI_HF32_Response.setMsResponse(responseList[5]);
            IMI_HF32_Response.setMaResponse(responseList[6]);
            IMI_HF32_Response.setFocusResponse(responseList[7]);
            IMI_HF32_Response.setFsResponse(responseList[8]);
            IMI_HF32_Response.setFiResponse(responseList[9]);
            IMI_HF32_Response.setFnResponse(responseList[10]);
            if (response.contains("PW")) {
                int index = response.indexOf("PW");
                String pw = response.substring(index, index + 3);
                IMI_HF32_Response.setPowerResponse(pw);
            }
        } else if (response.startsWith("SA")) {
            // SA ET0 KV072 MX001000 MS0008000 MA01250 FO0 FS100 FI100 FN@0
            String responseList[] = response.split(" ");
            IMI_HF32_Response.setTechniqueResponse(responseList[1]);
            IMI_HF32_Response.setKvResponse(responseList[2]);
            IMI_HF32_Response.setMxResponse(responseList[3]);
            IMI_HF32_Response.setMsResponse(responseList[4]);
            IMI_HF32_Response.setMaResponse(responseList[5]);
            IMI_HF32_Response.setFocusResponse(responseList[6]);
            IMI_HF32_Response.setFsResponse(responseList[7]);
            IMI_HF32_Response.setFiResponse(responseList[8]);
            IMI_HF32_Response.setFnResponse(responseList[9]);
        } else if (response.contains("PW")) {
            IMI_HF32_Response.setPowerResponse(response);
        } else if (response.contains("KV")) {
            IMI_HF32_Response.setKvResponse(response);
        } else if (response.contains("MX")) {
            IMI_HF32_Response.setMxResponse(response);
        } else if (response.contains("MA")) {
            IMI_HF32_Response.setMaResponse(response);
        } else if (response.contains("MS")) {
            IMI_HF32_Response.setMsResponse(response);
        }
//        IMI_HF32_Response.print();
    }

    @Override
    public boolean openPort() throws SerialPortException {
        boolean isOpen;
        if (serialPort != null) {
            if (!serialPort.isOpened()) {
                isOpen = serialPort.openPort();//Open port
            } else {
                isOpen = true;
            }
        } else {
            isOpen = initializeSerialPort();
        }
        return isOpen;
    }

    @Override
    public boolean closePort() throws SerialPortException {
        serialPort.closePort();
        serialPort = null;
        return true;
    }

    @Override
    public void sendCommandToGenerator(String command) throws SerialPortException {
//      byte[] buffer = {salida.getBytes(),checksum};
//        System.out.println("Opennig Generator Port: " + COM_PORT + " - Command: " + command);
        if (command.contains("ST") || command.contains("HE")) {
//            System.out.println("Openig Generator Port: " + COM_PORT + " - Command: " + command);
        }
        if (openPort()) {
            serialPort.writeBytes(buildCommand(command).getBytes());//Write data to port
        } else {
            Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, "Port {0} is NOT Open or Available.", COM_PORT);
        }
    }

    /**
     * Adds the serial port event listener to the current COM_PORT.
     *
     * @throws SerialPortException
     */
    @Override
    public void addEventListener() throws SerialPortException {
        System.out.println("Adding event listener to COM PORT: " + COM_PORT);
        int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
        serialPort.setEventsMask(mask);//Set mask
        serialPort.addEventListener(this);//Add SerialPortEventListener
    }

    /**
     * Sends a power on request to the generator.
     *
     * @return true if the generator response is successful for power it on
     * command PW1
     */
    @Override
    public boolean openConnectionWithGenerator() {
        boolean isConnectionOpen = false;
        try {
            if (openPort()) {
                sendCommandToGenerator(IMI_HF32_Commands.POWER_STATE_COMMAND);
//                Thread.sleep(500);// waits for the generator response
                //            response = generatorResponseList.poll();
                if (isConnectionOpen = generatorResponseObject.getPowerResponse().contains(IMI_HF32_Commands.POWER_ON_COMMAND)) {
//                    System.out.println("IS ON");
                } else if (generatorResponseObject.getPowerResponse().contains(IMI_HF32_Commands.POWER_OFF_COMMAND)) {
                    sendCommandToGenerator(IMI_HF32_Commands.POWER_ON_COMMAND);
                    Thread.sleep(100);// waits for the generator response
                    //                response = generatorResponseList.poll();
                }
                if (isConnectionOpen = generatorResponseObject.getPowerResponse().contains(IMI_HF32_Commands.POWER_ON_COMMAND)) {
//                    System.out.println("NOW IS ON?: " + isConnectionOpen);
                } else {
//                    System.out.println("IS OFF: did not work :( " + isConnectionOpen);
                }
            } else {
                Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, "Port {0} is NOT Open or Available.", COM_PORT);
            }
//            closePort();
        } catch (SerialPortException | InterruptedException ex) {
            Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return isConnectionOpen;
    }

    @Override
    public String getGeneratorResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return generatorResponse;
    }

    /**
     * Builds a valid command to be sent to the high voltage generator. Based on
     * the generator documentation a valid command follows this pattern
     * <command><data><ETX><checksum>
     * Where Command: is an ASCII alphanumeric command E.g. KV+ which adds one
     * to the current KV value within the generator, Data: is an ASCII numeric
     * value E.g. 1. then command + data = KV1 EXT: ASCII end of transmission
     * delimiter (03H) Checksum: Binary 1 byte summation of all Command, Data
     * and ETX bytes
     *
     * @param command
     * @return valid command.
     */
    @Override
    public String buildCommand(String command) {
        //            String command = "PW?";
//        System.out.println("Sending Command to generator: " + command);
        String output = new StringBuilder(command).append(DELIMITER).toString();
        byte[] checksum = output.getBytes(StandardCharsets.US_ASCII);

        int sum = 0;
        for (byte c : checksum) {
            sum += c;
        }
        sum = sum % 256;
        char checksumResult = (char) sum;
//        System.out.println("the check_sum number is: " + sum + " - The Binary 1 byte summation of all command is: " + checksumResult);

        return new StringBuilder(output).append(checksumResult).toString();
    }

    /**
     * Serial Port Event listener that receives every event that the serial COM
     * port triggers.
     *
     * @param event
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
//        System.out.println("Event recived from the port: " + COM_PORT);
        if (event.isRXCHAR()) {//If data is available
            try {
//                System.out.println("Event value: " + event.getEventValue());
                byte buffer[] = serialPort.readBytes();

                if (buffer != null) {
                    int numbBytes = buffer.length;
                    generatorResponse = new StringBuilder(generatorResponse).append(new String(buffer, 0, numbBytes)).toString();
                    if (generatorResponse.contains(DELIMITER)) {
                        System.out.println("\n Buffer = " + generatorResponse);
                        String[] generatorMessages = generatorResponse.split(DELIMITER);
                        int count = 0;
                        for (String generatorMessage : generatorMessages) {
//                            System.out.println("Notifying generator response listeners: ("+generatorMessage+") - "+ count++);
                            notifyGeneratorResponse(generatorMessage);
                        }
//                        generatorResponseList.add(generatorResponse);
                        generatorResponse = "";
                    }
                }

            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
    }

    @Override
    public GeneratorCommandsInterface getCommands() {
        try {
            return IMI_HF32_Commands.class.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GeneratorResponseInterface getResponse() {
        try {
            return IMI_HF32_Response.class.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(IMI_HF32_GeneratorMessage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public final void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void notifyGeneratorResponse(String generatorResponse) {
        processGeneratorResponse(generatorResponse);
        support.firePropertyChange("generatorResponse", this.generatorResponse, generatorResponse);
        this.generatorResponse = generatorResponse;
    }

    @Override
    public void sendSetupCommandToGenerator(int et, int kv, double ma, double ms, double mx, int fo, int fs, int fi, int fn) throws SerialPortException {
        String fullCommand = buildSetupCommand(et, kv, ma, ms, mx, fo, fs, fi, fn);
        sendCommandToGenerator(fullCommand);
    }

    public String buildSetupCommand(int et, int kv, double ma, double ms, double mx, int fo, int fs, int fi, int fn) {
        String setupCommand = new StringBuilder(this.getCommands().getET_Command(String.valueOf(et)))
                .append(" ")
                .append(this.getCommands().getKV_Command(convertKv(kv)))
                .append(" ")
                .append(this.getCommands().getMX_Command(convertMx(mx)))
                .append(" ")
                .append(this.getCommands().getMS_Command(convertMs(ms)))
                .append(" ")
                .append(this.getCommands().getMA_Command(convertMa(ma)))
                .append(" ")
                .append(this.getCommands().getFO_Command(String.valueOf(fo)))
                .append(" ")
                .append(this.getCommands().getFS_Command(String.valueOf(fs)))
                .append(" ")
                .append(this.getCommands().getFI_Command(String.valueOf(fi)))
                .append(" ")
                .append(this.getCommands().getFN_Command(String.valueOf(fn))).append("0")
                .toString();
        String fullCommand = new StringBuilder(IMI_HF32_Commands.SA_COMMAND).append(" ").append(setupCommand).toString();
//        System.out.println("SET UP COMMAND: " + fullCommand);
        return fullCommand;
    }

    private String convertKv(int kv) {
        String kvString = String.valueOf(kv);
        if (kv < 100) {
            kvString = new StringBuilder("0").append(String.valueOf(kvString)).toString();
        }
        return kvString;
    }

    private String convertMx(double mx) {
        int mxValue = (int) (mx * 100);
        String mxString = String.valueOf(mxValue);

        if (mxValue >= 10000) {
            mxString = new StringBuilder("0").append(mxString).toString();
        } else if (mxValue >= 1000) {
            mxString = new StringBuilder("00").append(mxString).toString();
        } else if (mxValue >= 100) {
            mxString = new StringBuilder("000").append(mxString).toString();
        } else if (mxValue >= 10) {
            mxString = new StringBuilder("0000").append(mxString).toString();
        }

        return mxString;
    }

    private String convertMs(double ms) {
        int msValue = (int) (ms * 100);
        String msString = String.valueOf(msValue);

        if (msValue >= 100000) {
            msString = new StringBuilder("0").append(msString).toString();
        } else if (msValue >= 10000) {
            msString = new StringBuilder("00").append(msString).toString();
        } else if (msValue >= 1000) {
            msString = new StringBuilder("000").append(msString).toString();
        } else if (msValue >= 1000) {
            msString = new StringBuilder("0000").append(msString).toString();
        } else if (msValue >= 10) {
            msString = new StringBuilder("00000").append(msString).toString();
        }

        return msString;
    }

    private String convertMa(double ma) {
        int maValue = (int) (ma * 10);
        String maString = String.valueOf(maValue);

        if (maValue >= 1000) {
            maString = new StringBuilder("0").append(maString).toString();
        } else if (maValue >= 100) {
            maString = new StringBuilder("00").append(maString).toString();
        } else if (maValue >= 10) {
            maString = new StringBuilder("000").append(maString).toString();
        }

        return maString;
    }
}
