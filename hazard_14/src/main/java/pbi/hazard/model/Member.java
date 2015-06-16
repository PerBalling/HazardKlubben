package pbi.hazard.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.wicket.version.undo.Change;

@Entity
// @ NamedQuery(name="member.activeMembers",
// query="select m from Member m where active = ?")
public class Member implements Serializable { // extends Change
	private Integer id;
	private Integer cap; // Medlnr
	private String firstName; // Fornavn
	private String lastName; // Efternavn
	private String nickName;
	private String email1; // Email
	private String email2;
	private String phone;
	private String contactId; // Loennr
	private String notes;
	private String payment; // Betaling
	private Date updated; // Datetime

	/*
	 * SELECT ID, "Medlnr", "Fornavn", "Efternavn", "Ledig", "Loennr", "Email",
	 * "Betaling" FROM "PUBLIC"."Hazard"
	 */
	private Boolean active; // Ledig: 0: Active. -1: False

	// private List<PullOutMember> pullOutMember;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String returnName() {
		return firstName + " " + (lastName == null ? "" : lastName);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return (lastName == null ? "" : lastName);
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(length = 40)
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		// System.out.println("setNickName: "+this.nickName +"\t"+ nickName);
		this.nickName = nickName;
	}

	@Column(unique = true, nullable = false)
	public Integer getCap() {
		return (cap == null ? 0 : cap);
	}

	public void setCap(Integer cap) {
		this.cap = cap;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(length = 20)
	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/*	
	*//**
	 * @param pullOutMember
	 *          the pullOutMember to set
	 */
	/*
	 * public void setPullOutMember(List<PullOutMember> pullOutMember) {
	 * this.pullOutMember = pullOutMember; }
	 *//**
	 * @return the pullOutMember
	 */
	/*
	 * public List<PullOutMember> getPullOutMember() { return pullOutMember; }
	 */

	public Member() {
		// System.out.println("Constructor: "+this);
	}

	public Member(Integer id, Integer cap, String fname, Boolean active) {
		// super();
		this.id = id;
		this.cap = cap;
		this.firstName = fname;
		this.active = active;
	}

	// @Override
	public String toString() {
		return "Member [id=" + id + ", cap=" + cap + ", firstName=" + (firstName == null ? "" : firstName) + ", lastName="
				+ (lastName == null ? "" : lastName) + ", active=" + active + "]";
	}

	// public String fieldsToString() {
	// StringBuffer buff = new StringBuffer();
	//
	// Field[] fields = this.getClass().getDeclaredFields();
	// for (int i = 0; i < fields.length; i++) {
	// try {
	// buff.append(fields[i].getName()).append("\t'").append(fields[i].get(this)).append("'\n");
	//
	// } catch (IllegalAccessException ex) {
	// ex.printStackTrace();
	// } catch (IllegalArgumentException ex) {
	// ex.printStackTrace();
	// }
	//
	// }
	// return buff.toString();
	//
	// }

	// @Override
	// public void undo() {
	// // TODO Auto-generated method stub
	// System.out.println("UNDO: "+getCap());
	// }

	// public String showAll() {
	// return "Member [id=" + id + ", cap=" + cap + ", firstName=" + firstName +
	// ", lastName=" + lastName + ", nickName="
	// + nickName + ", email1=" + email1 + ", email2=" + email2 + ", phone=" +
	// phone + ", contactId=" + contactId
	// + ", notes=" + notes + ", payment=" + payment + ", updated=" + updated +
	// ", active=" + active + "]";
	// }

}
