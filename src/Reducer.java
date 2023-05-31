import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private HashMap<String, ArrayList<IntermidiateResult>> intResultsThread = new HashMap<>();
    private Object intResultsThreadLock = new Object();
    private HashMap<Segment, ArrayList<UserLeaderboard>> segmentsThread = new HashMap<>();
    private Object segmentsThreadLock = new Object();

    public Reducer(HashMap<String,Integer> usersRoutesCounters, Object usersRoutesCountersLock, 
                   HashMap<String,Integer> usersWaypointsCounters, Object usersWaypointsCountersLock, 
                   HashMap<String,Double> sumDistance, Object sumDistanceLock, 
                   HashMap<String,Double> sumElevation, Object sumElevationLock, 
                   HashMap<String,Long> sumTime, Object sumTimeLock, 
                   HashMap<String,Result> results, Object resultsLock, 
                   HashMap<String, ArrayList<IntermidiateResult>> intResultsThread, Object intResultsThreadLock, 
                   HashMap<Segment, ArrayList<UserLeaderboard>> segmentsThread, Object segmentsThreadLock){
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
        this.intResultsThread = intResultsThread;
        this.intResultsThreadLock = intResultsThreadLock;
        this.segmentsThread = segmentsThread;
        this.segmentsThreadLock = segmentsThreadLock;
    }

    // public Result generateResult(String user){
    //     double sumDist, sumEle;
    //     long sumT;
    //     int routes;
        
    //     synchronized(sumDistanceLock){
    //         sumDist = sumDistance.get(user);
    //     }

    //     synchronized(sumElevationLock){
    //         sumEle= sumElevation.get(user);
    //     }

    //     synchronized(sumTimeLock){
    //         sumT = sumTime.get(user);
    //     }

    //     synchronized(usersRoutesCountersLock){
    //         routes = usersRoutesCounters.get(user);
    //     }

    //     return new Result(user, sumDist/routes, sumEle/routes, sumT/routes, (sumDist/routes)/(sumT/routes)*3600000);
    // }

    public void updateResults(String user){
        ArrayList<IntermidiateResult> userResults;
        double sumDist, sumEle;
        long sumT;
        int routes;
        HashMap<Integer, Long> segmentResults = new HashMap<>();

        synchronized(intResultsThreadLock){
            userResults = intResultsThread.get(user);
        }

        for(IntermidiateResult ir:userResults){
            updateHashMaps(ir);

            if(ir.getSegments().size()>0){
                for(int s:ir.getSegments()){
                    if(segmentResults.get(s)==null){
                        segmentResults.put(s, (long)0);
                    }
                    segmentResults.put(s, segmentResults.get(s)+ir.getTime());
                }
            }
        }


        synchronized(segmentsThreadLock){
            for(Segment s:segmentsThread.keySet()){
                for(int code:segmentResults.keySet()){
                    if(s.hashCode()==code){
                        ArrayList<UserLeaderboard> leaderboard = segmentsThread.get(s);
                        boolean foundRunner = false;
                        for(UserLeaderboard runner:leaderboard){
                            if(runner.getUser().equals(user)){
                                foundRunner = true;
                                if(runner.getTime()>segmentResults.get(code)){
                                    runner.setTime(segmentResults.get(code));
                                }
                            }
                        }

                        if(foundRunner == false){
                            leaderboard.add(new UserLeaderboard(user, segmentResults.get(code)));
                        }

                        Collections.sort(leaderboard, Comparator.comparingLong(UserLeaderboard::getTime));
                        segmentsThread.put(s, leaderboard);
                    }
                }
            }
        }

        synchronized(intResultsThreadLock){
            intResultsThread.put(user, new ArrayList<>());
        }

        synchronized(sumDistanceLock){
            sumDist = sumDistance.get(user);
        }
        
        synchronized(sumElevationLock){
            sumEle = sumElevation.get(user);
        }
        
        synchronized(sumTimeLock){
            sumT = sumTime.get(user);
        }

        synchronized(usersRoutesCountersLock){
            routes = usersRoutesCounters.get(user);
        }

        Result result = new Result(user, sumDist/routes, sumEle/routes, sumT/routes, (sumDist/routes)/(sumT/routes)*3600000);

        results.put(user, result);
    }

    public void updateHashMaps(IntermidiateResult result){
        String user = result.getUser();

        synchronized(sumDistanceLock){
            if(sumDistance.get(user)==null){
                sumDistance.put(user, result.getDistance());
            }
            else{
                sumDistance.put(user, sumDistance.get(user)+result.getDistance());
            }
        }

        synchronized(sumElevationLock){
            if(sumElevation.get(user)==null){
                sumElevation.put(user, result.getElevation());
            }
            else{
                sumElevation.put(user, sumElevation.get(user)+result.getElevation());
            }
        }

        synchronized(sumTimeLock){
            if(sumTime.get(user)==null){
                sumTime.put(user, result.getTime());
            }
            else{
                sumTime.put(user, sumTime.get(user)+result.getTime());
            }
        }
    }

    public void run(){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean running = true;
        while(running){
            synchronized(usersWaypointsCountersLock){
                for(String user:usersWaypointsCounters.keySet()){
                    if(usersWaypointsCounters.get(user)==1){
                        synchronized(resultsLock){
                            updateResults(user);
                            running = false;
                            usersWaypointsCounters.remove(user);
                            break;
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
