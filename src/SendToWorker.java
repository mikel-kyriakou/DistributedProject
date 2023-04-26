import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SendToWorker extends Thread {
    // ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<ChunkedGPX> worker_list;
    private Object lock;


    public SendToWorker(Socket connection, ArrayList<ChunkedGPX> list, Object lock){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            // in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.worker_list = list;
        this.lock = lock;
    }

    public SendToWorker(ObjectOutputStream out, ArrayList<ChunkedGPX> list, Object lock){
        this.out = out;
        this.worker_list = list;
        this.lock = lock;
    }


    public void run(){
        try {
            // while(true){
            //     synchronized(lock){
            //         if(worker_list.size()>0){
            //             ChunkedGPX toSend = worker_list.get(0);
            //             out.writeObject(toSend);
            //             out.flush();
            //             worker_list.remove(0);
            //         }
            //     }   
            // }

            while(true){
                synchronized(lock){
                    if(worker_list.size()>0){
                        int toSend = worker_list.size();
                        out.writeInt(toSend);
                        out.flush();
                        ChunkedGPX chunked = worker_list.get(0);
                        out.writeObject(chunked);
                        out.flush();
                        worker_list.remove(0);
                        System.out.println(chunked.getWpt1());
                        System.out.println(chunked.getWpt2());
                        break;
                    }
                    else{
                        out.writeInt(0);
                        out.flush();
                    }
                }   
            }

            while(true){

                sleep(100);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
    
}
