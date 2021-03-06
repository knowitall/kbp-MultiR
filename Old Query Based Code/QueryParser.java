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
public class QueryParser {
    public static final String sentence_file = "/sentences.text";
    public static final String pos_file = "/sentences.stanfordpos";
    public static final String token_file = "/sentences.tokens";
    public static final String ner_file = "/sentences.stanfordner";
    public static final String deps_file = "/sentences.depsStanfordCCProcessed2.nodup";
    public static final String args_out = "/query_args";
    public static final String token_out_file = "/query_tokens";
    public static final String pos_out_file = "/query_postags";
    public static final String ner_out_file = "/query_ner";
    public static final String sentence_out = "/query_sentences";
    public static final String position_out_file = "/query_positions";
    public static final String deps_out_file = "/query_dependencies";
    public static final String sentence_out_file = "/query_sentences";

    public static void outputDataForQuery(Query q, String fileLoc, String outputDir, Boolean overwrite) throws FileNotFoundException {
        String entity = q.entity;
        String file = "/" + q.queryId + "_" + q.entity.replace(" ", "_") + ".txt";
        File testFile = new File(outputDir + sentence_out + file);

        if (overwrite || !testFile.exists()) {
            Scanner tokenFile = new Scanner(new File(fileLoc + token_file));
            Scanner posFile = new Scanner(new File(fileLoc + pos_file));
            Scanner sentenceFile = new Scanner(new File(fileLoc + sentence_file));
            Scanner nerFile = new Scanner(new File(fileLoc + ner_file));
            Scanner depsFile = new Scanner(new File(fileLoc + deps_file));

            PrintStream tokenOut = new PrintStream(new File(outputDir + token_out_file + file));
            tokenOut.println(q.queryId + "\t" + q.entity);
            PrintStream posOut = new PrintStream(new File(outputDir + pos_out_file + file));
            posOut.println(q.queryId + "\t" + q.entity);
            PrintStream sentenceOut = new PrintStream(new File(outputDir + sentence_out + file));
            sentenceOut.println(q.queryId + "\t" + q.entity);
            PrintStream argOut = new PrintStream(new File(outputDir + args_out + file));
            argOut.println(q.queryId + "\t" + q.entity);
            PrintStream positionOut = new PrintStream(new File(outputDir + position_out_file + file));
            positionOut.println(q.queryId + "\t" + q.entity);
            PrintStream depsOut = new PrintStream(new File(outputDir + deps_out_file + file));
            depsOut.println(q.queryId + "\t" + q.entity);
            PrintStream nerOut = new PrintStream(new File(outputDir + ner_out_file + file));
            nerOut.println(q.queryId + "\t" + q.entity);

            while (sentenceFile.hasNextLine()) {

                String tokens = tokenFile.nextLine();
                String sentence = sentenceFile.nextLine();
                String pos = posFile.nextLine();
                String ner = nerFile.nextLine();
                String deps = depsFile.nextLine();
                String[] tabSep = sentence.split("\\t");

                if (tabSep[1].contains(entity)) {
                    sentenceOut.println(sentence);
                    tokenOut.println(tokens);
                    posOut.println(pos);
                    nerOut.println(ner);
                    argOut.println(findArguments(ner, tokens));
                    positionOut.println(findPositions(ner));
                    depsOut.println(deps);
                }
            }

            nerOut.close();
            tokenOut.close();
            posOut.close();
            sentenceOut.close();
            argOut.close();
            positionOut.close();
            depsOut.close();
        }
    }

    public static String findPositions(String nerString) {
        String[] tabSep = nerString.split("\\t");
        String[] ner = tabSep[1].split(" ");
        StringBuilder output = new StringBuilder();
        output.append(tabSep[0]);

        boolean started = false;
        if (!ner[0].equals("O")) {
            output.append("\t");
            output.append(0);
            started = true;
        }

        int i = 1;
        for (; i < ner.length; i++) {
            String current = ner[i];
            if (current.equals("O") && started) {
                output.append(":");
                output.append(i);
                started = false;
            } else if (!current.equals("O") && !started) {
                output.append("\t");
                output.append(i);
                started = true;
            } else if (!current.equals("O") && !ner[i-1].equals(current)) {
                output.append(":");
                output.append(i);
                output.append("\t");
                output.append(i);
            }
        }

        if (started) {
            output.append(":");
            output.append(i);
        }

        return output.toString();
    }



    public static String findArguments(String nerString, String tokenString) {
        String[] tabSepTokens = tokenString.split("\\t");
        String[] tokens = tabSepTokens[1].split(" ");
        String[] tabSep = nerString.split("\\t");
        String[] ner = tabSep[1].split(" ");
        StringBuilder output = new StringBuilder();
        output.append(tabSepTokens[0]);

        if (!ner[0].equals("O")) {
            output.append("\t");
            output.append(tokens[0]);
        }

        for (int i = 1; i < ner.length; i++) {
            if (!ner[i].equals("O")) {
                if (ner[i].equals(ner[i-1])) {
                    output.append(" ");
                    output.append(tokens[i]);
                } else {
                    output.append("\t");
                    output.append(tokens[i]);
                }
            }
        }

        return output.toString();
    }


    private static void parseDependencies(String depString, int tokenSize, int[] p, String[] t) {

        int[] parent = new int[tokenSize + 1];
        String[] types = new String[tokenSize + 1];
        for (int i = 0; i < parent.length; ++i) {
            parent[i] = -1;
        }

        String[] deps = depString.split("\\|");
        for (String dep : deps) {
            String[] depTokens = dep.split(" ");
            int first = Integer.parseInt(depTokens[0]);
            int third = Integer.parseInt(depTokens[2]);
            String type = depTokens[1];
            int gov = first, d = third;
            if (parent[d] != -1) {
                // error
                //X.getCounter("multi parent").increment();
                System.out.println("Dependency Loop Error: parseDependencies");
                break;
            } else {
                parent[d] = gov-1;
                types[d] = type;
            }
        }

        System.arraycopy(parent, 1, p, 0, p.length);
        System.arraycopy(types, 1, t, 0, t.length);


    }

    private static Map<String, String> findSecondArguments(String entity, String[] args, String[] positions) {
        Map<String, String> offsetEntity = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            if (!args[i].contains(entity)) {
                offsetEntity.put(positions[i], args[i]);
            }
        }
        return offsetEntity;
    }

    private static String findEntityNER(String entity, String[] nerString, String[] tokens) {
        String[] entTokens = entity.split(" ");
        int entIndex = 0;
        for (int i = 0; i < tokens.length; i++) {
            if (entTokens[entIndex].contains(tokens[i]) && entIndex == (entTokens.length - 1)) {
                return nerString[i];
            } else if (entTokens[entIndex].contains(tokens[i])) {
                ++entIndex;
            } else if (!entTokens[entIndex].contains(tokens[i]) && entIndex > 0) {
                entIndex = 0;
            }
        }
        return null;
    }

    private static String findEntityPosition(String entity, String[] args, String[] position) {
        String entityPosition = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains(entity)) {
                entityPosition = position[i];
                break;
            }
        }

        return entityPosition;
    }


    public static List<EntityWrapper> prepareQueryForFeatureExtraction(Query q, String dataLocation) throws FileNotFoundException {
        List<EntityWrapper> results = new ArrayList<EntityWrapper>();

        String filename = "/" + q.queryId + "_" + q.entity.replace(" ", "_") + ".txt";
        Scanner tokenIn = new Scanner(new File(dataLocation + token_out_file + filename));
        tokenIn.nextLine();
        Scanner argsIn = new Scanner(new File(dataLocation + args_out + filename));
        argsIn.nextLine();
        Scanner depsIn = new Scanner(new File(dataLocation + deps_out_file + filename));
        depsIn.nextLine();
        Scanner posIn = new Scanner(new File(dataLocation + pos_out_file + filename));
        posIn.nextLine();
        Scanner positionIn = new Scanner(new File(dataLocation + position_out_file + filename));
        positionIn.nextLine();
        Scanner sentenceIn = new Scanner(new File(dataLocation + sentence_out_file + filename));
        sentenceIn.nextLine();
        Scanner nerIn = new Scanner(new File(dataLocation + ner_out_file + filename));
        nerIn.nextLine();


        while (tokenIn.hasNextLine()) {
            String[] tabTokens = tokenIn.nextLine().split("\\t");
            String[] tokens = tabTokens[1].split(" ");

            String sentence = sentenceIn.nextLine().split("\\t")[1];

            int[] p = new int[tokens.length];
            String[] t = new String[tokens.length];
            parseDependencies(depsIn.nextLine().split("\\t")[1], tokens.length, p, t);


            String[] pos = posIn.nextLine().split("\\t")[1].split(" ");

            String[] nerString = nerIn.nextLine().split(" ");

            String[] positionsArray = positionIn.nextLine().split("\\t");
            String[] positions = Arrays.copyOfRange(positionsArray, 1, positionsArray.length);
            System.out.println(Arrays.toString(positions));
            String[] argsArray = argsIn.nextLine().split("\\t");
            String[] args = Arrays.copyOfRange(argsArray, 1, argsArray.length);
            System.out.println(Arrays.toString(args));
            Map<String, String> secondArguments = findSecondArguments(q.entity, args, positions);

            String[] entityPosition = findEntityPosition(q.entity, args, positions).split(":");
            int entityStart = Integer.parseInt(entityPosition[0]);
            int entityEnd = Integer.parseInt(entityPosition[1]);
            int[] positionArray = {entityStart, entityEnd};


            for (String arg2Pos: secondArguments.keySet()) {
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.dependencyParents = p;
                wrapper.dependencyTypes = t;
                wrapper.entityPos = positionArray;
                wrapper.posTags = pos;
                wrapper.tokens = tokens;
                wrapper.sentence = sentence;
                wrapper.sentenceId = Integer.parseInt(tabTokens[0]);
                wrapper.entity2 = secondArguments.get(arg2Pos);
                wrapper.entityNER = findEntityNER(wrapper.entity, nerString, tokens);
                wrapper.entity2NER = findEntityNER(wrapper.entity2, nerString, tokens);
                String[] entity2Position = arg2Pos.split(":");
                int entity2Start = Integer.parseInt(entity2Position[0]);
                int entity2End = Integer.parseInt(entity2Position[1]);
                int[] entity2Array = {entity2Start, entity2End};
                wrapper.entity2Pos = entity2Array;
                results.add(wrapper);
            }
        }

        return results;
    }
}
