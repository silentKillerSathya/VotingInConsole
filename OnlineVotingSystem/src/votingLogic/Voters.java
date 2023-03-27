package votingLogic;
import electionConduct.*;
import mainPackage.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Voters{
   
	String name;
	Gender gender;
	Date dob;
	String voterId;
	Taluk taluk;
	State state;
	Scanner sc = new Scanner(System.in);
	
	public Voters(String name, Gender gender, Date dob, String voterId, Taluk taluk, State state) {
		
		this.name = name;
		this.gender = gender;
		this.dob = dob;
		this.voterId = voterId;
		this.taluk = taluk;
		this.state = state;
	}
	
	public boolean vote() {
		
		List<Integer> electionIdList = new ArrayList<Integer>();
		ResultSet rs = null;
		try {
			Date date = new Date();
			PreparedStatement stmt = null;
			if((this.taluk != null) && (this.state == null)){
				stmt = MainClass.dbConnection.prepareStatement("select ID, Title from Election where ElectionDate = ? and Status = ? and WhichTaluk = ?");
				stmt.setString(3, this.taluk.toString());
			}
			else if((this.state != null) && (this.taluk == null)){
				stmt = MainClass.dbConnection.prepareStatement("select ID, Title from Election where ElectionDate = ? and Status = ? and WhichState = ?");
				stmt.setString(3, this.state.toString());
			}
			else {
				stmt = MainClass.dbConnection.prepareStatement("select ID, Title from Election where ElectionDate = ? and Status = ? and WhichState = ? and WhichTaluk = ?");
				stmt.setString(3, this.state.toString());
				stmt.setString(4, this.taluk.toString());
			}	
			stmt.setDate(1, new java.sql.Date(date.getTime()));
			stmt.setString(2, "Active");
			rs = stmt.executeQuery();
			if(rs.next()) {
				System.out.println();
				electionIdList.add(rs.getInt(1));
				System.out.println("ID: "+rs.getInt(1)+" -->  Title: "+rs.getString(2));
				while(rs.next()) {
					electionIdList.add(rs.getInt(1));
					System.out.println("ID: "+rs.getInt(1)+" -->  Title: "+rs.getString(2));
				}
				System.out.println();
			}
		}
		catch (SQLException e) {
			System.out.println("ddd");
			System.out.println(e.getMessage());		
		}
		if(electionIdList.size()==0) {
			System.out.println("Sorry! No elections!");
			return false;
		}
			else {
				int electionId = 0;
				while(true) {
					System.out.print("Which election you want to vote? (Please select the Election Id) : ");
					try {
						electionId = sc.nextInt();
					}
					catch(Exception ex) {
						System.out.println("please enter the valid input!");
						System.out.println();
						continue;
					}
					boolean isIdExist = false;
					for(Integer id : electionIdList) {
						if(electionId == id) {
							isIdExist = true;
							break;
						}
					}
					if(isIdExist == false) {
						System.out.println("Please enter the proper input!");
						System.out.println();
						continue;
					}
					break;
				}
				sc.nextLine();
				
				List<String> symbols = showCandidates(electionId);
				if(symbols.size()==0) {
					System.out.println("Sorry! No Candidates!");
					return false;
				}
				else {
					String vote = null;
					while(true) {
						System.out.print("Which candidate do you want to vote for? (Please enter the Symbol of the candidate) : ");
						vote = sc.nextLine();
						boolean crctSymbol = false;
						for(String symbol : symbols) {
							if(symbol.equals(vote)) {
								crctSymbol = true;
								break;
							}
						}
						if(crctSymbol == false) {
							System.out.println("Please enter the valid symbol!");
							System.out.println();
							continue;
						}
						break;
					}	
				 boolean successfullyVoteInserted = insertVote(electionId, this.voterId, vote);
				 if(!successfullyVoteInserted){
					 return false;
				 }
				}
			}		
		return true;				
		}	
		
	public List<String> showCandidates(int electionId) {
		
		List<String> symbols = new ArrayList<String>();
		try {
			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select Name, PartyName, Symbol from Candidates where ElectionId = ?");
			stmt.setInt(1, electionId);
			ResultSet rs = stmt.executeQuery();
					
			System.out.println();
			
			if(rs.next()) {
				System.out.printf("%10s %20s %20s","CANDIDATE NAME","PARTY NAME","SYMBOL");
				System.out.println();
				System.out.println();
				System.out.printf("%10s %20s %20s",rs.getString(1),rs.getString(2),rs.getString(3));
				symbols.add(rs.getString(3));
				System.out.println();
				while(rs.next()) {
					System.out.printf("%10s %20s %20s",rs.getString(1),rs.getString(2),rs.getString(3));
					symbols.add(rs.getString(3));
				}
				System.out.println();
			}	
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage()); 
		}
		return symbols;
	}
	
	public boolean insertVote(int electionId, String voterid, String symbol) {
		
		boolean duplicateVote = checkVote(electionId);
		if(duplicateVote) {
			System.out.println("duplicate vote!!");
			return false;
		}
		else {
			try {
				PreparedStatement stmt = MainClass.dbConnection.prepareStatement("insert into Vote values(?, ?)");
				stmt.setInt(1,electionId);
				stmt.setString(2, voterid);
				stmt.executeUpdate();
				PreparedStatement stm = MainClass.dbConnection.prepareStatement("select VotesCount from Result where ElectionId = ? and candidateSymbol = ?");
				stm.setInt(1,electionId);
				stm.setString(2, symbol);
				ResultSet rs = stm.executeQuery();
				int votesCount = 0;
				if(rs.next()) {
					votesCount = rs.getInt(1);
					PreparedStatement s = MainClass.dbConnection.prepareStatement("update Result set VotesCount = ? where ElectionId = ? and candidateSymbol = ?");
					s.setInt(1, votesCount+1);
					s.setInt(2, electionId);
					s.setString(3, symbol);
					s.executeUpdate();
				}
				else {
					PreparedStatement st = MainClass.dbConnection.prepareStatement("insert into Result values(?, ?, ?)");
					st.setInt(1,electionId);
					st.setString(2, symbol);
					st.setInt(3, 1);
					st.executeUpdate();
				}
				
			} 
			catch (SQLException e) {
				System.out.println(e.getMessage()); 
			}
			return true;
		}	
	}
	
	public boolean checkVote(int electionId) {
		
		try {
			PreparedStatement stmt = MainClass.dbConnection.prepareStatement("select * from Vote where ElectionId = ? and VoterId = ?");
			stmt.setInt(1, electionId);
			stmt.setString(2, this.voterId);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return true;
			}
			return false;
		} 
		catch (SQLException e) {
			System.out.println("aaa");
			System.out.println(e.getMessage());
			return false;
		}	
	}
}

