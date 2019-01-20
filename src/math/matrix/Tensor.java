package math.matrix;

public class Tensor {

	private final int[] dimensionLengths;

	private Object data;

	public Tensor(int... dimensionLengths) {
		this.dimensionLengths = dimensionLengths;
		if (this.dimensionLengths.length == 0) {
			this.data = 0.0;
		} else {
			this.data = new Object[this.dimensionLengths[0]];
			this.initArray((Object[]) this.data, this.dimensionLengths, 1);
		}
	}

	private void initArray(Object[] data, int[] dimensionLengths, int currentIndex) {
		for (int i = 0; i < data.length; i++) {
			data[i] = new Object[dimensionLengths[currentIndex]];
			if (currentIndex + 1 < dimensionLengths.length) {
				this.initArray((Object[]) data[i], dimensionLengths, currentIndex + 1);
			} else {
				this.initData((Object[]) data[i], 0.0);
			}
		}
	}

	private void initData(Object[] data, Object placeHolder) {
		for (int i = 0; i < data.length; i++) {
			data[i] = placeHolder;
		}
	}

	public void set(Object obj, int... coordinates) {
		if (coordinates.length == 0) {
			this.data = obj;
		} else {
			this.set(obj, (Object[]) this.data, 0, coordinates);
		}
	}

	private void set(Object obj, Object[] data, int currentIndex, int... coordinates) {
		if (currentIndex == coordinates.length - 1) {
			data[coordinates[currentIndex]] = obj;
		} else {
			this.set(obj, (Object[]) data[coordinates[currentIndex]], currentIndex + 1, coordinates);
		}
	}

	public Object get(int... coordinates) {
		if (coordinates.length == 0)
			return this.data;
		else
			return this.get((Object[]) this.data, 0, coordinates);
	}

	private Object get(Object[] data, int currentIndex, int... coordinates) {
		if (currentIndex == coordinates.length - 1)
			return data[coordinates[currentIndex]];
		else
			return this.get((Object[]) data[coordinates[currentIndex]], currentIndex + 1, coordinates);
	}

	@Override
	public String toString() {
		return this.toString(this.data);
	}

	public String toString(Object data) {
		String str = "";
		if (data instanceof Object[]) {
			Object[] arrdata = (Object[]) data;
			str += "[";
			for (int i = 0; i < arrdata.length; i++) {
				str += this.toString(arrdata[i]);
				if (i < arrdata.length - 1) {
					str += ", ";
				}
			}
			str += "]";
		} else {
			str += data.toString();
		}
		return str;
	}

}
