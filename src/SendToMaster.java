import java.io.*;
import java.util.ArrayList;

public class SendToMaster extends Thread {
    // ObjectInputStream in;
    ObjectOutputStream out;
    ArrayList<IntermidiateResult> list;
    Object lock;


    public SendToMaster(ObjectOutputStream connection, ArrayList<IntermidiateResult> list, Object lock){
        // try {
        //     out = new ObjectOutputStream(connection.getOutputStream());
        //     // in = new ObjectInputStream(connection.getInputStream());

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        this.out = connection;
        this.list = list;
        this.lock = lock;
    }

    public void run(){
        try {
            while(true){
                synchronized(lock){
                    if(list.size()>0){
                        int toSend = list.size();
                        out.writeInt(toSend);
                        out.flush();
                        IntermidiateResult result = list.get(0);
                        out.writeObject(result);
                        out.flush();
                        list.remove(0);
                    }
                    else{
                        out.writeInt(0);
                        out.flush();
                    }
                }   
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }/* finally {
            try {
                // in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }*/

    }
    
}

