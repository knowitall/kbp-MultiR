import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.Arrays;

public class Query {
    public String entity;
    public String queryId;
    public String type;
    public String docId;
    public String[] ignore;
    public String nodeId;

    public Query(Node n) {
        Element e = (Element) n;
        queryId = e.getAttribute("id");
        entity = e.getElementsByTagName("name").item(0).getTextContent();
        type = e.getElementsByTagName("enttype").item(0).getTextContent();
        docId = e.getElementsByTagName("docid").item(0).getTextContent();
        nodeId = e.getElementsByTagName("nodeid").item(0).getTextContent();
        ignore = new String[0];
        if (e.getElementsByTagName("ignore").item(0) != null) {
            ignore = e.getElementsByTagName("ignore").item(0).getTextContent().split(" ");
        }

    }

    public String toString() {
        return "Entity      : " + entity + "\n" +
               "Type        : " + type + "\n" +
               "Document ID : " + docId + "\n" +
               "Node ID     : " + nodeId + "\n" +
               "Ignore:     : " + Arrays.toString(ignore) + "\n\n";

    }
}