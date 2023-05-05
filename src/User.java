import java.io.*;
import java.net.*;
import java.util.Properties;

public class User extends Thread{
    private File user_route;

    public User(){
    }

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
            user_route = new File("src/gpxs/route6.gpx");

            /* Send the gpx */
            out.writeObject(user_route);
            out.flush();


            /* Print the received result from server */
            Result myResult = (Result) in.readObject();
            System.out.println(myResult);

            /* Update file with the results */
            writeFile(myResult);


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
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
        new User().start();
    }
}