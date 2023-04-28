import java.io.*;
import java.net.*;
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;  
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  
import java.text.DateFormat; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; 
import java.util.TimeZone;


public class ActionsForUsers extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<Waypoint> wpt_list = new ArrayList<Waypoint>();
    private ArrayList<ArrayList<ChunkedGPX>> list;
    private int[] index;
    private Object[] lock;


    // public ActionsForUsers(Socket connection) {
    //     try {
    //         out = new ObjectOutputStream(connection.getOutputStream());
    //         in = new ObjectInputStream(connection.getInputStream());

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    public ActionsForUsers(Socket connection, ArrayList<ArrayList<ChunkedGPX>> list, int[] index, Object[] lock) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.list = list;
        this.index = index;
        this.lock = lock;
    }

    public void roundRobin(ArrayList<Waypoint> wpt_list, ArrayList<ArrayList<ChunkedGPX>> list, int[] index, Object[] lock){
        Waypoint wpt1, wpt2;
        ChunkedGPX wpts;


        while(wpt_list.size()>1){
            wpt1 = wpt_list.get(0);
            wpt2 = wpt_list.get(1);
            wpts = new ChunkedGPX(wpt1, wpt2);
            synchronized(lock[index[0]]){
                list.get(index[0]).add(wpts);
                index[0] = (index[0]+1)%(list.size());
            }
            wpt_list.remove(0);
        }
    }


    public void getgpxfile(File f){ //synchronize
        try{
            //an instance of factory that gives a document builder  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            //an instance of builder to parse the specified xml file  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse(f);  
            doc.getDocumentElement().normalize();  
            Element x = doc.getDocumentElement();
            String creator = x.getAttribute("creator");
            NodeList nodeList = doc.getElementsByTagName("wpt");  
            // nodeList is not iterable, so we are using for loop  
            for (int itr = 0; itr < nodeList.getLength(); itr++)   
            {
                Element wpt = (Element) nodeList.item(itr); 
                Node node = nodeList.item(itr);  
                double my_lat = Double.parseDouble(wpt.getAttribute("lat"));
                double my_lon = Double.parseDouble(wpt.getAttribute("lon"));
                double my_ele = 0;
                Date date = null;
                if (node.getNodeType() == Node.ELEMENT_NODE)   
                {  
                    Element eElement = (Element) node;   
                    my_ele = Double.parseDouble(eElement.getElementsByTagName("ele").item(0).getTextContent());
                    String time=eElement.getElementsByTagName("time").item(0).getTextContent();
                    String dateTimeString = time; 
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
                    date = dateFormat.parse(dateTimeString);
                }

                wpt_list.add(new Waypoint(creator, my_lat, my_lon, my_ele, date));

            } 
        }
        catch (Exception e){  
        e.printStackTrace();  
        }  

    }

    public void run() {
        try {
            File f= (File) in.readObject();

            getgpxfile(f);

            roundRobin(wpt_list, list, index, lock);

            out.writeObject(wpt_list.get(0));
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}