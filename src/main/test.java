package main;

public class test {
	public static void main(String[] args){
		IdMatch a = new IdMatch();
		Truck b = a.getInfo("1", "zliu17@brandeis.edu");
		System.out.print("MAIN PROGRAM TRUCK B IS " + b);
	}
}
