package de.uni_koblenz.schemex.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DB-connection class
 * 
 * @author Mathias
 *
 */
public class DBConnect {

	private Statement state;
	private Connection cn;
		
	/**
	 * Connect to the database
	 * 
	 * @param db_host Hostname / ip-address of MySQL-server
	 * @param db_name database name
	 * @param db_user database username
	 * @param db_password database password
	 */
	public DBConnect(String db_host, String db_name, String db_user, String db_password)
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver"); //Or any other driver
		}
		catch(Exception x)
		{
			System.out.println("Unable to load the driver class!");
		}

		try{
			cn = DriverManager.getConnection("jdbc:mysql://"+db_host+"/"+db_name, db_user, db_password);
			state = cn.createStatement();
		}
		catch (Exception e) {
			System.out.println("Unable to connect to database!");
			// TODO: handle exception
		}
	}
	
	
	/**
	 * Close the database connection
	 */
	public void close(){
		try {
			cn.close();
		} catch (SQLException e) {
			System.out.println("Unable to close connection.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the current database state
	 * @return
	 */
	public Statement getStatement()
	{
		return this.state;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBConnect ct = new DBConnect("mysqlhost.uni-koblenz.de","master","mkonrath","schemex");
		ResultSet rs=null;
		try {
			rs = ct.getStatement().executeQuery("SELECT count(*) FROM class_cluster");
				while(rs.next())
				{
					System.out.println(rs.getInt(1));
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs = ct.getStatement().executeQuery("SELECT count(*) FROM class_cluster");
				while(rs.next())
				{
					System.out.println(rs.getInt(1));
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}


