import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Master{
    private static int number_of_workers;
    private static ArrayList<ArrayList<ChunkedGPX>> workerList = new ArrayList<ArrayList<ChunkedGPX>>();
    private int[] workerIndex = {0};
    private static Object[] workerListLock;
    private HashMap<String, Integer> usersRoutesCounters = new HashMap<>();
    private Object usersRoutesCountersLock = new Object();
    private HashMap<String, Integer> usersWaypointsCounters = new HashMap<>();
    private Object usersWaypointsCountersLock = new Object();
    private HashMap<String, Double> sumDistance = new HashMap<>();
    private Object sumDistanceLock = new Object();
    private HashMap<String, Double> sumElevation = new HashMap<>();
    private Object sumElevationLock = new Object();
    private HashMap<String, Long> sumTime = new HashMap<>();
    private Object sumTimeLock = new Object();
    private HashMap<String, Double> sumSpeed = new HashMap<>();
    private Object sumSpeedLock = new Object();
    private HashMap<String, Result> results = new HashMap<>();
    private Object resultsLock = new Object();

    
    public static void main(String args[]) {
        Master myMaster = new Master();

        /* Get data from config file */
        try {
            String configFilePath = "src/config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);
            number_of_workers = Integer.valueOf(prop.getProperty("NUMBER_OF_WORKERS"));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Initialize workerListLock */
        workerListLock = new Object[number_of_workers];

        for(int i=0; i<number_of_workers; i++){
            ArrayList<ChunkedGPX> row = new ArrayList<ChunkedGPX>();
            workerList.add(row);

            workerListLock[i] = new Object();
        }

        /* Connect with workers */
        System.out.println("Connect workers");
        myMaster.connectWithWorkers();

        /* Start reducer thread */
        myMaster.startReducer();

        /* Open server for users */
        System.out.println("Connect users");
        myMaster.openServerForUser();

        /* Close sockets */
        myMaster.closeWorkersSocket();

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
                Thread dUser = new ActionsForUsers(providerSocketUser, workerList, workerIndex, workerListLock, usersRoutesCounters, usersRoutesCountersLock, usersWaypointsCounters, usersWaypointsCountersLock, results, resultsLock);
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
            /* Index for workers */
            int i = 0;

            /* Create Server Socket */
            sWorker = new ServerSocket(5432, number_of_workers);


            while (i<number_of_workers) {
                /* Accept the connection */
                providerSocketWorker = sWorker.accept();

                ObjectOutputStream out = new ObjectOutputStream(providerSocketWorker.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(providerSocketWorker.getInputStream());

                /* Handle the request */
                Thread sender = new SendToWorker(out, workerList.get(i), workerListLock[i]);
                sender.start();

                Thread receiver = new ReceiveFromWorker(in, usersWaypointsCounters, usersWaypointsCountersLock, sumDistance, sumDistanceLock, sumElevation, sumElevationLock, sumTime, sumTimeLock, sumSpeed, sumSpeedLock);
                receiver.start();

                i++;
                
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void closeWorkersSocket(){
        try {
            providerSocketWorker.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void startReducer(){
        Thread reducer = new Reducer(usersRoutesCounters, usersRoutesCountersLock, usersWaypointsCounters, usersWaypointsCountersLock, sumDistance, sumDistanceLock, sumElevation, sumElevationLock, sumTime, sumTimeLock, results, resultsLock);
        reducer.start();
    }

}
