import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveFromWorker extends Thread {
        ObjectInputStream in;
        // ObjectOutputStream out;
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
    
    
    
    // public ReceiveFromWorker(Socket connection, ArrayList<IntermidiateResult> list, Object lock){
    //     try {
    //         // out = new ObjectOutputStream(connection.getOutputStream());
    //         in = new ObjectInputStream(connection.getInputStream());

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     this.resultsFromWorker = list;
    //     this.lock = lock;
    // }

    // public ReceiveFromWorker(ObjectInputStream in, ArrayList<IntermidiateResult> list, Object lock, HashMap<String, Integer> counters, Object countersLock){
    //     this.in = in;
    //     this.resultsFromWorker = list;
    //     this.lock = lock;
    //     this.counters = counters;
    //     this.countersLock = countersLock;
    // }

    public ReceiveFromWorker(ObjectInputStream in, HashMap<String, Integer> counters, Object countersLock, HashMap<String, Double> sumDistance,  Object sumDistanceLock, HashMap<String, Double> sumElevation, Object sumElevationLock, HashMap<String, Long> sumTime, Object sumTimeLock){
        this.in = in;
        this.counters = counters;
        this.countersLock = countersLock;
        this.sumDistance = sumDistance;
        this.sumDistanceLock = sumDistanceLock;
        this.sumElevation = sumElevation;
        this.sumElevationLock = sumElevationLock;
        this.sumTime = sumTime;
        this.sumTimeLock = sumTimeLock;
    }

    public void updateHashMaps(IntermidiateResult result){

        synchronized(sumDistanceLock){
            if(sumDistance.get(result.getUser())==null){
                sumDistance.put(result.getUser(), result.getDistance());
            }
            else{
                sumDistance.put(result.getUser(), sumDistance.get(result.getUser())+result.getDistance());
            }
        }

        synchronized(sumElevationLock){
            if(sumElevation.get(result.getUser())==null){
                sumElevation.put(result.getUser(), result.getElevation());
            }
            else{
                sumElevation.put(result.getUser(), sumElevation.get(result.getUser())+result.getElevation());
            }
        }

        synchronized(sumTimeLock){
            if(sumTime.get(result.getUser())==null){
                sumTime.put(result.getUser(), result.getTime());
            }
            else{
                sumTime.put(result.getUser(), sumTime.get(result.getUser())+result.getTime());
            }
        }

        synchronized(countersLock){
            counters.put(result.getUser(), counters.get(result.getUser())-1);
        }

    }

    public void run(){
        try { 
            // while(true){
            //     int size = (int) in.readInt();
            //     if(size>0){
            //         IntermidiateResult result = (IntermidiateResult) in.readObject();
            //         synchronized(lock){
            //             resultsFromWorker.add(result);
            //         }
            //         synchronized(countersLock){
            //             counters.put(result.getUser(), counters.get(result.getUser())-1);
            //             System.out.println(counters);
            //         }
            //         // System.out.println(resultsFromWorker.size());
            //     }
            //     else{
            //     }
            // }

            while(true){
                int size = (int) in.readInt();
                if(size>0){
                    IntermidiateResult result = (IntermidiateResult) in.readObject();
                    updateHashMaps(result);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }/* finally {
            try {
                in.close();
                // out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }*/

    }
}
