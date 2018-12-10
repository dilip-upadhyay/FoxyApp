package com.foxy.page;

import com.foxy.db.HibernateUtil;
import com.foxy.db.OrderNoReserved;
import com.foxy.util.OrderNumber;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.Transaction;











public class FoxyOrderNoReservePage
  extends Page
  implements Serializable
{
  private static String MENU_CODE = "FOXY";
  private OrderNoReserved ordNoReservedBean = null;
  private String orderIdYear = null;
  private String mainFactoryCode = null;
  private Integer orderNoCount = null;
  private Integer daysToExpired = Integer.valueOf(90);
  



  public FoxyOrderNoReservePage()
  {
    super(MENU_CODE);
    isAuthorize(MENU_CODE);
  }
  
  public OrderNoReserved getOrdNoReservedBean() {
    return ordNoReservedBean;
  }
  
  public void setOrdNoReservedBean(OrderNoReserved ordNoReservedBean) {
    this.ordNoReservedBean = ordNoReservedBean;
  }
  
  public String getOrderIdYear() {
    return orderIdYear;
  }
  
  public void setOrderIdYear(String orderIdYear) {
    this.orderIdYear = orderIdYear;
  }
  
  public String getMainFactoryCode() {
    return mainFactoryCode;
  }
  
  public void setMainFactoryCode(String mainFactoryCode) {
    this.mainFactoryCode = mainFactoryCode;
  }
  
  public Integer getOrderNoCount() {
    return orderNoCount;
  }
  
  public void setOrderNoCount(Integer orderNoCount) {
    this.orderNoCount = orderNoCount;
  }
  
  public Integer getDaysToExpired() {
    return daysToExpired;
  }
  
  public void setDaysToExpired(Integer daysToExpired) {
    this.daysToExpired = daysToExpired;
  }
  





  public String saveAdd()
  {
    String userid = null;
    
    String result = "fail";
    try
    {
      ExternalContext ectxtmp = FacesContext.getCurrentInstance().getExternalContext();
      if (ectxtmp != null) {
        userid = ectxtmp.getRemoteUser();
      }
      
      if (userid != null)
      {
        OrderNumber ordNo = new OrderNumber();
        
        Session session = HibernateUtil.currentSession();
        Transaction tx = session.beginTransaction();
        

        for (int i = 0; i < orderNoCount.intValue(); i++) {
          OrderNoReserved ordnumreserv = new OrderNoReserved();
          
          ordnumreserv.setReservedNo(ordNo.getOrderNumber(Integer.valueOf(Integer.parseInt(mainFactoryCode)), orderIdYear, false, ""));
          ordnumreserv.setForUserId(userid);
          ordnumreserv.setMainFactory(Integer.valueOf(Integer.parseInt(mainFactoryCode)));
          ordnumreserv.setYear(Integer.valueOf(Integer.parseInt(orderIdYear)));
          ordnumreserv.setReservedOn(Calendar.getInstance(Locale.US).getTime());
          ordnumreserv.setExpiredAfterDays(daysToExpired);
          ordnumreserv.setStatus("A");
          session.saveOrUpdate(ordnumreserv);
        }
        
        tx.commit();
        session.clear();
        result = "success";
      }
    } catch (Exception e) { Transaction tx;
      e.printStackTrace();
      FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
      ctx.addMessage(null, fmsg);
      return null;
    } finally {
      HibernateUtil.closeSession();
    }
    return result;
  }
}
