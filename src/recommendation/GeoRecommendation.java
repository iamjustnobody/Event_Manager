package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {

	public List<Item> recommendations(String userId,double lat,double lon){
		List<Item> recommendedItems=new ArrayList<>();
		
		//get all fav itemids
		DBConnection connection=DBConnectionFactory.getConnection();
		Set<String> favouritedItemIds=connection.getFavouriteItemIds(userId);
		
		//get all categories and sort by count
		Map<String,Integer> allCategories=new HashMap<>();
		for(String itemId:favouritedItemIds) {
			Set<String> categories=connection.getCategories(itemId);
			for(String category:categories) {
				allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
			}
		}
		
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		

		// Step 3, search based on category, filter out favorite items //
		//search based on Map<String,Integer> allCategories
		
		Set<String> visitedItemIds = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = connection.searchItems(lat, lon, category.getKey());
			for(Item item:items) {
				if(!visitedItemIds.contains(item.getItemId())&&!favouritedItemIds.contains(item.getItemId())) {
			  //if(!visitedItemIds.contains(item.getItemId())) {
			      recommendedItems.add(item);
			      visitedItemIds.add(item.getItemId());
				}
			}
		}
		
		connection.close();
		return recommendedItems;
	}
}
