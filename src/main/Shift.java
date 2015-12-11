package main;

public class Shift {
	static int id;
	static String locationName;
	static String day;
	static String slot;
	
	public Shift(){
		this(0, null, null, null);
	}
	
	public Shift(int id, String locationName, String day, String slot){
		this.id = id;
		this.locationName = locationName;
		this.day = day;
		this.slot = slot;
	}
	
	public String toString(){
		return "Shift id: "+id+"=["+locationName+", "+day+", "+slot+"]";
	}
}
