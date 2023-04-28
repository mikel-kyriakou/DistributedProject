import java.util.ArrayList;
import java.util.Date;

public class WorkerCalculator extends Thread {
    private ChunkedGPX chunked;
    private ArrayList<IntermidiateResult> list;
    Object lock;

    public WorkerCalculator(ChunkedGPX chunked, ArrayList<IntermidiateResult> list, Object lock){
        this.chunked = chunked;
        this.list = list;
        this.lock = lock;
    }

    public void run(){
        String user = chunked.getWpt1().getUser();
        double distance = calculateDistance();
        long time = calculateTime();
        double elevation = calculateElevation();
        double speed = calcualteSpeed(distance, time);

        synchronized(lock){
            list.add(new IntermidiateResult(user, distance, elevation, speed, time));
        }
    }

    public double calculateDistance(){ //km
        double lat1, lat2, lon1, lon2;
        lat1 = chunked.getWpt1().getLat();
        lat2 = chunked.getWpt2().getLat();
        lon1 = chunked.getWpt1().getLon();
        lon2 = chunked.getWpt2().getLon();

        final double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    public long calculateTime(){ //milliseconds
        Date d1 = chunked.getWpt1().getDate();
        Date d2 = chunked.getWpt2().getDate();

        long difference_In_Time
            = d2.getTime() - d1.getTime();

        return difference_In_Time;

    }

    public double calculateElevation(){
        double el1 = chunked.getWpt1().getElevation();
        double el2 = chunked.getWpt2().getElevation();

        if(el2-el1>0){
            return el2-el1;
        }
        else{
            return 0;
        }
    }

    public double calcualteSpeed(double distance, long time){ //km per h
        return (distance/time)*3600000;
    }
    
}
