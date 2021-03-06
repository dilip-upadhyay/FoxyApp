/*
 * InvMovementTable01.java
 *
 * Created on February 22, 2007, 11:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author hcting
 */
/**
 * Creates a new instance of InvMovementTable01
 */
/* NOTE: implements Serializable is required for data table click command link */
public class InvMovementTable01 implements Serializable {
    
    private Long   invmrefid = null;
    private String refNo = null;
    private String lotid = null;
    private Date ttDate = null;
    private Double qty = null;
    private Double usedQty = null;
    private Double unitcost = null;
    private Double inputValue = null;
    private Double sgdValue = null;
    private String origin = null;
    private String desc = null;
    
    public InvMovementTable01() {
    }
    
    public Long getInvmrefid() {
        return invmrefid;
    }
    
    public void setInvmrefid(Long invmrefid) {
        this.invmrefid = invmrefid;
    }
    
    public String getRefNo() {
        return refNo;
    }
    
    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }
    
    public String getLotid() {
        return lotid;
    }
    
    public void setLotid(String lotid) {
        this.lotid = lotid;
    }
    
    public Double getQty() {
        return qty;
    }
    
    public void setQty(Double qty) {
        this.qty = qty;
    }
    
    public Double getUsedQty() {
        return usedQty;
    }
    
    public void setUsedQty(Double usedQty) {
        this.usedQty = usedQty;
    }
    
    public void setTtDate(Date ttDate) {
        this.ttDate = ttDate;
    }
    
    public Date getTtDate() {
        return ttDate;
    }
    
    public Double getUnitcost() {
        return unitcost;
    }
    
    public void setSgdValue(Double sgdValue) {
        this.sgdValue = sgdValue;
    }
    
    public Double getSgdValue(){
        return  this.sgdValue;
    }
    
    public void setUnitcost(Double unitcost) {
        this.unitcost = unitcost;
    }
    
    public Double getInputValue() {
        return inputValue;
    }
    
    public void setInputValue(Double inputValue) {
        this.inputValue = inputValue;
    }
    
    public void AccQty(Double qty){
        if ( this.qty == null){
            this.qty =  qty;
        }else{
            if ( qty != null){
                this.qty +=  qty;
            }
        }
    }
    
    public void AccSgdValue(Double sgdVal){
        if ( this.sgdValue == null){
            this.sgdValue =  sgdVal;
        }else{
            if ( sgdVal != null){
                this.sgdValue +=  sgdVal;
            }
        }
    }
    
    public void AccInputValue(Double inputValue){
        if ( this.inputValue == null){
            this.inputValue =  inputValue;
        }else{
            if ( inputValue != null){
                this.inputValue +=  inputValue;
            }
        }
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
}




