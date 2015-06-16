package pbi.hazard.common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import pbi.hazard.WicketApplication;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

public class CsvFile {
	private static Logger logger = WicketApplication.getLogger();
	private final String path = "db/";
	private final String fileExt = ".csv"; // .txt

	public void CsvWinner(List<PullOut> pullOutList, PullOutDAO pullOutDAO) {
		// logger.info(pullOutList);
		// System.out.println("### CsvWinner:");
		// System.out.println(pullOutList);
		// System.out.println("### "+pullOutList.size());
		FileWriter csvFile = createCsv("vindere" + fileExt);

		String outputLine = new String();
		outputLine = "Navn\tStart\tStop\tNr\tVinder\tSidst vundet\tNote\n";
		saveToCsv(csvFile, outputLine);

		for (Iterator<PullOut> iterator = pullOutList.iterator(); iterator.hasNext();) {
			PullOut pullOut = (PullOut) iterator.next();
			outputLine = pullOut.getName();
			outputLine += "\t";
			outputLine += DateTime.formatDateToCsv(pullOut.getStart());
			outputLine += "\t";
			outputLine += DateTime.formatDateToCsv(pullOut.getStop());
			outputLine += "\t";
			if (pullOut.getWinner() == null) {
				outputLine += "\t";
				outputLine += "\t";
			} else {
				outputLine += pullOut.getWinner().getCap();
				outputLine += "\t";
				outputLine += pullOut.getWinner().returnName();
				outputLine += "\t";

				// Last won
				PullOut lastWon = pullOutDAO.findPreviousWon(pullOut.getWinner(), pullOut.getStop());
				// System.out.println(lastWon);
				outputLine += (lastWon == null ? "" : DateTime.formatDateToCsv(lastWon.getStop()));

				// List<PullOut> Wins = pullOutDAO.findWhenWon(pullOut.getWinner());
				// // System.out.println(Wins.size());
				// if (Wins.size() > 0) {
				// // System.out.println(Wins.get(0));
				// Date lastWon = null;
				// for (Iterator<PullOut> iter2 = Wins.iterator(); iter2.hasNext();) {
				// PullOut po = (PullOut) iter2.next();
				// if (lastWon == null && po.getStop().before(pullOut.getStop())) {
				// lastWon = po.getStop();
				// }
				// }
				// if (lastWon != null) {
				// outputLine += DateTime.formatDateToCsv(lastWon);
				// // System.out.println(lastWon);
				// }
				// }
			}
			outputLine += "\t";

			outputLine += (pullOut.getNote() == null ? "" : pullOut.getNote());
			outputLine += "\n";
			saveToCsv(csvFile, outputLine);
		}

		closeCsv(csvFile);
	}

	public void CsvMembers(List<Member> members) {
		// logger.info(activeMembers);
		FileWriter csvFile = createCsv("medlemmer" + fileExt);

		String outputLine = new String();
		outputLine = "Nr\tNavn\tEmail1\tEmail2\tTlf.\tKontaktId\tBetaling\tAktiv\tOpdateret\n";
		saveToCsv(csvFile, outputLine);

		for (Iterator<Member> iterator = members.iterator(); iterator.hasNext();) {
			Member member = (Member) iterator.next();
			outputLine = String.valueOf(member.getCap());
			outputLine += "\t";
			outputLine += member.returnName();
			outputLine += "\t";
			outputLine += (member.getEmail1() == null ? "" : member.getEmail1());
			outputLine += "\t";
			outputLine += (member.getEmail2() == null ? "" : member.getEmail2());
			outputLine += "\t";
			outputLine += (member.getPhone() == null ? "" : member.getPhone());
			outputLine += "\t";
			outputLine += (member.getContactId() == null ? "" : member.getContactId());
			outputLine += "\t";
			outputLine += (member.getPayment() == null ? "" : member.getPayment());
			outputLine += "\t";
			outputLine += member.getActive();
			outputLine += "\t";
			outputLine += DateTime.formatDateToCsv(member.getUpdated());
			outputLine += "\n";
			saveToCsv(csvFile, outputLine);
		}

		closeCsv(csvFile);
	}

	public void CsvPullList(List<PullOutMember> pomList, PullOut pullOut) {
		String fileName = DateTime.formatDateToSort(pullOut.getStart()) + " " + pullOut.getName();
		if (pullOut.getNote() != null && !pullOut.getNote().isEmpty()) {
			fileName += " - " + pullOut.getNote();
		}
		Ascii asc = new Ascii();
		fileName = asc.plainAscii(fileName);
		fileName += fileExt;
		FileWriter csvFile = createCsv(fileName);

		String outputLine = new String();
		outputLine = "Nr\tNavn\tTrukket\n";
		saveToCsv(csvFile, outputLine);

		int pullOutNum = 1;
		for (Iterator<PullOutMember> iterator = pomList.iterator(); iterator.hasNext();) {
			PullOutMember pom = (PullOutMember) iterator.next();
			outputLine = String.valueOf(pom.getMember().getCap());
			outputLine += "\t";
			outputLine += pom.getMember().returnName();
			outputLine += "\t";
			outputLine += (pom.getPulledOut() == null ? "" : String.valueOf(pullOutNum++));
			outputLine += "\n";
			saveToCsv(csvFile, outputLine);
		}

		closeCsv(csvFile);
	}

	private FileWriter createCsv(String name) {
		FileWriter writer;

		try {
			writer = new FileWriter(path + name);
		} catch (IOException e) {
			// e.printStackTrace();
			System.err.println(e.getMessage());
			logger.error(e.getMessage());
			writer = null;
		}
		return writer;
	}

	private void saveToCsv(FileWriter writer, String line) {
		// System.out.print(line);
		if (writer == null) {
			logger.error("null: " + line);
		} else {
			try {
				writer.append(line);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void closeCsv(FileWriter writer) {
		if (writer == null) {
			logger.error("null: closeCsv");
		} else {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
