/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * @date 6/21/2019
 * @author AlexcobarL
 */
@ManagedBean
@SessionScoped
public class LoginController extends com.innotech.xraymanagerapp.business.LoginBusinessController implements Serializable {

    
    @PostConstruct
    public void init() {
        setSelected();
        System.out.println("JSF Version: " + FacesContext.class.getPackage().getImplementationVersion());
    }

}
