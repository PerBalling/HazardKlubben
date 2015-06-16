package pbi.hazard;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pbi.hazard.common.Ascii;
import pbi.hazard.common.DateTime;
import pbi.hazard.model.PullOut;

/**
 * Simple test using the WicketTester
 */
public class Misc extends WicketTestBase {

	@Before
	public void init() {
		super.init();
	}

	@Test
	public void AsciiConvert() {
		String number = "0123456789";
		String lower = "abcdefghijklmnopqrstuvwxyz";
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String chars = " -_";
		String speciel = "ÆØÅæøå";
		String err = "öõéá½";
		String res;

		Ascii asc = new Ascii();
		res = asc.plainAscii(number);
		assertEquals(res, number);

		res = asc.plainAscii(lower);
		assertEquals(res, lower);

		res = asc.plainAscii(upper);
		assertEquals(res, upper);

		res = asc.plainAscii(chars);
		// System.out.println(chars + "\t" + res);
		assertEquals(res, chars);

		res = asc.plainAscii(speciel);
		// System.out.println(speciel + "\t" + res);
		assertEquals(res, "EOAeoa");

		res = asc.plainAscii(err);
		assertEquals(res, "_____");

	}

	@Test
	public void SysOutData1() {
		ReportOnsitePage rop = new ReportOnsitePage();
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, -1);
		List<PullOut> listPullOut = pullOutDAO.getPullOutWinner(c1.getTime(), new Date());
		rop.SysOutData(listPullOut);
	}

	@Test
	public void SysOutData2() {
		ReportOnsitePage rop = new ReportOnsitePage();
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 1);
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.DATE, 1);
		List<PullOut> listPullOut = null;
		rop.SysOutData(listPullOut);
	}

	@Test
	public void GetOS() {
		ClosePage clp = new ClosePage();
		clp.getOsName();
	}

}
