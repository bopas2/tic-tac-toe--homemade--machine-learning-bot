import java.util.Scanner; 
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter; 
import java.util.Set;
import java.util.HashSet;
import java.io.*;

public class TTTRunner {
    
   public static void main( String[] args ) {
      Random rand = new Random();   //If we need to make a move, and have no moves in database, we make a random move.
      Scanner keyboard = new Scanner(System.in); //user data
      Board test = new Board();  //game board
      Map<String,Map<String,Integer>> mapX = new HashMap<>(); //holds x moves
      Map<String,Map<String,Integer>> mapO = new HashMap<>(); //holys o moves
      mapX = createMap(true); mapO = createMap(false); //if there is previous data, we upload it to our maps (true means access X data, false = O data) 
      int whatToDo = 0;
      while(whatToDo != 3) {
         whatToDo = intro();
         if(whatToDo == 1) { //determines user intent
            System.out.println("Would you like to play as 'X' or 'O'? ('X' goes first!)");
            String move = keyboard.next();
            if(move.equals("X")) {
               ArrayList<String> Moves = new ArrayList(); //tracks the moves in the game
               Moves.add(0,"---------"); //Starter board for the game
               System.out.println(test);
               while(!test.checkWin("O") && !test.checkWin("X") && !test.drawChecker()) { //Ensures neither team has won or drawed yet
                  int where = -1;
                  while(!test.checkValidity(where)) {
                     System.out.println("Enter location for your move \n(e.g. middle spot = 5, top left = 1, bottom right = 9):");
                     where = keyboard.nextInt();
                  }
                  test.MakeMove("X",where);
                  Moves.add(test.returnBoard());
                  System.out.println(test + "\n");
                  if(test.checkWin("X") || test.drawChecker())
                     break;
                  where = -1;
                  while(!test.checkValidity(where)) { //sees if where the computer is placing its move is actually legal
                        int jam = determineBestMove(test,mapO,"X","O");
                        if(test.checkValidity(jam)) {
                           where = jam;
                        }
                        else {
                           where = rand.nextInt(9) + 1;
                        }
                  }
                  test.MakeMove("O",where);
                  Moves.add(test.returnBoard()); 
                  System.out.println("Computer's move: \n" + test + "\n");                                          
               }
               test.announceWinnerPlayerX();
               mapO = updateDatabase(Moves,test,mapX,mapO,false,false,false);  
               mapX = updateDatabase(Moves,test,mapX,mapO,true,true,false);
               test.clear();   
            }
            else { //Player plays as O
               ArrayList<String> Moves = new ArrayList(); //tracks the moves in the game
               Moves.add(0,"---------"); //Starter board for the game
               System.out.println(test);
               while(!test.checkWin("O") && !test.checkWin("X") && !test.drawChecker()) { //Ensures neither team has won or drawed yet
                  int where = -1;
                  while(!test.checkValidity(where)) { //sees if where the computer is placing its move is actually legal
                        int jam = determineBestMove(test,mapX,"O","X");
                        if(test.checkValidity(jam)) {
                           where = jam;
                        }
                        else {
                           where = rand.nextInt(9) + 1;
                        }
                  }
                  test.MakeMove("X",where);
                  Moves.add(test.returnBoard()); 
                  System.out.println("Computer's move: \n" + test + "\n");
                  where = -1;      
                  if(test.checkWin("X") || test.drawChecker())
                     break;
                  while(!test.checkValidity(where)) {
                     System.out.println("Enter location for your move \n(e.g. middle spot = 5, top left = 1, bottom right = 9):");
                     where = keyboard.nextInt();
                  }
                  test.MakeMove("O",where);
                  Moves.add(test.returnBoard());
                  System.out.println(test + "\n");                                                      
               }
               test.announceWinnerPlayerO();
               mapX = updateDatabase(Moves,test,mapX,mapO,true,false,false);  
               mapO = updateDatabase(Moves,test,mapX,mapO,false,false,true); 
               test.clear(); 
            }
            move = "";
            exportData(mapX,mapO);  
         }
         if(whatToDo == 2) {
            int amountOfTraining = howManyTrainings();
            for(int i = 0; i < amountOfTraining; i++) {
               ArrayList<String> Moves = new ArrayList(); //tracks the moves in the game
               Moves.add(0,"---------"); //Starter board for the game
               while(!test.checkWin("O") && !test.checkWin("X") && !test.drawChecker()) { //Ensures neither team has won or drawed yet
                  int where = -1; //invalid placement
                  while(!test.checkValidity(where)) { //sees if where the computer is placing its move is actually legal
                     int shouldBeRandom = rand.nextInt(10) + 1; //In order to ensure we are seeing all moves, not just repeats, we need to add some randomness
                     if(shouldBeRandom % 5 == 0 || !mapX.containsValue(test.returnBoard())) { //20% will be random, or if the map doesn't have the board we are questioning
                        where = rand.nextInt(9) + 1;
                     }
                     else {
                        where = determineBestMove(test,mapX,"O","X");
                     }              
                  } 
                  test.MakeMove("X",where);
                  Moves.add(test.returnBoard());
                  if(test.checkWin("X") || test.drawChecker())
                     break;            
                  where = -1;
                  while(!test.checkValidity(where)) { //sees if where the computer is placing its move is actually legal
                     int shouldBeRandom = rand.nextInt(10) + 1; //In order to ensure we are seeing all moves, not just repeats, we need to add some randomness
                     if(shouldBeRandom % 5 == 0 || !mapO.containsValue(test.returnBoard())) {
                        where = rand.nextInt(9) + 1;
                     }
                     else {
                        where = determineBestMove(test,mapO,"X","O");
                     }
                     if(test.drawChecker()) //Stop immieadeatly if there is a draw 
                        break;                  
                  } 
                  test.MakeMove("O",where);
                  Moves.add(test.returnBoard());   
               }
               mapX = updateDatabase(Moves,test,mapX,mapO,true,false,false);  
               mapO = updateDatabase(Moves,test,mapX,mapO,false,false,false);  
               test.clear();   
            }
            exportData(mapX,mapO);         
         }
      }
   }
   
   //Takes the map and puts it into a text file
   public static void exportData(Map<String,Map<String,Integer>> mapX, Map<String,Map<String,Integer>> mapO) {
      String fileName = "dataX.txt";   //file for X's moves 
      try {
         PrintWriter outputStreamX = new PrintWriter(fileName);
         Set<String> xStarters = new HashSet<>();
         xStarters = mapX.keySet();    //Gives us access to every key in our larger X map
         for(String a : xStarters) {   //Loop through the keys
            outputStreamX.println("!" + a);  //Exclamation marks in the file indicates that it is a board we are questioning, not a move to be completed.
            Set<String> xFinishers = new HashSet<>(); 
            xFinishers = mapX.get(a).keySet();  //Get all the potential moves
            for(String b : xFinishers) {  //go through potential boards
               outputStreamX.println("*" + mapX.get(a).get(b) + "*" + b); //looks like *SCORE*BOARDSCREEN, score inbetween astriks followed by the board
            }
         }
         outputStreamX.close(); // close stream
      } catch (Exception e) { //must have an exception just in case the text file doesn't exist...???...google it lol
         e.printStackTrace();
      }
      
      String fileName2 = "dataO.txt";  //same as X moves
      try {
         PrintWriter outputStreamO = new PrintWriter(fileName2);
         Set<String> oStarters = new HashSet<>();
         oStarters = mapO.keySet();
         for(String a : oStarters) {
            outputStreamO.println("!" + a);
            Set<String> oFinishers = new HashSet<>();
            oFinishers = mapO.get(a).keySet();
            for(String b : oFinishers) {
               outputStreamO.println("*" + mapO.get(a).get(b) + "*" + b);
            }
         }
         outputStreamO.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   //Takes moves and put them into a map
   public static Map<String,Map<String,Integer>> updateDatabase(ArrayList<String> Moves, Board test, Map<String,Map<String,Integer>> mapXx, Map<String,Map<String,Integer>> mapOo, boolean X, boolean playerX, boolean playerO) {
         Map<String,Map<String,Integer>> mapX = new HashMap<>(); mapX = mapXx; //This is done to ensure no data loss when returning.
         Map<String,Map<String,Integer>> mapO = new HashMap<>(); mapO = mapOo;
         String winner = test.whoWon();   //See who won
         int xScore = 0; int oScore = 0;          //Determine if we want the next "move" to be more likely to be picked.
         if(winner.equals("X")) {            //if, in this game, the team picked the move, and won, we want to
            xScore = 1;                      //increase the likelyhood of picking that movee, if they lost,
            oScore = -1;                     //we want to decrease the likelyhood
         }
         if(winner.equals("O")) {
            xScore = -1;
            oScore = 1;         
         }
         if(playerX) 
            xScore = 0;
         if(playerO) 
            oScore = 0;
         //goes through the moves
         for(int i = 0; i < Moves.size() - 1; i++) {
            if((i % 2 == 0 || i == 0) && X ) { //determines if this was an X move or  O move. Even moves are X moves.
               if(mapX.containsKey(Moves.get(i))) {      //If the map already contains the board that the game is questioning 
                  if(mapX.get(Moves.get(i)).containsKey(Moves.get(i+1))) { //If the map already has the board we are questioning AND the move the computer made
                     int score = mapX.get(Moves.get(i)).get(Moves.get(i+1)) + xScore;  //We take the score it already has, and update it based on how it did
                     mapX.get(Moves.get(i)).replace("" + Moves.get(i+1),score);        //We update the score
                  }
                  else {
                     mapX.get(Moves.get(i)).put("" + Moves.get(i+1),xScore);  //It has the board we are questioning, but not the move. We put the move into the map, with the score. 
                  }
               }
               else {                          // It doesn't have the move we want to make OR the board we are questioning, so we make both. 
                  Map<String,Integer> newBoyO = new HashMap<>();  //new map value
                  newBoyO.put("" + Moves.get(i+1),xScore);  //make the map with its score
                  mapX.put("" + Moves.get(i),newBoyO); //put the map and score into the larger map
               }
            }
            if(i % 2 != 0 && !X) {     //this is the same proccess for X moves, but for O moves. 
               if(mapO.containsKey(Moves.get(i))) {
                  if(mapO.get(Moves.get(i)).containsKey(Moves.get(i+1))) {
                     int score = mapO.get(Moves.get(i)).get(Moves.get(i+1)) + oScore;
                     mapO.get(Moves.get(i)).replace("" + Moves.get(i+1),score);
                  }
                  else {
                     mapO.get(Moves.get(i)).put("" + Moves.get(i+1),oScore);
                  }
               }
               else {
                  Map<String,Integer> newBoyO = new HashMap<>();
                  newBoyO.put("" + Moves.get(i+1),oScore);
                  mapO.put("" + Moves.get(i),newBoyO);
               }
            }
         }
         if(X) //boolean X tells us which map we are returning
            return mapX;
         return mapO;
   }   
   
   //Creates map from reading a file
   public static Map<String,Map<String,Integer>> createMap(boolean X) {
      Map<String,Map<String,Integer>> mapO = new HashMap<>();      
      Map<String,Map<String,Integer>> mapX = new HashMap<>();      
      try {
         if(!X) {
            Scanner fileReader = new Scanner(new File("dataO.txt"));    // OPENS FILE TO READ
            String key = fileReader.nextLine();     //READS FIRST LINE
            while(fileReader.hasNextLine()) {                         //READS ENTIRE FILE
               while(key.substring(0,1).equals("!")) {                 //IF THE LINE IS A KEY, SKIP TO NEXT LINE
                     String next = fileReader.nextLine();               //READ LINE
                     if(!next.substring(0,1).equals("!")) {
                        int nextScore = Integer.parseInt(next.substring(1,next.lastIndexOf('*')));   //WE KNOW THIS ISN'T A KEY, MUST BE A VALUE LINE, GET THE INTEGER
                        Map<String,Integer> temp = new HashMap<>();              //CREATE MAP VALUE
                        temp.put(next.substring(next.lastIndexOf('*') + 1),nextScore); //GET THE NEXT BOARD AND PUT IN INTO THE MAP WITH IT'S VALUE
                        mapO.put(key.substring(1),temp);                                      //PUT MAP AND KEY INTO THE BIG BOY MAP
                     }
                     else
                        key = next;
               }
            }
            return mapO;
         }
         if(X) {
            Scanner fileReader1 = new Scanner(new File("dataX.txt"));    // OPENS FILE TO READ
            String key = fileReader1.nextLine();                         //READS FIRST LINE
            while(fileReader1.hasNextLine()) {                         //READS ENTIRE FILE
               while(key.substring(0,1).equals("!")) {                 //IF THE LINE IS A KEY, SKIP TO NEXT LINE
                     String next = fileReader1.nextLine();               //READ LINE
                     if(!next.substring(0,1).equals("!")) {
                        int nextScore = Integer.parseInt(next.substring(1,next.lastIndexOf('*')));   //WE KNOW THIS ISN'T A KEY, MUST BE A VALUE LINE, GET THE INTEGER
                        Map<String,Integer> temp = new HashMap<>();              //CREATE MAP VALUE
                        temp.put(next.substring(next.lastIndexOf('*') + 1),nextScore); //GET THE NEXT BOARD AND PUT IN INTO THE MAP WITH IT'S VALUE
                        mapX.put(key.substring(1),temp);                                      //PUT MAP AND KEY INTO THE BIG BOY MAP
                     }
                     else
                        key = next;
               }
            }
            return mapX;
         }
      } catch (Exception e) {}
      return mapO;
   }
   
   public static int intro() {
      Scanner keyboard = new Scanner(System.in);
      System.out.println("Welcome to Thomas Lang's 'Tic Tac Toe' game.");
      System.out.println("Your opponent is an artificial intelligence who learns from its defeats and victories to make decisions. It has trained for over one billion games.");
      System.out.println("Enter '1' to play \nEnter '2' to train the program\nEnter '3' to quit.");
      while(true) {
         int input = keyboard.nextInt();
         if(input == 2) 
            return 2;
         if(input == 1) 
            return 1;
         if(input == 3)
            return 3;
         System.out.println("INVALID INPUT!");
         System.out.println("Welcome to Thomas Lang's 'Tic Tac Toe' game.");
         System.out.println("Your opponent is an artificial intelligence who learns from its defeats and victories to make decisions. It has trained for over one billion games.");
         System.out.println("Enter '1' to play \nEnter '2' to train the program\nEnter '3' to quit.");
     }
   }
   
   public static int howManyTrainings() {
      Scanner keyboard = new Scanner(System.in);
      System.out.println("How many training simulations should the AI complete?");
      return keyboard.nextInt();
   }
   
   public static int determineBestMove(Board test, Map<String,Map<String,Integer>> map, String enemy, String ally) {
      int vergeOfVictory = test.preventDoom(enemy, ally);
      if(vergeOfVictory != -1)
         return vergeOfVictory + 1;
      ArrayList<String> blacklist = new ArrayList<>();
      String board = test.returnBoard();  //string of board
      String bestBoard = "";     //what best board is scorewise
      Set<String> moves = new HashSet<>();   
      if(!map.containsKey(board))
         return -1;
      moves = map.get(board).keySet();    //all of the possible moves of the current board
      int bestScore = 0;
      int counter = 0;
      for(String i : moves) { // loop through possibilities
         if(counter == 0) {
            bestBoard = i; bestScore = map.get(board).get(i); //set base best score
            counter++;
         }
         else {
            if(map.get(board).get(i) > bestScore && !onBlacklist(blacklist,i)) {   //sets new best score
               bestScore = map.get(board).get(i);
               bestBoard = i;
            }
         }
      }
      for(int i = 0; i < board.length(); i++) {
         if(!board.substring(i,i+1).equals(bestBoard.substring(i,i+1))) {  //where the two differ is where the next move goes 
            if(test.checkValidity(i))
               return i+1;
            else
               blacklist.add(bestBoard);
            }
      }
      return -1;
   }
   
   public static boolean onBlacklist(ArrayList<String> list, String victim) {
      for(String h : list) {
         if(victim == h) 
            return true;
      }
      return false; 
   }
}