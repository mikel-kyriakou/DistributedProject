import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ReceiveFromMaster extends Thread {
        ObjectInputStream in;
        private ArrayList<ChunkedGPX> threadList = new ArrayList<ChunkedGPX>();
        // ObjectOutputStream out;

    
    public ReceiveFromMaster(ObjectInputStream connection, ArrayList<ChunkedGPX> list){
        // try {
        //     // out = new ObjectOutputStream(connection.getOutputStream());
        //     // in = new ObjectInputStream(connection.getInputStream());

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        this.in = connection;
        this.threadList = list;
    }

    public ReceiveFromMaster(Socket connection, ArrayList<ChunkedGPX> list){
        try {
            // out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // this.in = connection;
        this.threadList = list;
    }

    public void run(){
        try { 
            // while(true){
            //     if(in.available()>0){
            //         ChunkedGPX received_chunked = (ChunkedGPX) in.readObject();
            //         System.out.println("received_chunked " + received_chunked);
            //     }
            // }

            while(true){
                if(in.available()>0){
                    int received_chunked = (int) in.readInt();
                    System.out.println("received_chunked " + received_chunked);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                // out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
