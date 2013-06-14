import com.sun.deploy.util.StringUtils;

import java.util.*;
import java.util.regex.*;

public class Sentence{
    public int sentenceID;
    private List<String> arg1;
    private List<String> arg2;
    private List<String> position1;
    private List<String> position2;
    private String[] posTokens;
    private List<String> entities;
    private List<String> entities2;
    private String posString;
    private String[] ner;
    private String sentence;
    private String[] tokens;
    private int[] dependencyParents;
    private String[] dependencyTypes;
    private String[] wikiData;
    private List<String> wikiId;
    private String file;

    public Sentence(String sentence, String ner, String pos,
                    String tokens, String deps, String wiki,
                    String meta) {
        this.ner = ner.split("\\t")[1].split(" ");
        this.tokens = tokens.split("\\t")[1].split(" ");
        file = meta.split("\\t")[2];
        String[] sentenceChunks = sentence.split("\\t");
        this.sentence = sentenceChunks[1];
        this.sentenceID = Integer.parseInt(sentenceChunks[0]);
        posString = pos;
        posTokens = pos.split("\\t")[1].split(" ");
        System.out.println(sentence);
        System.out.println(pos);
        String[] depsSplit = deps.split("\\t");
        if (depsSplit.length > 1) {
            parseDependencies(depsSplit[1]);
        }
        String[] wikiSplit = wiki.split("\\t");
        wikiData = Arrays.copyOfRange(wikiSplit, 1, wikiSplit.length);

        findArguments();
    }

    private void parseDependencies(String depString) {
        dependencyParents = new int[tokens.length + 1];
        dependencyTypes = new String[tokens.length + 1];
        for (int i = 0; i < dependencyParents.length; ++i) {
            dependencyParents[i] = -1;
        }

        String[] deps = depString.split("\\|");
        for (String dep : deps) {
            String[] depTokens = dep.split(" ");
            int first = Integer.parseInt(depTokens[0]);
            int third = Integer.parseInt(depTokens[2]);
            String type = depTokens[1];
            int gov = first, d = third;
            if (dependencyParents[d] != -1 || dependencyParents[d] != gov-1) {
                dependencyParents[d] = -1;
            } else {
                dependencyParents[d] = gov-1;
                dependencyTypes[d] = type;
            }
        }
    }

    private String getEntityForOffset(String offset) {
        String[] split = offset.split(":");
        int start = Integer.parseInt(split[0]);
        int end = Integer.parseInt(split[1]);
        String entity = tokens[start];
        for (int i = start + 1; i < end; i++) {
            entity += " " + tokens[i];
        }
        return entity;
    }


    private void findArguments() {
        List<String> firstArguments = new ArrayList<String>();
        List<String> secondArguments = new ArrayList<String>();
        List<String> firstArgumentsPosition = new ArrayList<String>();
        List<String> secondArgumentsPosition = new ArrayList<String>();
        List<String> entityList = new ArrayList<String>();
        List<String> entityList2 = new ArrayList<String>();
        Set<String> startingIndices = new HashSet<String>();
        wikiId = new ArrayList<String>();

        String arg = "";
        String offset = "";

        //look at wikification data for possible arguments
        for (String wiki: wikiData) {
            String[] wikiSplit = wiki.split(" ");
            double confidence = Double.parseDouble(wikiSplit[3]);
            String wikiOffset = wikiSplit[0] + ":" + wikiSplit[1];

            if (confidence > .5) {
                startingIndices.add(wikiSplit[0]);
                String nerTag = ner[Integer.parseInt(wikiSplit[0])];

                String entity = getEntityForOffset(wikiOffset);
                if (nerTag.equals("O") || nerTag.equals("ORGANIZATION") || nerTag.equals("PERSON")) {
                    firstArguments.add(nerTag);
                    wikiId.add(wikiSplit[2]);
                    firstArgumentsPosition.add(wikiOffset);
                    entityList.add(entity);
                }
                secondArguments.add(nerTag);
                secondArgumentsPosition.add(wikiOffset);
                entityList2.add(entity);
            }
        }

        // look for additional in NER data
        arg = "";
        offset = "";
        boolean started = false;
        if (!ner[0].equals("O")) {
            arg = ner[0];
            offset = "0";
            started = true;
        }

        int i = 1;
        for (; i < ner.length; i++) {
            String current = ner[i];
            if (current.equals("O") && started) {
                offset += ":" + i;
                if (!startingIndices.contains("" + offset.charAt(0))) {
                    String entity = getEntityForOffset(offset);
                    if (current.equals("PERSON") || current.equals("ORGANIZATION")){
                        firstArguments.add(arg);
                        firstArgumentsPosition.add(offset);
                        wikiId.add("NIL");
                        entityList.add(entity);
                    }
                    secondArguments.add(arg);
                    entityList2.add(entity);
                    secondArgumentsPosition.add(offset);
                }
                arg = "";
                offset = "";
                started = false;
            } else if (!current.equals("O") && !started) {
                arg = current;
                offset = "" + i;
                started = true;
            } else if (!current.equals("O") && !ner[i-1].equals(current)) {
                offset += ":" + i;
                if (!startingIndices.contains("" + offset.charAt(0))) {
                    String entity = getEntityForOffset(offset);
                    if (current.equals("PERSON") || current.equals("ORGANIZATION")){
                        wikiId.add("NIL");
                        firstArguments.add(arg);
                        entityList.add(entity);
                        firstArgumentsPosition.add(offset);
                    }
                    secondArguments.add(arg);
                    entityList2.add(offset);
                    secondArgumentsPosition.add(offset);
                }
                offset = i + "";
                arg = current;
            }
        }

        if (started) {
            offset += ":" + i;
            String entity = getEntityForOffset(offset);
            if (arg.equals("PERSON") || arg.equals("ORGANIZATION")){
                firstArguments.add(arg);
                entityList.add(entity);
                wikiId.add("NIL");
                firstArgumentsPosition.add(offset);
            }
            secondArguments.add(arg);
            entityList2.add(entity);
            secondArgumentsPosition.add(offset);
        }

//        //Use a regex as one last attempt to find additional arguments
//        Pattern pattern = Pattern.compile("DT? (NOD?|JJ|N)*N");
//        Matcher matcher = pattern.matcher(posString);
//        while (matcher.find()) {
//            int startIndex = matcher.start();
//            System.out.println("Start Character: " + startIndex);
//            int tokenIndex = getTokenInteger(startIndex);
//            System.out.println("Token Index: " + tokenIndex);
//            if (tokenIndex < 0) {
//                throw new IllegalArgumentException();
//            }
//            String regexOffset = ":";
//            if (!secondArgumentsPosition.contains(regexOffset)) {
//                secondArgumentsPosition.add(regexOffset);
//                secondArguments.add("O");
//            }
//        }

        arg1 = firstArguments;
        arg2 = secondArguments;
        position1 = firstArgumentsPosition;
        position2 = secondArgumentsPosition;
        entities = entityList;
        entities2 = entityList2;
        System.out.println(arg1);
        System.out.println(position1);
        System.out.println(arg2);
        System.out.println(position2);
        System.out.println(entities);
        System.out.println(entities2);
    }

    private int getTokenInteger(int startChar) {
        int currentChar = 0;
        String[] posSplit = posString.split("\\t");
        String[] split = posSplit[1].split(" ");
        String[] posComb = new String[1 + split.length];
        posComb[0] = posSplit[0];
        for (int i = 0; i < split.length; i++){
            posComb[i+1] = split[i];
        }

        for (int i = 0; i < posComb.length; i++) {
            if (currentChar == startChar) {
                return i - 1;
            }
            // +1 for the space or tab character
            currentChar += posComb[i].length() + 1;

        }
        return -1;
    }

    public List<EntityWrapper> getSentenceEntities() {
        List<EntityWrapper> ents = new ArrayList<EntityWrapper>();
        for (int i = 0; i < arg1.size(); i++) {
            for (int j = 0; j < arg2.size(); j++) {
                if (!entities.get(i).equals(entities2.get(j))) {
                    EntityWrapper e = new EntityWrapper();
                    e.dependencyParents = dependencyParents;
                    e.dependencyTypes = dependencyTypes;
                    e.sentence = sentence;
                    e.tokens = tokens;
                    e.posTags = posTokens;
                    e.entityNER = arg1.get(i);
                    e.entity2NER = arg2.get(j);
                    e.sentenceId = sentenceID;
                    e.entity = entities.get(i);
                    e.entity2 = entities2.get(j);
                    e.wikiEntity = wikiId.get(i);
                    e.documentName = file;

                    String[] offset = position1.get(i).split(":");
                    int[] integerOffset = new int[2];
                    integerOffset[0] = Integer.parseInt(offset[0]);
                    integerOffset[1] = Integer.parseInt(offset[1]);
                    e.entityPos = integerOffset;

                    offset = position2.get(j).split(":");
                    int[] integerOffset2 = new int[2];
                    integerOffset2[0] = Integer.parseInt(offset[0]);
                    integerOffset2[1] = Integer.parseInt(offset[1]);
                    e.entity2Pos = integerOffset2;

                    ents.add(e);
                }
            }
        }
        return ents;
    }

}