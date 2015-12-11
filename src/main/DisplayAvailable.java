package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@WebServlet("/DisplayAvailable")
public class DisplayAvailable extends HttpServlet {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = "food_truck";//
	static final String DB_HOST = "localhost";
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("DB_USER");
	static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
	
	private static final long serialVersionUID = 1L;
	static Connection conn;
	
	public DisplayAvailable(){
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        int year = 2016;
		int id = Integer.parseInt(request.getParameter("id")); 
        
        /** **/
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
 
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        /** **/
        System.out.println("line 70 display called");
        Gson gson = new Gson(); 
        JsonObject myObj = new JsonObject();
		//obtain shifts 
		String[][] avail = getAvail(year, id);
		//convert to JSON
		JsonElement aJson = gson.toJsonTree(avail);
        /** return false if id not verified, else true and proceed **/
        if(avail[0][0] == null){//no available shifts
            myObj.addProperty("success", false);
        }else {
            myObj.addProperty("success", true);
        }
        /** pass back truck object and shift array in Json format**/
        myObj.add("avail", aJson);
        out.println(myObj.toString());
        out.close();
    }

	private String[][] getAvail(int year, int id) {
		String[][] data = null;
		String sql = null;
		if(id==-1){
			sql = getQuery(year);
		}else{
			sql = getCurrentQuery(id, year);
		}
		
		try {
			Class.forName("org.postgresql.Driver");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			List<String[]> result = new LinkedList<String[]>();
			while(rs.next()){
				String l = rs.getString(1);
				String d = rs.getString(2);
				String s = rs.getString(3);
				String[] temp = {l, d, s};
				result.add(temp);
			}
			
			data = new String[result.size()][3];
			for(int i = 0; i<data.length; i++){
				data[i] = result.remove(0);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	private String getQuery(int year) {
		// TODO Auto-generated method stub
		return "SELECT location_name, dates, slot \n"+ 
		"from shift_capacity as a \n"+
		"WHERE capacity > (SELECT COUNT(*) FROM assignment WHERE location_name = a.location_name AND dates = a.dates AND slot = a.slot AND year = "+year+")";
	}
	
	private String getCurrentQuery(int id, int year) {
		// TODO Auto-generated method stub
		System.out.println("In query should be fine");
		return "select * from assignment where id = "+id+" and year = "+year;
	}
	
}