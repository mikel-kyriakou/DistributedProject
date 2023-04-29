import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Worker {
    private int id;
    ObjectOutputStream out= null ;
    ObjectInputStream in = null ;
    Socket requestSocket= null ;
    private ArrayList<ChunkedGPX> myWorkerList = new ArrayList<ChunkedGPX>();
    Object lock = new Object();
    Object resultsLock = new Object();
    private ArrayList<IntermidiateResult> resultsList = new ArrayList<IntermidiateResult>();

    public Worker(int id){
        this.id = id;
    }

    public void establishConnection() {
        try {

            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket("localhost", 5432); //kalitera na oriso se allo arxio to host kai to port kai na to paro apo ekei para na to diloso edo

            /* Create the streams to send and receive data from server */
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
        }/* finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } */
    }

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
        Worker w = new Worker(0);
        w.establishConnection();
        w.calculate();
    }
}
