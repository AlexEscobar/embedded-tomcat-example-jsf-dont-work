/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.directory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages directory creation
 * 
 * @author Alexander Escobar L.
 */
public class DirectoryManager {
   
    
    /**
     * Creates a directory if does not exist
     * @param path
     * @return 
     */
    public static Path createDirectory(String path){
               
        /*
         * Use createDirectories method of Files class
         * to create a directory along with all the
         * non-existent parent directories
         */
 
        //Path of the directory
        Path dirsPath = Paths.get(path);
 
        try {
            
            Files.createDirectories(dirsPath);
            
        } catch (IOException e) {
//            e.printStackTrace();
        }
        
        
        return dirsPath;
    }
    
    public static void main(String args[]){
        createDirectory("C:/dir_1/dir_2");
    }
}
