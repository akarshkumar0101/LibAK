package data.tuple;

import data.Data;

public class Tuple {
	protected final Object[] data;

	public Tuple(Object... data) {
		this.data = data;
	}

	public Object getObject(int index) {
		return data[index];
	}

	public Object[] getData() {
		return data;
	}

	public void set(int index, Object o) {
		data[index] = o;
	}

	public boolean contains(Object obj) {
		for (Object o : data) {
			if (Data.satisfiesEquavilance(o, obj, false))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object another) {
		if (another instanceof Tuple && data.length == ((Tuple) another).data.length) {
			for (int i = 0; i < data.length; i++) {
				if (!Data.satisfiesEquavilance(data[i], ((Tuple) another).data[i], false))
					return false;
			}
			return true;
		}
		return false;
	}
}
