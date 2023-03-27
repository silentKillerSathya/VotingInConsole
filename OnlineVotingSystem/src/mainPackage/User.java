package mainPackage;
import electionConduct.*;
import votingLogic.*;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class User{
   
	String userName;
	String password;
	String email;
	
	Scanner sc = new Scanner(System.in);
	
	User(String userName, String password, String email){
		this.userName = userName;
		this.password = password;
		this.email = email;
	}
	
	public User userlogIn() {
		if((!(password.matches("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))) || (!(email.matches(".+@gmail\\.com")))) {
			return null;
		}
			return this;
	}
	
	public Voters checkVoter() {
	    
		System.out.print("Please enter voter name : ");
		String voterName = sc.nextLine();
		System.out.print("Please enter your Voter Id : ");
		String voterId = sc.nextLine();
			
		try {
			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select * from Voters where VoterName = ? and VoterId = ?");
			stmt.setString(1, voterName);
			stmt.setString(2, voterId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			
			Gender gender = null;
			if(rs.getString(2).equals("M")) {
				gender = Gender.MALE;
			}
			else if(rs.getString(2).equals("F")) {
				gender = Gender.FEMALE;
			}
			else {
				gender = Gender.OTHERS;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dob = null;
			try {
				dob = (Date) sdf.parse(rs.getString(3));
			} 
			catch (ParseException e) {
				e.getMessage();
			}
			Taluk taluk = null;
			for(Taluk t : Taluk.values()) {
				if(rs.getString(5).equals(t.toString())) {
					taluk = t;
					break;
				}
			}
			
			State state = null;
			for(State s : State.values()) {
				if(rs.getString(6).equals(s.toString())) {
					state = s;
					break;
				}
			}	
			return new Voters(voterName, gender, dob, voterId, taluk, state);
		} 
		catch (SQLException e) {
			e.getMessage();
			return null;
		}
	}
	
   public int electionSelectionForAll() {
 		
 		List<Integer> electionsId = new ArrayList<Integer>();
 		try {
			Statement st = MainClass.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("select ID, Title from Election where CandidatesCount > 0");
			while(rs.next()) {
				System.out.println("ID: "+rs.getInt(1) +"   Election Name: "+rs.getString(2));
				electionsId.add(rs.getInt(1));
			}	
		} catch (SQLException e1) {
			e1.getMessage();
		} 
 		int election = 0;
 		
 		if(electionsId.size() == 0) {
 			System.out.println("Sorry! No elections here! so No candidates here!");
 		}
 		else {
 			while(true) {
 				System.out.print("Which election candidates do you want to see? (Please select the Election ID) : ");
 	 			try {
 	 				election = sc.nextInt();
 	 			}
 	 			catch(Exception ex) {
 	 				System.out.println("Please enter the valid input!");
 	 				System.out.println();
 	 				ex.getMessage();
 	 				continue;
 	 			}
			boolean isAnyOneSelected = false;
			for(int i=0; i<electionsId.size(); i++) {
				 if(electionsId.get(i) == election) {
					 isAnyOneSelected = true;
					 break;
				 }
			 }
			if(isAnyOneSelected == false) {
				System.out.println("Please select the proper id!");
				System.out.println();
				continue;
			}	
			break;
 		}
 	  }
 		return election;	
 	}
	
     public List<Candidate> viewCandidates() {
 		
 		int election = electionSelectionForAll();
 		List<Candidate> candidates = new ArrayList<>();
 		if(election!=0) {
 			try {
 				Statement ps = MainClass.dbConnection.createStatement();
 				ResultSet rs = ps.executeQuery("select Candidates.*,Title from Election,Candidates where Candidates.ElectionId="+election+" and Election.Id=Candidates.ElectionId");
 				Statement ps2 = MainClass.dbConnection.createStatement();
 				ResultSet rs2 = ps2.executeQuery("select * from Election where ID = "+election);
 				rs2.next();				
 				String reason = rs2.getString(4);
 				ElectionOf electionOf = null;
 				if(reason.equals("Taluk")) {
 				    electionOf = ElectionOf.Taluk;
 				}
 				else {
 					electionOf = ElectionOf.State;
 				}
 				String sts = rs2.getString(5);
 				ElectionStatus status = null;
 				if(sts.equals("Active")) {
 					status = ElectionStatus.ACTIVE;
 				}
 				else {
 					status = ElectionStatus.ARCHIVED;
 				}
 				String tal = rs2.getString(6);
 				Taluk taluk = null;
 				for(Taluk t : Taluk.values()) {
 					if(tal.equals(t.toString())) {
 						taluk = t;
 					}
 				}
 				String st = rs2.getString(7);
 				State state = null;
 				for(State s : State.values()) {
 					if(st.equals(s.toString())) {
 						state = s;
 					}
 				}
 				Election elec = new Election(rs2.getInt(1), rs2.getString(2), rs2.getInt(3), electionOf, status, taluk, state, rs2.getDate(8), rs2.getDate(9), rs2.getDate(10));
 				while(rs.next()) {
 					Candidate candidate = new Candidate(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4), elec);
 					candidates.add(candidate);
 					System.out.println(candidate);
 				}
 			} 
 			catch (SQLException e) {
 				System.out.println(e.getMessage());
 			}
 		}	
 		return candidates;
 	}
     
     public void viewResult() {
 		
 		Election election = showArchivedElections();
 		if(election == null) {
 			System.out.println("Sorry! No elections!");
 		}
 		else {
 			List<Candidate> candidates = getAParticularElectionCandidates(election.getElectionId(), election);
 			if(candidates.size()==0) {
 				System.out.println("Sorry! No Candidates here!");
 				System.out.println();
 			}
 			else {
 				Map<Candidate, Integer> votesForEveryCandidate = new HashMap<Candidate, Integer>();	
 	 			for(int i=0; i<candidates.size(); i++) {
 	 				int totalVotesForACandidate = getVoteCountsForACandidate(candidates.get(i), election.getElectionId());
 	 				votesForEveryCandidate.put(candidates.get(i), totalVotesForACandidate);
 	 			}	
 	 			Result result = new Result(election, votesForEveryCandidate);
 	 			System.out.println(result);
 			}	
 		}		
 	}
     
     public Election showArchivedElections() {
 		
 		ResultSet rs = null;
 		boolean electionExist = false;
 		
 		try {	
 			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select ID, Title from Election where Status = ?");		
 			stmt.setString(1, "Archived");	
 			rs = stmt.executeQuery();
 			if(rs.next()) {
 				System.out.println("ID: "+rs.getInt(1)+" --->  Title: "+rs.getString(2));
 				while(rs.next()) {			
 	 				System.out.println("ID: "+rs.getInt(1)+" --->  Title: "+rs.getString(2));
 	 			}
 				electionExist = true;
 				System.out.println();
 			}	
 		}
 		catch (SQLException e) {
 			System.out.println(e.getMessage());		
 		}
 		if(!electionExist) {
 			return null;
 		}
 		int electionId = 0;
 		while(true) {
 			System.out.print("Which election you want to see result? (Please select the Election Id) : ");
 			try {
 				electionId = sc.nextInt();
 			}
 			catch(Exception ex) {
 				System.out.println(ex.getMessage());
 				System.out.println("please enter the valid input!");
 				System.out.println();
 				continue;
 			}
 			Election getElection = getAParticularElection(electionId);
 					
 			if(getElection == null) {
 				System.out.println("Please enter the valid id!");
 				System.out.println();
 				continue;
 			}	
 			return getElection;
 		}									
 	}
     
     public List<Candidate> getAParticularElectionCandidates(int electionId, Election elec) {
 		
 		List<Candidate> candidatesList = new ArrayList<>();
 		try {
 			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select * from Candidates where ElectionId = ?");
 			stmt.setInt(1, electionId);
 			ResultSet rs = stmt.executeQuery();
 			while(rs.next()) {
 				Candidate candidate = new Candidate(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4), elec);
 				candidatesList.add(candidate);
 			}
 		} 
 		catch (SQLException e) {		
 			e.getMessage();
 		}
 		return candidatesList;
 	}
     
     public Election getAParticularElection(int electionId) {
 		
 		Election election = null;
 		try {
 			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select * from Election where ID = ?");
 			stmt.setInt(1, electionId);
 			ResultSet rSet = stmt.executeQuery();
 			rSet.next();
 			String reason = rSet.getString(4);
 			ElectionOf electionOf = null;
 			if(reason.equals("Taluk")) {
 				electionOf = ElectionOf.Taluk;
 			}
 			else {
 				electionOf = ElectionOf.State;
 			}
 			String sts = rSet.getString(5);
 			ElectionStatus status = null;
 			if(sts.equals("Active")) {
 				status = ElectionStatus.ACTIVE;
 			}
 			else {
 				status = ElectionStatus.ARCHIVED;
 			}
 			String tal = rSet.getString(6);
 			Taluk taluk = null;
 			for(Taluk t : Taluk.values()) {
 				if(tal.equals(t.toString())) {
 					taluk = t;
 				}
 			}
 			String st = rSet.getString(7);
 			State state = null;
 			for(State s : State.values()) {
 				if(st.equals(s.toString())) {
 					state = s;
 				}
 			}
 			election = new Election(rSet.getInt(1), rSet.getString(2), rSet.getInt(3), electionOf, status, taluk, state, rSet.getDate(8), rSet.getDate(9), rSet.getDate(10));
 		} 
 		catch (SQLException e) {
 			System.out.println("asdfghjk");
 			System.out.println(e.getMessage());
 		}
 		return election;
 	}
     
     public int getVoteCountsForACandidate(Candidate candidate, int electionId) {
 		
 		int voteCounts = 0;
 		try {
 			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select VotesCount from Result where ElectionId = ? and CandidateSymbol = ?");
 			stmt.setInt(1, electionId);	
 			stmt.setString(2, candidate.getSymbol());	
 			ResultSet rs = stmt.executeQuery();
 			rs.next();
 			voteCounts = rs.getInt(1);
 		} 
 		catch (SQLException e) {
 			e.getMessage();
 		}		
 		return voteCounts;
 	}
}
