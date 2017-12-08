package uk.co.humboldt.MavenJNIExample;

// Trivial demonstration class
public class TestObject {

	// Load Wrapper.so
	static {
	    System.loadLibrary("wrapper");
	}

	private double x;

	public TestObject(double x) {
		this.x = x;
	}

	public TestObject() {
		x = 0.0;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	// Calls into the wrapper code, which calls into libexample.so
	public native double getY();
}
