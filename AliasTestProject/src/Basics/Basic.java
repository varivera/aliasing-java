package Basics;

public class Basic {
	
	int a,b;
	public int[] arr;
	T v, w, z, y;
	
	public Basic() {
		arr = new int[] {1,2};
		v = new T();
		w = new T();
		z = new T();
	}
	
	public void test1 () {
		v = w;
	}
	
	public void test2 () {
		v = w;
		v = z;
	}
	
	public void test3 () {
		v = w;
		z = v;
	}

}
