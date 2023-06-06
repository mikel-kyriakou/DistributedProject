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

    public double getDistance() {
        return this.distance;
    }

    public double getElevation() {
        return this.elevation;
    }

    public double getSpeed() {
        return this.speed;
    }

    public long getTime() {
        return this.time;
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
