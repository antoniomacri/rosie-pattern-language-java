package uk.co.humboldt.MavenJNIExample;

public class Application {
	
	public static void main(String[] args) throws Exception {
		TestObject obj = new TestObject();
		for(int i = 0; i < args.length; i++) {
			obj.setX(Double.valueOf(args[i]).doubleValue());
			System.out.println("f(" + obj.getX() + ") = " + obj.getY());
		}
	}
}
