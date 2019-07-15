package Basics;

public class T {
	T a,b, c;
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
	

}
