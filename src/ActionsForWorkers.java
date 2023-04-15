import java.io.*;
import java.net.*;


public class ActionsForWorkers extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;

    public ActionsForWorkers(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            

        } catch (IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
