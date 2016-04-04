package util;

/**
 * The Pair class implements pairs of two objects.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <T>
 *            the type of the first component.
 * @param <S>
 *            the type of the second component.
 */
public class Pair<T, S> {

	/**
	 * The first component.
	 */
	private T first;

	/**
	 * The second component.
	 */
	private S second;

	/**
	 * Constructs a pair.
	 * 
	 * @param f
	 *            the first component.
	 * @param s
	 *            the second component.
	 */
	public Pair(T f, S s) {
		first = f;
		second = s;
	}

	/**
	 * Sets the first component.
	 * 
	 * @param f
	 *            the value for the first component.
	 */
	public void setFirst(T f) {
		this.first = f;
	}

	/**
	 * Sets the second component.
	 * 
	 * @param s
	 *            the value for the second component.
	 */
	public void setSecond(S s) {
		this.second = s;
	}

	/**
	 * Returns the first component.
	 * 
	 * @return the first component.
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Returns the second component.
	 * 
	 * @return the second component.
	 */
	public S getSecond() {
		return second;
	}

	/**
	 * Returns the String representation of this Pair.
	 */
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}