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

    public static String buildProtobufsForTest(String dataDir, String outDir)
            throws IOException {

        String outputFile = outDir + "/kbp_relations.pb.gz";
        RelationECML featureExtractor = new RelationECML();
        Map<String, List<EntityWrapper>> arg1Dict = new HashMap<String, List<EntityWrapper>>();
        List<EntityWrapper> extraction = QueryParserModified.prepareAllSentencesForFeatureExtraction(dataDir);

        List<QueryRelation.Mention> mentions = new ArrayList<QueryRelation.Mention>();

        for (EntityWrapper ent: extraction) {
            if (arg1Dict.containsKey(ent.entity)) {
                arg1Dict.get(ent.entity).add(ent);
            } else {
                List<EntityWrapper> entList = new ArrayList<EntityWrapper>();
                entList.add(ent);
                arg1Dict.put(ent.entity, entList);
            }
        }

        for (String entity: arg1Dict.keySet()) {
            System.out.println(entity);
            List<EntityWrapper> entList = arg1Dict.get(entity);

            String wikiId = "";
            for (EntityWrapper entWrapper: entList) {
                wikiId = entWrapper.wikiEntity;
                List<String> features = featureExtractor.getFeaturesForEntity(entWrapper);
                QueryRelation.Mention mb = QueryRelation.Mention.newBuilder().
                        addAllFeature(features).
                        setDestId(entWrapper.sentenceId).
                        setSourceId(entWrapper.sentenceId).
                        setFilename(entWrapper.documentName).
                        setSentence(entWrapper.sentence).
                        build();

                mentions.add(mb);
            }
            QueryRelation.Relation finalRelation = QueryRelation.Relation.newBuilder().
                    setDestGuid(entity).
                    setRelType("NA").
                    setSourceGuid(wikiId).
                    addAllMention(mentions).build();
            System.out.println(finalRelation);
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
