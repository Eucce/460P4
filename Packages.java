import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.Connection;



public class Packages {
	
	
	 public static void deletePackage(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
	    	ResultSet answer = null;
			Scanner scan = new Scanner(System.in);  
	    	System.out.println("Which package do you want to delete?");
		String query1 = "SELECT Package_ID,Name FROM lexc.Package";

			answer = statement.executeQuery(query1);
			while (answer.next()) {
				System.out.println(answer.getString(1));
			}

		    String delPack = scan.nextLine();
	    	    ResultSet answer2 = null;

		    if(checkDatesPackage(delPack,connection)) {
		    	if(checkRegistration(delPack,answer2,statement))
		    		deleteWhole(delPack,statement);
		    	else
			    	System.out.println("People are currently enrolled in that class");


		    }
		    else
		    	System.out.println("Package currenlty has classes which are ongoing");
		    
		    scan.close();
		    statement.close();
            	    connection.close();

	    }
	    private static void deleteWhole(String Package,Statement statement)throws SQLException {
			// TODO Auto-generated method stub
			String query = "DELETE FROM lexc.CoursePackage WHERE Package_ID = %s";
			query =String.format(query,Package);
			statement.executeQuery(query);
			
	    	String query2 = "DELETE FROM lexc.Package WHERE Package_ID = %s";
			query2 = String.format(query2,Package);
			statement.executeQuery(query2);
			
			
		}
	    private static boolean checkRegistration(String Package,ResultSet answer,Statement statement) throws SQLException {
			String query1 = "SELECT COUNT(*) FROM lexc.Subscription"
					+ " WHERE Package_ID = %s";
			query1 =String.format(query1,Package);
			answer = statement.executeQuery(query1);
			answer.next();
			if(answer.getInt(1)!=0) 
				return false;
			else 
				return true;
			
		}

		
	    private static boolean checkDatesPackage(String Package,Connection connection) throws SQLException {
	    	     	Statement statement = connection.createStatement();
			ResultSet answer = null;
			boolean valid = true;
			String query1 = "SELECT Course_ID FROM LEXC.COURSEPACKAGE "
					+ "WHERE Package_id=%s";
			query1 = String.format(query1,Package);
			answer = statement.executeQuery(query1);
			System.out.println(query1);
 			if (answer != null) {
				while (answer.next()) {
					valid = checkDates(answer.getString(1),answer,statement);
					if(valid == false) {
						break;
					}
				}
			}
			else
				System.out.println("Error printing the members enrolled in course " + Package);
			return valid;

		}

		private static boolean checkDates(String course,ResultSet answer,Statement statement) throws SQLException {
			long millis=System.currentTimeMillis(); 
			Date curDate = new Date(millis);
			String query1 = "SELECT End_Date FROM LEXC.COURSE "
		    				+ "WHERE course_id = %s";
			query1 =String.format(query1,course);
			answer = statement.executeQuery(query1);
			answer.next();
			Date endDate = answer.getDate(1);
			return curDate.before(endDate);

		}

	    public static void updatePackage(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();

			Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    	ResultSet answer = null;
	    	String query1 = "SELECT Package_ID,Name FROM lexc.Package";

			System.out.println("Avaiable packages.....");
			answer = statement.executeQuery(query1);
			while (answer.next()) {
				System.out.println(answer.getString(1));
			}
	        System.out.println("Please select a Package you would want to change");
		    String result2 = scan.nextLine();
		    if(checkDatesPackage(result2,connection)) {
		    	if(checkRegistration(result2,answer,statement)) {
				String query2 = "SELECT Course_ID,Name FROM lexc.Course"
			    				+ " WHERE End_Date > SYSDATE";
			    	answer = statement.executeQuery(query2);
		    		answer.next();
			    	System.out.println("Avaiable courses to add/delete to the Package....");
			    	while (answer.next()) {
			    		System.out.println(answer.getString(1)+"\t"+answer.getString(2) );
			    	}
			    	System.out.println("Enter CourseID which you would like to add/delete?");
				    String result4 = scan.nextLine();



			    	System.out.println("Do you want to delete or add onto this package?");
			    	String result3 = scan.nextLine();
			    	if(result3.toLowerCase()=="add") {
				    	updateHelper(result2,result4,"add",answer,statement);
				    	
			    	}
			    	else if (result3.toLowerCase() == "delete"){
			    		updateHelper(result2,result4,"delete",answer,statement);
			    	}
		    	}
		    	else
			    	System.out.println("People are currently enrolled in this package");

		    	
		    }
		    else
		    	System.out.println("You cannot change this package because all classes involved are terminated");

		    scan.close();
		    statement.close();
            	    connection.close();

	    }

	    private static void updateHelper(String packageID, String courseID, String str, ResultSet answer,Statement statement) throws SQLException {
			// TODO Auto-generated method stub
			if(str.equals("add")) {
				String query1 = "INSERT INTO CoursePackage(Package_ID,Course_ID) VALUES "
						+ "VALUES (%s,'%s')";
				query1 =String.format(query1,packageID,courseID);
		    	statement.executeQuery(query1);
			}
			else {
				String query1 = "DELETE FROM lexc.CoursePackage WHERE Package_ID = %s AND Course_ID = %s";
				query1 =String.format(query1,packageID,courseID);
		    	statement.executeQuery(query1);
				
			}
		}

	public static void addPackage(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();

	    	ResultSet answer = null;
		Scanner scan = new Scanner(System.in);  

 		  System.out.println("What do you want the title of the package to be?");
		    String title = scan.nextLine();
		    System.out.println("What do you want the price of the package to be?");
		    int price = scan.nextInt();
		    scan.nextLine(); 

		    String query4 = "SELECT COUNT(*) FROM lexc.Package";
		    answer = statement.executeQuery(query4);
		    answer.next();
			int newID = Integer.valueOf(answer.getString(1))-1;
			String query5;
			  query5 = "INSERT INTO Package(Package_ID,Name,Price) "
			    				+ "VALUES (%s,'%s',%s)";
			   query5 =String.format(query5,newID, title,price);
			   statement.executeQuery(query5);
		    


	    	String query1 = "SELECT Course_ID,Name FROM lexc.Course"
					+ " WHERE End_Date > SYSDATE";
	    	answer = statement.executeQuery(query1);
			System.out.println("Avaiable courses to build a Package");
			while (answer.next()) {
				System.out.println(answer.getString(1) + "\t"
	                    + answer.getString(2));
			}
	        System.out.println("Please enter Course IDs you would want to package together sepearted by commas");
		    String results = scan.nextLine();
		    String[] resultsArr = results.split(",");
		    
		    for (int i = 0; i< resultsArr.length;i++) {
			    String query3 = "INSERT INTO lexc.CoursePackage(Package_ID,Course_ID) "
			    				+ "VALUES (%s,%s)";
				query3 =String.format(query3,newID, resultsArr[i]);
				statement.executeQuery(query3);
		    }
		   
		    scan.close();
		    statement.close();
            	    connection.close();


	    }

}
