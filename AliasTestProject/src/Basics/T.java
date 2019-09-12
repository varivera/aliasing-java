package Basics;

public class T {
	T a,b, c, x, y;
	
	T right;
	
	public T(int i) {
	}
	
	public T() {
		a = b;
	}
	
	public T(T v) {
		a = v;
	}
	
	void remoteCall() {
		a = b;
	}
	
	void remoteArg(T v) {
		a = v;
	}

	public T getA() {
		return a;
	}
	
	void m(int i, boolean b, 
			String v, T w) {
		a = c;
	}

	void m(int i, boolean b, 
			T v, T w) {
		a = this.b;
	}
	
	public void set_x(T ww) {
		x = ww;
	}
	
	public void set_y(T ww) {
		y = ww;
	}
	

}
