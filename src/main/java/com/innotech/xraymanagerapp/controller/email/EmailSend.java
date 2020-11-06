/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.email;

import java.io.Serializable;

/**
 *
 * @author Alexander Escobar L.
 */
public class EmailSend implements Serializable {

    private String[] imageIdList;
    private int senderId;// email from
    private String emailReceiver;// email to
    private String emailType;// could be: jpeg, tif, png or dicom
    private String emailSubject;// the subject of the email
    private String emailBody;// the body of the email in html format

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getEmailReceiver() {
        return emailReceiver;
    }

    public void setEmailReceiver(String emailReceiver) {
        this.emailReceiver = emailReceiver;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String[] getImageIdList() {
        return imageIdList;
    }

    public void setImageIdList(String[] imageIdList) {
        this.imageIdList = imageIdList;
    }
    
    
}
