/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Date September 23rd, 2020
 * @author Alexander Escobar L.
 */
public class AuthenticationToken {

    public static String createToken(String u, String p) {
        try {
            return AuthenticationUtils.encodeSHA256(new StringBuilder(u).append('.').append(p).append(".").append(getTokenExpirationDate()).toString());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(AuthenticationToken.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date getTokenExpirationDate() {
        return addDaysToDate(new Date(), 1);
    }
}
