/*
 * FoxyDownloadOrderConfirmationFormPage.java
 *
 * Created on June 24, 2012, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.page;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.foxy.db.Category;
import com.foxy.db.HibernateUtil;
import com.foxy.db.OrderSummary;
import com.foxy.db.Orders;
import com.foxy.util.ListData;
import com.foxy.util.OrderFormPageUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;

public class FoxyDownloadOrderConfirmationFormPage2 extends Page implements Serializable {

    private static String MENU_CODE = "FOXY";
    private String ordId = null;
    private String origin = null;
    private List madeInList = null;
    private ResourceBundle foxyParam = null;
    private char companycodelastchar;

    public class descAlpha implements Comparator {

        /**
         * Compare two object. Callback for sort or TreeMap. effectively returns
         * a-b; orders descending
         *
         * @param pFirst first object a to be compared
         *
         * @param pSecond second object b to be compared
         *
         * @return +1 if a>b, 0 if a=b, -1 if a<b
         */
        public final int compare(Object pFirst, Object pSecond) {
            String str1 = (String) pFirst;
            String str2 = (String) pSecond;
            int ret = str1.compareTo(str2);

            ret = ret * -1; //reverse the order becode descending
            return ret;
        } // end compare
    };

    //Class constructor
    public FoxyDownloadOrderConfirmationFormPage2() {
        super("DownloadOrderConfirmationForm");
        this.isAuthorize(MENU_CODE);

        Locale defaultEng = new Locale("en");
        //Always fixed to Eng, else path may have problems
        foxyParam = ResourceBundle.getBundle("Foxy", defaultEng);

        //Always fixed to Eng, else path may have problems
        ordId = foxySessionData.getOrderId();
    }

    private String getFoxyParam(String key) {
        String val = null;
        try {
            val = foxyParam.getString(key);
        } catch (MissingResourceException e) {
            //e.printStackTrace();
            System.err.println("Parameter key [" + key + "] not found");
        } finally {
        }
        return val;
    }

    public boolean isOrdIdSet() {
        if (ordId == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getOrdId() {
        return ordId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List getMadeInList() {
        if (madeInList == null) {
            madeInList = new ArrayList();

            if (this.isOrdIdSet()) { //customer code
                try {
                    Session session = (Session) HibernateUtil.currentSession();
                    Transaction tx = (Transaction) session.beginTransaction();

                    String qstr = "SELECT DISTINCT fm.countrycode as origin, param.description as countryname FROM factorymast fm ";
                    qstr = qstr + "LEFT JOIN parameter param ON  param.code = fm.countrycode AND param.category = 'CNTY' ";
                    qstr = qstr + "WHERE fm.factorycode IN (Select mainfactory from ordsummary where orderid = :porderid )";

                    //set parameter value
                    SQLQuery q = session.createSQLQuery(qstr);
                    q.setString("porderid", this.ordId);

                    //add scalar
                    q.addScalar("origin", Hibernate.STRING);
                    q.addScalar("countryname", Hibernate.STRING);

                    Iterator it = q.list().iterator();
                    int i = 0;

                    madeInList.add(new SelectItem("", "----All-----"));
                    while (it.hasNext()) {
                        Object[] tmpRow = (Object[]) it.next();
                        i = 0;
                        try {
                            madeInList.add(new SelectItem((String) tmpRow[i++], (String) tmpRow[i++]));
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }

                    tx.commit();
                } catch (HibernateException e) {
                    //do something here with the exception
                    //e.printStackTrace();
                    FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                    ctx.addMessage(null, fmsg);
                } catch (Exception e) {
                    //e.printStackTrace();
                    FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                    ctx.addMessage(null, fmsg);
                } finally {
                    HibernateUtil.closeSession();
                }
            } else {
                System.out.println("OderID not set in DownloadOrderConfirmationForm page");

            }
        }
        return madeInList;
    }

    //Wrapper method to return pdf format based on company code.
    public String OrderConfirmationFromByCountry() {
        String str = null;
        int pdfformat;

        // Get data from database
        try {
            Session session = (Session) HibernateUtil.currentSession();
            List order = session.createQuery("from Orders where orderid = '" + this.ordId + "'  AND status != 'D'").list();
            // Populate table with data
            for (int i = 0; i < order.size(); i++) {
                Orders ord = (Orders) order.get(i);
                str = ord.getCnameCode();

                //get the last characters out of company code (ie: '1' for CN1, '2' for CN2, '4' for C004, etc)
                companycodelastchar = str.charAt(str.length() - 1);
                //extract integer value out of it 
                pdfformat = companycodelastchar - '0';
                //since we have 3 format only, use mod 3 for map it.
                pdfformat = pdfformat % 3;

                System.err.println("Current format selected for pdf form - [" + pdfformat + "]");

                switch (pdfformat) {
                    case 0:
                        str = OrderConfirmationFormByCountryFmt01(ord);
                        break;
                    case 1:
                        str = OrderConfirmationFormByCountryFmt02(ord);
                        break;
                    case 2:
                        str = OrderConfirmationFormByCountryFmt03(ord);
                        break;
                    default:
                        System.err.println("ERROR -- OCF PDF format NOT FOUND IN (0-2) range - [" + pdfformat + "]");
                        break;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return str;
    }

    public String OrderConfirmationFormByCountryFmt01(Orders ord) {
        HttpServletResponse resp = (HttpServletResponse) this.ectx.getResponse();
        ServletOutputStream out = null;
        PdfPCell cell = null;
        String filename = null;  //File name to keep for imtermediate file
        String filepath = null;
        String tmpstr = null;
        TreeMap<String, Integer> sortedRmk = null;
        ListData ld = (ListData) getBean("listData");
        Date tmpdate;


        SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        SimpleDateFormat fmt2 = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat fmt3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat fmt4 = new SimpleDateFormat("yyyMMddHHmmss");

        Font font12 = new Font(Font.FontFamily.HELVETICA, 12);
        Font font10 = new Font(Font.FontFamily.HELVETICA, 10);
        Font font8 = new Font(Font.FontFamily.HELVETICA, 8);
        Font font7 = new Font(Font.FontFamily.HELVETICA, 7);

        Font tblHeader = new Font(Font.FontFamily.HELVETICA, 8);

        Rectangle border_l = new Rectangle(0f, 0f);
        //border_l.setBorderWidth(3);
        border_l.setBorderWidthLeft(1f);
        border_l.setBorderWidthBottom(0f);
        border_l.setBorderWidthRight(0f);
        border_l.setBorderWidthTop(1f);
        border_l.setBorderColor(BaseColor.GRAY);

        Rectangle border_lc = new Rectangle(0f, 0f);
        border_lc.setBorderWidthLeft(0f);
        border_lc.setBorderWidthBottom(0f);
        border_lc.setBorderWidthRight(1f);
        border_lc.setBorderWidthTop(1f);
        border_lc.setBorderColor(BaseColor.GRAY);

        Rectangle border_rc = new Rectangle(0f, 0f);
        border_rc.setBorderWidthLeft(0f);
        border_rc.setBorderWidthBottom(0f);
        border_rc.setBorderWidthRight(0f);
        border_rc.setBorderWidthTop(1f);
        border_rc.setBorderColor(BaseColor.GRAY);

        Rectangle border_r = new Rectangle(0f, 0f);
        border_r.setBorderWidthLeft(0f);
        border_r.setBorderWidthBottom(0f);
        border_r.setBorderWidthRight(1f);
        border_r.setBorderWidthTop(1f);
        border_r.setBorderColor(BaseColor.GRAY);

        Rectangle detail_r = new Rectangle(0f, 0f);
        detail_r.setBorderWidthLeft(0f);
        detail_r.setBorderWidthBottom(1f);
        detail_r.setBorderWidthRight(0f);
        detail_r.setBorderWidthTop(0f);


        Document document = new Document(PageSize.A4, 36, 36, 36, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            filepath = this.getFoxyParam("FileUploadPath");
            filename = filepath + File.separatorChar + this.ordId + ".jpg";

            File f = new File(filename);
            if (f.exists() == false) {
                filename = filepath + File.separatorChar + "default.jpg";
            }


            Image img = Image.getInstance(filename);
            img.setBorder(1);
            img.scalePercent(50);


            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new OrderFormPageUtil());
            document.open();

            /*
             * Content Here
             */
            PdfPTable datatable = new PdfPTable(4);     // Create table with 4 columns
            PdfPTable innertable = new PdfPTable(2);

            PdfPTable toptable = new PdfPTable(2);
            PdfPTable headertable = new PdfPTable(2);

            PdfPTable detailtable = new PdfPTable(11);
            PdfPTable remarktable = new PdfPTable(2); //Table for remarks if any
            //PdfPTable footertable = new PdfPTable(3);

            int colWidths[] = {15, 35, 15, 35};   // Column width

            int headerColWidths[] = {60, 40};   // Column width
            headertable.setWidths(headerColWidths);
            headertable.setWidthPercentage(100); // percentage
            headertable.getDefaultCell().setBorder(0);
            headertable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            headertable.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);

            int topColWidths[] = {50, 50};   // Column width
            toptable.setWidths(topColWidths);
            toptable.setWidthPercentage(100); // percentage
            toptable.getDefaultCell().setBorder(0);
            toptable.getDefaultCell().setFixedHeight(100);
            toptable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            toptable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            int innerColWidths[] = {30, 70};   // Column width
            innertable.setWidths(innerColWidths);
            innertable.setWidthPercentage(100); // percentage
            innertable.getDefaultCell().setBorder(0);
            innertable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            innertable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            datatable.setWidths(colWidths);
            datatable.setWidthPercentage(100); // percentage
            datatable.getDefaultCell().setPadding(3);
            datatable.getDefaultCell().setBorderWidth(1);
            datatable.getDefaultCell().setBorderColor(BaseColor.GRAY);
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            datatable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            int detailColWidths[] = {12, 20, 15, 15, 14, 12, 10, 10, 10, 14, 10};   // Column width
            detailtable.setWidths(detailColWidths);
            detailtable.setWidthPercentage(100); // percentage
            detailtable.getDefaultCell().setPadding(3);
            detailtable.getDefaultCell().setBorder(0);
            detailtable.getDefaultCell().setBorderWidthBottom(1);
            detailtable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            detailtable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);


            int remarkColWidths[] = {12, 88};   // Column width
            remarktable.setWidths(remarkColWidths);
            remarktable.setWidthPercentage(100); // percentage
            remarktable.getDefaultCell().setPadding(3);
            remarktable.getDefaultCell().setBorder(0);
            remarktable.getDefaultCell().setBorderWidthBottom(1);
            remarktable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            remarktable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            // Get data from database
            Session session = (Session) HibernateUtil.currentSession();

            String tmpDateStr = null;
            // Populate table with data

            //do not pass in 0 to close hibernate session for Company Name
            String companyNameFull = ld.getCompanyNameFull(ord.getCnameCode(), 1);
            headertable.addCell(new Phrase(companyNameFull, font12));

            tmpstr = "[" + companycodelastchar;
            if ("".equals(this.origin) || this.origin == null) {
                tmpstr = tmpstr + "/*] ";
            } else {
                tmpstr = tmpstr + "/" + this.origin + "] ";
            }

            headertable.addCell(new Phrase(tmpstr, font7));


            cell = new PdfPCell(img, false); //Don't use fix to cell, else image scale would not work
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setColspan(1);
            cell.setBorder(0);
            toptable.addCell(cell);

            cell = new PdfPCell(new Phrase("Order Confirmation", font12));
            cell.setColspan(1);
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Customer Code", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getCustCode(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("NEW ORDER", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("/ AMENDMENT / STOCK / CANCEL", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Brand/Division", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getCustBrand() + " / " + ord.getCustDivision(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Merchandiser", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getMerchandiser(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Style No", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getStyleCode(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("DailyCap/worker", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            if (ord.getDailyCap() != null) {
                cell = new PdfPCell(new Phrase(": " + ord.getDailyCap() + " dz/cm", font10));
            } else {
                cell = new PdfPCell(new Phrase(": ", font10));
            }
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Our Ref #", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getOrderId(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Price Term", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": CMT", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Ord Date", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            tmpdate = ord.getOrderDate();
            if (tmpdate == null) {
                cell = new PdfPCell(new Phrase(": ", font10));
            } else {
                cell = new PdfPCell(new Phrase(": " + fmt2.format(tmpdate), font10));
            }
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Season", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getSeason(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Description", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getDescription(), font10));
            cell.cloneNonPositionParameters(border_lc);
            //cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Graphic Emb", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            //since order table keep Graphic Type code, need to use listdata to get detail/name/desc
            String graphicType = ld.getGraphicTypeDesc(ord.getGraphicTypeCode());
            cell = new PdfPCell(new Phrase(": " + graphicType, font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabric Type", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFabricType(), font10));
            cell.cloneNonPositionParameters(border_lc);
            //cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Colouring", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            //since order table keep Graphic Type code, need to use listdata to get detail/name/desc
            String colourType = ld.getColourTypeDesc(ord.getColourTypeCode());
            cell = new PdfPCell(new Phrase(": " + colourType, font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabric Mill", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase(": " + ord.getFabricMill(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Wash Type", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getWash(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Factory CMT", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFtyCm(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Graphic/EMB Cost", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getGcost(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabrication", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFabrication(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);

            border_l.setBorderWidthBottom(1f);
            border_lc.setBorderWidthBottom(1f);
            border_rc.setBorderWidthBottom(1f);
            border_r.setBorderWidthBottom(1f);

            cell = new PdfPCell(new Phrase("Remark", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getRemark(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            //create SQL statement
            tmpstr = "from OrderSummary where orderid = '" + this.ordId + "' AND status != 'D' ";
            if ("".equals(this.origin) || this.origin == null) {
                //dont add any filter to origin column if to select all ordsummary info (dropdown box all)
            } else {
                tmpstr = tmpstr + "AND mainFactory IN (SELECT factoryCode from FactoryMast WHERE countryCode = '" + this.origin + "') ";
            }
            tmpstr = tmpstr + "order by month, location";

            List orders = session.createQuery(tmpstr).list();

            detailtable.addCell(new Phrase("SubRef #", tblHeader));
            //Right align col header
            cell = new PdfPCell(new Phrase("Quantity", tblHeader));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPaddingRight(10f);
            cell.cloneNonPositionParameters(detail_r);
            detailtable.addCell(cell);

            detailtable.addCell(new Phrase("Delivery", tblHeader));
            detailtable.addCell(new Phrase("Fabric Date", tblHeader));
            detailtable.addCell(new Phrase("Destination", tblHeader));
            detailtable.addCell(new Phrase("Category", tblHeader));
            detailtable.addCell(new Phrase("Quota", tblHeader));
            detailtable.addCell(new Phrase("ITax", tblHeader));
            detailtable.addCell(new Phrase("Ship By", tblHeader));
            detailtable.addCell(new Phrase("Factory", tblHeader));
            detailtable.addCell(new Phrase("Method", tblHeader));

            datatable.setHeaderRows(1); // this is the end of the table header


            Category cat = null;
            String rmkStr = null;
            Boolean firstRmk = true;
            for (int i = 0; i < orders.size(); i++) {
                OrderSummary ords = (OrderSummary) orders.get(i);
                detailtable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font8));

                //Dynamic quantity column
                if ("PCS".equals(ords.getUnit())) {
                    cell = new PdfPCell(new Phrase(ords.getQtyPcsRounded() + " pcs", font8));
                } else {
                    cell = new PdfPCell(new Phrase(ords.getQtyDznRounded() + " dzn", font8));
                }
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingRight(10f);
                cell.cloneNonPositionParameters(detail_r);
                detailtable.addCell(cell);

                tmpdate = ords.getDelivery();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(ords.getDelivery()), font8));
                } else {
                    detailtable.addCell(new Phrase("UNKNOWN", font8));
                }
                tmpdate = ords.getFabricDeliveryDate();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(tmpdate), font8));
                } else {
                    detailtable.addCell(new Phrase(" ", font8));
                }

                detailtable.addCell(new Phrase(ld.getDestinationDesc(ords.getDestination(), 1), font8));
                cat = ld.getCategory(ords.getCatId(), 1);
                if (cat != null) {
                    detailtable.addCell(new Phrase(cat.getCategory(), font8));
                } else {
                    detailtable.addCell(new Phrase("Unkown", font8));
                }
                detailtable.addCell(new Phrase(ords.getQuota(), font8));
                detailtable.addCell(new Phrase(ords.getImportTax(), font8));
                detailtable.addCell(new Phrase(ords.getShip(), font8));
                detailtable.addCell(new Phrase(ld.getFactoryNameShort(ords.getMainFactory()), font8));
                detailtable.addCell(new Phrase(ords.getOrderMethod(), font8));

                //Add in to display remark if no empty or null
                rmkStr = ords.getRemark();
                if (rmkStr != null) {
                    if (rmkStr.length() > 0) {
                        if (firstRmk) {
                            descAlpha da = new descAlpha();
                            sortedRmk = new TreeMap(da);
                            firstRmk = false;
                            remarktable.addCell(new Phrase("SubRef#", tblHeader));
                            remarktable.addCell(new Phrase("Remark", tblHeader));
                        }
                        if (ords.getUpdUsrId() == null) {
                            sortedRmk.put(fmt4.format(ords.getInsTime()), i);
                        } else {
                            sortedRmk.put(fmt4.format(ords.getUpdTime()), i);
                        }
                    }
                }
            }//End for inner loop

            if (sortedRmk != null) {
                if (sortedRmk.size() > 0) {
                    Set st = sortedRmk.entrySet();
                    Iterator it = st.iterator();
                    Integer idx;
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
                        idx = (Integer) me.getValue();
                        OrderSummary ords = (OrderSummary) orders.get(idx);

                        remarktable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font10));
                        remarktable.addCell(new Phrase(ords.getRemark(), font8));
                    }
                }
            }

            document.add(headertable);
            document.add(new Paragraph(" "));
            document.add(toptable);
            document.add(new Paragraph(" "));
            document.add(datatable);
            document.add(new Paragraph(" "));
            document.add(detailtable);
            if (firstRmk == false) {
                document.add(new Paragraph(" "));
                document.add(remarktable);
            }
            document.add(new Paragraph(" "));
            document.close();
        } catch (DocumentException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        resp.setContentType("application/pdf");

        String filestr;
        if ("".equals(this.origin) || this.origin == null) {
            filestr = this.ordId + "-All";
        } else {
            filestr = this.ordId + "-" + ld.getCountryDesc(this.origin, 1);
        }

        resp.setHeader("Content-Disposition", "attachment; filename=OCF-" + filestr + ".pdf");
        resp.setContentLength(baos.size());

        try {
            out = resp.getOutputStream();
            baos.writeTo(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        this.ctx.responseComplete();
        return null;
    }

    public String OrderConfirmationFormByCountryFmt02(Orders ord) {
        HttpServletResponse resp = (HttpServletResponse) this.ectx.getResponse();
        ServletOutputStream out = null;
        PdfPCell cell = null;
        String filename = null;  //File name to keep for imtermediate file
        String filepath = null;
        String tmpstr = null;
        TreeMap<String, Integer> sortedRmk = null;
        ListData ld = (ListData) getBean("listData");
        Date tmpdate;


        SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        SimpleDateFormat fmt2 = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat fmt3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat fmt4 = new SimpleDateFormat("yyyMMddHHmmss");

        Font font12 = new Font(Font.FontFamily.HELVETICA, 12);
        Font font10 = new Font(Font.FontFamily.HELVETICA, 10);
        Font font8 = new Font(Font.FontFamily.HELVETICA, 8);
        Font font7 = new Font(Font.FontFamily.HELVETICA, 7);

        Font tblHeader = new Font(Font.FontFamily.HELVETICA, 8);

        Rectangle border_l = new Rectangle(0f, 0f);
        border_l.setBorderWidthLeft(1f);
        border_l.setBorderWidthBottom(0f);
        border_l.setBorderWidthRight(0f);
        border_l.setBorderWidthTop(1f);
        border_l.setBorderColor(BaseColor.GRAY);

        Rectangle border_lc = new Rectangle(0f, 0f);
        border_lc.setBorderWidthLeft(0f);
        border_lc.setBorderWidthBottom(0f);
        border_lc.setBorderWidthRight(1f);
        border_lc.setBorderWidthTop(1f);
        border_lc.setBorderColor(BaseColor.GRAY);

        Rectangle border_rc = new Rectangle(0f, 0f);
        border_rc.setBorderWidthLeft(0f);
        border_rc.setBorderWidthBottom(0f);
        border_rc.setBorderWidthRight(0f);
        border_rc.setBorderWidthTop(1f);
        border_rc.setBorderColor(BaseColor.GRAY);

        Rectangle border_r = new Rectangle(0f, 0f);
        border_r.setBorderWidthLeft(0f);
        border_r.setBorderWidthBottom(0f);
        border_r.setBorderWidthRight(1f);
        border_r.setBorderWidthTop(1f);
        border_r.setBorderColor(BaseColor.GRAY);

        Rectangle detail_r = new Rectangle(0f, 0f);
        detail_r.setBorderWidthLeft(0f);
        detail_r.setBorderWidthBottom(1f);
        detail_r.setBorderWidthRight(0f);
        detail_r.setBorderWidthTop(0f);


        Document document = new Document(PageSize.A4, 36, 36, 36, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            filepath = this.getFoxyParam("FileUploadPath");
            filename = filepath + File.separatorChar + this.ordId + ".jpg";

            File f = new File(filename);
            if (f.exists() == false) {
                filename = filepath + File.separatorChar + "default.jpg";
            }


            Image img = Image.getInstance(filename);
            img.setBorder(1);
            img.scalePercent(50);


            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new OrderFormPageUtil());
            document.open();


            /*
             * Content Here
             */
            PdfPTable datatable = new PdfPTable(4);     // Create table with 4 columns
            PdfPTable innertable = new PdfPTable(2);

            PdfPTable toptable = new PdfPTable(2);
            PdfPTable headertable = new PdfPTable(2);

            PdfPTable detailtable = new PdfPTable(11);
            PdfPTable remarktable = new PdfPTable(2); //Table for remarks if any

            int colWidths[] = {15, 35, 15, 35};   // Column width

            int headerColWidths[] = {60, 40};   // Column width
            headertable.setWidths(headerColWidths);
            headertable.setWidthPercentage(100); // percentage
            headertable.getDefaultCell().setBorder(0);
            headertable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            headertable.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);

            int topColWidths[] = {50, 50};   // Column width
            toptable.setWidths(topColWidths);
            toptable.setWidthPercentage(100); // percentage
            toptable.getDefaultCell().setBorder(0);
            toptable.getDefaultCell().setFixedHeight(100);
            toptable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            toptable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            int innerColWidths[] = {30, 70};   // Column width
            innertable.setWidths(innerColWidths);
            innertable.setWidthPercentage(100); // percentage
            innertable.getDefaultCell().setBorder(0);
            innertable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            innertable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            datatable.setWidths(colWidths);
            datatable.setWidthPercentage(100); // percentage
            datatable.getDefaultCell().setPadding(3);
            datatable.getDefaultCell().setBorderWidth(1);
            datatable.getDefaultCell().setBorderColor(BaseColor.GRAY);
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            datatable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            int detailColWidths[] = {12, 20, 15, 15, 14, 12, 10, 10, 10, 14, 10};   // Column width
            detailtable.setWidths(detailColWidths);
            detailtable.setWidthPercentage(100); // percentage
            detailtable.getDefaultCell().setPadding(3);
            detailtable.getDefaultCell().setBorder(0);
            detailtable.getDefaultCell().setBorderWidthBottom(1);
            detailtable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            detailtable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);


            int remarkColWidths[] = {12, 88};   // Column width
            remarktable.setWidths(remarkColWidths);
            remarktable.setWidthPercentage(100); // percentage
            remarktable.getDefaultCell().setPadding(3);
            remarktable.getDefaultCell().setBorder(0);
            remarktable.getDefaultCell().setBorderWidthBottom(1);
            remarktable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            remarktable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            // Get data from database
            Session session = (Session) HibernateUtil.currentSession();

            headertable.addCell(new Phrase("Order Confirmation", font12));

            String tmpDateStr = null;
            // Populate table with data

            tmpstr = "[" + companycodelastchar;
            if ("".equals(this.origin) || this.origin == null) {
                tmpstr = tmpstr + "/*] ";
            } else {
                tmpstr = tmpstr + "/" + this.origin + "] ";
            }
            headertable.addCell(new Phrase(tmpstr, font7));

            //do not pass in 0 to close hibernate session for Company Name
            String companyNameFull = ld.getCompanyNameFull(ord.getCnameCode(), 1);
            cell = new PdfPCell(new Phrase(companyNameFull, font12));

            cell.setColspan(1);
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            toptable.addCell(cell);

            cell = new PdfPCell(img, false); //Don't use fix to cell, else image scale would not work
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setColspan(1);
            cell.setBorder(0);
            toptable.addCell(cell);

            cell = new PdfPCell(new Phrase("Customer Code", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getCustCode(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Merchandiser", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getMerchandiser(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Brand/Division", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getCustBrand() + " / " + ord.getCustDivision(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("NEW ORDER", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("/ AMENDMENT / STOCK / CANCEL", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Style No", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getStyleCode(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("DailyCap/worker", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            if (ord.getDailyCap() != null) {
                cell = new PdfPCell(new Phrase(": " + ord.getDailyCap() + " dz/cm", font10));
            } else {
                cell = new PdfPCell(new Phrase(": ", font10));
            }
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Our Ref #", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getOrderId(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Price Term", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": CMT", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Ord Date", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            tmpdate = ord.getOrderDate();
            if (tmpdate == null) {
                cell = new PdfPCell(new Phrase(": ", font10));
            } else {
                cell = new PdfPCell(new Phrase(": " + fmt2.format(tmpdate), font10));
            }
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Season", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getSeason(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Description", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getDescription(), font10));
            cell.cloneNonPositionParameters(border_lc);
            //cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Graphic Emb", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            //since order table keep Graphic Type code, need to use listdata to get detail/name/desc
            String graphicType = ld.getGraphicTypeDesc(ord.getGraphicTypeCode());
            cell = new PdfPCell(new Phrase(": " + graphicType, font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabric Type", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFabricType(), font10));
            cell.cloneNonPositionParameters(border_lc);
            //cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Colouring", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            //since order table keep Graphic Type code, need to use listdata to get detail/name/desc
            String colourType = ld.getColourTypeDesc(ord.getColourTypeCode());
            cell = new PdfPCell(new Phrase(": " + colourType, font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabrication", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFabrication(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabric Mill", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase(": " + ord.getFabricMill(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Wash Type", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getWash(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Factory CMT", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFtyCm(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Graphic/EMB Cost", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getGcost(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            border_l.setBorderWidthBottom(1f);
            border_lc.setBorderWidthBottom(1f);
            border_rc.setBorderWidthBottom(1f);
            border_r.setBorderWidthBottom(1f);

            cell = new PdfPCell(new Phrase("Remark", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getRemark(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            //create SQL statement
            tmpstr = "from OrderSummary where orderid = '" + this.ordId + "' AND status != 'D' ";
            if ("".equals(this.origin) || this.origin == null) {
                //dont add any filter to origin column if to select all ordsummary info (dropdown box all)
            } else {
                tmpstr = tmpstr + "AND origin = '" + this.origin + "' ";
            }
            tmpstr = tmpstr + "order by month, location";

            List orders = session.createQuery(tmpstr).list();

            detailtable.addCell(new Phrase("SubRef #", tblHeader));
            //Right align col header
            cell = new PdfPCell(new Phrase("Quantity", tblHeader));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPaddingRight(10f);
            cell.cloneNonPositionParameters(detail_r);
            detailtable.addCell(cell);

            detailtable.addCell(new Phrase("Delivery", tblHeader));
            detailtable.addCell(new Phrase("Fabric Date", tblHeader));
            detailtable.addCell(new Phrase("Destination", tblHeader));
            detailtable.addCell(new Phrase("Category", tblHeader));
            detailtable.addCell(new Phrase("Quota", tblHeader));
            detailtable.addCell(new Phrase("ITax", tblHeader));
            detailtable.addCell(new Phrase("Ship By", tblHeader));
            detailtable.addCell(new Phrase("Factory", tblHeader));
            detailtable.addCell(new Phrase("Method", tblHeader));

            datatable.setHeaderRows(1); // this is the end of the table header


            Category cat = null;
            String rmkStr = null;
            Boolean firstRmk = true;
            for (int i = 0; i < orders.size(); i++) {
                OrderSummary ords = (OrderSummary) orders.get(i);
                detailtable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font8));

                //Dynamic quantity column
                if ("PCS".equals(ords.getUnit())) {
                    cell = new PdfPCell(new Phrase(ords.getQtyPcsRounded() + " pcs", font8));
                } else {
                    cell = new PdfPCell(new Phrase(ords.getQtyDznRounded() + " dzn", font8));
                }
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingRight(10f);
                cell.cloneNonPositionParameters(detail_r);
                detailtable.addCell(cell);

                tmpdate = ords.getDelivery();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(ords.getDelivery()), font8));
                } else {
                    detailtable.addCell(new Phrase("UNKNOWN", font8));
                }
                tmpdate = ords.getFabricDeliveryDate();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(tmpdate), font8));
                } else {
                    detailtable.addCell(new Phrase(" ", font8));
                }

                detailtable.addCell(new Phrase(ld.getDestinationDesc(ords.getDestination(), 1), font8));
                cat = ld.getCategory(ords.getCatId(), 1);
                if (cat != null) {
                    detailtable.addCell(new Phrase(cat.getCategory(), font8));
                } else {
                    detailtable.addCell(new Phrase("Unkown", font8));
                }
                detailtable.addCell(new Phrase(ords.getQuota(), font8));
                detailtable.addCell(new Phrase(ords.getImportTax(), font8));
                detailtable.addCell(new Phrase(ords.getShip(), font8));
                detailtable.addCell(new Phrase(ld.getFactoryNameShort(ords.getMainFactory()), font8));
                detailtable.addCell(new Phrase(ords.getOrderMethod(), font8));

                //Add in to display remark if no empty or null
                rmkStr = ords.getRemark();
                if (rmkStr != null) {
                    if (rmkStr.length() > 0) {
                        if (firstRmk) {
                            descAlpha da = new descAlpha();
                            sortedRmk = new TreeMap(da);
                            firstRmk = false;
                            remarktable.addCell(new Phrase("SubRef#", tblHeader));
                            remarktable.addCell(new Phrase("Remark", tblHeader));
                        }
                        if (ords.getUpdUsrId() == null) {
                            sortedRmk.put(fmt4.format(ords.getInsTime()), i);
                        } else {
                            sortedRmk.put(fmt4.format(ords.getUpdTime()), i);
                        }
                    }
                }
            }//End for inner loop

            if (sortedRmk != null) {
                if (sortedRmk.size() > 0) {
                    Set st = sortedRmk.entrySet();
                    Iterator it = st.iterator();
                    Integer idx;
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
                        idx = (Integer) me.getValue();
                        OrderSummary ords = (OrderSummary) orders.get(idx);

                        remarktable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font10));
                        remarktable.addCell(new Phrase(ords.getRemark(), font8));
                    }
                }
            }

            document.add(headertable);
            document.add(new Paragraph(" "));
            document.add(toptable);
            document.add(new Paragraph(" "));
            document.add(datatable);
            document.add(new Paragraph(" "));
            document.add(detailtable);
            if (firstRmk == false) {
                document.add(new Paragraph(" "));
                document.add(remarktable);
            }
            document.add(new Paragraph(" "));
            document.close();
        } catch (DocumentException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        resp.setContentType("application/pdf");

        String filestr;
        if ("".equals(this.origin) || this.origin == null) {
            filestr = this.ordId + "-All";
        } else {
            filestr = this.ordId + "-" + ld.getCountryDesc(this.origin, 1);
        }

        resp.setHeader("Content-Disposition", "attachment; filename=OCF-" + filestr + ".pdf");
        resp.setContentLength(baos.size());

        try {
            out = resp.getOutputStream();
            baos.writeTo(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        this.ctx.responseComplete();
        return null;
    }

    public String OrderConfirmationFormByCountryFmt03(Orders ord) {
        HttpServletResponse resp = (HttpServletResponse) this.ectx.getResponse();
        ServletOutputStream out = null;
        PdfPCell cell = null;
        String filename = null;  //File name to keep for imtermediate file
        String filepath = null;
        String tmpstr = null;
        TreeMap<String, Integer> sortedRmk = null;
        ListData ld = (ListData) getBean("listData");
        Date tmpdate;


        SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        SimpleDateFormat fmt2 = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat fmt3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat fmt4 = new SimpleDateFormat("yyyMMddHHmmss");

        Font font12 = new Font(Font.FontFamily.HELVETICA, 12);
        Font font10 = new Font(Font.FontFamily.HELVETICA, 10);
        Font font8 = new Font(Font.FontFamily.HELVETICA, 8);
        Font font7 = new Font(Font.FontFamily.HELVETICA, 7);

        Font tblHeader = new Font(Font.FontFamily.HELVETICA, 8);

        Rectangle border_l = new Rectangle(0f, 0f);
        border_l.setBorderWidthLeft(1f);
        border_l.setBorderWidthBottom(0f);
        border_l.setBorderWidthRight(0f);
        border_l.setBorderWidthTop(1f);
        border_l.setBorderColor(BaseColor.GRAY);

        Rectangle border_lb = new Rectangle(0f, 0f);
        border_lb.setBorderWidthLeft(1f);
        border_lb.setBorderWidthBottom(1f);
        border_lb.setBorderWidthRight(0f);
        border_lb.setBorderWidthTop(1f);
        border_lb.setBorderColor(BaseColor.GRAY);

        Rectangle border_lc = new Rectangle(0f, 0f);
        border_lc.setBorderWidthLeft(0f);
        border_lc.setBorderWidthBottom(0f);
        border_lc.setBorderWidthRight(1f);
        border_lc.setBorderWidthTop(1f);
        border_lc.setBorderColor(BaseColor.GRAY);

        Rectangle border_lbc = new Rectangle(0f, 0f);
        border_lbc.setBorderWidthLeft(0f);
        border_lbc.setBorderWidthBottom(1f);
        border_lbc.setBorderWidthRight(1f);
        border_lbc.setBorderWidthTop(1f);
        border_lbc.setBorderColor(BaseColor.GRAY);

        Rectangle border_rc = new Rectangle(0f, 0f);
        border_rc.setBorderWidthLeft(0f);
        border_rc.setBorderWidthBottom(0f);
        border_rc.setBorderWidthRight(0f);
        border_rc.setBorderWidthTop(1f);
        border_rc.setBorderColor(BaseColor.GRAY);

        Rectangle border_r = new Rectangle(0f, 0f);
        border_r.setBorderWidthLeft(0f);
        border_r.setBorderWidthBottom(0f);
        border_r.setBorderWidthRight(1f);
        border_r.setBorderWidthTop(1f);
        border_r.setBorderColor(BaseColor.GRAY);

        Rectangle detail_r = new Rectangle(0f, 0f);
        detail_r.setBorderWidthLeft(0f);
        detail_r.setBorderWidthBottom(1f);
        detail_r.setBorderWidthRight(0f);
        detail_r.setBorderWidthTop(0f);


        Document document = new Document(PageSize.A4, 36, 36, 36, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            filepath = this.getFoxyParam("FileUploadPath");
            filename = filepath + File.separatorChar + this.ordId + ".jpg";

            File f = new File(filename);
            if (f.exists() == false) {
                filename = filepath + File.separatorChar + "default.jpg";
            }


            Image img = Image.getInstance(filename);
            img.setBorder(1);
            img.scalePercent(50);


            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new OrderFormPageUtil());
            document.open();


            /*
             * Content Here
             */
            PdfPTable datatable = new PdfPTable(4);     // Create table with 4 columns
            PdfPTable toptable = new PdfPTable(3);
            PdfPTable headertable = new PdfPTable(2);

            PdfPTable detailtable = new PdfPTable(11);
            PdfPTable remarktable = new PdfPTable(2); //Table for remarks if any

            int headerColWidths[] = {60, 40};   // Column width
            headertable.setWidths(headerColWidths);
            headertable.setWidthPercentage(100); // percentage
            headertable.getDefaultCell().setBorder(0);
            headertable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            headertable.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);

            int topColWidths[] = {15, 45, 40};   // Column width
            toptable.setWidths(topColWidths);
            toptable.setWidthPercentage(100); // percentage
            toptable.getDefaultCell().setBorderWidth(1);
            toptable.getDefaultCell().setPadding(3);
            toptable.getDefaultCell().setBorderColor(BaseColor.GRAY);
            toptable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            toptable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            int colWidths[] = {15, 35, 15, 35};   // Column width
            datatable.setWidths(colWidths);
            datatable.setWidthPercentage(100); // percentage
            datatable.getDefaultCell().setPadding(3);
            datatable.getDefaultCell().setBorderWidth(1);
            datatable.getDefaultCell().setBorderColor(BaseColor.GRAY);
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            datatable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            int detailColWidths[] = {12, 20, 15, 15, 14, 12, 10, 10, 10, 14, 10};   // Column width
            detailtable.setWidths(detailColWidths);
            detailtable.setWidthPercentage(100); // percentage
            detailtable.getDefaultCell().setPadding(3);
            detailtable.getDefaultCell().setBorder(0);
            detailtable.getDefaultCell().setBorderWidthBottom(1);
            detailtable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            detailtable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);


            int remarkColWidths[] = {12, 88};   // Column width
            remarktable.setWidths(remarkColWidths);
            remarktable.setWidthPercentage(100); // percentage
            remarktable.getDefaultCell().setPadding(3);
            remarktable.getDefaultCell().setBorder(0);
            remarktable.getDefaultCell().setBorderWidthBottom(1);
            remarktable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            remarktable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            // Get data from database
            Session session = (Session) HibernateUtil.currentSession();

            headertable.addCell(new Phrase("Order Confirmation", font12));

            String tmpDateStr = null;
            // Populate table with data

            tmpstr = "[" + companycodelastchar;
            if ("".equals(this.origin) || this.origin == null) {
                tmpstr = tmpstr + "/*] ";
            } else {
                tmpstr = tmpstr + "/" + this.origin + "] ";
            }
            headertable.addCell(new Phrase(tmpstr, font7));

            //do not pass in 0 to close hibernate session for Company Name
            String companyNameFull = ld.getCompanyNameFull(ord.getCnameCode(), 1);
            cell = new PdfPCell(new Phrase(companyNameFull, font12));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            toptable.addCell(cell);

            cell = new PdfPCell(img, false); //Don't use fix to cell, else image scale would not work
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setColspan(1);
            cell.setBorder(0);
            cell.setRowspan(10);
            cell.setPadding(0);
            toptable.addCell(cell);

            cell = new PdfPCell(new Phrase("Customer Code", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getCustCode(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Brand/Division", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getCustBrand() + " / " + ord.getCustDivision(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Style No", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getStyleCode(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Our Ref #", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getOrderId(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Ord Date", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            tmpdate = ord.getOrderDate();
            if (tmpdate == null) {
                cell = new PdfPCell(new Phrase(": ", font10));
            } else {
                cell = new PdfPCell(new Phrase(": " + fmt2.format(tmpdate), font10));
            }
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Season", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getSeason(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Fabric Type", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getFabricType(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Merchandiser", font10));
            cell.cloneNonPositionParameters(border_l);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getMerchandiser(), font10));
            cell.cloneNonPositionParameters(border_lc);
            toptable.addCell(cell);


            cell = new PdfPCell(new Phrase("Price Term", font10));
            cell.cloneNonPositionParameters(border_lb);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase(": CMT", font10));
            cell.cloneNonPositionParameters(border_lbc);
            toptable.addCell(cell);

            //////////////////////////////////////////////////////////

            cell = new PdfPCell(new Phrase("NEW ORDER", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("/ AMENDMENT / STOCK / CANCEL", font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("DailyCap/worker", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            if (ord.getDailyCap() != null) {
                cell = new PdfPCell(new Phrase(": " + ord.getDailyCap() + " dz/cm", font10));
            } else {
                cell = new PdfPCell(new Phrase(": ", font10));
            }
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Graphic Emb", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            //since order table keep Graphic Type code, need to use listdata to get detail/name/desc
            String graphicType = ld.getGraphicTypeDesc(ord.getGraphicTypeCode());
            cell = new PdfPCell(new Phrase(": " + graphicType, font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Colouring", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            //since order table keep Graphic Type code, need to use listdata to get detail/name/desc
            String colourType = ld.getColourTypeDesc(ord.getColourTypeCode());
            cell = new PdfPCell(new Phrase(": " + colourType, font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Fabric Mill", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase(": " + ord.getFabricMill(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Wash Type", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getWash(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("Factory CMT", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFtyCm(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);

            /*cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);*/
            
            cell = new PdfPCell(new Phrase("Graphic/EMB Cost", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getGcost(), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Description", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getDescription(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3);
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("Fabrication", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getFabrication(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);


            border_l.setBorderWidthBottom(1f);
            border_lc.setBorderWidthBottom(1f);
            border_rc.setBorderWidthBottom(1f);
            border_r.setBorderWidthBottom(1f);

            cell = new PdfPCell(new Phrase("Remark", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase(": " + ord.getRemark(), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3); //span across all the rest of 3 col (to force it occupy full row)
            datatable.addCell(cell);


            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);

            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);

            //create SQL statement
            tmpstr = "from OrderSummary where orderid = '" + this.ordId + "' AND status != 'D' ";
            if ("".equals(this.origin) || this.origin == null) {
                //dont add any filter to origin column if to select all ordsummary info (dropdown box all)
            } else {
                tmpstr = tmpstr + "AND origin = '" + this.origin + "' ";
            }
            tmpstr = tmpstr + "order by month, location";

            List orders = session.createQuery(tmpstr).list();

            detailtable.addCell(new Phrase("SubRef #", tblHeader));
            //Right align col header
            cell = new PdfPCell(new Phrase("Quantity", tblHeader));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPaddingRight(10f);
            cell.cloneNonPositionParameters(detail_r);
            detailtable.addCell(cell);

            detailtable.addCell(new Phrase("Delivery", tblHeader));
            detailtable.addCell(new Phrase("Fabric Date", tblHeader));
            detailtable.addCell(new Phrase("Destination", tblHeader));
            detailtable.addCell(new Phrase("Category", tblHeader));
            detailtable.addCell(new Phrase("Quota", tblHeader));
            detailtable.addCell(new Phrase("ITax", tblHeader));
            detailtable.addCell(new Phrase("Ship By", tblHeader));
            detailtable.addCell(new Phrase("Factory", tblHeader));
            detailtable.addCell(new Phrase("Method", tblHeader));

            datatable.setHeaderRows(1); // this is the end of the table header


            Category cat = null;
            String rmkStr = null;
            Boolean firstRmk = true;
            for (int i = 0; i < orders.size(); i++) {
                OrderSummary ords = (OrderSummary) orders.get(i);
                detailtable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font8));

                //Dynamic quantity column
                if ("PCS".equals(ords.getUnit())) {
                    cell = new PdfPCell(new Phrase(ords.getQtyPcsRounded() + " pcs", font8));
                } else {
                    cell = new PdfPCell(new Phrase(ords.getQtyDznRounded() + " dzn", font8));
                }
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingRight(10f);
                cell.cloneNonPositionParameters(detail_r);
                detailtable.addCell(cell);

                tmpdate = ords.getDelivery();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(ords.getDelivery()), font8));
                } else {
                    detailtable.addCell(new Phrase("UNKNOWN", font8));
                }
                tmpdate = ords.getFabricDeliveryDate();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(tmpdate), font8));
                } else {
                    detailtable.addCell(new Phrase(" ", font8));
                }

                detailtable.addCell(new Phrase(ld.getDestinationDesc(ords.getDestination(), 1), font8));
                cat = ld.getCategory(ords.getCatId(), 1);
                if (cat != null) {
                    detailtable.addCell(new Phrase(cat.getCategory(), font8));
                } else {
                    detailtable.addCell(new Phrase("Unkown", font8));
                }
                detailtable.addCell(new Phrase(ords.getQuota(), font8));
                detailtable.addCell(new Phrase(ords.getImportTax(), font8));
                detailtable.addCell(new Phrase(ords.getShip(), font8));
                detailtable.addCell(new Phrase(ld.getFactoryNameShort(ords.getMainFactory()), font8));
                detailtable.addCell(new Phrase(ords.getOrderMethod(), font8));

                //Add in to display remark if no empty or null
                rmkStr = ords.getRemark();
                if (rmkStr != null) {
                    if (rmkStr.length() > 0) {
                        if (firstRmk) {
                            descAlpha da = new descAlpha();
                            sortedRmk = new TreeMap(da);
                            firstRmk = false;
                            remarktable.addCell(new Phrase("SubRef#", tblHeader));
                            remarktable.addCell(new Phrase("Remark", tblHeader));
                        }
                        if (ords.getUpdUsrId() == null) {
                            sortedRmk.put(fmt4.format(ords.getInsTime()), i);
                        } else {
                            sortedRmk.put(fmt4.format(ords.getUpdTime()), i);
                        }
                    }
                }
            }//End for inner loop

            if (sortedRmk != null) {
                if (sortedRmk.size() > 0) {
                    Set st = sortedRmk.entrySet();
                    Iterator it = st.iterator();
                    Integer idx;
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
                        idx = (Integer) me.getValue();
                        OrderSummary ords = (OrderSummary) orders.get(idx);

                        remarktable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font10));
                        remarktable.addCell(new Phrase(ords.getRemark(), font8));
                    }
                }
            }

            document.add(headertable);
            document.add(new Paragraph(" "));
            document.add(toptable);
            document.add(new Paragraph(" "));
            document.add(datatable);
            document.add(new Paragraph(" "));
            document.add(detailtable);
            if (firstRmk == false) {
                document.add(new Paragraph(" "));
                document.add(remarktable);
            }
            document.add(new Paragraph(" "));
            document.close();
        } catch (DocumentException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        resp.setContentType("application/pdf");

        String filestr;
        if ("".equals(this.origin) || this.origin == null) {
            filestr = this.ordId + "-All";
        } else {
            filestr = this.ordId + "-" + ld.getCountryDesc(this.origin, 1);
        }

        resp.setHeader("Content-Disposition", "attachment; filename=OCF-" + filestr + ".pdf");
        resp.setContentLength(baos.size());

        try {
            out = resp.getOutputStream();
            baos.writeTo(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        this.ctx.responseComplete();
        return null;
    }
    public String OrderConfirmationFormByCountryFmt04(Orders ord) {
        HttpServletResponse resp = (HttpServletResponse)this.ectx.getResponse();
        ServletOutputStream out = null;
        PdfPCell cell = null;
        String filename = null;
        String filepath = null;
        TreeMap<String, Integer> sortedRmk = null;
        ListData ld = (ListData)this.getBean("listData");
        String tmpstr = null;
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        SimpleDateFormat fmt2 = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat fmt3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat fmt4 = new SimpleDateFormat("yyyMMddHHmmss");
        Date dNow = new Date();
        Font font18 = new Font(Font.FontFamily.HELVETICA, 18.0f);
        Font font14 = new Font(Font.FontFamily.HELVETICA, 14.0f);
        Font font16 = new Font(Font.FontFamily.HELVETICA, 16.0f);
        Font font12 = new Font(Font.FontFamily.HELVETICA, 12.0f);
        Font font10 = new Font(Font.FontFamily.HELVETICA, 10.0f);
        Font font8 = new Font(Font.FontFamily.HELVETICA, 8.0f);
        Font font7 = new Font(Font.FontFamily.HELVETICA, 7.0f);
        Font font6 = new Font(Font.FontFamily.HELVETICA, 6.0f);
        Font tblHeader = new Font(Font.FontFamily.HELVETICA, 8.0f);
        Font tblBody = new Font(Font.FontFamily.HELVETICA, 8.0f);
        Rectangle border_l = new Rectangle(0.0f, 0.0f);
        border_l.setBorderWidthLeft(1.0f);
        border_l.setBorderWidthBottom(0.0f);
        border_l.setBorderWidthRight(0.0f);
        border_l.setBorderWidthTop(1.0f);
        border_l.setBorderColor(BaseColor.GRAY);
        Rectangle border_lc = new Rectangle(0.0f, 0.0f);
        border_lc.setBorderWidthLeft(0.0f);
        border_lc.setBorderWidthBottom(0.0f);
        border_lc.setBorderWidthRight(1.0f);
        border_lc.setBorderWidthTop(1.0f);
        border_lc.setBorderColor(BaseColor.GRAY);
        Rectangle border_rc = new Rectangle(0.0f, 0.0f);
        border_rc.setBorderWidthLeft(0.0f);
        border_rc.setBorderWidthBottom(0.0f);
        border_rc.setBorderWidthRight(0.0f);
        border_rc.setBorderWidthTop(1.0f);
        border_rc.setBorderColor(BaseColor.GRAY);
        Rectangle border_r = new Rectangle(0.0f, 0.0f);
        border_r.setBorderWidthLeft(0.0f);
        border_r.setBorderWidthBottom(0.0f);
        border_r.setBorderWidthRight(1.0f);
        border_r.setBorderWidthTop(1.0f);
        border_r.setBorderColor(BaseColor.GRAY);
        Rectangle detail_r = new Rectangle(0.0f, 0.0f);
        detail_r.setBorderWidthLeft(0.0f);
        detail_r.setBorderWidthBottom(1.0f);
        detail_r.setBorderWidthRight(0.0f);
        detail_r.setBorderWidthTop(0.0f);
        Document document = new Document(PageSize.A4, 36.0f, 36.0f, 36.0f, 54.0f);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            filepath = this.getFoxyParam("FileUploadPath");
            filename = filepath + File.separatorChar + this.ordId + ".jpg";
            File f = new File(filename);
            if (!f.exists()) {
                filename = filepath + File.separatorChar + "default.jpg";
            }
            Image img = Image.getInstance((String)filename);
            img.setBorder(1);
            img.scalePercent(50.0f);
            PdfWriter writer = PdfWriter.getInstance((Document)document, (OutputStream)baos);
            writer.setPageEvent((PdfPageEvent)new OrderFormPageUtil());
            document.open();
            PdfPTable datatable = new PdfPTable(4);
            PdfPTable innertable = new PdfPTable(2);
            PdfPTable toptable = new PdfPTable(2);
            PdfPTable headertable = new PdfPTable(2);
            PdfPTable detailtable = new PdfPTable(11);
            PdfPTable remarktable = new PdfPTable(3);
            int[] colWidths = new int[]{15, 35, 15, 35};
            int[] headerColWidths = new int[]{60, 40};
            headertable.setWidths(headerColWidths);
            headertable.setWidthPercentage(100.0f);
            headertable.getDefaultCell().setBorder(0);
            headertable.getDefaultCell().setHorizontalAlignment(2);
            headertable.getDefaultCell().setVerticalAlignment(6);
            int[] topColWidths = new int[]{50, 50};
            toptable.setWidths(topColWidths);
            toptable.setWidthPercentage(100.0f);
            toptable.getDefaultCell().setBorder(0);
            toptable.getDefaultCell().setFixedHeight(100.0f);
            toptable.getDefaultCell().setHorizontalAlignment(0);
            toptable.getDefaultCell().setVerticalAlignment(1);
            int[] innerColWidths = new int[]{30, 70};
            innertable.setWidths(innerColWidths);
            innertable.setWidthPercentage(100.0f);
            innertable.getDefaultCell().setBorder(0);
            innertable.getDefaultCell().setHorizontalAlignment(0);
            innertable.getDefaultCell().setVerticalAlignment(1);
            datatable.setWidths(colWidths);
            datatable.setWidthPercentage(100.0f);
            datatable.getDefaultCell().setPadding(3.0f);
            datatable.getDefaultCell().setBorderWidth(1.0f);
            datatable.getDefaultCell().setBorderColor(BaseColor.GRAY);
            datatable.getDefaultCell().setHorizontalAlignment(0);
            datatable.getDefaultCell().setVerticalAlignment(1);
            int[] detailColWidths = new int[]{12, 20, 15, 15, 14, 12, 10, 10, 10, 14, 10};
            detailtable.setWidths(detailColWidths);
            detailtable.setWidthPercentage(100.0f);
            detailtable.getDefaultCell().setPadding(3.0f);
            detailtable.getDefaultCell().setBorder(0);
            detailtable.getDefaultCell().setBorderWidthBottom(1.0f);
            detailtable.getDefaultCell().setHorizontalAlignment(0);
            detailtable.getDefaultCell().setVerticalAlignment(1);
            int[] remarkColWidths = new int[]{12, 70, 18};
            remarktable.setWidths(remarkColWidths);
            remarktable.setWidthPercentage(100.0f);
            remarktable.getDefaultCell().setPadding(3.0f);
            remarktable.getDefaultCell().setBorder(0);
            remarktable.getDefaultCell().setBorderWidthBottom(1.0f);
            remarktable.getDefaultCell().setHorizontalAlignment(0);
            remarktable.getDefaultCell().setVerticalAlignment(1);
            headertable.addCell(new Phrase("Order Confirmation", font12));
            Object tmpDateStr = null;
            if (ord.getUpdUsrId() == null) {
                headertable.addCell(new Phrase("Created by " + ord.getInsUsrId() + " on " + fmt.format(ord.getInsTime()), font7));
            } else {
                headertable.addCell(new Phrase("Last Updated by " + ord.getUpdUsrId() + " on " + fmt.format(ord.getUpdTime()), font7));
            }
            String companyNameFull = ld.getCompanyNameFull(ord.getCnameCode(), 1);
            cell = new PdfPCell(new Phrase(companyNameFull, font12));
            cell.setColspan(1);
            cell.setBorder(0);
            cell.setVerticalAlignment(6);
            toptable.addCell(cell);
            cell = new PdfPCell(img, false);
            cell.setHorizontalAlignment(2);
            cell.setVerticalAlignment(6);
            cell.setColspan(1);
            cell.setBorder(0);
            toptable.addCell(cell);
            cell = new PdfPCell(new Phrase("Customer Code", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getCustCode()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Merchandiser", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getMerchandiser()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Brand/Division", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getCustBrand() + " / " + ord.getCustDivision(), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("NEW ORDER", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("/ AMENDMENT / STOCK / CANCEL", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Style No", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getStyleCode()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Unit Price", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": US$" + ord.getUnitPrice() + "/pc", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Our Ref #", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getOrderId()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Price Term", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getPriceTerm()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Ord Date", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            Date tmpdate = ord.getOrderDate();
            cell = tmpdate == null ? new PdfPCell(new Phrase(": ", font10)) : new PdfPCell(new Phrase(": " + fmt2.format(ord.getOrderDate()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Season", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getSeason()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Description", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getDescription()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Graphic Emb", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            String graphicType = ld.getGraphicTypeDesc(ord.getGraphicTypeCode());
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)graphicType), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Fabric Type", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getFabricType()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Colouring", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            String colourType = ld.getColourTypeDesc(ord.getColourTypeCode());
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)colourType), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Fabrication", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getFabrication()), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Fabric Mill", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getFabricMill()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("DailyCap/worker", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = ord.getDailyCap() != null ? new PdfPCell(new Phrase(": " + ord.getDailyCap() + " dz/cm", font10)) : new PdfPCell(new Phrase(": ", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Fabric YY/DZ", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getFabricYyDz()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Wash Type", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getWash()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Fabric Price", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getFabricPrice()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Wash Cost", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getSwash()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Factory CMT", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + (null != ord.getFtyCm() ? ord.getFtyCm() : ""), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Costing Basic Trim", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getDoubleStrValue(ord.getCostBasicTrim()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Costing Add Trim", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(":" + ord.getDoubleStrValue(ord.getCostAddTrim()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Actual CMT", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(":" + ord.getDoubleStrValue(ord.getActualCm()), font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Factory Trim", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getDoubleStrValue(ord.getFtyTrim()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Actual Output", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = ord.getActualOutput() != null ? new PdfPCell(new Phrase(": " + ord.getDoubleStrValue(ord.getActualOutput()) + "/cm", font10)) : new PdfPCell(new Phrase(":", font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(""));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("Actual Trim", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + ord.getDoubleStrValue(ord.getActualTrim()), font10));
            cell.cloneNonPositionParameters(border_lc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            border_l.setBorderWidthBottom(1.0f);
            border_lc.setBorderWidthBottom(1.0f);
            border_rc.setBorderWidthBottom(1.0f);
            border_r.setBorderWidthBottom(1.0f);
            cell = new PdfPCell(new Phrase("Remark (Factory)", font10));
            cell.cloneNonPositionParameters(border_l);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase(": " + StringUtils.stripToEmpty((String)ord.getRemark()), font10));
            cell.cloneNonPositionParameters(border_lc);
            cell.setColspan(3);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_rc);
            datatable.addCell(cell);
            cell = new PdfPCell(new Phrase("", font10));
            cell.cloneNonPositionParameters(border_r);
            datatable.addCell(cell);
            detailtable.addCell(new Phrase("SubRef #", tblHeader));
            cell = new PdfPCell(new Phrase("Quantity", tblHeader));
            cell.setHorizontalAlignment(2);
            cell.setPaddingRight(10.0f);
            cell.cloneNonPositionParameters(detail_r);
            detailtable.addCell(cell);
            detailtable.addCell(new Phrase("Delivery", tblHeader));
            detailtable.addCell(new Phrase("Fabric Date", tblHeader));
            detailtable.addCell(new Phrase("Destination", tblHeader));
            detailtable.addCell(new Phrase("Category", tblHeader));
            detailtable.addCell(new Phrase("Quota", tblHeader));
            detailtable.addCell(new Phrase("ITax", tblHeader));
            detailtable.addCell(new Phrase("Ship By", tblHeader));
            detailtable.addCell(new Phrase("Factory", tblHeader));
            detailtable.addCell(new Phrase("Method", tblHeader));
            datatable.setHeaderRows(1);
            tmpstr = "from OrderSummary where orderid = '" + this.ordId + "' AND status != 'D' ";
            if (!"".equals(this.origin) && this.origin != null) {
                tmpstr = tmpstr + "AND location = '" + this.origin + "' ";
            }
            tmpstr = tmpstr + "order by month, location";
            Session session = HibernateUtil.currentSession();
            List orders = session.createQuery(tmpstr).list();
            Category cat = null;
            String rmkStr = null;
            Boolean firstRmk = true;
            for (int i = 0; i < orders.size(); ++i) {
                OrderSummary ords = (OrderSummary)orders.get(i);
                detailtable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font8));
                cell = "PCS".equals(ords.getUnit()) ? new PdfPCell(new Phrase("" + ords.getQtyPcsRounded() + " pcs", font8)) : new PdfPCell(new Phrase("" + ords.getQtyDznRounded() + " dzn", font8));
                cell.setHorizontalAlignment(2);
                cell.setPaddingRight(10.0f);
                cell.cloneNonPositionParameters(detail_r);
                detailtable.addCell(cell);
                tmpdate = ords.getDelivery();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(ords.getDelivery()), font8));
                } else {
                    detailtable.addCell(new Phrase("UNKNOWN", font8));
                }
                tmpdate = ords.getFabricDeliveryDate();
                if (tmpdate != null) {
                    detailtable.addCell(new Phrase(fmt2.format(tmpdate), font8));
                } else {
                    detailtable.addCell(new Phrase(" ", font8));
                }
                detailtable.addCell(new Phrase(ld.getDestinationDesc(ords.getDestination(), 1), font8));
                cat = ld.getCategory(ords.getCatId(), 1);
                if (cat != null) {
                    detailtable.addCell(new Phrase(cat.getCategory(), font8));
                } else {
                    detailtable.addCell(new Phrase("Unkown", font8));
                }
                detailtable.addCell(new Phrase(ords.getQuota(), font8));
                detailtable.addCell(new Phrase(ords.getImportTax(), font8));
                detailtable.addCell(new Phrase(ords.getShip(), font8));
                detailtable.addCell(new Phrase(ld.getFactoryNameShort(ords.getMainFactory()), font8));
                detailtable.addCell(new Phrase(ords.getOrderMethod(), font8));
                rmkStr = ords.getRemark();
                if (rmkStr == null || rmkStr.length() <= 0) continue;
                if (firstRmk.booleanValue()) {
                    descAlpha da = new descAlpha();
                    sortedRmk = new TreeMap<String, Integer>((Comparator<String>)da);
                    firstRmk = false;
                    remarktable.addCell(new Phrase("SubRef#", tblHeader));
                    remarktable.addCell(new Phrase("Remark", tblHeader));
                    remarktable.addCell(new Phrase("Last Update/By", tblHeader));
                }
                if (ords.getUpdUsrId() == null) {
                    sortedRmk.put(fmt4.format(ords.getInsTime()), i);
                    continue;
                }
                sortedRmk.put(fmt4.format(ords.getUpdTime()), i);
            }
            if (sortedRmk != null && sortedRmk.size() > 0) {
                Set<Entry<String, Integer>> st = sortedRmk.entrySet();
                Iterator<Entry<String, Integer>> it = st.iterator();
                Integer idx = null;
                while (it.hasNext()) {
                    Map.Entry me = it.next();
                    idx = (Integer)me.getValue();
                    OrderSummary ords = (OrderSummary)orders.get(idx);
                    remarktable.addCell(new Phrase(ords.getMonth() + ords.getLocation(), font10));
                    remarktable.addCell(new Phrase(ords.getRemark(), font8));
                    if (ords.getUpdUsrId() == null) {
                        remarktable.addCell(new Phrase(fmt3.format(ords.getInsTime()) + "/" + ords.getInsUsrId(), font7));
                        continue;
                    }
                    remarktable.addCell(new Phrase(fmt3.format(ords.getUpdTime()) + "/" + ords.getUpdUsrId(), font7));
                }
            }
            document.add((Element)headertable);
            document.add((Element)new Paragraph(" "));
            document.add((Element)toptable);
            document.add((Element)new Paragraph(" "));
            document.add((Element)datatable);
            document.add((Element)new Paragraph(" "));
            document.add((Element)detailtable);
            if (!firstRmk.booleanValue()) {
                document.add((Element)new Paragraph(" "));
                document.add((Element)remarktable);
            }
            document.add((Element)new Paragraph(" "));
            document.close();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String filestr = "".equals(this.origin) || this.origin == null ? this.ordId + "-All" : this.ordId + "-" + ld.getCountryDesc(this.origin, 1);
        try {
            resp.setHeader("Content-Disposition", "attachment; filename=OCF-" + filestr + ".pdf");
            resp.setContentLength(baos.size());
            out = resp.getOutputStream();
            baos.writeTo((OutputStream)out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            HibernateUtil.closeSession();
        }
        this.ctx.responseComplete();
        return null;
    }
}
