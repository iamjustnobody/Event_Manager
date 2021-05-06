package db;

import java.util.List;
import java.util.Set;

import entity.Item;
//constructor
public interface DBConnection {
	public void close();
	public void setFaviouriteItems(String userId, List<String> itemIds);
	public void unsetFaviouriteItems(String userId, List<String> itemIds);
	public Set<String> getFavouriteItemIds(String userId);
	public Set<Item> getFavouriteItems(String userId);
	public Set<String> getCategories(String itemId);
	public List<Item> searchItems(double lat,double lon,String term);
	public void saveItem(Item item);
	public String getFullname(String userId);
	public boolean verifyLogin(String userId,String password);
	
	public boolean registerUser(String userId,String password,String firstname,String lastname);

}
