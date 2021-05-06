package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	public static void writeJsonArray(HttpServletResponse response,JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out=response.getWriter();
		out.print(array);
		out.close();
		
	}
	public static void writeJsonObject(HttpServletResponse response,JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out=response.getWriter();
		out.print(obj);
		out.close();
	}

	//parse a JSONObject from http request
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sBuilder=new StringBuilder();
		
			try(BufferedReader reader=request.getReader()) {
			//	BufferedReader reader=request.getReader();
				String line=null;
				while((line=reader.readLine())!=null) {
					sBuilder.append(line);
				}
				return new JSONObject(sBuilder.toString());
						
			} catch (Exception e) { //not only IOException that is for reader
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    return new JSONObject();
		}
}
