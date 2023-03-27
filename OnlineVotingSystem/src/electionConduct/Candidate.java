package electionConduct;
import java.util.Date;


public class Candidate{
	
  String candidateName;
  Date dob;
  String partyName;
  String symbol;
  Election election; 
  String voterId;
  
  public Candidate(String candidateName, Date dob, String partyName, String symbol, Election election){
	  this.candidateName = candidateName;
	  this.dob = dob;
	  this.partyName = partyName;
	  this.symbol = symbol;
	  this.election = election;
  }
  
 
public String toString() {
	System.out.println();
	return "[ candidateName = " + candidateName + ", dob = " + dob + ", partyName = " + partyName + ", symbol = "+ symbol + ", Election = " + election.electionTitle + " ]";
}

public String getCandidateName() {
	 return candidateName;
  }

  public void setCandidateName(String candidateName) {
	 this.candidateName = candidateName;
  }

  public Election getElection() {
	 return election;
  }

  public void setElection(Election election) {
	 this.election = election;
  }

  public String getPartyName() {
	 return partyName;
  }

  public void setPartyName(String partyName) {
	 this.partyName = partyName;
  }

  public String getSymbol() {
	 return symbol;
  }

  public void setSymbol(String symbol) {
	 this.symbol = symbol;
  }
  
}
