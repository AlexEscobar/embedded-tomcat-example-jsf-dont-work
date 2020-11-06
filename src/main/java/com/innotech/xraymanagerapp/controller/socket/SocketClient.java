/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.socket;

import com.innotech.xraymanagerapp.business.generalxray.FlatPanel;
import com.innotech.xraymanagerapp.business.generalxray.FlatPanelEventListener;
import com.innotech.xraymanagerapp.careray.SocketCommunicationCodes;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;

/**
 *
 * @author Alexander Escobar L.
 */
//@Singleton
public class SocketClient implements Serializable {
//
//    @Inject
//    Event<FlatPanelEventListener> events;

    private FlatPanel flatPanel;

    public static final String SERVER_ADDRESS = "localhost";
    public static int SERVER_PORT;

    private volatile boolean keepRunning;
    private String msg;

    private Socket socket = null;
    private BufferedOutputStream os = null;
    private boolean isAlive;

    private static SocketClient singletonInstance;
    private PropertyChangeSupport support;

    public SocketClient() {
        System.out.println("Initiating ApplicationScoped SocketClient instance: ");
        support = new PropertyChangeSupport(this);
//        initClient();
    }

    public SocketClient(int port) {
        SERVER_PORT = port;
        System.out.println("Initiating ApplicationScoped SocketClient instance: ");
        support = new PropertyChangeSupport(this);
//        initClient();
    }

//    public static SocketClient getSingletonInstance(int port) {
//        System.out.println("Initiating Singleton SocketClient: ");
//        if (singletonInstance == null) {
//            singletonInstance = new SocketClient(port);
//        }
//        return singletonInstance;
//    }
    public void setMessageListener(PropertyChangeListener messageListener) {
        addPropertyChangeListener(messageListener);
    }

    public boolean initClient(int port) {

        isAlive = false;
        // establish a connection 
        System.out.println("current port: " + SERVER_PORT + " - new port: " + port);
        if (SERVER_PORT == 0 || SERVER_PORT != port) {
            closeConnection();
            SERVER_PORT = port;
        }
        try {
            System.out.println("Attempting to connect to server and port: " + SERVER_ADDRESS + " - " + SERVER_PORT);
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            socket.setTcpNoDelay(true);

            // sends output to the socket 
            os = new BufferedOutputStream(socket.getOutputStream());
            isAlive = true;
            System.out.println("Connected to the C++ Server");
            ExecutorService executor = Executors.newFixedThreadPool(1);

            // execute the thread that opens the socket listener which will receive all the incoming messages.
            executor.submit(() -> {
                try {
                    receiveMessage(socket.getInputStream());
                } catch (IOException ex) {
                    Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            });
            executor.shutdown();

        } catch (UnknownHostException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        FlatPanel.initializeProperties();
        return isAlive;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean closeConnection() {

        // close the connection 
        try {
            System.out.println("Closing socket client...");
//            input.close();
            if (Objects.nonNull(os)) {
                os.close();
            }
            if (Objects.nonNull(socket)) {
                socket.close();
            }
            return true;
        } catch (IOException i) {
            System.out.println(i);
        }
        isAlive = false;
        FlatPanel.initializeProperties();
        notifyPanelState(new FlatPanel(""));
        return false;
    }

    public boolean sendMessages(String messageToSend, int port) {
        boolean success = false;
        if (socket == null || socket.isClosed()) {
            success = initClient(port);
        }
//        msg = null;
//        System.out.println("messageToSend through socket client: " + messageToSend);
        try {
            os.write(messageToSend.getBytes());
            os.flush();
            success = true;
        } catch (NullPointerException | IOException ex) {
//            FlatPanel.initializeProperties();
            isAlive = false;
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return isAlive;
    }

    public void receiveMessage(InputStream is) {
//        System.out.println("receiveMessage(): " + socket.isClosed());
        while (!socket.isClosed() && isAlive) {
            try {
                //Allocate ByteBuffer for message
                ByteBuffer bMessage = ByteBuffer.allocate(255);
                bMessage.order(ByteOrder.LITTLE_ENDIAN);
                is.read(bMessage.array(), 0, 255);
                //Convert the message to string
//                System.out.println("Received from C++ Server: " + msg);
                
                msg = new String(bMessage.array());
                if (!msg.contains("<")) {
                    FlatPanel.initializeProperties();
                    closeConnection();
                    notifyPanelState(new FlatPanel(msg));
                    break;
                }
                notifyPanelState(new FlatPanel(msg));
            } catch (IOException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                FlatPanel.initializeProperties();
                closeConnection();
                notifyPanelState(new FlatPanel(msg));
                break;
            }
        }
        System.out.println("Finishing receiveMessage() thread: " + socket.isClosed());
    }

    public String getMsg() {
        return msg;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public static void main(String args[]) {
        int port = 1224;
        DataInputStream input = new DataInputStream(System.in);
        SocketClient client = new SocketClient(port);
        client.initClient(port);
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // execute the thread that waits for radiation to hit the panel
        executor.submit(() -> {
            try {
                //            client.receiveMessages();
                client.receiveMessage(client.getSocket().getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        executor.shutdown();
        String message = "";
        while (!message.equals("exit")) {
            try {
                message = input.readLine();
//                System.out.println("message is: " + message);
                client.sendMessages(message, port);
            } catch (IOException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public final void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void notifyPanelState(FlatPanel flatPanel) {
        support.firePropertyChange("flatPanel", this.flatPanel, flatPanel);
        this.flatPanel = flatPanel;
    }

//    public FlatPanelEventListener getFlatPanelEventListener() {
//        return flatPanelEventListener;
//    }
//
//    public void setFlatPanelEventListener(FlatPanelEventListener flatPanelEventListener) {
//        this.flatPanelEventListener = flatPanelEventListener;
//    }
}
