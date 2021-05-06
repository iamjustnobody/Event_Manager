package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
//import java.net.*; //HttpURLConnection connection=(HttpURLConnection) new URL(url).openConnection();
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {

	private static final String URL="https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD="";
	private static final String API_KEY="aA6sE8upe93N4OHjqLv0K0tT7WiuAa1y";
	//public JSONArray search(double lat,double lon,String keyword) {
	public List<Item> search(double lat,double lon,String keyword) {
		if(keyword==null) {
			keyword=DEFAULT_KEYWORD;
		}
		
		try {
			keyword=URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String query=String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY,lat,lon,keyword,50);
		String url=URL+"?"+query;
		try {
	//	java.net.URL url2=new java.net.URL(url);
	//	URLConnection url2con=url2.openConnection();
	//	HttpURLConnection connection=(HttpURLConnection) url2con;
		HttpURLConnection connection=(HttpURLConnection) new java.net.URL(url).openConnection();
		
		connection.setRequestMethod("GET");
		int responseCode =connection.getResponseCode();
		System.out.println("sending request to url:"+url);
		System.out.println("response code::"+responseCode);
		if(responseCode!=200) {return new ArrayList<>();}//{return new JSONArray();}
		
		BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
		//String line;
		String line=reader.readLine();
		StringBuilder response=new StringBuilder();
		while(line!=null) {
			response.append(line);
			line=reader.readLine();
		}
	/*	while((line=reader.readLine())!=null) {
			response.append(line);
		}*/
		reader.close();
		System.out.println(response.length());
		JSONObject obj=new JSONObject(response.toString());
		
		if(!obj.isNull("_embedded")) {
			System.out.println("yes");
			JSONObject embedded=obj.getJSONObject("_embedded");
			//return embedded.getJSONArray("events");
			return getItemList(embedded.getJSONArray("events"));
		}else {
			System.out.println("no");}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<>(); //new JSONArray();
	}
	
	private void queryAPI(double lat,double lon) {
		List<Item> events=search(lat,lon,null); //JSONArray events=search(lat,lon,null);
	//	System.out.println("Hi"+events.length());
		try {
		for(int i=0;i<events.size();++i) { //for(Item event:events){ //i<events.length()
			Item event=events.get(i); //JSONObject event=events.getJSONObject(i);
		//	System.out.println("Hi");
			System.out.println(event.toJSONObject()); //System.out.println(event.toString(2));
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	   
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException{
		List<Item> itemList=new ArrayList<>();
		for(int i=0;i<events.length();i++) {
			JSONObject event=events.getJSONObject(i);
			ItemBuilder builder=new ItemBuilder();
			if(!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
            if(!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
            if(!event.isNull("url")) {
            	builder.setUrl(event.getString("url"));
			}
            if(!event.isNull("rating")) {
            	builder.setRating(event.getDouble("rating"));
			}
            if(!event.isNull("distance")) {
            	builder.setDistance(event.getDouble("distance"));
			}
            builder.setAddress(getAddress(event));
            builder.setImageUrl(getImageUrl(event));
            builder.setCategories(getCategories(event));
            itemList.add(builder.build());
		}
		return itemList;
	}
	private String getAddress(JSONObject event) throws JSONException {
		if(!event.isNull("_embedded")) {
			JSONObject embedded=event.getJSONObject("_embedded");
			if(!embedded.isNull("venues")) { //if(!embedded.isNull("event")) {
				JSONArray venues=embedded.getJSONArray("venues"); //JSONArray events=embedded.getJSONArray("events");
				//for(JSONObject venue:venues) { //for(JSONObject event:events) { //already "event" parameter
					for(int i=0;i<venues.length();i++) {
						JSONObject venue=venues.getJSONObject(i);
						StringBuilder addressBuilder =new StringBuilder();
						if(!venue.isNull("address")) {
							JSONObject address=venue.getJSONObject("address");
						//	StringBuilder addressBuilder =new StringBuilder();
							if(!address.isNull("line1")) {
								addressBuilder.append(address.getString("line1"));
							}
							if(!address.isNull("line2")) {
								addressBuilder.append(address.getString("line2"));
							}
							if(!address.isNull("line3")) {
								addressBuilder.append(address.getString("line3"));
							}
						}
						if(!venue.isNull("city")) {
							JSONObject city=venue.getJSONObject("city");
							if(!city.isNull("name")) {
								addressBuilder.append(", ");
								addressBuilder.append(city.getString("name"));
							}
						}
						String addressStr=addressBuilder.toString();
						if(!addressStr.equals("")) {
							return addressStr;
						}
				}
			}
		}
		return "";
	}
	
	private String getImageUrl(JSONObject event) throws JSONException {
		if(!event.isNull("images")) {
			JSONArray images=event.getJSONArray("images");
			for(int i=0;i<images.length();i++) {
				JSONObject image=images.getJSONObject(i);
				if(!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}
	
	private Set<String> getCategories(JSONObject event) throws JSONException{
		Set<String> categories=new HashSet<>();
		if(!event.isNull("classifications")) {
			JSONArray classifications=event.getJSONArray("classifications");
			for(int i=0;i<classifications.length();i++) {
				JSONObject classification=classifications.getJSONObject(i);
				if(!classification.isNull("segment")) {
					JSONObject segment=classification.getJSONObject("segment");
					if(!segment.isNull("name")) {
						String name=segment.getString("name");
						categories.add(name);
					}
				}
			}
		}
		return categories;
	}
	
	
	public static void main(String args[]) {
		TicketMasterAPI tmApi=new TicketMasterAPI();
		tmApi.queryAPI(51.8624, -2.2471);
	}
}
