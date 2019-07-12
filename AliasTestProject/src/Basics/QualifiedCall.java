package Basics;

public class QualifiedCall {
	
	T v, t;
	public QualifiedCall() {
		v = new T();
		t = new T();
	}
	
	public void unq1() {
		//Call();
		t.remoteCall();
	}
	
	public void Call() {
		v = t;
	}

}
