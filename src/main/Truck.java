package main;

import java.util.LinkedList;
import java.util.List;

public class Truck {
	
	//Name, ID, pts, Shifts
	public String name = null;
	public String points = null;
//	public List<Shift> shiftList = new LinkedList<Shift>();
	public List<String[]> list = new LinkedList<String[]>();
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getPoints(){
		return points;
	}
	public void setPoints(String points){
		this.points = points;
	}
	
	public List<String[]> getShiftList(){
		return list;
	}
	
	public void addShift(String locationName, String day, String slot){
		String[] temp = {locationName, day, slot};
		list.add(temp);
	}
	
	public String toString(){
		return name+" "+points+" \n"
//	+shiftList
	;
	}
}
