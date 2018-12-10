package com.foxy.page;

import com.foxy.bean.FoxySessionData;
import com.foxy.data.FoxyOrderDetaiData;
import com.foxy.db.Category;
import com.foxy.db.CustBrand;
import com.foxy.db.CustDivision;
import com.foxy.db.Customer;
import com.foxy.db.HibernateUtil;
import com.foxy.db.HibernateUtilInternal;
import com.foxy.db.OrderConfirm;
import com.foxy.db.OrderNoReplaced;
import com.foxy.db.OrderNoReserved;
import com.foxy.db.OrderSummary;
import com.foxy.db.Orders;
import com.foxy.db.Parameter;
import com.foxy.db.User;
import com.foxy.util.ListData;
import com.foxy.util.OrderNumber;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;









public class FoxyOrderPage
  extends Page
  implements Serializable
{
  private static String MENU_CODE = "FOXY";
  private DataModel orderDetailModel;
  private List brandList = null;
  private List divisionList = null;
  private List ordNoReservedList = null;
  
  private String orderId = null;
  private String orderIdYear = null;
  private String mainFactoryCode = null;
  private String cnameCode = "CN1";
  private Date orderDate = Calendar.getInstance(Locale.US).getTime();
  private String customer = null;
  private String custBrandCode = null;
  private String custDivisionCode = null;
  private String style = null;
  private String season = null;
  private Double unitPrice = null;
  private Double qtyDzn = null;
  private Double qtyPcs = null;
  private Double dailyCap = null;
  private String colourTypeCode = null;
  private String graphicTypeCode = null;
  private String merchandiser = null;
  private String description = null;
  private String fabrication = null;
  private String fabricType = null;
  private Double horizontal = null;
  private Double vertical = null;
  private String remark = null;
  private String priceTerm = "FOB";
  private String fabricMill = null;
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
  private String recordStamp = null;
  private String duplicatefrom = "";
  private List searchTypes = new ArrayList();
  private String remarkMarketing = null;
  


  public FoxyOrderPage()
  {
    super("OrderForm");
    
    isAuthorize(MENU_CODE);
    



    if (foxySessionData == null) {
      foxySessionData = ((FoxySessionData)getBean("foxySessionData"));
    }
    



    if (foxySessionData.getAction() == null) {
      foxySessionData.setAction("ADD");
    }
    





    if ((foxySessionData.getAction().equals("ADD")) && (foxySessionData.getOrderId() != null))
    {

      if (customer == null) {
        getOrderUpd(foxySessionData.getOrderId());
        orderId = null;
        orderIdYear = null;
        recordStamp = null;
        duplicatefrom = (" via Dup[" + foxySessionData.getOrderId() + "]");
      }
    } else {
      duplicatefrom = "";
    }
    



    if (foxySessionData.getAction().equals("ENQUIRY")) {
      if (ectx.getRequestParameterMap().containsKey("recordid")) {
        foxySessionData.setPageParameter(ectx.getRequestParameterMap().get("recordid").toString());
      }
      
      if (foxySessionData.getOrderId() != null) {
        foxySessionData.setPageParameter(foxySessionData.getOrderId());
      }
      getOrderEnq(foxySessionData.getPageParameter());
    }
    



    if (foxySessionData.getAction().equals("UPDATE")) {
      if (foxySessionData.getPageParameter() != null) {
        getOrderUpd(foxySessionData.getPageParameter());

      }
      else
      {
        getOrderUpd(foxySessionData.getOrderId());
      }
    }
    if ((foxySessionData.getAction().equals("SEARCH")) || (foxySessionData.getAction().equals("LIST")))
    {
      searchTypes.add(new SelectItem("1", "Ref Number", "selected"));
      searchTypes.add(new SelectItem("2", "Style Code", ""));
      searchTypes.add(new SelectItem("3", "Season", ""));
    }
  }
  



  private String smartGetNewOrderId()
  {
    String companyName = "";
    if ("AUTOGENERATE".equals(this.orderId)) {
      OrderNumber ordNo = new OrderNumber();
      setOrderId(ordNo.getOrderNumber(Integer.valueOf(Integer.parseInt(getMainFactoryCode())), orderIdYear, true, cnameCode));
    } else if (!"".equals(this.orderId)) {
      try {
        Session session = HibernateUtil.currentSession();
        Transaction tx = session.beginTransaction();
        String qstr = "DELETE OrderNoReserved r WHERE r.reservedNo = :rOrdId";
        Query q = session.createQuery(qstr);
        q.setString("rOrdId", getOrderId());
        q.executeUpdate();
        

        if (!StringUtils.stripToEmpty(cnameCode).isEmpty()) {
          Query qry = session.createQuery("from Parameter as param where param.code = :companyName");
          qry.setString("companyName", cnameCode);
          
          List parameterList = qry.list();
          if (!parameterList.isEmpty()) {
            Parameter param = (Parameter)parameterList.get(0);
            companyName = param.getDescription();
          }
        }
        
        tx.commit();
        session.clear();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        HibernateUtilInternal.closeSession();
      }
      
      String yearAndFactory = this.orderId.substring(0, 3);
      Integer orderId = Integer.valueOf(Integer.parseInt(this.orderId.substring(3)));
      if (yearAndFactory.substring(0, 2).equals("13")) {
        this.orderId = (yearAndFactory + (!companyName.isEmpty() ? companyName.substring(0, 1) + String.format("%03d", new Object[] { orderId }) : String.format("%03d", new Object[] { orderId })));
      } else {
        this.orderId = (yearAndFactory + (!companyName.isEmpty() ? companyName.substring(0, 1) + String.format("%04d", new Object[] { orderId }) : String.format("%04d", new Object[] { orderId })));
      }
    }
    return getOrderId();
  }
  
  public String getDuplicatefrom() {
    return duplicatefrom;
  }
  
  private void getOrderEnq(String orderid) {
    try {
      Session session = HibernateUtil.currentSession();
      Transaction tx = session.beginTransaction();
      List result = session.createQuery("from Orders where orderid = '" + orderid + "'").list();
      
      tx.commit();
      session.clear();
      ListData ld = (ListData)getBean("listData");
      for (int i = 0; i < result.size(); i++) {
        Orders od = (Orders)result.get(i);
        orderId = od.getOrderId();
        orderDate = od.getOrderDate();
        customer = ld.getCustomer(od.getCustCode()).getCustName();
        custBrandCode = od.getCustBrand();
        custDivisionCode = od.getCustDivision();
        style = od.getStyleCode();
        season = ld.getSeasonDesc(od.getSeason());
        unitPrice = od.getUnitPrice();
        merchandiser = ld.getMerchandiser(od.getMerchandiser()).getFullName();
        description = od.getDescription();
        fabrication = od.getFabrication();
        remark = od.getRemark();
        remarkMarketing = od.getRemarkMarketing();
        horizontal = od.getHorizontal();
        vertical = od.getVertical();
        dailyCap = od.getDailyCap();
        colourTypeCode = od.getColourTypeCode();
        graphicTypeCode = od.getGraphicTypeCode();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void getOrderUpd(String orderid) {
    try {
      SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Session session = HibernateUtil.currentSession();
      Transaction tx = session.beginTransaction();
      List result = session.createQuery("from Orders where orderid = '" + orderid + "'").list();
      
      tx.commit();
      session.clear();
      for (int i = 0; i < result.size(); i++) {
        Orders od = (Orders)result.get(i);
        orderId = od.getOrderId();
        orderDate = od.getOrderDate();
        cnameCode = od.getCnameCode();
        customer = od.getCustCode();
        custBrandCode = od.getCustBrand();
        custDivisionCode = od.getCustDivision();
        style = od.getStyleCode();
        season = od.getSeason();
        unitPrice = od.getUnitPrice();
        merchandiser = od.getMerchandiser();
        description = od.getDescription();
        dailyCap = od.getDailyCap();
        colourTypeCode = od.getColourTypeCode();
        graphicTypeCode = od.getGraphicTypeCode();
        fabrication = od.getFabrication();
        fabricType = od.getFabricType();
        remark = od.getRemark();
        remarkMarketing = od.getRemarkMarketing();
        horizontal = od.getHorizontal();
        vertical = od.getVertical();
        priceTerm = od.getPriceTerm();
        fabricMill = od.getFabricMill();
        fabricPrice = od.getFabricPrice();
        fabricYyDz = od.getFabricYyDz();
        costCm = od.getCostCm();
        costBasicTrim = od.getCostBasicTrim();
        costAddTrim = od.getCostAddTrim();
        ftyCm = od.getFtyCm();
        ftyRemark = od.getFtyRemark();
        ftyTrim = od.getFtyTrim();
        actualCm = od.getActualCm();
        actualOutput = od.getActualOutput();
        actualTrim = od.getActualTrim();
        wash = od.getWash();
        swash = od.getSwash();
        gcost = od.getGcost();
        quotaUom = od.getQuotaUom();
        uom = od.getUom();
        if (od.getInsUsrId() != null) {
          recordStamp = ("Created [" + od.getInsUsrId() + " - " + sdf1.format(od.getInsTime()) + "]");
        } else {
          recordStamp = "Created [ No Record ]";
        }
        
        if (od.getUpdUsrId() != null) {
          recordStamp = (recordStamp + "     Last Update [" + od.getUpdUsrId() + " - " + sdf1.format(od.getUpdTime()) + "]");
        } else {
          recordStamp += "     Last Update [No Update On Record yet]";
        }
        setSessionObject1(od);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public String save() {
    Orders order = new Orders();
    
    if (ectx.getRequestParameterMap().containsKey("UpdAddForm:OrderIdD")) {
      orderId = ectx.getRequestParameterMap().get("UpdAddForm:OrderIdD").toString();
      order.setOrderId(orderId);
    } else {
      order.setOrderId(smartGetNewOrderId());
      
      setSessionObject1((Object)null);
    }
    try
    {
      Session session = HibernateUtil.currentSession();
      Transaction tx = session.beginTransaction();
      order.setCnameCode(getCnameCode());
      order.setCustCode(getCustomer());
      order.setCustBrand(getCustBrandCode());
      order.setCustDivision(getCustDivisionCode());
      order.setStyleCode(getStyle());
      order.setSeason(getSeason());
      order.setMerchandiser(getMerchandiser());
      order.setOrderDate(getDate());
      order.setUnitPrice(getUnitPrice());
      order.setQtyDzn(getQtyDzn());
      order.setQtyPcs(getQtyPcs());
      order.setDescription(getDescription());
      order.setFabrication(getFabrication());
      order.setFabricType(getFabricType());
      order.setDailyCap(getDailyCap());
      order.setColourTypeCode(getColourTypeCode());
      order.setGraphicTypeCode(getGraphicTypeCode());
      order.setRemark(getRemark());
      order.setRemarkMarketing(getRemarkMarketing());
      order.setHorizontal(getHorizontal());
      order.setVertical(getVertical());
      order.setPriceTerm(getPriceTerm());
      order.setFabricMill(getFabricMill());
      order.setFabricPrice(getFabricPrice());
      order.setFabricYyDz(getFabricYyDz());
      order.setCostCm(getCostCm());
      order.setCostBasicTrim(getCostBasicTrim());
      order.setCostAddTrim(getCostAddTrim());
      order.setFtyCm(getFtyCm());
      order.setFtyRemark(getFtyRemark());
      order.setFtyTrim(getFtyTrim());
      order.setActualCm(getActualCm());
      order.setActualOutput(getActualOutput());
      order.setActualTrim(getActualTrim());
      order.setWash(getWash());
      order.setSwash(getSwash());
      order.setGcost(getGcost());
      order.setQuotaUom(getQuotaUom());
      order.setUom(getUom());
      order.setStatus("A");
      
      Orders tmpobj = (Orders)getSessionObject1(Orders.class);
      if (tmpobj != null) {
        order.setInsTime(tmpobj.getInsTime());
        order.setInsUsrId(tmpobj.getInsUsrId());
        order.setImgFile(tmpobj.getImgFile());
      }
      session.saveOrUpdate(order);
      
      tx.commit();
      session.clear();
      HibernateUtil.closeSession();
    } catch (Exception e) {
      e.printStackTrace();
      return "success";
    }
    getOrderEnq(orderId);
    foxySessionData.setPageParameter(orderId);
    foxySessionData.setAction("ADD");
    foxySessionData.setOrderId(orderId);
    return "ADD_DETAIL";
  }
  





  public String execOrderIdTransfer()
  {
    String oldOrderId = foxySessionData.getOrderId();
    String newOrderId = smartGetNewOrderId();
    FoxyFileUploadPage filemove = new FoxyFileUploadPage();
    

    foxySessionData.setOrderId(newOrderId);
    



    try
    {
      String userid = null;
      ExternalContext tmpectx = FacesContext.getCurrentInstance().getExternalContext();
      if (tmpectx != null) {
        userid = tmpectx.getRemoteUser();
      }
      
      Session session = HibernateUtil.currentSession();
      
      session.clear();
      Transaction tx = session.beginTransaction();
      

      String qstr = "UPDATE OrderSummary SET orderId = :pnewOrderId WHERE orderId = :poldOrderId";
      
      Query q = session.createQuery(qstr);
      q.setString("pnewOrderId", newOrderId);
      q.setString("poldOrderId", oldOrderId);
      
      q.executeUpdate();
      tx.commit();
      session.clear();
      
      tx = session.beginTransaction();
      qstr = "UPDATE OrderConfirm SET orderId = :pnewOrderId WHERE orderId = :poldOrderId";
      q = session.createQuery(qstr);
      q.setString("pnewOrderId", newOrderId);
      q.setString("poldOrderId", oldOrderId);
      
      q.executeUpdate();
      tx.commit();
      session.clear();
      

      tx = session.beginTransaction();
      qstr = "UPDATE Orders SET orderId = :pnewOrderId, updUsrId = :pupdUsrId, updTime = now() WHERE orderId = :poldOrderId";
      q = session.createQuery(qstr);
      q.setString("pnewOrderId", newOrderId);
      q.setString("poldOrderId", oldOrderId);
      q.setString("pupdUsrId", userid);
      
      q.executeUpdate();
      tx.commit();
      session.clear();
      

      OrderNoReplaced ordIdReplaced = new OrderNoReplaced();
      ordIdReplaced.setNewOrderId(newOrderId);
      ordIdReplaced.setOldOrderId(oldOrderId);
      ordIdReplaced.setStatus("A");
      tx = session.beginTransaction();
      session.saveOrUpdate(ordIdReplaced);
      tx.commit();
      filemove.transferOrderId(oldOrderId, newOrderId);
    }
    catch (HibernateException e)
    {
      e.printStackTrace();
      FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
      ctx.addMessage(null, fmsg);
    }
    catch (Exception e) {
      FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
      ctx.addMessage(null, fmsg);
    } finally {
      HibernateUtil.closeSession();
    }
    return "success";
  }
  
  public DataModel getOrderDetail() {
    Double totalDzn = new Double(0.0D);
    Double totalPcs = new Double(0.0D);
    ListData ld = (ListData)getBean("listData");
    
    if (orderDetailModel == null) {
      try {
        orderDetailModel = new ListDataModel();
        Session session = HibernateUtil.currentSession();
        List ordSum = session.createQuery("from OrderSummary where orderid = '" + orderId + "' and status != 'D' order by month, location").list();
        
        if (ordSum.size() <= 0) {
          FoxyOrderDetaiData odDs = new FoxyOrderDetaiData();
          tableList.add(odDs);
        }
        for (int i = 0; i < ordSum.size(); i++) {
          FoxyOrderDetaiData odDs = new FoxyOrderDetaiData();
          OrderSummary os = (OrderSummary)ordSum.get(i);
          
          List result = session.createQuery("from Category where catId = " + os.getCatId()).list();
          if (result.size() > 0) {
            Category cat = (Category)result.get(0);
            odDs.setCategory(cat.getCategory());
          }
          
          odDs.setMonth(os.getMonth());
          odDs.setDestName(ld.getDestinationDesc(os.getDestination(), 1));
          odDs.setDestShortName(ld.getDestinationShortDesc(os.getDestination(), 1));
          odDs.setLocation(os.getLocation());
          odDs.setOrderMethod(os.getOrderMethod());
          odDs.setDelivery(os.getDelivery());
          odDs.setFabricDate(os.getFabricDeliveryDate());
          
          odDs.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
          odDs.setRemark(os.getRemark());
          odDs.setQuantityDzn(os.getQtyDzn());
          odDs.setQuantityPcs(os.getQtyPcs());
          odDs.setUnit(os.getUnit());
          tableList.add(odDs);
          
          List ordCon = session.createQuery("from OrderConfirm where orderid = '" + orderId + "' and srefid = " + os.getId() + " and status != 'D' order by month, location, sublocation").list();
          




          totalDzn = Double.valueOf(0.0D);
          totalPcs = Double.valueOf(0.0D);
          
          if (ordCon.size() <= 0) {
            FoxyOrderDetaiData tot = new FoxyOrderDetaiData();
            tot.setMonth(os.getMonth());
            tot.setLocation(os.getLocation());
            tot.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
            tot.setCategory(odDs.getCategory());
            tot.setOrderMethod(os.getOrderMethod());
            tot.setDelivery(os.getDelivery());
            tot.setDestName(odDs.getDestName());
            tot.setDestShortName(odDs.getDestShortName());
            
            tableList.add(tot);
          }
          
          for (int j = 0; j < ordCon.size(); j++) {
            FoxyOrderDetaiData odDc = new FoxyOrderDetaiData();
            OrderConfirm oc = (OrderConfirm)ordCon.get(j);
            







            odDc.setMonth(oc.getMonth());
            odDc.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
            odDc.setCategory(odDs.getCategory());
            odDc.setOrderMethod(os.getOrderMethod());
            odDc.setDelivery(os.getDelivery());
            odDc.setDestName(odDs.getDestName());
            odDc.setDestShortName(odDs.getDestShortName());
            
            odDc.setPoNumber(oc.getPoNumber());
            odDc.setPoDate(oc.getPoDate());
            odDc.setLocation(oc.getLocation());
            odDc.setSubLocation(oc.getSubLocation());
            odDc.setQuantityDzn(oc.getQtyDzn());
            odDc.setQuantityPcs(oc.getQtyPcs());
            odDc.setUnit(os.getUnit());
            odDc.setVesselDate(oc.getVesselDate());
            odDc.setFabricDate(oc.getFabricDate());
            tableList.add(odDc);
            totalDzn = Double.valueOf(totalDzn.doubleValue() + oc.getQtyDzn().doubleValue());
            totalPcs = Double.valueOf(totalPcs.doubleValue() + oc.getQtyPcs().doubleValue());
            if (j == ordCon.size() - 1) {
              FoxyOrderDetaiData tot = new FoxyOrderDetaiData();
              tot.setMonth(oc.getMonth());
              tot.setLocation(os.getLocation());
              tot.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
              tot.setCategory(odDs.getCategory());
              tot.setOrderMethod(os.getOrderMethod());
              tot.setDelivery(os.getDelivery());
              tot.setDestName(odDs.getDestName());
              tot.setDestShortName(odDs.getDestShortName());
              
              tot.setQuantityDzn(totalDzn);
              tot.setQuantityPcs(totalPcs);
              tot.setUnit(os.getUnit());
              tableList.add(tot);
            }
          }
        }
        orderDetailModel.setWrappedData(tableList);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        HibernateUtil.closeSession();
      }
    }
    return orderDetailModel;
  }
  
  public List getBrandItemsList() {
    if (brandList == null) {
      List resultList = null;
      brandList = new ArrayList();
      
      if (customer != null) {
        try {
          Session session = HibernateUtil.currentSession();
          Transaction tx = session.beginTransaction();
          Criteria crit = session.createCriteria(CustBrand.class);
          crit.add(Expression.eq("custCode", customer));
          resultList = crit.list();
          
          tx.commit();
        } catch (HibernateException e) {
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
        
        if (resultList.size() > 0) {
          brandList.add(new SelectItem(new String(""), new String("Pls select one")));
        } else {
          brandList.add(new SelectItem(new String(""), new String("Empty")));
        }
        for (int i = 0; i < resultList.size(); i++) {
          CustBrand cbrand = (CustBrand)resultList.get(i);
          brandList.add(new SelectItem(cbrand.getBrandCode(), cbrand.getBrandCode() + " - " + cbrand.getBrandDesc()));
        }
      } else {
        brandList.add(new SelectItem(new String(""), new String("Empty")));
      }
    }
    
    return brandList;
  }
  
  public List getDivisionItemsList() {
    if (divisionList == null) {
      divisionList = new ArrayList();
      

      if ((customer != null) && (custBrandCode != null)) {
        List resultList = null;
        try {
          Session session = HibernateUtil.currentSession();
          Transaction tx = session.beginTransaction();
          Criteria crit = session.createCriteria(CustDivision.class);
          crit.add(Expression.eq("custCode", customer));
          crit.add(Expression.eq("brandCode", custBrandCode));
          resultList = crit.list();
          
          tx.commit();
        }
        catch (HibernateException e) {
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
        
        if (resultList.size() > 0) {
          divisionList.add(new SelectItem(new String(""), new String("Pls select one")));
        } else {
          divisionList.add(new SelectItem(new String(""), new String("Empty")));
        }
        
        for (int i = 0; i < resultList.size(); i++) {
          CustDivision cdivision = (CustDivision)resultList.get(i);
          divisionList.add(new SelectItem(cdivision.getDivCode(), cdivision.getDivCode() + " - " + cdivision.getDivDesc()));
        }
      } else {
        divisionList.add(new SelectItem(new String(""), new String("Empty")));
      }
    }
    return divisionList;
  }
  
  public List getOrdNoReservedList() {
    if (ordNoReservedList == null) {
      ordNoReservedList = new ArrayList();
      List resultList = null;
      String userid = null;
      
      userid = getUserId();
      

      if ((orderIdYear != null) && (mainFactoryCode != null) && (userid != null)) {
        try {
          Session session = HibernateUtil.currentSession();
          Transaction tx = session.beginTransaction();
          Criteria crit = session.createCriteria(OrderNoReserved.class);
          crit.add(Expression.eq("year", Integer.valueOf(Integer.parseInt(orderIdYear))));
          crit.add(Expression.eq("mainFactory", Integer.valueOf(Integer.parseInt(mainFactoryCode))));
          crit.add(Expression.eq("forUserId", userid));
          crit.add(Expression.ge("expiredOn", Calendar.getInstance(Locale.US).getTime()));
          crit.addOrder(Order.asc("reservedNo"));
          resultList = crit.list();
          
          tx.commit();
        } catch (HibernateException e) {
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
        
        if (resultList.size() > 0) {
          ordNoReservedList.add(new SelectItem("", "Pls select one"));
        }
        
        ordNoReservedList.add(new SelectItem("AUTOGENERATE", "Auto Generate / Using Expired RefNum"));
        
        for (int i = 0; i < resultList.size(); i++) {
          OrderNoReserved ordNoRsv = (OrderNoReserved)resultList.get(i);
          ordNoReservedList.add(new SelectItem(ordNoRsv.getReservedNo(), ordNoRsv.getReservedNo() + " ExpOn[" + ordNoRsv.getExpiredOnFmted() + "]"));
        }
      } else {
        ordNoReservedList.add(new SelectItem("", "Please Select Year & Factory"));
      }
    }
    
    return ordNoReservedList;
  }
  
  public String onYearOrFactoryChange() {
    ordNoReservedList = null;
    return null;
  }
  

  public String onCustCodeChange()
  {
    brandList = null;
    divisionList = null;
    return null;
  }
  

  public String onCustBrandChange()
  {
    divisionList = null;
    return null;
  }
  



  public String enquire()
  {
    foxySessionData.setAction("ENQUIRY");
    if (foxySessionData.getPageParameter() != null) {
      foxySessionData.setOrderId(foxySessionData.getPageParameter());
      getOrderEnq(foxySessionData.getOrderId());
    }
    return "OrderEnqSuccess";
  }
  


  public String update()
  {
    foxySessionData.setAction("ENQUIRY");
    return "success";
  }
  


  public String edit()
  {
    foxySessionData.setAction("UPDATE");
    getOrderUpd(getOrderIdD());
    return "EditOrder";
  }
  


  public String delete()
  {
    foxySessionData.setAction("ENQUIRY");
    try
    {
      Session session = HibernateUtil.currentSession();
      Transaction tx = session.beginTransaction();
      
      List child = session.createQuery("from OrderSummary where orderid = '" + getOrderId() + "'").list();
      
      if (child.size() > 0) {
        FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "@ Error: Child exist for Ref. Number [" + getOrderId() + "], delete not allowed.", "");
        ctx.addMessage(null, fmsg);
        return "success";
      }
      
      List result = session.createQuery("from Orders where orderid = '" + getOrderId() + "'").list();
      
      if (result.size() == 1) {
        Orders od = (Orders)result.get(0);
        od.setStatus("D");
        session.saveOrUpdate(od);
      }
      



      int resultos = session.createQuery("delete from OrderSummary where orderid = " + getOrderId()).executeUpdate();
      










      List resultOC = session.createQuery("from OrderConfirm where orderid = '" + getOrderId() + "'").list();
      int resultSP;
      if (resultOC.size() > 0) { int resultoc;
        for (int j = 0; j < resultOC.size(); j++) {
          OrderConfirm oc = (OrderConfirm)resultOC.get(j);
          




          resultoc = session.createQuery("delete from Shipping where crefid = " + oc.getId()).executeUpdate();
        }
        










        resultSP = session.createQuery("delete from OrderConfirm where orderid = '" + getOrderId() + "'").executeUpdate();
      }
      

      tx.commit();
      session.clear();
      HibernateUtil.closeSession();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "success";
  }
  
  public String orderInstruction()
  {
    foxySessionData.setAction("ADD");
    foxySessionData.setPageParameter(String.valueOf(getOrderId()));
    return "ADD_DETAIL";
  }
  
  public String poEntry()
  {
    foxySessionData.setAction("ENQUIRY");
    foxySessionData.setPageParameter(String.valueOf(getOrderId()));
    return "ADD_PO";
  }
  
  public String shipEntry()
  {
    foxySessionData.setAction(new String("ENQUIRY"));
    foxySessionData.setPageParameter(String.valueOf(getOrderId()));
    return "ADD_SHIP";
  }
  



  public String shortCut()
  {
    return "go_enqOrder";
  }
  



  public String getOrderId()
  {
    return orderId;
  }
  
  public String getOrderIdD() {
    return orderId;
  }
  
  public void setOrderId(String newValue) {
    orderId = newValue;
  }
  
  public void setOrderIdD(String newValue) {
    orderId = newValue;
  }
  
  public String getOrderIdYear()
  {
    return orderIdYear;
  }
  
  public void setOrderIdYear(String newValue) {
    orderIdYear = newValue;
  }
  
  public String getMainFactoryCode()
  {
    return mainFactoryCode;
  }
  
  public void setMainFactoryCode(String newValue) {
    mainFactoryCode = newValue;
  }
  
  public Date getDate()
  {
    return orderDate;
  }
  
  public void setDate(Date newValue) {
    orderDate = newValue;
  }
  
  public String getCnameCode()
  {
    return cnameCode;
  }
  
  public void setCnameCode(String cnameCode) {
    this.cnameCode = cnameCode;
  }
  
  public String getCustomer()
  {
    return customer;
  }
  
  public void setCustomer(String newValue) {
    customer = newValue;
  }
  
  public String getCustBrandCode()
  {
    return custBrandCode;
  }
  
  public void setCustBrandCode(String newValue) {
    custBrandCode = newValue;
  }
  
  public String getCustDivisionCode()
  {
    return custDivisionCode;
  }
  
  public void setCustDivisionCode(String newValue) {
    custDivisionCode = newValue;
  }
  
  public String getStyle()
  {
    return style;
  }
  
  public void setStyle(String newValue) {
    style = newValue;
  }
  
  public String getSeason()
  {
    return season;
  }
  
  public void setSeason(String newValue) {
    season = newValue;
  }
  
  public List getSearchTypes()
  {
    return searchTypes;
  }
  
  public void setSearchTypes(List newValue) {
    searchTypes = newValue;
  }
  
  public Double getUnitPrice()
  {
    return unitPrice;
  }
  
  public void setUnitPrice(Double newValue) {
    unitPrice = newValue;
  }
  
  public Double getQtyDzn()
  {
    return qtyDzn;
  }
  
  public void setQtyDzn(Double newValue) {
    qtyDzn = newValue;
  }
  
  public Double getQtyPcs()
  {
    return qtyPcs;
  }
  
  public void setQtyPcs(Double newValue) {
    qtyPcs = newValue;
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
  
  public String getMerchandiser()
  {
    return merchandiser;
  }
  
  public void setMerchandiser(String newValue) {
    merchandiser = newValue;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String newValue) {
    description = newValue;
  }
  
  public Double getHorizontal()
  {
    return horizontal;
  }
  
  public void setHorizontal(Double newValue) {
    horizontal = newValue;
  }
  
  public Double getVertical()
  {
    return vertical;
  }
  
  public void setVertical(Double newValue) {
    vertical = newValue;
  }
  
  public String getFabrication()
  {
    return fabrication;
  }
  
  public void setFabrication(String newValue) {
    fabrication = newValue;
  }
  
  public String getFabricType()
  {
    return fabricType;
  }
  
  public void setFabricType(String fabricType) {
    this.fabricType = fabricType;
  }
  
  public String getPriceTerm()
  {
    return priceTerm;
  }
  
  public void setPriceTerm(String newValue) {
    priceTerm = newValue;
  }
  
  public String getFabricMill()
  {
    return fabricMill;
  }
  
  public void setFabricMill(String newValue) {
    fabricMill = newValue;
  }
  
  public String getFabricYyDz()
  {
    return fabricYyDz;
  }
  
  public void setFabricYyDz(String newValue) {
    fabricYyDz = newValue;
  }
  
  public String getFabricPrice()
  {
    return fabricPrice;
  }
  
  public void setFabricPrice(String newValue) {
    fabricPrice = newValue;
  }
  
  public Double getCostCm()
  {
    return costCm;
  }
  
  public void setCostCm(Double newValue) {
    costCm = newValue;
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
  
  public Double getFtyCm()
  {
    return ftyCm;
  }
  
  public void setFtyCm(Double newValue) {
    ftyCm = newValue;
  }
  
  public String getFtyRemark()
  {
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
  
  public String getWash()
  {
    return wash;
  }
  
  public void setWash(String newValue) {
    wash = newValue;
  }
  
  public String getSwash()
  {
    return swash;
  }
  
  public void setSwash(String newValue) {
    swash = newValue;
  }
  
  public String getGcost()
  {
    return gcost;
  }
  
  public void setGcost(String newValue) {
    gcost = newValue;
  }
  
  public String getQuotaUom()
  {
    return quotaUom;
  }
  
  public void setQuotaUom(String newValue) {
    quotaUom = newValue;
  }
  
  public String getUom()
  {
    return uom;
  }
  
  public void setUom(String newValue) {
    uom = newValue;
  }
  
  public String getRecordStamp() {
    return recordStamp;
  }
  
  public String getRemark()
  {
    return remark;
  }
  
  public void setRemark(String newValue) {
    remark = newValue;
  }
  
  public String getRemarkMarketing() {
    return remarkMarketing;
  }
  
  public void setRemarkMarketing(String remarkMarketing) {
    this.remarkMarketing = remarkMarketing;
  }
}
