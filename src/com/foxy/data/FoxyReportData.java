/*
 * FoxyOrderDetailData.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 *
 * @author eric
 */
public class FoxyReportData  implements Serializable {
    /**
     * serial id for serialisation versioning
     */
    private static final long serialVersionUID = 1L;
    
    /* Report Data */
    private String orderId = null;
    private String month = null;
    private String location = null;
    private String subLocation = null;
    private String poNumber = null;
    private Date closeDate = null;
    private Date orderDate = null;
    private String customer = null;
    private String style = null;
    private Double quantityDzn = null;
    private Double quantityPcs = null;
    private Double unitPrice = null;
    private String category = null;
    private String destName = null;
    private String destShortName = null;
    private String ship = null;
    
    private Double lotQty = null;
    private Double mrQty = null;
    private Double shipQty = null;
    
    /* For Shipping */
    private String invoice = null;
    private String lcNumber = null;
    private Date etd = null;
    private Date eta = null;
    private Double amount = null;
    private Double ctn = null;
    private String vessel = null;
    private String voyage = null;

    
    private Integer refIdS = null;
    private Integer refIdC = null;
    private Integer refIdSp = null;
    private String countryShortName = null;
    private String orderMethod = null;
    private String make = null;
    
    private Date poDate = null;
    
    private String unit = null;
    private Date fabricDate = null;
    private String remark = null;
    private Date vesselDate = null;
    
    private Date delivery = null;
    private String destination = null;
    
    public FoxyReportData() {}
    
    //PROPERTY: refIdS
    public Integer getRefIdS(){
        return this.refIdS;
    }
    public void setRefIdS(Integer newValue) {
        this.refIdS = newValue;
    }
    
    //PROPERTY: refIdC
    public Integer getRefIdC(){
        return this.refIdC;
    }
    public void setRefIdC(Integer newValue) {
        this.refIdC = newValue;
    }
    
    //PROPERTY: refIdS
    public Integer getRefIdSp(){
        return this.refIdSp;
    }
    public void setRefIdSp(Integer newValue) {
        this.refIdSp = newValue;
    }
    
    //PROPERTY: orderId
    public String getOrderId(){
        return this.orderId;
    }
    
    public void setOrderId(String newValue) {
        this.orderId = newValue;
    }
    
    //PROPERTY: month
    public String getMonth(){
        return this.month;
    }
    
    public void setMonth(String newValue) {
        this.month = newValue;
    }
    
    //PROPERTY: location
    public String getLocation(){
        return this.location;
    }
    
    public void setLocation(String newValue) {
        this.location = newValue;
    }

    //PROPERTY: ship
    public String getShip(){
        return this.ship;
    }
    
    public void setShip(String newValue) {
        this.ship = newValue;
    }
    
    //PROPERTY: subLocation
    public String getSubLocation(){
        return this.subLocation;
    }
    
    public void setSubLocation(String newValue) {
        this.subLocation = newValue;
    }
    
    //PROPERTY: countryShortName
    public String getCountryShortName(){
        return this.countryShortName;
    }
    
    public void setCountryShortName(String newValue) {
        this.countryShortName = newValue;
    }
    
    //PROPERTY: category
    public String getCategory(){
        return this.category;
    }
    public void setCategory(String newValue) {
        this.category = newValue;
    }

    //PROPERTY: customer
    public String getCustomer(){
        return this.customer;
    }
    public void setCustomer(String newValue) {
        this.customer = newValue;
    }
    
    //PROPERTY: orderMethod
    public String getOrderMethodD(){
        if (this.orderMethod != null && this.orderMethod.length() > 0) {
            return "(" + this.orderMethod + ")";
        } else {
            return " ";
        }
    }
    public String getOrderMethod(){
        return this.orderMethod;
    }
    public void setOrderMethod(String newValue) {
        this.orderMethod = newValue;
    }
    
    //PROPERTY: destName
    public String getDestName(){
        return this.destName;
    }
    public void setDestName(String newValue) {
        this.destName = newValue;
    }
    
    //PROPERTY: destShortName
    public String getDestShortName(){
        return this.destShortName;
    }
    public void setDestShortName(String newValue) {
        this.destShortName = newValue;
    }
    
    //PROPERTY: quantityDzn
    public Double getQuantityDzn(){
        return this.quantityDzn;
    }
    
    public void setQuantityDzn(Double newValue) {
        this.quantityDzn = newValue;
    }
    
    //PROPERTY: quantityPcs
    public Double getQuantityPcs(){
        return this.quantityPcs;
    }
    
    public void setQuantityPcs(Double newValue) {
        this.quantityPcs = newValue;
    }
    
    //PROPERTY: unit
    public String getUnit(){
        return this.unit;
    }
    
    public void setUnit(String newValue) {
        this.unit = newValue;
    }
    
    //PROPERTY: remark
    public String getRemark(){
        return this.remark;
    }
    
    public void setRemark(String newValue) {
        this.remark = newValue;
    }
    
    //PROPERTY: vesselDate
    public Date getVesselDate() {
        return this.vesselDate;
    }
    
    public void setVesselDate(Date newValue) {
        this.vesselDate = newValue;
    }
    
    //PROPERTY: closeDate
    public Date getCloseDate() {
        return this.closeDate;
    }
    
    public void setCloseDate(Date newValue) {
        this.closeDate = newValue;
    }
    
    //PROPERTY: orderDate
    public Date getOrderDate() {
        return this.orderDate;
    }
    public void setOrderDate(Date newValue) {
        this.orderDate = newValue;
    }
    
    //PROPERTY: delivery
    public Date getDelivery() {
        return this.delivery;
    }
    
    public void setDelivery(Date newValue) {
        this.delivery = newValue;
    }
    
    //PROPERTY: destination
    public String getDestination(){
        return this.destination;
    }
    
    public void setDestination(String newValue) {
        this.destination = newValue;
    }
    
    //PROPERTY: poNumber
    public String getPoNumber(){
        return this.poNumber;
    }
    
    public void setPoNumber(String newValue) {
        this.poNumber = newValue;
    }
    
    //PROPERTY: make
    public String getMake(){
        return this.make;
    }
    
    public void setMake(String newValue) {
        this.make = newValue;
    }

    //PROPERTY: style
    public String getStyle(){
        return this.style;
    }
    public void setStyle(String newValue) {
        this.style = newValue;
    }    
    
    //PROPERTY: poDate
    public Date getPoDate() {
        return this.poDate;
    }
    public void setPoDate(Date newValue) {
        this.poDate = newValue;
    }
    
    //PROPERTY: fabricDate
    public Date getFabricDate() {
        return this.fabricDate;
    }
    public void setFabricDate(Date newValue) {
        this.fabricDate = newValue;
    }
    
    //PROPERTY: lot
    public String getLot(){
        if (this.month != null && this.location != null)         {
            //if (this.subLocation == null) {
            return (this.month + this.location);
                /*else {
                return (this.month + this.location);
            }*/
        } else {
            return (new String(""));
        }
    }
    
    //PROPERTY: lotS
    public String getLotS(){
        return (this.month);
    }
    
    //PROPERTY: lotD
    public String getLotD(){
        if (this.month == null) {
            this.month = "";
        }
        if (this.location == null) {
            this.location = "";
        }
        if (this.subLocation == null) {
            this.subLocation = "";
        }        
        return (this.month + this.location + this.subLocation);
    }
    
    //PROPERTY: invoice
    public String getInvoice(){
        return this.invoice;
    }
    public void setInvoice(String newValue) {
        this.invoice = newValue;
    }
    //PROPERTY: lcNumber
    public String getLcNumber(){
        return this.lcNumber;
    }
    public void setLcNumber(String newValue) {
        this.lcNumber = newValue;
    }
    //PROPERTY: etd
    public Date getEtd(){
        return this.etd;
    }
    public void setEtd(Date newValue) {
        this.etd = newValue;
    }
    //PROPERTY: eta
    public Date getEta(){
        return this.eta;
    }
    public void setEta(Date newValue) {
        this.eta = newValue;
    }
    
    //PROPERTY: amount
    public Double getAmount(){
        return this.amount;
    }
    public void setAmount(Double newValue) {
        this.amount = newValue;
    }
    //PROPERTY: ctn
    public Double getCtn(){
        return this.ctn;
    }
    public void setCtn(Double newValue) {
        this.ctn = newValue;
    }
    //PROPERTY: vessel
    public String getVessel(){
        return this.vessel;
    }
    public void setVessel(String newValue) {
        this.vessel = newValue;
    }
    //PROPERTY: voyage
    public String getVoyage(){
        return this.voyage;
    }
    public void setVoyage(String newValue) {
        this.voyage = newValue;
    }
    
    //PROPERTY: lotQty
    public Double getLotQty(){
        return this.lotQty;
    }    
    public void setLotQty(Double newValue) {
        this.lotQty = newValue;
    }

    //PROPERTY: mrQty
    public Double getMrQty(){
        return this.mrQty;
    }    
    public void setMrQty(Double newValue) {
        this.mrQty = newValue;
    }

    //PROPERTY: shipQty
    public Double getShipQty(){
        return this.shipQty;
    }    
    public void setShipQty(Double newValue) {
        this.shipQty = newValue;
    }

    //PROPERTY: unitPrice
    public Double getUnitPrice(){
        return this.unitPrice;
    }    
    public void setUnitPrice(Double newValue) {
        this.unitPrice = newValue;
    }

    //PROPERTY: total
    public Double getTotal(){
        if (this.quantityPcs != null && this.unitPrice != null) {
            return this.quantityPcs * this.unitPrice;
        } else {
            return null;
        }
    }    
    //PROPERTY: payDate
    public String getPayDate(){
        String[] ids = TimeZone.getAvailableIDs(+8 * 60 * 60 * 1000);
        SimpleTimeZone pdt = new SimpleTimeZone(+8 * 60 * 60 * 1000, ids[0]);
        GregorianCalendar gc = new GregorianCalendar(pdt);
        if (etd != null) {
            gc.setTime(etd);
            gc.add(gc.DAY_OF_YEAR, 10);
            return String.valueOf(gc.get(Calendar.WEEK_OF_MONTH));
        } else {
            return null;
        }
    }       
}
