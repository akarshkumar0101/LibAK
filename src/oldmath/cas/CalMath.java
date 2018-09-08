package oldmath.cas;

public class CalMath {

	public static double getValue(String inp) {
		inp = inp.replaceAll(" ", "");
		inp = "(" + inp + ")";
		Phrase p = null;
		try {
			p = createPhrase(inp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p.getValue();
	}

	public static Phrase createPhrase(String inp) throws Exception {
		String temp = inp.replace("e", Math.E + "");
		temp = temp.replace("π", Math.PI + "");
		temp = temp.replace("pi", Math.PI + "");
		Phrase p = new Phrase("(" + temp + ")");
		p.originalString = inp;
		return p;
	}

}
