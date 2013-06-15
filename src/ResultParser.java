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

    public static void processMultirResults(List<EntityWrapper> extractions, String resultsDirectory) throws FileNotFoundException {

        PrintStream output = new PrintStream(new File(resultsDirectory + "/kbp_results"));
        Scanner results = new Scanner(new File(resultsDirectory + "/results"));
        Scanner entities = new Scanner(new File(resultsDirectory + "/entity_output"));
        results.nextLine();
        while (results.hasNextLine()) {
            String entityLine = entities.nextLine();
            String[] entitySplit = entityLine.split("\\t");
            String line = results.nextLine();
            String[] components = line.split("\\t");
            String[] arg1Comps = components[0].split("|");
            String arg2 = components[1];
            String relation = components[3];
            String confidence = components[4];
            String arg1 = arg1Comps[0];
            String doc = entitySplit[1];
            String sentence = entitySplit[2];
            int sentenceId = Integer.parseInt(entitySplit[0]);
            String wikiEntity = entitySplit[3];
            output.println(sentenceId + "\t" + arg1 + "\t" + arg2 + "\t" + relation + "\t" + confidence +
                    "\t" + sentence + "\t" + doc + "\t" + wikiEntity);

        }
        output.close();
    }
}
