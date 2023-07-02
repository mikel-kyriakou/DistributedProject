import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SendToWorker extends Thread {
    ObjectOutputStream out;
    private ArrayList<ChunkedGPX> worker_list;
    private Object lock;


    public SendToWorker(Socket connection, ArrayList<ChunkedGPX> list, Object lock){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.worker_list = list;
        this.lock = lock;
    }

    public SendToWorker(ObjectOutputStream out, ArrayList<ChunkedGPX> list, Object lock){
        this.out = out;
        this.worker_list = list;
        this.lock = lock;
    }


    public void run(){
        /* In this try block we are constantly sending the size of the worker list 
        and when its over 0, we send a chunked gpx and we remove it from the list */
        try {
            while(true){
                synchronized(lock){
                    if(worker_list.size()>0){
                        int toSend = worker_list.size();
                        out.writeInt(toSend);
                        out.flush();
                        ChunkedGPX chunked = worker_list.get(0);
                        out.writeObject(chunked);
                        out.flush();
                        worker_list.remove(0);
                    }
                }   
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
    
}
