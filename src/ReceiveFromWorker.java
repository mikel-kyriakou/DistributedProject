import java.io.*;
import java.net.Socket;

public class ReceiveFromWorker extends Thread {
        ObjectInputStream in;
        // ObjectOutputStream out;
        private int[] test;

    
    public ReceiveFromWorker(Socket connection, int[] test){
        try {
            // out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.test = test;
    }

    public void run(){
        try { 
            while(true){
                // int r;
                // r = (int) in.readInt();
                // test[0] = r;
            }
        // } catch (IOException e) {
        //     e.printStackTrace();
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
