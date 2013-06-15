import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 6/14/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtoReader {

    public static void readProtobufer(String file) throws IOException {
        List<KbpRelation.Relation> relations = ProtobufBuilder.readProtobufFile(file);
        System.out.println(relations.get(10000));
    }

}
