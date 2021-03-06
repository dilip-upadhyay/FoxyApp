/*
 * FoxyCRptsCutOffFABalancePage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.faces.application.FacesMessage;
import com.foxy.db.HibernateUtil;
import com.foxy.util.ListData;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.lang.Math;


/**
 *
 * @author hcting
 */
public class FoxyCRptsCutOffFABalancePage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel dataListModel;
    private String country = null;
    private String refno = null;
    private String remarkfilter = null;
    private List remarkList = null;
    
    private SimpleDateFormat fmt1 =  new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date today_date  = new Date(); //current date
    
    
    public class ReportDataBean {
        private String refno = null;
        private String cust = null;
        private Double fabricQtyB4 = null;
        private Double fabricQtyAft = null;
        private Double mktUnitPrice = null;
        private Double mktQty = null;
        private Double salesQty = null;
        private Double unShipQty = null;
        private Double unShipMktValue = null;
        private Date  firstDelivery = null;
        private Date  lastDelivery = null;
        private String remarks = null;
        
        
        public String getRefno() {
            return refno;
        }
        
        public void setRefno(String refno) {
            this.refno = refno;
        }
        
        public String getCust() {
            return cust;
        }
        
        public void setCust(String cust) {
            this.cust = cust;
        }
        
        public Double getFabricQtyB4() {
            return fabricQtyB4;
        }
        
        public void setFabricQtyB4(Double fabricQtyB4) {
            this.fabricQtyB4 = fabricQtyB4;
        }
        
        public void accumulateFabricQtyB4(Double fabricQtyB4) {
            if ( fabricQtyB4 != null ){
                if ( this.fabricQtyB4 == null){
                    this.fabricQtyB4 = fabricQtyB4;
                }else{
                    this.fabricQtyB4 += fabricQtyB4;
                }
            }
        }
        
        
        public Double getFabricQtyAft() {
            return fabricQtyAft;
        }
        
        public void setFabricQtyAft(Double fabricQtyAft) {
            this.fabricQtyAft = fabricQtyAft;
        }
        
        public void accumulateFabricQtyAft(Double fabricQtyAft) {
            if ( fabricQtyAft != null ){
                if ( this.fabricQtyAft == null){
                    this.fabricQtyAft = fabricQtyAft;
                }else{
                    this.fabricQtyAft += fabricQtyAft;
                }
            }
        }
        
        
        public Double getMktUnitPrice() {
            return mktUnitPrice;
        }
        
        public void setMktUnitPrice(Double mktUnitPrice) {
            this.mktUnitPrice = mktUnitPrice;
        }
        
        public Double getMktQty() {
            return mktQty;
        }
        
        public void setMktQty(Double mktQty) {
            this.mktQty = mktQty;
        }
        
        public void accumulateMktQty(Double mktQty) {
            if ( mktQty != null ){
                if ( this.mktQty == null){
                    this.mktQty = mktQty;
                }else{
                    this.mktQty += mktQty;
                }
            }
        }
        
        
        public Double getSalesQty() {
            return salesQty;
        }
        
        public void setSalesQty(Double salesQty) {
            this.salesQty = salesQty;
        }
        
        public void accumulateSalesQty(Double salesQty) {
            if ( salesQty != null ){
                if ( this.salesQty == null){
                    this.salesQty = salesQty;
                }else{
                    this.salesQty += salesQty;
                }
            }
        }
        
        public Double getUnShipQty() {
            return unShipQty;
        }
        
        public void setUnShipQty(Double unShipQty) {
            this.unShipQty = unShipQty;
        }
        
        public void accumulateUnShipQty(Double unShipQty) {
            if ( unShipQty != null ){
                if ( this.unShipQty == null){
                    this.unShipQty = unShipQty;
                }else{
                    this.unShipQty += unShipQty;
                }
            }
        }
        
        public Double getUnShipMktValue() {
            return unShipMktValue;
        }
        
        public void setUnShipMktValue(Double unShipMktValue) {
            this.unShipMktValue = unShipMktValue;
        }
        
        public void accumulateUnShipMktValue(Double unShipMktValue) {
            if ( unShipMktValue != null ){
                if ( this.unShipMktValue == null){
                    this.unShipMktValue = unShipMktValue;
                }else{
                    this.unShipMktValue += unShipMktValue;
                }
            }
        }
        
        public  Date getFirstDelivery(){
            return this.firstDelivery;
        }
        
        public  void setFirstDelivery(Date firstDelivery){
            this.firstDelivery = firstDelivery;
        }
        
        public  Date getLastDelivery(){
            return this.lastDelivery;
        }
        
        public  void setLastDelivery(Date lastDelivery){
            this.lastDelivery = lastDelivery;
        }
        
        public void setRemarks(String  remarks){
            this.remarks = remarks;
        }
        
        public String getRemarks(){
            return  this.remarks;
        }
        
    }; //Inner class end
    
    
    public FoxyCRptsCutOffFABalancePage() {
        super(new String("CutOffProfitReportsForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public String getTitle(){
        String str = " ";
        
        if ( this.searchKey != null){
            str = " [" + this.searchKey + "] ";
        }else{
            str +=  "(" + this.getCountryName() + ") ";
        }
        
        str +=  "Cut Off Date [" +  fmt1.format(this.getFromDate()) + "] ";
        
        if (isPendingOnly()){
            str += " Status [Pending] ";
        }
        
        str +=  "As At [" +  fmt2.format(new Date()) + "] ";
        
        return str;
    }
    
    
    
    //List of options listing for remark filtering
    public List getRemarkList() {
        if (remarkList == null ){
            remarkList = new ArrayList();
            remarkList.add(new SelectItem(new String(""),  new String("All")));
            remarkList.add(new SelectItem(new String("1"), new String("Pending")));
        }
        return remarkList;
    }
    
    public String getRemarkfilter(){
        return remarkfilter;
    }
    
    public void setRemarkfilter(String remarkfilter){
        this.remarkfilter = remarkfilter;
    }
    
    private boolean isPendingOnly(){
        //"1" is for order with pending only
        if ( "1".equals(this.remarkfilter)) {
            return true;
        }else{
            return false;
        }
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getRefno() {
        return refno;
    }
    
    public void setRefno(String refno) {
        this.refno = refno;
    }
    
    
    
    
    public String getCountryName(){
        ListData ld = (ListData)getBean("listData");
        return(ld.getCountryDesc(this.getCountry(), 0));
        //return this.country;
    }
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        
        if ( this.getFromDate() != null ){
            Calendar cal = Calendar.getInstance();
            // This is the right way to set the month
            cal.setTime(this.getFromDate());
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            
        }
        
        return (null);
    }
    
    
    
    
    public DataModel getReportDataByCountry(){
        List<ReportDataBean>  dataList = null;
        if ( this.country != null){
            try {
                //System.err.println("Search for records " +  this.country + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                
                String qstr = "";
                qstr += " SELECT os.orderid as orderid, od.custcode as cust, od.unitprice as unitprice, ";
                qstr += "         sum((SELECT sum(ivm.quantity) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate1 )) as fabricqtyb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate2)) as fabricqtyaft, ";
                qstr += "         sum((SELECT sum(sd.qtypcs) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate3)) as salesinvqty, ";
                qstr += "         sum(os.qtypcs) as mktlotqty, ";
                qstr += "         min(delivery) as mindate, max(delivery) as maxdate ";
                qstr += " FROM ordsummary as os ";
                qstr += " LEFT JOIN orders od ON od.orderid = os.orderid ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += " WHERE fm.countrycode = :pcountry ";
                qstr += " AND srefid IN (SELECT srefid FROM invmovement as ivm WHERE EXISTS (Select iv.invrefid from inventory AS iv ";
                qstr += "                                                WHERE iv.type = 'FABRIC' AND iv.invrefid = ivm.invrefid) ) ";
                qstr += " GROUP BY orderid ";
                qstr += " HAVING mindate <= :pcutofdate4  AND maxdate >= :pcutofdate5 ";
                qstr += " ORDER BY orderid ";
                
                //System.err.println(qstr);
                //System.err.println("from date " + this.getFromDate() + " To date " + this.getToDate() +  "origin = " + this.country);
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                
                q.setString("pcountry", this.country);
                q.setDate("pcutofdate1", this.getFromDate());
                q.setDate("pcutofdate2", this.getFromDate());
                q.setDate("pcutofdate3", this.getFromDate());
                q.setDate("pcutofdate4", this.getFromDate());
                q.setDate("pcutofdate5", this.getFromDate());
                
                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("cust", Hibernate.STRING);
                q.addScalar("unitprice", Hibernate.DOUBLE);
                q.addScalar("fabricqtyb4", Hibernate.DOUBLE);
                q.addScalar("fabricqtyaft", Hibernate.DOUBLE);
                q.addScalar("salesinvqty", Hibernate.DOUBLE);
                q.addScalar("mktlotqty", Hibernate.DOUBLE);
                q.addScalar("mindate", Hibernate.DATE);
                q.addScalar("maxdate", Hibernate.DATE);
                
                
                //System.err.println("Test 111111112222233333344444444 = [" + this.remarkfilter + "]");
                
                Iterator it = q.list().iterator();
                //System.err.println("Total records = " + q.list().size());
                tx.commit();
                
                if ( dataList == null){
                    dataList = new ArrayList();
                }
                
                String tmporderid = null;
                Double tmpd1 = null;
                Double tmpd2 = null;
                Double mktuprc = null;
                ReportDataBean gtot = new ReportDataBean();
                gtot.setRefno("Grandtotal:");
                
                try { //loop of all applicable order id
                    while (it.hasNext()){
                        Object[] tmpRow = (Object[])it.next();
                        int i = 0;
                        
                        ReportDataBean obj = new  ReportDataBean();
                        
                        tmporderid = (String)tmpRow[i++];
                        obj.setRefno(tmporderid);
                        
                        obj.setCust((String)tmpRow[i++]);
                        
                        mktuprc = super.roundDouble((Double)tmpRow[i++],2);//unit price (mkt)
                        obj.setMktUnitPrice(mktuprc);
                        
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//fabric Qty b4
                        obj.setFabricQtyB4(tmpd1);
                        gtot.accumulateFabricQtyB4(tmpd1);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//fabric Qty aft
                        obj.setFabricQtyAft(tmpd1);
                        gtot.accumulateFabricQtyAft(tmpd1);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//salesinvqty value
                        obj.setSalesQty(tmpd1);
                        tmpd2 = super.roundDouble((Double)tmpRow[i++],2);//mktlotqty value
                        obj.setMktQty(tmpd2);
                        if ( tmpd1 != null && tmpd2 != null){
                            Double  tmpprc = null;
                            Double  fivePercen = new Double(0.05);
                            
                            tmpprc = Math.abs(( tmpd2 - tmpd1)/tmpd1);
                            if ( fivePercen.compareTo(tmpprc) <= 0){
                                obj.setRemarks("Pending");
                            }else{
                                if (isPendingOnly()){
                                    obj = null;  //release current object allocated
                                    continue;    //skip to next record
                                }
                            }
                            tmpd2 = tmpd2 - tmpd1;//for used as unShipQty
                        }else {
                            obj.setRemarks("Pending");
                        }
                        
                        obj.setFirstDelivery((Date)tmpRow[i++]);
                        obj.setLastDelivery((Date)tmpRow[i++]);
                        
                        obj.setUnShipQty(tmpd2);
                        gtot.accumulateUnShipQty(tmpd2);
                        
                        tmpd1 = super.roundDouble((tmpd2*mktuprc),2);
                        obj.setUnShipMktValue(tmpd1);
                        gtot.accumulateUnShipMktValue(tmpd1);
                        
                        dataList.add(obj);
                    }
                } catch ( Exception e){
                    e.printStackTrace();
                }
                
                dataList.add(gtot);
                
                if (dataListModel == null) {
                    //System.err.println("Search for records 333");
                    dataListModel = new ListDataModel();
                }
                
                dataListModel.setWrappedData(dataList);
                
            } catch (HibernateException e) {
                //do something here with the exception
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            }catch (Exception e){
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            }finally {
                HibernateUtil.closeSession();
            }
        }else {
            System.err.println("No records ....!!! Search key is null");
        }
        //Avoid null pointer exception
        if (dataListModel == null) {
            System.err.println("No records ....!!!");
            dataListModel = new ListDataModel();
        }
        
        return dataListModel;
    }
    
}
