package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@WebServlet("/UpdateOldShifts")
public class UpdateOldShifts extends HttpServlet{
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

	static final int YEAR = 2016;
	
	public UpdateOldShifts(){
		
	}
	
	public void test(){
		try {
			doPost(null, null);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		int id = Integer.parseInt(request.getParameter("id"));
		
		JsonParser parser= new JsonParser();
		 
//		  Object obj=parser.parse(s);
//		  JsonArray array=(JsonArray)obj;
//		  System.out.println("======the 2nd element of array======");
//		  System.out.println(array.get(1));
//		  System.out.println();
//		                
//		  JsonObject obj2=(JsonObject)array.get(1);
//		  System.out.println("======field \"1\"==========");
//		  System.out.println(obj2.get("1"));    
//
//		                
//		  s="{}";
//		  obj=parser.parse(s);
//		  System.out.println(obj);
//		                
//		  s="[5,]";
//		  obj=parser.parse(s);
//		  System.out.println(obj);
//		                
//		  s="[5,,2]";
//		  obj=parser.parse(s);
//		  System.out.println(obj);
//		  
		  //
		  
		  String d = request.getParameter("shifts");
		  JsonArray jArray = (JsonArray) parser.parse(d);
		  int l = jArray.size();
		  System.out.println("the jsonarray is size: "+l);
		  String[][] locations = null;
		  if(l==1){
			  JsonArray first = (JsonArray)jArray.get(0);
			  String[] f = getArray(first);
			  locations = new String[1][3];
			  locations[0] = f;
		  }else if(l==2){//2
			  JsonArray first = (JsonArray)jArray.get(0);
			  String[] f = getArray(first);
			  JsonArray second = (JsonArray)jArray.get(1);
			  String[] f2 = getArray(second);

			  locations = new String[2][3];
			  locations[0] = f;
			  locations[1] = f2;
		  }
		  

//        /** **/
//        PrintWriter out = response.getWriter();
//        response.setContentType("text/html");
//        response.setHeader("Cache-control", "no-cache, no-store");
//        response.setHeader("Pragma", "no-cache");
//        response.setHeader("Expires", "-1");
// 
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
//        response.setHeader("Access-Control-Max-Age", "86400");
//        /** **/
//        out.close();
        


        Statement st = null;
        
        try {
			st = conn.createStatement();
			String[] contact = getContact(id);
			//convert input to psql safe version and then update
			if(l>0){
				for(int i = 0; i<locations.length; i++){
					System.out.println(locations[i][0]+" "+locations[i][1]+" "+locations[i][2]);
					String[] temp ={psqlSafe(locations[i][0]), psqlSafe(locations[i][1]), psqlSafe(locations[i][2])};
					String sql = getQuery(temp, contact[0], contact[1], id);
					st.executeUpdate(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
    }
	
	private String[] getArray(JsonArray first) {
	// TODO Auto-generated method stub
		String s = psqlSafeString(first.get(0).toString());
		  String s2 = psqlSafeString(first.get(1).toString());
		  String s3 = psqlSafeString(first.get(2).toString());
		  String[] temp = new String[3];
		  temp[0]=s;temp[1]=s2;temp[2]=s3;
	return temp;
}

	private String psqlSafeString(String string) {
		// TODO Auto-generated method stub
		return string.substring(1, string.length()-1);
	}

	private String[] getContact(int id) {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT owner_name, email from foodtruck WHERE id = "+ id);
			rs.next();
			String name = rs.getString(1).trim();
			String email = rs.getString(2).trim();
			String[] result = {name, email};
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return null;
}

	private String getQuery(String[] temp, String name, String email, int id) {
		
		String result = "INSERT INTO assignment (name, email, id, location_name, year, dates, slot) values ('"+name+"', '"+email+"', "+id+", '"+temp[0]+"', "+YEAR+", '"+temp[1]+"', '"+temp[2]+"')";
		System.out.println(result);
		return result;
}

	/**
	 * returns psql-safe version of truck name
	 * @param temp
	 * @return
	 */
	private static String psqlSafe(String temp) {
		// TODO Auto-generated method stub
		int itr = 0;
		while(temp.indexOf("'", itr) != -1){
			int i = temp.indexOf("'", itr);
			temp = temp.substring(0, i)+"'"+temp.substring(i); 
			itr = i+2;
		}
		return temp;
	}
}