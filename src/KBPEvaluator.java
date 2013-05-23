/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/17/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.FileNotFoundException;
import java.util.*;


public class KBPEvaluator {


    public static void main(String[] args) throws FileNotFoundException {
        Map<String, String> freeBaseToKBP = new HashMap<String, String>();

        if (args.length < 1) {
            System.out.println("Usage error: Filename must be passed as an argument");
        } else {

            RelationECML featureExtractor = new RelationECML();
            /*String[] tokens = {"Other", "quantitative", "funds", "run", "by", "firms", ",",
                    "including", "AQR", "Capital", "Management", "LLC", "and", "Highbridge",
                    "Capital", "Management", "LLC", ",", "may", "also", "have", "sustained",
                    "heavy", "losses", "."};

            String[] posTags = {"JJ", "JJ", "NNS", "VBN", "IN", "NNS", ",", "VBG", "NNP",
                    "NNP", "NNP", "NNP", "CC", "NNP", "NNP", "NNP", "NNP", ",", "MD", "RB",
                    "VB", "VBN", "JJ", "NNS", "."};
            int[] arg1Pos = {8, 12};
            int[] arg2Pos = {13, 17};
            List<String> features = featureExtractor.getFeatures(0, tokens, posTags, null, null, arg1Pos, arg2Pos, "AQR Capital Management LLC", "Highbridge Capital Management LLC");

            System.out.println(features);*/

            /*String kbpFile = args[0];
            XMLParser parser = new XMLParser(kbpFile);
            List<Query> queries = parser.getQueries();
            parseQueryData(queries);

            List<EntityWrapper> extractionData = QueryParser.prepareQueryForFeatureExtraction(queries.get(1));
            EntityWrapper w = extractionData.get(0);
            List<String> features = featureExtractor.getFeatures(w.sentenceId, w.tokens, w.posTags,
                    w.dependencyParents, w.dependencyTypes, w.entityPos, w.entity2Pos, w.entity, w.entity2);
            System.out.println(features);

*/

        }
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
