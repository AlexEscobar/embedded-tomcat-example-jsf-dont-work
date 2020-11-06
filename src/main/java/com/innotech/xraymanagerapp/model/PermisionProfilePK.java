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
public class PermisionProfilePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "ProfileId")
    private int profileId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PermisionId")
    private int permisionId;

    public PermisionProfilePK() {
    }

    public PermisionProfilePK(int profileId, int permisionId) {
        this.profileId = profileId;
        this.permisionId = permisionId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getPermisionId() {
        return permisionId;
    }

    public void setPermisionId(int permisionId) {
        this.permisionId = permisionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) profileId;
        hash += (int) permisionId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PermisionProfilePK)) {
            return false;
        }
        PermisionProfilePK other = (PermisionProfilePK) object;
        if (this.profileId != other.profileId) {
            return false;
        }
        if (this.permisionId != other.permisionId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.PermisionProfilePK[ profileId=" + profileId + ", permisionId=" + permisionId + " ]";
    }
    
}
