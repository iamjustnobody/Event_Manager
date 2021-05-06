package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	//	response.setContentType("text/html");
//		response.setContentType("application/json"); //1-4 b4 RpcHelper
//		PrintWriter out=response.getWriter();  //1-4 b4 RpcHelper
	//	String username=request.getParameter("username"); //1-3
//		if(username!=null) {  //1-3
	/*	out.print("<html><body>");
		out.print("<h1>hello JayChou & " + username + "</h1>");
		////out.print("<h1>hello JayChou & " + request.getParameter("username") + "</h1>");
		////out.print("<h1>hello JayChou & " + request + "</h1>"); ////out.print("<h1>hello JayChou</h1>");
		out.print("</body></html>");
		*/ //1
		/*
			  try { JSONObject obj=new JSONObject(username); out.print(obj); } catch
			  (JSONException e) { // TODO Auto-generated catch block 
			  e.printStackTrace();  }
			  }
			 
			//out.print(obj);
			  //nothing shown on http://localhost:8080/Jupiter/search?username=abcd
			 */ //2
			
			
	/*		  JSONObject obj=new JSONObject(); try { obj.put("username", "Jay"); } catch
			  (JSONException e) { // TODO Auto-generated catch block 
				  e.printStackTrace(); 
			  }
			  out.print(obj);
			 */ //3
		
	//	}
		
	/*	JSONArray array=new JSONArray();
		try {
			array.put(new JSONObject().put("username","abcd"));
			array.put(new JSONObject().put("username","1234"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.print(array);
		*/
//		out.close();
	//below after introducing RpcHelper
	/*	JSONArray array=new JSONArray();
		
		try {
			array.put(new JSONObject().put("name", "abcd").put("address", "san francisco").put("time", "01/01/2017"));
			
			array.put(new JSONObject().put("name", "1234").put("address", "san jose").put("time", "01/02/2017"));
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response, array);
		*/
		//below after introducing RpcHelper&& ticketmasterapi
	/*	double lat=Double.parseDouble(request.getParameter("lat"));
		double lon=Double.parseDouble(request.getParameter("lon"));
		TicketMasterAPI tmAPI=new TicketMasterAPI();
		List<Item> items=tmAPI.search(lat, lon, null);
		JSONArray array=new JSONArray();
		for(Item item:items) {
			array.put(item.toJSONObject());
		}
		RpcHelper.writeJsonArray(response, array);
		*/
		//below after DBConnection & MySQLConnection searchitem saveitem
		//below after introducing session
		// allow access only if session exists
				HttpSession session = request.getSession(false);
				if (session == null) {
					response.setStatus(403);
					return;
				}

				// optional
				String userId = session.getAttribute("user_id").toString(); 
				
		//below after DBConnection & MySQLConnection searchitem saveitem
			//below before introducing session	
		double lat=Double.parseDouble(request.getParameter("lat"));
		double lon=Double.parseDouble(request.getParameter("lon"));
		String term=request.getParameter("term");
		
//		String userId = request.getParameter("user_id");
				
		DBConnection connection=DBConnectionFactory.getConnection(); 
		try {
			//before introducing Session
	//	List<Item> items=connection.searchItems(lat, lon, term);  //tmAPI.search(lat, lon, term);
			//after introducing session
			List<Item> items=connection.searchItems(lat, lon, userId); //term
			
			Set<String> favouriteItemIds=connection.getFavouriteItemIds(userId); //after introducing session & userId
			
		JSONArray array=new JSONArray();
		for(Item item:items) {
		//	array.put(item.toJSONObject()); //before introducing session & userId
			JSONObject obj=item.toJSONObject();
			obj.put("favourite", favouriteItemIds.contains(item.getItemId()));
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		// optional
	//	String userId = session.getAttribute("user_id").toString(); 
		
		doGet(request, response);
	}

}
