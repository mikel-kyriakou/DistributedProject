import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;  
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  
import java.io.File;  
import java.text.DateFormat; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.TimeZone;
import java.util.ArrayList;



public class ReadXMLFile
{  
    public static void main(String argv[])   
    {  
        ArrayList<Waypoint> list = new ArrayList<Waypoint>();
        try   
        {  
            //creating a constructor of file class and parsing an XML file  src\gpxs\route1.gpx
            File file = new File("src/gpxs/route1.gpx");
            //an instance of factory that gives a document builder  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            //an instance of builder to parse the specified xml file  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse(file);  
            doc.getDocumentElement().normalize();  
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());  
            Element x = doc.getDocumentElement();
            String creator = x.getAttribute("creator");
            System.out.println("Creator:" + creator);
            NodeList nodeList = doc.getElementsByTagName("wpt");  
            // nodeList is not iterable, so we are using for loop  
            for (int itr = 0; itr < nodeList.getLength(); itr++)   
            {
                Element wpt = (Element) nodeList.item(itr); 
                Node node = nodeList.item(itr);  
                double my_lat = Double.parseDouble(wpt.getAttribute("lat"));
                double my_lon = Double.parseDouble(wpt.getAttribute("lon"));
                System.out.println("\nNode Name :" + node.getNodeName());  
                System.out.println("latitude: "+ wpt.getAttribute("lat"));
                System.out.println("longtude: "+ wpt.getAttribute("lon"));
                double my_ele = 0;
                Date date = null;
                if (node.getNodeType() == Node.ELEMENT_NODE)   
                {  
                    Element eElement = (Element) node;   
                    my_ele = Double.parseDouble(eElement.getElementsByTagName("ele").item(0).getTextContent());
                    System.out.println("elevation: "+ eElement.getElementsByTagName("ele").item(0).getTextContent());  
                    System.out.println("time: "+ eElement.getElementsByTagName("time").item(0).getTextContent());
                    String time=eElement.getElementsByTagName("time").item(0).getTextContent();
                    String dateTimeString = time; 
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
                    date = dateFormat.parse(dateTimeString); 
                    System.out.println(date);
                }  

                list.add(new Waypoint(creator, my_lat, my_lon, my_ele, date));

            } 

            for(Waypoint w:list){
                System.out.println(w);
            }

        }   
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  
}  