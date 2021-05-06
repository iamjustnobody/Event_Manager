package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {

	private Connection conn;
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn=DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
       if(conn!=null) {
    	   try {
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }
	}

	@Override
	public void setFaviouriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
          if(conn==null) {
        	  System.err.println("DB connection failed");
        	  return;
          }
          try {
        	  String sql="INSERT IGNORE INTO history(user_id,item_id) VALUES (?,?)";
        	  PreparedStatement ps=conn.prepareStatement(sql);
        	  ps.setString(1, userId);
        	  for(String itemId:itemIds) {
        		  ps.setString(2, itemId);
        		  ps.execute();
        		  }
          }catch(Exception e) {
        	  e.printStackTrace();
          }
	}

	@Override
	public void unsetFaviouriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if(conn==null) {
      	  System.err.println("DB connection failed");
      	  return;
        }
        try {
      	  String sql="DELETE FROM history WHERE user_id = ? AND item_id = ?";
      	  PreparedStatement ps=conn.prepareStatement(sql);
      	  ps.setString(1, userId);
      	  for(String itemId:itemIds) {
      		  ps.setString(2, itemId);
      		  ps.execute();
      		  }
        }catch(Exception e) {
      	  e.printStackTrace();
        }
	}

	@Override
	public Set<String> getFavouriteItemIds(String userId) {
		// TODO Auto-generated method stub
		if(conn==null) {return new HashSet<>();}
		Set<String> favouriteItems=new HashSet<>();
		String sql="SELECT item_id FROM history WHERE user_id=?";
	    PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1,userId);
		    ResultSet rs=stmt.executeQuery();
		    while(rs.next()) {
		    	String itemId=rs.getString("item_id");
		    	favouriteItems.add(itemId);
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return favouriteItems;
	}

	@Override
	public Set<Item> getFavouriteItems(String userId) {
		// TODO Auto-generated method stub
		if(conn==null) {return new HashSet<>();}
		Set<Item> favouriteItems=new HashSet<>();
		Set<String> itemIds=getFavouriteItemIds(userId);//public //no instantiation
		String sql = "SELECT * FROM items WHERE item_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(String itemId:itemIds) {
				stmt.setString(1,itemId);
				ResultSet rs=stmt.executeQuery();
				ItemBuilder itemBuilder=new ItemBuilder();
				while(rs.next()) {
					itemBuilder.setItemId(rs.getString("item_id"));//itemBuilder.setItemId(itemId);
					itemBuilder.setName(rs.getString("name"));
					itemBuilder.setAddress(rs.getString("address"));
					itemBuilder.setImageUrl(rs.getString("image_url"));
					itemBuilder.setUrl(rs.getString("url"));
					itemBuilder.setCategories(getCategories(itemId));//!
					itemBuilder.setDistance(rs.getDouble("distance"));
					itemBuilder.setRating(rs.getDouble("rating"));
					favouriteItems.add(itemBuilder.build());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return favouriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		if(conn==null) {return new HashSet<>();}
		Set<String> categories=new HashSet<>();
		String sql="SELECT category FROM categories WHERE item_id=?";
	    PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1,itemId);
		    ResultSet rs=stmt.executeQuery();
		    while(rs.next()) {
		    	String category=rs.getString("category");
		    	categories.add(category);
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	    
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// TODO Auto-generated method stub
		TicketMasterAPI ticketMasterAPI=new TicketMasterAPI();
		List<Item> items=ticketMasterAPI.search(lat, lon, term);
		for(Item item:items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		// TODO Auto-generated method stub
       if(conn==null) {
    	   System.err.println("DB connection failed");
    	   return;
       }
       try {
    	   //String sql="INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?)";
    	   String sql="INSERT INTO items VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?,rating=?,address=?,image_url=?,url=?,DISTANCE=?";
    	   PreparedStatement ps=conn.prepareStatement(sql);
    	   ps.setString(1, item.getItemId());
    	   ps.setString(2, item.getName());
    	   ps.setDouble(3, item.getRating());
    	   ps.setString(4, item.getAddress());
    	   ps.setString(5, item.getImageUrl());
    	   ps.setString(6, item.getUrl());
    	   ps.setDouble(7, item.getDistance());
    	   
    	   ps.setString(8, item.getName());
    	   ps.setDouble(9, item.getRating());
    	   ps.setString(10, item.getAddress());
    	   ps.setString(11, item.getImageUrl());
    	   ps.setString(12, item.getUrl());
    	   ps.setDouble(13, item.getDistance());
    	   ps.execute();
    	   
    	   sql="INSERT IGNORE INTO categories VALUES (?,?)";
    	   ps=conn.prepareStatement(sql);
    	   ps.setString(1, item.getItemId());
    	   for(String category:item.getCategories()) {
    		   ps.setString(2, category);
    		   ps.execute();
    	   }
       }catch(Exception e) {
    	   e.printStackTrace();
       }
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return "";
		}
		
		String name = "";
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return name;

	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return false;
		}
	
		try {
			String sql="SELECT * FROM users WHERE user_id = ? AND password = ? ";
			PreparedStatement statement=conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs=statement.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

	
		return false;

	}
	
	public boolean registerUser(String userId,String password,String firstname,String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			String sql="INSERT IGNORE INTO users VALUES(?,?,?,?)";
			PreparedStatement ps=conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate()==1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
