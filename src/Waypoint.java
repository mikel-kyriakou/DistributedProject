import java.io.Serializable;
import java.util.Date;

public class Waypoint implements Serializable{
    private String user;
    private double lon;
    private double lat;
    private double elevation;
    private Date date;

    public Waypoint(){

    }

    public Waypoint(String user, double lat, double lon, double elevation, Date date) {
        this.user = user;
        this.lat = lat;
        this.lon = lon;
        this.elevation = elevation;
        this.date = date;
    }

    public String getUser(){
        return this.user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getElevation() {
        return this.elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Date getDate(){
        return this.date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    @Override
    public String toString() {
        return "{" +
            " user='" + getUser() + "'" +
            ", lon='" + getLon() + "'" +
            ", lat='" + getLat() + "'" +
            ", elevation='" + getElevation() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }

}
