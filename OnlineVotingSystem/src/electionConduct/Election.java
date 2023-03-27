package electionConduct;
import java.util.Date;

public class Election {
	
  String electionTitle;
  int candidatesCount;
  int electionId;
  ElectionOf electionof;
  Taluk taluk;
  State state; 
  ElectionStatus status;
  Date startDate;
  Date electionDate;
  Date resultDate;
  
  
  public Election(int ElectionId, String electionTitle, int candidatesCount, ElectionOf electionof, ElectionStatus status, Taluk taluk, State state, Date startDate, Date electionDate, Date resultDate){
	  this.electionId = ElectionId;
	  this.electionDate = electionDate;
	  this.electionTitle = electionTitle;
	  this.candidatesCount = candidatesCount;
	  this.electionof = electionof;
	  this.taluk = taluk;
	  this.state = state;
	  this.status = status;
	  this.startDate = startDate;
	  this.resultDate = resultDate;
  }

public String getElectionTitle() {
	return electionTitle;
}

public void setElectionTitle(String electionTitle) {
	this.electionTitle = electionTitle;
}

public int getCandidatesCount() {
	return candidatesCount;
}

public void setCandidatesCount(int candidatesCount) {
	this.candidatesCount = candidatesCount;
}

public int getElectionId() {
	return electionId;
}

public void setElectionId(int electionId) {
	this.electionId = electionId;
}

public ElectionOf getElectionof() {
	return electionof;
}

public void setElectionof(ElectionOf electionof) {
	this.electionof = electionof;
}

public Taluk getTaluk() {
	return taluk;
}

public void setTaluk(Taluk taluk) {
	this.taluk = taluk;
}

public State getState() {
	return state;
}

public void setState(State state) {
	this.state = state;
}

public ElectionStatus getStatus() {
	return status;
}

public void setStatus(ElectionStatus status) {
	this.status = status;
}

public Date getStartDate() {
	return startDate;
}

public void setStartDate(Date startDate) {
	this.startDate = startDate;
}

public Date getElectionDate() {
	return electionDate;
}

public void setElectionDate(Date electionDate) {
	this.electionDate = electionDate;
}

public Date getResultDate() {
	return resultDate;
}

public void setResultDate(Date resultDate) {
	this.resultDate = resultDate;
}

@Override
public String toString() {
	System.out.println();
	return "[ ElectionId = "+ electionId +", ElectionTitle = " + electionTitle + ", candidatesCount = " + candidatesCount + ", ElectionOf = " + electionof + ", Taluk = " + taluk + ", State = " + state + ", Status = " + status
			+ ", StartDate = " + startDate + ", ElectionDate = " + electionDate + ", RsesultDate = " + resultDate + " ]";
}
  
}

