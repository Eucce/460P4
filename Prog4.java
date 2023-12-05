/*
 * Prog4.java - Allows for various operations on an Oracle
 * SQL database regarding the various information used for a sports center.
 *
 *
 */

import java.sql.Date;
import java.sql.*;
import java.time.Year;
import java.util.*;

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
                if (!schedulingMap.containsKey(rowsTrainer)) {
                    schedulingMap.put(rowsTrainer, new ArrayList<>());
                }
                CourseData nextCourse = new CourseData();
                nextCourse.setCourseName(trainerSchedules.getString("lexc.course.name"));
                nextCourse.setStartTime(trainerSchedules.getString("lexc.course.start_time"));
                nextCourse.setDuration(trainerSchedules.getFloat("lexc.course.duration"));
                nextCourse.setStartDate(trainerSchedules.getDate("lexc.course.start_date"));
                nextCourse.setEndDate(trainerSchedules.getDate("lexc.course.end_date"));
                schedulingMap.get(rowsTrainer).add(nextCourse);
            }
            int year = Year.now().getValue();
            for (String trainerName : schedulingMap.keySet()) {
                System.out.println(trainerName + "'s schedule for December:");
                for (CourseData nextCourse : schedulingMap.get(trainerName)) {
                    if (nextCourse.getStartDate().getYear() <= year && ((nextCourse.getEndDate().getYear() == year && nextCourse.getEndDate().getMonth() == 11) || nextCourse.getEndDate().getYear() > year))
                        System.out.println(nextCourse.getCourseName() + " from " + nextCourse.getStartDate() + " to " + nextCourse.getEndDate() + ", starting at " + nextCourse.getStartTime() + " and ending at " + addTime(nextCourse.getStartTime(), nextCourse.getDuration()));
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
            int year = Year.now().getValue();
            while (matchingMember.next()) {
                ResultSet membersCourses = statement.executeQuery("SELECT lexc.course.name, lexc.course.start_date, lexc.course.end_date, lexc.course.start_time, lexc.course.duration FROM " +
                        "(" +
                        "(" +
                        "(" +
                        "(" +
                        "lexc.member JOIN lexc.subscription ON lexc.member.member_id=lexc.subscription.member_id" +
                        ")" +
                        " JOIN lexc.package ON lexc.subscription.package_id=lexc.package.package_id" +
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
                    targetCourse.setDuration(membersCourses.getFloat("lexc.course.duration"));
                    if (targetCourse.getStartDate().getYear() <= year && targetCourse.getStartDate().getMonth() <= 10 && (targetCourse.getEndDate().getYear() == year && targetCourse.getEndDate().getMonth() >= 10) || targetCourse.getEndDate().getYear() > year)
                        System.out.println(targetCourse.getCourseName() + " from " + targetCourse.getStartDate() + " to " + targetCourse.getEndDate() + ", starting at " + targetCourse.getStartTime() + " and ending at " + addTime(targetCourse.getStartTime(), targetCourse.getDuration()));
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
            ResultSet courses = statement.executeQuery("SELECT name, telephone_no FROM lexc.member WHERE acc_balance<0;");
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

    private static Time addTime(Time timeIn, float hourVal) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeIn);
        calendar.add(Calendar.HOUR_OF_DAY, (int) Math.floor(hourVal));
        calendar.add(Calendar.MINUTE, (int) (hourVal % 1));
        return (Time) calendar.getTime();
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
        Scanner scn = new Scanner(System.in);
        CourseData newCourse = new CourseData();
        newCourse.setCourseID(getNextID("Course"));
        System.out.println("Please input the name of the course to add:");
        String name = scn.nextLine();
        newCourse.setCourseName(name);
        System.out.println("Please input the start time in the form 'H:MM PM/AM':");
        String start_time = scn.nextLine();
        newCourse.setStartTime(start_time);
        System.out.println("Enter a duration for the course:");
        int duration = Integer.parseInt(scn.nextLine());
        newCourse.setDuration(duration);

    }


    private static void addMember() {
        MemberData newMember = new MemberData();
        newMember.setMemberID(getNextID("Member"));
        Scanner userScanner = new Scanner(System.in);
        System.out.println("Please input the name of the member to add:");
        String name = userScanner.nextLine();
        newMember.setName(name);
        System.out.println("Please input " + name + "'s phone number:");
        String number = userScanner.nextLine();
        newMember.setPhoneNum(number);
        newMember.setAcctBalance(0);
        newMember.setMoneySpent(0);
        newMember.setMembershipName("");
        try {
            ResultSet packages = statement.executeQuery("SELECT * from lexc.Package");
            TreeMap<Integer, Integer> package_prices = new TreeMap<>();
            if (packages != null) {
                System.out.println();
                System.out.printf("%-12s\t%-20s\t%-8s%n", "Package ID", "Package Name", "Price");
                System.out.println("------------------------------------------------");
                while (packages.next()) {
                    int curID = packages.getInt("package_id");
                    int curPrice = packages.getInt("price");
                    package_prices.put(curID, curPrice);
                    System.out.printf("%-12d\t%-20s\t%-8d%n", curID, packages.getString("name"), curPrice);
                }
            }
            System.out.println("Enter the Package ID of your choosing:");
            int package_id = Integer.parseInt(userScanner.nextLine());
            int price = package_prices.get(package_id);
            newMember.setAcctBalance(newMember.getAcctBalance() + price);
            statement.execute("INSERT INTO lexc.Member VALUES " + newMember.insertString());
            statement.execute("INSERT INTO lexc.Subscription VALUES (" + newMember.getMemberID() + ", " + package_id + ")");
            System.out.println("Member added successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private static void deleteMember() {
        try {
            ResultSet members = statement.executeQuery("SELECT * from lexc.Member");
            if (members != null) {
                System.out.println();
                System.out.printf("%-10s\t%-20s\t%-10s\t%-14s%n", "Member ID", "Name", "Phone Num", "Acct Balance");
                System.out.println("--------------------------------------------------------------------------");
                while (members.next()) {
                    int curMemID = members.getInt("member_id");
                    String curName = members.getString("name");
                    String phoneNum = members.getString("telephone_no");
                    int acct_balance = members.getInt("acc_balance");
                    System.out.printf("%-10d\t%-20s\t%-10s\t%-14d%n", curMemID, curName, phoneNum, acct_balance);
                }
            }
            System.out.println("Enter a member ID to delete:");
            Scanner scn = new Scanner(System.in);
            int memID = Integer.parseInt(scn.nextLine());
            checkEquipmentCheckouts(memID);
            boolean deletable = checkTransactionHistory(memID);
            removeSubscriptions(memID);
            if (deletable) {
                statement.execute("DELETE from lexc.Member where member_id = " + memID);
                System.out.println("Member deleted successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in deleting a member");
        }
    }

    private static void removeSubscriptions(int memID) {
        try {
            statement.executeQuery("DELETE from lexc.Subscription where member_id = " + memID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in removing member " + memID + "'s subscriptions");
        }
    }

    private static boolean checkTransactionHistory(int memID) {
        try {
            ResultSet member = statement.executeQuery("select Acc_Balance, name from lexc.Member where member_id = " + memID);
            if (member != null) {
                while (member.next()) {
                    int amountOwed = member.getInt("Acc_Balance");
                    if (amountOwed > 0) {
                        String name = member.getString("name");
                        System.out.println("The member '" + name + "' owes a balance of $" + amountOwed);
                        System.out.println("This member cannot be deleted until the balance is paid off");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find the member with the id - " + memID);
        }
        return true;
    }

    private static void checkEquipmentCheckouts(int memID) {
        try {
            ResultSet checkouts = statement.executeQuery("SELECT * from lexc.checkout where member_id = " + memID);
            if (checkouts != null) {
                while (checkouts.next()) {
                    if (checkouts.getTimestamp("In_time") == null) {
                        decreaseItemStock(checkouts.getInt("Item_ID"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in searching for checkouts by member with id - " + memID);
        }
    }

    private static void decreaseItemStock(int itemID) {

        try {
            statement.executeQuery("update lexc.Equipment set stock = stock - 1 where item_id = " + itemID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Decreasing the missing item's stock");
        }
    }

    private static int getNextID(String table) {
        ResultSet max_id = null;
        int retval = -1;
        try {
            max_id = statement.executeQuery("SELECT MAX(MEMBER_ID) from lexc." + table);
            if (max_id != null) {
                while (max_id.next()) {
                    retval = max_id.getInt("MAX(member_id)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error getting the max member_id from the Member table");
        }
        return retval + 1;
    }


    private ArrayList<CourseData> courses() {
        ArrayList<CourseData> retVal = new ArrayList<>();
        try {
            ResultSet courses = statement.executeQuery("SELECT * from [tablename-courses]");
            while (courses.next()) {
                CourseData thisCourse = new CourseData();
                thisCourse.setCourseName(courses.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }


}


