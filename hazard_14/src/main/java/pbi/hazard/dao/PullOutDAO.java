package pbi.hazard.dao;

import java.io.Serializable;
import java.util.Calendar;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.MILLISECOND;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.TypedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import pbi.hazard.WicketApplication;
import pbi.hazard.common.DateTime;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

@Repository
// Loaded by Spring
public class PullOutDAO implements Serializable {

	@Autowired
	private SessionFactory sessionFactory;
	private static int LIMIT = 16;

	// private static Logger logger = WicketApplication.getLogger();

	// @SpringBean
	// private PullOutMemberDAO pmDao;

	// public void doStuff(){
	// System.out.println("doStuff.");
	// }

	@SuppressWarnings("unchecked")
	public List<PullOut> getPullOut(int count) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria criteria = DetachedCriteria.forClass(PullOut.class);
		criteria.addOrder(Order.desc("start"));

		// Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.MONTH, -4); // Within last 4 month
		// System.out.println(cal.getTime());
		// criteria.add(Restrictions.gt("start", cal.getTime())); // Today

		// Hibernate LIMIT
		// query.setFirstResult(n);
		// query.setMaxResults(m);

		ht.setMaxResults(count);
		return ht.findByCriteria(criteria);
	}

	public List<PullOut> getPullOut() {
		return getPullOut(LIMIT);
	}

	public List<PullOut> getPullOut(Date start, Date stop) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria criteria = DetachedCriteria.forClass(PullOut.class);
		criteria.addOrder(Order.desc("start"));

		// Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.MONTH, -4); // Within last 4 month
		// System.out.println(cal.getTime());
		criteria.add(Restrictions.ge("start", start));
		criteria.add(Restrictions.le("stop", stop));

		// System.out.println(criteria);

		// Hibernate LIMIT
		// query.setFirstResult(n);
		// query.setMaxResults(m);

		return ht.findByCriteria(criteria);
	}

	/**
	 * 
	 * @param start
	 * @param stop
	 * @return List of PullOuts in date interval where a winner was found
	 */
	public List<PullOut> getPullOutWinner(Date start, Date stop) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria criteria = DetachedCriteria.forClass(PullOut.class);

		criteria.add(Restrictions.isNotNull("winner"));
		criteria.add(Restrictions.ge("start", start));
		criteria.add(Restrictions.le("stop", stop));

//		criteria.setProjection(Projections.distinct(Projections.property("id")));
		
		criteria.addOrder(Order.asc("id"));


		return ht.findByCriteria(criteria);
	}

	 public PullOut getPullOut(Integer id) {
	 HibernateTemplate ht = new HibernateTemplate(sessionFactory);
	 return ht.get(PullOut.class, id);
	 }

	// read active members - via MemberDAO

	/*
	 * @SuppressWarnings("unchecked") public void savePullOut(PullOut t, Event e)
	 * { HibernateTemplate ht = new HibernateTemplate(sessionFactory);
	 * Collection<Object> entities = new Collection<Object>() { };
	 * entities.add(t); entities.add(e); ht.saveOrUpdateAll(entities); }
	 */

	public void savePullOut(PullOut pullOut) {
		// System.out.println("Save: " + pullOut);
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		ht.saveOrUpdate(pullOut);
		// System.out.println("Done: " + pullOut);

		// tmDao = new PullOutMemberDAO();
		// tmDao.savePullOutMember(pullOut);

		// return pullOut;
		// Event
		// startEvent.setDate(startDate);
		// System.out.println("Save Event: " + startEvent);
		// eventDao.save(startEvent);

		// http://forum.springsource.org/showthread.php?t=27441

		// PullOut

		// null

		// pullOut.setStart(startEvent);
		// xxxxxxxxx

		// PullOut pullOut = new PullOut();
		// pullOut.
		// MemberDAO memberDAO;
		// memberDAO.save(startEvent);
		// .save(getModelObject());
	}

	// public PullOut getLastClosed() {
	// /*
	// * String querySql = "	SELECT     ID,    NAME,    START,   stop "; querySql
	// * += "	FROM     PUBLIC.PULLOUT )"; querySql +=
	// * "	where stop = (SELECT MAX(stop) FROM PULLOUT)"; querySql +=
	// * "and stop>CURRENT_DATE";
	// */
	//
	// /*
	// * getNewestPullOut DetachedCriteria criteria =
	// * DetachedCriteria.forClass(PullOut.class); //
	// * criteria.addOrder(Order.desc("start")); // ht.setMaxResults(count);
	// * criteria.setProjection(Projections.max("id"));
	// *
	// * // List<PullOut>=
	// *
	// * // PullOut=ht.findByCriteria(criteria); return (PullOut)
	// * ht.findByCriteria(criteria);
	// *
	// * SELECT ID, NAME, NOTE, START, STOP, WINNER_ID FROM PUBLIC.PULLOUT where
	// * id=(select max(id) from PUBLIC.PULLOUT)
	// */
	//
	// HibernateTemplate ht = new HibernateTemplate(sessionFactory);
	// // return ht.get(PullOut.class, id);
	//
	// DetachedCriteria maxQuery = DetachedCriteria.forClass(PullOut.class);
	// maxQuery.setProjection(Projections.max("stop"));
	//
	// // DetachedCriteria todayQuery = DetachedCriteria.forClass( PullOut.class
	// );
	// // todayQuery.setProjection( Projections.property(propertyName)( "stop" )
	// );
	//
	// // Criteria query = getSession().createCriteria( PullOut.class );
	// // Session session = ht.getSessionFactory().getCurrentSession();
	// // Session session = sessionFactory.getCurrentSession();
	//
	// // session.createCriteria( PullOut.class );
	// // Criteria query = session.createCriteria( PullOut.class );
	// DetachedCriteria query = DetachedCriteria.forClass(PullOut.class);
	// // session.createCriteria( PullOut.class );
	// query.add(Property.forName("stop").eq(maxQuery));
	// // query.add( Restrictions.gt("stop", "current_date"));
	// // Date today = new Date();
	// // Date today = Calendar.getInstance().get getTime();
	// // System.out.println(today);
	// Calendar cal = Calendar.getInstance();
	// cal.set(HOUR_OF_DAY, 0);
	// cal.set(MINUTE, 0);
	// cal.set(SECOND, 0);
	// cal.set(MILLISECOND, 0);
	// // System.out.println(cal.getTime());
	//
	// query.add(Restrictions.gt("stop", cal.getTime())); // Today
	//
	// /*
	// * https://forum.hibernate.org/viewtopic.php?p=2387727
	// *
	// *
	// *
	// http://www.techinfopad.com/spring/100210572-detachedcriteria-on-date-column
	// * -type.html highestDate = new Date(); // modify the date, parse it from a
	// * string, bind it from web field or whatever ... DetachedCriteria c =
	// * DetachedCriteria.forClass(CustomTable.class);
	// * c.add(Restrictions.lt("reportDate", highestDate));
	// */
	//
	// List<PullOut> lastClosedList = ht.findByCriteria((DetachedCriteria) query);
	// // System.out.println(lastClosedList);
	//
	// if (lastClosedList.size() == 1) {
	// return lastClosedList.get(0);
	// } else {
	// return new PullOut();
	// }
	// }

	/*
	 * SELECT ID, NAME, START, STOP, NOTE, WINNER_ID FROM PUBLIC.PULLOUT where
	 * start>current_date
	 */
	public int startedToday() {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		// String query = "from PullOut where start>current_date";
		// List<PullOut> l = ht.find(query);

		Calendar cal = Calendar.getInstance();
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
		DetachedCriteria crit1 = DetachedCriteria.forClass(PullOut.class);
		crit1.add(Restrictions.gt("stop", cal.getTime()));
		List<PullOut> pullOutList = ht.findByCriteria(crit1);

		return pullOutList.size();
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	public List<PullOut> findWhenWon(Member m) {
		// logger.info("findWhenWon: " + m);

		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria crit1 = DetachedCriteria.forClass(PullOut.class);
		crit1.add(Restrictions.eq("winner", m));
		crit1.addOrder(Order.desc("stop"));

		List<PullOut> pullOut = Collections.emptyList();
		if (m.getId() != null) {
			ht.setMaxResults(LIMIT);
			pullOut = ht.findByCriteria(crit1);
		}
		// logger.info("pullOutList: " + pullOut);

		return pullOut;
	}

	public PullOut findPreviousWon(Member m, Date d) {
		// logger.info("findPreviousWon: " + m);

		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria crit1 = DetachedCriteria.forClass(PullOut.class);
		crit1.add(Restrictions.eq("winner", m));
		crit1.add(Restrictions.lt("stop", d));
		crit1.addOrder(Order.desc("stop"));

		List<PullOut> pullOut = Collections.emptyList();
		if (m.getId() != null) {
			ht.setMaxResults(1);
			pullOut = ht.findByCriteria(crit1);
		}
		// logger.info("pullOutList: " + pullOut);

		return pullOut.size() == 1 ? pullOut.get(0) : null;
	}

	/**
	 * 
	 * @return
	 */
	public PullOut getNewestPullOut() {
		// logger.info("getNewestPullOut" );
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria criteria = DetachedCriteria.forClass(PullOut.class);
		criteria.addOrder(Order.desc("start"));
		// criteria.setProjection(Projections.max("id"));
		ht.setMaxResults(1);

		// List<PullOut>=PullOut=ht.findByCriteria(criteria);

		// SELECT
		// ID,
		// NAME,
		// NOTE,
		// START,
		// STOP,
		// WINNER_ID
		// FROM
		// PUBLIC.PULLOUT
		// where id=(select max(id) from PUBLIC.PULLOUT)
		List<PullOut> pullOutList = Collections.emptyList();
		pullOutList = ht.findByCriteria(criteria);
		// logger.info("getNewestPullOut: " + pullOut);
		return pullOutList.size() == 1 ? pullOutList.get(0) : null;
	}

	/*
	 * String to display on event from Cap
	 */
	public String toolTip(Member m) {
		String info = m.getCap() + " ";
		if (m.getPhone() != null) {
			info += "Tlf.: ";
			info += spaceAdd(m.getPhone()) + "\n";
		}

		List<PullOut> Wins = findWhenWon(m);
		if (Wins.size() > 0) {
			// info += (info.length() == 0) ? "" : "\n";
			info += "Vinder:";
		}
		for (Iterator<PullOut> iterator = Wins.iterator(); iterator.hasNext();) {
			PullOut pullOut = iterator.next();
			info += "\n";
			info += spaceAdd(DateTime.formatDate(pullOut.getStop()));
		}

		return info;
	}

	/*
	 * 
	 */
	private String spaceAdd(String str) {
		return " " + str;
	}

}
