import java.io.Serializable;
import java.util.ArrayList;

public class IntermidiateResult implements Serializable{
    private String user;
    private double distance, elevation, speed;
    private long time;
    private ArrayList<Integer> segments = new ArrayList<>();


    public IntermidiateResult() {
    }

    public IntermidiateResult(String user, double distance, double elevation, double speed, long time, ArrayList<Integer> segments) {
        this.user = user;
        this.distance = distance;
        this.elevation = elevation;
        this.speed = speed;
        this.time = time;
        this.segments = segments;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getElevation() {
        return this.elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public IntermidiateResult user(String user) {
        setUser(user);
        return this;
    }

    public IntermidiateResult distance(double distance) {
        setDistance(distance);
        return this;
    }

    public IntermidiateResult elevation(double elevation) {
        setElevation(elevation);
        return this;
    }

    public IntermidiateResult speed(double speed) {
        setSpeed(speed);
        return this;
    }

    public IntermidiateResult time(long time) {
        setTime(time);
        return this;
    }

    public ArrayList<Integer> getSegments(){
        return segments;
    }

    @Override
    public String toString() {
        return "{" +
            " user='" + getUser() + "'" +
            ", distance='" + getDistance() + "'" +
            ", elevation='" + getElevation() + "'" +
            ", speed='" + getSpeed() + "'" +
            ", time='" + getTime() + "'" +
            "}";
    }

}
