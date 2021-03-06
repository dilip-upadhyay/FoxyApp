/*
 * ProdSchedule.java
 *
 * Created on June 30, 2006, 6:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.db;

import java.io.Serializable;
import java.util.Date;
import javax.faces.context.FacesContext;
import com.foxy.util.ListData;


/**
 *
 * @author hcting
 */
public class ProdSchedule implements Auditable,Serializable {
    private Long prodSchId = null;
    private String ccode = null;
    private Long lineNo = null;
    private String refNo = null;
    private Long ordQty = null;
    private Date sewStart = null;
    private Date sewEnd= null;
    private Long numOfDay = null;
    private Long dailyProdQty = null;
    private boolean bySubCon = false;
    private String groups = null;
    private String remark = null;
    private String status = null;
    private String delUsrId   = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    
    /** Creates a new instance of Farameter */
    public ProdSchedule() {
    }
    
    public String getCcode() {
        return ccode;
    }
    
    public void setCcode(String ccode) {
        this.ccode = ccode;
    }
    
    public String getCountryDesc(){
        FacesContext ctx = FacesContext.getCurrentInstance();
        ListData ld = (ListData)ctx.getApplication().getVariableResolver().resolveVariable(ctx, "listData");
        return ld.getCountryDesc(this.ccode, 1);
    }
    
    public Long getDailyProdQty() {
        return dailyProdQty;
    }
    
    public void setDailyProdQty(Long dailyProdQty) {
        this.dailyProdQty = dailyProdQty;
    }
    
    
    public boolean getBySubCon() {
        return bySubCon;
    }
    
    public boolean isBySubCon() {
        return bySubCon;
    }
    
    public void setBySubCon(boolean bySubCon) {
        this.bySubCon = bySubCon;
    }
    
    public String getSubConDesc(){
        if ( this.bySubCon){
            return "YES";
        }else{
            return "NO";
        }
    }
    
    public Long getProdSchId() {
        return prodSchId;
    }
    
    public void setProdSchId(Long Id) {
        this.prodSchId = Id;
    }
    
    public Long getLineNo() {
        return lineNo;
    }
    
    public void setLineNo(Long lineNo) {
        this.lineNo = lineNo;
    }
    
    public Long getNumOfDay() {
        return numOfDay;
    }
    
    public void setNumOfDay(Long numOfDay) {
        this.numOfDay = numOfDay;
    }
    
    public Long getOrdQty() {
        return ordQty;
    }
    
    public void setOrdQty(Long ordQty) {
        this.ordQty = ordQty;
    }
    
    public String getRefNo() {
        return refNo;
    }
    
    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }
    
    public Date getSewEnd() {
        return sewEnd;
    }
    
    public void setSewEnd(Date sewEnd) {
        this.sewEnd = sewEnd;
    }
    
    public Date getSewStart() {
        return sewStart;
    }
    
    public void setSewStart(Date sewStart) {
        this.sewStart = sewStart;
    }
    
    public String getGroups() {
        return groups;
    }
    
    public void setGroups(String groups) {
        this.groups = groups;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    //PROPERTY: status
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String newValue) {
        this.status = newValue;
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
    
    public String getDelUsrId() {
        return delUsrId;
    }
    
    public void setDelUsrId(String delUsrId) {
        this.delUsrId = delUsrId;
    }
    
    public Date getDelTime() {
        return delTime;
    }
    
    public void setDelTime(Date delTime) {
        this.delTime = delTime;
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
