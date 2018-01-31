import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Character.Subset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class homework {

	private Scanner s;
	private static Set<Sentence> knowledgeBaseSet = new HashSet<Sentence>();
	private static boolean found = false;

	static HashMap<String,ArrayList<Sentence>> knowledge = new HashMap<>();

	public void openfile(String fileName){
		try{
			s = new Scanner(new File(fileName));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readfile() throws IOException{

		ArrayList<String> queries = new ArrayList<String>();

		int numberofQueries = Integer.parseInt(s.nextLine().trim());
		for(int i = 0;i<numberofQueries;i++){
			queries.add(s.nextLine().trim());
		}

		//Parsing the query
		ArrayList<Sentence> query = parseSentence(queries);

		ArrayList<String> sentence = new ArrayList<String>(); 
		int numberofKB = Integer.parseInt(s.nextLine().trim());
		for(int i = 0;i<numberofKB;i++){
			sentence.add(s.nextLine().trim());
		}

		//Parsing the KB
		ArrayList<Sentence> KBsentence = parseSentence(sentence);
		for(Sentence sent: KBsentence){
			Sentence.factoringKB(sent);
			addToKnowledgeBase(sent,knowledge);
			knowledgeBaseSet.add(sent);
		}

		HashMap<String,ArrayList<Sentence>>  hm = Sentence.cloneKB(knowledge);
		Boolean [] result = new Boolean [queries.size()];
		
		sendQuery(query, result);

	}

	public static ArrayList<Sentence> parseSentence(ArrayList<String> sentence) {

		ArrayList<Sentence> list = new ArrayList<Sentence>();

		for(int i = 0;i<sentence.size();i++){
			String str = sentence.get(i);
			ArrayList<Predicate> predSentence = new ArrayList<Predicate>();
			StringTokenizer orTokens = new StringTokenizer(str, "|");

			while(orTokens.hasMoreTokens()){
				//remove space
				String statement = orTokens.nextToken().trim();
				//System.out.println(statement);
				String predicate = statement.substring(0,statement.indexOf("(")).trim();

				//System.out.println(predicate);
				String s1 = statement.substring(statement.indexOf("(")+1,statement.indexOf(")")).trim();

				//System.out. println(s1);
				StringTokenizer brackets = new StringTokenizer(s1, ",");

				String var[] = new String[brackets.countTokens()];

				int k = 0;
				while(brackets.hasMoreTokens()){
					String variable = brackets.nextToken().trim();

					if(Character.isLowerCase(variable.charAt(0))){
						var[k++] = variable + (i+1);	
					}
					else{
						var[k++] = variable;
					}	
				}

				Predicate p1 = new Predicate(predicate,var);
				//System.out.println("the perdicate is");
				//Display.displayPredicate(p1);
				predSentence.add(p1);
			}

			list.add(new Sentence(predSentence));
		}

		return list;

	}

	public static void addToKnowledgeBase(Sentence fullSentence, HashMap<String,ArrayList<Sentence>> knowledgeMap) {

		ArrayList<Sentence> allSentence = new ArrayList<Sentence>();

		for(int m = 0;m<fullSentence.fSentence.size();m++){
			if(knowledgeMap.containsKey(fullSentence.fSentence.get(m).predicate)){
				allSentence = knowledgeMap.get(fullSentence.fSentence.get(m).predicate);
				//System.out.println(fullSentence.get(m).predicate);
				allSentence.add(fullSentence);
				knowledgeMap.put(fullSentence.fSentence.get(m).predicate,allSentence);
			}
			else{
				allSentence = new ArrayList<Sentence>();
				allSentence.add(fullSentence);
				knowledgeMap.put(fullSentence.fSentence.get(m).predicate,allSentence);
			}
		}

	}
	
	public static void sendQuery(ArrayList<Sentence> queries, Boolean [] result) throws IOException{

		for(int r = 0;r<queries.size();r++){

			String appendNotQuery = "";
			String query = queries.get(r).fSentence.get(0).predicate;
			String variable[] = queries.get(r).fSentence.get(0).var;

			if(query.charAt(0)!='~'){
				appendNotQuery = '~' + query;
			}
			else{
				appendNotQuery = query.substring(1, query.length());
			}

			Predicate p = new Predicate(appendNotQuery,variable);

			ArrayList<Predicate> solveQuery = new ArrayList<Predicate>();
			solveQuery.add(p);

			Sentence firstQuery = new Sentence(solveQuery);
			HashMap<String,ArrayList<Sentence>> cloneKnowledge = Sentence.cloneKB(knowledge);
			addToKnowledgeBase(firstQuery, cloneKnowledge);
			
			
			Set<Sentence> visited = new HashSet<Sentence>();
			visited.addAll(knowledgeBaseSet);

			found = false;


			DFS(firstQuery,visited,cloneKnowledge);
			result[r]= found;

			//System.out.println(result[r]);
		}
		
		display_sol(result);
		//Call write method to add result

	}
	
	public static void display_sol(Boolean[] result) throws IOException{

				String output = "output.txt";
				File fout = new File(output);
				FileOutputStream fos = new FileOutputStream(fout);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				for(int i = 0;i<result.length;i++){
					bw.write(result[i].toString().toUpperCase());
					bw.newLine();
				}
						
						bw.close();
	}

	public static void DFS(Sentence query, Set<Sentence> visited,HashMap<String,ArrayList<Sentence>> cloneKnowledge){
		if(found ==true)
			return;

		String pred = "";
		String findPred = "";
		String[] var;

		for(int i = 0;i<query.fSentence.size();i++){
			pred = query.fSentence.get(i).predicate;
			var = query.fSentence.get(i).var;

			if(pred.charAt(0)=='~'){
				findPred = pred.substring(1,pred.length());
			}

			else{
				findPred = '~' + pred;
			}

			Predicate p1 = new Predicate(findPred,var);
			if(cloneKnowledge.containsKey(findPred)){

				ArrayList<Sentence> querySentence = new ArrayList<Sentence>();
				querySentence = cloneKnowledge.get(findPred);

				//Pick sentence from list of sentences
				for(int d = 0;d<querySentence.size();d++){
					//Pick first sentence
					Sentence s = Sentence.cloneSentence(querySentence.get(d));

					//System.out.println("Picked sentence ");
					//Display.displayListPredicate(s.fSentence);


					for(int m = 0;m<s.fSentence.size();m++){
						//Traverse the first sentence to find predicate
						if(findPred.equals(s.fSentence.get(m).predicate)){
							//Call Unification
							HashMap<String,String> predValues = new HashMap<String,String>();
							Predicate.unify(var,s.fSentence.get(m).var, 0,0 ,predValues);

//							System.out.println();
//							System.out.println("Unify values ");
//							if(predValues !=null){
//								for (Entry<String, String> entry : predValues.entrySet())
//								{
//									System.out.print(entry.getKey() + " " + entry.getValue());
//									System.out.println();
//								}
//							}

							//Call Replace variables in sentence

							if(predValues!=null){
								Sentence firstSentence = Sentence.replaceAll(query, predValues);;
								Sentence secondSentence = Sentence.replaceAll(s, predValues);

								//System.out.println("trying to resolve ");
								//Display.displayListPredicate(firstSentence.fSentence);


								//System.out.println("against  ");
								//Display.displayListPredicate(secondSentence.fSentence);

								Sentence mergedSentence =  Sentence.resolve(firstSentence,secondSentence);
								
								

								//System.out.println("got  ");
								//Display.displayListPredicate(mergedSentence.fSentence);

								if(!visited.contains(mergedSentence) && !mergedSentence.fSentence.containsAll(firstSentence.fSentence) 
										&& !mergedSentence.fSentence.containsAll(firstSentence.fSentence)){
									addToKnowledgeBase(mergedSentence, cloneKnowledge);
									
									visited.add(mergedSentence);

									if(mergedSentence.fSentence.size()==0){
										found = true;
										break;
									} else{
										DFS(mergedSentence,visited,cloneKnowledge);
									}
								}
							}
						}
					}
					if(found== true){
						break;
					}

				}
				if(found == true)
					break;

			}
			if(found==true){
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException{
		homework hw = new homework();
		String fileName = "input.txt";
		hw.openfile(fileName);
		hw.readfile();

	}
}
