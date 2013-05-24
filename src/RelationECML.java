import java.util.ArrayList;
import java.util.List;

public class RelationECML {

	static String serializedClassifier = "../model/ner-eng-ie.crf-3-all2008.ser.gz";
	static final int LongPathThresh = 5;
	//private AbstractSequenceClassifier classifier;

	public RelationECML() {
		//classifier = CRFClassifier
		//		.getClassifierNoExceptions(serializedClassifier);
	}

	public List<String> getFeatures(int sentenceId, String[] tokens, 
			String[] postags,
			int[] depParents, String[] depTypes,
			int[] arg1Pos, int[] arg2Pos, String arg1ner, String arg2ner) {

		List<String> features = new ArrayList<String>();

		/** ner feature, such as LOCATION->PERSON */
		features.add(arg1ner + "->" + arg2ner);

		// it's easier to deal with first, second
		int[] first = arg1Pos, second = arg2Pos;
		String firstNer = arg1ner, secondNer = arg2ner;
		if (arg1Pos[0] > arg2Pos[0]) {
			second = arg1Pos; first = arg2Pos;
			firstNer = arg2ner; secondNer = arg1ner;
		}
		
		// define the inverse prefix
		String inv = (arg1Pos[0] > arg2Pos[0])? 
				"inverse_true" : "inverse_false";
		
		// define the middle parts
		StringBuilder middleTokens = new StringBuilder();
		StringBuilder middleTags = new StringBuilder();
		for (int i=first[1]; i < second[0]; i++) {
			if (i > first[1]) {
				middleTokens.append(" ");
				middleTags.append(" ");
			}
			middleTokens.append(tokens[i]);
			middleTags.append(postags[i]);
		}
		
		if (second[0] - first[1] > 10) {
			middleTokens.setLength(0);
			middleTokens.append("*LONG*");
			
			// newly added
			middleTags.setLength(0);
			middleTags.append("*LONG*");
		}
		
		// define the prefixes and suffixes
		String[] prefixTokens = new String[2];
		String[] suffixTokens = new String[2];
		
		for (int i=0; i < 2; i++) {
			int tokIndex = first[0] - i - 1;
			if (tokIndex < 0) prefixTokens[i] = "B_" + tokIndex;
			else prefixTokens[i] = tokens[tokIndex];
		}

		for (int i=0; i < 2; i++) {
			int tokIndex = second[1] + i;
			if (tokIndex >= tokens.length) suffixTokens[i] = "B_" + (tokIndex - tokens.length + 1);
			else suffixTokens[i] = tokens[tokIndex];
		}

		String[] prefixes = new String[3];
		String[] suffixes = new String[3];

		prefixes[0] = suffixes[0] = "";
		prefixes[1] = prefixTokens[0];
		prefixes[2] = prefixTokens[1] + " " + prefixTokens[0];
		suffixes[1] = suffixTokens[0];
		suffixes[2] = suffixTokens[0] + " " + suffixTokens[1];
		
		// generate the features in the same order as in ecml data
		String mto = middleTokens.toString();
		String mta = middleTags.toString();
		
		features.add(inv + "|" + firstNer + "|" + mto + "|" + secondNer);
		features.add(inv + "|" + prefixes[1] + "|" + firstNer + "|" + mto + "|" + secondNer + "|" + suffixes[1]);
		features.add(inv + "|" + prefixes[2] + "|" + firstNer + "|" + mto + "|" + secondNer + "|" + suffixes[2]);

		features.add(inv + "|" + firstNer + "|" + mta + "|" + secondNer);
		features.add(inv + "|" + prefixes[1] + "|" + firstNer + "|" + mta + "|" + secondNer + "|" + suffixes[1]);
		features.add(inv + "|" + prefixes[2] + "|" + firstNer + "|" + mta + "|" + secondNer + "|" + suffixes[2]);

		
		// dependency features
		if (depParents == null || depParents.length < tokens.length) return features;
		
		// identify head words of arg1 and arg2
		// (start at end, while inside entity, jump)
		int head1 = arg1Pos[1]-1;
		while (depParents[head1] >= arg1Pos[0] && depParents[head1] < arg1Pos[1]) head1 = depParents[head1];
		int head2 = arg2Pos[1]-1;
		//System.out.println(head1 + " " + head2);
		while (depParents[head2] >= arg2Pos[0] && depParents[head2] < arg2Pos[1]) head2 = depParents[head2];
		
		
		// find path of dependencies from first to second
		int[] path1 = new int[tokens.length];
		for (int i=0; i < path1.length; i++) path1[i] = -1;
		path1[0] = head1; // last token of first argument
		for (int i=1; i < path1.length; i++) {
			path1[i] = depParents[path1[i-1]];
			if (path1[i] == -1) break;
		}	
		int[] path2 = new int[tokens.length];
		for (int i=0; i < path2.length; i++) path2[i] = -1;
		path2[0] = head2; // last token of first argument
		for (int i=1; i < path2.length; i++) {
			path2[i] = depParents[path2[i-1]];
			if (path2[i] == -1) break;
		}
		int lca = -1;
		int lcaUp = 0, lcaDown = 0;
		outer:
		for (int i=0; i < path1.length; i++)
			for (int j=0; j < path2.length; j++) {
				if (path1[i] == -1 || path2[j] == -1) {
					break; // no path
				}
				if (path1[i] == path2[j]) {
					lca = path1[i];
					lcaUp = i;
					lcaDown = j;
					break outer;
				}
			}
		
		if (lca < 0) return features; // no dependency path (shouldn't happen)
		
		String[] dirs = new String[lcaUp + lcaDown];
		String[] strs = new String[lcaUp + lcaDown];
		String[] rels = new String[lcaUp + lcaDown];

		StringBuilder middleDirs = new StringBuilder();
		StringBuilder middleRels = new StringBuilder();
		StringBuilder middleStrs = new StringBuilder();

		if (lcaUp + lcaDown < 12) {
			
			for (int i=0; i < lcaUp; i++) {
				dirs[i] = "->";
				strs[i] = i > 0? tokens[path1[i]] : "";
				rels[i] = depTypes[path1[i]];
				//System.out.println("[" + depTypes[path1[i]] + "]->");
			}
			for (int j=0; j < lcaDown; j++) {
			//for (int j=lcaDown-1; j >= 0; j--) {
				dirs[lcaUp + j] = "<-";
				strs[lcaUp + j] = (lcaUp + j > 0)? tokens[path2[lcaDown-j]] : ""; // word taken from above
				rels[lcaUp + j] = depTypes[path2[lcaDown-j]];
				//System.out.println("[" + depTypes[path2[j]] + "]<-");
			}
			
			for (int i=0; i < dirs.length; i++) {
				middleDirs.append(dirs[i]);
				middleRels.append("[" + rels[i] + "]" + dirs[i]);
				middleStrs.append(strs[i] + "[" + rels[i] + "]" + dirs[i]);
			}
		}
		else {
				middleDirs.append("*LONG-PATH*");
				middleRels.append("*LONG-PATH*");
				middleStrs.append("*LONG-PATH*");
		}
	
		String basicDir = arg1ner + "|" + middleDirs.toString() + "|" + arg2ner;
		String basicDep = arg1ner + "|" + middleRels.toString() + "|" + arg2ner;
		String basicStr = arg1ner + "|" + middleStrs.toString() + "|" + arg2ner;
		

		// new left and right windows: all elements pointing to first arg, but not on path
		//List<Integer> lws = new ArrayList<Integer>();
		//List<Integer> rws = new ArrayList<Integer>();
		
		List<String> arg1dirs = new ArrayList<String>();
		List<String> arg1deps = new ArrayList<String>();
		List<String> arg1strs = new ArrayList<String>();
		List<String> arg2dirs = new ArrayList<String>();
		List<String> arg2deps = new ArrayList<String>();
		List<String> arg2strs = new ArrayList<String>();
		
		// pointing out of argument
		for (int i=0; i < tokens.length; i++) {
			// make sure itself is not either argument
			//if (i >= first[0] && i < first[1]) continue;
			//if (i >= second[0] && i < second[1]) continue;
			if (i == head1) continue;
			if (i == head2) continue;
			
			// make sure i is not on path
			boolean onPath = false;
			for (int j=0; j < lcaUp; j++) if (path1[j] == i) onPath = true;
			for (int j=0; j < lcaDown; j++) if (path2[j] == i) onPath = true;
			if (onPath) continue;
			// make sure i points to first or second arg
			//if (depParents[i] >= first[0] && depParents[i] < first[1]) lws.add(i);
			//if (depParents[i] >= second[0] && depParents[i] < second[1]) rws.add(i);
			if (depParents[i] == head1) {
				//lws.add(i);
				arg1dirs.add("->");				
				arg1deps.add("[" + depTypes[i] + "]->");
				arg1strs.add(tokens[i] + "[" + depTypes[i] + "]->");
			}
			if (depParents[i] == head2) {
				//rws.add(i);			
				arg2dirs.add("->");				
				arg2deps.add("[" + depTypes[i] + "]->");
				arg2strs.add("[" + depTypes[i] + "]->" + tokens[i]);
			}
		}
		
		
		// case 1: pointing into the argument pair structure (always attach to lhs):
		// pointing from arguments
		if (lcaUp == 0 && depParents[head1] != -1 || depParents[head1] == head2) {
			arg1dirs.add("<-");				
			arg1deps.add("[" + depTypes[head1] + "]<-");
			arg1strs.add(tokens[head1] + "[" + depTypes[head1] + "]<-");
			
			if (depParents[depParents[head1]] != -1) {
				arg1dirs.add("<-");
				arg1deps.add("[" + depTypes[depParents[head1]] + "]<-");
				arg1strs.add(tokens[depParents[head1]] + "[" + depTypes[depParents[head1]] + "]<-");
			}
		}
		// if parent is not on path or if parent is 
		if (lcaDown == 0 && depParents[head2] != -1 || depParents[head2] == head1) { // should this actually attach to rhs???
			arg1dirs.add("<-");
			arg1deps.add("[" + depTypes[head2] + "]<-");
			arg1strs.add(tokens[head2] + "[" + depTypes[head2] + "]<-");
			
			if (depParents[depParents[head2]] != -1) {
				arg1dirs.add("<-");
				arg1deps.add("[" + depTypes[depParents[head2]] + "]<-");
				arg1strs.add(tokens[depParents[head2]] + "[" + depTypes[depParents[head2]] + "]<-");
			}
		}
		
		// case 2: pointing out of argument
		
		//features.add("dir:" + basicDir);		
		//features.add("dep:" + basicDep);

		
		// left and right, including word
		for (String w1 : arg1strs)
			for (String w2 : arg2strs)
				features.add("str:" + w1 + "|" + basicStr + "|" + w2);
		
		/*
		for (int lw : lws) {
			for (int rw : rws) {
				features.add("str:" + tokens[lw] + "[" + depTypes[lw] + "]<-" + "|" + basicStr
						+ "|" + "[" + depTypes[rw] + "]->" + tokens[rw]);
			}
		}
		*/
		
		
		
		// only left
		for (int i=0; i < arg1dirs.size(); i++) {
			features.add("str:" + arg1strs.get(i) + "|" + basicStr);
			features.add("dep:" + arg1deps.get(i) + "|" + basicDep);
			features.add("dir:" + arg1dirs.get(i) + "|" + basicDir);
		}
		
		
		// only right
		for (int i=0; i < arg2dirs.size(); i++) {
			features.add("str:" + basicStr + "|" + arg2strs.get(i));
			features.add("dep:" + basicDep + "|" + arg2deps.get(i));
			features.add("dir:" + basicDir + "|" + arg2dirs.get(i));
		}

		features.add("str:" + basicStr);

		return features;
	}

    public List<String> getFeaturesForEntity(EntityWrapper e) {
        return getFeatures(e.sentenceId, e.tokens, e.posTags, e.dependencyParents,
                e.dependencyTypes, e.entityPos, e.entity2Pos, e.entity, e.entity2);
    }


}
