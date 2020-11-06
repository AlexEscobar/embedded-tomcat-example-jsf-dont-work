/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author X-Ray
 */
@Entity
@Table(name = "User_Profile")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserProfile.findAll", query = "SELECT u FROM UserProfile u"),
    @NamedQuery(name = "UserProfile.findByUserId", query = "SELECT u FROM UserProfile u WHERE u.userProfilePK.userId = :userId"),
    @NamedQuery(name = "UserProfile.findByProfileId", query = "SELECT u FROM UserProfile u WHERE u.userProfilePK.profileId = :profileId"),
    @NamedQuery(name = "UserProfile.findByEntryDate", query = "SELECT u FROM UserProfile u WHERE u.entryDate = :entryDate"),
    @NamedQuery(name = "UserProfile.findByStatus", query = "SELECT u FROM UserProfile u WHERE u.status = :status")})
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserProfilePK userProfilePK;
   
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
   
    @Column(name = "Status")
    private boolean status;
    @JoinColumn(name = "ProfileId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Profiles profiles;
    @JoinColumn(name = "UserId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users users;

    public UserProfile() {
    }

    public UserProfile(UserProfilePK userProfilePK) {
        this.userProfilePK = userProfilePK;
    }

    public UserProfile(UserProfilePK userProfilePK, Date entryDate, boolean status) {
        this.userProfilePK = userProfilePK;
        this.entryDate = entryDate;
        this.status = status;
    }

    public UserProfile(int userId, int profileId) {
        this.userProfilePK = new UserProfilePK(userId, profileId);
    }

    public UserProfilePK getUserProfilePK() {
        return userProfilePK;
    }

    public void setUserProfilePK(UserProfilePK userProfilePK) {
        this.userProfilePK = userProfilePK;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Profiles getProfiles() {
        return profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userProfilePK != null ? userProfilePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserProfile)) {
            return false;
        }
        UserProfile other = (UserProfile) object;
        if ((this.userProfilePK == null && other.userProfilePK != null) || (this.userProfilePK != null && !this.userProfilePK.equals(other.userProfilePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if(profiles != null)
            return profiles.toString();
        else
            return "";
    }
    
}
