package main;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.json.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.GenerateSeq;

@ServerEndpoint("/StartLottery")
public class StartLottery {
	static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());
//	static int min = 1; 
//	static int max = 3; // this should be the food truck owner number query from database
//	//static int current_position = 0;
//	int owner_length = 2;
//	//static int operation;
//	//static int id;
	//static int current_position;
	ArrayList<Integer> arraylist = new ArrayList<Integer>();
	
	@OnOpen
	public void handleOpen(Session userSession){
		users.add(userSession);
		String a = userSession.getId();
		System.out.println("client: " +a+ " is now connected...");
	}
	
	/*@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException{
		String username = (String) userSession.getUserProperties().get("username");
		if (username == null) {
			userSession.getUserProperties().put("username", message);
			userSession.getBasicRemote().sendText(buildJsonData("System","you are now connected as" +message));
		} else {
			Iterator<Session> iterator = users.iterator();
			while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(username, message));
		}
	}*/
	/*@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException{
		String username = (String) userSession.getUserProperties().get("username");
		if (username == null) { // which means the users login for the first time
			if (message == "admin"){ // which means it is the admin who login
				userSession.getUserProperties().put("username", "admin");
			}
			else{
				userSession.getUserProperties().put("username", "client");
			}
		}
		
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(max - min + 1) + min;
		
		if(round_end == 0){
		Iterator<Session> iterator = users.iterator();
		while (iterator.hasNext()) {
			if((String) iterator.next().getUserProperties().get("username") == "admin")
			{iterator.next().getBasicRemote().sendText(buildJsonData("lottery start!"));}
			else
			iterator.next().getBasicRemote().sendText(buildJsonData(Integer.toString(randomInt)));
		}
		}
	}*/
	@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException{
		String userid = (String) userSession.getUserProperties().get("userid");
		//if (userid == null) {
		JsonElement jelement = new JsonParser().parse(message);
		com.google.gson.JsonObject obj = jelement.getAsJsonObject();
		int operation = Integer.parseInt(obj.get("operation").toString());
		//int id = Integer.parseInt(obj.get("id").toString());
		//int current_position = Integer.parseInt(obj.get("next").toString());
		int current_position = obj.get("next").getAsInt();
			
			if(operation == 1){ // from food truck owner pick a slot
				int next_position = current_position+1;
				String next_next_id = "0";
				if(current_position < GenerateSeq.getlength()){ // if still has food truck owner in the array
					String next_id = new Integer(GenerateSeq.getnext(next_position)).toString();
					if(current_position+1 < GenerateSeq.getlength())
					{next_next_id = new Integer(GenerateSeq.getnext(next_position+1)).toString();}
					
					JsonObject jsonObject = Json.createObjectBuilder().add("next_id", next_id).add("next_position", next_position).add("next_next_id",next_next_id).build();
					StringWriter stringWriter = new StringWriter();
					try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
						jsonWriter.write(jsonObject);
					}
					String sent = stringWriter.toString();
					/**/
					Iterator<Session> iterator = users.iterator();
					while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(sent));	
					}
				else { // end this round
					JsonObject jsonObject = Json.createObjectBuilder().add("status", "finish").build();
					StringWriter stringWriter = new StringWriter();
					try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
						jsonWriter.write(jsonObject);
					}
					String sent = stringWriter.toString();
					
					Iterator<Session> iterator = users.iterator();
					while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(sent));
					
				}
			}
			else if(operation == 0){ // from admin start the lottery
				userSession.getUserProperties().put("userid", "admin");
				//int round = obj.get("id").getAsInt();
				/*generate a random sequence */
				/*owner_length should be query from the database */
				
				/*for (int n=0; n<owner_length-1; n++)
				{
					arraylist.add(n+1);
				}
				Collections.shuffle(arraylist);*/
				
				//String next_id = arraylist.get(current_position).toString();
				GenerateSeq.generate_sequence();
				String next_id = new Integer(GenerateSeq.getfirst()).toString();
				int next_position = 1;
				String next_next_id = new Integer(GenerateSeq.getnext(next_position+1)).toString();
				
				JsonObject jsonObject = Json.createObjectBuilder().add("next_id", next_id).add("next_position", next_position).add("next_next_id",next_next_id).build();
				StringWriter stringWriter = new StringWriter();
				try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
					jsonWriter.write(jsonObject);
				}
				String sent = stringWriter.toString();
				
				/**/
				Iterator<Session> iterator = users.iterator();
				while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(sent));
			}
			
			
		} 
	//}
	
	
	@OnClose
	public void handleClose(Session userSession){
		users.remove(userSession);
		String a = userSession.getId();
		System.out.println("client:"+ a +" is now disconnected...");
	}
	
	/*private String buildJsonData(String username, String message){
		JsonObject jsonObject = Json.createObjectBuilder().add("message", username+":"+message).build();
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.write(jsonObject);
		}
		
		return stringWriter.toString();
	}*/
	private String buildJsonData(String message){
		JsonObject jsonObject = Json.createObjectBuilder().add("message", message).build();
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.write(jsonObject);
		}
		
		return stringWriter.toString();
	}
	
	
	
}

