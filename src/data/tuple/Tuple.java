package data.tuple;

import data.Data;

public class Tuple {

	protected final Object[] data;

	public Tuple(Object... data) {
		this.data = data;
	}

	public Object getObject(int index) {
		return this.data[index];
	}

	public Object[] getData() {
		return this.data;
	}

	public void set(int index, Object o) {
		this.data[index] = o;
	}

	public boolean contains(Object obj) {
		for (Object o : this.data) {
			if (Data.equals(o, obj, true))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object another) {
		if (another instanceof Tuple && this.data.length == ((Tuple) another).data.length) {
			for (int i = 0; i < this.data.length; i++) {
				if (!Data.equals(this.data[i], ((Tuple) another).data[i], true))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String str = "(";
		for (int i = 0; i < this.data.length; i++) {
			str += this.data[i];
			if (i != this.data.length - 1) {
				str += ", ";
			}
		}
		return str + ")";
	}
}
