import java.io.*;
import java.net.*;

public class Worker2 {
    private int id;
    ObjectOutputStream out= null ;
    ObjectInputStream in = null ;
    Socket requestSocket= null ;

    public Worker2(int id){
        this.id = id;
        // new ConnectionWithMaster().start();
        // establishConnection();
    }

    public void establishConnection() {
        try {

            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket("localhost", 4322); //kalitera na oriso se allo arxio to host kai to port kai na to paro apo ekei para na to diloso edo

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
