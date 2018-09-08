package math.matrix;

public class Tensor {
	private Object data;

	public Tensor(int... dimensionLengths) {
		if (dimensionLengths.length == 0) {
			data = 0.0;
		} else {
			data = new Object[dimensionLengths[0]];
			initArray((Object[]) data, dimensionLengths, 1);
		}
	}

	private void initArray(Object[] data, int[] dimensionLengths, int currentIndex) {
		for (int i = 0; i < data.length; i++) {
			data[i] = new Object[dimensionLengths[currentIndex]];
			if (currentIndex + 1 < dimensionLengths.length) {
				initArray((Object[]) data[i], dimensionLengths, currentIndex + 1);
			} else {
				initData((Object[]) data[i], 0.0);
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
			data = obj;
		} else {
			set(obj, (Object[]) data, 0, coordinates);
		}
	}

	private void set(Object obj, Object[] data, int currentIndex, int... coordinates) {
		if (currentIndex == coordinates.length - 1) {
			data[coordinates[currentIndex]] = obj;
		} else {
			set(obj, (Object[]) data[coordinates[currentIndex]], currentIndex + 1, coordinates);
		}
	}

	public Object get(int... coordinates) {
		if (coordinates.length == 0)
			return data;
		else
			return get((Object[]) data, 0, coordinates);
	}

	private Object get(Object[] data, int currentIndex, int... coordinates) {
		if (currentIndex == coordinates.length - 1)
			return data[coordinates[currentIndex]];
		else
			return get((Object[]) data[coordinates[currentIndex]], currentIndex + 1, coordinates);
	}

	@Override
	public String toString() {
		return toString(data);
	}

	public String toString(Object data) {
		String str = "";
		if (data instanceof Object[]) {
			Object[] arrdata = (Object[]) data;
			str += "[";
			for (int i = 0; i < arrdata.length; i++) {
				str += toString(arrdata[i]);
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
