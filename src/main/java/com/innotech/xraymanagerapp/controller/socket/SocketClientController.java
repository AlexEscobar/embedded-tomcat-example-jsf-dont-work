/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.socket;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Alexander Escobar L.
 */
@Named("socketClientController")
@SessionScoped
@Startup
public class SocketClientController implements Serializable{
    
//    @EJB
//    SocketClient client;
    
    @PostConstruct
    public void init(){
//        client.initClient(1224);
    }
    
    @PreDestroy
    public void predestroy(){
//        client.closeConnection();
    }
    
    public SocketClient getSocketClient(){
//        return client;
        return null;
    }
    
}
