import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Master extends Thread{
    private static final int number_of_workers = 3;
    private static Worker[] arrayOfWorkers = new Worker[number_of_workers];
    // private static ArrayList<ArrayList<Waypoint>> master_list = new ArrayList<ArrayList<Waypoint>>();
    private static ArrayList<ArrayList<ChunkedGPX>> workerList = new ArrayList<ArrayList<ChunkedGPX>>();
    private int workerIndex = 0;

    
    public static void main(String args[]) {
        for(int i=0; i<number_of_workers; i++){
            arrayOfWorkers[i] = new Worker(i);
            ArrayList<ChunkedGPX> row = new ArrayList<ChunkedGPX>();
            workerList.add(row);
        }

        new Master().openServerForUser();

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
                Thread dUser = new ActionsForUsers(providerSocketUser, workerList, workerIndex);
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

    // void openServerForWorkers() {
    //     try {
    //         /* Create Server Socket */
    //         sWorker = new ServerSocket(4322, 10);


    //         while (true) {
    //             /* Accept the connection */
    //             providerSocketWorker = sWorker.accept();

    //             /* Handle the request */
    //             Thread dWorker = new ActionsForWorkers(providerSocketWorker, workerList);
    //             dWorker.start();
    //         }

    //     } catch (IOException ioException) {
    //         ioException.printStackTrace();
    //     } finally {
    //         try {
    //             providerSocketWorker.close();
    //         } catch (IOException ioException) {
    //             ioException.printStackTrace();
    //         }
    //     }
    // }

    
    // void roundRobinWorkers(){
    //     int workerId=0;

    //     while(true){
    //         if(master_list.size()>0){
    //             if(master_list.get(0).size() > 1){

    //                 workerId = (workerId+1)%number_of_workers;
    //                 master_list.get(0).remove(0);
    //             }
    //             else{
    //                 master_list.remove(0);
    //             }
    //         }
    //     }
    // }
    
}
