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
    public static final String PROTOBUF_OUT = "/kbp_relations.pb.gz";

    public static List<EntityWrapper> buildProtobufsForTest(String dataDir, String protobufDir)
            throws IOException {

        RelationECML featureExtractor = new RelationECML();
        Map<String, List<EntityWrapper>> arg1Dict = new HashMap<String, List<EntityWrapper>>();
        List<EntityWrapper> extraction = SentenceParser.prepareAllSentencesForFeatureExtraction(dataDir);


        for (EntityWrapper ent: extraction) {
            if (arg1Dict.containsKey(ent.entity)) {
                arg1Dict.get(ent.entity).add(ent);
            } else {
                List<EntityWrapper> entList = new ArrayList<EntityWrapper>();
                entList.add(ent);
                arg1Dict.put(ent.entity, entList);
            }
        }

        BufferedOutputStream output = new BufferedOutputStream(
                new FileOutputStream(protobufDir + PROTOBUF_OUT));
        GZIPOutputStream out = new GZIPOutputStream(output);

        try {
            for (String entity: arg1Dict.keySet()) {
                List<EntityWrapper> entList = arg1Dict.get(entity);

                for (EntityWrapper entWrapper: entList) {
                    List<KbpRelation.Mention> mentions = new ArrayList<KbpRelation.Mention>();
                    List<String> features = featureExtractor.getFeaturesForEntity(entWrapper);
                    //feature extractor will return null in the event of a dependency graph loop
                    if (features == null) continue;

                    KbpRelation.Mention mb = KbpRelation.Mention.newBuilder().
                            addAllFeature(features).
                            setDestId(entWrapper.entityKey).
                            setSourceId(entWrapper.sentenceId).
                            setFilename(entWrapper.documentName).
                            setSentence(entWrapper.sentence).
                            build();

                    mentions.add(mb);

                    KbpRelation.Relation finalRelation = KbpRelation.Relation.newBuilder().
                            setDestGuid(entWrapper.entity2).
                            setRelType("NA").
                            setSourceGuid(entity + "|" + entWrapper.entityKey).
                            addAllMention(mentions).build();
                    System.out.println(finalRelation);

                    finalRelation.writeDelimitedTo(out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

        return extraction;
    }

    public static KbpRelation.Relation readProtobufFile(String file) throws IOException {
        File protoFile = new File(file);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(protoFile));
        GZIPInputStream gin = new GZIPInputStream(in);
        KbpRelation.Relation relation = KbpRelation.Relation.parseDelimitedFrom(gin);
        gin.close();
        return relation;
    }
}
