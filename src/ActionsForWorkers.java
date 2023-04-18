import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class ActionsForWorkers extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<ChunkedGPX> worker_list;

    public ActionsForWorkers(Socket connection, ArrayList<ChunkedGPX> list) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.worker_list = list;
    }



    public void run() {
        try {

            out.writeObject(worker_list);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
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
