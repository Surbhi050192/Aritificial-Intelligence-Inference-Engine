import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Sentence {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fSentence == null) ? 0 : fSentence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (fSentence == null) {
			if (other.fSentence != null)
				return false;
		} else if (!fSentence.equals(other.fSentence))
			return false;
		return true;
	}

	ArrayList<Predicate> fSentence;

	//Create sentence
	public Sentence(ArrayList<Predicate> fSentence) {
		this.fSentence = fSentence;
	}

	//Clone sentence
	public static Sentence cloneSentence(Sentence shallowSentence){

		ArrayList<Predicate> cloneSentence = new ArrayList<Predicate>();
		for(int i = 0;i<shallowSentence.fSentence.size();i++){
			Predicate p = Predicate.clonePredicate(shallowSentence.fSentence.get(i));
			cloneSentence.add(p);
		}
		Sentence copySentence = new Sentence(cloneSentence);
		return copySentence;
	}

	//Factor the sentence and remove duplicates
	public static void factoringKB(Sentence sentence){
		
		ArrayList<Predicate> al = new ArrayList<Predicate>();
		HashSet<Predicate> hs = new HashSet<Predicate>(sentence.fSentence);

		sentence.fSentence = new ArrayList<Predicate>(hs);
		

	}

	public static HashMap<String,ArrayList<Sentence>> cloneKB(HashMap<String,ArrayList<Sentence>> knowledge){
		HashMap<String, ArrayList<Sentence>> cloneKnowledge = new HashMap<String, ArrayList<Sentence>>();
		for(Entry<String, ArrayList<Sentence>> entry : knowledge.entrySet()){
			ArrayList<Sentence> clonedArraySentence = new ArrayList<Sentence>();
			String key = entry.getKey();
			ArrayList<Sentence> al = entry.getValue();
			for(int i = 0;i<al.size();i++){
				Sentence clonePred = cloneSentence(al.get(i));
				clonedArraySentence.add(clonePred);
			}
			cloneKnowledge.put(key, clonedArraySentence);
		}
		return cloneKnowledge;

	}

	//Merge and resolve sentence
	public static Sentence resolve(Sentence s1, Sentence s2){

		ArrayList<Predicate> al = new ArrayList<Predicate>();

		HashSet<Predicate> first = new HashSet<Predicate>(s1.fSentence);
		HashSet<Predicate> second = new HashSet<Predicate>(s2.fSentence);
		HashSet<Predicate> merge = new HashSet<Predicate>();

		HashSet<Predicate> hs = new HashSet<Predicate>();

		Iterator firstIterator = first.iterator(); 

		while (firstIterator.hasNext()){
			Predicate firstPred = (Predicate) firstIterator.next();
			if(firstPred.predicate.charAt(0)=='~'){
				String other = firstPred.predicate.substring(1, firstPred.predicate.length());
				Predicate secondPred = new Predicate(other,firstPred.var.clone());
				if(!second.contains(secondPred)){
					Predicate addList = Predicate.clonePredicate(firstPred);
					merge.add(addList);
				}
			}
			else{
				String other = '~' + firstPred.predicate;
				Predicate secondPred = new Predicate(other,firstPred.var.clone());
				if(!second.contains(secondPred)){
					Predicate addList = Predicate.clonePredicate(firstPred);
					merge.add(addList);
				}
			}
		}

		Iterator secondIterator = second.iterator(); 

		while (secondIterator.hasNext()){
			Predicate firstPred = (Predicate) secondIterator.next();
			if(firstPred.predicate.charAt(0)=='~'){
				String other = firstPred.predicate.substring(1, firstPred.predicate.length());
				Predicate secondPred = new Predicate(other,firstPred.var.clone());
				if(!first.contains(secondPred)){
					Predicate addList = Predicate.clonePredicate(firstPred);
					merge.add(addList);
				}
			}
			else{
				String other = '~' + firstPred.predicate;
				Predicate secondPred = new Predicate(other,firstPred.var.clone());
				if(!first.contains(secondPred)){
					Predicate addList = Predicate.clonePredicate(firstPred);
					merge.add(addList);
				}
			}
		}

		Iterator mergeList = merge.iterator();
		while(mergeList.hasNext()){
			al.add((Predicate)mergeList.next());
		}
		Sentence sen = new Sentence(al);
		return sen;
	}

	//Replace all variables with unified values
	public static Sentence replaceAll(Sentence fullSentence, HashMap<String,String> unify){

		ArrayList<Predicate> newSentence = new ArrayList<Predicate>();

		for(int i = 0;i<fullSentence.fSentence.size();i++){

			Predicate newPred = new Predicate(fullSentence.fSentence.get(i).predicate,fullSentence.fSentence.get(i).var.clone());
			String[] varPredicate = newPred.var;
			for(int j = 0;j<varPredicate.length;j++){
				if(unify.containsKey(varPredicate[j])){
					varPredicate[j] = unify.get(varPredicate[j]);
				}
			}
			newSentence.add(newPred);
		}
		Sentence sen = new Sentence(newSentence);
		return sen;
	}
}
