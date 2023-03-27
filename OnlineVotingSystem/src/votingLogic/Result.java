package votingLogic;
import electionConduct.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Result {
	
  Election election;
  Map<Candidate, Integer> totalvotesForEveryCandidate;
  
  public Result(Election election, Map<Candidate, Integer> totalvotesForEveryCandidate){
	  
	  this.election = election;
	  this.totalvotesForEveryCandidate = totalvotesForEveryCandidate;
	 
  }
  
  public static Map<Candidate, Integer> sort(Map<Candidate, Integer> map){
	  
	  List<Map.Entry<Candidate, Integer>> list = new LinkedList<>(map.entrySet());
	  Collections.sort(list, (Map.Entry<Candidate, Integer> o1, Map.Entry<Candidate, Integer> o2) -> o2.getValue() - o1.getValue());
	  Map<Candidate, Integer> linkedHashMap = new LinkedHashMap<Candidate, Integer>();
	  for(Map.Entry<Candidate, Integer> entry : list) {
		  linkedHashMap.put(entry.getKey(), entry.getValue());
	  }
	  return linkedHashMap;
  }

@Override
  public String toString() {	
	
	Map<Candidate, Integer> mapp = sort(totalvotesForEveryCandidate);
	
	for(Map.Entry<Candidate, Integer> map : mapp.entrySet()) {
		System.out.println(map.getKey().getCandidateName()+"  --> "+map.getValue()+" votes");
	}	
	
	return "";
   }
}
