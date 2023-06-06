import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ReceiveFromWorker extends Thread {
        ObjectInputStream in;
        ArrayList<IntermidiateResult> resultsFromWorker;
        Object lock;
        private HashMap<String, Integer> counters;
        private Object countersLock;
        private HashMap<String, Double> sumDistance = new HashMap<>();
        private Object sumDistanceLock = new Object();
        private HashMap<String, Double> sumElevation = new HashMap<>();
        private Object sumElevationLock = new Object();
        private HashMap<String, Long> sumTime = new HashMap<>();
        private Object sumTimeLock = new Object();
        private HashMap<String, Double> sumSpeed = new HashMap<>();
        private Object sumSpeedLock = new Object();
        // private HashMap<Integer, HashMap<String, Long>> segmentsThread = new HashMap<>();
        // private Object segmentsLockThread = new Object();
        private HashMap<Segment, ArrayList<UserLeaderboard>> segmentsThread = new HashMap<>();
        private Object segmentsLockThread = new Object();
        private HashMap<String, ArrayList<IntermidiateResult>> intResultsThread = new HashMap<>();
        private Object intResultsThreadLock = new Object();
    
    
    

    public ReceiveFromWorker(ObjectInputStream in, HashMap<String, Integer> counters, Object countersLock, HashMap<String, Double> sumDistance,  Object sumDistanceLock, HashMap<String, Double> sumElevation, Object sumElevationLock, HashMap<String, Long> sumTime, Object sumTimeLock, HashMap<String, Double> sumSpeed, Object sumSpeedLock){
        this.in = in;
        this.counters = counters;
        this.countersLock = countersLock;
        this.sumDistance = sumDistance;
        this.sumDistanceLock = sumDistanceLock;
        this.sumElevation = sumElevation;
        this.sumElevationLock = sumElevationLock;
        this.sumTime = sumTime;
        this.sumTimeLock = sumTimeLock;
        this.sumSpeed = sumSpeed;
        this.sumSpeedLock = sumSpeedLock;
    }

    public ReceiveFromWorker(ObjectInputStream in, HashMap<String, Integer> counters, Object countersLock, HashMap<String, ArrayList<IntermidiateResult>> intResultsThread, Object intResultsThreadLock){
        this.in = in;
        this.counters = counters;
        this.countersLock = countersLock;
        this.intResultsThread = intResultsThread;
        this.intResultsThreadLock = intResultsThreadLock;
    }

    public void updateIntResults(IntermidiateResult result){
        ArrayList<IntermidiateResult> userList;
        synchronized(intResultsThreadLock){
            if(intResultsThread.containsKey(result.getUser())){
                userList = intResultsThread.get(result.getUser());
            }
            else{
                userList = new ArrayList<>();
            }
            userList.add(result);
            intResultsThread.put(result.getUser(), userList);
    
        }

        synchronized(countersLock){
            counters.put(result.getUser(), counters.get(result.getUser())-1);
        }
    }

    public void run(){
        /* We are constantly reading an int and when its over 0 we read in inter result. */
        try {
            while(true){
                int size = (int) in.readInt();
                if(size>0){
                    IntermidiateResult result = (IntermidiateResult) in.readObject();
                    if(result!=null){
                        updateIntResults(result);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }    }
}
