import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bdwalker
 * Date: 5/20/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryParserModified {
    public static final String sentence_file = "/sentences.text";
    public static final String pos_file = "/sentences.stanfordpos";
    public static final String token_file = "/sentences.tokens";
    public static final String ner_file = "/sentences.stanfordner";
    public static final String deps_file = "/sentences.depsStanfordCCProcessed";
    public static final String wiki_file = "/sentences.wikification";
    public static final String meta_file = "/sentences.meta";
    public static List<EntityWrapper> prepareAllSentencesForFeatureExtraction(String fileLoc) throws FileNotFoundException {
        List<EntityWrapper> results = new ArrayList<EntityWrapper>();

        Scanner tokenIn = new Scanner(new File(fileLoc + token_file));
        Scanner posFile = new Scanner(new File(fileLoc + pos_file));
        Scanner sentenceIn = new Scanner(new File(fileLoc + sentence_file));
        Scanner nerFile = new Scanner(new File(fileLoc + ner_file));
        Scanner depsFile = new Scanner(new File(fileLoc + deps_file));
        Scanner wikiFile = new Scanner(new File(fileLoc + wiki_file));
        Scanner metaFile = new Scanner(new File(fileLoc + meta_file));
        List<Sentence> sentences = new ArrayList<Sentence>();

        while (sentenceIn.hasNextLine()) {
            String sentence = sentenceIn.nextLine();
            String ner = nerFile.nextLine();
            String pos = posFile.nextLine();
            String tokens = tokenIn.nextLine();
            String deps = depsFile.nextLine();
            String wiki = wikiFile.nextLine();
            String meta = metaFile.nextLine();
            if (sentence.startsWith("1257509\t")) {
                sentences.add(new Sentence(sentence, ner, pos, tokens, deps, wiki, meta));
                break;
            }

        }

        for (Sentence s: sentences) {
            results.addAll(s.getSentenceEntities());
        }

        return results;
    }
}
