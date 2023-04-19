import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SendToWorker extends Thread {
    // ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<ChunkedGPX> worker_list;


    public SendToWorker(Socket connection, ArrayList<ChunkedGPX> list){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            // in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.worker_list = list;
    }

    public void run(){
        try {  
            synchronized(worker_list){
                while(worker_list.size()>0){
                    out.writeObject(worker_list.get(0));
                    out.flush();
                    worker_list.remove(0);
                }
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
    
}
