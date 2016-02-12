import java.sql.*;
import java.math.*;
import java.io.*;
import oracle.jdbc.driver.*;
import java.util.*;

public class driver 
{
	
	public static void main(String[] args) 
	{
		Scanner scan = new Scanner(System.in);
		
	    try
	    {
	      
	      System.out.println("Enter username");
	      String username = scan.nextLine();
	      System.out.println("Enter password");
	      String password = scan.nextLine();
	    	
	      // Load the database driver
	  	  Class.forName( "oracle.jdbc.driver.OracleDriver" ) ;

	  	  // Get a connection to the database
	  	  Connection conn = DriverManager.getConnection( "jdbc:oracle:thin:@apollo.vse.gmu.edu:1521:ite10g", username, password );
	    	
	  	  // Print all warnings
		  for( SQLWarning warn = conn.getWarnings(); warn != null; warn = warn.getNextWarning() )
		  {
			  System.out.println( "SQL Warning:" ) ;
			  System.out.println( "State  : " + warn.getSQLState()  ) ;
			  System.out.println( "Message: " + warn.getMessage()   ) ;
			  System.out.println( "Error  : " + warn.getErrorCode() ) ;
		  }	
		  
		  
		  boolean done = false; //assume user wants to always do at least 1 thing
		  int option;
		  String input;
		  DatabaseMetaData meta = conn.getMetaData();
		  //main loop for programs
		  while(!done)
		  {
			  printMenu();
			  option = scan.nextInt();
			  scan.nextLine(); //get rid of newline
			  if(option < 1 || option > 7)
				  continue;
			  if(option == 1)
			  {
				  printRelations();
				  input = scan.nextLine();  
				  viewTable(conn, input);	   	  
			  }
			  else if(option == 2)
			  {
				  printRelations();
				  input = scan.nextLine();
				  insertRecord(scan, conn, input, meta);
			  }
			  else if(option == 3)
			  {
				  printRelations();
				  input = scan.nextLine();
				  updateRecord(scan, conn, input, meta);	
			  }
			  else if(option == 4)
			  {
				  printRelations();
				  input = scan.nextLine();
				  deleteRecord(scan, conn, input, meta);
			  }
			  else if(option == 5)
				  searchForMovie(scan, conn);
			  else if(option == 6)
			  {
				  showProfileInfo(scan, conn);		 	  
			  }
			  else
				  done = true;
				 
			  
		  }
	    	
	    	
	    }
		catch(SQLException e)
		{
			System.out.println( "SQL Exception:" ) ;

			  // Loop through the SQL Exceptions
			  while(e != null )
			  {
				  System.out.println( "State  : " + e.getSQLState()  ) ;
				  System.out.println( "Message: " + e.getMessage()   ) ;
				  System.out.println( "Error  : " + e.getErrorCode() ) ;

				  e = e.getNextException() ;
			  }
			
		}
		catch(Exception e)
	    {
			System.out.println( e ) ;
	    }
		finally
		{
			scan.close();
		}
	    
	}
	
	
	
	
	public static void printMenu()
	{
		System.out.println("1: View table content");
		System.out.println("2: Insert new record");
		System.out.println("3: Update record");
		System.out.println("4: Delete record");
		System.out.println("5: Search for movies");
		System.out.println("6: Show information for members profile");
		System.out.println("7: exit");
	}
	
	public static void printRelations()
	{

		  System.out.println("Account");
		  System.out.println("Profile");
		  System.out.println("Credit_Card");
		  System.out.println("Rental_History");
		  System.out.println("Movie");
		  System.out.println("Actor");
		  System.out.println("Played_In");
		  System.out.println("Genre");
		  System.out.println("Pref_Genre");
		  System.out.println("Mov_Genre");
	}
	
	public static void viewTable(Connection conn, String relation) throws SQLException
	{	   
	      String sql = "SELECT * FROM " + relation;	
	      
	      PreparedStatement stmt = conn.prepareStatement(sql);
	      //stmt.setString(1, relation);

	      ResultSet rs = stmt.executeQuery(sql);

	      ResultSetMetaData rsmd = rs.getMetaData();
	      int columns = rsmd.getColumnCount();
	      
	      while(rs.next())  //for each tuple
	      {
	    	  String temp = "";
	    	  for(int i = 1; i < (columns+1); i++)  //for each column 
	    	      temp += rs.getString(i) + " ";
	    	  System.out.println(temp);
	      }
	      System.out.println();
		  
		  rs.close();
		  stmt.close();
		
	}
	
	public static void insertRecord(Scanner scan, Connection conn, String relation, DatabaseMetaData meta) throws SQLException
	{
		  relation = relation.toUpperCase();
          ResultSet rs = meta.getColumns(null,null,relation,null);	
          String attributes = "";
          int i = 1;
          String questionMarks = "";
          ArrayList<String> attributeTypes = new ArrayList<String>();
          ArrayList<String> attributeNames = new ArrayList<String>();
          while(rs.next())
          {
        	  if(i != 1)
        		  questionMarks += ", ";
        	  attributeTypes.add(rs.getString("TYPE_NAME"));
        	  if(i != 1)
        	      attributes += ", ";
        	  attributes += rs.getString("COLUMN_NAME");
        	  attributeNames.add(rs.getString("COLUMN_NAME"));
        	  
        	  i++;
        	  questionMarks += "?";
          }
          
          //i = numberOfattributes + 1, so subtract 1;
          //i--;
          if(i == 1)
          {
        	  System.out.println("Invalid table");
        	  return;
          }
         
		  String sql = "INSERT INTO " + relation + " VALUES(" + questionMarks + ")";  
		  PreparedStatement stmt = conn.prepareStatement(sql);
		  
		  System.out.println("Attributes are: " + attributes);
		  
		  //stmt.setString(1, relation);  //set the attributes
		  for(int j = 1; j < i; j++)
		  {
			  System.out.println("Enter: " + attributeNames.get(j-1));
			      stmt.setString(j, scan.nextLine());
	
		  }
		  
	      
	      stmt.executeUpdate();
	      
	      rs.close();
	      stmt.close();
	     
	      return;
	      	
	}
	
	public static void updateRecord(Scanner scan, Connection conn, String relation, DatabaseMetaData meta) throws SQLException
	{
		relation = relation.toUpperCase();
        ResultSet rs = meta.getColumns(null,null,relation,null);	
        //String attributes = "";
        int i = 1;
       // String questionMarks = "";
        ArrayList<String> attributeTypes = new ArrayList<String>();
        ArrayList<String> attributeNames = new ArrayList<String>();
        ArrayList<Integer> setList = new ArrayList<Integer>();
        ArrayList<Integer> whereList = new ArrayList<Integer>();
        String set = "", where = "", setQuestionMarks = "", whereQuestionMarks = "";
        while(rs.next())
        {
      	  /*if(i != 1)
      	      attributes += ", ";
      	  attributes += rs.getString("COLUMN_NAME");*/
      	  
      	  attributeTypes.add(rs.getString("TYPE_NAME"));
      	  attributeNames.add(rs.getString("COLUMN_NAME"));
      	  
      	  i++;
      	  //questionMarks += "?";
        }
        
        if(i == 1)
        {
      	  System.out.println("Invalid table");
      	  return;
        }
        
        //go through all the attributes, and see which ones the user wants to change and which ones to select tuples based on.
        String input;
        for(int j = 1; j < i; j++)
        {
        	System.out.println("Do you want to modify attribute: " + attributeNames.get(j-1) + " (yes OR no)");
        	input = scan.nextLine();
        	if(input.equals("yes"))
        	{
        		if(set != "")
        			set += ", ";
        		set += attributeNames.get(j-1) + "= ?";    
        		setList.add(j-1);
        		
        	}
        	System.out.println("Do you want select tuples to change based on attribute: " + attributeNames.get(j-1) + " (yes OR no)");
        	input = scan.nextLine();
        	if(input.equals("yes"))
        	{
        		if(where != "")
        		    where += "AND ";
        		where += attributeNames.get(j-1) + "= ?";
        		whereList.add(j-1);
        		
        	}
        }
		
		
		 String sql = "UPDATE " + relation + " SET " + set + " WHERE " + where;
		 PreparedStatement stmt = conn.prepareStatement(sql);
		 
		 //fill question marks for SET
		 System.out.println("Enter values for new attribute values");
		 for(int j = 0; j < setList.size(); j++)
	     {
			System.out.println("what value for: " + attributeNames.get(setList.get(j)));
			stmt.setString(j+1, scan.nextLine());		
	     }
		 
		 //fill question marks for WHERE
		 System.out.println("Enter values for selecting tuples to change");
		 for(int j = 0; j < whereList.size(); j++)
	     {
			System.out.println("what value for: " + attributeNames.get(whereList.get(j)));
			stmt.setString(j+1+setList.size(), scan.nextLine());		
	     }
		
		 stmt.executeUpdate();
		      
         rs.close();
         stmt.close();
   
         return;
		      	
		
	}
    
	public static void deleteRecord(Scanner scan, Connection conn, String relation, DatabaseMetaData meta) throws SQLException
	{ 
		relation = relation.toUpperCase();
        ResultSet rs = meta.getColumns(null,null,relation,null);	
    
        int i = 1;
        String input;
        ArrayList<String> attributeTypes = new ArrayList<String>();
        ArrayList<String> attributeNames = new ArrayList<String>();
        ArrayList<Integer> whereList = new ArrayList<Integer>();
        String where = "";
        while(rs.next())
        {
    
      	  attributeTypes.add(rs.getString("TYPE_NAME"));
      	  attributeNames.add(rs.getString("COLUMN_NAME"));
      	  
      	  i++;

        }
		if(i == 1)
		{
			System.out.println("Invalid table");
			return;
		}
		
		//ask for each attribute if the user wants to use it to determine which tuple(s) to delete
		for(int j = 1; j < i; j++)
        {
		    System.out.println("Do you want select tuples to delete based on attribute: " + attributeNames.get(j-1) + " (yes OR no)");
        	input = scan.nextLine();
        	if(input.equals("yes"))
        	{
        		if(where != "")
        		    where += "AND ";
        		where += attributeNames.get(j-1) + "= ?";
        		whereList.add(j-1);	
        	}
        }
		
		String sql = "DELETE FROM " + relation + " WHERE " + where;
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		//fill question marks for WHERE
		System.out.println("Enter values for selecting tuples to delete");
		for(int j = 0; j < whereList.size(); j++)
	    {
	    	System.out.println("what value for: " + attributeNames.get(whereList.get(j)));
			stmt.setString(j+1, scan.nextLine());		
	    }
		
		stmt.executeUpdate();
	      
        rs.close();
        stmt.close();
   
        return;
		
		
	}
	
	public static void searchForMovie(Scanner scan, Connection conn) throws SQLException
	{
		String input, sql;
		System.out.println("search by movie or actor?");
		input = scan.nextLine();
		if(input.toLowerCase().equals("movie"))
		{
			System.out.println("enter movie name:");
			input = scan.nextLine();
			sql = "SELECT m_name, year, av_rating FROM Movie WHERE m_name = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, input);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				System.out.println("Movie: " + rs.getString(1) + " (" + rs.getString(2) + "), rating: " + rs.getString(3));
			}
			rs.close();
			stmt.close();
			return;
		}
		else if(input.toLowerCase().equals("actor"))
		{
			String firstName,lastName;
			System.out.println("enter actors first name");
			firstName = scan.nextLine();
			System.out.println("enter actors last name");
			lastName = scan.nextLine();
			
			sql = "SELECT UNIQUE m_name, year, av_rating FROM movie, played_in, Actor WHERE  movie.m_id = played_in.m_id AND played_in.actor_ID = Actor.actor_ID AND (Actor.first_name = ? OR Actor.last_name = ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, firstName);
			stmt.setString(2, lastName);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				System.out.println("Movie: " + rs.getString(1) + " (" + rs.getString(2) + "), rating: " + rs.getString(3));
			}
			rs.close();
			stmt.close();
			return;
		}
		else
		{
			System.out.println("Invalid Option");
			return;
		}
		
	}
	
	public static void showProfileInfo(Scanner scan, Connection conn) throws SQLException
	{
		String sql = "SELECT m_name, year, rating FROM Rental_History NATURAL JOIN Movie WHERE A_ID = ? AND profile_name = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		System.out.println("Enter A_ID");
		stmt.setString(1, scan.nextLine());
		System.out.println("Enter profile_name");
		stmt.setString(2, scan.nextLine());
		
		ResultSet rs = stmt.executeQuery();
		System.out.println("Watch history");
		while(rs.next())
		{
			System.out.println("Movie: " + rs.getString(1) + " (" + rs.getString(2) + "), rating: " + rs.getString(3));
		}
		System.out.println();
		rs.close();
		stmt.close();
		return;
		
	}
	
	
	
}
