import java.util.Arrays;
import java.util.HashMap;

public class Predicate {

	String predicate;
	String[] var;

	public Predicate(String predicate, String[] variables){

		this.predicate = predicate;
		this.var = variables;
	}

	public static Predicate clonePredicate(Predicate shallowPred){

		Predicate p = new Predicate(shallowPred.predicate , shallowPred.var.clone());
		return p;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		//if first character is Capital then only add to hash code
		for(int i = 0; i<var.length;i++){
			if(Character.isUpperCase(var[i].charAt(0))){
				result = prime * result + Arrays.hashCode(var);
			}
		}
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
		Predicate other = (Predicate) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate)){
			return false;
		}
		if(this.var.length!=other.var.length)
			return false;

		for(int i = 0; i<this.var.length;i++){
			if(Character.isUpperCase(this.var[i].charAt(0)) && Character.isLowerCase(other.var[i].charAt(0)))
				return false;
			else if(Character.isUpperCase(this.var[i].charAt(0)) && Character.isUpperCase(other.var[i].charAt(0))){
				if(!this.var[i].equals(other.var[i])){
					return false;
				}

			}

		}
		//override equal
		return true;
	}

	public static HashMap<String,String> unify(String[] predOne, String[] predTwo){

		HashMap<String,String> hm = new HashMap<String,String>();

		for(int i = 0;i<predOne.length;i++){
			String firstVar = predOne[i];
			String secondVar = predTwo[i];

			if(firstVar.equals(secondVar))
				continue;	

			else if(Character.isLowerCase(firstVar.charAt(0))){
				if(!hm.containsKey(Character.isLowerCase(firstVar.charAt(0))))
					hm.put(firstVar,secondVar);
				else
					continue;
			}

			else if(Character.isLowerCase(secondVar.charAt(0))){
				if(!hm.containsKey(Character.isLowerCase(secondVar.charAt(0))))
					hm.put(secondVar,firstVar);
				else
					continue;
			}


			else
				return null;

		}
		return hm;	
	}

	public static void unify(String[] predOne, String[] predTwo,int i,int j, HashMap<String,String> hm){

		if(i==predOne.length){
			return;
		}

		String firstVar = predOne[i];
		String secondVar = predTwo[j];

		if(firstVar.equals(secondVar)){
			unify(predOne, predTwo,i+1,j+1,hm);
			return;
		}
		
		if(Character.isUpperCase(firstVar.charAt(0)) && Character.isUpperCase(secondVar.charAt(0))){
			if(!firstVar.equals(secondVar)){
				hm = null;
				return;
			}
		}

		if(!Character.isLowerCase(firstVar.charAt(0))){
			unify(predTwo, predOne,i, j, hm);
			return;
		}
		if(hm!=null && hm.containsKey(firstVar)){
			predOne[i] = hm.get(firstVar);
			unify(predOne, predTwo, i, j, hm);
			return;
		}
		else if( hm!=null &&  hm.containsKey(secondVar)){
			predTwo[j] = hm.get(secondVar);
			unify(predOne, predTwo, i, j, hm);
			return;
		}
		else{
			if(hm==null){
				hm= new HashMap<String, String>();
			}
			hm.put(firstVar, secondVar);
			unify(predOne, predTwo,i+1,j+1,hm);
			return;
		}
	}
}
