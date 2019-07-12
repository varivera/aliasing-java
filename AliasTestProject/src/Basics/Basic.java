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
	
	public void creation3c () {
		this.v = this.w;
	}
	
	public void creation3b () {
		v = this.w;
	}
	
	
	public void creation3a () {
		//this.v = w;
		this.v = w;
	}
	
	public void creationAndCall1 () {
		assg2();
		v = new T(); 
	}
	
	public void creationAndCall2 () {
		v = z;
		v = new T(); 
	}
	
	//here
	//public void 
	
	public void func () {
		w = get_v();
	}
	
	public T get_v () {
		return v;
	}
	
	public void methodInv2() {
		methodInv ();
	}
	
	public void methodInv() {
		w = get_v ();
	}
	
	//returning an attribute
	public T return6 () {
		return return5();
	}
	
	//returning an attribute
	public T return5 () {
		return v;
	}
	
	//returning a local 
	public T return4 () {
		T local = new T();
		return local;
	}
	
	//returning an argument
	public T return3(T a) {
		return a;
	}
	
	//returning an expression
	public Integer return2() {
		return 1+1;
	}
	
	//returning a integer
	public Integer return1() {
		return 1;
	}
	
	public void nestedCall () {
		t2 (z);
	}
	
	public void t2 (T ar) {
		t3 (ar);
	}
	
	public void t3 (T ar) {
		args (ar, 2);
	}
	
	
	public void unq_call_arg() {
		args (w, 2+3);
	}
	
	public void args (T ar, Integer i) {
		v = ar;
	}
	
	public void unq_call() {
		assg1 ();
	}
	
	public void creation2 () {
		T v = w;
		T a = v;
		//T b;
		T w = a;
	}
	
	public void creation (T b) {
		T a = new T(); //local var
		T c; //local var
		c = new T(); //local var
		b = new T(); //argument
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
