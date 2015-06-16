package pbi.hazard.archive;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import pbi.hazard.model.Member;

@Entity
public class APullOutMember implements Serializable {
	private static final long serialVersionUID = 534839282646316007L;
	private Integer id;
	private Integer memberId;
//	private Member member;
	private Boolean onsite;
	private Integer pullOutId;
	private Date pulledOut;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	@Column(nullable = false)
	public Boolean getOnsite() {
		return onsite;
	}

	public void setOnsite(Boolean onsite) {
		this.onsite = onsite;
	}

	public Integer getPullOutId() {
		return pullOutId;
	}

	public void setPullOutId(Integer pullOutId) {
		this.pullOutId = pullOutId;
	}

	public Date getPulledOut() {
		return pulledOut;
	}

	public void setPulledOut(Date pulledOut) {
		this.pulledOut = pulledOut;
	}

	/**
	 * 
	 */
	// public APullOutMember() {
	// TODO Auto-generated constructor stub
	// System.out.println("Constructor: "+this);
	// }

	/**
	 * @param memberId
	 * @param onsite
	 * @param pullOutId
	 * @param pulledOut
	 */
	public APullOutMember(Integer memberId, Boolean onsite, Integer pullOutId, Date pulledOut) {
		this.memberId = memberId;
		this.onsite = onsite;
		this.pullOutId = pullOutId;
		this.pulledOut = pulledOut;
	}

	@Override
	public String toString() {
		return "ArchivePullOutMember [id=" + id + ", pullOutId=" + pullOutId + ", pulledOut=" + pulledOut + ", memberId="
				+ memberId + ", onsite=" + onsite + "]";
	}

//	@ManyToOne(optional = false)
//	@JoinColumn(name="pbi.hazard.model.Member")
//	public Member getMember() {
//		return member;
//	}
//
//	public void setMember(Member member) {
//		this.member = member;
//	}

}
