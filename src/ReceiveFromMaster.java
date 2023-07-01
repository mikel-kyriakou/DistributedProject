import java.io.*;
import java.util.ArrayList;

public class ReceiveFromMaster extends Thread {
        ObjectInputStream in;
        private ArrayList<ChunkedGPX> threadList = new ArrayList<ChunkedGPX>();
        Object lock;
    
    public ReceiveFromMaster(ObjectInputStream connection, ArrayList<ChunkedGPX> list, Object lock){
        this.in = connection;
        this.threadList = list;
        this.lock = lock;
    }

    public void run(){
        int counter = 0;
        /* We are constantly receiving a number and when we get an int over 0 we also read a chunked gpx.
         * Then we add this chunked gpx to the worker list.
         */
        try { 
            while(true){

                // int size = (int) in.readInt();
                // if(size>0){
                //     ChunkedGPX received_chunked = (ChunkedGPX) in.readObject();
                //     synchronized(lock){
                //         threadList.add(received_chunked);
                //     }
                // }

                in.readInt();
                ChunkedGPX received_chunked = (ChunkedGPX) in.readObject();
                synchronized(lock){
                    threadList.add(received_chunked);
                }

                counter++;
                System.out.println("Receive from master: counter " + counter);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
