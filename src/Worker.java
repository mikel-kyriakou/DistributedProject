import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Properties;

public class Worker {
    ObjectOutputStream out= null ;
    ObjectInputStream in = null ;
    Socket requestSocket= null ;
    private ArrayList<ChunkedGPX> myWorkerList = new ArrayList<ChunkedGPX>();
    Object lock = new Object();
    Object resultsLock = new Object();
    private ArrayList<IntermidiateResult> resultsList = new ArrayList<IntermidiateResult>();

    public void establishConnection() {
        try {
            /* Get data from config file */
            String configFilePath = "src/config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);
            int port = Integer.valueOf(prop.getProperty("MASTER_PORT_FOR_WORKERS"));           

            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket("localhost", port);

            /* Create the streams to send and receive data from server and start the threads. */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            Thread receiver = new ReceiveFromMaster(in, myWorkerList, lock);
            receiver.start();
            Thread sender = new SendToMaster(out, resultsList, lock);
            sender.start();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /* This method start a threat for each worker that makes calculation for every inter result we receive. */
    public void calculate(){
        while(true){
            synchronized(lock){
                if(myWorkerList.size()>0){
                    Thread t = new WorkerCalculator(myWorkerList.get(0), resultsList, resultsLock);
                    t.start();
                    myWorkerList.remove(0);
                }
            }
        }
    }

    public static void main(String[] args){
        Worker w = new Worker();
        w.establishConnection();
        w.calculate();
    }
}
