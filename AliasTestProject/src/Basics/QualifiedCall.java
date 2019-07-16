package Basics;

public class QualifiedCall {
	
	T v2, v, t;
	public QualifiedCall() {
		v = new T();
		v2 = new T();
		t = new T();
	}
	
	public void qThis18 () {
		this.v = this.v2;
	}
	
	public void qThis17 () {
		v = this.t;
	}
	
	
	public void qThis16 () {
		this.v = t;
	}
	
	public void q15 (T v) {
		q14 (t.a);
	}
	
	public void q14 (T v) {
		this.v = v;
	}
	
	public void q13() {
		v.remoteArg(t.a);
	}
	
	public void q12() {
		v = t.getA().a;
	}
	
	public void q11() {
		gett().getA().a = v2;
	}
	
	public void q10() {
		//t.a = v2;
		gett().a = v2;
	}
	
	public void q9() {
		v2 = gett().a;
	}
	
	public void q8() {
		v.a = gett();
	}
	
	public T gett() {
		return t;
	}
	
	public void q7() {
		v.a.b = t;
	}
	
	public void q6() {
		t = v.a.b;
	}
	
	public void q5() {
		v2 = v;
		v2 = t;
		t.a = v;
	}
	
	public void q4() {
		v2 = v;
		v2 = t;
		v = t.a;
	}
	
	public void q3() {
		v = t.getA();
	}
	
	public void q2() {
		t.remoteArg(v);
	}
	
	public void q1() {
		Call();
		t.remoteCall();
	}
	
	public void Call() {
		v = t;
	}

}
