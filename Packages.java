import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class Packages {
	
	
	 public static void deletePackage(Statement statement) throws SQLException {
	    	ResultSet answer = null;
			Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    	System.out.println("Which package do you want to delete?");
		    String delPack = scan.nextLine();
		    if(checkDatesPackage(delPack,answer,statement)) {
		    	if(checkRegistration(delPack,answer,statement))
		    		deleteWhole(delPack,statement);
		    	else
			    	System.out.println("People are currently enrolled in that class");


		    }
		    else
		    	System.out.println("Package currenlty has classes which are ongoing");
		    
		    scan.close();

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
			if(answer.getInt(1)!=0) 
				return false;
			else 
				return true;
			
		}

		
	    private static boolean checkDatesPackage(String Package,ResultSet answer,Statement statement) throws SQLException {
			boolean valid = true;
			String query1 = "(SELECT Course_ID FROM LEXC.COURSEPACKAGE"
					+ "WHERE Package_id = %s";
			query1 =String.format(query1,Package);
			answer = statement.executeQuery(query1);
			while (answer.next()) {
				valid = checkDates(answer.getString(1),answer,statement);
				if(valid == false) {
					break;
				}
			}
			return valid;

		}

		private static boolean checkDates(String course,ResultSet answer,Statement statement) throws SQLException {
			long millis=System.currentTimeMillis(); 
			Date curDate = new Date(millis);
			String query1 = "SELECT End_Date FROM LEXC.COURSE"
		    				+ "WHERE course_id = %s";
			query1 =String.format(query1,course);
			statement.executeQuery(query1);
			Date endDate = answer.getDate(1);
			return curDate.before(endDate);

		}

	    public static void updatePackage(Statement statement) throws SQLException {
			Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    	ResultSet answer = null;
	    	String query1 = "SELECT Course_ID,Name FROM lexc.Package";

			System.out.println("Avaiable packages.....");
			answer = statement.executeQuery(query1);
			while (answer.next()) {
				System.out.println(answer.getString(1));
			}
	        System.out.println("Please select a Package you would want to change");
		    String result2 = scan.nextLine();
		    if(checkDates(result2,answer,statement)) {
		    	if(checkRegistration(result2,answer,statement)) {

			    	System.out.println("Do you want to delete or add onto this package?");
			    	String result3 = scan.nextLine();
			    	if(result3.toLowerCase()=="add") {
			    		String query2 = "SELECT Course_ID,Name FROM lexc.Course"
			    				+ " WHERE End_Date > GETDATE()";
			    		answer = statement.executeQuery(query2);
			    		System.out.println("Avaiable courses to add to the Package....");
			    		while (answer.next()) {
			    			System.out.println(answer.getString(1)+"\t"+answer.getString(2) );
			    		}
			    		System.out.println("Enter CourseID which you would like to add?");
				    	String result4 = scan.nextLine();
				    	updateHelper(result2,result4,"add",answer,statement);
				    	
			    	}
			    	else if (result3.toLowerCase() == "delete"){
			    		System.out.println("Avaiable courses to delete to the Package....");
			    		while (answer.next()) {
			    			System.out.println(answer.getString(1));
			    		}
			    		System.out.println("Which do you want to delete?");
				    	String result4 = scan.nextLine();
				    	updateHelper(result2,result4,"delete",answer,statement);
			    	}
		    	}
		    	else
			    	System.out.println("People are currently enrolled in this package");

		    	
		    }
		    else
		    	System.out.println("You cannot change this package because all classes involved are terminated");

		    scan.close();
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

		public static void addPackage(Statement statement) throws SQLException {
	    	ResultSet answer = null;
	    	String query1 = "SELECT Course_ID,Name FROM lexc.Course"
					+ " WHERE End_Date > GETDATE()";
	    	answer = statement.executeQuery(query1);
			System.out.println("Avaiable courses to build a Package");
			while (answer.next()) {
				System.out.println(answer.getString(1) + "\t"
	                    + answer.getString(2));
			}
			Scanner scan = new Scanner(System.in);  
	        System.out.println("Please enter Course IDs you would want to package together sepearted by commas");
		    String results = scan.nextLine();
		    String[] resultsArr = results.split(",");
		    
		    String query2 = "SELECT COUNT(*) FROM lexc.CoursePackage";
		    answer = statement.executeQuery(query2);
			int newID = Integer.valueOf(answer.getString(1))-1;
		    for (int i = 0; i< resultsArr.length;i++) {
			    String query3 = "INSERT	INTO lexc.CoursePackage(Package_ID,Course_ID) "
			    				+ "VALUES (%s,%s)";
				query3 =String.format(query3,newID, resultsArr[i]);
				statement.executeQuery(query3);
			    newID = newID + 1;
		    }
		    System.out.println("What do you want the title of the package to be?");
		    String title = scan.nextLine();
		    System.out.println("What do you want the price of the package to be?");
		    int price = scan.nextInt();
		    
		    String query4 = "SELECT COUNT(*) FROM lexc.Package";
		    answer = statement.executeQuery(query4);
			int newID2 = Integer.valueOf(answer.getString(1))-1;
			String query5;
		    for (int i = 0; i< resultsArr.length;i++) {
			    query5 = "INSERT INTO Package(Package_ID,Name,Price) VALUES "
			    				+ "VALUES (%s,'%s',%s)";
			    query5 =String.format(query5,newID2, title,price);
			    statement.executeQuery(query5);
			    newID2 = newID2 + 1;
		    }
		    scan.close();

	    }

}
