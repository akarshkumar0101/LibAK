package math;

import java.util.ArrayList;
import java.util.List;

public class Domain {
	private final List<Range> parts;

	public Domain() {
		parts = new ArrayList<Range>();
	}

	public Domain(Range range) {
		this();
		addRange(range);
	}

	public void addRange(Range range) {
		parts.add(range);
	}

	public boolean overlaps(Domain another) {
		for (Range r1 : parts) {
			for (Range r2 : another.parts) {
				if (r1.overlaps(r2)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String str = "";
		for (Range r : parts) {
			str += r + "U";
		}
		return str;
	}
}
