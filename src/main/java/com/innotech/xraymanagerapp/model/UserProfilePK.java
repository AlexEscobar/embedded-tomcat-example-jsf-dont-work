/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author X-Ray
 */
@Embeddable
public class UserProfilePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "UserId")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ProfileId")
    private int profileId;

    public UserProfilePK() {
    }

    public UserProfilePK(int userId, int profileId) {
        this.userId = userId;
        this.profileId = profileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += (int) profileId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserProfilePK)) {
            return false;
        }
        UserProfilePK other = (UserProfilePK) object;
        if (this.userId != other.userId) {
            return false;
        }
        if (this.profileId != other.profileId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.UserProfilePK[ userId=" + userId + ", profileId=" + profileId + " ]";
    }
    
}
