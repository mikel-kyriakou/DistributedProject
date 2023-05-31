import java.io.*;
import java.net.*;
import java.util.Properties;

public class UserForSegment extends Thread{
    private File user_route;

    /* This method writes the results received from master to a txt file */
    public void writeFile(Result result){ 
        try {
            FileWriter myWriter = new FileWriter("src/files/results.txt", true);
            myWriter.write(result.toString() + "\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;

        try {
            /* Get data from config file */
            String configFilePath = "src/config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);
            int port = Integer.valueOf(prop.getProperty("MASTER_PORT_FOR_USERS"));
            String host = prop.getProperty("MASTER_HOST");

            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host, port);

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /* Get the gpx file */
            user_route = new File("src/gpxs/segment2.gpx");

            /* Send the gpx */
            out.writeObject(user_route);
            out.flush();

            /* Get message */
            Object message = in.readObject();
            System.out.println(message);

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new UserForSegment().start();
    }
}
