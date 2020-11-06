/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.socket;

//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//import javax.websocket.ContainerProvider;
//import javax.websocket.DeploymentException;
//import javax.websocket.MessageHandler;
//import javax.websocket.Session;
//import javax.websocket.WebSocketContainer;

/**
 *
 * @author PC
 */

//@Singleton
//@Startup
public class SingletonSocket {
//    private final String WS_SERVER_URL = "ws://localhost:1224"; //fictitious
//    private Session session = null;
//    private String serverMessage;
//    
//    @PostConstruct
//    public void bootstrap() {
//        WebSocketContainer webSocketContainer = null;
//        try {
//            webSocketContainer = ContainerProvider.getWebSocketContainer();
//            session = webSocketContainer.connectToServer(JEESocketClient.class, new URI(WS_SERVER_URL));
//            System.out.println("Connected to WS endpoint " + WS_SERVER_URL);
//            session.addMessageHandler(new MessageHandler.Whole<String>() {
//                @Override
//                public void onMessage(String msg) {
//                    System.out.println("Connected to the client server from JEE webSocket client: "+WS_SERVER_URL);
//                    System.out.println("Mesage received: "+msg);
//                    setServerMessage(msg);
//                }
//                
//            });
//        } catch (DeploymentException | URISyntaxException | IOException ex) {
//            Logger.getLogger(SingletonSocket.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    @PreDestroy
//    public void destroy() {
//        close();
//    }
//    private void close() {
//        try {
//            session.close();
//            System.out.println("CLOSED Connection to WS endpoint " + WS_SERVER_URL);
//        } catch (IOException ex) {
//            Logger.getLogger(SingletonSocket.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    public void sendMessageToServer(String messageToSend){
//        try {
//            session.getBasicRemote().sendText(messageToSend);
//        } catch (IOException ex) {
//            Logger.getLogger(SingletonSocket.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public Session getSession() {
//        return session;
//    }
//
//    public void setSession(Session session) {
//        this.session = session;
//    }
//
//    public String getServerMessage() {
//        return serverMessage;
//    }
//
//    public void setServerMessage(String serverMessage) {
//        this.serverMessage = serverMessage;
//    }
//    
    
}
