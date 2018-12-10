package com.foxy.page;

import com.foxy.bean.FoxySessionData;
import com.foxy.data.FoxyOrderList;
import com.foxy.db.Customer;
import com.foxy.db.HibernateUtil;
import com.foxy.db.Orders;
import com.foxy.db.User;
import com.foxy.util.FoxyPagedDataModel;
import com.foxy.util.ListData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;






public class FoxyOrderSearchPage
  extends Page
  implements Serializable
{
  private static String MENU_CODE = "FOXY";
  private DataModel orderDetailModel;
  private DataModel orderListModel = null;
  private List searchTypes = new ArrayList();
  private ListData ld = null;
  private Integer count = Integer.valueOf(0);
  


  public FoxyOrderSearchPage()
  {
    super("OrderSearchForm");
    
    isAuthorize(MENU_CODE);
    



    if (foxySessionData == null) {
      foxySessionData = ((FoxySessionData)getBean("foxySessionData"));
    }
    
    searchTypes.add(new SelectItem("1", "Ref Number", "selected"));
    searchTypes.add(new SelectItem("2", "Style Code", ""));
    searchTypes.add(new SelectItem("3", "Season", ""));
  }
  
  public DataModel getOrderList()
  {
    Number numofRec = null;
    


    int firstrow = foxyTable.getFirst();
    int pagesize = foxyTable.getRows();
    


    if (foxySessionData != null) {
      if (foxySessionData.getTableRows() == 0) {
        foxySessionData.setTableFirstRow(firstrow);
        foxySessionData.setTableRows(pagesize);
        orderListModel = null;
      } else if (foxySessionData.isBackToList())
      {













        orderListModel = null;
        

        foxySessionData.setBackToList(false);
      }
    }
    
    if (ld == null) {
      ld = ((ListData)getBean("listData"));
    }
    

    if ((searchKey != null) && (orderListModel == null))
    {
      try {
        List result = null;
        Session session = HibernateUtil.currentSession();
        Transaction tx = session.beginTransaction();
        
        if ((searchType != null) && (searchType.equals("3"))) {
          result = session.createQuery("select count(*) as num from Orders as orders where orders.season in (select code from Parameter as param where param.category='SEA' and param.description like '%" + searchKey.replace('*', '%') + "%') and status != 'D'").list();

        }
        else if ((searchType != null) && (searchType.equals("2"))) {
          result = session.createQuery("select count(*) as num from Orders as orders where orders.styleCode like '" + searchKey.replace('*', '%') + "' and status != 'D'").list();
        }
        else {
          result = session.createQuery("select count(*) as num from Orders as orders where orders.orderId like '" + searchKey.replace('*', '%') + "' and status != 'D'").list();
        }
        

        numofRec = Integer.valueOf(Integer.parseInt(result.get(0).toString()));
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
      
      numofRec = Integer.valueOf(numofRec == null ? 0 : numofRec.intValue());
    }
    
    try
    {
      List result = null;
      if ((searchKey != null) && (orderListModel == null)) {
        Session session = HibernateUtil.currentSession();
        Transaction tx = session.beginTransaction();
        
        if ((searchType != null) && (searchType.equals("3"))) {
          result = session.createQuery("from Orders as orders where orders.season in (select code from Parameter as param where param.category='SEA' and param.description like '%" + searchKey.replace('*', '%') + "%') and status != 'D' " + "order by orderdate desc ").setFirstResult(firstrow).setMaxResults(pagesize).list();



        }
        else if ((searchType != null) && (searchType.equals("2"))) {
          result = session.createQuery("from Orders as orders where orders.styleCode like '" + searchKey.replace('*', '%') + "' and status != 'D' " + "order by orderdate desc ").setFirstResult(firstrow).setMaxResults(pagesize).list();
        }
        else
        {
          result = session.createQuery("from Orders as orders where orders.orderId like '" + searchKey.replace('*', '%') + "' and status != 'D' " + "order by orderdate desc ").setFirstResult(firstrow).setMaxResults(pagesize).list();
        }
        

        tx.commit();
        for (int i = 0; i < result.size(); i++) {
          Orders ord = (Orders)result.get(i);
          FoxyOrderList odl = new FoxyOrderList();
          odl.setOrderId(ord.getOrderId());
          odl.setCompanyNameShort(ld.getCompanyNameShort(ord.getCnameCode(), 1));
          odl.setOrderDate(ord.getOrderDate());
          odl.setStyleCode(ord.getStyleCode());
          odl.setCustCode(ord.getCustCode());
          odl.setCustName(ld.getCustomer(ord.getCustCode()).getCustName());
          odl.setCustBrand(ord.getCustBrand());
          odl.setCustDivision(ord.getCustDivision());
          odl.setSeason(ord.getSeason());
          odl.setSeasonD(ld.getSeasonDesc(ord.getSeason()));
          odl.setMerchandiserName(ld.getMerchandiser(ord.getMerchandiser()).getFullName());
          odl.setImgFile(ord.getImgFile());
          tableList.add(odl);
        }
        
        orderListModel = new FoxyPagedDataModel(tableList, numofRec.intValue(), pagesize);
      }
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
    
    if (foxySessionData != null) {
      foxySessionData.setTableFirstRow(firstrow);
      foxySessionData.setTableRows(pagesize);
    }
    return orderListModel;
  }
  


  public String search()
  {
    foxySessionData.setAction("LIST");
    foxyTable.setFirst(0);
    foxySessionData.setTableFirstRow(0);
    return "success";
  }
  
  public String newSearch()
  {
    foxySessionData.setAction("SEARCH");
    foxySessionData.setTableFirstRow(0);
    foxySessionData.setTableRows(0);
    foxySessionData.setSearchKey(null);
    foxySessionData.setSearchType("1");
    foxySessionData.setBackToList(false);
    foxySessionData.setOrderId(null);
    return "NewOrderSearch";
  }
  
  public String backToList()
  {
    foxySessionData.setAction("LIST");
    foxySessionData.setBackToList(true);
    foxySessionData.setOrderId(null);
    return "BackToList";
  }
  


  public String uploadImage()
  {
    foxySessionData.setOrderId(foxySessionData.getPageParameter());
    return "uploadimage";
  }
  

  public String downloadOrderConfirmationForm()
  {
    foxySessionData.setOrderId(foxySessionData.getPageParameter());
    return "downloadOrderConfirmationForm";
  }
  






  public String duplicateNewOrderForm()
  {
    foxySessionData.setOrderId(foxySessionData.getPageParameter());
    foxySessionData.setAction("ADD");
    return "duplicateNewOrderForm";
  }
  





  public String orderIdTransferForm()
  {
    foxySessionData.setOrderId(foxySessionData.getPageParameter());
    foxySessionData.setAction("UPDATE");
    return "OrderIdTransferForm";
  }
  
  public List getSearchTypes()
  {
    return searchTypes;
  }
  
  public void setSearchTypes(List newValue) {
    searchTypes = newValue;
  }
  



  public String shortCut()
  {
    return "go_downloadOCF";
  }
  

  public String downloadFoxyOrderConfirmationForm()
  {
    foxySessionData.setOrderId(foxySessionData.getPageParameter());
    return "foxyDownloadOrderConfirmationForm";
  }
}
