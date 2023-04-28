import java.io.*;
import java.util.ArrayList;

public class ReceiveFromWorker extends Thread {
        ObjectInputStream in;
        // ObjectOutputStream out;
        ArrayList<IntermidiateResult> resultsFromWorker;
        Object lock;
    
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

    public ReceiveFromWorker(ObjectInputStream in, ArrayList<IntermidiateResult> list, Object lock){
        this.in = in;
        this.resultsFromWorker = list;
        this.lock = lock;
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
                    System.out.println(resultsFromWorker.size());
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
