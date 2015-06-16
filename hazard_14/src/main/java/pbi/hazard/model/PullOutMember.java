package pbi.hazard.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

// Many2Many implementation
// http://www.zabada.com/tutorials/hibernate-and-jpa-with-spring-example.php

@Entity
public class PullOutMember implements Serializable {
	private Integer id;
	private PullOut pullOut;
	private Member member;
	private Date pulledOut; // Boolean
	private Boolean onsite;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

//	@ ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@ManyToOne(optional = false)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void setPullOut(PullOut pullOut) {
		this.pullOut = pullOut;
	}

//	@ ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@ManyToOne(optional = false)
	public PullOut getPullOut() {
		return pullOut;
	}

	@Column(nullable=false)
	public Boolean getOnsite() {
		return onsite;
	}

	public void setOnsite(Boolean onsite) {
		this.onsite = onsite;
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
	public PullOutMember() {
		// TODO Auto-generated constructor stub
//		System.out.println("Constructor: "+this);
}

	/**
	 * @param pullOut
	 * @param member
	 */
	public PullOutMember(PullOut pullOut, Member member) {
//		super();
		this.pullOut = pullOut;
		this.member = member;
		setOnsite(false);
		setPulledOut(null);
	}

	@Override
	public String toString() {
		return "PullOutMember [id=" + id + ", pullOut=" + pullOut + ", member=" + member + ", pulledOut=" + pulledOut
				+ ", onsite=" + onsite + "]";
	}

}
