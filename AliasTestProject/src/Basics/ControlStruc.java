package Basics;

public class ControlStruc {
	
	T a, b, c, d;
	
	
	public ControlStruc() {
		a = new T();
		b = new T();
		c = new T();
		d = new T();
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
		//a:b, c:d, a.b = c
		// b.b:c, a.b:d
	}
	
		
}
