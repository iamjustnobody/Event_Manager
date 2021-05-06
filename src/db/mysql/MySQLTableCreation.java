package db.mysql;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTableCreation {
//run as java app to reset db schema
	public static void main(String[] args) {
		
		//coonect to mysql
		System.out.println("Connecting to "+MySQLDBUtil.URL);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn=DriverManager.getConnection(MySQLDBUtil.URL);
			if(conn==null) {
				return;
			}
			//drop tables in case they exist
			Statement statement=conn.createStatement();
			String sql="DROP TABLE IF EXISTS categories";
			statement.executeUpdate(sql);
			sql="DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);
			sql="DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);
			sql="DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);
			//create new tables
			sql="CREATE TABLE items ("
					+"item_id VARCHAR(255) NOT NULL,"
					+"name VARCHAR(255),"
					+"rating FLOAT,"
					+"address VARCHAR(255),"
					+"image_url VARCHAR(255),"
					+"url VARCHAR(255),"
					+"DISTANCE FLOAT,"
					+"PRIMARY KEY (item_id)"
					+")";
			statement.executeUpdate(sql);
			sql="CREATE TABLE users ("
					+"user_id VARCHAR(255) NOT NULL,"
					+"PASSWORD VARCHAR(255) NOT NULL,"
					+"first_name VARCHAR(255),"
					+"last_name VARCHAR(255),"
					+"PRIMARY KEY (user_id)"
					+")";
			statement.executeUpdate(sql);
			sql="CREATE TABLE categories ("
					+"item_id VARCHAR(255) NOT NULL,"
					+"category VARCHAR(255) NOT NULL,"
					+"PRIMARY KEY (item_id,category),"
					+"FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+")";
			statement.executeUpdate(sql);
			sql="CREATE TABLE history ("
					+"user_id VARCHAR(255) NOT NULL,"
					+"item_id VARCHAR(255) NOT NULL,"
					+"last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+"PRIMARY KEY (user_id,item_id),"
					+"FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+"FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+")";
			statement.executeUpdate(sql);
			sql="INSERT INTO users VALUES('1111','3229c1097c00d497a0fd282d586be050','John','Smith')";
			statement.executeUpdate(sql);
			conn.close();
			System.out.println("Import done successfully");
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException | ClassNotFoundException
//				| SQLException e) {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
