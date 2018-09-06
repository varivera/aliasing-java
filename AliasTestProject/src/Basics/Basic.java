package Basics;

public class Basic {
	
	public int[] arr;
	T v, w, z, y;
	int q, q2;
	
	public Basic() {
		arr = new int[] {1,2};
		v = new T();
		w = new T();
		z = new T();
	}
	
	public void creationAndCall2 () {
		assg2();
		v = new T(); 
	}
	
	public void creation () {
		T a = new T(); //local var
		
		v = new T();  // class var
	}
	
	public void localArg2 (T b, int ss) {
		T a = new T();
		a = v;
		w = a;
		ss = q;
		q2 = ss;
	}
	
	public void localArg1 (T b, int ss) {
		T a = new T();
		a = v;
		w = a;
		
	}
	
	public void assg3 () {
		v = w;
		z = v;
	}
	
	public void assg2 () {
		v = w;
		v = z;
	}
	
	public void assg1 () {
		v = w;
	}

}
