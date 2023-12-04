/*
 * Prog4.java - Allows for various operations on an Oracle
 * SQL database regarding the various information used for a sports center.
 *
 *
 */

import java.lang.reflect.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        try {
            HashMap<String, ArrayList<CourseData>> schedulingMap = new HashMap<>();
            ResultSet trainerSchedules = statement.executeQuery("SELECT lexc.trainer.name, lexc.course.name, lexc.course.start_time, lexc.course.duration, lexc.course.start_date, lexc.course.end_date FROM " +
                    "(" +
                    "lexc.trainer JOIN lexc.course ON lexc.course.trainer_id=lexc.trainer.trainer_id" +
                    ");");
            while (trainerSchedules.next()) {
                String rowsTrainer = trainerSchedules.getString("lexc.trainer.name");
                CourseData thisCourse = new CourseData();
                thisCourse.setStartTime(trainerSchedules.getString());
                if (!schedulingMap.containsKey(rowsTrainer)) {
                    schedulingMap.put(rowsTrainer, new ArrayList<>());
                }
                CourseData nextCourse = new CourseData();
                nextCourse.setCourseName(trainerSchedules.getString("lexc.course.name"));
                nextCourse.setStartTime(trainerSchedules.getString("lexc.course.start_time"));
                nextCourse.setDuration(trainerSchedules.getInt("lexc.course.duration"));
                nextCourse.setStartDate(trainerSchedules.getDate("lexc.course.start_date"));
                nextCourse.setEndDate(trainerSchedules.getDate("lexc.course.end_date"));
                schedulingMap.get(rowsTrainer).add(nextCourse);
            }
            for (String trainerName : schedulingMap.keySet()) {
                System.out.println(trainerName + "'s schedule for December:");
                for (CourseData nextCourse : schedulingMap.get(trainerName)) {
                    //TODO: If start date is during or before December, and end date is during or after December...
                    //TODO: Display time better
                    System.out.println(nextCourse.getCourseName() + " from " + nextCourse.getStartDate() + " to " + nextCourse.getEndDate() + ", starting at " + nextCourse.getStartTime() + " and ending at " + nextCourse.getStartTime() + nextCourse.getDuration());
                }
            }
        } catch (Exception e) {
            System.out.println("Error in function 'queryThree()'");
            e.printStackTrace();
        }
    }

    private static void queryTwo() {
        try {
            Scanner userScanner = new Scanner(System.in);
            System.out.println("Please provide the name of the member whose schedule to check.");
            String name = userScanner.next();
            ResultSet matchingMember = statement.executeQuery("SELECT memberID FROM lexc.member WHERE name='" + name + "';");
            while (!matchingMember.next()) {
                System.out.println("Could not find a member with that name! Please try again.");
                name = userScanner.next();
                matchingMember = statement.executeQuery("SELECT memberID FROM lexc.member WHERE name='" + name + "';");
            }
            System.out.println(name + "'s Schedule during November:");
            while (matchingMember.next()) {
                ResultSet membersCourses = statement.executeQuery("SELECT lexc.course.name, lexc.course.start_date, lexc.course.end_date, lexc.course.start_time, lexc.course.duration FROM " +
                        "(" +
                        "(" +
                        "(" +
                        "(" +
                        "lexc.member JOIN lexc.subscription ON lexc.member.member_id=subscription.member_id" +
                        ")" +
                        " JOIN lexc.package ON lexc.subscription.package_id=package.package_id" +
                        ")" +
                        " JOIN lexc.coursepackage ON lexc.package.package_id=lexc.coursepackage.package_id" +
                        ")" +
                        " JOIN lexc.course ON lexc.coursepackage.course_id=lexc.course.course_id" +
                        ")" +
                        " WHERE lexc.member.member_id=" + matchingMember.getInt("member_id"));
                while (membersCourses.next()) {
                    CourseData targetCourse = new CourseData();
                    targetCourse.setCourseName(membersCourses.getString("lexc.course.name"));
                    targetCourse.setStartDate(Date.valueOf(membersCourses.getString("lexc.course.start_date")));
                    targetCourse.setEndDate(Date.valueOf(membersCourses.getString("lexc.course.end_date")));
                    targetCourse.setDuration(membersCourses.getInt("lexc.course.duration"));
                    //TODO: If start date is during or before November, and end date is during or after November...
                    //TODO: Display time better
                    System.out.println(targetCourse.getCourseName() + " from " + targetCourse.getStartDate() + " to " + targetCourse.getEndDate() + ", starting at " + targetCourse.getStartTime() + " and ending at " + targetCourse.getStartTime() + targetCourse.getDuration());
                }
            }
        } catch (Exception e) {
            System.out.println("Error in 'queryTwo()'");
            e.printStackTrace();
        }
    }

    private static void queryOne() {
        try {
            System.out.println("Members with Negative Balances");
            System.out.println("NAME - PHONE #");
            ResultSet courses = statement.executeQuery("SELECT name, phone FROM lexc.members WHERE acc_balance<0;");
            while (courses.next()) {
                MemberData nextMember = new MemberData();
                nextMember.setName(courses.getString("name"));
                nextMember.setPhoneNum(courses.getString("phone"));
                System.out.println(nextMember.getName() + " - " + nextMember.getPhoneNum());
            }
        } catch (Exception e) {
            System.out.println("Error in 'queryOne()'");
            e.printStackTrace();
        }
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

    private static void addMember() { //get max member number
        MemberData newMember = new MemberData();
        Scanner userScanner = new Scanner(System.in);
        System.out.println("Please input the name of the member to add.");
        String name = userScanner.next();
        newMember.setName(name);
        System.out.println("Please input " + name + "'s phone number.");
        newMember.setPhoneNum(userScanner.next());
        newMember.setAcctBalance(0);
        newMember.setMoneySpent(0);
        newMember.setMembershipName("");

        System.out.println(newMember.insertString());




        try {
            ResultSet courses = statement.executeQuery("SELECT * from [tablename-courses];");
            while (courses.next())
                //TODO: Evaluate new MemberID. Need to see the existing member IDs and get the next one.

                statement.execute("INSERT INTO lexc.members VALUES ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private ArrayList<CourseData> courses() {
        ArrayList<CourseData> retVal = new ArrayList<>();
        try {
            ResultSet courses = statement.executeQuery("SELECT * from [tablename-courses];");
            while (courses.next()) {
                CourseData thisCourse = new CourseData();
                thisCourse.setCourseName(courses.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }

    private static void deleteMember() {

    }


}
