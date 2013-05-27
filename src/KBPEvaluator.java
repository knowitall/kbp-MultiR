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

        /*
        XMLParser parser = new XMLParser(kbpFile);
        List<Query> queries = parser.getQueries();
        parseQueryData(queries);

        List<EntityWrapper> extractionData = QueryParser.prepareQueryForFeatureExtraction(queries.get(1));
        EntityWrapper w = extractionData.get(0);
        List<String> features = featureExtractor.getFeatures(w.sentenceId, w.tokens, w.posTags,
                w.dependencyParents, w.dependencyTypes, w.entityPos, w.entity2Pos, w.entity, w.entity2);
        System.out.println(features);
*/
        //buildTestRelation();

        prepareTest();
        readTestRelation();
        readTest();

    }

    public static void buildTestRelation() throws IOException {
        XMLParser parser = new XMLParser(kbpFile);
        RelationECML featureExtractor = new RelationECML();

        Query sampleQuery = parser.getQueryForQueryId("SF559");
        List<EntityWrapper> extraction = QueryParser.prepareQueryForFeatureExtraction(sampleQuery);
        EntityWrapper needle = null;

        for (EntityWrapper e: extraction) {
            if (e.sentenceId == 20025780) {
                needle = e;
            }
        }

        List<String> features = featureExtractor.getFeaturesForEntity(needle);
        System.out.println(needle.sentence);
        QueryRelation.Mention mb = QueryRelation.Mention.newBuilder().addAllFeature(features).setDestId(-1).setSourceId(-1).setFilename("blah").setSentence(needle.sentence).build();
        QueryRelation.Relation finalRelation = QueryRelation.Relation.newBuilder().setDestGuid("/m/01j6t").setRelType("NA").setSourceGuid("/m/0vmt").addMention(mb).build();

        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("../Barry_Goldwater.pb"));
        finalRelation.writeTo(output);
    }

    public static void prepareTest() throws IOException {
        QueryRelation.Mention mb = QueryRelation.Mention.newBuilder().
                addFeature("Blah blah blah blah feature").
                setSourceId(-1).
                setFilename("test.txt").
                setDestId(-1).
                setSentence("blah blah blah blah sentence").build();
        QueryRelation.Relation rel = QueryRelation.Relation.newBuilder().
                setDestGuid("q").
                setRelType("NA").
                setSourceGuid("s").
                addMention(mb).build();
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("../test.pb.gz"));
        GZIPOutputStream gout = new GZIPOutputStream(output);
        rel.writeDelimitedTo(gout);
        gout.close();

    }

    public static void readTestRelation() throws IOException {
        InputStream is = new GZIPInputStream(new BufferedInputStream
                (new FileInputStream("../test.pb.gz")));

        QueryRelation.Relation r = QueryRelation.Relation.parseDelimitedFrom(is);
        System.out.println(r);

    }

    public static void readTest() throws IOException {
        InputStream is = new GZIPInputStream(new BufferedInputStream
                (new FileInputStream("./test-Multiple.pb.gz")));

        QueryRelation.Relation r = QueryRelation.Relation.parseDelimitedFrom(is);
        System.out.println(r);
        System.out.println();

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
