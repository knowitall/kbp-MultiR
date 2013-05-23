/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/17/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class XMLParser {
    private Document xmlDoc;

    public XMLParser(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty: XMLParser Constructor");
        }

        try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            xmlDoc = db.parse(xmlFile);
            xmlDoc.getDocumentElement().normalize();

        } catch (Exception e) {
            System.out.println("IO Exception occurred, XMLParser constructor");
            e.printStackTrace();
        }
    }

    public List<Query> getQueries() {
        List<Query> queries = new ArrayList<Query>();
        NodeList nodes = xmlDoc.getElementsByTagName("query");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Query q = new Query(n);
                queries.add(q);
            }
        }
        return queries;
    }
}
