package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GenerateSeq {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = "food_truck";
	static final String DB_HOST = "localhost";
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("DB_USER");
	static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
	
	private static final long serialVersionUID = 1L;
	static Connection conn;
	
	static int round;
	static List<Integer> data;
	static int year;
	
//	/** test **/
//	public GenerateSeq(){
//		data = new LinkedList<Integer>();
//        /** dummy data**/
//        Random rand = new Random();
//        year = 2016;
//        for(int i=0; i<20; i++){
//        	data.add(rand.nextInt(60)+70); //random number b/w 70 and 130 as ID
//        }
//	}
	
//	public List<Integer[]> test(){
//		try {
//			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return initiate(data.size());
//	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    }

	
	/**
	 * populate round and sequence with the latest round and generated sequence based on the number of trucks
	 * @param number of trucks 
	 * @throws SQLException 
	 */
	private static void initiate(int size) {
		// TODO Auto-generated method stub
		Statement st = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		try {
			Class.forName("org.postgresql.Driver");
			st = conn.createStatement();
			
			//Sql to retrieve last roundnum last: 
			String sql1 = "SELECT MAX(round_number) FROM rounds WHERE year = "+year;
			rs1 = st.executeQuery(sql1);
			rs1.next();
			int last = rs1.getInt(1);
			round = last + 1;
			//Create new round	
			String sql2 = "INSERT INTO rounds (year, round_number) values ("+year+", "+round+")";
			st.executeUpdate(sql2);
			//Create new sequence based on food truck
			for(int i = 1; i< size+1;i++){
				String temp = getSeqQuery(i, round);
				st.executeUpdate(temp);
			}
			//insert into Lottery
			//Get truck name and email for lottery insert
			Map<Integer, String[]> truckinfo = new HashMap<Integer, String[]>();
			String sql4 = "SELECT owner_name, email, id FROM foodtruck";
			rs2 = st.executeQuery(sql4);
			while(rs2.next()){
				String name = rs2.getString(1).trim();
				String email = rs2.getString(2).trim();
				int id = rs2.getInt(3);
				String[] temp = {name, email};
				truckinfo.put(id, temp);
			}
			//insert into lottery
			insertLottery(truckinfo);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void insertLottery(Map<Integer, String[]> truckinfo) {
		try {
			Class.forName("org.postgresql.Driver");
			Statement st = conn.createStatement();
			
			//randomly generate order
			Collections.shuffle(data);
			int position = 0;
			while(position < data.size()){
				int temp = data.get(position);
				String s = getLotteryQuery(temp, truckinfo, position+1);
				st.executeUpdate(s);
				position++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getLotteryQuery(int id, Map<Integer, String[]> truckinfo, int position) {
		// TODO Auto-generated method stub
		
		String[] array = truckinfo.get(id);
		String name = array[0];
		String email = array[1];
		return "INSERT INTO lottery (name, email, id, year, round_number, position) values ('"+name+"', '"+email+"', "+id+", "+year+", "+round+", "+position+")";
	}

	/**
	 * return query line for inserting sequence
	 * @param number
	 * @param last
	 * @return
	 */
	private static String getSeqQuery(int number, int last) {
		return "INSERT INTO sequence (year, round_number, position) values ("+year+", "+last+1+", "+number+")";
	}
	
	public static int getfirst(){
		
//		try {
//			Class.forName("org.postgresql.Driver");
//			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		
        data = new LinkedList<Integer>();
//        /** dummy data**/
//        Random rand = new Random();
//        year = 2016;
        data.add(70);
        data.add(71);
        //data.add(72);
//         
//        //retrieve order of new round
//        initiate(data.size());

		return data.get(0);
	}
	
	public static int getnext(int order){
		System.out.println(data.get(order-1));
		return data.get(order-1);
	}
	
	public static int getlength(){
		return data.size();
	}
	
	public static void generate_sequence(){
		
	}
	
}
