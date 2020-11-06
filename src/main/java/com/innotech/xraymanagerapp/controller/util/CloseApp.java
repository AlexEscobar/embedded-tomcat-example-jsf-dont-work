/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import java.io.IOException;

/**
 *
 * @author Hi
 */
public class CloseApp {
    

    public synchronized static void closeApp() throws IOException, InterruptedException {

//        I don't know any other solution, apart from executing a specific Windows command like 
        Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
    }
    
}
