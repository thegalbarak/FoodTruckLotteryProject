var points;
var shiftList;
var name;
var client_email;
var truck_id;

//var shiftList=[["123","location1","slot","day"],["456","location2","slot","day"]];
function check(){
	//make the AJAX request, dataType is set to json
	    //meaning we are expecting JSON data in response from the server
		var email= document.getElementById('email').value; 
		var foodtruck=document.getElementById('foodtruck').value;
		truck_id=foodtruck;
		$.cookie("truck_id",truck_id);
		client_email=email;
		$.cookie("client_email",client_email);
		


		var dataString = "Email=" + email+ "&TruckId=" + foodtruck;
		//alert("about to send"+dataString);

	    $.ajax({
	        type: "POST",
	        url: "IdMatch",
	        data: dataString,
	        dataType: "json",
	        
	        //if received a response from the server
	        success: function( data, textStatus, jqXHR) {
	            //if verified need to recieve truck name ,points, shifts list (containing: id,location, day, slot)
	   
	            if(data.success){
	            	
	                $("#response").html("<b>Welcome!</b>");
	      
	                //no points mean no shifts to keep= go to addorgo page.
	                //points=window.points;
	                points=data.truck.points;
	                //window.points=points;
	                shiftList=data.shiftArray;
	                name=data.truck.name;
	               
	                if(shiftList.length<points){
	                	if(shiftList.length==1){
	                		points=1;
	                	}
	                	else{
	                		points=0;
	                	}
	                }
	                
	                
	                //client_id=data.shiftArray[0][0];
	                //document.cookie="p"+points+";name"+name;
	                $.cookie("name",name);
	                $.cookie("points",points);
	                $.cookie("array",JSON.stringify(shiftList));
	                //$.cookie("client_id",client_id);
	                
	               // $.cookie("name", name);
	                if (points==0){ 
	                	//alert("add or go now");
	                	
	                	window.location.href="addOrGo.html";
	                }
	                //keep 1 or 2 shifts=go to keeping shift page
	                else{
	                	//alert(name+" "+points+ ""+shiftList);
	                	
	                	window.location.href="keeping_shift.html"; 
	            	}   

	            }
	            else {
	                $("#response").html("<b>Email and Truck ID do not match!</b>");
	                alert("can't be verified! ");
	                dataString="";
	            }
	        },
	        
	        //If there was no resonse from the server
	        error: function(jqXHR, textStatus, errorThrown){
	             console.log("Something really bad happened " + textStatus);
	              $("#response").html(jqXHR.responseText);
	        },
	        
	        //capture the request before it was sent to server
	        beforeSend: function(jqXHR, settings){
	            //adding some Dummy data to the request

	            settings.data += "&dummyData=whatever";
	            //disable the button until we get the response
	            $('#submit').attr("disabled", true);
	        },
	        
	        //this is called after the response or error functions are finsihed
	        //so that we can take some action
	        complete: function(jqXHR, textStatus){
	            //enable the button 
	            $('#submit').attr("disabled", false);
	        }

	    });        
}
 


function sendShifts(name,points,shiftList,truck_id){
	//alert(points+ "");
	
	var first = $("select#shifts1").val();
	var second = $("select#shifts2").val();
	
	
		if(points==2){
			$.cookie("firstShift_loc",shiftList[first-1][0]);
			$.cookie("firstShift_date",shiftList[first-1][1]);
			$.cookie("firstShift_slot",shiftList[first-1][2]);
			$.cookie("secondShift_loc",shiftList[second-1][0]);
			$.cookie("secondShift_date",shiftList[second-1][1]);
			$.cookie("secondShift_slot",shiftList[second-1][2]);
			
			
			if (first==second){
				alert("Try again! You picked the same shift twice!");
				return false;
			}
			else{ //send both to java 
				//var dataString =  "firstShiftloc=" + shiftList[first-1][0]+"&firstShiftdate=" shiftList[first-1][1]+"&firstShiftslot=" +shiftList[first-1][2]+"&SecondShiftloc=" + shiftList[second-1][0]+"&SecondShiftdate="+shiftList[second-1][1]+"&SecondShiftdate"+shiftList[second-1][2];
				var datalist=[shiftList[first-1],shiftList[second-1]];
				var dataString = "id="+truck_id+"&shifts="+ JSON.stringify(datalist);
				//alert(dataString);
			    $.ajax({
			        type: "POST",
			        url: "UpdateOldShifts",
			        data: dataString,
			        dataType: "json",
			        
			        success: function( data, textStatus, jqXHR) {
	            //if verified need to recieve truck name ,points, shifts list (containing: id,location, day, slot)
	   
	            	if(data.success){
	            		
	 	    		  	 addToTable(data.avail);
	 	    		   
	            		//alert("success");
	            	}

	            	else {
	            		alert("fail");
	            		return false;
	            	}
			        //continue if received a response from the server
			    	}	
			    }); 

			}
			return true;
		}
	    else{
		//send first to java
		$.cookie("firstShift_loc",shiftList[first-1][0]);
		$.cookie("firstShift_date",shiftList[first-1][1]);
		$.cookie("firstShift_slot",shiftList[first-1][2]);
		//var dataString = "firstShiftloc=" + shiftList[first-1][0]+"firstShiftdate=" shiftList[first-1][1]+"firstShiftslot=" shiftList[first-1][2];
		var datalist=[shiftList[first-1]];
		//alert(datalist);
		var dataString = "id="+truck_id+"&shifts="+JSON.stringify(datalist);
		//alert(dataString);
			    $.ajax({
			        type: "POST",
			        url: "UpdateOldShifts",
			        data: dataString,
			        dataType: "json",
			        
			        success: function( data, textStatus, jqXHR) {
			        	
			        if(data.success){
			        	var d=JSON.parse(data);
	 	    		  	addToTable(d.avail);
			        	alert("success");
	            	}

	            	else {
	            		alert("fail");
	            		return false;
	            	}
			        //continue if received a response from the server
			    	}
			    });  
		}
		return true;

}

function addToTable(oldShifts){
	for(var i=0;i<oldShifts.length;i++){
		changeTable(oldShifts[i][0],oldShifts[i][1],oldShifts[i][2]);
	}
	
}
function changeTable(loc,date,slot){
  var day;
  var slott;
  switch(date){
    case "Monday":day="Mon"; break;
    case "Tuesday":day="Tue"; break;
    case "Wednesday":day="Wed"; break;
    case "Thursday":day="Thu"; break;
    case "Friday":day="Fri"; break;
    case "Saturday":day="Sat"; break;
    case "Sunday":day="Sun"; break;
    default: break;
  }
  switch(slot){
    case "Breakfast":slott="B"; break;
    case "Lunch":slott="L"; break;
    case "Dinner":slott="D"; break;
    default: break;
  }
  var s=slott+"_"+day+"1";
  //var popup = '<a data-rel="popup" href="#popupBasic">' + loc + '</a>';
  //alert(s);
  document.getElementById(s).style.color="White";
  document.getElementById(s).style.background="#2c2c2c";
  //document.getElementById(s).innerHTML=loc;
  //document.getElementById(s).innerHTML= popup;

  //$("#"+s).append("<h2>Welcome "+name+" !! </h2>");
}

function build(name,points,shiftList){
	
	//points=window.points;
	//alert(name+" "+points+ "");
	$("#welcomeName").html("");
	$("#welcomeName").append("<h2>Welcome "+name+" !! </h2>");
	$("#numShifts").html("");
	$("#numShifts").append("<h2> keep "+points+" shift/s </h2>");
	$("#first").html("");
	$("#first").append("<fieldset>");
	$("#first").append("<select name='shifts' id='shifts1'>");
	
	for(var i=0;i<shiftList.length; i++){
		var num=i+1;
		var string=shiftList[i][0]+" "+shiftList[i][1]+" "+shiftList[i][2];
		var s='<option value="'+num+'">'+string+'</option>';
		$("#shifts1").append(s);

	}
	$("#first").append("</select>");
	
	if(points>1){
		$("#second").html("");
	 	//$("#second").append("<fieldset class='ui-field-contain'>");
	 	$("#second").append("<select name='shifts' id='shifts2'>");
	 	for(var i=0;i<shiftList.length; i++){
	 		var num=i+1;
	 		var string=shiftList[i][0]+" "+shiftList[i][1]+" "+shiftList[i][2];
	 		var s='<option value="'+num+'">'+string+'</option>';
			$("#shifts2").append(s);
	 	}
		$("#second").append("</select>");
		
	}
	$("#second").append("</fieldset>");
}
