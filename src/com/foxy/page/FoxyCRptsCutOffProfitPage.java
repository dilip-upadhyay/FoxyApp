/*
 * FoxyCRptsCutOffProfitPage.java
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
public class FoxyCRptsCutOffProfitPage extends Page implements Serializable {

    private static String MENU_CODE = new String("FOXY");
    private DataModel dataListModel;
    private String country = null;
    private String refno = null;
    private String federated = null;
    private SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date today_date = new Date(); //current date

    public class ReportDataBean {

        private String refno = null;
        private String customer = null;
        private boolean federated = false;
        private Date deliveryDate = null;
        private Double fabricQtyB4 = null;
        private Double fabricSgdValB4 = null;
        private Double accsQtyB4 = null;
        private Double accsSgdValB4 = null;
        private Double revenueB4 = null;
        private Double fobvalB4 = null;
        private Double cmtvalB4 = null;
        private Double fabricQtyAft = null;
        private Double fabricSgdValAft = null;
        private Double accsQtyAft = null;
        private Double accsSgdValAft = null;
        private Double revenueAft = null;
        private Double fobvalAft = null;
        private Double cmtvalAft = null;
        private Double unShipQty = null;

        public String getRefno() {
            return refno;
        }

        public void setRefno(String refno) {
            this.refno = refno;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public void setFederated(boolean federated) {
            this.federated = federated;
        }

        public boolean isFederated() {
            return federated;
        }

        public Double getFabricQtyB4() {
            return fabricQtyB4;
        }

        public void setFabricQtyB4(Double fabricQtyB4) {
            this.fabricQtyB4 = fabricQtyB4;
        }

        public void accumulateFabricQtyB4(Double fabricQtyB4) {
            if (fabricQtyB4 != null) {
                if (this.fabricQtyB4 == null) {
                    this.fabricQtyB4 = fabricQtyB4;
                } else {
                    this.fabricQtyB4 += fabricQtyB4;
                }
            }
        }

        public Double getFabricSgdValB4() {
            return fabricSgdValB4;
        }

        public void setFabricSgdValB4(Double fabricSgdValB4) {
            this.fabricSgdValB4 = fabricSgdValB4;
        }

        public void accumulateFabricSgdValB4(Double fabricSgdValB4) {
            if (fabricSgdValB4 != null) {
                if (this.fabricSgdValB4 == null) {
                    this.fabricSgdValB4 = fabricSgdValB4;
                } else {
                    this.fabricSgdValB4 += fabricSgdValB4;
                }
            }
        }

        public Double getAccsQtyB4() {
            return accsQtyB4;
        }

        public void setAccsQtyB4(Double accsQtyB4) {
            this.accsQtyB4 = accsQtyB4;
        }

        public Double getAccsSgdValB4() {
            return accsSgdValB4;
        }

        public void setAccsSgdValB4(Double accsSgdValB4) {
            this.accsSgdValB4 = accsSgdValB4;
        }

        public void accumulateAccsSgdValB4(Double accsSgdValB4) {
            if (accsSgdValB4 != null) {
                if (this.accsSgdValB4 == null) {
                    this.accsSgdValB4 = accsSgdValB4;
                } else {
                    this.accsSgdValB4 += accsSgdValB4;
                }
            }
        }

        public Double getRevenueB4() {
            return revenueB4;
        }

        public void setRevenueB4(Double revenueB4) {
            this.revenueB4 = revenueB4;
        }

        public void accumulateRevenueB4(Double revenueB4) {
            if (revenueB4 != null) {
                if (this.revenueB4 == null) {
                    this.revenueB4 = revenueB4;
                } else {
                    this.revenueB4 += revenueB4;
                }
            }
        }

        public Double getAccsQtyAft() {
            return accsQtyAft;
        }

        public void setAccsQtyAft(Double accsQtyAft) {
            this.accsQtyAft = accsQtyAft;
        }

        public Double getAccsSgdValAft() {
            return accsSgdValAft;
        }

        public void setAccsSgdValAft(Double accsSgdValAft) {
            this.accsSgdValAft = accsSgdValAft;
        }

        public void accumulateAccsSgdValAft(Double accsSgdValAft) {
            if (accsSgdValAft != null) {
                if (this.accsSgdValAft == null) {
                    this.accsSgdValAft = accsSgdValAft;
                } else {
                    this.accsSgdValAft += accsSgdValAft;
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
            if (fabricQtyAft != null) {
                if (this.fabricQtyAft == null) {
                    this.fabricQtyAft = fabricQtyAft;
                } else {
                    this.fabricQtyAft += fabricQtyAft;
                }
            }
        }

        public Double getFabricSgdValAft() {
            return fabricSgdValAft;
        }

        public void setFabricSgdValAft(Double fabricSgdValAft) {
            this.fabricSgdValAft = fabricSgdValAft;
        }

        public void accumulateFabricSgdValAft(Double fabricSgdValAft) {
            if (fabricSgdValAft != null) {
                if (this.fabricSgdValAft == null) {
                    this.fabricSgdValAft = fabricSgdValAft;
                } else {
                    this.fabricSgdValAft += fabricSgdValAft;
                }
            }
        }

        public Double getRevenueAft() {
            return revenueAft;
        }

        public void setRevenueAft(Double revenueAft) {
            this.revenueAft = revenueAft;
        }

        public void accumulateRevenueAft(Double revenueAft) {
            if (revenueAft != null) {
                if (this.revenueAft == null) {
                    this.revenueAft = revenueAft;
                } else {
                    this.revenueAft += revenueAft;
                }
            }
        }

        public Double getProfitB4() {
            Double profit = new Double(0.0);
            if (revenueB4 != null) {
                profit = revenueB4;
            }

            if (fabricSgdValB4 != null) {
                profit -= fabricSgdValB4;
            }

            if (accsSgdValB4 != null) {
                profit -= accsSgdValB4;
            }

            if (isFederated() && cmtvalB4 != null) {
                profit -= cmtvalB4;
            }

            return profit;
        }

        public Double getProfitAft() {
            Double profit = new Double(0.0);
            if (revenueAft != null) {
                profit = revenueAft;
            }

            if (fabricSgdValAft != null) {
                profit -= fabricSgdValAft;
            }

            if (accsSgdValAft != null) {
                profit -= accsSgdValAft;
            }

            if (isFederated() && cmtvalAft != null) {
                profit -= cmtvalAft;
            }

            return profit;
        }

        public Double getUnShipQty() {
            return unShipQty;
        }

        public void setUnShipQty(Double unShipQty) {
            this.unShipQty = unShipQty;
        }

        public void accumulateUnShipQty(Double unShipQty) {
            if (unShipQty != null) {
                if (this.unShipQty == null) {
                    this.unShipQty = unShipQty;
                } else {
                    this.unShipQty += unShipQty;
                }
            }
        }

        public Date getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(Date deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public Double getFobvalB4() {
            return fobvalB4;
        }

        public void setFobvalB4(Double fobvalB4) {
            this.fobvalB4 = fobvalB4;
        }

        public void accumulateFobvalB4(Double fobvalB4) {
            if (fobvalB4 != null) {
                if (this.fobvalB4 == null) {
                    this.fobvalB4 = fobvalB4;
                } else {
                    this.fobvalB4 += fobvalB4;
                }
            }
        }

        public Double getCmtvalB4() {
            return cmtvalB4;
        }

        public void setCmtvalB4(Double cmtvalB4) {
            this.cmtvalB4 = cmtvalB4;
        }

        public void accumulateCmtvalB4(Double cmtvalB4) {
            if (cmtvalB4 != null) {
                if (this.cmtvalB4 == null) {
                    this.cmtvalB4 = cmtvalB4;
                } else {
                    this.cmtvalB4 += cmtvalB4;
                }
            }
        }

        public String getFormatCmtval() {
            if (this.customer == null) {
                return "TOTAL";
            } else if (isFederated() == false) {
                return "CMT";
            } else {
                return null;
            }
        }

        public Double getFobvalAft() {
            return fobvalAft;
        }

        public void setFobvalAft(Double fobvalAft) {
            this.fobvalAft = fobvalAft;
        }

        public void accumulateFobvalAft(Double fobvalAft) {
            if (fobvalAft != null) {
                if (this.fobvalAft == null) {
                    this.fobvalAft = fobvalAft;
                } else {
                    this.fobvalAft += fobvalAft;
                }
            }
        }

        public Double getCmtvalAft() {
            return cmtvalAft;
        }

        public void setCmtvalAft(Double cmtvalAft) {
            this.cmtvalAft = cmtvalAft;
        }

        public void accumulateCmtvalAft(Double cmtvalAft) {
            if (cmtvalAft != null) {
                if (this.cmtvalAft == null) {
                    this.cmtvalAft = cmtvalAft;
                } else {
                    this.cmtvalAft += cmtvalAft;
                }
            }
        }
    }; //Inner class end

    public FoxyCRptsCutOffProfitPage() {
        super(new String("CutOffProfitReportsForm"));

        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFederated() {
        return this.federated;
    }

    public void setFederated(String federated) {
        this.federated = federated;
    }

    public String getTitle() {
        String str = " ";

        if (this.searchKey != null) {
            str = " [" + this.searchKey + "] ";
        } else {
            str += "(" + this.getCountryName() + ") ";
        }

        str += "Cut Off Date [" + fmt1.format(this.getFromDate()) + "] ";

        if ("1".equals(this.federated)) {//1-Federated, 0-Non-Federated, else no need to display
            str += " For [Federated Customer] ";
        }
        if ("0".equals(this.federated)) {
            str += " For [Non-Federated Customer] ";
        }

        str += "As At [" + fmt2.format(new Date()) + "] ";

        return str;
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

    public String getCountryName() {
        ListData ld = (ListData) getBean("listData");
        return (ld.getCountryDesc(this.getCountry(), 0));
        //return this.country;
    }

    public String search() {
        this.foxySessionData.setAction(LST);

        if (this.getFromDate() != null) {
            Calendar cal = Calendar.getInstance();
            // This is the right way to set the month
            cal.setTime(this.getFromDate());
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

        }

        return (null);
    }

    public DataModel getReportDataByRefNo() {
        List<ReportDataBean> dataList = null;
        if (this.searchKey != null) {
            try {
                //System.err.println("Search for records " +  this.country + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();

                String qstr = "";
                qstr += " SELECT os.orderid, os.srefid as srefid, cust.federated as federated, cust.custname as custname, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate1 )) as fabricvalb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate2)) as fabricvalaft, ";
                qstr += "         sum((SELECT sum(ivm.quantity) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate3 )) as fabricqtyb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate4)) as fabricqtyaft, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'ACCS' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate5)) as accsvalb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'ACCS' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate6)) as accsvalaft, ";
                qstr += "         sum((SELECT sum(sd.revenue*s.forexrate) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate7)) as revenuevalb4, ";
                qstr += "         sum((SELECT sum(sd.revenue*s.forexrate) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery >  :pcutofdate8)) as revenuevalaft, ";
                qstr += "         sum((SELECT sum(sd.fobval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate9)) as fobvalb4, ";
                qstr += "         sum((SELECT sum(sd.fobval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery >  :pcutofdate10)) as fobvalaft, ";
                qstr += "         sum((SELECT sum(sd.cmtval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate11)) as cmtvalb4, ";
                qstr += "         sum((SELECT sum(sd.cmtval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery >  :pcutofdate12)) as cmtvalaft, ";
                qstr += "         sum((SELECT sum(sd.qtypcs) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate13)) as salesinvqty, ";
                qstr += "         sum(os.qtypcs) as mktlotqty ";
                qstr += " FROM ordsummary as os ";
                qstr += " LEFT JOIN orders od ON od.orderid = os.orderid ";
                qstr += " LEFT JOIN customer cust ON cust.custcode = od.custcode ";
                qstr += " WHERE os.orderid  LIKE '" + this.searchKey.replace('*', '%') + "' ";

                //Only set federated filter if user selected, default is all (ignore this filter)
                if (this.federated != null && this.federated.length() > 0) {
                    qstr += "  AND  cust.federated = :pfederated ";
                }
                qstr += " AND srefid IN (SELECT srefid FROM invmovement) ";
                qstr += " GROUP BY orderid ";
                qstr += " ORDER BY orderid ";


                //System.err.println(qstr);
                //System.err.println("from date " + this.getFromDate() + " To date " + this.getToDate() +  "origin = " + this.country);
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                q.setDate("pcutofdate1", this.getFromDate());
                q.setDate("pcutofdate2", this.getFromDate());
                q.setDate("pcutofdate3", this.getFromDate());
                q.setDate("pcutofdate4", this.getFromDate());
                q.setDate("pcutofdate5", this.getFromDate());
                q.setDate("pcutofdate6", this.getFromDate());
                q.setDate("pcutofdate7", this.getFromDate());
                q.setDate("pcutofdate8", this.getFromDate());
                q.setDate("pcutofdate9", this.getFromDate());
                q.setDate("pcutofdate10", this.getFromDate());
                q.setDate("pcutofdate11", this.getFromDate());
                q.setDate("pcutofdate12", this.getFromDate());
                q.setDate("pcutofdate13", this.getFromDate());

                //Only set federated filter if user selected, default is all (ignore this filter)
                if (this.federated != null && this.federated.length() > 0) {
                    q.setBoolean("pfederated", ("1".equals(this.federated)) ? true : false);
                }


                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("federated", Hibernate.BOOLEAN);
                q.addScalar("custname", Hibernate.STRING);
                q.addScalar("fabricvalb4", Hibernate.DOUBLE);
                q.addScalar("fabricvalaft", Hibernate.DOUBLE);
                q.addScalar("fabricqtyb4", Hibernate.DOUBLE);
                q.addScalar("fabricqtyaft", Hibernate.DOUBLE);
                q.addScalar("accsvalb4", Hibernate.DOUBLE);
                q.addScalar("accsvalaft", Hibernate.DOUBLE);
                q.addScalar("revenuevalb4", Hibernate.DOUBLE);
                q.addScalar("revenuevalaft", Hibernate.DOUBLE);
                q.addScalar("fobvalb4", Hibernate.DOUBLE);
                q.addScalar("fobvalaft", Hibernate.DOUBLE);
                q.addScalar("cmtvalb4", Hibernate.DOUBLE);
                q.addScalar("cmtvalaft", Hibernate.DOUBLE);
                q.addScalar("salesinvqty", Hibernate.DOUBLE);
                q.addScalar("mktlotqty", Hibernate.DOUBLE);


                //System.err.println("Test 111111112222233333344444444");

                Iterator it = q.list().iterator();
                //System.err.println("Total records = " + q.list().size());
                tx.commit();

                if (dataList == null) {
                    dataList = new ArrayList();
                }

                String tmporderid = null;
                Double tmpd1 = null;
                Double tmpd2 = null;
                ReportDataBean gtot = new ReportDataBean();
                gtot.setRefno("Grandtotal:");

                try { //loop of all applicable order id
                    while (it.hasNext()) {
                        Object[] tmpRow = (Object[]) it.next();
                        int i = 0;

                        ReportDataBean obj = new ReportDataBean();

                        tmporderid = (String) tmpRow[i++];
                        obj.setRefno(tmporderid);
                        obj.setFederated((Boolean) tmpRow[i++]);
                        obj.setCustomer((String) tmpRow[i++]);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric value b4
                        obj.setFabricSgdValB4(tmpd1);
                        gtot.accumulateFabricSgdValB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric value aft
                        obj.setFabricSgdValAft(tmpd1);
                        gtot.accumulateFabricSgdValAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric Qty b4
                        obj.setFabricQtyB4(tmpd1);
                        gtot.accumulateFabricQtyB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric Qty aft
                        obj.setFabricQtyAft(tmpd1);
                        gtot.accumulateFabricQtyAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//accs value b4
                        obj.setAccsSgdValB4(tmpd1);
                        gtot.accumulateAccsSgdValB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//accs value aft
                        obj.setAccsSgdValAft(tmpd1);
                        gtot.accumulateAccsSgdValAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//revenue value b4
                        obj.setRevenueB4(tmpd1);
                        gtot.accumulateRevenueB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//revenue value aft
                        obj.setRevenueAft(tmpd1);
                        gtot.accumulateRevenueAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//FOB value b4
                        obj.setFobvalB4(tmpd1);
                        gtot.accumulateFobvalB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//FOB value aft
                        obj.setFobvalAft(tmpd1);
                        gtot.accumulateFobvalAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//CMT value b4
                        obj.setCmtvalB4(tmpd1);
                        gtot.accumulateCmtvalB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//CMT value aft
                        obj.setCmtvalAft(tmpd1);
                        gtot.accumulateCmtvalAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//salesinvqty value
                        tmpd2 = super.roundDouble((Double) tmpRow[i++], 2);//mktlotqty value
                        if (tmpd1 != null && tmpd2 != null) {
                            tmpd2 = tmpd2 - tmpd1;
                        }
                        obj.setUnShipQty(tmpd2);
                        gtot.accumulateUnShipQty(tmpd2);

                        dataList.add(obj);
                    }
                } catch (Exception e) {
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
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } finally {
                HibernateUtil.closeSession();
            }
        } else {
            System.err.println("No records ....!!! Search key is null");
        }
        //Avoid null pointer exception
        if (dataListModel == null) {
            System.err.println("No records ....!!!");
            dataListModel = new ListDataModel();
        }

        return dataListModel;
    }

    public DataModel getReportDataByCountry() {
        List<ReportDataBean> dataList = null;
        if (this.country != null) {
            try {
                //System.err.println("Search for records " +  this.country + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();

                String qstr = "";
                qstr += " SELECT os.orderid, os.srefid as srefid, cust.federated as federated, cust.custname as custname, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate1 )) as fabricvalb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate2)) as fabricvalaft, ";
                qstr += "         sum((SELECT sum(ivm.quantity) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate3 )) as fabricqtyb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'FABRIC' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate4)) as fabricqtyaft, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'ACCS' AND  ivm.srefid = os.srefid and os.delivery <= :pcutofdate5)) as accsvalb4, ";
                qstr += "         sum((SELECT sum(ivm.quantity*ivm.unitcost*iv.forexrate) FROM invmovement AS ivm LEFT JOIN inventory AS iv ON iv.invrefid = ivm.invrefid ";
                qstr += "          WHERE iv.type = 'ACCS' AND  ivm.srefid = os.srefid and os.delivery > :pcutofdate6)) as accsvalaft, ";
                qstr += "         sum((SELECT sum(sd.revenue*s.forexrate) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate7)) as revenuevalb4, ";
                qstr += "         sum((SELECT sum(sd.revenue*s.forexrate) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery >  :pcutofdate8)) as revenuevalaft, ";
                qstr += "         sum((SELECT sum(sd.fobval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate9)) as fobvalb4, ";
                qstr += "         sum((SELECT sum(sd.fobval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery >  :pcutofdate10)) as fobvalaft, ";
                qstr += "         sum((SELECT sum(sd.cmtval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate11)) as cmtvalb4, ";
                qstr += "         sum((SELECT sum(sd.cmtval*s.forexrate)  FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery >  :pcutofdate12)) as cmtvalaft, ";
                qstr += "         sum((SELECT sum(sd.qtypcs) FROM salesinvoicedetail sd LEFT JOIN salesinvoice s ON s.saleinvid = sd.saleinvid ";
                qstr += "          WHERE sd.srefid = os.srefid and os.delivery <= :pcutofdate13)) as salesinvqty, ";
                qstr += "         sum(os.qtypcs) as mktlotqty ";
                qstr += " FROM ordsummary as os ";
                qstr += " LEFT JOIN orders od ON od.orderid = os.orderid ";
                qstr += " LEFT JOIN customer cust ON cust.custcode = od.custcode ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += " WHERE fm.countrycode = :pcountry ";

                //Only set federated filter if user selected, default is all (ignore this filter)
                if (this.federated != null && this.federated.length() > 0) {
                    qstr += "  AND  cust.federated = :pfederated ";
                }
                qstr += " AND srefid IN (SELECT srefid FROM invmovement) ";
                qstr += " GROUP BY orderid ";
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
                q.setDate("pcutofdate6", this.getFromDate());
                q.setDate("pcutofdate7", this.getFromDate());
                q.setDate("pcutofdate8", this.getFromDate());
                q.setDate("pcutofdate9", this.getFromDate());
                q.setDate("pcutofdate10", this.getFromDate());
                q.setDate("pcutofdate11", this.getFromDate());
                q.setDate("pcutofdate12", this.getFromDate());
                q.setDate("pcutofdate13", this.getFromDate());

                //Only set federated filter if user selected, default is all (ignore this filter)
                if (this.federated != null && this.federated.length() > 0) {
                    q.setBoolean("pfederated", ("1".equals(this.federated)) ? true : false);
                }


                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("federated", Hibernate.BOOLEAN);
                q.addScalar("custname", Hibernate.STRING);
                q.addScalar("fabricvalb4", Hibernate.DOUBLE);
                q.addScalar("fabricvalaft", Hibernate.DOUBLE);
                q.addScalar("fabricqtyb4", Hibernate.DOUBLE);
                q.addScalar("fabricqtyaft", Hibernate.DOUBLE);
                q.addScalar("accsvalb4", Hibernate.DOUBLE);
                q.addScalar("accsvalaft", Hibernate.DOUBLE);
                q.addScalar("revenuevalb4", Hibernate.DOUBLE);
                q.addScalar("revenuevalaft", Hibernate.DOUBLE);
                q.addScalar("fobvalb4", Hibernate.DOUBLE);
                q.addScalar("fobvalaft", Hibernate.DOUBLE);
                q.addScalar("cmtvalb4", Hibernate.DOUBLE);
                q.addScalar("cmtvalaft", Hibernate.DOUBLE);
                q.addScalar("salesinvqty", Hibernate.DOUBLE);
                q.addScalar("mktlotqty", Hibernate.DOUBLE);


                //System.err.println("Test 111111112222233333344444444");

                Iterator it = q.list().iterator();
                //System.err.println("Total records = " + q.list().size());
                tx.commit();

                if (dataList == null) {
                    dataList = new ArrayList();
                }

                String tmporderid = null;
                Double tmpd1 = null;
                Double tmpd2 = null;
                ReportDataBean gtot = new ReportDataBean();
                gtot.setRefno("Grandtotal:");

                try { //loop of all applicable order id
                    while (it.hasNext()) {
                        Object[] tmpRow = (Object[]) it.next();
                        int i = 0;

                        ReportDataBean obj = new ReportDataBean();

                        tmporderid = (String) tmpRow[i++];
                        obj.setRefno(tmporderid);
                        obj.setFederated((Boolean) tmpRow[i++]);
                        obj.setCustomer((String) tmpRow[i++]);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric value b4
                        obj.setFabricSgdValB4(tmpd1);
                        gtot.accumulateFabricSgdValB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric value aft
                        obj.setFabricSgdValAft(tmpd1);
                        gtot.accumulateFabricSgdValAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric Qty b4
                        obj.setFabricQtyB4(tmpd1);
                        gtot.accumulateFabricQtyB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//fabric Qty aft
                        obj.setFabricQtyAft(tmpd1);
                        gtot.accumulateFabricQtyAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//accs value b4
                        obj.setAccsSgdValB4(tmpd1);
                        gtot.accumulateAccsSgdValB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//accs value aft
                        obj.setAccsSgdValAft(tmpd1);
                        gtot.accumulateAccsSgdValAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//revenue value b4
                        obj.setRevenueB4(tmpd1);
                        gtot.accumulateRevenueB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//revenue value aft
                        obj.setRevenueAft(tmpd1);
                        gtot.accumulateRevenueAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//FOB value b4
                        obj.setFobvalB4(tmpd1);
                        gtot.accumulateFobvalB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//FOB value aft
                        obj.setFobvalAft(tmpd1);
                        gtot.accumulateFobvalAft(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//CMT value b4
                        obj.setCmtvalB4(tmpd1);
                        gtot.accumulateCmtvalB4(tmpd1);

                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//CMT value aft
                        obj.setCmtvalAft(tmpd1);
                        gtot.accumulateCmtvalAft(tmpd1);


                        tmpd1 = super.roundDouble((Double) tmpRow[i++], 2);//salesinvqty value
                        tmpd2 = super.roundDouble((Double) tmpRow[i++], 2);//mktlotqty value
                        if (tmpd1 != null && tmpd2 != null) {
                            tmpd2 = tmpd2 - tmpd1;
                        }
                        obj.setUnShipQty(tmpd2);
                        gtot.accumulateUnShipQty(tmpd2);

                        dataList.add(obj);
                    }
                } catch (Exception e) {
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
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } finally {
                HibernateUtil.closeSession();
            }
        } else {
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
