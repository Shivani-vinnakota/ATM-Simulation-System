package com.centum.atm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection 
{
   private static Connection conn;
   
   private MyConnection() {}
   
   public static Connection getConnection() throws ClassNotFoundException, SQLException
   {
	   if(conn==null) 
	   {
		 Class.forName("com.mysql.cj.jdbc.Driver"); 
		 conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm","root","Shivani369$");  
	   }
	return conn;
	   
   }
}
