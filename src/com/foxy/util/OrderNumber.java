package com.foxy.util;

import com.foxy.db.HibernateUtilInternal;
import com.foxy.db.OrderNo;
import com.foxy.db.OrderNoReserved;
import com.foxy.db.OrderSummary;
import com.foxy.db.Orders;
import com.foxy.db.Parameter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;













public class OrderNumber
{
  Integer orderId = null;
  OrderNo ordno = null;
  Date dNow = null;
  SimpleDateFormat fmt = new SimpleDateFormat("yyyy");
  SimpleDateFormat sfmt = new SimpleDateFormat("yy");
  


  public OrderNumber()
  {
    dNow = new Date();
  }
  
  public String getOrderNumber(Integer mainFactoryCode, String orderYear, boolean considerReused, String companyName)
  {
    List result = null;
    String refNum = null;
    

    Session session;
    

    Transaction tx;
    

    if (considerReused) {
      try {
        session = HibernateUtilInternal.currentSession();
        tx = session.beginTransaction();
        DetachedCriteria subquerySummary = DetachedCriteria.forClass(OrderSummary.class)
        		 					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        							.setProjection(Projections.property("orderId"));
        
        DetachedCriteria subqueryOrder = DetachedCriteria.forClass(Orders.class)
        		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setProjection(Projections.property("orderId"));
        
        Criteria crit = session.createCriteria(OrderNoReserved.class);
        crit.add(Expression.eq("year", Integer.valueOf(Integer.parseInt(orderYear))));
        crit.add(Expression.eq("mainFactory", mainFactoryCode));
        crit.add(Expression.lt("expiredOn", Calendar.getInstance(Locale.US).getTime()));
        crit.add(Subqueries.propertyNotIn("reservedNo", subquerySummary));
        crit.add(Subqueries.propertyNotIn("reservedNo", subqueryOrder));
        crit.addOrder(Order.asc("reservedNo"));
        
        List resultList = crit.list();
        
        if (resultList.size() > 0) {
          OrderNoReserved ordReservedId = (OrderNoReserved)resultList.get(0);
          refNum = ordReservedId.getReservedNo();
          System.out.println(dNow.toString() + "  Reserved OrderId expired and reused [refnum:" + ordReservedId.getReservedNo() + "  ReservedBy:" + ordReservedId.getForUserId() + " expiredOn:" + ordReservedId.getExpiredOn().toString() + "]");
          

          session.delete(ordReservedId);
        }
        tx.commit();
      } catch (HibernateException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        HibernateUtilInternal.closeSession();
      }
    }
    


    if (refNum == null) {
      try {
        session = HibernateUtilInternal.currentSession();
        tx = session.beginTransaction();
        result = session.createQuery("from OrderNo where mainfactory =" + mainFactoryCode + " and year=" + orderYear).list();
        if (result.isEmpty()) {
          ordno = new OrderNo();
          ordno.setMainFactory(mainFactoryCode);
          ordno.setYear(Integer.valueOf(Integer.parseInt(orderYear)));
          ordno.setSequence(Integer.valueOf(1));
          session.saveOrUpdate(ordno);
          this.orderId = new Integer(1);
        } else {
          ordno = ((OrderNo)result.get(0));
          this.orderId = Integer.valueOf(ordno.getSequence().intValue() + 1);
          ordno.setSequence(this.orderId);
        }
        if (!StringUtils.stripToEmpty(companyName).isEmpty()) {
          Query qry = session.createQuery("from Parameter as param where param.code = :companyName");
          qry.setString("companyName", companyName);
          
          List parameterList = qry.list();
          if (!parameterList.isEmpty()) {
            Parameter param = (Parameter)parameterList.get(0);
            companyName = param.getDescription();
          }
        }
        tx.commit();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        HibernateUtilInternal.closeSession();
      }
      
      refNum = orderYear.substring(2);
      if (refNum.equals("13")) {
        refNum = refNum + mainFactoryCode + (!companyName.isEmpty() ? companyName.substring(0, 1) + String.format("%03d", new Object[] { this.orderId }) : String.format("%03d", new Object[] { this.orderId }));
      } else {
        refNum = refNum + mainFactoryCode + (!companyName.isEmpty() ? companyName.substring(0, 1) + String.format("%04d", new Object[] { this.orderId }) : String.format("%04d", new Object[] { this.orderId }));
      }
    } else {
      try {
        session = HibernateUtilInternal.currentSession();
        tx = session.beginTransaction();
        if (!StringUtils.stripToEmpty(companyName).isEmpty()) {
          Query qry = session.createQuery("from Parameter as param where param.code = :companyName");
          qry.setString("companyName", companyName);
          
          List parameterList = qry.list();
          if (!parameterList.isEmpty()) {
            Parameter param = (Parameter)parameterList.get(0);
            companyName = param.getDescription();
          }
        }
        tx.commit();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        HibernateUtilInternal.closeSession();
      }
      
      String yearAndFactory = refNum.substring(0, 3);
      Integer orderId = Integer.valueOf(Integer.parseInt(refNum.substring(4)));
      if (yearAndFactory.substring(0, 2).equals("13")) {
        refNum = yearAndFactory + (!companyName.isEmpty() ? companyName.substring(0, 1) + String.format("%03d", new Object[] { orderId }) : String.format("%03d", new Object[] { orderId }));
      } else {
        refNum = yearAndFactory + (!companyName.isEmpty() ? companyName.substring(0, 1) + String.format("%04d", new Object[] { orderId }) : String.format("%04d", new Object[] { orderId }));
      }
    }
    
    return refNum;
  }
}
