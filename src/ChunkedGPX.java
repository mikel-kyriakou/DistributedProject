import java.io.Serializable;

public class ChunkedGPX implements Serializable{
    private Waypoint wpt1, wpt2;

    public ChunkedGPX() {
    }

    public ChunkedGPX(Waypoint wpt1, Waypoint wpt2) {
        this.wpt1 = wpt1;
        this.wpt2 = wpt2;
    }

    public Waypoint getWpt1() {
        return this.wpt1;
    }

    public void setWpt1(Waypoint wpt1) {
        this.wpt1 = wpt1;
    }

    public Waypoint getWpt2() {
        return this.wpt2;
    }

    public void setWpt2(Waypoint wpt2) {
        this.wpt2 = wpt2;
    }

    @Override
    public String toString() {
        return "{" +
            ", wpt1='" + getWpt1() + "'" +
            ", wpt2='" + getWpt2() + "'" +
            "}";
    }

}
