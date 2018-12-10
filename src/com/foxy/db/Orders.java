/*
 * Order.java
 *
 * Created on June 21, 2006, 3:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.db;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author eric
 */
public class Orders implements Auditable, Serializable {

    private String orderId = null;
    private String countryCode = null;
    private String cnameCode = null;
    private String custCode = null;
    private String custBrand = null;
    private String custDivision = null;
    private String styleCode = null;
    private String season = null;
    private String merchandiser = null;
    private Date orderDate = null;
    private Double unitPrice = null;
    private String type = null;
    private String description = null;
    private String fabrication = null;
    private String fabricType = null;
    private String remark = null;
    private Double qtyDzn = null;
    private Double qtyPcs = null;
    private Double horizontal = null;
    private Double vertical = null;
    private String imgFile = null;
    private String priceTerm = null;
    private String fabricMill = null;
    private Double dailyCap = null;
    private String colourTypeCode = null;
    private String graphicTypeCode = null;
    private String fabricYyDz = null;
    private String fabricPrice = null;
    private Double costCm = null;
    private Double costBasicTrim = null;
    private Double costAddTrim = null;
    private Double ftyCm = null;
    private String ftyRemark = null;
    private Double ftyTrim = null;
    private Double actualOutput = null;
    private Double actualCm = null;
    private Double actualTrim = null;
    private String wash = null;
    private String swash = null;
    private String gcost = null;
    private String quotaUom = null;
    private String uom = null;
    private String status = null;
    private String delUsrId = null;
    private Date delTime = null;
    private String updUsrId = null;
    private Date updTime = null;
    private String insUsrId = null;
    private Date insTime = null;
	private String remarkMarketing = null;

    /**
     * Creates a new instance of Order
     */
    public Orders() {
    }

    //PROPERTY: orderId
    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String newValue) {
        this.orderId = newValue;
    }
    //PROPERTY: countryCode

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String newValue) {
        this.countryCode = newValue;
    }

    //PROPERTY: CompanyName
    public String getCnameCode() {
        return cnameCode;
    }

    public void setCnameCode(String cnameCodeNew) {
        this.cnameCode = cnameCodeNew;
    }

    //PROPERTY: custCode
    public String getCustCode() {
        return this.custCode;
    }

    public void setCustCode(String newValue) {
        this.custCode = newValue;
    }
    //PROPERTY: custBrand

    public String getCustBrand() {
        return this.custBrand;
    }

    public void setCustBrand(String newValue) {
        this.custBrand = newValue;
    }
    //PROPERTY: custDivision

    public String getCustDivision() {
        return this.custDivision;
    }

    public void setCustDivision(String newValue) {
        this.custDivision = newValue;
    }

    //PROPERTY: styleCode
    public String getStyleCode() {
        return this.styleCode;
    }

    public void setStyleCode(String newValue) {
        this.styleCode = newValue;
    }
    //PROPERTY: season

    public String getSeason() {
        return this.season;
    }

    public void setSeason(String newValue) {
        this.season = newValue;
    }
    //PROPERTY: merchandiser

    public String getMerchandiser() {
        return this.merchandiser;
    }

    public void setMerchandiser(String newValue) {
        this.merchandiser = newValue;
    }
    //PROPERTY: orderDate

    public Date getOrderDate() {
        return this.orderDate;
    }

    public void setOrderDate(Date newValue) {
        this.orderDate = newValue;
    }
    //PROPERTY: unitPrice

    public Double getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(Double newValue) {
        this.unitPrice = newValue;
    }
    //PROPERTY: type

    public String getType() {
        return this.type;
    }

    public void setType(String newValue) {
        this.type = newValue;
    }
    //PROPERTY: description

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String newValue) {
        this.description = newValue;
    }
    //PROPERTY: fabrication

    public String getFabrication() {
        return this.fabrication;
    }

    public void setFabrication(String newValue) {
        this.fabrication = newValue;
    }

    //PROPERTY: fabricType
    public String getFabricType() {
        return fabricType;
    }

    public void setFabricType(String fabricType) {
        this.fabricType = fabricType;
    }

    //PROPERTY: remark
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String newValue) {
        this.remark = newValue;
    }
    //PROPERTY: qtyDzn

    public Double getQtyDzn() {
        return this.qtyDzn;
    }

    public void setQtyDzn(Double newValue) {
        this.qtyDzn = newValue;
    }
    //PROPERTY: qtyPcs

    public Double getQtyPcs() {
        return this.qtyPcs;
    }

    public void setQtyPcs(Double newValue) {
        this.qtyPcs = newValue;
    }
    //PROPERTY: horizontal

    public Double getHorizontal() {
        return this.horizontal;
    }

    public void setHorizontal(Double newValue) {
        this.horizontal = newValue;
    }
    //PROPERTY: vartical

    public Double getVertical() {
        return this.vertical;
    }

    public void setVertical(Double newValue) {
        this.vertical = newValue;
    }

    //PROPERTY: priceTerm
    public String getPriceTerm() {
        return this.priceTerm;
    }

    public void setPriceTerm(String newValue) {
        this.priceTerm = newValue;
    }
    //PROPERTY: fabricMill

    public String getFabricMill() {
        return this.fabricMill;
    }

    public void setFabricMill(String newValue) {
        this.fabricMill = newValue;
    }

    public Double getDailyCap() {
        return dailyCap;
    }

    public void setDailyCap(Double dailyCap) {
        this.dailyCap = dailyCap;
    }

    public String getColourTypeCode() {
        return colourTypeCode;
    }

    public void setColourTypeCode(String colourTypeCode) {
        this.colourTypeCode = colourTypeCode;
    }

    public String getGraphicTypeCode() {
        return graphicTypeCode;
    }

    public void setGraphicTypeCode(String graphicTypeCode) {
        this.graphicTypeCode = graphicTypeCode;
    }

    //PROPERTY: fabricYyDz
    public String getFabricYyDz() {
        return this.fabricYyDz;
    }

    public void setFabricYyDz(String newValue) {
        this.fabricYyDz = newValue;
    }
    //PROPERTY: fabricPrice

    public String getFabricPrice() {
        return this.fabricPrice;
    }

    public void setFabricPrice(String newValue) {
        this.fabricPrice = newValue;
    }
    //PROPERTY: costCm

    public Double getCostCm() {
        return this.costCm;
    }

    public void setCostCm(Double newValue) {
        this.costCm = newValue;
    }

    public Double getCostBasicTrim() {
        return costBasicTrim;
    }

    public void setCostBasicTrim(Double costBasicTrim) {
        this.costBasicTrim = costBasicTrim;
    }

    public Double getCostAddTrim() {
        return costAddTrim;
    }

    public void setCostAddTrim(Double costAddTrim) {
        this.costAddTrim = costAddTrim;
    }

    //PROPERTY: factory CMT
    public Double getFtyCm() {
        return ftyCm;
    }

    public void setFtyCm(Double ftyCm) {
        this.ftyCm = ftyCm;
    }

    //PROPERTY: factory CMT remark
    public String getFtyRemark() {
        return ftyRemark;
    }

    public void setFtyRemark(String ftyRemark) {
        this.ftyRemark = ftyRemark;
    }

    public Double getFtyTrim() {
        return ftyTrim;
    }

    public void setFtyTrim(Double ftyTrim) {
        this.ftyTrim = ftyTrim;
    }

    public Double getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(Double actualOutput) {
        this.actualOutput = actualOutput;
    }

    public Double getActualCm() {
        return actualCm;
    }

    public void setActualCm(Double actualCm) {
        this.actualCm = actualCm;
    }

    public Double getActualTrim() {
        return actualTrim;
    }

    public void setActualTrim(Double actualTrim) {
        this.actualTrim = actualTrim;
    }

    public String getDoubleStrValue(Double val) {
        if (val == null) {
            return "";
        } else {
            return val.toString();
        }
    }

    //PROPERTY: wash
    public String getWash() {
        return this.wash;
    }

    public void setWash(String newValue) {
        this.wash = newValue;
    }
    //PROPERTY: swash

    public String getSwash() {
        return this.swash;
    }

    public void setSwash(String newValue) {
        this.swash = newValue;
    }
    //PROPERTY: gcost

    public String getGcost() {
        return this.gcost;
    }

    public void setGcost(String newValue) {
        this.gcost = newValue;
    }
    //PROPERTY: quotaUom

    public String getQuotaUom() {
        return this.quotaUom;
    }

    public void setQuotaUom(String newValue) {
        this.quotaUom = newValue;
    }
    //PROPERTY: uom

    public String getUom() {
        return this.uom;
    }

    public void setUom(String newValue) {
        this.uom = newValue;
    }

    //PROPERTY: status
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String newValue) {
        this.status = newValue;
    }

    public String getImgFileLink() {
        if (imgFile == null) {
            return "Not Yet Uploaded";
        }
        return imgFile;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
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
    	
        this.insTime =insTime;
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

	public String getRemarkMarketing() {
		return remarkMarketing;
	}

	public void setRemarkMarketing(String remarkMarketing) {
		this.remarkMarketing = remarkMarketing;
	}

	
}
