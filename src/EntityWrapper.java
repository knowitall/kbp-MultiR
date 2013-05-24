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
    public String[] tokens;
    public int sentenceId;
    public String[] posTags;
    public String entity2;
    public int[] entityPos;
    public int[] entity2Pos;
    public int[] dependencyParents;
    public String[] dependencyTypes;
    public String sentence;

    public EntityWrapper(String e) {
        entity = e;
        tokens = new String[0];
        sentenceId = -1;
        posTags = new String[0];
        entity2 = "NULL";
        entityPos = new int[0];
        entity2Pos = new int[0];
        dependencyParents = new int[0];
        sentence = "";
        dependencyTypes = new String[0];
    }

    public int hashCode() {
        return entity.hashCode();
    }

    public String toString() {
        return "Entity             : " + entity + "\n" +
               "Sentence ID        : " + sentenceId + "\n" +
               "Sentence           :" + sentence + "\n" +
               "POS Tags           : " +  Arrays.toString(posTags) + "\n" +
               "Tokens             : " + Arrays.toString(tokens) + "\n" +
               "Entity2            : " + entity2 + "\n" +
               "Dependency Types   : " + Arrays.toString(dependencyTypes) + "\n" +
               "Dependency Parents : " + Arrays.toString(dependencyParents) + "\n" +
               "Entity Pos         : " + Arrays.toString(entityPos) + "\n" +
               "Entity2 Pos        : " + Arrays.toString(entity2Pos) + "\n\n";
    }

}
