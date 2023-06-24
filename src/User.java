import java.io.*;
import java.net.*;
import java.util.Properties;

public class User extends Thread{
    private File user_route;

    public User(){
    }

    /* This method writes the results received from master to a txt file */
    // public void writeFile(Result result){ 
    //     try {
    //         FileWriter myWriter = new FileWriter("src/files/results.txt", true);
    //         myWriter.write(result.toString() + "\n");
    //         myWriter.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    public void writeFile(String result){
        try {
            FileWriter myWriter = new FileWriter("src/files/results.txt", true);
            myWriter.write(result + "\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file.";
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
            // user_route = new File("src\\gpxs\\segment1.gpx");
            // String fileContent = readFile(user_route);

            // /* Send the gpx */
            // out.writeInt(1);
            // out.writeUTF("segment");
            // out.writeUTF(fileContent);
            // out.flush();

            // /* Print the received result from server */
            // String myResult = (String)in.readUTF();
            // System.out.println(myResult);

            /* Get the gpx file */
            user_route = new File("src\\gpxs\\route4.gpx");
            String fileContent = readFile(user_route);

            /* Send the gpx */
            out.writeInt(2);
            // out.writeUTF("user4");
            out.writeUTF(fileContent);
            out.flush();

            /* Print the received result from server */
            String myResult = (String)in.readUTF();
            System.out.println(myResult);

            // /* Leaderboard */
            // out.writeInt(3);
            // out.writeUTF("user1");
            // out.flush();
            // int numLeaderboards = (int) in.readInt();
            // for(int i=0; i<numLeaderboards; i++){
            //     String name = (String) in.readUTF();
            //     System.out.println(name);
            //     int numRunners = (int) in.readInt();
            //     for(int j=0; j<numRunners; j++){
            //         String runner = (String) in.readUTF();
            //         String time = (String) in.readUTF();
            //         // long time = (long) in.readLong();
            //         System.out.println(runner + " " + time);
            //     }
            // }

            // /* Send the gpx */
            // out.writeInt(4);
            // out.writeUTF("user4");
            // out.flush();

            // /* Print the received result from server */
            // double sumDist = (double)in.readDouble();
            // double sumEle = (double)in.readDouble();
            // String sumTime = (String)in.readUTF();
            // // long sumTime = (long)in.readLong();
            // System.out.println("Total distance: " + sumDist + "\nTotal Elevation: " + sumEle + "\nTotal time: " + sumTime);


            /* Update file with the results */
            // writeFile(myResult);


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        // } catch(ClassNotFoundException e) {
        //     throw new RuntimeException(e);
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