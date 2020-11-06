/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import com.innotech.xraymanagerapp.model.HighVoltageGenerators;
import java.io.Serializable;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import jssc.SerialPortException;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateful
public class GeneratorFactory implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.HighVoltageGeneratorsFacade ejbFacade;

    private HighVoltageGenerators currentGenerator;

    public com.innotech.xraymanagerapp.dto.HighVoltageGeneratorsFacade getEjbFacade() {
        return ejbFacade;
    }

    public HighVoltageGenerators getCurrentGenerator() {
        if (Objects.isNull(currentGenerator)) {
            currentGenerator = getEjbFacade().findCurrentGenerator();
        }
        return currentGenerator;
    }

    /**
     * Creates the proper GeneratorValidator based on the current generator that
     * is being used at the clinic.
     *
     * @param generatorMessageListenerInterface
     * @return GeneratorValidator object for the current generator that is being
     * used at the clinic.
     * @throws jssc.SerialPortException
     */
    public GeneratorValidator getGeneratorValidatorByCurrentGenerator(AbstractGeneratorMessageListener generatorMessageListenerInterface) throws SerialPortException, NullPointerException {

        GeneratorMessageManagerInterface generatorMessageManager;
        // calls the first implementation of the interface if this is the case, the system use it otherwise the system will find the right
        // implementation base on the implementation model on the commands.
        generatorMessageManager = IMI_HF32_GeneratorMessage.getStaticInstance(generatorMessageListenerInterface, getCurrentGenerator().getComPort());

        // if the model on the current generatorCommands class is equals to the model on the generator configuration at the database
        // then we return the Generator Validator with the correct generator Message implementation
        if (Objects.equals(getCurrentGenerator().getModel(), generatorMessageManager.getCommands().getGeneratorModel())) {
            return GeneratorValidator.getInstance(generatorMessageListenerInterface, generatorMessageManager, getCurrentGenerator().getComPort());
        }
//        else if .....
//        Room for the implementation of others generators

        return null;
    }

    public GeneratorValidator getGeneratorValidatorByCurrentGenerator(AbstractGeneratorMessageListener generatorMessageListenerInterface, String comPort) throws SerialPortException, NullPointerException {

        GeneratorMessageManagerInterface generatorMessageManager;
        // calls the first implementation of the interface if this is the case, the system use it otherwise the system will find the right
        // implementation base on the implementation model on the commands.
        generatorMessageManager = IMI_HF32_GeneratorMessage.getStaticInstance(generatorMessageListenerInterface, comPort);

        // if the model on the current generatorCommands class is equals to the model on the generator configuration at the database
        // then we return the Generator Validator with the correct generator Message implementation
        return GeneratorValidator.getInstance(generatorMessageListenerInterface, generatorMessageManager, comPort);
//        else if .....
//        Room for the implementation of others generators

    }
}
