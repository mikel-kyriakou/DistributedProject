import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Master{
    private static final int number_of_workers = 1;
    // private static Worker2[] arrayOfWorkers = new Worker2[number_of_workers];
    // private static ArrayList<ArrayList<Waypoint>> master_list = new ArrayList<ArrayList<Waypoint>>();
    private static ArrayList<ArrayList<ChunkedGPX>> workerList = new ArrayList<ArrayList<ChunkedGPX>>();
    private int[] workerIndex = {0};
    private static Object[] workerListLock = new Object[number_of_workers];
    private static int[] fromworker = {1};

    
    public static void main(String args[]) {
        Master myMaster = new Master();

        for(int i=0; i<number_of_workers; i++){
            ArrayList<ChunkedGPX> row = new ArrayList<ChunkedGPX>();
            workerList.add(row);

            workerListLock[i] = new Object();
        }

        System.out.println("Connect workers");
        myMaster.connectWithWorkers();

        System.out.println("Connect users");
        myMaster.openServerForUser();

        myMaster.closeWorkersSocker();
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

    void connectWithWorkers() {
        try {
            int i = 0;

            /* Create Server Socket */
            sWorker = new ServerSocket(5432, number_of_workers);


            while (i<number_of_workers) {
                /* Accept the connection */
                providerSocketWorker = sWorker.accept();

                ObjectOutputStream out = new ObjectOutputStream(providerSocketWorker.getOutputStream());

                /* Handle the request */
                Thread sender = new SendToWorker(out, workerList.get(i), workerListLock[i]);
                sender.start();

                // out.writeInt(0);
                // out.flush();

                // Thread receiver = new ReceiveFromWorker(providerSocketWorker, fromworker);
                // receiver.start();
                i++;
                
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void closeWorkersSocker(){
        try {
            providerSocketWorker.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
