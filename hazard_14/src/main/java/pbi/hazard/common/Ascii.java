package pbi.hazard.common;

public class Ascii {

	/**
	 * 
	 */
	public Ascii() {
	}

	/**
	 * UTF-8 ?
	 * @param inp
	 * @return
	 */
	public String plainAscii(String inp) {
		final String validChars = " -0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
		final char def = '_';
		char c;
		int a, pos;
		String ret = "";

		for (int i = 0; i < inp.length(); i++) {
			c = inp.charAt(i);
			a = (int) c;
//			System.out.println("ASCII value of: " + c + " is:" + a);
			pos = validChars.indexOf(c);
//			System.out.println(pos);
			if (pos != -1) {
				a = -1;
			}
//			System.out.println(a);
			switch (a) {
			case -1: // Valid
				ret += c;
				break;

			case 198: // Æ 
				ret += 'E';
				break;

			case 216: // Ø 
				ret += 'O';
				break;

			case 197: // Å 
				ret += 'A';
				break;

			case 230: // æ 
				ret += 'e';
				break;

			case 248: // ø 
				ret += 'o';
				break;

			case 229: // å 
				ret += 'a';
				break;

			default:
//				System.err.println(c + " " + a);
				ret += def;
				break;
			}
//			System.out.println(ret);
		}

		return ret;
	}
}
