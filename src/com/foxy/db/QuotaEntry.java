/*
 * QuotaEntry.java
 *
 * Created on August 6, 2006, 11:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.db;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author hcting
 */
public class QuotaEntry implements Auditable,Serializable {
    private Long qtaEntryId = null;
    private String country = null;
    private String quota = null;
    private Date effDate = null;
    private Date expDate = null;
    private String type = null;
    private String quotaRefNo = null;
    private Double quotaQty = null;
    private Double cost = null;
    private String status = null;
    private String delUsrId  = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    /** Creates a new instance of Category */
    public QuotaEntry() {
        Calendar cl = Calendar.getInstance();
        //System.err.println(cl.toString());
        //int y = cl.get(cl.YEAR);
        //System.err.println("YEAR = " + y + "      " + cl.toString());
        cl.set(cl.get(cl.YEAR),cl.DECEMBER, 31); //0-Jan!
        this.expDate = cl.getTime();
    }
    
    public Long getQtaEntryId() {
        return qtaEntryId;
    }
    
    public void setQtaEntryId(Long qtaEntryId) {
        this.qtaEntryId = qtaEntryId;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getQuota() {
        return quota;
    }
    
    public void setQuota(String quota) {
        this.quota = quota;
    }
    
    public Date getEffDate() {
        return effDate;
    }
    
    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }
    
    public Date getExpDate() {
        return expDate;
    }
    
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getQuotaRefNo() {
        return quotaRefNo;
    }
    
    public void setQuotaRefNo(String quotaRefNo) {
        this.quotaRefNo = quotaRefNo;
    }
    
    public Double getQuotaQty() {
        return quotaQty;
    }
    
    public void setQuotaQty(Double quotaQty) {
        this.quotaQty = quotaQty;
    }
    
    public Double getCost() {
        return cost;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getDelTime() {
        return delTime;
    }
    
    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }
    
    public String getDelUsrId() {
        return delUsrId;
    }
    
    public void setDelUsrId(String delUsrId) {
        this.delUsrId = delUsrId;
    }
    
    public Date getInsTime() {
        return insTime;
    }
    
    public void setInsTime(Date insTime) {
        this.insTime = insTime;
    }
    
    public Date getUpdTime() {
        return updTime;
    }
    
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
    
    public String getInsUsrId() {
        return insUsrId;
    }
    
    public void setInsUsrId(String insUsrId) {
        this.insUsrId = insUsrId;
    }
    
    public String getUpdUsrId() {
        return updUsrId;
    }
    
    public void setUpdUsrId(String updUsrId) {
        this.updUsrId = updUsrId;
    }
}
