/*
 * Prog4.java - Allows for various operations on an Oracle
 * SQL database regarding the various information used for a sports center.
 *
 *
 */

import java.sql.*;
import java.util.Scanner;

public class Prog4 {
    private static Statement statement = null; // The one statement we use for JDBC interaction.

    private static void setupJDBC(String username, String password) {
        final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  " + "Error loading Oracle JDBC driver.  \n" + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        }

        Connection dbconn = null;

        try {
            dbconn = DriverManager.getConnection(oracleURL, username, password);

        } catch (SQLException e) {
            System.err.println("*** SQLException:  " + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        try {
            statement = dbconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            setupJDBC(args[0], args[1]);
        } else {
            System.out.println("\nUsage:  java JDBC <username> <password>\n" + "    where <username> is your Oracle DBMS" + " username,\n    and <password> is your Oracle" + " password (not your system password).\n");
            System.exit(-1);
        }


        System.out.println("Welcome to Program 4.");
        System.out.println("What would you like to do?");
        System.out.println("1. Add or Delete a Member");
        System.out.println("2. Add or Delete a Course");
        System.out.println("3. Add, Update, or Delete a Package");
        System.out.println("4. View Indebted Members (query 1)");
        System.out.println("5. View Member Schedule (query 2)");
        System.out.println("6. View Trainer Hours (query 3)");
        System.out.println("7. Mystery Query (TODO: query 4)");
        System.out.println("You may also type 'exit' to quit.");

        Scanner userScanner = new Scanner(System.in);
        boolean validInput = false;

        while (!validInput) {
            String response = userScanner.next();
            validInput = true;
            if (response.equalsIgnoreCase("1")) {
                System.out.println("Would you like to ADD or DELETE a member?");
                response = userScanner.next();
                if (response.equalsIgnoreCase("add")) {
                    addMember();
                } else if (response.equalsIgnoreCase("delete")) {
                    deleteMember();
                } else {
                    System.out.println("Invalid input. Please try again.");
                    validInput = false;
                }
            } else if (response.equalsIgnoreCase("2")) {
                System.out.println("Would you like to ADD or DELETE a course?");
                response = userScanner.next();
                if (response.equalsIgnoreCase("add")) {
                    addCourse();
                } else if (response.equalsIgnoreCase("delete")) {
                    deleteCourse();
                } else {
                    System.out.println("Invalid input. Please try again.");
                    validInput = false;
                }
            } else if (response.equalsIgnoreCase("3")) {
                System.out.println("Would you like to ADD, UPDATE, or DELETE a package?");
                response = userScanner.next();
                if (response.equalsIgnoreCase("add")) {
                    addPackage();
                } else if (response.equalsIgnoreCase("update")) {
                    updatePackage();
                } else if (response.equalsIgnoreCase("delete")) {
                    deletePackage();
                } else {
                    System.out.println("Invalid input. Please try again.");
                    validInput = false;
                }
            } else if (response.equalsIgnoreCase("4")) {
                queryOne();
            } else if (response.equalsIgnoreCase("5")) {
                queryTwo();
            } else if (response.equalsIgnoreCase("6")) {
                queryThree();
            } else if (response.equalsIgnoreCase("7")) {
                queryFour();
            } else if (response.equalsIgnoreCase("exit")) {
                System.out.println("Thanks for using Program 4.");
                System.exit(0);
            } else {
                validInput = false;
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void queryFour() {

    }

    private static void queryThree() {

    }

    private static void queryTwo() {

    }

    private static void queryOne() {

    }

    private static void deletePackage() {

    }

    private static void updatePackage() {

    }

    private static void addPackage() {

    }

    private static void deleteCourse() {

    }

    private static void addCourse() {

    }

    private static void addMember() {

    }

    private static void deleteMember() {

    }


}