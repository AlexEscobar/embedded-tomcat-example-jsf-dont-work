package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.business.generalxray.AbstractGeneratorMessageListener;
import com.innotech.xraymanagerapp.business.generalxray.GeneratorValidator;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.GeneratorParameters;
import com.innotech.xraymanagerapp.model.GeneratorSpeciesBySizeConfig;
import com.innotech.xraymanagerapp.model.GeneratorSpeciesByThicknessConfig;
import com.innotech.xraymanagerapp.model.HighVoltageGenerators;
import java.beans.PropertyChangeEvent;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import jssc.SerialPortException;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

@Named("generatorController")
@ViewScoped
public class GeneratorController extends AbstractGeneratorMessageListener implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.business.generalxray.GeneratorFactory generatorFactory;
    @EJB
    private com.innotech.xraymanagerapp.dto.GeneratorSpeciesBySizeConfigFacade generatorBySizeConfigEjbFacade;

    @EJB
    private com.innotech.xraymanagerapp.dto.GeneratorSpeciesByThicknessConfigFacade generatorByThicknessConfigFacade;

    @Inject
    @Push
    private PushContext generatorMessageChannel;

    private GeneratorValidator validator;
    protected boolean IS_GENERATOR_CONNECTED;// = ConfigurationBusinessController.IS_GENERATOR_CONNECTED;// whether the system is working with a high frecuency generator or not

    // current values on generator
    private Integer kv = 0;
    private Double mx = 0.0;
    private Double ma = 0.0;
    private Double ms = 0.0;
    private Integer et = 0;
    private Integer fo = 0;
    private Integer fs = 0;
    private Integer fi = 0;
    private Integer fn = 0;
    private String st = "";//status of the generator, see device documentation for extended information
    private String he = "";//status of the generator, see device documentation for extended information
    private Boolean pw = false;
    private boolean isGeneratorOn;
    private boolean isGeneratorPhysicallyConnected;
    private int generatorStatus;//0 to 11. 7 = error phase, 2 = standby phase

    // values to send to the generator    
    private Integer kvToSend;
    private Double mxToSend;
    private Double maToSend;
    private Double msToSend;
    private Integer etToSend;
    private Integer foToSend;
    private static GeneratorParameters gp;
    private boolean keepSendingCommands;
    private static LocalDateTime firstTimeStampSeconds = LocalDateTime.now();
    private static LocalDateTime secondTimeStampSeconds = LocalDateTime.now();

    public GeneratorController() {
    }

    @PostConstruct
    public void init() {
        try {
            this.gp = new GeneratorParameters();
            keepSendingCommands = true;
            IS_GENERATOR_CONNECTED = JsfUtil.getIsGeneratorConnected();

            isGeneratorPhysicallyConnected = true;
            validator = getGeneratorFactory().getGeneratorValidatorByCurrentGenerator(this);
            if (IS_GENERATOR_CONNECTED) {
                setUpGenerator();
                System.out.println("Executor 1 starting");
                ExecutorService executor = Executors.newFixedThreadPool(1);

                executor.execute(() -> {
                    getCurentPhaseState();
                    sendHECommand();
                    performSecurityChecksEveryXSeconds();
                });
                executor.shutdown();
            }
        } catch (SerialPortException | NullPointerException ex) {
            Logger.getLogger(GeneratorController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void predestroy() {
        keepSendingCommands = false;
        initializeVariables();
    }

    private void performSecurityChecksEveryXSeconds() {
        while (keepSendingCommands) {
            try {
                sentHECommand();
                generatorSecurityChecks();
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GeneratorController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    private void sentHECommand() {
        if (isGeneratorPhysicallyConnected) {
            try {
                sendHECommand();
                firstTimeStampSeconds = LocalDateTime.now();
                Thread.sleep(3000);
                Duration duration = Duration.between(firstTimeStampSeconds, secondTimeStampSeconds);
                long difference = Math.abs(duration.toMillis());

//                System.out.println("The time difference is: " + difference + "\n" + firstTimeStampSeconds + " - " + secondTimeStampSeconds);
                if (difference > 7000) {
                    String message = "Error: No response received from generator. check COM connection and power...";
                    gp.setHe(message);
                    if (isGeneratorPhysicallyConnected) {
                        sendMessage(gp);
                    }
                    isGeneratorPhysicallyConnected = false;
                    Logger.getLogger(GeneratorController.class.getName()).log(Level.SEVERE, message);
                }
                if (generatorStatus == 7) {
                    if (he.equals("")) {
                        System.out.println("isGeneratorPhysicallyConnected is going to be false because it is empty: ");
                        gp.setHe("Error: Generator disconnected. Possible reasons are: \n1- Generator disconnected from COM Port, \n2- Generator is off or \n3- Generator disconnected from power supply");
                        if (isGeneratorPhysicallyConnected) {
                            sendMessage(gp);
                        }
                        isGeneratorPhysicallyConnected = false;

                        Logger.getLogger(GeneratorController.class.getName()).log(Level.SEVERE, "Generator got disconnected");
//                        break;
                    } else {
                        System.out.println("isGeneratorPhysicallyConnected is true");
                        isGeneratorPhysicallyConnected = true;
                    }
                }
//                System.out.println("Current HE3: " + he);
            } catch (InterruptedException ex) {
                Logger.getLogger(GeneratorController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
//        System.out.println("Current HE3 breaking Sent HE Command...: " + he);
    }

    public void initializeLocalVariables() {
        System.out.println("initializing local variables");
        kv = 0;
        mx = 0.0;
        ma = 0.0;
        ms = 0.0;
        et = 0;
        fo = 0;
        fs = 0;
        fi = 0;
        fn = 0;
        st = "";//status of the generator, see device documentation for extended information
        he = "";//status of the generator, see device documentation for extended information
        pw = false;
        isGeneratorOn = false;
        kvToSend = -1;
        mxToSend = 0.0;
        maToSend = 0.0;
        msToSend = 0.0;
//        etToSend = 0;
//        foToSend = 0;

        gp = new GeneratorParameters(pw, kv, ma, ms, mx, fo, he);

    }

    public void initializeVariables() {
        initializeLocalVariables();
        sendMessage(gp);
        isGeneratorPhysicallyConnected = true;
//        generatorStatus = 0;//0 to 11. 7 = error phase, 2 = standby phase
    }

    public void sendMessage(GeneratorParameters message) {
        generatorMessageChannel.send(message);
    }

    //***************** Generator securiry checks ***********************//
    /**
     * Initializes the generator
     */
    public void setUpGenerator() {
        turnOnGenerator();
    }

    public void generatorSecurityChecks() {
        try {
//            if (checkComPortConnection()) {
            getCurentState();
            sendPowerStateCommand();
//            }
        } catch (javax.ejb.EJBException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public boolean checkComPortConnection() {
        try {
            if (validator.checkComPortConnection()) {
                return true;
            }
        } catch (javax.ejb.EJBException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    public boolean turnOnGenerator() {
        try {
            if (IS_GENERATOR_CONNECTED) {
                if (validator.turnOnGeneratorDevice()) {
                    System.out.println("GeneratorController: Device is ON");
                    return true;
                } else {
                    System.out.println("GeneratorController Device is OFF");
                }
            }
        } catch (javax.ejb.EJBException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    public boolean turnOffGenerator() {
        try {
            if (validator.turnOffGeneratorDevice()) {
//                System.out.println("Generator Device is OFF");
                return true;
            } else {
//                System.out.println("Generator Device is ONN");
            }
        } catch (javax.ejb.EJBException | SerialPortException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    public void getCurentState() {
        try {
            validator.sendCurrentGeneratorStateCommand();
            validator.getCurrentGeneratorState();
//            System.out.println("Current state: " + validator.getCurrentGeneratorState());
        } catch (javax.ejb.EJBException | SerialPortException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void getCurentPhaseState() {
        try {
            validator.sendPhaseStatusCommand();
            Thread.sleep(50);
            System.out.println("current phase:" + st);
        } catch (javax.ejb.EJBException | SerialPortException | InterruptedException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void sendHECommand() {
        try {
            validator.sendTubeAnodeHeatCommand();
        } catch (javax.ejb.EJBException | SerialPortException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void sendPowerStateCommand() {
        try {
            validator.sendPowerStateCommand();
        } catch (javax.ejb.EJBException | SerialPortException | NullPointerException ex) {
            Logger.getLogger(LoginController.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void sendSmallAnimals() {
        // T0 KV060 MX000800 MS0008000 MA01000 FO0 FS100 FI100 FN00
        // SA ET0 KV072 MX001000 MS0008000 MA01250 FO0 FS100 FI100 FN@0 
        // ET0 KV088 MX001250 MS0010000 MA01250 FO1 FS100 FI100 FN00
        kvToSend = 60;
        maToSend = 100.0;
        msToSend = 80.0;
        mxToSend = 8.0;
        fo = 0;
        fs = 100;
        fi = 100;
        fn = 00;
        et = 0;
        sendValuesToGenerator();
    }

    public void sendMediumAnimals() {
        // T0 KV060 MX000800 MS0008000 MA01000 FO0 FS100 FI100 FN00
        // SA ET0 KV072 MX001000 MS0008000 MA01250 FO0 FS100 FI100 FN@0 
        // ET0 KV088 MX001250 MS0010000 MA01250 FO1 FS100 FI100 FN00
        kvToSend = 72;
        maToSend = 125.0;
        msToSend = 80.0;
        mxToSend = 10.0;
        fo = 0;
        fs = 100;
        fi = 100;
        fn = 00;
        et = 0;
        sendValuesToGenerator();

    }

    public void sendLargeAnimals() {
        // T0 KV060 MX000800 MS0008000 MA01000 FO0 FS100 FI100 FN00
        // SA ET0 KV072 MX001000 MS0008000 MA01250 FO0 FS100 FI100 FN@0 
        // ET0 KV088 MX001250 MS0010000 MA01250 FO1 FS100 FI100 FN00
        // SA ET0 KV60 MA1000.0 MS8000.0 MX FO0 FS100 FI100 FI0
        kvToSend = 88;
        maToSend = 125.0;
        msToSend = 100.0;
        mxToSend = 12.5;
        fo = 1;
        fs = 100;
        fi = 100;
        fn = 00;
        et = 0;
        sendValuesToGenerator();

    }

    public void sendValuesToGenerator() {
        try {
            validator.sendSetupCommand(et, kvToSend, maToSend, msToSend, mxToSend, fo, fs, fi, fn);
        } catch (SerialPortException ex) {
            Logger.getLogger(GeneratorController.class
                    .getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setCurrentValues(GeneratorParameters generatorResponse) throws NumberFormatException, NullPointerException {
        pw = generatorResponse.isPw();
        kv = generatorResponse.getKv();
        ma = generatorResponse.getMa();
        ms = generatorResponse.getMs();
        mx = generatorResponse.getMx();
        fo = generatorResponse.getFo();
        he = generatorResponse.getHe();
        st = generatorResponse.getSt();
//        System.out.println("current Values, ST:" + st + " - HE:" + he + " - KV:" + kv + " - PW:" + pw);
    }

    public GeneratorParameters getGeneratorParameters(String generatorResponse) {
        try {
            generatorResponse = generatorResponse.replace("�", "");
//        Buffer = ET0 KV060 MX000800 MS0008000 MA01000 FO0 FS100 FI100 FN00J]]
//         Buffer = GS WS0 ET0 KV060 MX000800 MS0008000 MA01000 FO0 FS100 FI100 FN00�PW1�]]

//            if (generatorResponse.contains("ER1") || generatorResponse.contains("EL0") || generatorResponse.contains("EI4")) {
            if (generatorResponse.contains("ER1")) {//MS0016000 ER125
                gp.setErrorER(generatorResponse);
            } else {
                gp.setErrorER(null);
            }

            if (generatorResponse.contains("EL0")) {
                gp.setErrorEL(generatorResponse);
                JsfUtil.addErrorMessage("Generator response with an Error: " + generatorResponse);
            } else {
                gp.setErrorEL(null);
            }

            if (generatorResponse.contains("EI4")) {
                gp.setErrorEI(generatorResponse);
            } else {
                gp.setErrorEI(null);
            }

            if (generatorResponse.contains("HE")) {
                String status = extractParameterAndValue(generatorResponse, "HE", 5);
                gp.setHe(status);
            }

            if (generatorResponse.contains("ST001") || generatorResponse.contains("ST002") || generatorResponse.contains("ST003")
                    || generatorResponse.contains("ST004") || generatorResponse.contains("ST005") || generatorResponse.contains("ST006") || generatorResponse.contains("ST007")
                    || generatorResponse.contains("ST008") || generatorResponse.contains("ST009") || generatorResponse.contains("ST010") || generatorResponse.contains("ST011")) {
                String status = extractParameterAndValue(generatorResponse, "ST", 5);
                generatorStatus = Integer.parseInt(status.replace("ST", ""));
                gp.setSt(status);
            }

            if (generatorResponse.startsWith("ET")) {
                // SA ET0 KV072 MX001000 MS0008000 MA01250 FO0 FS100 FI100 FN@0
                String responseList[] = generatorResponse.split(" ");
                gp.setKv(Integer.parseInt(responseList[1].replace(validator.getGeneratorMessageManager().getCommands().getKV_Command(""), "")));
                gp.setMx(Double.parseDouble(responseList[2].replace(validator.getGeneratorMessageManager().getCommands().getMX_Command(""), "")) / 100.0);
                gp.setMs(Double.parseDouble(responseList[3].replace(validator.getGeneratorMessageManager().getCommands().getMS_Command(""), "")) / 100.0);
                gp.setMa(Double.parseDouble(responseList[4].replace(validator.getGeneratorMessageManager().getCommands().getMA_Command(""), "")) / 10.0);
                gp.setFo(Integer.parseInt(responseList[5].replace(validator.getGeneratorMessageManager().getCommands().getFO_Command(""), "")) / 100);

            } else if (generatorResponse.startsWith("GS")) {
                // GS WS3 ET3 KV078 MX000200 MS0020000 MA00100 FO0 FS100 FI100 FN00
                // or with power command
                // GS WS1 ET0 KV057 MX000100 MS0000500 MA02000 FO1 FS100 FI100 FN00�PW0�
                String responseList[] = generatorResponse.split(" ");
                gp.setKv(Integer.parseInt(responseList[3].replace(validator.getGeneratorMessageManager().getCommands().getKV_Command(""), "")));
                gp.setMx(Double.parseDouble(responseList[4].replace(validator.getGeneratorMessageManager().getCommands().getMX_Command(""), "")) / 100.0);
                gp.setMs(Double.parseDouble(responseList[5].replace(validator.getGeneratorMessageManager().getCommands().getMS_Command(""), "")) / 100.0);
                gp.setMa(Double.parseDouble(responseList[6].replace(validator.getGeneratorMessageManager().getCommands().getMA_Command(""), "")) / 10.0);
                gp.setFo(Integer.parseInt(responseList[7].replace(validator.getGeneratorMessageManager().getCommands().getFO_Command(""), "")) / 100);
                if (generatorResponse.contains("PW")) {
                    String pwResponse = extractParameterAndValue(generatorResponse, "PW", 3);
                    System.out.println("PW:::RESPONSE: " + pwResponse);
                    gp.setPw(pwResponse.contains("PW1"));
                }
            } else {

                if (generatorResponse.contains("PW") || generatorResponse.contains("W1")) {
                    if (generatorResponse.contains("W1")) {
                        gp.setPw(true);
                    } else {
                        String response = extractParameterAndValue(generatorResponse, "PW", 3);
                        gp.setPw(response.contains("PW1"));
                    }
                } else if (generatorResponse.contains("W0")) {
                    gp.setPw(false);
                } else {
                    if (generatorResponse.contains("KV")) {
                        String response = extractParameterAndValue(generatorResponse, "KV", 5);
                        gp.setKv(Integer.parseInt(response.replace(validator.getGeneratorMessageManager().getCommands().getKV_Command(""), "")));
                    }
                    if (generatorResponse.contains("MA")) {
                        String response = extractParameterAndValue(generatorResponse, "MA", 7);
                        gp.setMa(Double.parseDouble(response.replace(validator.getGeneratorMessageManager().getCommands().getMA_Command(""), "")) / 10.0);
                    }
                    if (generatorResponse.contains("MS")) {
                        String response = extractParameterAndValue(generatorResponse, "MS", 9);
                        gp.setMs(Double.parseDouble(response.replace(validator.getGeneratorMessageManager().getCommands().getMS_Command(""), "")) / 100.0);
                    }
                    if (generatorResponse.contains("MX")) {
                        String response = extractParameterAndValue(generatorResponse, "MX", 8);
                        gp.setMx(Double.parseDouble(response.replace(validator.getGeneratorMessageManager().getCommands().getMX_Command(""), "")) / 100.0);
                    }
                }
            }

        } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e) {
            Logger.getLogger(GeneratorController.class
                    .getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return gp;
    }

    public String extractParameterAndValue(String generatorResponse, String parameterName, int valueLengh) throws IndexOutOfBoundsException {
//        System.out.println("Lengh: " + valueLengh);
        int index = generatorResponse.indexOf(parameterName);
        return generatorResponse.substring(index, index + valueLengh);
    }
    //***************** End of Generator securiry checks ***********************//

    //***************** Setup Generator Technique ******************************//
    public HighVoltageGenerators getGeneratorConfiguration() {
        return getGeneratorFactory().getCurrentGenerator();
    }

    public GeneratorSpeciesBySizeConfig getGeneratorSpeciesBySizeConfigController(
            Integer generatorId, Integer speciesId, Integer animalSizeId, Integer bodyPartId,
            Integer bodyPartViewId) {
        try {
            return generatorBySizeConfigEjbFacade.findGeneratorConfiguratorBySize(generatorId, speciesId, animalSizeId, bodyPartId, bodyPartViewId);
        } catch (NullPointerException e) {
            Logger.getLogger(GeneratorController.class
                    .getName()).log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public GeneratorSpeciesByThicknessConfig getGeneratorSpeciesByThicknessConfigController(
            Integer generatorId, Integer bodyPartId, Integer bodyPartViewId, Integer technique, Integer thickness) throws NullPointerException {
        try {
            return generatorByThicknessConfigFacade.findGeneratorConfiguratorByThickness(generatorId, bodyPartId, bodyPartViewId, technique, thickness);
        } catch (NullPointerException e) {
            Logger.getLogger(GeneratorController.class
                    .getName()).log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    private void setupGeneratorTechnique(int kv_, double ma_, double ms_, double mx_, int fo_, int fs_, int fi_, int fn_, int et_) {

        kvToSend = kv_;
        maToSend = ma_;
        msToSend = ms_;
        mxToSend = mx_;
        fo = fo_;
        fs = fs_;
        fi = fi_;
        fn = fn_;
        et = et_;

        sendValuesToGenerator();
    }

    public void updateGeneratorTechniqueBySize(Integer speciesId, Integer animalSizeId, Integer bodyPartId,
            Integer bodyPartViewId, Integer technique) throws NullPointerException {
        if (Objects.nonNull(gp)) {
            if (gp.getKv() > 0) {
                GeneratorSpeciesBySizeConfig generatorConfig = getGeneratorSpeciesBySizeConfigController(
                        getGeneratorConfiguration().getId(), speciesId, animalSizeId, bodyPartId, bodyPartViewId);

                generatorConfig.setKv(gp.getKv());
                generatorConfig.setMa(gp.getMa());
                generatorConfig.setMs(gp.getMs());
                generatorConfig.setMx(gp.getMx());
                generatorConfig.setEt(et);

                GeneratorSpeciesBySizeConfigController generatorSpeciesBySizeConfigController = new GeneratorSpeciesBySizeConfigController();
                generatorSpeciesBySizeConfigController.setFacade(generatorBySizeConfigEjbFacade);
                generatorSpeciesBySizeConfigController.setSelected(generatorConfig);
                generatorSpeciesBySizeConfigController.update();
                return;
            }
        }
        JsfUtil.addErrorMessage("Error", "New values for the current technique could not be updated...");
    }

    public void setupGeneratorTechniqueBySize(Integer speciesId, Integer animalSizeId, Integer bodyPartId,
            Integer bodyPartViewId) throws NullPointerException {

        GeneratorSpeciesBySizeConfig generatorConfig = getGeneratorSpeciesBySizeConfigController(
                getGeneratorConfiguration().getId(), speciesId, animalSizeId, bodyPartId, bodyPartViewId);

        setupGeneratorTechnique(
                generatorConfig.getKv(), generatorConfig.getMa(), generatorConfig.getMs(),
                generatorConfig.getMx(), generatorConfig.getFo(), generatorConfig.getFs(),
                generatorConfig.getFi(), generatorConfig.getFn(), generatorConfig.getEt());

    }

    public void setupGeneratorTechniqueByThickness(Integer bodyPartId,
            Integer bodyPartViewId, Integer technique, Integer thickness) throws NullPointerException {

        GeneratorSpeciesByThicknessConfig generatorConfig = getGeneratorSpeciesByThicknessConfigController(
                getGeneratorConfiguration().getId(), bodyPartId, bodyPartViewId, technique, thickness);

        setupGeneratorTechnique(
                generatorConfig.getKv(), generatorConfig.getMa(), generatorConfig.getMs(),
                generatorConfig.getMx(), generatorConfig.getFo(), generatorConfig.getFs(),
                generatorConfig.getFi(), generatorConfig.getFn(), generatorConfig.getEt());
    }

    public void sendKvPlusCommand() throws SerialPortException {
        kvToSend = kv + 1;
        validator.sendKvPlusCommand();
    }

    public void sendKvMinusCommand() throws SerialPortException {
        kvToSend = kv - 1;
        validator.sendKvMinusCommand();
        getCurentState();
    }

    public void sendMAPlusCommand() throws SerialPortException {
        maToSend = ma + 1;
        validator.sendMAPlusCommand();
        getCurentState();
    }

    public void sendMAMinusCommand() throws SerialPortException {
        maToSend = ma - 1;
        validator.sendMAMinusCommand();
        getCurentState();
    }

    public void sendMSPlusCommand() throws SerialPortException {
        msToSend = ms + 1;
        validator.sendMSPlusCommand();
        getCurentState();
    }

    public void sendMSMinusCommand() throws SerialPortException {
        msToSend = ms - 1;
        validator.sendMSMinusCommand();
        getCurentState();
    }

    public void sendMXPlusCommand() throws SerialPortException {
        mxToSend = mx + 1;
        validator.sendMXPlusCommand();
        getCurentState();
    }

    public void sendMXMinusCommand() throws SerialPortException {
        mxToSend = mx - 1;
        validator.sendMXMinusCommand();
        getCurentState();
    }

    public void sendTechniqueCommand() throws SerialPortException {
        validator.sendTechniqueCommand(et);
        getCurentState();
    }

    //***************** End Setup Generator Technique ***************************//
    //***************** Start Compare generator sent and got Technique ***************************//
    /**
     * Compares the technique values sent to the generator with the generator
     * response values.
     *
     * @return true if the sent values are equal than the response, false
     * otherwise.
     */
    public boolean compareSentAndResponseTechniques() {
//        System.out.println("Comparing: gp.getKv(): " + gp.getKv() + " - KV: " + kv);
//        System.out.println("KV: " + gp.getKv() + " = " + kvToSend);
//        System.out.println("MA: " + ma + " = " + maToSend);
//        System.out.println("MS: " + ms + " = " + msToSend);
//        System.out.println("MX: " + mx + " = " + mxToSend);
        return Objects.equals(gp.getKv(), kvToSend); //                && Objects.equals(ma, maToSend)
        //                && Objects.equals(ms, msToSend)
        //                && Objects.equals(mx, mxToSend) //                && Objects.equals(fo, foToSend)
        //                && Objects.equals(et, etToSend)
    }

    //***************** End Compare generator sent and got Technique ***************************//
    public com.innotech.xraymanagerapp.business.generalxray.GeneratorFactory getGeneratorFactory() {
        return generatorFactory;
    }

    public Integer getKv() {
        return kv;
    }

    public void setKv(Integer kv) {
        this.kv = kv;
    }

    public Double getMx() {
        return mx;
    }

    public void setMx(Double mx) {
        this.mx = mx;
    }

    public Double getMa() {
        return ma;
    }

    public void setMa(Double ma) {
        this.ma = ma;
    }

    public Double getMs() {
        return ms;
    }

    public void setMs(Double ms) {
        this.ms = ms;
    }

    public Integer getEt() {
        return et;
    }

    public void setEt(Integer et) {
        this.et = et;
    }

    public Integer getFo() {
        return fo;
    }

    public void setFo(Integer fo) {
        this.fo = fo;
    }

    public Integer getFs() {
        return fs;
    }

    public void setFs(Integer fs) {
        this.fs = fs;
    }

    public Integer getFi() {
        return fi;
    }

    public void setFi(Integer fi) {
        this.fi = fi;
    }

    public Integer getFn() {
        return fn;
    }

    public void setFn(Integer fn) {
        this.fn = fn;
    }

    public Boolean getPw() {
//        setCurrentValues();
        return pw;
    }

    public void setPw(Boolean pw) {
        this.pw = pw;
    }

    public boolean isIsGeneratorOn() {
//        setCurrentValues();
        turnOnGenerator();
        return isGeneratorOn;
    }

    public void setIsGeneratorOn(boolean isGeneratorOn) {
        this.isGeneratorOn = isGeneratorOn;
    }

    public GeneratorValidator getValidator() {
        return validator;
    }

    public void setValidator(GeneratorValidator validator) {
        this.validator = validator;
    }

    public Integer getKvToSend() {
        return kvToSend;
    }

    public void setKvToSend(Integer kvToSend) {
        this.kvToSend = kvToSend;
    }

    public Double getMxToSend() {
        return mxToSend;
    }

    public void setMxToSend(Double mxToSend) {
        this.mxToSend = mxToSend;
    }

    public Double getMaToSend() {
        return maToSend;
    }

    public void setMaToSend(Double maToSend) {
        this.maToSend = maToSend;
    }

    public Double getMsToSend() {
        return msToSend;
    }

    public void setMsToSend(Double msToSend) {
        this.msToSend = msToSend;
    }

    public Integer getEtToSend() {
        return etToSend;
    }

    public void setEtToSend(Integer etToSend) {
        this.etToSend = etToSend;
    }

    public Integer getFoToSend() {
        return foToSend;
    }

    public void setFoToSend(Integer foToSend) {
        this.foToSend = foToSend;
    }

    public synchronized boolean isIsGeneratorPhysicallyConnected() {
        return isGeneratorPhysicallyConnected;
    }

    public void setIsGeneratorPhysicallyConnected(boolean isGeneratorPhysicallyConnected) {
        this.isGeneratorPhysicallyConnected = isGeneratorPhysicallyConnected;
    }

    public static GeneratorParameters getGp() {
        return gp;
    }

    public static void setGp(GeneratorParameters gp) {
        GeneratorController.gp = gp;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        secondTimeStampSeconds = LocalDateTime.now();
        String generatorResponse = (String) evt.getNewValue();
//        System.out.println("propertyChangeListener notification: \n" + generatorResponse);
        setCurrentValues(getGeneratorParameters(generatorResponse));
        sendMessage(gp);
    }
}
