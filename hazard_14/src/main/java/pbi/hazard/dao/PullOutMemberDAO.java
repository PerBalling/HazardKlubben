package pbi.hazard.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

//import pbi.hazard.model.SqlGroupResult;

@Repository
// Loaded by Spring
public class PullOutMemberDAO implements Serializable {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private MemberDAO memberDAO;

	// public PullOutMember getPullOutMember(Integer id) {
	// HibernateTemplate ht = new HibernateTemplate(sessionFactory);
	// return ht.get(PullOutMember.class, id);
	// }

	public void savePullOutMembers(PullOut pullOut) {
		// System.out.println("Save PM: " + pullOut);
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		List<Member> poMembers = memberDAO.getByActive(Boolean.TRUE);

		// System.out.println("Active members: " + poMembers.size());
		for (Iterator<Member> i = poMembers.iterator(); i.hasNext();) {
			Member m = i.next();
			// System.out.println("Active: >> " + m);
			PullOutMember pullOutMember = new PullOutMember(pullOut, m);
			ht.saveOrUpdate(pullOutMember);
		}
		// System.out.println("Done PM: " + pullOut);

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

	/**
	 * @param pullOut
	 * @param orderBy
	 *          1=Member.Cap, 2=PullOutMember.pulledOut
	 * @return List of PullOutMembers from a specific PullOut
	 */
	public List<PullOutMember> getPullOutMemberList(PullOut pullOut, int orderBy) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		// String query =
		// "from Terminal t where (:id is null or t.id = :id) and (:name is null or t.name = :name)";
		// find("FROM Ziel z WHERE z.mitarbeiter.mitarbeiterId = ?", mitarbeiterId);

		// System.out.println(pullOut);
		// String query = "from PullOutMember pm where pm.pullOut.id = ?";
		// String query = "from PullOutMember pm where pm.pullOut = ?";
		String query = "from PullOutMember pom where pom.pullOut = ?";
		switch (orderBy) {
		case 2:
			query += " order by pom.pulledOut";
			break;

		default:
			query += " order by pom.member.cap";
			break;
		}

		// return ht.findByValueBean(query, pullOut);
		// return ht.find(query, pullOut.getId());
		return ht.find(query, pullOut);
	}

	/**
	 * 
	 * @param listPullOut
	 * @return Distinct list of onsite Members from list of pullOuts
	 */
	public List<Member> getMemberList(List<PullOut> listPullOut) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria criteria = DetachedCriteria.forClass(PullOutMember.class);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.distinct(Projections.property("member")));
		criteria.setProjection(projList);

		criteria.add(Restrictions.eq("onsite", true));
		criteria.add(Restrictions.in("pullOut", listPullOut.toArray()));

		// criteria.addOrder(Order.asc("cap"));

		return ht.findByCriteria(criteria);
	}

	/**
	 * 
	 * @param listPullOut
	 * @return List of onsite PullOutMembers from list of pullOuts
	 */
	public List<PullOutMember> getOnsiteList(List<PullOut> listPullOut) {
		if (listPullOut.size() == 0) {
			return null;
		} else {
			HibernateTemplate ht = new HibernateTemplate(sessionFactory);

			DetachedCriteria criteria = DetachedCriteria.forClass(PullOutMember.class);

			criteria.add(Restrictions.in("member", getMemberList(listPullOut).toArray()));
			criteria.add(Restrictions.in("pullOut", listPullOut.toArray()));

			// Hibernate expects the entire model object (PullOut) - not only id
			// criteria.setProjection(Projections.distinct(Projections.property("id")));

			criteria.addOrder(Order.asc("member"));
			criteria.addOrder(Order.asc("pullOut"));

			return ht.findByCriteria(criteria);
		}
	}

	// public List<PullOutMember> getMemberOnsiteList(PullOut pullOut, Member
	// member) {
	// HibernateTemplate ht = new HibernateTemplate(sessionFactory);
	//
	// DetachedCriteria criteria = DetachedCriteria.forClass(PullOutMember.class);
	//
	// // criteria.add(Restrictions.eq("onsite", true));
	// // criteria.add(Restrictions.isNotNull("pulledOut"));
	// // criteria.add(Restrictions.gt("pulledOut", fromDate));
	// // criteria.add(Restrictions.lt("pulledOut", toDate));
	//
	// criteria.add(Restrictions.eq("member", member));
	// criteria.add(Restrictions.eq("pullOut", pullOut));
	//
	// criteria.addOrder(Order.asc("pullOut"));
	//
	// return ht.findByCriteria(criteria);
	// }

	// public List<PullOutMember> getPullOutMemberListCopy(PullOut oldPullOut) {
	// List<PullOutMember> list=getPullOutMemberList(oldPullOut);
	// for (Iterator iterator = list.iterator(); iterator.hasNext();) {
	// PullOutMember pullOutMember = (PullOutMember) iterator.next();
	// System.out.println(">pom>"+pullOutMember);
	// }
	// return list;
	// }

	public void savePullOutCopyMembers(PullOut newPullOut, PullOut oldPullOut) {
		// System.out.println("Save PMcopy:\n " + newPullOut + ", " + oldPullOut);
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		List<PullOutMember> list = getPullOutMemberList(oldPullOut, 1);

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			PullOutMember pom = (PullOutMember) iterator.next();
			// System.out.println(">pom>" + pom);
			// pom.setId(null);
			// pom.setPullOut(newPullOut);
			// pom.setPulledOut(null);
			//
			PullOutMember newPom = new PullOutMember(newPullOut, pom.getMember());
			newPom.setOnsite(pom.getOnsite());
			ht.saveOrUpdate(newPom);
		}
		// System.out.println("Active members: " + poMembers.size());
		// for (Iterator<Member> i = poMembers.iterator(); i.hasNext();) {
		// Member m = (Member) i.next();
		// // System.out.println("Active: >> " + m);
		// PullOutMember pullOutMember = new PullOutMember(pullOut, m);
		// ht.saveOrUpdate(pullOutMember);
		// }

		// System.out.println("Done PMcopy.");
	}

	public void updatePullOutMember(PullOutMember pom) {
		// System.out.println("Update PM: " + pom);
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		ht.saveOrUpdate(pom);

		// System.out.println("Done PM: " + pom);

	}

	/*
	 * Implement SQL: SELECT ID, ONSITE, PULLEDOUT, MEMBER_ID, PULLOUT_ID FROM
	 * PUBLIC.PULLOUTMEMBER WHERE PULLOUT_ID=xx AND PULLEDOUT=(SELECT
	 * MAX(PULLEDOUT) FROM PULLOUTMEMBER WHERE PULLOUT_ID=xx)
	 */
	public PullOutMember findLatest(PullOut p) {
		// System.out.println("findLatest: "+p);
		PullOutMember latest = new PullOutMember();

		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		// Criteria crit1 =
		// sessionFactory.openSession().createCriteria(PullOutMember.class);
		// Criteria crit1 =
		// ht.getSessionFactory().getCurrentSession().createCriteria(PullOutMember.class);
		DetachedCriteria crit1 = DetachedCriteria.forClass(PullOutMember.class);
		crit1.add(Restrictions.eq("pullOut", p));
		crit1.setProjection(Projections.max("pulledOut"));
		// System.out.println(crit1);
		// List pomDate = crit1.list();
		List pomDate = ht.findByCriteria(crit1);
		// System.out.println(pomDate);
		// sessionFactory.close();
		Date d;
		// if (pomDate.size() == 1) {
		// d=(Date) pomDate.get(0);
		// }else {
		// throw new SQLDataException("Max date not found for PullOut=" + p);
		// }
		try {
			d = (Date) pomDate.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PullOutMember();
		}

		// Criteria crit2 =
		// ht.getSessionFactory().getCurrentSession().createCriteria(PullOutMember.class);
		DetachedCriteria crit2 = DetachedCriteria.forClass(PullOutMember.class);
		crit2.add(Restrictions.eq("pullOut", p));
		crit2.add(Restrictions.eq("pulledOut", d));
		// System.out.println(crit2);
		// List<PullOutMember> pom = crit2.list();
		List<PullOutMember> pom = ht.findByCriteria(crit2);
		// System.out.println(pom);
		// sessionFactory.close();

		if (pom.size() > 0) {
			latest = pom.get(0);
			// System.out.println(latest);
		}
		return latest;

		// try {
		// latest = pom.get(0);
		// System.out.println(latest);
		// return latest;
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return new PullOutMember();
		// }
	}

	/**
	 * Finds last pulled which is onsite NOTE: It is not checked if all onsite is
	 * pulled!
	 * 
	 * @param p
	 * @return
	 */
	// public PullOutMember findLastOnsitePulled(PullOut p) {
	// return findLastOnsitePulled(p, 0);
	// }

	/**
	 * 
	 * @param p
	 * @param index
	 * @return
	 */
	public PullOutMember findLastOnsitePulled(PullOut p, int index) {
		// System.out.println(">>> findLastOnsitePulled: "+p);
		PullOutMember latestOnsitePulled = new PullOutMember();

		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		// Find the datetime for latest onsite pulled out
		DetachedCriteria crit1 = DetachedCriteria.forClass(PullOutMember.class);
		crit1.add(Restrictions.eq("pullOut", p));
		crit1.add(Restrictions.eq("onsite", true));
		crit1.add(Restrictions.isNotNull("pulledOut"));
		// crit1.setProjection(Projections.max("pulledOut"));
		// crit1.setProjection(Projections.property("pulledOut"));
		crit1.addOrder(Order.desc("pulledOut"));
		// System.out.println(crit1);

		List<PullOutMember> pomMember = ht.findByCriteria(crit1);

		// for (Iterator iterator = pomMember.iterator(); iterator.hasNext();) {
		// PullOutMember pullOutMember = (PullOutMember) iterator.next();
		// System.out.println("pom="+pullOutMember);
		// }

		PullOutMember retPom = null;
		int i = 0;
		for (PullOutMember pullOutMember : pomMember) {
			if (index == i++) {
				// System.out.println("pom2: "+ i +" "+pullOutMember);
				retPom = pullOutMember;

			}
			// else
			// {System.out.println("pom2: "+ i );}
		}

		return retPom;

		/*
		 * List pomDate = ht.findByCriteria(crit1); //
		 * System.out.println("pomDate\t"+index+"\n"+pomDate);
		 * 
		 * Date d;
		 * 
		 * try { // if (pomDate.size()>index) { // d = (Date) pomDate.get(index); //
		 * } else { d = (Date) pomDate.get(0); // } } catch (Exception e) {
		 * e.printStackTrace(); return new PullOutMember(); }
		 * 
		 * // Find the POM based on datetime from crit1 DetachedCriteria crit2 =
		 * DetachedCriteria.forClass(PullOutMember.class);
		 * crit2.add(Restrictions.eq("pullOut", p));
		 * crit2.add(Restrictions.eq("pulledOut", d)); // System.out.println(crit2);
		 * 
		 * List<PullOutMember> pom = ht.findByCriteria(crit2); //
		 * System.out.println(pom);
		 * 
		 * if (pom.size()>0) { latestOnsitePulled = pom.get(0); //
		 * System.out.println(latestOnsitePulled); }
		 * 
		 * // System.out.println("<<< findLastOnsitePulled: "+p); return
		 * latestOnsitePulled;
		 */
	}

	/**
	 * List of members onsite grouped by pullout ???
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return list
	 */
	/*
	 * public List<SqlGroupResult> getOnsiteList(Date fromDate, Date toDate) { /*
	 * 
	 * SELECT m.cap ,m.firstname ,m.lastname ,to_char( p.stop, 'YYYY-MM-DD')
	 * "Dato" ,count(m.id) "Antal" FROM PULLOUTMEMBER pm, member m, pullout p
	 * where pm.MEMBER_ID=m.id and pm.PULLOUT_ID =p.id and pm.ONSITE and to_char(
	 * p.stop, 'YYYY')='2012' group by m.cap,m.firstname ,m.lastname ,"Dato" order
	 * by "Dato",m.cap
	 * 
	 * 
	 * SELECT to_char( PULLEDOUT, 'YYYY-MM-DD') "Dato", MEMBER_ID
	 * ,count(PULLOUT_ID) FROM PUBLIC.PULLOUTMEMBER where PULLOUT_ID in
	 * (11,12,13,14,15) and onsite group by MEMBER_ID
	 * 
	 * 
	 * http://stackoverflow.com/questions/8491796/hibernate-group-by-criteria-object
	 * For example : SELECT column_name, max(column_name) , min (column_name) ,
	 * count(column_name) FROM table_name WHERE column_name > xxxxx GROUP BY
	 * column_name Its equivalent criteria object is : List result =
	 * session.createCriteria(SomeTable.class) .add(Restrictions.ge("someColumn",
	 * xxxxx)) .setProjection(Projections.projectionList()
	 * .add(Projections.groupProperty("someColumn"))
	 * .add(Projections.max("someColumn")) .add(Projections.min("someColumn"))
	 * .add(Projections.count("someColumn")) ).list();
	 * 
	 * Iterator resultIterator = results.iterator(); Object[] obj = (Object[])
	 * resultIterator.next(); for (int i = 0; i < obj.length; i++) {
	 * System.out.print(obj[i] + "\t"); }
	 */

	// System.out.println(fromDate + "\t" + toDate);
	/*
	 * HibernateTemplate ht = new HibernateTemplate(sessionFactory); //
	 * DetachedCriteria crit1 = DetachedCriteria.forClass(PullOut.class); //
	 * crit1.add(Restrictions.ge("start", fromDate)); //
	 * crit1.add(Restrictions.ge("stop", toDate)); // System.out.println(crit1);
	 * 
	 * DetachedCriteria crit2 = DetachedCriteria.forClass(PullOutMember.class);
	 * crit2.add(Restrictions.eq("onsite", true));
	 * crit2.add(Restrictions.isNotNull("pulledOut"));
	 * crit2.add(Restrictions.gt("pulledOut", fromDate));
	 * crit2.add(Restrictions.lt("pulledOut", toDate)); //
	 * crit2.setProjection(Projections.count("pulledOut")); //
	 * crit2.addOrder(Order.desc("pulledOut")); //
	 * crit2.setProjection(Projections.groupProperty("pullOut"));
	 * 
	 * // join PULLOUT_ID -> WINNER_ID is not null
	 * 
	 * ProjectionList projectionList = Projections.projectionList(); //
	 * projectionList.add(Projections.count("member")); //
	 * projectionList.add(Projections.groupProperty("member")); //
	 * projectionList.add(Projections.groupProperty("pulledOut"));
	 * 
	 * //
	 * http://stackoverflow.com/questions/2067363/group-by-month-with-criteria-in
	 * -hibernate // projectionList.add(Projections.sqlGroupProjection( //
	 * "to_char({alias}.pulledOut, 'YYYY-MM-DD') as date, MEMBER_ID as member, count(MEMBER_ID) as onsite"
	 * , // "to_char({alias}.pulledOut, 'YYYY-MM-DD'), MEMBER_ID", // new
	 * String[]{"date", "member", "onsite"}, // new Type[] {Hibernate.STRING,
	 * Hibernate.INTEGER, Hibernate.INTEGER}));
	 * 
	 * // OK: // projectionList // .add(Projections // .sqlGroupProjection( //
	 * "YEAR({alias}.pulledOut) as year, MONTH({alias}.pulledOut) as month, DAYOFMONTH({alias}.pulledOut) as day,"
	 * // + " MEMBER_ID as member, count(MEMBER_ID) as onsite", //
	 * "YEAR({alias}.pulledOut), MONTH({alias}.pulledOut), DAYOFMONTH({alias}.pulledOut), MEMBER_ID"
	 * , // new String[] { "year", "month", "day", "member", "onsite" }, new
	 * Type[] { // Hibernate.INTEGER, // Hibernate.INTEGER, Hibernate.INTEGER,
	 * Hibernate.INTEGER, // Hibernate.INTEGER }));
	 * 
	 * // select // YEAR(this_.pulledOut) as year, // MONTH(this_.pulledOut) as
	 * month, // DAYOFMONTH(this_.pulledOut) as day, // MEMBER_ID as member, //
	 * count(MEMBER_ID) as onsite // from // PullOutMember this_ // where //
	 * this_.onsite=true // and this_.pulledOut is not null // and
	 * this_.pulledOut>'2012-01-23' // and this_.pulledOut<NOW() // group by //
	 * YEAR(this_.pulledOut), // MONTH(this_.pulledOut), //
	 * DAYOFMONTH(this_.pulledOut), // MEMBER_ID
	 * 
	 * // TEST: Group By list // projectionList // .add(Projections //
	 * .sqlGroupProjection( //
	 * "YEAR({alias}.pulledOut) as year, MONTH({alias}.pulledOut) as month, DAYOFMONTH({alias}.pulledOut) as day,"
	 * // + " MEMBER_ID as member, count(MEMBER_ID) as onsite", //
	 * "MEMBER_ID, YEAR({alias}.pulledOut), MONTH({alias}.pulledOut), DAYOFMONTH({alias}.pulledOut)"
	 * , // new String[] { "member", "year", "month", "day", "onsite" }, new
	 * Type[] { // Hibernate.INTEGER, // Hibernate.INTEGER, Hibernate.INTEGER,
	 * Hibernate.INTEGER, // Hibernate.INTEGER }));
	 * 
	 * projectionList.add(Projections.sqlGroupProjection(
	 * "YEAR({alias}.pulledOut) as year, MONTH({alias}.pulledOut) as month, DAYOFMONTH({alias}.pulledOut) as day,"
	 * +
	 * " PULLOUT_ID as pullout,  MEMBER_ID as member, count(MEMBER_ID) as onsite",
	 * "YEAR({alias}.pulledOut), MONTH({alias}.pulledOut), DAYOFMONTH({alias}.pulledOut), PULLOUT_ID, MEMBER_ID"
	 * , new String[] { "year", "month", "day", "pullout", "member", "onsite" },
	 * new Type[] { Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER,
	 * Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER }));
	 * 
	 * // https://forum.hibernate.org/viewtopic.php?p=2422170 //
	 * crit2.addOrder(Order.desc("aliasInCriteriaScope"));
	 * 
	 * crit2.setProjection(projectionList);
	 * 
	 * // SELECT to_char( PULLEDOUT, 'YYYY-MM-DD') "Dato" // , MEMBER_ID // --
	 * ,PULLOUT_ID // ,count(MEMBER_ID) "Count" // FROM PUBLIC.PULLOUTMEMBER //
	 * where PULLOUT_ID in (37,38,39) // and onsite // -- order by PULLEDOUT desc
	 * // group by "Dato", MEMBER_ID
	 * 
	 * // select // month(this_.pulledOut) as month, // year(this_.pulledOut) as
	 * year // from // PullOutMember this_ // where // this_.onsite=true // and
	 * this_.pulledOut is not null // and this_.pulledOut>'2012-11-01' // -- and
	 * this_.pulledOut<? // group by // month(this_.pulledOut), //
	 * year(this_.pulledOut)
	 * 
	 * crit2.setResultTransformer(new
	 * AliasToBeanResultTransformer(SqlGroupResult.class));
	 * 
	 * List<SqlGroupResult> list = ht.findByCriteria(crit2); //
	 * System.out.println(list.size()); // sqlGroupResult[] res =
	 * (sqlGroupResult[]) list.toArray(new // sqlGroupResult[list.size()]);
	 * 
	 * // for (Iterator<SqlGroupResult> it = list.iterator(); it.hasNext();) { //
	 * SqlGroupResult res = (SqlGroupResult) it.next(); //
	 * System.out.println(res); // }
	 * 
	 * // Iterator<PullOutMember> resultIterator = list.iterator(); // Object[]
	 * obj = (Object[]) resultIterator.next(); // for (int i = 0; i < obj.length;
	 * i++) { // System.out.print(obj[i] + "\t"); // }
	 * 
	 * // for (int i = 0; i < list.length; i++) { // System.out.println(list[i]);
	 * // }
	 * 
	 * // for (int i = 0; i < res.length; i++) { // System.out.println(res[i]); //
	 * }
	 * 
	 * // for (Iterator<sqlGroupResult> it = (Iterator<sqlGroupResult>) //
	 * list.iterator(); it.hasNext();) { // sqlGroupResult obj = (sqlGroupResult)
	 * it.next(); // System.out.println(obj); // }
	 * 
	 * // for (sqlGroupResult obj : list) { // System.out.println(obj); //
	 * String[] sa=(String[]) obj; // for (int i = 0; i < sa.length; i++) { //
	 * System.out.println(sa[i]); // } // }
	 * 
	 * return list; }
	 */

	// public List<SqlGroupResult> getOnsiteList2(Date fromDate, Date toDate) {
	// return null;
	// // 07-09-2012 Trækning 4-1
	// }

	/*
	 * SELECT m.cap, m.firstname, m.lastname, pm.ONSITE, pm.PULLEDOUT,
	 * pm.PULLOUT_ID FROM PUBLIC.PULLOUTMEMBER pm, member m where
	 * pm.MEMBER_ID=m.id and pm.ONSITE and pm.PULLEDOUT>'2012-01-01' group by
	 * YEAR(pm.PULLEDOUT), MONTH(pm.PULLEDOUT) order by PULLOUT_ID, PULLEDOUT
	 * 
	 * 
	 * 
	 * SELECT count(m.cap), m.firstname, m.lastname, pm.ONSITE, TO_CHAR (
	 * pm.PULLEDOUT, 'YYYY.MM.DD' ) "Dato", pm.PULLOUT_ID FROM
	 * PUBLIC.PULLOUTMEMBER pm, member m where pm.MEMBER_ID=m.id and pm.ONSITE and
	 * pm.PULLEDOUT>'2012-01-01' group by cap order by PULLOUT_ID, PULLEDOUT
	 * 
	 * 
	 * 
	 * 
	 * SELECT TO_CHAR ( pm.PULLEDOUT, 'DD.MM.YYYY' ) "Dato", m.firstname,
	 * m.lastname , count(*) FROM PUBLIC.PULLOUTMEMBER pm, member m where
	 * pm.MEMBER_ID=m.id and m.cap=25 and pm.ONSITE and pm.PULLEDOUT>'2012-01-01'
	 * group by TO_CHAR ( pm.PULLEDOUT, 'DD.MM.YYYY' ), m.firstname, m.lastname
	 * //order by pm.PULLEDOUT
	 * 
	 * 
	 * --------------
	 * 
	 * SELECT TO_CHAR ( pm.PULLEDOUT, 'DD.MM.YYYY' ) "Dato", m.firstname,
	 * m.lastname , count(*) "Antal" FROM PUBLIC.PULLOUTMEMBER pm, member m where
	 * pm.MEMBER_ID=m.id and pm.ONSITE and pm.PULLEDOUT>'2012-01-01' group by
	 * "Dato", m.firstname, m.lastname order by "Dato"
	 * 
	 * 
	 * 
	 * SELECT TO_CHAR ( pm.PULLEDOUT, 'DD.MM.YYYY' ) "Dato" ,m.id ,m.firstname
	 * ,m.lastname , count(*) "Antal" FROM PUBLIC.PULLOUTMEMBER pm, member m where
	 * pm.MEMBER_ID=m.id and pm.ONSITE and pm.PULLEDOUT>'2012-01-01' group by
	 * "Dato" ,m.id ,m.firstname ,m.lastname order by "Dato"
	 * 
	 * 
	 * --------------
	 * 
	 * 
	 * 
	 * SELECT m.cap, m.firstname, m.lastname, pm.ONSITE, pm.PULLEDOUT,
	 * pm.PULLOUT_ID FROM PUBLIC.PULLOUTMEMBER pm, member m where
	 * pm.MEMBER_ID=m.id //and pm.ONSITE and to_char( pm.PULLEDOUT,
	 * 'YYYY-MM-DD')='2012-09-07' and cap=223
	 * 
	 * 
	 * 
	 * 
	 * 
	 * SELECT m.cap ,m.firstname ,m.lastname ,pm.ONSITE ,pm.PULLEDOUT
	 * ,pm.PULLOUT_ID ,p.stop FROM PULLOUTMEMBER pm, member m, pullout p where
	 * pm.MEMBER_ID=m.id and pm.PULLOUT_ID =p.id //and pm.ONSITE and to_char(
	 * pm.PULLEDOUT, 'YYYY-MM-DD')='2012-09-07' //and m.cap=226 order by pm.id
	 * 
	 * 
	 * 
	 * 
	 * select to_char(p.stop, 'DD-MM-YYYY') "Dato" , p.name , p.note ,
	 * count(pm.onsite) "OnSite" from pulloutmember pm left outer join pullout p
	 * on p.id = pm.pullout_id where pm.onsite=true //and datediff('dd', p.stop,
	 * current_timestamp) < 366 //and to_char( pm.PULLEDOUT,
	 * 'YYYY-MM-DD')='2012-09-07' group by p.stop, p.name, p.note, pm.pullout_id
	 * order by p.stop
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * select to_char(p.stop, 'DD-MM-YYYY') "Dato" , p.name , p.note ,
	 * count(pm.onsite) "OnSite" from pulloutmember pm left outer join pullout p
	 * on p.id = pm.pullout_id where pm.onsite=true //and datediff('dd', p.stop,
	 * current_timestamp) < 366 and to_char( pm.PULLEDOUT,
	 * 'YYYY-MM-DD')='2011-06-17' group by p.stop, p.name, p.note, pm.pullout_id
	 * order by p.stop
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * SELECT m.cap ,m.firstname ,m.lastname //,pm.ONSITE //,to_char(pm.PULLEDOUT
	 * ,'YYYY-MM-DD') //,pm.PULLOUT_ID //,p.stop ,to_char( p.stop, 'YYYY-MM-DD')
	 * "Dato" //,p.name ,count(m.id) "Antal" FROM PULLOUTMEMBER pm, member m,
	 * pullout p where pm.MEMBER_ID=m.id and pm.PULLOUT_ID =p.id and pm.ONSITE
	 * //and to_char( p.stop, 'YYYY-MM-DD')='2011-06-17' //and "Dato"='2011-06-17'
	 * and to_char( p.stop, 'YYYY')='2012' //and to_char( p.stop, 'YYYY-MM-DD
	 * HH24:MI')='2011-06-17 16:30' //and to_char( p.stop, 'YYYY-MM-DD
	 * HH24:MI')='2011-06-17 16:43' //and p.id=13 //and m.cap=226 group by
	 * m.cap,m.firstname ,m.lastname ,"Dato" //,p.name order by "Dato",m.cap
	 */

}
