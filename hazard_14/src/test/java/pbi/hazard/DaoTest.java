package pbi.hazard;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import pbi.hazard.model.Member;

/**
 * Simple test using the WicketTester
 */
// public class TestHomePage extends TestCase {
public class DaoTest extends WicketTestBase {

	@Before
	public void init() {
		super.init();
	}

	@Test
	public void MemberToString() {
		Member m = new Member();
		assertEquals(m.toString(), "Member [id=null, cap=null, firstName=, lastName=, active=null]");
		m = new Member(0, 0, "First", true);
		assertEquals(m.toString(), "Member [id=0, cap=0, firstName=First, lastName=, active=true]");
	}

//	@Test
//	public void PullOutGet() {
//		PullOut p = (PullOut) pullOutDao.getPullOut(0);
//}
}
