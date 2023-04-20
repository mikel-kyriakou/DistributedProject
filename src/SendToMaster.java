import java.io.*;
import java.net.Socket;

public class SendToMaster extends Thread {
    // ObjectInputStream in;
    ObjectOutputStream out;


    public SendToMaster(Socket connection){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            // in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){
        try {
            while(true){
                out.writeInt(0);
                out.flush();
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

