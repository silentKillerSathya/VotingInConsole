package mainPackage;
import electionConduct.*;
import votingLogic.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Admin {
	
	Scanner sc = new Scanner(System.in);

	private static String adminName = "Esakki";
	private static String adminPass = "Esakki@293";
	private Admin() {}
	
	static Admin admin = new Admin();
	
	//-------------------------------------------GET INSTANCE FOR ADMIN-----------------------------------------------------------
	
	public static Admin getInstanceForAdmin() {
		if(admin == null) {
			admin = new Admin();
		}
		return admin;
	}
	
	//-----------------------------------------------ADMIN LOGIN------------------------------------------------------------------
	
	public Admin AdminlogIn(String adminName, String adminPassword) throws VotingException {
		return validateAdminLogin(adminName, adminPassword);
	}
	
	//--------------------------------------------VALIDATE ADMIN LOGIN------------------------------------------------------------
	
	public Admin validateAdminLogin(String adminName, String adminPassword) throws VotingException{
		if(adminName.equals(Admin.adminName) && adminPassword.equals(Admin.adminPass)){
			return this;
		}
		else {		
			throw new VotingException("Please enter the valid inputs for admin.");
		}
	}
	
	//-----------------------------------CHECK THE GIVEN ELECTION IS ALREADY EXIST------------------------------------------------
	
	public boolean checkElection(String title) {
		PreparedStatement stmt;
		try {
			stmt = MainClass.dbConnection.prepareStatement("select Title from Election where Title = ? and Status = ?");
			stmt.setString(1, title);
			stmt.setString(2, "Active");
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String existTitle = rs.getString(1);			
		} 
		catch (SQLException e) {
			e.getMessage();
			return true;
		}
		return false;
	}	
	
	//-----------------------------------------------CREATE ELECTION--------------------------------------------------------------
	
	public int createElection(String title, Date electiondate, Date resultDate) {
		
		System.out.print("Election of 'TALUK' / 'STATE' : ");
		String electionOf = sc.nextLine();
		
		if(electionOf.equals(ElectionOf.Taluk.toString())) {	
			String taluka = null;
			loopForTalukName:
			while(true) {				
				System.out.println("Please enter the Taluk name: ");
				System.out.println();
				int i=1;
				for(Taluk taluk : Taluk.values()) {
					System.out.print(i +"-->"+ taluk+" ");
					i++;
				}
				System.out.println();
				taluka = sc.nextLine();
				boolean talukaIsContains = false;
				for(Taluk taluk : Taluk.values()) {
					if(taluka.equals(taluk.toString())) {
						talukaIsContains = true;
						break loopForTalukName;
					}
				}
				if(talukaIsContains == false) {
					System.out.println("Please enter the proper input for taluk!");
					continue loopForTalukName;
				}
			}	
			return insertElection(electiondate, resultDate, title, 0, electionOf, taluka);
		}
		
		else if(electionOf.equals(ElectionOf.State.toString())) {
			String state = null;
			loopForStateName:
		    while(true) {
		    	System.out.println("Please enter the State name: ");
				System.out.println();
				int j=1;
				for(State st : State.values()) {
					System.out.print(j+"-->"+st+" ");
					j++;
				}
				System.out.println();
				state = sc.nextLine();
				boolean stateIsContains = false;				
				for(State states : State.values()) {
					if(state.equals(states.toString())) {
						stateIsContains = true;
						break loopForStateName;
					}
				}				
				if(stateIsContains == false) {
					System.out.println("Please enter the proper input for state!");
					continue loopForStateName;
				}
		    }
			return insertElection(electiondate, resultDate, title, 0, electionOf, state);
		}
		else {
			System.out.println("Please enter the valid choice!");
			System.out.println();
			return createElection(title, electiondate, resultDate);
		}
	}
	
	//-----------------------------------------INSERT ELECTION IN DATABASE TABLE--------------------------------------------------
	
	public int insertElection(Date electiondate, Date resultDate, String title, int count, String electionOf, String place) {
		Date startDate = new Date();
		try {
			PreparedStatement stmt = null;
			if(electionOf.equals(ElectionOf.Taluk.toString())) {
				stmt = MainClass.dbConnection.prepareStatement("insert into Election (Title, CandidatesCount, ElectionOf, status, WhichTaluk, StartDate, ElectionDate, ResultDate) values(?, ?, ?, ?, ?, ?, ?, ?)");
			}
			else {
				stmt = MainClass.dbConnection.prepareStatement("insert into Election (Title, CandidatesCount, ElectionOf, status, WhichState, StartDate, ElectionDate, ResultDate) values(?, ?, ?, ?, ?, ?, ?, ?)");
			}	
			stmt.setString(1, title);
			stmt.setInt(2, count);
			stmt.setString(3, electionOf);
			stmt.setString(4, "Active");
			stmt.setString(5, place);
			stmt.setDate(6, new java.sql.Date(startDate.getTime()));
			stmt.setDate(7, new java.sql.Date(electiondate.getTime()));		
			stmt.setDate(8, new java.sql.Date(resultDate.getTime()));						
			int result = stmt.executeUpdate();
			System.out.println("Successfully Election created!!");
			System.out.println();
			return result;
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println();
		}
		return -1;
	}
	
	//----------------------------------------VIEW ALL ACTIVE ELECTION TITLES-----------------------------------------------------
	
	public boolean viewAllActiveElectionTitles() {

		Statement stmt;
		try {
			stmt = MainClass.dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery("select ID, Title from Election where Status = 'Active'");

			if(rs.next()) {
				System.out.println();
				System.out.println("ELECTION ID: "+rs.getString(1) + "  -->  ELECTION NAME: "+ rs.getString(2));
				while(rs.next()) {				
					 System.out.println("ELECTION ID: "+rs.getString(1) + "  -->  ELECTION NAME: "+ rs.getString(2));
				 }
				System.out.println();
				return true;
			}
			return false;
		} 
		catch (SQLException e) {
			e.getMessage();
			return false;
		}
	 }
	
	//-------------------------------------------------ADD CANDIDATES-------------------------------------------------------------
	
 	public boolean addCandidates() {
 		
	       System.out.println();
		   System.out.print("Please enter the Candidate name: ");
		   String candidateName = sc.nextLine();
		   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		   Date dateOfBirth = null;
		   String partyName = null;
		   String symbol = null;
		   while(true) {
			   System.out.print("Please enter your date of birth: ");	  
			   String dob = sc.nextLine();		   
			   try {
				dateOfBirth = sdf.parse(dob);
			   } 	   
			   catch (ParseException e) {
				  System.out.println(e.getMessage());
				  System.out.println("Please enter the valid date!");
				  System.out.println();
				  continue;
			   }
			   break;
		   }	   
		   long db = dateOfBirth.getTime();
		   long currentTime = System.currentTimeMillis();
		   long days = (currentTime - db)/86400000;
		   int years = (int) (days/365);
		   
		   if(years>=21 && years<=65) {
			   
			   System.out.print("Please enter your voter id : ");
			   String candidateVoterId = sc.nextLine();
			   System.out.print("Please enter the Party name : ");
			   partyName = sc.nextLine();
			 
			 boolean activeElections = viewAllActiveElectionTitles();
			   if(!activeElections) {
				   System.out.println("Sorry! No elections are available!");
				   System.out.println();
				   return false;
			   }
			   int electionId = electionSelectionOnlyActive();
		while(true) {
			 try {
				PreparedStatement ps = MainClass.dbConnection.prepareStatement("select * from Candidates where ElectionId = ? and VoterId = ?");
				ps.setInt(1, electionId);
				ps.setString(2, candidateVoterId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					System.out.println("This candidate is already exist!");
					System.out.println();
					return false;
				}	
				break;
			 } 
			 catch (SQLException e) {
				e.getMessage();
			 }
		 } 
		 while(true) {
			 System.out.print("Please enter the symbol of this party: ");
			 symbol = sc.nextLine();
			 try {
				PreparedStatement ps = MainClass.dbConnection.prepareStatement("select * from Candidates where Symbol = ? and VoterId != ?");
				ps.setString(1, symbol);
				ps.setString(2, candidateVoterId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					System.out.println("This symbol is already exist. Your symbol is must unique. Please enter the valid input!");
					System.out.println();
					continue;
				}	
				break;
			 } 
			 catch (SQLException e) {
				 System.out.println("aaaa");
				System.out.println(e.getMessage());
				System.out.println();
			 }
		 }
			 int candidateInsertion = insertCandidate(candidateName, dateOfBirth, partyName, symbol, electionId, candidateVoterId);
			 if(candidateInsertion!=-1) {
				 System.out.println();
				 System.out.println("Successfully added!");
				 System.out.println();
				 try {
					Statement sm = MainClass.dbConnection.createStatement();
					ResultSet rs = sm.executeQuery("select CandidatesCount from Election where ID = "+electionId);
					rs.next();
					int count = rs.getInt(1);
					PreparedStatement st = MainClass.dbConnection.prepareStatement("update Election set CandidatesCount = ? where ID = ?");
					st.setInt(1, ++count);
					st.setInt(2, electionId);
					st.executeUpdate();
				 } 
				 catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			 }
			 
		   }
		   else {
			   System.out.println("Sorry! This person is not eligible for candidate!");
			   System.out.println();
		   }	
		   return true;
   }
 	
 	//------------------------------------------SELECT ELECTION IN ONLY ACTIVE----------------------------------------------------
 	
 	public int electionSelectionOnlyActive() {
 		
 		List<Integer> electionIdList = new ArrayList<Integer>();
		Statement stmt;
		int electionId = 0;
		while(true) {
			System.out.print("In which election? (Please enter the electionId) : ");		
			try {
				electionId = sc.nextInt();
			}
			catch(Exception ex) {
				System.out.println("Please enter the valid input!");
				System.out.println();
				ex.getMessage();
				continue;
			}
			break;
		}	
		sc.nextLine();
			 try {
				stmt = MainClass.dbConnection.createStatement();
				ResultSet rs = stmt.executeQuery("select ID from Election where Status = 'Active'");
				while(rs.next()) {
					electionIdList.add(rs.getInt(1));
				}	
			  } 
			  catch (SQLException e) {
				 e.getMessage();
			  }
			 boolean isElectionIdExist = false;
			 for(int i=0; i<electionIdList.size(); i++) {
				 if(electionIdList.get(i) == electionId) {
					 isElectionIdExist = true;
					 return electionId;
				 }
			 }
			 if(isElectionIdExist == false) {
				 System.out.println("Please enter the proper id!");
				 System.out.println();
				 return electionSelectionOnlyActive();
			 }	
			 
		return electionId;
 	}
 	
 	//--------------------------------------------SELECT ELECTION IN ALL----------------------------------------------------------
 	
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
 				System.out.print("Which election ? (Please select the Election ID) : ");
 	 			try {
 	 				election = sc.nextInt();
 	 			}
 	 			catch(Exception ex) {
 	 				System.out.println("Please enter the valid input!");
 	 				System.out.println();
 	 				continue;
 	 			}
 	 			sc.nextLine();
 	 			break;
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
				electionSelectionForAll();
			}		
 		}
 		return election;	
 	}
 	
 	//-----------------------------------------------VIEW CANDIDATES--------------------------------------------------------------
 	
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
 	
 	//---------------------------------------INSERT CANDIDATE IN DATABASE TABLE---------------------------------------------------
 	
	public int insertCandidate(String candidateName, Date dob, String partyName, String symbol, int electionId, String candidateVoterId) {
	try {
		PreparedStatement stmt = MainClass.dbConnection.prepareStatement("insert into Candidates (Name, DOB, PartyName, Symbol, ElectionId, VoterId) values(?, ?, ?, ?, ?, ?)");
		stmt.setString(1, candidateName);
		stmt.setDate(2, new java.sql.Date(dob.getTime()));
		stmt.setString(3, partyName);
		stmt.setString(4, symbol);
		stmt.setInt(5, electionId);	
		stmt.setString(6, candidateVoterId);
		int result = stmt.executeUpdate();
		return result;
	}
	catch(Exception ex) {
		System.out.println("bbb");
		System.out.println(ex.getMessage());
	}
	return -1;
   }
	
	//-------------------------------------------VIEW ALL ELECTIONS---------------------------------------------------------------
	
	public List<Election> viewAllElectionRecords(){
		 
		List<Election> electionList = new ArrayList<Election>();
		try {
			Statement stmt = MainClass.dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from Election");
			while(rs.next()) {
				String reason = rs.getString(4);
				ElectionOf electionOf = null;
				if(reason.equals("Taluk")) {
					electionOf = ElectionOf.Taluk;
				}
				else {
					electionOf = ElectionOf.State;
				}
				String sts = rs.getString(5);
				ElectionStatus status = null;
				if(sts.equals("Active")) {
					status = ElectionStatus.ACTIVE;
				}
				else {
					status = ElectionStatus.ARCHIVED;
				}
				String tal = rs.getString(6);
				Taluk taluk = null;
				for(Taluk t : Taluk.values()) {
					if(tal.equals(t.toString())) {
						taluk = t;
					}
				}
				String st = rs.getString(7);
				State state = null;
				for(State s : State.values()) {
					if(st.equals(s.toString())) {
						state = s;
					}
				}
				Election election = new Election(rs.getInt(1), rs.getString(2), rs.getInt(3), electionOf, status, taluk, state, rs.getDate(8), rs.getDate(9), rs.getDate(10));
				System.out.println(election);
				electionList.add(election);
			}
		}
		catch(Exception ex) {			
			System.out.println(ex.getMessage());
		}
		return electionList;
	}

	//--------------------------------------------END PARTICULAR ELECTION---------------------------------------------------------
	public void endElection() {
		
		boolean isAnyElectionExist = viewAllActiveElectionTitles();
		if(isAnyElectionExist == true) {
			int electionId = electionSelectionOnlyActive();
			try {
				PreparedStatement ps = MainClass.dbConnection.prepareStatement("update Election set Status = ? where ID = ?");
				ps.setString(1, "Archived");
				ps.setInt(2, electionId);
				ps.executeUpdate();
			} 
			catch (SQLException e) {
				e.getMessage();
			}	
			System.out.println("Successfully election ended!!");
			System.out.println();
		}		
	}
	
	public void viewResult() {
		
		int electionId = electionSelectionForAll();
		Election election = getAParticularElection(electionId);
		List<Candidate> candidatesList = getAParticularElectionCandidates(electionId);
		if(candidatesList.size()==0) {
			System.out.println("Sorry! No candidates here!");
			System.out.println();
		}
		else {
			Map<Candidate, Integer> votesForEveryCandidate = new HashMap<Candidate, Integer>();
			for(int i=0; i<candidatesList.size(); i++) {
				int totalVotesForACandidate = getVoteCountsForACandidate(candidatesList.get(i), election.getElectionId());
				votesForEveryCandidate.put(candidatesList.get(i), totalVotesForACandidate);	
			}	
			Result result = new Result(election, votesForEveryCandidate);
			System.out.println(result);
		}	
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
	
public List<Candidate> getAParticularElectionCandidates(int electionId) {
 		
 		List<Candidate> candidatesList = new ArrayList<>();
 		Election elec = getAParticularElection(electionId);
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
				}break;
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
}

