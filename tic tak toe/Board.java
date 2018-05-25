import java.util.Arrays;

public class Board {
      
   private String boardStr = "";
   
   public Board(String starter) {
      boardStr = "---------";
   }
   
   public Board() {
      boardStr = "---------";
   }
   
   public void MakeMove(String target, int boardLocation) {
         String a = boardStr.substring(0,boardLocation - 1);
         String b = boardStr.substring(boardLocation);
         boardStr = a + target + b;
   }  
   
   public boolean checkWin(String target) {
      String answer = target + target + target; 
      //Checks for row win solutions
      for(int j = 0; j < 8; j+=3) {
         String test = "";
         for(int i = 0; i < 3; i++) {
            test += boardStr.substring(i+j,i+j+1);
         }
         if(test.equals(answer)) 
            return true;
      }
      //Checks for collumn solutions
      for(int j = 0; j < 3; j++) {
         String test = "";
         for(int i = 0; i < 9; i+=3) {
            test += boardStr.substring(i+j,i+j+1);
         }
         if(test.equals(answer))
            return true;
      }
      //Diagonals
      String LtoR = boardStr.substring(0,1) + boardStr.substring(4,5) + boardStr.substring(8,9); 
      String RtoL = boardStr.substring(2,3) + boardStr.substring(4,5) + boardStr.substring(6,7);
      if(LtoR.equals(answer) || RtoL.equals(answer)) 
         return true;
          
      return false; 
   }

   public String whoWon() {
      if(checkWin("X"))
         return "X";
      if(checkWin("O"))
         return "O";
      return "draw";
   }

   public void announceWinnerPlayerX() {
      if(whoWon().equals("X")) {
         System.out.println("Congratualtions! You have won! \nThe Artificial Intelligence will learn from its defeat\n");
      }
      else if(whoWon().equals("O")) {
         System.out.println("You have been defeated! \nThe Artifical Intelligence will learn from its victory\n");
      }
      else
         System.out.println("Draw!");
   }
   
   public void announceWinnerPlayerO() {
      if(whoWon().equals("O")) {
         System.out.println("Congratualtions! You have won! \nThe Artificial Intelligence will learn from its defeat\n");
      }
      else if(whoWon().equals("X")) {
         System.out.println("You have been defeated! \nThe Artifical Intelligence will learn from its victory\n");
      }
      else
         System.out.println("Draw!");
   }

   public boolean checkValidity(int Location) {
      if(Location < 1 || Location > 9)
         return false;
      if(boardStr.substring(Location - 1,Location).equals("-"))
         return true; 
      return false;
   }
   
   public boolean drawChecker() {
      for(int i = 0; i < 9; i++) {
         if(boardStr.substring(i,i+1).equals("-")) {
            String wtf = boardStr.substring(i,i+1);
            return false;
         }
      }
      return true; 
   }
   
   public void clear() {
      boardStr = "---------";
   }
   
   public String returnBoard() {
      return boardStr;
   } 
   
   public int preventDoom(String x, String a) { //forces bot to win or defend if about to win or lose
      for(int j = 0; j < 8; j+=3) { //checks rows
         String test = "";
         for(int i = 0; i < 3; i++) {
            test += boardStr.substring(i+j,i+j+1);
         }
         if(test.equals(a+a+"-")) 
            return j + 2;
         if(test.equals(a+"-"+a))
            return j + 1;
         if(test.equals("-"+a+a))
            return j;
      }
      for(int j = 0; j < 3; j++) {//collumns
         String test = "";
         for(int i = 0; i < 9; i+=3) {
            test += boardStr.substring(i+j,i+j+1);
         }
         if(test.equals(a+a+"-")) 
            return 6 + j;
         if(test.equals(a+"-"+a))
            return 4 + j;
         if(test.equals("-"+a+a))
            return j;
      }
      String LtoR = boardStr.substring(0,1) + boardStr.substring(4,5) + boardStr.substring(8,9); 
      String RtoL = boardStr.substring(2,3) + boardStr.substring(4,5) + boardStr.substring(6,7);     
      if(LtoR.equals(a + a + "-")) 
         return 8;
      if(LtoR.equals(a + "-" + a) || RtoL.equals(a + "-" + a))
         return 4;
      if(LtoR.equals("-" + a + a))
         return 0;
      if(RtoL.equals("-" + a + a))
         return 2;
      if(RtoL.equals(a + a + "-"))
         return 6;
         ////////////////////////////////
      for(int j = 0; j < 8; j+=3) { //checks rows
         String test = "";
         for(int i = 0; i < 3; i++) {
            test += boardStr.substring(i+j,i+j+1);
         }
         if(test.equals(x+x+"-")) 
            return j + 2;
         if(test.equals(x+"-"+x))
            return j + 1;
         if(test.equals("-"+x+x))
            return j;
      }
      for(int j = 0; j < 3; j++) {//collumns
         String test = "";
         for(int i = 0; i < 9; i+=3) {
            test += boardStr.substring(i+j,i+j+1);
         }
         if(test.equals(x+x+"-")) 
            return 6 + j;
         if(test.equals(x+"-"+x))
            return 4 + j;
         if(test.equals("-"+x+x))
            return j;
      }     
      if(LtoR.equals(x + x + "-")) 
         return 8;
      if(LtoR.equals(x + "-" + x) || RtoL.equals(x + "-" + x))
         return 4;
      if(LtoR.equals("-" + x + x))
         return 0;
      if(RtoL.equals("-" + x + x))
         return 2;
      if(RtoL.equals(x + x + "-"))
         return 6;      
      return -1;
   }
   
   public String toString()
   {
     return boardStr.substring(0,3) + "\n" + boardStr.substring(3,6) + "\n" + boardStr.substring(6);
   }  
   
}