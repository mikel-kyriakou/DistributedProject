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
import java.util.Collection;
import java.util.Date; 
import java.util.TimeZone;
import java.util.HashMap;



public class ActionsForUsers extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<Waypoint> wpt_list = new ArrayList<Waypoint>();
    private ArrayList<ArrayList<ChunkedGPX>> list;
    private int[] index;
    private Object[] lock;
    private HashMap<String, Integer> routesCounters;
    private Object routesCountersLock;
    private HashMap<String, Integer> waypointsCounters;
    private Object waypointsCountersLock;
    private String user;
    private HashMap<String, Result> results = new HashMap<>();
    private Object resultsLock = new Object();
    private HashMap<Segment, ArrayList<UserLeaderboard>> segmentsThread = new HashMap<>();
    private Object segmentsLockThread = new Object();

    public ActionsForUsers(Socket connection, ArrayList<ArrayList<ChunkedGPX>> list, int[] index, Object[] lock, HashMap<String, Integer> counters, Object routesCountersLock, HashMap<String, Integer> waypointsCounters, Object waypointsCountersLock, HashMap<String, Result> results, Object resultsLock, HashMap<Segment, ArrayList<UserLeaderboard>> segments, Object segmentsLock) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.list = list;
        this.index = index;
        this.lock = lock;
        this.routesCounters = counters;
        this.routesCountersLock = routesCountersLock;
        this.waypointsCounters = waypointsCounters;
        this.waypointsCountersLock = waypointsCountersLock;
        this.results = results;
        this.resultsLock = resultsLock;
        this.segmentsThread = segments;
        this.segmentsLockThread = segmentsLock;
    }

    /* This method adds chunked gpxs to workers lists using round robin */
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


    /* This methos reads gpx file and adds waypoints to a local list */
    public Boolean getgpxfile(File f){
        try{
            //an instance of factory that gives a document builder  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            //an instance of builder to parse the specified xml file  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse(f);  
            doc.getDocumentElement().normalize();  
            Element x = doc.getDocumentElement();
            String creator = x.getAttribute("creator");
            /* Determine if it is a segment by the creators name. Segments have creator as "gpxgenerator.com" */
            if(creator.equals("gpxgenerator.com")){
                Segment s = new Segment(f);
                synchronized(segmentsLockThread){
                    segmentsThread.put(s, new ArrayList<>());
                }
                return false;
            }
            this.user = creator;
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
            
            return true;

        }
        catch (Exception e){  
        e.printStackTrace();  
        }  
        
        return false;
    }

    /* This method updates counters to keep count of routes and waypoints received by each user */
    public void updateCounters(){
        synchronized(routesCountersLock){
            if(routesCounters.get(user) == null){
                routesCounters.put(user, 1);
            }
            else{
                routesCounters.put(user, routesCounters.get(user)+1);
            }
        }

        synchronized(waypointsCountersLock){
            if(waypointsCounters.get(user) == null){
                waypointsCounters.put(user, wpt_list.size());
            }
            else{
                waypointsCounters.put(user, waypointsCounters.get(user)+wpt_list.size());
            }
        }

    }

    /* This method waits for result to be added to results list and then returns the result */
    public Result waitForResult(){
        while(true){
            synchronized(resultsLock){
                if(results.get(user)!=null){
                    Result toReturn = results.get(user);
                    results.remove(user);
                    return toReturn;
                }
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Check if the route contains any segment */
    public void checkRouteHasSegment(){

        synchronized(segmentsLockThread){
            if(segmentsThread.size()==0){
                return;
            }
            
            for(Segment s:segmentsThread.keySet()){
                ArrayList<Waypoint> segment = s.getWpts();
                for(int i=0; i<wpt_list.size(); i++){
                    if(wpt_list.get(i).equals(segment.get(0)) && wpt_list.size()>i+segment.size()){
                        for(int j=0; j<segment.size(); j++){
                            if(wpt_list.get(i+j).equals(segment.get(j))){
                                break;
                            }
                        }
                        updateWptsSegment(s.hashCode(), i, i+segment.size());
                    }
                }
            }
        }
    }

    /* Give Waypoints the name of the segment */
    public void updateWptsSegment(int name, int startIndex, int stopIndex){
        for(int i=startIndex; i<=stopIndex;i++){
            wpt_list.get(i).addSegment(name);
        }
    }

    public void run() {
        try {
            File f= (File) in.readObject();

            Boolean isRoute = getgpxfile(f);

            if(isRoute){

                updateCounters();

                checkRouteHasSegment();

                roundRobin(wpt_list, list, index, lock);

                Result result = waitForResult();

                out.writeObject(result);
                out.flush();
            }
            else{
                out.writeObject("Segment set");
                out.flush();
            }

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