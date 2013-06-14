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
        if (args.length < 2) {
            System.out.println("Missing command line argument: location of kbp query file, pre-processed data file and output directory required");
            System.out.println("Usage: /data/file/directory /data/output/directory");
        } else {

            dataLocation = args[0];
            processedOutputLocation = args[1];
            ProtobufBuilder.buildProtobufsForTest(dataLocation, processedOutputLocation);

        }
    }
}
