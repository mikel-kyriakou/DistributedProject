import java.util.HashMap;

public class Reducer extends Thread {
    private HashMap<String, Integer> usersRoutesCounters = new HashMap<>();
    private Object usersRoutesCountersLock = new Object();
    private HashMap<String, Integer> usersWaypointsCounters = new HashMap<>();
    private Object usersWaypointsCountersLock = new Object();
    private HashMap<String, Double> sumDistance = new HashMap<>();
    private Object sumDistanceLock = new Object();
    private HashMap<String, Double> sumElevation = new HashMap<>();
    private Object sumElevationLock = new Object();
    private HashMap<String, Long> sumTime = new HashMap<>();
    private Object sumTimeLock = new Object();
    private HashMap<String, Result> results = new HashMap<>();
    private Object resultsLock = new Object();

    public Reducer(HashMap<String,Integer> usersRoutesCounters, Object usersRoutesCountersLock, HashMap<String,Integer> usersWaypointsCounters, Object usersWaypointsCountersLock, HashMap<String,Double> sumDistance, Object sumDistanceLock, HashMap<String,Double> sumElevation, Object sumElevationLock, HashMap<String,Long> sumTime, Object sumTimeLock, HashMap<String,Result> results, Object resultsLock) {
        this.usersRoutesCounters = usersRoutesCounters;
        this.usersRoutesCountersLock = usersRoutesCountersLock;
        this.usersWaypointsCounters = usersWaypointsCounters;
        this.usersWaypointsCountersLock = usersWaypointsCountersLock;
        this.sumDistance = sumDistance;
        this.sumDistanceLock = sumDistanceLock;
        this.sumElevation = sumElevation;
        this.sumElevationLock = sumElevationLock;
        this.sumTime = sumTime;
        this.sumTimeLock = sumTimeLock;
        this.results = results;
        this.resultsLock = resultsLock;
    }

    public Result generateResult(String user){
        double sumDist, sumEle;
        long sumT;
        int routes;
        
        synchronized(sumDistanceLock){
            sumDist = sumDistance.get(user);
        }

        synchronized(sumElevationLock){
            sumEle= sumElevation.get(user);
        }

        synchronized(sumTimeLock){
            sumT = sumTime.get(user);
        }

        synchronized(usersRoutesCountersLock){
            routes = usersRoutesCounters.get(user);
        }

        return new Result(user, sumDist/routes, sumEle/routes, sumT/routes, (sumDist/routes)/(sumT/routes)*3600000);
    }

    public void run(){
        while(true){
            synchronized(usersRoutesCountersLock){
                synchronized(usersWaypointsCountersLock){
                    for(String user:usersWaypointsCounters.keySet()){
                        if(usersRoutesCounters.get(user)!=null){
                            if(usersRoutesCounters.get(user).equals(usersWaypointsCounters.get(user))){
                                synchronized(resultsLock){
                                    results.put(user, generateResult(user));
                                }
                            }
                        }
                    }
                }
            }

            try {
                sleep(1000);
            } catch (InterruptedException  e) {
                e.printStackTrace();
            }
        }
    }
    
}
