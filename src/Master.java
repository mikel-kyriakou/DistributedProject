import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Master extends Thread{
    private static final int number_of_workers = 3;
    private static Worker[] arrayOfWorkers = new Worker[number_of_workers];
    private static ArrayList<ArrayList<Waypoint>> master_list = new ArrayList<ArrayList<Waypoint>>();
    
    public static void main(String args[]) {
        new Master().openServerForUser();

        for(int i=0; i<number_of_workers; i++){
            arrayOfWorkers[i] = new Worker();
        }
    }

    /* Define the socket that receives requests */
    ServerSocket sUser;
    ServerSocket sWorker;

    /* Define the socket that is used to handle the connection */
    Socket providerSocketUser;
    Socket providerSocketWorker;

    void openServerForUser() {
        try {

            /* Create Server Socket */
            sUser = new ServerSocket(4321, 10);


            while (true) {
                /* Accept the connection */
                providerSocketUser = sUser.accept();

                /* Handle the request */
                Thread dUser = new ActionsForUsers(providerSocketUser, master_list);
                dUser.start();
            }

            

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocketUser.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void openServerForWorkers() {
        try {
            /* Create Server Socket */
            sWorker = new ServerSocket(4322, 10);


            while (true) {
                /* Accept the connection */
                providerSocketWorker = sWorker.accept();

                /* Handle the request */
                Thread dWorker = new ActionsForWorkers(providerSocketWorker);
                dWorker.start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocketWorker.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    
}
