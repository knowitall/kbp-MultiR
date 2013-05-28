/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/17/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */

import com.google.protobuf.CodedOutputStream;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



public class KBPEvaluator {
    public static final String kbpFile = "/home/bdwalker/multiR/2011_queries/data/queries.xml";

    public static void main(String[] args) throws IOException {
        Map<String, String> freeBaseToKBP = new HashMap<String, String>();

        RelationECML featureExtractor = new RelationECML();


    }



    public static void prepareForFeatureExtraction(Query query) {

    }

    public static void parseQueryData(List<Query> queries)
            throws FileNotFoundException {

        /*for (Query q: queries) {
            QueryParser.outputDataForQuery(q);
        }*/

        QueryParser.outputDataForQuery(queries.get(1));
    }
}
