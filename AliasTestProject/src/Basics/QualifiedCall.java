package Basics;

public class QualifiedCall {
	
	T v, t;
	public QualifiedCall() {
		v = new T();
		t = new T();
	}
	
	public void unq4() {
		v = t.a;
	}
	
	public void unq3() {
		v = t.getA();
	}
	
	public void unq2() {
		t.remoteArg(v);
	}
	
	public void unq1() {
		Call();
		t.remoteCall();
	}
	
	public void Call() {
		v = t;
	}

}
