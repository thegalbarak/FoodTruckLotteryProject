package main;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.*;

@WebServlet("/IdMatch")
public class IdMatch extends HttpServlet {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = "food_truck";//
	static final String DB_HOST = "localhost";
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("DB_USER");
	static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
	
	
	
	/** export FOODTRUCKUSER, FOODTRUCKPASS in terminal**/
	private static final long serialVersionUID = 1L;
	static Connection conn;
	
	public IdMatch(){
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String id = request.getParameter("TruckId"); 
        String email = request.getParameter("Email");
        
        /** DIDNOT ALTER**/
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
 
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
 
        /** DID NOT ALTER above**/
        
        Gson gson = new Gson(); 
        JsonObject myObj = new JsonObject();
 
        Truck truck = getInfo(id, email);
        //output Array
        List<String[]> list = truck.getShiftList();
        String[][] shifts = new String[list.size()][3];
        
        int i =0;
        while(!list.isEmpty()){
        	shifts[i] = list.remove(0);
        	i++;
        }
        JsonElement shiftArrayJSON = gson.toJsonTree(shifts);
        
        JsonElement truckInfo = gson.toJsonTree(truck);
        if(truck.getName() == null){//return false if id not verified
            myObj.addProperty("success", false);
        }else {
            myObj.addProperty("success", true);
        }
        myObj.add("truck", truckInfo);
        //test for array
        myObj.add("shiftArray", shiftArrayJSON);
        //END TEST
        out.println(myObj.toString());
 
        out.close();
 
    }
	
	public Truck getInfo(String id, String email) {
		boolean verified = verify(id, email);
		
		Truck truck = new Truck();
	    Statement st = null;
	    String sql = null;
	    String sql1 = null;
	    String sql2 = null;
	    ResultSet rs = null;
	    ResultSet rs1 = null;
	    ResultSet rs2 = null;

        try {
        	Class.forName("org.postgresql.Driver");
        	st = conn.createStatement();
            //sql for SHIFTLIST
            sql = "select location_name, dates, slot from assignment where id = "+id;
            //sql for NAME
            sql1 = "SELECT name \n"+
            		"FROM foodtruck \n"+
            		"WHERE id = "+id+";";  
            //sql for POINTS
            sql2 = "SELECT points \n"+
            		"FROM foodtruck \n"+
            		"WHERE id = "+id+";";
            
            //return 
            // truckname = james1
            
            /** retrieve **/
            if(verified){ /** id verified **/
            	//Name
                rs1 = st.executeQuery(sql1);
            	rs1.next();
            	String truckName = rs1.getString(1);
            	truck.setName(truckName);
            	
            	//Points
                rs2 = st.executeQuery(sql2);
            	rs2.next();
            	String truckPoints = rs2.getString(1);
            	System.out.println("Truckpoints: "+truckPoints);//
            	truck.setPoints(truckPoints);
            	
            	//2-D Array
                rs = st.executeQuery(sql);
                
                List<String[]> list = new LinkedList<String[]>();

                while(rs.next()){
                	 String locationName = rs.getString(1);
                	 String date = rs.getString(2);
                	 String slot = rs.getString(3);
                	 truck.addShift(locationName, date, slot);
                }
                
            }else{/**NOT verified**/
            	return new Truck();
            }
            

        } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(IdMatch.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return truck;
	}

	private boolean verify(String id, String email) {
		
		Statement st = null;
	    String sql = null;
	    ResultSet rs = null;
	    String verifyEmail = null;
		
        try {
        	Class.forName("org.postgresql.Driver");
        	st = conn.createStatement();
            
            /** use ID/EMAIL to retrieve NAME, POINTS, SHIFTLISTS(ID, LOCATION NAME, DAY, SLOT)**/
            
            sql = "SELECT email \n" + 
            "FROM foodtruck " +
            		"WHERE id = "+ id ;
            
            
			/** VERIFY ID/EMAIL **/
            
            rs = st.executeQuery(sql);
            while(rs.next()){
            	verifyEmail = rs.getString(1).trim(); //email
            }
            
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(verifyEmail!=null && verifyEmail.equals(email)){
        	return true;
        }else{
        	return false;
        }
	}
}
