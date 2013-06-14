import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/20/13
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityWrapper {
    public String entity;
    public String entity2;
    public String[] tokens;
    public int sentenceId;
    public String[] posTags;
    public int[] entityPos;
    public int[] entity2Pos;
    public int[] dependencyParents;
    public String[] dependencyTypes;
    public String entityNER;
    public String entity2NER;
    public String sentence;
    public String wikiEntity;
    public String freebaseEntity;
    public String documentName;

    public EntityWrapper() {
        freebaseEntity = "NIL";
        wikiEntity = "NIL";
        tokens = new String[0];
        sentenceId = -1;
        posTags = new String[0];
        entityPos = new int[0];
        entity2Pos = new int[0];
        dependencyParents = new int[0];
        sentence = "";
        dependencyTypes = new String[0];
        entity2NER = "";
        entityNER = "";
    }

    public int hashCode() {
        return entity.hashCode();
    }

    public String toString() {
        return "Sentence ID        : " + sentenceId + "\n" +
               "Sentence           :" + sentence + "\n" +
               "POS Tags           : " +  Arrays.toString(posTags) + "\n" +
               "Tokens             : " + Arrays.toString(tokens) + "\n" +
               "Dependency Types   : " + Arrays.toString(dependencyTypes) + "\n" +
               "Dependency Parents : " + Arrays.toString(dependencyParents) + "\n" +
               "Entity             : " + entity + "\n" +
               "Entity Pos         : " + Arrays.toString(entityPos) + "\n" +
               "Entity2 Pos        : " + entity2 + "\n" +
               "Entity2 Pos        : " + Arrays.toString(entity2Pos) + "\n" +
               "Document Name      : " + documentName + "\n\n";
    }

}
