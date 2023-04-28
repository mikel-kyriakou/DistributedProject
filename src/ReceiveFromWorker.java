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

    public ReceiveFromWorker(ObjectInputStream in, ArrayList<IntermidiateResult> list, Object lock, HashMap<String, Integer> counters, Object countersLock){
        this.in = in;
        this.resultsFromWorker = list;
        this.lock = lock;
        this.counters = counters;
        this.countersLock = countersLock;
    }


    public void run(){
        try { 
            while(true){
                int size = (int) in.readInt();
                if(size>0){
                    IntermidiateResult result = (IntermidiateResult) in.readObject();
                    synchronized(lock){
                        resultsFromWorker.add(result);
                    }
                    synchronized(countersLock){
                        counters.put(result.getUser(), counters.get(result.getUser())-1);
                        System.out.println(counters);
                    }
                    // System.out.println(resultsFromWorker.size());
                }
                else{
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
