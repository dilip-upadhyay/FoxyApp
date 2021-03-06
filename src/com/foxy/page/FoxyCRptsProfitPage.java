/*
 * FoxyCRptsProfitPage.java
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
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;


/**
 *
 * @author hcting
 */
public class FoxyCRptsProfitPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel dataListModel;
    private List quotaForCountry = null;
    private String country = null;
    private String federated = null;
    private String refno = null;
    
    private Date Jan01 = null;
    private SimpleDateFormat fmt1 =  new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    public class ReportDataBean {
        private String refno = null;
        private String lot = null;
        private String origin = null;
        private String customer = null;
        private Double fabric = null;
        private Double accs = null;
        private Double revenue = null;
        private Double fobval = null;
        private Double cmtval = null;
        private Date etd = null;
        private Date delivery = null;
        private boolean federated = false;
        
        public String getRefno() {
            return refno;
        }
        
        public void setRefno(String refno) {
            this.refno = refno;
        }
        
        public String getOrigin() {
            return origin;
        }
        
        public void setOrigin(String origin) {
            this.origin = origin;
        }
        
        public String getCustomer() {
            return customer;
        }
        
        public void setCustomer(String customer) {
            this.customer = customer;
        }
        
        public Double getFabric() {
            return fabric;
        }
        
        public void setFabric(Double fabric) {
            this.fabric = fabric;
        }
        
        public void accumulateFabric(Double fabric) {
            if ( fabric != null ){
                if ( this.fabric == null){
                    this.fabric = fabric;
                }else{
                    this.fabric += fabric;
                }
            }
        }
        
        public Double getAccs() {
            return accs;
        }
        
        public void setAccs(Double accs) {
            this.accs = accs;
        }
        
        public void accumulateAccs(Double accs) {
            if ( accs != null ){
                if ( this.accs == null){
                    this.accs = accs;
                }else{
                    this.accs += accs;
                }
            }
        }
        
        public Double getRevenue() {
            return revenue;
        }
        
        public void setRevenue(Double revenue) {
            this.revenue = revenue;
        }
        
        public void accumulateRevenue(Double revenue) {
            if ( revenue != null ){
                if ( this.revenue == null){
                    this.revenue = revenue;
                }else{
                    this.revenue += revenue;
                }
            }
        }
        
        public Double getFobval() {
            return fobval;
        }
        
        public void setFobval(Double fobval) {
            this.fobval = fobval;
        }
        
        public void accumulateFobval(Double fobval) {
            if ( fobval != null ){
                if ( this.fobval == null){
                    this.fobval = fobval;
                }else{
                    this.fobval += fobval;
                }
            }
        }
        
        public Double getCmtval() {
            return cmtval;
        }
        
        public void setCmtval(Double cmtval) {
            this.cmtval = cmtval;
        }
        
        public void accumulateCmtval(Double cmtval) {
            if ( cmtval != null ){
                if ( this.cmtval == null){
                    this.cmtval = cmtval;
                }else{
                    this.cmtval += cmtval;
                }
            }
        }
        
        public Date getEtd() {
            return etd;
        }
        
        public void setEtd(Date etd) {
            this.etd = etd;
        }
        
        public Date getDelivery() {
            return delivery;
        }
        
        public void setDelivery(Date delivery) {
            this.delivery = delivery;
        }
        
        public void setFederated(boolean federated) {
            this.federated = federated;
        }
        
        
        public boolean isFederated() {
            return federated;
        }
        
        public Double getProfit(){
            Double profit = new Double(0.0);
            if ( revenue != null ){
                profit = revenue;
            }
            
            if ( fabric != null ){
                profit -= fabric;
            }
            
            if ( accs != null){
                profit -= accs;
            }
            
            if (isFederated() && cmtval != null){
                profit -= cmtval;
            }
            
            return profit;
        }
        
        public String getLot() {
            return lot;
        }
        
        public void setLot(String lot) {
            this.lot = lot;
        }
        
    }; //Inner class end
    
    
    public FoxyCRptsProfitPage() {
        super(new String("ProfitReportsForm"));
        
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
            str = " [" + this.searchKey + "] As At ["  +  fmt2.format(new Date()) + "] ";
        }else{
            str +=  " (" + this.getCountryName() + ") ";
            
            if ( "1".equals(this.federated)){//1-Federated, 0-Non-Federated, else no need to display
                str +=  " [Federated Customer] ";
            }
            if ("0".equals(this.federated)){
                str +=  " [Non-Federated Customer] ";
            }
            
            str +=  "Delivery Date From [" +  fmt1.format(this.getFromDate()) + "] ";
            if ( this.getToDate() != null) {
                str +=  " To [" +  fmt1.format(this.getToDate()) + "] ";
            }
            str +=  " As At [" +  fmt2.format(new Date()) + "] ";
        }
        return str;
    }
    
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getFederated(){
        return this.federated;
    }
    
    public void setFederated(String federated) {
        this.federated = federated;
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
    
    public void setJan01(Date Jan01) {
        this.Jan01 = Jan01;
    }
    
    public Date getJan01() {
        return Jan01;
    }
    
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        
        if ( this.getFromDate() != null ){
            Calendar cal = Calendar.getInstance();
            // This is the right way to set the month
            cal.setTime(this.getFromDate());
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            
            this.Jan01 = cal.getTime();
        }
        
        return (null);
    }
    
    
    public DataModel getReportDataByRefNo(){
        List<ReportDataBean>  dataList = null;
        if ( this.searchKey != null){
            try {
                //System.err.println("Search key [" +  this.searchKey + "]");
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                
                
                String qstr = "SELECT os.orderid as orderid, os.srefid as  srefid, os.month as month, os.location as location, ";
                qstr += " os.delivery as delivery, p.description as origin, cust.federated as  federated, cust.custname as custname, ";
                qstr += "        (SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "         WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid) as fabricval, ";
                qstr += "        (SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "         WHERE iv.type = 'ACCS' AND  ivm.srefid = os.srefid) as accsval, ";
                qstr += "        (SELECT sum(sd.fobval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "         WHERE sd.srefid = os.srefid) as fobval, ";
                qstr += "        (SELECT sum(sd.cmtval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "         WHERE sd.srefid = os.srefid) as cmtval, ";
                qstr += "        (SELECT sum(sd.revenue*s.forexrate) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "         WHERE sd.srefid = os.srefid) as revenueval, ";
                qstr += "        (SELECT MAX(sd.etd) FROM salesinvoicedetail as sd WHERE sd.srefid = os.srefid) as etd "; //there could be multiple different etd per srefid in sd!!
                qstr += " FROM ordsummary as os";
                qstr += " LEFT JOIN orders od ON od.orderid = os.orderid ";
                qstr += " LEFT JOIN customer cust ON cust.custcode = od.custcode ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += " LEFT JOIN parameter p on p.code = fm.countrycode AND p.category = 'CNTY' ";
                qstr += " WHERE os.orderid  LIKE '" + this.searchKey.replace('*', '%') + "' ";
                qstr += " AND os.srefid IN (SELECT srefid FROM invmovement) ";
                qstr += " ORDER BY orderid";
                
                //System.err.println(qstr);
                //System.err.println("from date " + this.getFromDate() + " To date " + this.getToDate() +  "origin = " + this.country);
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                
                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("month", Hibernate.STRING);
                q.addScalar("location", Hibernate.STRING);
                q.addScalar("srefid", Hibernate.LONG);
                q.addScalar("delivery", Hibernate.DATE);
                q.addScalar("origin", Hibernate.STRING);
                q.addScalar("federated", Hibernate.BOOLEAN);
                q.addScalar("custname", Hibernate.STRING);
                q.addScalar("fabricval", Hibernate.DOUBLE);
                q.addScalar("accsval", Hibernate.DOUBLE);
                q.addScalar("fobval", Hibernate.DOUBLE);
                q.addScalar("cmtval", Hibernate.DOUBLE);
                q.addScalar("revenueval", Hibernate.DOUBLE);
                q.addScalar("etd", Hibernate.DATE);
                
                //System.err.println("Test 111111112222233333344444444");
                
                Iterator it = q.list().iterator();
                //System.err.println("Total records = " + q.list().size());
                tx.commit();
                
                if ( dataList == null){
                    dataList = new ArrayList();
                }
                
                String tmporderid1 = null;
                String tmporderid2 = null;
                String lotid = null;
                Long tmpsrefid = null;
                Double tmpd1 = null;
                ReportDataBean tot = new ReportDataBean();
                ReportDataBean gtot = new ReportDataBean();
                tot.setLot("Subtotal:");
                gtot.setLot("Gandtotal:");
                try { //loop of all applicable order id
                    while (it.hasNext()){
                        Object[] tmpRow = (Object[])it.next();
                        int i = 0;
                        ReportDataBean obj = new  ReportDataBean();
                        
                        tmporderid1 = (String)tmpRow[i++];
                        lotid = tmporderid1;
                        lotid += (String)tmpRow[i++];
                        lotid += (String)tmpRow[i++];
                        obj.setLot(lotid);
                        
                        tmpsrefid = (Long)tmpRow[i++];
                        obj.setRefno(tmporderid1);
                        obj.setDelivery((Date)tmpRow[i++]);
                        obj.setOrigin((String)tmpRow[i++]);
                        obj.setFederated((Boolean)tmpRow[i++]);
                        obj.setCustomer((String)tmpRow[i++]);
                        
                        tmpd1 = (Double)tmpRow[i++];//fabric value
                        obj.setFabric(super.roundDouble(tmpd1,2));
                        
                        tmpd1 = (Double)tmpRow[i++];//accs value
                        obj.setAccs(super.roundDouble(tmpd1,2));
                        
                        tmpd1 = (Double)tmpRow[i++];//fob value
                        obj.setFobval(super.roundDouble(tmpd1,2));
                        
                        tmpd1 = (Double)tmpRow[i++];//cmt value
                        obj.setCmtval(super.roundDouble(tmpd1,2));
                        
                        tmpd1 = (Double)tmpRow[i++];//revenue value
                        obj.setRevenue(super.roundDouble(tmpd1,2));
                        
                        obj.setEtd((Date)tmpRow[i++]);
                        
                        if ( tmporderid2 != null && ! tmporderid1.equals(tmporderid2) ){
                            dataList.add(tot);
                            tot = new ReportDataBean();
                            tot.setLot("Subtotal:");
                        }
                        
                        //System.err.println("tmporderid1 = " + tmporderid1 + "tmporderid2 = " + tmporderid2);
                        //System.err.println("tmporderid2 = " + tmporderid2);
                        tot.accumulateAccs(obj.getAccs());
                        tot.accumulateCmtval(obj.getCmtval());
                        tot.accumulateFabric(obj.getFabric());
                        tot.accumulateFobval(obj.getFobval());
                        tot.accumulateRevenue(obj.getRevenue());
                        
                        gtot.accumulateAccs(obj.getAccs());
                        gtot.accumulateCmtval(obj.getCmtval());
                        gtot.accumulateFabric(obj.getFabric());
                        gtot.accumulateFobval(obj.getFobval());
                        gtot.accumulateRevenue(obj.getRevenue());
                        
                        tmporderid2 = tmporderid1;
                        
                        dataList.add(obj);
                        
                    }
                } catch ( Exception e){
                    e.printStackTrace();
                }
                
                dataList.add(tot);
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
            } finally {
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
    
    
    public DataModel getReportDataByCntryDate(){
        List<ReportDataBean>  dataList = null;
        if ( this.country != null){
            try {
                //System.err.println("Search for records " +  this.country + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                
                String qstr = "";
                qstr += " SELECT os.orderid, os.srefid as srefid, cust.federated as federated, cust.custname as custname, ";
                qstr += "        sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "         WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid)) as fabricval, ";
                qstr += "        sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "         WHERE iv.type = 'ACCS' AND  ivm.srefid = os.srefid)) as accsval, ";
                qstr += "        sum((SELECT sum(sd.fobval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "         WHERE sd.srefid = os.srefid)) as fobval, ";
                qstr += "        sum((SELECT sum(sd.cmtval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "         WHERE sd.srefid = os.srefid)) as cmtval, ";
                qstr += "        sum((SELECT sum(sd.revenue*s.forexrate) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "         WHERE sd.srefid = os.srefid)) as revenueval ";
                qstr += " FROM ordsummary as os ";
                qstr += " LEFT JOIN orders od ON od.orderid = os.orderid  ";
                qstr += " LEFT JOIN customer cust ON cust.custcode = od.custcode  ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += " WHERE fm.countrycode = :pcountry ";
                
                //Only set federated filter if user selected, default is all (ignore this filter)
                if ( this.federated != null  && this.federated.length() > 0){
                    qstr += "  AND  cust.federated = :pfederated ";
                }
                qstr += " AND os.delivery >= :pdeliveryFrom ";
                qstr += " AND os.delivery <= :pdeliveryTo ";
                qstr += " AND srefid IN (SELECT srefid FROM invmovement) ";
                qstr += " GROUP BY orderid ";
                qstr += " ORDER BY orderid ";
                
                //System.err.println(qstr);
                //System.err.println("from date " + this.getFromDate() + " To date " + this.getToDate() +  "origin = " + this.country);
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                
                q.setString("pcountry", this.country);
                
                //Only set federated filter if user selected, default is all (ignore this filter)
                if ( this.federated != null  && this.federated.length() > 0){
                    q.setBoolean("pfederated", ("1".equals(this.federated))?true:false);
                }
                
                q.setDate("pdeliveryFrom", this.getFromDate());
                q.setDate("pdeliveryTo", this.getToDate());
                
                
                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("federated", Hibernate.BOOLEAN);
                q.addScalar("custname", Hibernate.STRING);
                q.addScalar("fabricval", Hibernate.DOUBLE);
                q.addScalar("accsval", Hibernate.DOUBLE);
                q.addScalar("fobval", Hibernate.DOUBLE);
                q.addScalar("cmtval", Hibernate.DOUBLE);
                q.addScalar("revenueval", Hibernate.DOUBLE);
                
                
                //System.err.println("Test 111111112222233333344444444");
                
                Iterator it = q.list().iterator();
                //System.err.println("Total records = " + q.list().size());
                tx.commit();
                
                if ( dataList == null){
                    dataList = new ArrayList();
                }
                
                String tmporderid = null;
                Double tmpd1 = null;
                ReportDataBean gtot = new ReportDataBean();
                gtot.setRefno("Grandtotal:");
                
                try { //loop of all applicable order id
                    while (it.hasNext()){
                        Object[] tmpRow = (Object[])it.next();
                        int i = 0;
                        
                        ReportDataBean obj = new  ReportDataBean();
                        
                        tmporderid = (String)tmpRow[i++];
                        obj.setFederated((Boolean)tmpRow[i++]);
                        obj.setCustomer((String)tmpRow[i++]);
                        
                        obj.setRefno(tmporderid);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//fabric value
                        obj.setFabric(tmpd1);
                        gtot.accumulateFabric(tmpd1);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//accs value
                        obj.setAccs(tmpd1);
                        gtot.accumulateAccs(tmpd1);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//fob value
                        obj.setFobval(tmpd1);
                        gtot.accumulateFobval(tmpd1);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//cmt value
                        obj.setCmtval(tmpd1);
                        gtot.accumulateCmtval(tmpd1);
                        
                        tmpd1 = super.roundDouble((Double)tmpRow[i++],2);//revenue value
                        obj.setRevenue(tmpd1);
                        gtot.accumulateRevenue(tmpd1);
                        
                        //obj.set(String)tmpRow[idx++]);
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
