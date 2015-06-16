package pbi.hazard.dao;

import java.io.Serializable;
import java.util.Date;
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

import pbi.hazard.common.DateTime;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;

@Repository
public class MemberDAO implements Serializable {
	@Autowired
	private SessionFactory sessionFactory;

	// public void save(Member member) {
	public Member saveMember(Member member) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		// System.out.println(member); // DEBUG

		// if (member.getId() == null) {
		// // New
		// ht.save(member);
		// } else{
		// ht.update(member);}
		member.setUpdated(new Date()); // Now
		ht.saveOrUpdate(member);
		return member;
	}

	// @OrderBy("active desc,cap asc")
	@SuppressWarnings("unchecked")
	public List<Member> getMembers() {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		// Select all
		// return ht.find("from Member");
		// return ht.find("from Member order by active desc,cap");

		// return ht.loadAll(Member.class); // OK, but no sort order

		return ht.findByCriteria(DetachedCriteria.forClass(Member.class).addOrder(Order.asc("cap")));

		// From: HSQL DB Manager
		// SELECT * FROM PUBLIC.MEMBER order by active desc,cap
		// From: https://jira.springframework.org/browse/SPR-1411
		// getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Cat.class).addOrder(Order.asc("name")));
		// return
		// ht.findByCriteria(DetachedCriteria.forClass(Member.class).addOrder(Order.desc("active")).addOrder(Order.asc("cap")));
	}

	@SuppressWarnings("unchecked")
	public List<Member> getFilteredMembersSql(Member filterMember) {
		// System.out.println("Filter: " + m);
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		/*
		 * SELECT ID, ACTIVE, CAP, NAME FROM PUBLIC.MEMBER where lower(name) like
		 * '%jens%'
		 */
		String query = "from Member";
		String where = "";
		if (filterMember.getCap() != 0) {
			where += "cap=" + filterMember.getCap();
		}
		
		if (filterMember.getFirstName() != null) {
			String andWhere = "lower(firstname) like '%" + filterMember.getFirstName().toLowerCase() + "%'";
			where = (where == "" ? andWhere : where + " and " + andWhere);
		}

		if (filterMember.getLastName() != null && filterMember.getLastName().length() > 0) {
			String andWhere = "lower(lastname) like '%" + filterMember.getLastName().toLowerCase() + "%'";
			where = (where == "" ? andWhere : where + " and " + andWhere);
		}

		if (filterMember.getActive()) {
			String andWhere = "active=true";
			where = (where == "" ? andWhere : where + " and " + andWhere);
		}

		if (filterMember.getUpdated() != null) {
			String andWhere = "updated >= '" + DateTime.formatSqlDate(filterMember.getUpdated()) + "'";
			where = (where == "" ? andWhere : where + " and " + andWhere);
		}

		if (where != "") {
			query += " where " + where;
		}
		query += " order by cap asc";
		// System.out.println(query);

		return ht.find(query);
	}

	@SuppressWarnings("unchecked")
	// public List<Member> getActiveMembers() {
	public List<Member> getByActive(Boolean active) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		// Select based on active field
		// return ht.find("from Member where active = ?", active);
		return ht.find("from Member where active = ? order by cap asc", active);
		// Try this. .findByNamedQuery("member.activeMembers", active);
	}

	// public Member getMember(Integer id) {
	public Member getMemberById(Integer id) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		return ht.get(Member.class, id);
		// Member member = (Member) ht.load(Member.class, id);
		// => Unexpected RuntimeException
		// WicketMessage: Exception in rendering component: [MarkupContainer
		// [Component id = form]]Root
		// cause:org.hibernate.LazyInitializationException: could not initialize
		// proxy - no Session
		// return member;
	}

	public int getMaxCap() {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		List<Integer> l = ht.find("select max(cap) from Member");
		// System.out.println(l.size() + "\n" + l);
		if (l.get(0) == null) {
			return 0;
		} else {
			return l.get(0);
		}
	}

	// public List<String> getMailList() {
	// HibernateTemplate ht = new HibernateTemplate(sessionFactory);
	//
	// return ht.find("mail1,mail2 from Member where active = ? order by cap asc",
	// Boolean.TRUE);
	// }

	// Get list of used values in payment field
	public List<String> getPaymentList() {
		String column = "payment";
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		DetachedCriteria criteria = DetachedCriteria.forClass(Member.class);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(column));
		criteria.setProjection(projList);

		criteria.add(Restrictions.isNotNull(column));
		// criteria.add(Restrictions.isNotEmpty(column)); // => Err
		criteria.setProjection(Projections.groupProperty(column));
		criteria.addOrder(Order.asc(column));

		// ht.setMaxResults(10);
		return ht.findByCriteria(criteria);
	}

	/*
	 * SELECT CAP, EMAIL1, EMAIL2, FIRSTNAME, LASTNAME, NOTES, PAYMENT, PHONE FROM
	 * PUBLIC.MEMBER where ACTIVE=true {and PAYMENT = 'Direkte'} {order by
	 * PAYMENT}
	 */
	@SuppressWarnings("unchecked")
	public List<Member> getFilteredMembers(Member filterMember) {
		// System.out.println(member.fieldsToString());
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);

		// Field[] f = Member.class.getDeclaredFields();
		// for (int i = 0; i < f.length; i++) {
		// System.out.println(f[i]);
		// }

		DetachedCriteria criteria = DetachedCriteria.forClass(Member.class);
		/*
		 * cap 'null' firstName 'null' lastName 'null' nickName 'null' email1 'null'
		 * email2 'null' phone 'null' contactId 'null' notes 'null' payment
		 * 'Direkte' updated 'null' active 'null'
		 */
		if (filterMember.getCap() != 0) {
			// System.out.println("cap: " + member.getCap());
			criteria.add(Restrictions.eq("cap", filterMember.getCap()));
		}

		if (filterMember.getFirstName() != null) {
			// System.out.println("firstName: " +
			// "%".concat(member.getFirstName()).concat("%"));
			criteria.add(Restrictions.ilike("firstName", "%".concat(filterMember.getFirstName()).concat("%")));
		}
		if (filterMember.getLastName() != null) {
			// System.out.println("lastName: " +
			// "%".concat(member.getLastName()).concat("%"));
			criteria.add(Restrictions.ilike("lastName", "%".concat(filterMember.getLastName()).concat("%")));
		}
		if (filterMember.getEmail1() != null) {
			// System.out.println("email1: " +
			// "%".concat(member.getEmail1()).concat("%"));
			criteria.add(Restrictions.ilike("email1", "%".concat(filterMember.getEmail1()).concat("%")));
		}
		if (filterMember.getContactId() != null) {
			// System.out.println("contactId: " +
			// "%".concat(member.getContactId()).concat("%"));
			criteria.add(Restrictions.ilike("contactId", "%".concat(filterMember.getContactId()).concat("%")));
		}
		if (filterMember.getPayment() != null) {
			// System.out.println("payment: " + member.getPayment());
			criteria.add(Restrictions.eq("payment", filterMember.getPayment()));
		}
		if (filterMember.getActive()) {
			// System.out.println("active");
			criteria.add(Restrictions.eq("active", true));
		}
		if (filterMember.getUpdated() != null) {
			// System.out.println("active");
			criteria.add(Restrictions.gt("updated", filterMember.getUpdated()));
		}

		criteria.addOrder(Order.asc("cap"));

		return ht.findByCriteria(criteria);
	}

}
