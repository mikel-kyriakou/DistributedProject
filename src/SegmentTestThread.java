import java.util.ArrayList;
import java.util.HashMap;

public class SegmentTestThread extends Thread{
    private HashMap<Segment, ArrayList<UserLeaderboard>> segmentsThread = new HashMap<>();
    private Object segmentsThreadLock = new Object();

    public SegmentTestThread(HashMap<Segment, ArrayList<UserLeaderboard>> segments, Object segmentsLock){
        this.segmentsThread = segments;
        this.segmentsThreadLock = segmentsLock;
    }

    public void run(){
        try {
            while(true){
                sleep(2000);

                synchronized(segmentsThreadLock){
                    System.out.println(segmentsThread);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
