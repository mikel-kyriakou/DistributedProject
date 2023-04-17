import java.io.*;
import java.net.*;


public class ActionsForWorkers extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    ChunkedGPX packetToSend;

    public ActionsForWorkers(Socket connection, int id, Waypoint wpt1, Waypoint wpt2) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            packetToSend = new ChunkedGPX(id, wpt1, wpt2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out.writeObject(packetToSend);
            out.flush();

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
