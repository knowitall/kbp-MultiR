import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/28/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultParser {
    public static final String FINAL_RESULT_FILE = "/kbp_results";

    public static void processMultirResults(String resultsDirectory,
                                            String resultsFile,
                                            String entityFile) throws FileNotFoundException {

        PrintStream output = new PrintStream(new File(resultsDirectory + FINAL_RESULT_FILE));
        Scanner results = new Scanner(new File(resultsDirectory + resultsFile));
        Scanner entities = new Scanner(new File(resultsDirectory + entityFile));
        results.nextLine();
        while (results.hasNextLine()) {
            String line = results.nextLine();
            String[] components = line.split("\\t");
            String relation = components[3];
            if (!relation.equals("NA")) {
                String entityLine = entities.nextLine();
                String[] entitySplit = entityLine.split("\\t");
                String arg2 = components[1];
                String confidence = components[4];
                String arg1 = components[0];
                String doc = entitySplit[1];
                String sentence = entitySplit[2];
                int sentenceId = Integer.parseInt(entitySplit[0]);
                String wikiEntity = entitySplit[3];
                output.println(sentenceId + "\t" + arg1 + "\t" + arg2 + "\t" + relation + "\t" + confidence +
                        "\t" + sentence + "\t" + doc + "\t" + wikiEntity);
            }
        }
        output.close();
    }
}
