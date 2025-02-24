/**
* A cell in a Dawgram puzzle.
* 
* @author OTechCup
* @credits ["Mr. O"]
* @version 0.1
*/


package dawgram;


public class Cell {
	/**
	 * Constructor, the state is set to UNKNOWN
	 * 
	 * @param ng the dawgram puzzle this cell is part of
	 * @param row the cell row in the grid
	 * @param col the cell column in the grid
	 */
	public Cell(Dawgram ng, int row, int col) {
		if (ng == null)
			throw new IllegalArgumentException("ng cannot be null");
		
		if ((row < 0)  || (row >= ng.getNumRows()))
			throw new IllegalArgumentException("row invalid, must be 0 <= row < " + ng.getNumRows());
		
		if ((col < 0)  || (col >= ng.getNumCols()))
			throw new IllegalArgumentException("col invalid, must be 0 <= col < " + ng.getNumCols());
		
		this.ng    = ng;
		this.row   = row;
		this.col   = col;
		this.state = Dawgram.UNKNOWN;
	}
	
	/**
	 * Constructor
	 * 
	 * @param ng the dawgram puzzle this cell is part of
	 * @param row the cell row in the grid
	 * @param col the cell column in the grid
	 * @param state the initial Cell state
	 */
	public Cell(Dawgram ng, int row, int col, int state) {
		this(ng, row, col);
		if (!isValidState(state))
			throw new IllegalArgumentException("invalid state (" + state + ")");
		this.state = state;
	}
	
  /**
   * Retrieve the cell row
   * 
   * @return the row
   */
	public int getRow() {
		return row;
	}
	
  /**
   * Retrieve the cell column
   * 
   * @return the column
   */
	public int getCol() {
		return col;
	}
	
	/**
	 * Is the cell state FULL?
	 * 
	 * @return true if the cell state is FULL, otherwise false
	 */
	public boolean isFull() {
		if (!checkState())
			throw new DawgramException("invalid cell state (" + state + ")");
		return state == Dawgram.FULL;
	}
	
	/**
	 * Is the cell state EMPTY?
	 * 
	 * @return true if the cell state is EMPTY, otherwise false
	 */
	public boolean isEmpty() {
		if (!checkState())
			throw new DawgramException("invalid cell state (" + state + ")");
		return state == Dawgram.EMPTY;
	}
	
	/**
	 * Is the cell state UNKNOWN?
	 * 
	 * @return true if the cell state is UNKNOWN, otherwise false
	 */
	public boolean isUnknown() {
		if (!checkState())
			throw new DawgramException("invalid cell state (" + state + ")");
		return state == Dawgram.UNKNOWN;
	}
	
	/**
	 * Retrieve the cell state
	 * 
	 * @return the cell state (FULL, EMPY or UNKNOWN)
	 */
	public int getState() {
		if (!checkState())
			throw new DawgramException("invalid cell state (" + state + ")");
		return state;
	}
	
	/**
	 * Set the cell state to FULL
	 */
	public void setFull() {
		state = Dawgram.FULL;
	}
	
	/**
	 * Set the cell state to EMPTY
	 */
	public void setEmpty() {
		state = Dawgram.EMPTY;
	}
	
	/**
	 * Set the cell state to UNKNOWN
	 */
	public void setUnknown() {
		state = Dawgram.UNKNOWN;
	}
	
	/**
	 * Set the cell state
	 * 
	 * @param state the desired state (FULL, EMPY or UNKNOWN)
	 */
	void setState(int state) {
		if (!isValidState(state))
			throw new DawgramException("invalid state (" + state + ")");
		this.state = state;
	}
	
  /**
   * String representation of the assignment
   * 
   * @return the string representation (the cell state)
   */
	@Override
	public String toString() {
		return "" + getState();
	}
	
  /**
   * String representation of the assignment (useful for debugging)
   * 
   * @return the string representation
   */
	public String toStringFull() {
		StringBuffer sb = new StringBuffer("Cell(");
		sb.append(row);
		sb.append(",");
		sb.append(col);
		sb.append(",");
		sb.append(getState());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Check the cell state is valid (FULL, EMPY or UNKNOWN)
	 * 
	 * @return true if the state is valid, otherwise false
	 */
	private boolean checkState() {
		return isValidState(state);
	}
	
	/**
	 * Check if an integer is a valid cell state (FULL, EMPY or UNKNOWN)
	 * 
	 * @param state the integer to check
	 * @return true if the state is valid, otherwise false
	 */
	public static boolean isValidState(int state) {
		if ((state == Dawgram.FULL)  ||
				(state == Dawgram.EMPTY) ||
				(state == Dawgram.UNKNOWN))
			return true;
		else
			return false;
	}
	
	/**
	 * Check that all the cells in an array are from the same dawgram
	 * 
	 * @param cells the array of cells to check
	 * @return true if all the cells are from the same dawgram, otherwise false
	 */
	public static boolean checkSameDawgram(Cell[] cells) {
		if (cells == null)
			throw new IllegalArgumentException("cells cannot be null");
		if (cells.length < Dawgram.MIN_SIZE)
			throw new IllegalArgumentException("cells cannot be shorter than " + Dawgram.MIN_SIZE);
		Dawgram ng = cells[0].ng;
		for (int i=1; i<cells.length; i++)
			if (cells[i].ng != ng)
				return false;
		return true;
	}
		
	private int      state;
	private int      row;
	private int      col;
  private Dawgram ng = null;	
}
