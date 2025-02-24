/**
* A text-based user interface to a Dawgram puzzle.
* 
* @author OTechCup
* @credits ["Mr. O"]
* @version 0.1
*/


package dawgram;

import java.io.*;
import java.util.*;
import java.util.regex.*;


public class DawgramUI {
    /**
     * Default constructor
     */
    public DawgramUI() {
        scnr       = new Scanner(System.in);
        Scanner fs = null;
        
        try {
            fs = new Scanner(new File(NGFILE));
        } catch (FileNotFoundException e) {
            System.out.println(NGFILE + " not found");
        }
        
        puzzle     = new Dawgram(fs);
    }
    
    /**
     * A string representation of a Dawgram puzzle suitable for console display
     * If showFullOnly is set, the unknown cells are shown as empty.
     * 
     * @param showFullOnly show all non full cells as empty
     * @return a string representation of the puzzle for display
     */
    public String display(boolean showFullOnly) {
        // collect the nums for the rows and columns
        int      numRows      = puzzle.getNumRows();
        int      numCols      = puzzle.getNumCols();
        int[][] rowNums       = new int[numRows][];
        int[][] colNums       = new int[numCols][];
        int     maxRowNumsLen = 0;
        int     maxColNumsLen = 0;
        
        for (int row=0; row<numRows; row++) {
            rowNums[row] = puzzle.getRowNums(row);
        
            if (rowNums[row].length > maxRowNumsLen)
                maxRowNumsLen = rowNums[row].length;
        }
        
        for (int col=0; col<numRows; col++) {
            colNums[col] = puzzle.getColNums(col);
        
            if (colNums[col].length > maxColNumsLen)
                maxColNumsLen = colNums[col].length;
        }
        
        // nums for columns
        StringBuffer sb = new StringBuffer();
        
        sb.append(" ".repeat(2*maxRowNumsLen+4));
        sb.append("-".repeat(numCols));
        sb.append("\n");
        
        for (int i=0; i<maxColNumsLen; i++) {
            sb.append(" ".repeat(2*maxRowNumsLen+4));
        
            for (int col=0; col<numCols; col++)
                if (i < colNums[col].length)
                    sb.append(numAsChar(colNums[col][i]));
                else
                    sb.append(" ");
            
            sb.append("\n");
        }
        
        sb.append(" ".repeat(2*maxRowNumsLen+4));
        sb.append("-".repeat(numCols));
        sb.append("\n");
        sb.append(" ".repeat(2*maxRowNumsLen+4));
        
        for (int col=0; col<numCols; col++)
            sb.append(alertChar(false, col));
        
        sb.append("\n");
        sb.append(" ".repeat(2*maxRowNumsLen+4));
        
        for (int col=0; col<numCols; col++)
            sb.append(numAsChar(col));
        
        sb.append("\n\n");
        
        // nums for row and the grid
        for (int row=0; row<numRows; row++) {
            sb.append("[");
        
            for (int i=0; i<rowNums[row].length; i++) {
                sb.append(numAsChar(rowNums[row][i]));
                if (i<rowNums[row].length-1)
                    sb.append(" ");
            }
            
            sb.append("]");
            sb.append(" ".repeat(2*(maxRowNumsLen-rowNums[row].length)));
            sb.append(alertChar(true, row));
            sb.append(numAsChar(row) + " ");
            sb.append(seqAsChar(puzzle.getRowSequence(row), showFullOnly) + "\n");
        }
        
        sb.append("\n");
        
        return sb.toString();
    }
    
    /**
     * A string representation of a Dawgram puzzle suitable for console display
     * 
     * @return a string representation of the puzzle for display
     */
    public String display() {
        return display(false);
    }
    
    /**
     * Provides the character to annotate each row to indicate if it is valid or solved
     * 
     * @param isRow a switch to indicate this is a row (true) or column (false)
     * @param idx the row or column number
     * @return the character to display
     */
    private char alertChar(boolean isRow, int idx) {
        if (isRow && (idx < 0 || idx >= puzzle.getNumRows()))
            throw new IllegalArgumentException("invalid idx for row (" + idx + ")");
        else if (!isRow && (idx < 0 || idx >= puzzle.getNumCols()))
            throw new IllegalArgumentException("invalid idx for col (" + idx + ")");
        
        if (isRow) {
            if (puzzle.isRowSolved(idx))
                return SOLVED_CHAR;
            else if (!puzzle.isRowValid(idx))
                return INVALID_CHAR;
            else
                return ' ';
        } else { // is col
            if (puzzle.isColSolved(idx))
                return SOLVED_CHAR;
            else if (!puzzle.isColValid(idx))
                return INVALID_CHAR;
            else
                return ' ';
        }
    }
    
    /**
     * Main control loop.  This displays the puzzle, then enters a loop displaying a menu,
     * getting the user command, executing the command, displaying the puzzle and checking
     * if further moves are possible
     */
    public void menu() {
        String command = "";
        
        System.out.println(display(puzzle.isSolved()));
        
        while (!command.equalsIgnoreCase("Quit") && !puzzle.isSolved())  {
            displayMenu();
            command = getCommand();
            execute(command);
      if (command.equalsIgnoreCase("Quit"))
          break;
            System.out.println(display(puzzle.isSolved()));
        
            if (puzzle.isSolved())
                System.out.println("puzzle is solved");
        }
    }
    
    /**
     * Display the user menu
     */
    private void displayMenu()  {
        System.out.println("Commands are:");
        System.out.println("   Help               [H]");
        System.out.println("   Move               [M]");
        System.out.println("   Row multi move     [R]");
        System.out.println("   Col multi move     [C]");
        System.out.println("   Undo assignment    [U]");
        System.out.println("   Restart puzzle [Clear]");
        System.out.println("   Save to file    [Save]");
        System.out.println("   Load from file  [Load]");
        System.out.println("   To end program  [Quit]");    
    }
    
    /**
     * Get the user command
     * 
     * @return the user command string
     */
    private String getCommand() {
        System.out.print ("Enter command: ");
        
        return scnr.nextLine();
    }
    
  /**
   * Execute the user command string
   * 
   * @param command the user command string
   */
    private void execute(String command) {
        if (command.equalsIgnoreCase("Quit")) {
            System.out.println("Program closing down");
            System.exit(0);
        } else if (command.equalsIgnoreCase("H")) {
            help();
        } else if (command.equalsIgnoreCase("M")) {
            move();
        } else if (command.equalsIgnoreCase("R")) {
            rowMultiMove();
        } else if (command.equalsIgnoreCase("C")) {
            colMultiMove();
        } else if (command.equalsIgnoreCase("U")) {
            undo();
        } else if (command.equalsIgnoreCase("Clear")) {
            clear();
        } else if (command.equalsIgnoreCase("Save")) {
            save();
        } else if (command.equalsIgnoreCase("Load")) {
            load();
        } else {
            System.out.println("Unknown command (" + command + ")");
        }
    }
    
    /**
     * Display some use4ful help text on the puzzle
     */
    private void help() {
        System.out.println("Dawgram is a puzzle where you must colour in/fill in the grid according to the patterns");
        System.out.println("of contiguous full cells given in the rows and columns.  Full cells are shown as '" + FULL_CHAR + "',");
        System.out.println("unknown cells as '" + UNKNOWN_CHAR + "', and cells you are sure are empty as '" + EMPTY_CHAR + "'.");
        System.out.println("If a row or column is invalid (doesn't match the pattern) this will be marked with a '" + INVALID_CHAR + "'; ");
        System.out.println("a solved row or column is marked with a '" + SOLVED_CHAR + "', but it may still be wrong because of the other");
        System.out.println("columns or rows - keep trying!");
        System.out.println("");
    }
    
    /**
     * Make a move
     */
    private void move() {
        Assign userMove = getUserMove();
        
        if (userMove == null) {
          System.out.println("invalid user move");
        
          return;
        }
        
        puzzle.setState(userMove);
    }
    
  /**
   * Get the user's move
   * 
   * @return the user move
   */
  private Assign getUserMove() {
    int row   = getInt("Enter row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
    
    if ((row<0) || (row>(puzzle.getNumRows()-1)))
      return null;
    
    int col   = getInt("Enter col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
    
    if ((col<0) || (col>(puzzle.getNumCols()-1)))
      return null;
    
    char c    = getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
    
    if (!isValidStateChar(c))
        return null;
    
    int state = stateFromChar(c);
    
    return new Assign(row, col, state);
  }
  
  /**
   * Make a multi-column row move
   */
  private void rowMultiMove() {
    ArrayList<Assign> list = getRowMultiUserMove();
    
    if (list == null) {
      System.out.println("invalid user move list");
    
      return;
    }
    
    for (Assign a : list)
        puzzle.setState(a);
  }
  
  /**
   * Get the user's multi-column row move
   * 
   * @return the move as list of moves (or null on error)
   */
  private ArrayList<Assign> getRowMultiUserMove() {
    int row = getInt("Enter row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
    
    if ((row<0) || (row>(puzzle.getNumRows()-1)))
      return null;
    
    int first = getInt("Enter first col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
    
    if ((first<0) || (first>(puzzle.getNumCols()-1)))
      return null;
    
    int last  = getInt("Enter last col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
    
    if ((last<0) || (last>(puzzle.getNumCols()-1)))
      return null;
    
    char c    = getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
    
    if (!isValidStateChar(c))
        return null;
    
    int state = stateFromChar(c);
    int start = (first <= last) ? first : last;
    int end   = (first <= last) ? last  : first;
    ArrayList<Assign> list = new ArrayList<>();
    
    for (int col=start; col<=end; col++)
        list.add(new Assign(row, col, state));
    
    return list;
  }
  
  /**
   * Make a multi-row column move
   */
  private void colMultiMove() {
    ArrayList<Assign> list = getColMultiUserMove();
    
    if (list == null) {
      System.out.println("invalid user move list");
    
      return;
    }
    
    for (Assign a : list)
        puzzle.setState(a);
  }
  
  /**
   * Get the user's multi-row column move
   * 
   * @return the move as an array-list of moves (or null on error)
   */
  private ArrayList<Assign> getColMultiUserMove() {
    int col = getInt("Enter col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
    
    if ((col<0) || (col>(puzzle.getNumCols()-1)))
      return null;
    
    int first = getInt("Enter first row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
    
    if ((first<0) || (first>(puzzle.getNumRows()-1)))
      return null;
    
    int last  = getInt("Enter last row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
    
    if ((last<0) || (last>(puzzle.getNumRows()-1)))
      return null;
    
    char c    = getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
    
    if (!isValidStateChar(c))
        return null;
    
    int state = stateFromChar(c);
    int start = (first <= last) ? first : last;
    int end   = (first <= last) ? last  : first;
    ArrayList<Assign> list = new ArrayList<>();
    
    for (int row=start; row<=end; row++)
        list.add(new Assign(row, col, state));
    
    return list;
  }
  
  /**
   * Undo the last move made on the puzzle
   */
  public void undo() {
      char c = getChar("Undo move (Y/N)?: ");
      
      if (c == 'Y') {
          // Undo the last move made on the puzzle
          puzzle.undo();
      } else if (c == 'N') {
          return;
      } else {
          undo();
      };
  }
  
  /**
   * Reset the puzzle
   */
  public void clear() {
      char c = getChar("Restart the puzzle (Y/N)?: ");
      
      if (c == 'Y') {
          // Clear the current state of the puzzle
          puzzle.clear();
      } else if (c == 'N') {
          return;
      } else {
          clear();
      };
  }
  
  /**
   * Save the state of the puzzle
   */
  public void save() {
      char c = getChar("Save game (Y/N)?: ");
      
      if (c == 'Y') { 
        // Save the current state of the puzzle
        String msg = puzzle.save();
        
        System.out.println(msg);
      } else if (c == 'N') {
          return;
      } else {
          save();
      };
  }
  
  /**
   * Save the state of the puzzle
   */
  public void load() {
      char c = getChar("Load saved game (Y/N)?: ");
      
      if (c == 'Y') { 
        // Load saved game
          puzzle.load();
      } else if (c == 'N') {
          return;
      } else {
          load();
      };
  }
  
  /**
   * Get an integer from the user
   * 
   * @param prompt a string to prompt the user
   * @return the integer (or -1 on error)
   */
  private int getInt(String prompt) {
        if (prompt == null)
            throw new IllegalArgumentException("prompt cannot be null");
        
    System.out.print (prompt);
    
    if (!scnr.hasNext()) {
      scnr.nextLine(); // clear the line
 
      return -1;
    }
    
    char c   = scnr.next().charAt(0);
    int  num = numFromChar(c);
    
    scnr.nextLine(); // clear the line
    
    return num;
  }
  
  /**
   * Get a character from the user
   * 
   * @param prompt a string to prompt the user
   * @return the character (or '?' on error)
   */
  private char getChar(String prompt) {
        if (prompt == null)
            throw new IllegalArgumentException("prompt cannot be null");
        
    System.out.print (prompt);
    
    if (!scnr.hasNext()) {
      scnr.nextLine(); // clear the line
      
      return '?'; // sentinel, not equal to one of the established chars
    }
    
    char c = scnr.next().charAt(0);
    
    scnr.nextLine(); // clear the line
    
    return c;
  }
  
  /**
   * Get a number from a character representation (0-9A-Za-z)
   * 
   * @param c the character representation
   * @return the integer representation (or -1 on error)
   */
  public static int numFromChar(char c) {
      final String  regex = "[0-9A-Za-z]";
      final Pattern pat   = Pattern.compile(regex);
      
      if (!pat.matcher(""+c).matches())
          throw new IllegalArgumentException("c must be " + regex);
      
        if ((c >= '0') && (c <= '9'))
            return (int) (c-'0');
        else if ((c >= 'A') && (c <= 'Z'))
            return (int) (c-'A'+10);
        else if ((c >= 'a') && (c <= 'z'))
            return (int) (c -'a'+36);
        else
            return -1; // should never happen
  }
    
  /**
   * Get a character representation (0-9A-Za-z) of a number
   * 
   * @param i the integer (must be positive)
   * @return the character representation (or '?' on error)
   */
    public static char numAsChar(int i) {
        if (i < 0)
            throw new IllegalArgumentException("i must be >= 0 (" + i + ")");
        if ((i >= 0) && (i < 10))
            return (char) ('0'+i);
        else if ((i >= 10) && (i < 36))
            return (char) ('A'+i-10);
        else if ((i >= 36) && (i < 62))
            return (char) ('a'+i-36);
        else
            return '?';
    }
    
    /**
     * Check if a character represents a valid cell state
     * 
     * @param c the character
     * @return true if this character represents a vaild cell state, otherwise false
     */
    public static boolean isValidStateChar(char c) {
        if ((c == FULL_CHAR)  ||
                (c == EMPTY_CHAR) ||
                (c == UNKNOWN_CHAR))
            return true;
        else
            return false;
    }
    
    /**
     * Convert a cell state sequence string into a string representation using the
     * display characters. If showFullOnly is set, the unknown cells are shown as empty.
     * 
     * @param seq the cell state sequence
     * @param showFullOnly show all non full cells as empty
     * @return the sequence ready for display
     */
    public static String seqAsChar(String seq, boolean showFullOnly) {
        if (seq == null)
            throw new IllegalArgumentException("seq cannot be null");
        if (seq.length() < Dawgram.MIN_SIZE)
            throw new IllegalArgumentException("seq cannot be shorter than " + Dawgram.MIN_SIZE);
        
        StringBuffer sb = new StringBuffer();
        
        for (int i=0; i<seq.length(); i++) {
            int state = Dawgram.UNKNOWN;
        
            try {
                state = Integer.parseInt(seq.substring(i, i+1));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("seq contains non number (" + seq.charAt(i) + ") in seq["+ i +"]");
            }
            
            if (!Cell.isValidState(state))
                throw new IllegalArgumentException("invalid state (" + state + ") in seq["+ i +"]");
            if (!showFullOnly)
                sb.append(stateAsChar(state));
            else
                sb.append(state == Dawgram.FULL ? DawgramUI.FULL_CHAR : " ");
        }
        
        return sb.toString();
    }
    
    /**
     * Convert a cell state sequence string into a string representation using the
     * display characters.
     * 
     * @param seq the cell state sequence
     * @return the sequence ready for display
     */
    public static String seqAsChar(String seq) {
        return seqAsChar(seq, false);
    }
    
    /**
     * Convert an individual cell state into a display character
     * 
     * @param state the cell state
     * @return the character ready for display
     */
    public static char stateAsChar(int state) {
        if (!Cell.isValidState(state))
            throw new IllegalArgumentException("invalid state (" + state + ")");
        if (state == Dawgram.FULL)
            return FULL_CHAR;
        else if (state == Dawgram.EMPTY)
            return EMPTY_CHAR;
        else
            return UNKNOWN_CHAR;
    }
    
    /**
     * Convert a display character into a Dawgram cell state
     * 
     * @param c the display character
     * @return the cell state
     */
    public static int stateFromChar(char c) {
        if (!isValidStateChar(c))
            throw new IllegalArgumentException("invalid state char (" + c + ")");
        
        if (c == FULL_CHAR)
            return Dawgram.FULL;
        else if (c == EMPTY_CHAR)
            return Dawgram.EMPTY;
        else
            return Dawgram.UNKNOWN;
    }
    
    public static void main(String[] args) {
        DawgramUI ui = new DawgramUI();
        
    ui.menu();
    }
    
    private Scanner  scnr   = null;
    private Dawgram puzzle = null;
    
    private static final String NGFILE   = "nons/tiny.non";
    public static final char EMPTY_CHAR   = 'X';
    public static final char FULL_CHAR    = '@'; // or use '\u2588'
    public static final char UNKNOWN_CHAR = '.';
    public static final char INVALID_CHAR = '?';
    public static final char SOLVED_CHAR  = '*';
}
