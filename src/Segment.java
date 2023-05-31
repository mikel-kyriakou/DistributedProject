import java.io.*;
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


public class Segment {
    private String name;
    private ArrayList<Waypoint> segmentWpts = new ArrayList<>();

    public Segment(File f){
        getSegmentFile(f);
    }


    public Segment(String name, ArrayList<Waypoint> segmentWpts) {
        this.name = name;
        this.segmentWpts = segmentWpts;
    }

    public Segment(String name, File f){
        this.name = name;
        getSegmentFile(f);
    }

    public void getSegmentFile(File f){
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

                segmentWpts.add(new Waypoint(creator, my_lat, my_lon, my_ele, date));
            }
        }
        catch (Exception e){  
        e.printStackTrace();  
        }
    }

    public ArrayList<Waypoint> getWpts(){
        return segmentWpts;
    }

    public Waypoint getWptAtIndex(int index){
        if(index<0){
            return segmentWpts.get(segmentWpts.size()+index);
        }
        return segmentWpts.get(index);
    }


    @Override
    public String toString() {
        return "{" +
            "name='" + this.hashCode() + "'" +
            "}";
    }

}
