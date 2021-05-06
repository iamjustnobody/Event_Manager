package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import recommendation.GeoRecommendation;

/**
 * Servlet implementation class RecommendedItem
 */
@WebServlet("/recommendation")
public class RecommendedItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendedItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
/*		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		JSONArray array=new JSONArray();
		JSONObject obj1=new JSONObject();
		JSONObject obj2=new JSONObject();
		try {
			obj1.put("name", "abcd");
			obj1.put("address", "san francisco");
			obj1.put("time", "01/01/2017");
			obj2.put("name", "1234");
			obj2.put("address", "san jose");
			obj2.put("time", "01/02/2017");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		array.put(obj1);
		array.put(obj2);
		out.print(array);
		out.close();
		*/
		/*
		 * JSONArray array=new JSONArray(); try { array.put(new JSONObject().put("name",
		 * "abcd").put("address", "san francisco").put("time", "01/01/2017"));
		 * 
		 * array.put(new JSONObject().put("name", "1234").put("address",
		 * "san jose").put("time", "01/02/2017")); ; } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		
		HttpSession session=request.getSession(false);
		if(session==null) {
			response.setStatus(403);
			return;
		}
		
		String userId=session.getAttribute("user_id").toString();
		// String userId=request.getParameter("user_id");
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		GeoRecommendation recommendation = new GeoRecommendation();
		List<Item> items = recommendation.recommendations(userId, lat, lon);
		if(items==null||items.size()==0) {
			try {
				RpcHelper.writeJsonObject(response, new JSONObject().put("address", "san francisco"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
		JSONArray array = new JSONArray();
		for (Item item : items) {
			array.put(item.toJSONObject());
		}
		

		RpcHelper.writeJsonArray(response, array);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
