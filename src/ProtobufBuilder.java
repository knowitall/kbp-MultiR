/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/28/13
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ProtobufBuilder {

    public static String buildProtobufsForTest(String dataDir, String outDir, Query q)
            throws IOException {

        RelationECML featureExtractor = new RelationECML();
        List<EntityWrapper> extraction = QueryParser.prepareQueryForFeatureExtraction(q, dataDir);

        List<QueryRelation.Mention> mentions = new ArrayList<QueryRelation.Mention>();
        for (EntityWrapper ent: extraction) {
            System.out.println(ent);

            List<String> features = featureExtractor.getFeaturesForEntity(ent);
            QueryRelation.Mention mb = QueryRelation.Mention.newBuilder().
                    addAllFeature(features).
                    setDestId(-1).
                    setSourceId(-1).
                    setFilename("NA").
                    setSentence(ent.sentence).
                    build();

            mentions.add(mb);
        }

        QueryRelation.Relation finalRelation = QueryRelation.Relation.newBuilder().
                setDestGuid("/m/0vmt").
                setRelType("NA").
                setSourceGuid("/m/01j6t").
                addAllMention(mentions).build();

        String outputFile = outDir + "/" + q.queryId +
                "_" + q.entity.replace(" ", "_") + ".pb.gz";
        BufferedOutputStream output = new BufferedOutputStream(
                new FileOutputStream(outputFile));
        GZIPOutputStream out = new GZIPOutputStream(output);

        try {
            finalRelation.writeDelimitedTo(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

        return outputFile;
    }

    public static QueryRelation.Relation readProtobufFile(String file) throws IOException {
        File protoFile = new File(file);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(protoFile));
        GZIPInputStream gin = new GZIPInputStream(in);
        QueryRelation.Relation relation = QueryRelation.Relation.parseDelimitedFrom(gin);
        gin.close();
        return relation;
    }
}
