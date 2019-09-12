package Basics;

public class ControlStruc {
	
	T a, b, c, d, e, f, g, h,x;
	
	
	public ControlStruc() {
		a = new T();
		b = new T();
		c = new T();
		d = new T();
		e = new T();
		f = new T();
		g = new T();
		h = new T();
		x = new T();
		
	}
	
	public void cond1(boolean cc) {
		a = b;
		if (cc) {
			c = d;
		}
		//a:b and c:d
	}
	
	public void cond2(boolean cc) {
		if (cc) {
			a = b;
		}else {
			c = d;
		}
		//a:b and c:d
	}
	
	public void cond3(boolean cc) {
		if (cc) {
			a = b;
		}else {
			c = d;
		}
		a.b = c;
		//a:b, c:d, a.b : c
		// b.b:c, a.b:d
	}
	
	public void cond4(boolean cc) {
		if (cc) {
			a = c;
		}else {
			b = c;
		}
		//a:c, b:c, not a:b
	}
	
	public void cond5(boolean cc) {
		a=b;
		if (cc) {
			a = c;
		}else {
			a = d;
		}
		//a:c, a:d, not a:b
	}
	
	public void cond6(boolean cc) {
		a=b;
		c=d;
		if (cc) {
			c=b;
			if (cc) {
				a = e;
			}else {
				a = f;
			}
		}else {
			a = d;
		}
		//a:e, b:c, a:f, d:c, c:a, a:d
		//not {f:c, b:a, c:e}
	}
	
	public void cond7(boolean cc) {
		if (cc) {
			a.right = b.a;
		}else {
			c = b.a;
		}
		b = x;
		if (true) {
			x = a.c;
		}
		//a:e, b:c, a:f, d:c, c:a, a:d
		//not {f:c, b:a, c:e}
	}
	
		
}
