import java.io.*;
import java.net.*;
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  
import java.text.DateFormat; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private HashMap<String, Double> sumDistanceThread = new HashMap<>();
    private Object sumDistanceThreadLock = new Object();
    private HashMap<String, Double> sumElevationThread = new HashMap<>();
    private Object sumElevationThreadLock = new Object();
    private HashMap<String, Long> sumTimeThread = new HashMap<>();
    private Object sumTimeThreadLock = new Object();


    public ActionsForUsers(Socket connection, ArrayList<ArrayList<ChunkedGPX>> list, int[] index, Object[] lock, 
                            HashMap<String, Integer> counters, Object routesCountersLock, 
                            HashMap<String, Integer> waypointsCounters, Object waypointsCountersLock, 
                            HashMap<String, Result> results, Object resultsLock, 
                            HashMap<Segment, ArrayList<UserLeaderboard>> segments, Object segmentsLock, 
                            HashMap<String, Double> sumDistanceThread, Object sumDistanceThreadLock, 
                            HashMap<String, Double> sumElevationThread, Object sumElevationThreadLock, 
                            HashMap<String, Long> sumTimeThread, Object sumTimeThreadLock) {
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
        this.sumDistanceThread = sumDistanceThread;
        this.sumDistanceThreadLock = sumDistanceThreadLock;
        this.sumElevationThread = sumElevationThread;
        this.sumElevationThreadLock = sumElevationThreadLock;
        this.sumTimeThread = sumTimeThread;
        this.sumTimeThreadLock = sumTimeThreadLock;
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
    public String getgpxfile(String f){
        try{
            //an instance of factory that gives a document builder  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            //an instance of builder to parse the specified xml file  
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(f));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();  
            Element x = doc.getDocumentElement();
            String creator = x.getAttribute("creator");
            /* Determine if it is a segment by the creators name. Segments have creator as "gpxgenerator.com" */
            // if(creator.equals("gpxgenerator.com")){
            //     Segment s = new Segment(f);
            //     synchronized(segmentsLockThread){
            //         segmentsThread.put(s, new ArrayList<>());
            //     }
            //     return false;
            // }
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

            return creator;

        }
        catch (Exception e){
        e.printStackTrace();
        }  
            
        return null;
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
                System.out.println("Actions for users: wpt counter else");
                waypointsCounters.put(user, waypointsCounters.get(user)+wpt_list.size());
            }
        }

    }

    /* This method waits for result to be added to results list and then returns the result */
    public Result waitForResult() {
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
                int i=0, j = 0, j_start = 0;

                for(j = 0; j<=wpt_list.size() - segment.size(); j++){
                    if(wpt_list.get(j).equals(segment.get(i))){
                        System.out.println("Action for users: found first wpt match");
                        j_start = j;
                        break;
                    }
                }

                while(true){
                    System.out.println("Action for users: segment while");
                    if(wpt_list.size() - j <= 1){
                        System.out.println("Action for users: segment break");
                        break;
                    }

                    if(segment.size() - i <= 1){
                        System.out.println("Action for users: found segment");
                        updateWptsSegment(s.hashCode(), j_start, j);
                        System.out.println("Action for users: segment break");
                        break;
                    }
                    if(segment.get(i+1).equals(wpt_list.get(j+1))){
                        i++;
                        j++;
                    }
                    else if(segment.get(i).equals(wpt_list.get(j+1))){
                        j++;
                    }
                    else{
                        System.out.println("Action for users: segment break");
                        break;
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

    public String setRoute(String fileContent) throws IOException{
        System.out.println("Actions for users: getgpx");
        String user = getgpxfile(fileContent);
        
        System.out.println("Actions for users: updateCounters");
        updateCounters();

        System.out.println("Actions for users: chech segment");
        checkRouteHasSegment();

        System.out.println("Actions for users: round robin");
        roundRobin(wpt_list, list, index, lock);

        System.out.println("Actions for users: wait for results");
        Result result = waitForResult();

        String resultToReturn = result.toString();

        try{
            out.writeUTF(resultToReturn);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return user;
    }

    public void setSegment(String name, String fileContent){
        Segment s = new Segment(name, fileContent);
        synchronized(segmentsLockThread){
            segmentsThread.put(s, new ArrayList<>());
        }

        try {
            out.writeUTF("Segment set");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStats(String user){
        double userDistance, userElevation;
        long userTime;

        synchronized(sumDistanceThreadLock){
            if(!sumDistanceThread.containsKey(user)){
                return;
            }
            userDistance = sumDistanceThread.get(user);
        }

        synchronized(sumElevationThreadLock){
            userElevation = sumElevationThread.get(user);
        }
        
        synchronized(sumTimeThreadLock){
            userTime = sumTimeThread.get(user);
        }

        try {
            out.writeDouble(userDistance);
            out.writeDouble(userElevation);
            out.writeUTF(millisecondsToString(userTime));
            // out.writeLong(userTime);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLeaderboards(String user){
        System.out.println("Action for users: in send leaderboards ");
        ArrayList<Segment> toSend = new ArrayList<>();
        synchronized(segmentsLockThread){
            for(Segment s:segmentsThread.keySet()){
                ArrayList<UserLeaderboard> list = segmentsThread.get(s);
                for(UserLeaderboard ul:list){
                    System.out.println("Action for users: leaderboards user check " + ul.getUser() + " " + user);
                    if(ul.getUser().equals(user)){
                        toSend.add(s);
                        break;
                    }
                }
            }
        }

        try {
            out.writeInt(toSend.size());
            for(Segment s:toSend){
                out.writeUTF(s.getName());
                ArrayList<UserLeaderboard> list = segmentsThread.get(s);
                out.writeInt(list.size());
                for(UserLeaderboard ul:list){
                    out.writeUTF(ul.getUser());
                    out.writeUTF(millisecondsToString(ul.getTime()));
                    // out.writeLong(ul.getTime());
                }
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String millisecondsToString(long ms){
        long totalSeconds = ms/1000;
        int hours = (int) totalSeconds / 3600;
        int minutes = (int)(totalSeconds % 3600) / 60;
        int seconds = (int)totalSeconds % 60;

        return(hours + ":" + minutes + ":" + seconds);
    }

    public void run() {
        try {

            String segmentGPX = null, routeGPX = null, username = null, segmentName = null;

            int choice = (int)in.readInt();
            switch (choice) {
                case 1:
                    segmentName = (String)in.readUTF();
                    segmentGPX = (String)in.readUTF();
                    setSegment(segmentName, segmentGPX);
                    break;
                
                case 2:
                    routeGPX = (String)in.readUTF();
                    String user = setRoute(routeGPX);
                    sendStats(user);
                    break;

                case 4:
                    username = (String)in.readUTF();
                    sendLeaderboards(username);
                    break;
            
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
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