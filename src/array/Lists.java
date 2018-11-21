package array;

import java.util.ArrayList;
import java.util.List;

public class Lists {
	
	public static <T> List<T> subList(List<T> list, int beginIndex, int endIndex) {
		return subList(list, beginIndex, endIndex, new ArrayList<T>(endIndex-beginIndex));
	}

	// inc, exclusive
	public static <T> List<T> subList(List<T> list, int beginIndex, int endIndex, List<T> newList) {
		for (int i = beginIndex; i < endIndex; i++) {
			newList.add(list.get(i));
		}
		return newList;
	}

	public static <T> List<T> removeSubList(List<T> list, int beginIndex, int endIndex, List<T> newList) {
		for (int i = beginIndex; i < endIndex; i++) {
			T item = list.remove(beginIndex);
			if (newList != null) {
				newList.add(item);
			}
		}
		return newList;
	}

	public static <T> void removeSubList(List<T> list, int beginIndex, int endIndex) {
		removeSubList(list, beginIndex, endIndex, null);
	}

	public static <T> int numberOfElement(List<T> list, T element) {
		int num = 0;
		for (T el : list) {
			if (element == null) {
				if (el == null) {
					num++;
				}
			} else if (element.equals(el)) {
				num++;
			}
		}
		return num;
	}
}
