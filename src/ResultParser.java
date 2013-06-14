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
        Map<Integer, EntityWrapper> extractionDict = new HashMap<Integer, EntityWrapper>();
        for (EntityWrapper ent: extractions) {
            extractionDict.put(ent.entityKey, ent);
        }

        PrintStream output = new PrintStream(new File(resultsDirectory + "/kbp_results"));
        Scanner results = new Scanner(new File(resultsDirectory + "/results"));
        results.nextLine();
        while (results.hasNextLine()) {
            String line = results.nextLine();
            String[] components = line.split("\\t");
            String[] arg1Comps = components[0].split("|");
            String arg2 = components[1];
            String relation = components[3];
            String confidence = components[4];
            String arg1 = arg1Comps[0];
            int key = Integer.parseInt(arg1Comps[1]);
            EntityWrapper entity = extractionDict.get(key);
            String doc = entity.documentName;
            String sentence = entity.sentence;
            int sentenceId = entity.sentenceId;
            String wikiEntity = entity.wikiEntity;
            output.println(arg1 + "\t" + arg2 + "\t" + relation + "\t" + confidence +
                    "\t" + sentence + "\t" + sentenceId + "\t" + doc + "\t" + wikiEntity);

        }
        output.close();
    }
}
