import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;

public class CourseData {
    private int courseID;
    private String courseName;
    private Date startDate;
    private Date endDate;
    private Time startTime;
    private DayOfWeek startDay;
    private int duration;
    private int maxMembers;
    private int trainerID;

    public int getCourseID() {return courseID;}

    public void setCourseID(int ID) {this.courseID = ID;}

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        String dayPart = startTime.split(" ")[0];
        String timePart = startTime.split(" ")[1];
        this.startDay = DayOfWeek.valueOf(dayPart);
        this.startTime = Time.valueOf(timePart);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(int trainerID) {
        this.trainerID = trainerID;
    }

    public String insertString() { //incomplete
        String retval = "[";
        retval += courseID + ", " + courseName + ", " + startTime + ", " ;


        return retval;
    }
}
