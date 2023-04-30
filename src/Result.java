import java.io.Serializable;

public class Result implements Serializable{
    private String user;
    private double averageDistance, averageElevation, averageTime, averageSpeed;


    public Result() {
    }

    public Result(String user, double averageDistance, double averageElevation, double averageTime, double averageSpeed) {
        this.user = user;
        this.averageDistance = averageDistance;
        this.averageElevation = averageElevation;
        this.averageTime = averageTime;
        this.averageSpeed = averageSpeed;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getAverageDistance() {
        return this.averageDistance;
    }

    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }

    public double getAverageElevation() {
        return this.averageElevation;
    }

    public void setAverageElevation(double averageElevation) {
        this.averageElevation = averageElevation;
    }

    public double getAverageTime() {
        return this.averageTime;
    }

    public void setAverageTime(double averageTime) {
        this.averageTime = averageTime;
    }

    public double getAverageSpeed() {
        return this.averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Result user(String user) {
        setUser(user);
        return this;
    }

    public Result averageDistance(double averageDistance) {
        setAverageDistance(averageDistance);
        return this;
    }

    public Result averageElevation(double averageElevation) {
        setAverageElevation(averageElevation);
        return this;
    }

    public Result averageTime(double averageTime) {
        setAverageTime(averageTime);
        return this;
    }

    public Result averageSpeed(double averageSpeed) {
        setAverageSpeed(averageSpeed);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " user='" + getUser() + "'" +
            ", averageDistance='" + getAverageDistance() + " km'" +
            ", averageElevation='" + getAverageElevation() + " m'" +
            ", averageTime='" + getAverageTime() + " msec'" +
            ", averageSpeed='" + getAverageSpeed() + " m/s'" +
            "}";
    }


}
