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

public class KBPEvaluator {
    public static final String HEAP_SIZE = "12G";
    public static final String PROTOBUF_FILE = "/kbp_relations.pb.gz";
    public static final String ENTITY_FILE = "/entity_output.txt";
    public static final String RESULT_FILE = "/results";
    public static final String MULTIR_FILE = "F:\\Documents\\GitHub\\kbp-multir\\lib\\multir.jar";

    private static String processedCorpusFileLocation;
    private static String trainedProtobufModelFile;
    private static String outputDirectory;
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.out.println("Missing required command line argument: location of kbp query " +
                    "file, pre-processed data file and output directory required");
            System.out.println("Usage: processed_file_directory /directory/model_protobuf.pb.gz " +
                    "testing_protobuf_output_directory processed_model_output_directory");
        } else {

            processedCorpusFileLocation = args[0];
            trainedProtobufModelFile = args[1];
            outputDirectory = args[2];

            //process corpus and output testing protobuf file
            SentenceParser.processCorpus(processedCorpusFileLocation,
                    outputDirectory, PROTOBUF_FILE, ENTITY_FILE);
            runMultiR();
            ResultParser.processMultirResults(outputDirectory, RESULT_FILE, ENTITY_FILE);
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
        Process proc = Runtime.getRuntime().exec("java -jar -Xmx" + HEAP_SIZE +
                " " + MULTIR_FILE + " preprocess -trainFile " +
                trainedProtobufModelFile + " -testFile " + outputDirectory + PROTOBUF_FILE +
                " -outDir " + outputDirectory);

        proc.waitFor();
        printOutput(proc);

        System.out.println("Processing Completed, Training...");
        proc = Runtime.getRuntime().exec("java -Djava.util.Arrays.useLegacyMergeSort=true -jar -Xmx" +
                HEAP_SIZE + " " + MULTIR_FILE + " train -dir " + outputDirectory);
        proc.waitFor();
        printOutput(proc);
        System.out.println("Training Completed, Begin Testing...");

        proc = Runtime.getRuntime().exec("java -jar -Xmx" + HEAP_SIZE + " " + MULTIR_FILE + " results -dir " +
                outputDirectory);
        proc.waitFor();
        printOutput(proc);
        System.out.println("Testing Completed, Results saved to: " + outputDirectory);
    }

    private static String convertStreamToString(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        return writer.toString();
    }
}
