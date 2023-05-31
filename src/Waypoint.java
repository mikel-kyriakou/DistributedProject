import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

public class Waypoint implements Serializable{
    private String user;
    private double lon;
    private double lat;
    private double elevation;
    private Date date;
    private ArrayList<Integer> segments = new ArrayList<>();

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

    public void addSegment(int s){
        segments.add(s);
    }

    public ArrayList<Integer> getSegments(){
        return segments;
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

    @Override
    public boolean equals(Object object){
        Waypoint w = (Waypoint) object;

        double lat1, lat2, lon1, lon2;
        lat1 = this.getLat();
        lat2 = w.getLat();
        lon1 = this.getLon();
        lon2 = w.getLon();

        final double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        if(d*1000<10){
            return true;
        }

        return false;
    }

}
