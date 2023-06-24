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
    private double routeDistance = 0;
    private double routeElevation = 0;
    private long routeTime = 0;


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

    /* A method that is called to start the reduce phase for the user that we pass as an argument */
    public void updateResults(String user){
        ArrayList<IntermidiateResult> userResults;
        double sumDist, sumEle;
        long sumT;
        int routes;
        HashMap<Integer, Long> segmentResults = new HashMap<>();

        /* Get the list of intermidiate results for the user */
        synchronized(intResultsThreadLock){
            userResults = intResultsThread.get(user);
        }

        for(IntermidiateResult ir:userResults){
            updateHashMaps(ir);
            updateRouteResults(ir);

            /* Checks if an intermidiate result belongs to a segment and updates the statistics for each segment it belongs to.
             * segmentResults is a hashmap we keep in the thread.
            */
            if(ir.getSegments().size()>0){
                for(int s:ir.getSegments()){
                    if(segmentResults.get(s)==null){
                        segmentResults.put(s, (long)0);
                    }
                    segmentResults.put(s, segmentResults.get(s)+ir.getTime());
                }
            }
        }

        /* Updates the main hashmap of the segments based on the segmentResults we calculated. Every time we add or change a UserLeaderboard
         * in an arraylist, we sort it based on the timing.
         */
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

        /* This creates a result containing the average values of all routes */
        // Result result = new Result(user, sumDist/routes, sumEle/routes, sumT/routes, (sumDist/routes)/(sumT/routes)*3600000);

        /* This creates a result for the specific route uploaded by user */
        Result result = new Result(user, routeDistance, routeElevation, routeTime, routeDistance/routeTime*3600000);


        results.put(user, result);
    }

    /* This method get as an argument an intermidiate result and updates all hashmaps with the stats */
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

    public void updateRouteResults(IntermidiateResult result){
        routeDistance += result.getDistance();
        routeElevation += result.getElevation();
        routeTime += result.getTime();
    }

    public void run(){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean running = true;

        /* In every loop it checks every users wpt counter list if it is equal to 1 and then it starts the reduce phase.
         * The counter has to be 1 because we add to this hashmap the amount of waypoints we receive from the user and we sub the amount 
         * of intermidiate results we get from the worker, so for n waypoints we create n-1 intermidiate results and n-(n-1)=1.
         */
        while(running){
            synchronized(usersWaypointsCountersLock){
                for(String user:usersWaypointsCounters.keySet()){
                    System.out.println("Reducer: wpt counter = " + usersWaypointsCounters.get(user));
                    if(usersWaypointsCounters.get(user)==1){
                        synchronized(resultsLock){
                            updateResults(user);
                            usersWaypointsCounters.remove(user);
                            /* Print leaderboard for testing purpose */
                            synchronized(segmentsThreadLock){
                                System.out.println(segmentsThread);
                            }

                            /* Runnig to false to stop the loop because everytime a user connects to master another reducer starts.
                             * The reason for this is to not run the reducer unnecessarily when none uses the program.
                             */
                            running = false;
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
