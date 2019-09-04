package Basics;

/**
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 *	Examples from the Alias Analysis paper by Rivera and Meyer:
 *		https://arxiv.org/pdf/1808.08748.pdf
 */

public class AAPaper {
	T a, b, x, l;
	AAPaper a2;
	public AAPaper() {
		a = new T(0);
		b = new T(0);
		x = new T(0);
		l = new T(0);
	}

	public void assignment () {
		a = b;
		// a:b -> a is aliased to b
	}
	
	public void composition () {
		a = x;
		b = x;
		// a:b:x -> a, b, and x are aliased to each other
	}
	
	public void creation() {
		x = new T(0);
	}
	
	public void conditional(boolean C) {
		if (C) {
			a = x;
		}else {
			b = x;
		}
		// a:x and b:x
		// NO: a:b
		/**
		 * {(n0, a, n3), (n0, x, n3), (n0, b, n2),
		 * 
		 *  (n'0, a, n1), (n'0, x, n3), (n'0, b, n3)}
		 */
	}
	
	public void loop(boolean C) {
		while (C) {
			l = l.right;
		}
		/**
		 * {(n0, l, n1), (n0, l, n2), (n0, l, n3),
		 *  (n1, right, n2),
		 *  (n2, right, n3)
		 *  (n3, right, n3)}
		 */
	}
	
	public void unqualifiedCall() {
		setx(a);
		// a:x
	}
	
	public void callSiteSensitivity() {
		setx (a);
		setx (b);
		// b:x
	}
	
	public void qualifiedCall() {
		a2.setx(b);
		// a2.x:b
	}
	
	public void setx(T v) {
		x = v;
	}
	
	
	//TODO: implement the other examples
	
}
