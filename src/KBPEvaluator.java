/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/17/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

public class KBPEvaluator {
    private static String dataLocation;
    private static String trainedProtobutLocation;
    private static String processedModelDirectory;
    private static String protobufDir;
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 4) {
            System.out.println("Missing required command line argument: location of kbp query file, pre-processed data file and output directory required");
            System.out.println("Usage: processed_file_directory /directory/model_protobuf.pb.gz testing_protobuf_output_directory processed_model_output_directory");
        } else {

            dataLocation = args[0];
            trainedProtobutLocation = args[1];
            protobufDir = args[2];
            processedModelDirectory = args[3];
            //process corpus and output testing protobuf file
            List<EntityWrapper> extraction = ProtobufBuilder.buildProtobufsForTest(dataLocation, protobufDir);
            runMultiR();
            ResultParser.processMultirResults(extraction, processedModelDirectory);

        }
    }

    private static void printOutput(Process p) throws IOException {
        InputStream in = p.getInputStream();
        InputStream err = p.getErrorStream();
        System.err.println(convertStreamToString(err));
        System.out.println(convertStreamToString(in));
    }

    private static void runMultiR() throws IOException, InterruptedException {
        //run the protobuf file against multiR and the trained model;

        System.out.println("Processing multiR data...");
        Process proc = Runtime.getRuntime().exec("java -jar -Xmx12G ./lib/multir.jar preprocess -trainFile " +
                trainedProtobutLocation + " -testFile " + dataLocation + ProtobufBuilder.PROTOBUF_OUT +
                " -outDir /home/bdwalker/multiR/model_data");

        proc.waitFor();
        printOutput(proc);

        System.out.println("Processing Completed, Training...");
        proc = Runtime.getRuntime().exec("java -Djava.util.Arrays.useLegacyMergeSort=true -jar -Xmx12G ./lib/multir.jar train -dir " + processedModelDirectory);
        proc.waitFor();
        printOutput(proc);
        System.out.println("Training Completed, Begin Testing...");

        proc = Runtime.getRuntime().exec("java -jar -Xmx12G ./lib/multir.jar results -dir " + processedModelDirectory);
        proc.waitFor();
        printOutput(proc);
        System.out.println("Testing Completed, Results saved to: " + processedModelDirectory);


    }

    private static String convertStreamToString(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        return writer.toString();
    }
}
