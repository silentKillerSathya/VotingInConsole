package mainPackage;
import java.sql.Connection;
import electionConduct.*;
import votingLogic.*;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MainClass {
	
   public static Connection dbConnection;
	
	static {		
		 if(dbConnection == null) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/VotingSystem", "esakki", "eh8GGogY=fyUSR");
				}
				catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
		}
	}
 
	public static void main(String[] args) {   
				
		Scanner sc = new Scanner(System.in);
		
		firstLoop:			
		while(true) {
			
			System.out.println();
			System.out.println("                                  ........ELECTION COMMISSION OF INDIA........");
			System.out.println();
			System.out.println();
			
		    System.out.println("Do you want to login as Admin or as a User?");
		    System.out.println("Press 1: ADMIN \nPress (any other): USER");
		
		    String choice = sc.nextLine();
		    
		switch(choice) {
		
		//------------------------------------------------ADMIN FLOW--------------------------------------------------------------
		
		case "1":
			Admin currentAdmin = null;
						
		    secondLoop:
			while(true) {
				System.out.print("Please enter your name: ");
				String adminName = sc.nextLine();
				System.out.print("Please enter your password: ");
				String adminPass = sc.nextLine();
				Admin admin = Admin.getInstanceForAdmin();
			
				try {
					currentAdmin = admin.AdminlogIn(adminName, adminPass);
					break;
				} 
				catch (VotingException ve) {
					System.out.println(ve.getMessage());
					System.out.println();
					continue;
				}
			}
					
			thirdLoop:
			while(true) {
			System.out.println();
			System.out.println("Press 1 --> CREATE ELECTION \nPress 2 --> ADD CANDIDATES \nPress 3 --> VIEW ELECTION \nPress 4 --> VIEW CANDIDATES \nPress 5 --> VIEW RESULT \nPress 6 --> END ELECTION \nPress (any other) --> LOGOUT");
			String option = sc.nextLine();
	
				switch(option) {
				
	//------------------------------------------------CREATE ELECTION------------------------------------------------------------
				case "1":
					String electionTitle = null;			
					fourthLoop:
						
				while(true) {									
					while(true) {
						System.out.print("Please enter the title of the Election: ");
						electionTitle = sc.nextLine();	
						boolean a = currentAdmin.checkElection(electionTitle);
						
						try {
							if(a == false) {
								throw new VotingException("This election is already exist. Please enter the proper input!");
							}				
							break;
						}
						catch(VotingException ve) {
							System.out.println(ve.getMessage());
							System.out.println();
							continue;
						}		
					}
					    String elecDate = null;
					    Date electionDate = null;
					    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					    long electionTime = 0;
					    Date currDate = new Date();    
					while(true) {
						System.out.print("Please enter the Date of the election: ");
						elecDate = sc.nextLine();
						
						try {
							electionDate = df.parse(elecDate);
						} 
						catch (ParseException e) {
							System.out.println(e.getMessage());
							System.out.println("Please enter the valid date!");
							System.out.println();
							continue;
						}
						
						 electionTime = electionDate.getTime();
						 Calendar calendar = Calendar.getInstance();
						 calendar.set(Calendar.HOUR_OF_DAY, 0);
						 calendar.set(Calendar.MINUTE, 0);
						 calendar.set(Calendar.SECOND, 0);
		   	             calendar.set(Calendar.MILLISECOND, 0);
				         long startOfDayMillis = calendar.getTimeInMillis();
			    	     
						try {
							if(!(((df.format(currDate)).equals(elecDate))) || (startOfDayMillis > electionTime)) {
								throw new VotingException("Please enter the proper date for election!");
							}
							break;
						}
						catch(VotingException ve) {
							System.out.println(ve.getMessage());
							System.out.println();
							continue;
						}
					}				
					String resDate = null;
				    Date resultDate = null;
				    
					while(true) {
						System.out.print("Please enter the Result Date of the election: ");
						resDate = sc.nextLine();
						try {
							resultDate = df.parse(resDate);
						} 
						catch (ParseException e) {
							System.out.println(e.getMessage());
							System.out.println();
							continue;
						}
						long resultTime = resultDate.getTime();
						
						try {
							if((electionTime > resultTime) || (!elecDate.equals(resDate))) {
								throw new VotingException("Result date is before the election date. Please enter the valid date for Result!");
							}
							break;
						}
						catch(VotingException ve) {
							System.out.println(ve.getMessage());
							continue;
						}	
					}
					
					int isElectionCreated = currentAdmin.createElection(electionTitle, electionDate, resultDate);
					try {
						if(isElectionCreated == -1) {
							throw new VotingException("Please enter the proper input!");
						}
						break fourthLoop;
					}
					catch(VotingException ve) {
						System.out.println(ve.getMessage());
						System.out.println();
						continue fourthLoop;
					}
				}					
				System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
				String like = sc.nextLine();
				switch(like) {					
					case "1":
						continue thirdLoop;
					default:
						System.out.println("Thank you!!!");
						continue firstLoop;
				}
                  
					
    //------------------------------------------------ADD CANDIDATES----------------------------------------------------------------
										
				//add candidates for particular election	
				case "2":

					currentAdmin.addCandidates();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String c = sc.nextLine();
					switch(c) {
					case "1":
						continue thirdLoop;
					default:
						System.out.println("Thank you!!!");
						continue firstLoop;
					}
				
    // -----------------------------------------------VIEW ELECTION------------------------------------------------------------------
					
				case "3":
					List<Election> elections = currentAdmin.viewAllElectionRecords();
					if(elections.size()==0) {
						System.out.println("No Elections found!!");
						System.out.println();
					}
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String cc = sc.nextLine();
					switch(cc) {
					case "1":
						continue thirdLoop;
					default:
						System.out.println("Thank you!!!");
						System.out.println();
						continue firstLoop;
					}
	//------------------------------------------VIEW CANDIDATES FOR ANY ELECTION-----------------------------------------------------	
				case "4":
					currentAdmin.viewCandidates();
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String a = sc.nextLine();
					switch(a) {
					case "1":
						continue thirdLoop;
					default:
						System.out.println("Thank you!!!");
						System.out.println();
						continue firstLoop;
					}
					
	//--------------------------------------------------VIEW RESULT-------------------------------------------------------------
				case "5":
					
					currentAdmin.viewResult();
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String z = sc.nextLine();
					switch(z) {
					case "1":
						continue thirdLoop;
					default:
						System.out.println("Thank you!!!");
						System.out.println();
						continue firstLoop;
					}
				
    //--------------------------------------------------END ELECTION-------------------------------------------------------------
				case "6":
					currentAdmin.endElection();
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String ccc = sc.nextLine();
					switch(ccc) {
					case "1":
						continue thirdLoop;
					default:
						System.out.println("Thank you!!!");
						System.out.println();
						continue firstLoop;
					}
				
	//-----------------------------------------------------LOGOUT--------------------------------------------------------------
				default:
					System.out.println();
					System.out.println("Thank you!!");
					System.out.println();
					continue firstLoop;		
			  }		
			}
			
	//---------------------------------------------------USER FLOW-------------------------------------------------------------
							
		default:
			
			User currentUser = null;
			while(true) {
				System.out.print("Please enter your name: ");
				String userName = sc.nextLine();
				System.out.print("Please enter your password: ");
				String password = sc.nextLine();
				System.out.print("Please enter your email: ");
				String email = sc.nextLine();
				User user = new User(userName, password, email);
				currentUser = user.userlogIn();
				try {
					if(currentUser == null) {
						throw new VotingException("Invalid password or email! Please enter the valid input!");
					}
					break;
				}
				catch(VotingException ve) {
					System.out.println(ve.getMessage());
					System.out.println();
					continue;
				}
			}
			
			optionLoop:
			while(true) {
				System.out.println();
				System.out.println("Press 1: VOTE \nPress 2: VIEW RESULT \nPress 3: VIEW CANDIDATES \nPress (any other): LOGOUT");
				String op = sc.nextLine();
				
				switch(op) {
				
   //----------------------------------------------VOTE FOR A ELECTION---------------------------------------------------------
				
				case "1":
					
					Voters voter = null;
					while(true) {
						voter = currentUser.checkVoter();
						try {
							if(voter == null) {
								throw new VotingException("You are not a voter for this election!");
							}
							break;
						}
						catch(VotingException ve) {
							System.out.println(ve.getMessage());
							System.out.println();
							System.out.println();
							System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
							String ccc = sc.nextLine();
							switch(ccc) {
							case "1":
								continue optionLoop;
							default:
								System.out.println("Thank you!!!");
								System.out.println();
								continue firstLoop;
							}
						}
					}
					boolean voting = voter.vote();
					if(voting == true) {
						System.out.println();
						System.out.println("Successfully voted!!");
					}
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String ccc = sc.nextLine();
						
					switch(ccc) {
						case "1":
							continue optionLoop;
						default:
							System.out.println("Thank you!!!");
							System.out.println();
							continue firstLoop;
					}
					
	//----------------------------------------------VIEW RESULT FOR A ELECTION---------------------------------------------------
					
				case "2":
					
					currentUser.viewResult();
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String aa = sc.nextLine();
					switch(aa) {
					case "1":
						continue optionLoop;
					default:
						System.out.println("Thank you!!");
						System.out.println();
						continue firstLoop;
					}
	
	//---------------------------------------------------VIEW CANDIDATES---------------------------------------------------------
					
				case "3":
					
					currentUser.viewCandidates();
					System.out.println();
					System.out.println("Would you like to continue or exit (1 -> continue  other -> exit)");
					String a = sc.nextLine();
					switch(a) {
					case "1":
						continue optionLoop;
					default:
						System.out.println("Thank you!!");
						System.out.println();
						continue firstLoop;
					}
					
				default:
					System.out.println();
					System.out.println("Thank you!!!");
					System.out.println();
					continue firstLoop;
				}
			}
		}				
	 }
   }
}
	
	