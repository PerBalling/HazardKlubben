package pbi.hazard;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import com.visural.wicket.behavior.beautytips.BeautyTipBehavior;

import pbi.hazard.common.CsvFile;
import pbi.hazard.common.DateTime;
import pbi.hazard.common.DefaultFocusBehavior;
import pbi.hazard.common.StaticImage;
import pbi.hazard.dao.MemberDAO;
import pbi.hazard.dao.PullOutDAO;
import pbi.hazard.dao.PullOutMemberDAO;
import pbi.hazard.model.Member;
import pbi.hazard.model.PullOut;
import pbi.hazard.model.PullOutMember;

/**
 * 
 * CLEAN UP
 * 
 */

/**
 * PullOut
 */
public class PullOutPage extends WebPage {
	private static Logger logger = WicketApplication.getLogger();
	private static final String ONSITE_PIC = "pic/Onsite_Green.gif";
	private static final String NOTONSITE_PIC = "pic/Onsite_Gray.gif";
	private static final String LASTONSITE_PIC = "pic/Onsite_Yellow.gif";
	private static final String ONSITETEXT = "Tilstede";
	private static final String NOTONSITETEXT = "Fraværende";
	private static final String PULLEDTEXT = "Trukket";
	private static final String NOTPULLEDTEXT = "Med igen";
	private static final String LASTMEMBERTEXT = "Sidste medlem";
	private static final int COLHEIGHT = 16;// 14;
	private static final int INFOROWS = 6;
	private int MinCap = Integer.MAX_VALUE;
	private int MaxCap = 0;
	private List<PullOutMember> pullOutMembers = new ArrayList<PullOutMember>();
	private int pulledCount = 0;
	private int onsiteCount = 0;
	private int onsitePulledCount = 0;
	// private Boolean lastOnsite = Boolean.FALSE;
	final Input input = new Input();

	@SpringBean
	private PullOutMemberDAO pullOutMemberDAO;
	@SpringBean
	private PullOutDAO pullOutDAO;
	@SpringBean
	private MemberDAO memberDAO;

	FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");

	private Member memberEntry = new Member();
	private PullOut thisPullOut; // Used in sub functions within this file
	private PullOutMember latestPom;

	// private ModalWindow OkCancel = null; // TODO: Remove modal

	public PullOutPage(PullOut pullOut) {
		// logger.info("NEW PullOutPage");
		thisPullOut = pullOut;
		GetData(thisPullOut);

		// SaveOutput(); // DO NOT: Save data also when starting the page
		// Page is loaded with each pull

		if (pulledCount == pullOutMembers.size()) {
			// Mark winner
			latestPom = pullOutMemberDAO.findLatest(thisPullOut);
		} else {
			// latestPom = new PullOutMember();
			latestPom = null;
		}

		// Last onsite pulled out
		// System.out.println("onsiteCount=" + onsiteCount);
		// System.out.println("onsitePulledCount=" + onsitePulledCount);
		// if (onsiteCount > 0 && onsiteCount == onsitePulledCount) {
		if (onsiteCount > 0) {
			// PullOutMember latestPom =
			// pullOutMemberDAO.findLastOnsitePulled(thisPullOut, (pulledCount ==
			// pullOutMembers.size()?1:0));
			if (pulledCount < pullOutMembers.size()) {
				// Don not set last onsite for the very last pull
				PullOutMember latestOnsite = pullOutMemberDAO.findLastOnsitePulled(thisPullOut, 0);
				try {
					// Member onsite = latestOnsite.getMember();
					// if (onsite.getActive()) {
					// WicketApplication.setLastOnsitePulled(onsite.getId());
					// }
					WicketApplication.setLastOnsitePulled(latestOnsite.getMember().getId());
				} catch (Exception e) {
					// latestOnsite is Null
					WicketApplication.setLastOnsitePulled(0);
				}
			}
		} else {
			// If undo last onsite
			WicketApplication.setLastOnsitePulled(0);
		}

		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);

		Label lblStart = new Label("pullinfo", DateTime.formatDate(thisPullOut.getStart()));
		lblStart.setOutputMarkupId(true);
		add(lblStart);

		Label lblId = new Label("pullOut", thisPullOut.getName());
		add(lblId);

		Label lblTotalCount = new Label("pulled", pullCountLabel(pullOutMembers.size(), pulledCount));
		add(lblTotalCount);

		Label lblOnsiteLabel = new Label("onsitelabel", ONSITETEXT + ":"); // 20121006:
																																				// HTML
																																				// marked
																																				// as
																																				// onsitelabelx?
		add(lblOnsiteLabel);
		Label lblPulledCount = new Label("onsitepulled", pullCountLabel(onsiteCount, onsitePulledCount));
		add(lblPulledCount);

		final CompoundPropertyModel<Member> inputModel = new CompoundPropertyModel<Member>(memberEntry);
		final CompoundPropertyModel<PullOut> poModel = new CompoundPropertyModel<PullOut>(thisPullOut);

		// Prepare modal window - Activated by java (or a link)
		// HTML page: <div wicket:id="okcancel"></div>
		/*
		 * OkCancel = new OkCancelModalWindow("okcancel") { public void
		 * onSelect(AjaxRequestTarget target, String selection) { // Handle Select
		 * action // resultLabel.setModelObject(selection); //
		 * target.addComponent(resultLabel); System.out.println("onSelect: " +
		 * selection); close(target); }
		 * 
		 * public void onCancel(AjaxRequestTarget target) { // Handle Cancel action
		 * // resultLabel.setModelObject("ModalWindow cancelled."); //
		 * target.addComponent(resultLabel); System.out.println("onCancel.");
		 * close(target); } }; add(OkCancel);
		 */
		Form frmReturn = new Form("returnform") {
			{
				Button btnReturn = new Button("pulloutlist");
				btnReturn.add(new AjaxEventBehavior("onclick") {

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						SaveOutput();
						setResponsePage(new PullOutListPage());
					}
				});
				add(btnReturn);
				if (pulledCount == pullOutMembers.size()) { // Winner
					btnReturn.add(new SimpleAttributeModifier("disabled", "disabled"));
				}
			}
		};
		add(frmReturn);

		Form<Member> pullform = new Form<Member>("pullform", inputModel) {
			{
				TextField<Integer> txtPullCap = new TextField<Integer>("pull_cap", inputModel.<Integer> bind("cap"));
				txtPullCap.setLabel(new Model<String>("Kapsel"));
				add(txtPullCap);
				if (pulledCount != pullOutMembers.size()) { // More members
					txtPullCap.setRequired(true);
					txtPullCap.add(new RangeValidator<Integer>(MinCap, MaxCap));
					txtPullCap.add(new DefaultFocusBehavior());

					// btnFinished.add(new SimpleAttributeModifier("disabled",
					// "disabled"));
				}
			}

			@Override
			protected void onSubmit() {
				// logger.info(">> onSubmit PULL >> " + thisPullOut);
				handlePull(memberEntry.getCap());
				// handlePull(memberEntry.getCap(), AjaxRequestTarget.get());
				// logger.info(">> onSubmit PULL >> DONE");
			}
		};
		add(pullform);

		// final CompoundPropertyModel<Boolean> skipModel = new
		// CompoundPropertyModel<Boolean>(skipPullOut);
		final PropertyModel<Boolean> skipModel = new PropertyModel<Boolean>(input, "bool");

		// https://cwiki.apache.org/WICKET/listview-with-checkboxes.html
		Form<Member> frmFinish = new Form<Member>("finishform", inputModel) {
			{

				TextField<String> txtNote = new TextField<String>("note", poModel.<String> bind("note"));
				add(txtNote);
				CheckBox chkSkip = new CheckBox("skip", skipModel);
				add(chkSkip);
				Button btnClose = new Button("close");
				// btnClose.add(new AjaxEventBehavior("onClick") {
				//
				// @Override
				// protected void onEvent(AjaxRequestTarget target) {
				// // setResponsePage(new PullOutListPage());
				// }
				// });
				add(btnClose);

				if (pulledCount == pullOutMembers.size()) { // Winner
					txtNote.add(new DefaultFocusBehavior());
					btnClose.add(new SimpleAttributeModifier("class", "stopbutton"));
					// btnReturn.add(new SimpleAttributeModifier("disabled", "disabled"));
				} // else {
					// btnFinished.add(new SimpleAttributeModifier("disabled",
					// "disabled"));
				// }
			}

			@Override
			protected void onSubmit() {
				// logger.info(">> onSubmit Finish >> Start");
				finishedPullOut();
				// logger.info(">> onSubmit Finish >> Done");
			}
		};
		add(frmFinish);

		// logger.info("ListView cols Start");
		ListView<List<PullOutMember>> tableCols = new ListView<List<PullOutMember>>("cols", listOfList(pullOutMembers)) {

			@Override
			protected void populateItem(ListItem<List<PullOutMember>> colItem) {
				// logger.info("ListView rows Start");
				ListView<PullOutMember> rows = new ListView<PullOutMember>("rows", colItem.getModelObject()) {

					@Override
					protected void populateItem(ListItem<PullOutMember> listRow) {
						final PullOutMember pom = listRow.getModelObject();
						final Member m = pom.getMember();

						Label lblCap = new Label("cap", String.valueOf(m.getCap()));
						lblCap.add(new AjaxEventBehavior("onclick") {
							@Override
							protected void onEvent(AjaxRequestTarget target) {
								// target.appendJavascript("alert ('" + jsAlert(m) + "'); ");
								handlePull(m.getCap());
							}

						});
						// http://wicket.visural.net/examples/tooltips
						lblCap.add(new BeautyTipBehavior(pullOutDAO.toolTip(m)));
						listRow.add(lblCap);

						Label lblName = new Label("name", m.returnName());
						lblName.add(new AjaxEventBehavior("onclick") {

							@Override
							protected void onEvent(AjaxRequestTarget target) {

								// memberEntry.setCap(999);
								// inputModel.setObject(memberEntry);
								// PullOutPage.this.get(pullform.getPath()).get;
								// SubmitLink.this.
								// pullform.findSubmittingButton().

								// logger.info(">> onClick PULL >> " + m);
								handlePull(m.getCap()); // /////////////////////// call submit
																				// form instead
								// logger.info(">> onClick PULL >> DONE");

							}
						});

						if (pom.getPulledOut() == null) { // Not pulled out
							if (pom.getOnsite()) {
								lblCap.add(new SimpleAttributeModifier("class", "onsite"));
								lblName.add(new SimpleAttributeModifier("class", "onsite"));
							} else {
								if (pom.getMember().returnName().toLowerCase().contains("blanko")) {
									lblCap.add(new SimpleAttributeModifier("class", "blanko"));
									lblName.add(new SimpleAttributeModifier("class", "blanko"));
								} else {
									lblCap.add(new SimpleAttributeModifier("class", "inbag"));
									lblName.add(new SimpleAttributeModifier("class", "inbag"));
								}
							}
						} else { // Pulled out
							if (latestPom != null && m.getId().equals(latestPom.getMember().getId())) {
								lblCap.add(new SimpleAttributeModifier("class", "winner"));
								lblName.add(new SimpleAttributeModifier("class", "winner"));
							} else {
								lblCap.add(new SimpleAttributeModifier("class", "taken"));
								lblName.add(new SimpleAttributeModifier("class", "taken"));
							}
						}
						// logger.info("add lblName");
						listRow.add(lblName);

						// if (pom.getOnsite()) {
						// countOnsite++;
						// System.out.println(pom.getMember().returnName());
						// }
						// System.out.println(onsiteCount + " " +
						// onsitePulledCount+" "+countOnsite);

						if (pom.getMember().getCap().compareTo(WicketApplication.getLastPulled()) == 0 && pom.getOnsite()
								&& onsiteCount == onsitePulledCount) {
							// System.out.println("LAST ONSITE: " +
							// pom.getMember().returnName());
							// lastOnsite=Boolean.TRUE;
							// WicketApplication.setLastOnsitePulled(pom.getMember().getCap());
						}

						Model<String> modelOnsite = new Model<String>(
								pom.getOnsite() ? (pom.getMember().getId() == WicketApplication.getLastOnsitePulled() ? LASTONSITE_PIC
										: ONSITE_PIC) : NOTONSITE_PIC);
						StaticImage imgOnsite = new StaticImage("onsite", modelOnsite);
						imgOnsite.add(new SimpleAttributeModifier("alt", (pom.getOnsite() ? ONSITETEXT : NOTONSITETEXT)));
						imgOnsite.add(new SimpleAttributeModifier("title", (pom.getOnsite() ? ONSITETEXT : NOTONSITETEXT)));
						imgOnsite.add(new AjaxEventBehavior("onclick") {
							private static final long serialVersionUID = 1L;

							@Override
							protected void onEvent(AjaxRequestTarget target) {
								changeOnsite(pom);
							}
						});
						// logger.info("add imgOnsite");
						listRow.add(imgOnsite);
					}
				};

				colItem.add(rows);
			}

		};
		add(tableCols);

		// logger.info("ListView inforows");
		add(new ListView<String>("inforows", WicketApplication.getInfoLog()) {
			private static final long serialVersionUID = 1L;

			public void populateItem(ListItem<String> item) {
				String info = item.getModelObject();
				item.add(new Label("log", info));
			}
		});

		if (WicketApplication.getInfoLog().size() == 0) {
			refillInfoLog();
			setLastPulled();
		}

	}

	private void SaveOutput() {
		// System.out.println("Generate output files!");

		// Only save when a winner is found
		if (pulledCount == pullOutMembers.size()) {
			CsvFile csvFile = new CsvFile(); // Avoid static error

			// Winner
			List<PullOut> pullOutList = pullOutDAO.getPullOut();
			// logger.info(pullOut);
			csvFile.CsvWinner(pullOutList, pullOutDAO);

			// Members
			List<Member> activeMembers = memberDAO.getMembers();	//.getByActive(Boolean.TRUE);
			// System.out.println(activeMembers);
			csvFile.CsvMembers(activeMembers);

			// Pull List
			List<PullOutMember> pomList = pullOutMemberDAO.getPullOutMemberList(thisPullOut, 2);
			if (pulledCount > 0) { // Only save when pullout has been started
				csvFile.CsvPullList(pomList, thisPullOut);
			}
		}
	}

	/*
	 * Build PullOut table
	 */
	private List<List<PullOutMember>> listOfList(List<PullOutMember> pullOutMembers) {
		// logger.info("listOfList Start");
		List<List<PullOutMember>> cols = new ArrayList<List<PullOutMember>>();

		Iterator<PullOutMember> iterator = pullOutMembers.iterator();
		List<PullOutMember> currentList = new ArrayList<PullOutMember>();
		cols.add(currentList);
		while (iterator.hasNext()) {
			PullOutMember pom = iterator.next();
			currentList.add(pom);
			if ((currentList.size() >= COLHEIGHT) && iterator.hasNext()) {
				// Next col
				currentList = new ArrayList<PullOutMember>();
				cols.add(currentList);
			}
		}

		// logger.info("listOfList Done");
		return cols;
	}

	/*
	 * Get data for this PullOut Return data to var: pullOutMembers
	 */
	private void GetData(PullOut pullOutData) {
		/*
		 * LoadableDetachableModel<List<PullOutMember>> model = new
		 * LoadableDetachableModel<List<PullOutMember>>() {
		 * 
		 * @Override protected List<PullOutMember> load() { return
		 * pullOutMemberDAO.getPullOutMemberList(pullOut); } };
		 */
		pullOutMembers = pullOutMemberDAO.getPullOutMemberList(pullOutData, 1);

		// Update local variables with data
		Member member;
		for (Iterator<PullOutMember> iterator = pullOutMembers.iterator(); iterator.hasNext();) {
			PullOutMember pom = (PullOutMember) iterator.next();
			member = pom.getMember();
			MinCap = Math.min(MinCap, member.getCap());
			MaxCap = Math.max(MaxCap, member.getCap());
			if (pom.getPulledOut() != null) {
				pulledCount++;
			}
			if (pom.getOnsite()) {
				onsiteCount++;
				if (pom.getPulledOut() != null) {
					onsitePulledCount++;
				}
			}
		}
	}

	private void handlePull(Integer cap) {
		String infoLogLine;
		// boolean goOn = true;

		// logger.info("handlePull Start");
		PullOutMember pulledOut = new PullOutMember();
		Member thisMember = new Member();
		for (Iterator<PullOutMember> iterator = pullOutMembers.iterator(); iterator.hasNext();) {
			PullOutMember pom = (PullOutMember) iterator.next();
			Member m = pom.getMember();
			if (cap.compareTo(m.getCap()) == 0) { // Found in list

				// System.out.println("handlePull - found " + m.getCap());
				// System.out.println("handlePull - last " +
				// WicketApplication.getLastPulled());
				if ((m.getCap().compareTo(WicketApplication.getLastPulled()) != 0) && pom.getPulledOut() != null) {
					// Undo last pullout - only if it is the last pulled

					// System.out.println("handlePull - not eq getLastPulled");
					// feedbackPanel.info is only working when form is submitted - I
					// think!
					feedbackPanel.info("Kun den seneste udtrukne (" + WicketApplication.getLastPulled() + ") kan sættes '"
							+ NOTPULLEDTEXT + "'.");
					// feedbackPanel.

					// AjaxRequestTarget.get(xx).appendJavascript("alert('if" +
					// " else');");

					// OkCancel.show(target);
					// OkCancel.show();

					// final ModalWindow okCancel;
					// add(okCancel = new ModalWindow("okCancel"));
					// goOn = false;

					// feedbackPanel.render(); // Fails
				} else {
					thisMember = m;
					pulledOut = pom;

					WicketApplication.setLastPulled(m.getCap());
					// WicketApplication.setLastOnsitePulled(0);

					// Toggle PulledOut
					pulledOut.setPulledOut((pulledOut.getPulledOut() == null) ? new Date() : null);
					// Save to DB
					pullOutMemberDAO.updatePullOutMember(pulledOut);
					setLastPulled();

					if (pulledCount + 1 == pullOutMembers.size()) {
						// Winner
						PullOutMember latestOnsite = null;
						if (pulledOut.getOnsite()) {
							// Member lastOnsitePulled = new Member();
							// lastOnsitePulled.setCap(WicketApplication.getLastOnsitePulled());
							// System.out.println(memberDAO.getFilteredMembers(lastOnsitePulled));
							latestOnsite = pullOutMemberDAO.findLastOnsitePulled(thisPullOut, 1);
							// System.out.println("latestOnsite1: " + latestOnsite);
							// System.out.println(latestOnsite.getMember());
							if (latestOnsite != null) {
								// System.out.println(latestOnsite.getMember());
								infoLogLine = LASTMEMBERTEXT + " " + ONSITETEXT;
								updateInfoLog(latestOnsite, infoLogLine.toUpperCase());
							}

							infoLogLine = (pulledOut.getPulledOut() != null) ? PULLEDTEXT + ", VINDER" : NOTPULLEDTEXT;
						} else {
							latestOnsite = pullOutMemberDAO.findLastOnsitePulled(thisPullOut, 0);
							// System.out.println("latestOnsite2: " + latestOnsite);
							// System.out.println(latestOnsite.getMember());
							if (latestOnsite != null) {
								// System.out.println(latestOnsite.getMember());
								infoLogLine = LASTMEMBERTEXT + " " + ONSITETEXT;
								updateInfoLog(latestOnsite, infoLogLine.toUpperCase());
							}
							infoLogLine = (pulledOut.getPulledOut() != null) ? PULLEDTEXT + ", VINDER" : NOTPULLEDTEXT;
						}
					} else {
						// Also when if undoing winner!
						if (pulledOut.getOnsite()) {
							infoLogLine = (pulledOut.getPulledOut() != null) ? PULLEDTEXT + ", "
									+ (onsiteCount == (onsitePulledCount + 1) ? LASTMEMBERTEXT + " " + ONSITETEXT : ONSITETEXT)
									: NOTPULLEDTEXT;
						} else {
							infoLogLine = (pulledOut.getPulledOut() != null) ? PULLEDTEXT : NOTPULLEDTEXT;
						}
					}

					// infoLogLine = (pulledOut.getPulledOut() != null) ? PULLEDTEXT
					// + (pulledOut.getOnsite() ? ", "
					// + (onsiteCount == (onsitePulledCount + 1) ? "Sidste medlem " +
					// ONSITETEXT : ONSITETEXT) : "")
					// + (pulledCount + 1 == pullOutMembers.size() ? ", VINDER" : "") :
					// NOTPULLEDTEXT;
					// System.out.println(onsiteCount + " " + onsitePulledCount);

					if (pulledOut.getPulledOut() != null && pulledOut.getOnsite()) {
						infoLogLine = infoLogLine.toUpperCase();
						// if (onsiteCount == (onsitePulledCount + 1)) {
						// WicketApplication.setLastOnsitePulled(m.getCap());
						// }
					}
					updateInfoLog(pulledOut, infoLogLine);

					if (pulledCount == 0) {
						// Set start date to time of first pullout
						thisPullOut.setStart(new Date());
						pullOutDAO.savePullOut(thisPullOut);
					}
				}
				break; // for loop
			}
		}

		if (feedbackPanel.anyMessage()) {
			// System.out.println("feedbackPanel.anyMessage");
			// feedbackPanel.render();
			// PullOutPage.this.renderComponent();
			// PullOutPage.this.dirty();
			// feedbackPanel.renderComponent();
			// setResponsePage(new PullOutPage(thisPullOut));
			// throw new
			// setResponsePage(PullOutPage.class,thisPullOut);
		}

		if (thisMember.getId() == null && !feedbackPanel.anyMessage()) {
			feedbackPanel.info("Ikke fundet: " + cap);
		}

		// System.out.println("Feedback: "+feedbackPanel.anyMessage());
		// logger.info("handlePull Done");
		if (pulledOut.getId() != null) { // || feedbackPanel.anyMessage()
			// reload page with updated data
			// Ajax onEvent does not submit page
			setResponsePage(new PullOutPage(thisPullOut));
		}
	}

	/*
	 * 
	 */
	private void changeOnsite(PullOutMember pom) {
		pom.setOnsite(!pom.getOnsite());
		pullOutMemberDAO.updatePullOutMember(pom);

		// updateInfoLog(pom.getMember(), pom.getOnsite() ? ONSITETEXT :
		// NOTONSITETEXT);
		updateInfoLog(pom, pom.getOnsite() ? ONSITETEXT : NOTONSITETEXT);
		// reload page with updated data
		setResponsePage(new PullOutPage(thisPullOut));
	}

	/*
	 * 
	 */
	private String pullCountLabel(int total, int pulled) {
		return String.valueOf(total) + "-" + String.valueOf(pulled) + ":" + String.valueOf(total - pulled);
	}

	/*
	 * 
	 */
	// private void updateInfoLog(Member thisMember, String what) {
	private void updateInfoLog(PullOutMember thisPom, String what) {
		// logger.info("updateInfoLog Start");
		// DecimalFormat df = new DecimalFormat("000");
		// String info = (thisMember == null ? "" : df.format(thisMember.getCap()));
		String info = (thisPom == null ? "" : formatCap(thisPom.getMember().getCap()));
		info += spaceAdd((thisPom == null ? "" : thisPom.getMember().returnName()));
		info += spaceAdd(what);
		// if (thisPom != null && thisPom.getPulledOut() != null) {
		// info += spaceAdd(thisPom.getOnsite() ? "_" + ONSITETEXT + "_" : "");
		// }
		WicketApplication.updateInfoLog(info, INFOROWS);
		// logger.info("updateInfoLog Done");
	}

	/*
	 * 
	 */
	private String formatCap(Integer cap) {
		// Width=3, Add nbsp
		String strRet = cap.toString();
		switch (strRet.length()) {
		case 1:
			strRet = "__".concat(strRet);
			break;

		case 2:
			strRet = "_".concat(strRet); // escapeMarkup
			break;

		default:
			break;
		}
		return strRet;
	}

	/*
	 * When a winner is found, insert a note and close the PullOut
	 */
	private void finishedPullOut() {
		boolean go = true;

		// System.out.println("\nfinishedPullOut: " + go);
		if (pulledCount == pullOutMembers.size()) { // Winner found
			// System.out.println("Winner found");
		} else { // More members
			// System.out.println("More members");
			go = false;
			feedbackPanel.info("Afslut ikke - Der mangler: " + (pullOutMembers.size() - pulledCount));
		}
		// System.out.println("finishedPullOut: " + go);

		// System.out.println("Skip: "+skipPullOut);
		// System.out.println("Skip: " + input.isSet());

		// if (skipPullOut) {
		if (input.isSet()) {
			if (thisPullOut.getNote() != null) { // Skip with note
				// System.out.println("Skip with note");
				go = true;
			} else { // Missing note
				// System.out.println("Missing note");
				go = false;
				feedbackPanel.info("Afslut ikke - Note mangler");
			}
		}
		// System.out.println("finishedPullOut: " + go);

		if (go) {
			thisPullOut.setStop(new Date());
			// thisPullOut.setWinner(skipPullOut?null:latestPom.getMember());
			thisPullOut.setWinner(input.isSet() ? null : latestPom.getMember());
			/*
			 * if (thisPullOut.getWinner() == null) { // throw new
			 * NullPointerException("finishedPullOut");
			 * System.err.println("PullOutPage.finishedPullOut()\tWinner=NULL!"); }
			 */

			// Save to DB
			pullOutDAO.savePullOut(thisPullOut);
			// System.out.println("Last Onsite: " +
			// memberDAO.getMemberById(WicketApplication.getLastOnsitePulled()));
			// System.out.println("Winner: " + thisPullOut.getWinner());
			// Update 2 members with Last Onsite + Winner data
			// Better: New data model: Event: id, datetime, event_text

			// Generate output files after each PullOut
			SaveOutput();

			// Cleanup for next PullOut
			WicketApplication.initVars();

			setResponsePage(new PullOutListPage());
		}
	}

	/*
	 * Used when this page was closed but Pullout not Closed. If more than max
	 * (INFOROWS) then only the last will be shown
	 */
	private void refillInfoLog() {
		// logger.info("refillInfoLog Start");
		// First line = a timestamp
		updateInfoLog(null, DateTime.formatDate(new Date()));

		// logger.info("refillInfoLog Get list");
		// Get list from DB, ordered by pulled time
		List<PullOutMember> pomList = pullOutMemberDAO.getPullOutMemberList(thisPullOut, 2);
		// logger.info("refillInfoLog Get Done");
		// First add onsite info
		for (Iterator<PullOutMember> iterator = pomList.iterator(); iterator.hasNext();) {
			PullOutMember pullOutMember = (PullOutMember) iterator.next();
			if (pullOutMember.getOnsite()) {
				// updateInfoLog(pullOutMember.getMember(), ONSITETEXT);
				updateInfoLog(pullOutMember, ONSITETEXT);
			}
		}

		// logger.info("refillInfoLog pulledOut info");
		// Then add pulledOut info
		for (Iterator<PullOutMember> iterator = pomList.iterator(); iterator.hasNext();) {
			PullOutMember pullOutMember = (PullOutMember) iterator.next();
			if (pullOutMember.getPulledOut() != null) {
				// updateInfoLog(pullOutMember.getMember(), PULLED + " [" +
				// DateTime.formatDate(pullOutMember.getPulledOut())
				// + "]");
				updateInfoLog(
						pullOutMember,
						PULLEDTEXT + (pullOutMember.getOnsite() ? " " + ONSITETEXT : "") + " ["
								+ DateTime.formatDate(pullOutMember.getPulledOut()) + "]");
			}
		}
		// logger.info("refillInfoLog Done");
	}

	private void setLastPulled() {
		PullOutMember latestPom = pullOutMemberDAO.findLatest(thisPullOut);
		try {
			WicketApplication.setLastPulled(latestPom.getMember().getCap());
		} catch (Exception e) {
			WicketApplication.setLastPulled(0);
		}
		// System.out.println("setLastPulled: " +
		// WicketApplication.getLastPulled());
	}

	/*
	 * 
	 */
	private String spaceAdd(String str) {
		return " " + str;
	}

	/** Simple data class that acts as a model for the input fields. */
	private static class Input implements IClusterable {
		/** a boolean. */
		private Boolean bool = Boolean.FALSE;

		public Boolean isSet() {
			return bool;
		}

		public void setBool(Boolean bool) {
			this.bool = bool;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "bool = '" + bool + "'";
		}
	}

}
