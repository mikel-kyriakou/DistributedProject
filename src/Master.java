import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Master{
    private static final int number_of_workers = 3;
    private static Worker2[] arrayOfWorkers = new Worker2[number_of_workers];
    // private static ArrayList<ArrayList<Waypoint>> master_list = new ArrayList<ArrayList<Waypoint>>();
    private static ArrayList<ArrayList<ChunkedGPX>> workerList = new ArrayList<ArrayList<ChunkedGPX>>();
    private int[] workerIndex = {0};
    private static Object workerListLock = new Object();

    
    public static void main(String args[]) {
        Master myMaster = new Master();

        // Object workerListLock = new Object();

        for(int i=0; i<number_of_workers; i++){
            arrayOfWorkers[i] = new Worker2(i);
            ArrayList<ChunkedGPX> row = new ArrayList<ChunkedGPX>();
            workerList.add(row);
        }

        myMaster.openServerForUser();
        myMaster.openServerForWorkers();

        for(int i=0; i<number_of_workers; i++){
            arrayOfWorkers[i].establishConnection();
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
                Thread dUser = new ActionsForUsers(providerSocketUser, workerList, workerIndex, workerListLock);
                dUser.start();

                System.out.println(workerList.get(0).size());
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
            int i = 0;

            /* Create Server Socket */
            sWorker = new ServerSocket(5432, 10);


            while (true) {
                /* Accept the connection */
                providerSocketWorker = sWorker.accept();

                /* Handle the request */
                Thread sender = new SendToWorker(providerSocketWorker, workerList.get(i), workerListLock);
                Thread dWorker = new ActionsForWorkers(providerSocketWorker, workerList.get(i));
                dWorker.start();
                i++;
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
