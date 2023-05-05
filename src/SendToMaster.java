import java.io.*;
import java.util.ArrayList;

public class SendToMaster extends Thread {
    ObjectOutputStream out;
    ArrayList<IntermidiateResult> list;
    Object lock;

    public SendToMaster(ObjectOutputStream connection, ArrayList<IntermidiateResult> list, Object lock){
        this.out = connection;
        this.list = list;
        this.lock = lock;
    }

    public void run(){
        /* We are constanlty sending the size of the results list 
        and when its over 0, we send an inter result and then remove it from the list. */
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
        }
   }  
}

