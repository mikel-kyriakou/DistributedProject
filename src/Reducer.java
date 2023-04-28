import java.util.HashMap;
import java.util.ArrayList;

public class Reducer extends Thread {
    
    private HashMap<String, Integer> counters;
    private Object countersLock; 
    private ArrayList<IntermidiateResult> intermidiateList; 
    private Object intermidiateListLock;
    // private HashMap<String, FinalResult> results;

    public Reducer(HashMap<String, Integer> counters, Object countersLock, ArrayList<IntermidiateResult> intermidiateList, Object intermidiateListLock){
        this.counters = counters;
        this.countersLock = countersLock;
        this.intermidiateList = intermidiateList;
        this.intermidiateListLock = intermidiateListLock;
    }

    public void calculateResults(String user){
        int sumDistance=0, sumElevation=0, sumTime=0, sumSpeed=0, counter=0;
        double averageSpeed, averageDistance, averageElevation;
        synchronized(intermidiateListLock){
            for(IntermidiateResult i:intermidiateList){
                if(i.getUser().equals(user)){
                    counter++;
                    sumDistance += i.getDistance();
                    sumElevation += i.getElevation();
                    sumTime += i.getTime();
                    sumSpeed += i.getSpeed();
                }
            }
            
            averageDistance = (double)sumDistance/counter;
            averageElevation =  (double)sumElevation/counter;
            averageSpeed = (double)sumSpeed/counter;

            Result result = new Result(averageDistance, averageSpeed, averageElevation);
        }
    }

    public run(){
        while(true){
            synchronized(countersLock){
                if(counters.size()>0){
                    counters.forEach((user, counter)-> calculateResults(user));
                }
            }
        }
    }
}
