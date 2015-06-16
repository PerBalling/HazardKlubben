package pbi.hazard.model;

import java.io.Serializable;
import java.util.List;

public class OnsitePlaceHolder implements Serializable {
	private static final long serialVersionUID = 1L;

	// private Date pullOutDay;
	private List<PullOut> pullOuts;
	private List<Member> onsiteMembers;
	// private PullOut actualPullOut;
	// private Boolean header;
	private int pullOutDateIdx;
	private int pullOutIdx;

	// public OnsitePlaceHolder(Date pullOutDay, List<PullOut> pullOuts,
	// List<Member> onsiteMembers) {
	// super();
	// this.pullOutDay = pullOutDay;
	// this.pullOuts = pullOuts;
	// this.onsiteMembers = onsiteMembers;
	// }

	// public List<PullOut> getPullOuts() {
	// return pullOuts;
	// }

	public void setPullOuts(List<PullOut> pullOuts) {
		this.pullOuts = pullOuts;
		pullOutDateIdx = 0;
		pullOutIdx = 0;
	}

	public List<Member> getOnsiteMembers() {
		return onsiteMembers;
	}

	public void setOnsiteMembers(List<Member> onsiteMembers) {
		this.onsiteMembers = onsiteMembers;
	}

	public PullOut getActualPullOut(int idx) {
		// System.out.println("# "+idx+" #");
		if (pullOuts.size() > 0 && idx < pullOuts.size()) {
			return pullOuts.get(idx);
		} else {
			return null;
		}
	}

	public PullOut getActualDayPullOut() {
		return getActualPullOut(pullOutDateIdx);
	}

	public PullOut getActualPullOut() {
		return getActualPullOut(pullOutIdx);
	}

	public PullOut getNextDayPullOut() {
		// System.out.println("D="+pullOutDateIdx);
		return getActualPullOut(++pullOutDateIdx);
	}

	// public boolean hasMoreDays() {
	// return pullOutDateIdx < pullOuts.size();
	// }

	public PullOut getNextPullOut() {
		// System.out.println("P="+pullOutIdx);
		return getActualPullOut(++pullOutIdx);
	}

	// public boolean hasMorePullOuts() {
	// return pullOutIdx < pullOuts.size();
	// }

	// public void setHeader(Boolean header) {
	// this.header = header;
	// }
	//
	// public Boolean getHeader() {
	// return header;
	// }
}
