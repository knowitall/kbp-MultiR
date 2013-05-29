/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/17/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.*;

public class KBPEvaluator {
    private static String dataLocation;
    private static String processedOutputLocation;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Missing command line argument: location of kbp query file, pre-processed data file and output directory required");
            System.out.println("Usage: /query/file /data/file/directory /data/output/directory");
        } else {

            String queryFile = args[0];
            dataLocation = args[1];
            processedOutputLocation = args[2];

            // parse xml query file obtained from kbp
            XMLParser parser = new XMLParser(queryFile);

            List<Query> queries = parser.getQueries();

            for (Query q: queries) {
                if (q.entity.equals("Barry Goldwater"))
                    slotFillSingleQuery(q, true);
            }
        }
    }

    public static void slotFillSingleQuery(Query query, Boolean overwrite)
            throws IOException {

        QueryParser.outputDataForQuery(query, dataLocation, processedOutputLocation, overwrite);
        String file = ProtobufBuilder.buildProtobufsForTest(processedOutputLocation, "..", query);
        //ProcessBuilder pb = new ProcessBuilder("java", "-jar", query.queryId + "_" + query.entity.replace(" ", "_"), "-dir", "/home/bdwalker/multiR/results/");
        //pb.start();


    }
}
