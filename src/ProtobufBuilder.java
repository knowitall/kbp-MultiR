/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/28/13
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class ProtobufBuilder {

    public static List<String> buildProtobufsForTest(String dir, Query q)
            throws IOException {

        RelationECML featureExtractor = new RelationECML();
        List<EntityWrapper> extraction = QueryParser.prepareQueryForFeatureExtraction(q);
        List<String> outputFiles = new ArrayList<String>();

        for (EntityWrapper ent: extraction) {
            System.out.println(ent);

            List<String> features = featureExtractor.getFeaturesForEntity(ent);
            QueryRelation.Mention mb = QueryRelation.Mention.newBuilder().
                    addAllFeature(features).
                    setDestId(-1).
                    setSourceId(-1).
                    setFilename("").
                    setSentence(ent.sentence).
                    build();

            QueryRelation.Relation finalRelation = QueryRelation.Relation.newBuilder().
                    setDestGuid("").
                    setRelType("").
                    setSourceGuid("").
                    addMention(mb).build();

            String outputFile = dir + "/" + q.queryId +
                    "_" + ent.entity +
                    "_" + ent.entity2 + ".pb.gz";
            BufferedOutputStream output = new BufferedOutputStream(
                    new FileOutputStream(outputFile));
            GZIPOutputStream out = new GZIPOutputStream(output);

            try {
                finalRelation.writeTo(out);
                outputFiles.add(outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                out.close();
            }
        }

        return outputFiles;
    }
}
