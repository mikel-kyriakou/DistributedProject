import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ReceiveFromMaster extends Thread {
        ObjectInputStream in;
        private ArrayList<ChunkedGPX> threadList = new ArrayList<ChunkedGPX>();
        // ObjectOutputStream out;

    
    public ReceiveFromMaster(Socket connection, ArrayList<ChunkedGPX> list){
        try {
            // out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.threadList = list;
    }

    public void run(){
        try { 
            while(true){
                ChunkedGPX test = (ChunkedGPX) in.readObject();
                threadList.add(test);
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
