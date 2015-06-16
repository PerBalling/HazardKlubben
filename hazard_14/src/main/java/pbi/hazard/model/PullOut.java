package pbi.hazard.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class PullOut implements Serializable {
	private Integer id;
	private String name;
	// private Event start;
	private Date start;
	private Date stop;
	private String note;
	private Member winner;

	// private List<PullOutMember> pullOutMembers = new
	// ArrayList<PullOutMember>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/*
	 * // @OneToMany(mappedBy = "pullOut") // See:
	 * http://www.zabada.com/tutorials/hibernate-and-jpa-with-spring-example.php
	 * // Only one way mapping public List<PullOutMember> getPullOutMembers() {
	 * return pullOutMembers; }
	 * 
	 * public void setPullOutMembers(List<PullOutMember> pullOutMembers) {
	 * this.pullOutMembers = pullOutMembers; }
	 */

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param start
	 *          the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public Date getStop() {
		return stop;
	}

	/**
	 * @param stop
	 *          the stop to set
	 */
	public void setStop(Date stop) {
		this.stop = stop;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@ManyToOne(optional = true)// , cascade = CascadeType.REFRESH)
	public Member getWinner() {
		return winner;
	}

	public void setWinner(Member winner) {
		this.winner = winner;
	}

	// TODO Auto-generated constructor stub
	public PullOut() {
		super();
		// System.out.println("Constructor: "+this);
	}

	@Override
	public String toString() {
		return "PullOut [id=" + id + ", name=" + name + ", start=" + start + ", stop=" + stop + ", note=" + note
				+ ", winner=" + winner + "]";
	}

	/*
	 * private String className() { String name = this.getClass().getName(); try {
	 * name = name.substring(name.lastIndexOf('.') + 1); } catch (Exception e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } return name; }
	 */
}
