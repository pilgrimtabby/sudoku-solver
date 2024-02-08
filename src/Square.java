import java.util.ArrayList;

/**
 * Hold information about an individual square on a sudoku board.
 *
 * @author pilgrim_tabby
 * @version 0.0.2
 */
public class Square implements Comparable<Square> {
    private int value;
    private final int row;
    private final int col;
    /** List of possible values / solutions. */
    private ArrayList<Integer> possible;

    /**
     * Construct a square from its value and location on a new board.
     * @param grid The grid being used to construct a new board.
     * @param row 0 to 8
     * @param col 0 to 8
     */
    public Square(int[][] grid, int row, int col) {
        this.value = grid[row][col];
        this.row = row;
        this.col = col;
        setPossible(grid);
    }

    /**
     * Construct a square from another square, making a deep copy
     * of the possible solution ArrayList.
     * @param square The old square to copy.
     */
    public Square(Square square) {
        this.value = square.value;
        this.row = square.row;
        this.col = square.col;
        if (this.value == 0) {
            this.possible = new ArrayList<>();
            this.possible.addAll(square.possible);
        }
    }

    /**
     * Setter method for this.value.
     * @param i The square's value.
     */
    public void setValue(int i) {
        this.value = i;
    }

    /**
     * Getter method for this.value.
     * @return The value.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Force a square to have only 1 possibility.
     * @param i The possible solution.
     */
    public void overwritePossible(int i) {
        this.possible.clear();
        this.possible.add(i);
    }

    /**
     * Getter method for this.possible.
     * @return ArrayList of possible solutions.
     */
    public ArrayList<Integer> getPossible() {
        return this.possible;
    }

    /**
     * Delete one possible solution from this.possible.
     * @param i The possible solution to delete (not an index!).
     */
    public void delPossible(int i) {
        this.possible.remove(Integer.valueOf(i));
    }

    /**
     * Getter method for this.row.
     * @return the square's row.
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Getter method for this.col.
     * @return the square's column.
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Setter method for this.possible when first board is created.
     * @param grid The grid of values used to create a new board.
     */
    private void setPossible(int[][] grid) {
        if (this.value != 0) { return; }

        this.possible = new ArrayList<>();
        ArrayList<Integer> existing = new ArrayList<>(8);
        existing.addAll(getRowValues(grid));
        existing.addAll(getColValues(grid));
        existing.addAll(getBoxValues(grid));

        // Add possible solutions not in surrounding cells
        for (int i = 1; i <= 9; i++) {
            if (!existing.contains(i)) {
                this.possible.add(i);
            }
        }
    }

    /**
     * Get values of all solved squares in a row.
     * @param grid The 9 x 9 number grid used to make a new board.
     * @return ArrayList of solved square values in a row.
     */
    private ArrayList<Integer> getRowValues(int[][] grid) {
        ArrayList<Integer> existing = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            int val = grid[this.row][i];
            if (val != 0) {
                existing.add(val);
            }
        }
        return existing;
    }

    /**
     * Get values of all solved squares in a column.
     * @param grid The 9 x 9 number grid used to make a new board.
     * @return ArrayList of solved square values in a column.
     */
    private ArrayList<Integer> getColValues(int[][] grid) {
        ArrayList<Integer> existing = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            int val = grid[i][this.col];
            if (val != 0) {
                existing.add(val);
            }
        }
        return existing;
    }

    /**
     * Get values of all solved squares in a 3 x 3 box.
     * @param grid The 9 x 9 number grid used to make a new board.
     * @return ArrayList of solved square values in a 3 x 3 box.
     */
    private ArrayList<Integer> getBoxValues(int[][] grid) {
        ArrayList<Integer> existing = new ArrayList<>();
        int boxTopRow = (this.row / 3) * 3;
        int boxLeftCol = (this.col / 3) * 3;
        for (int i = boxTopRow; i <= boxTopRow + 2; i++) {
            for (int j = boxLeftCol; j <= boxLeftCol + 2; j++) {
                int val = grid[i][j];
                if (val != 0) {
                    existing.add(val);
                }
            }
        }
        return existing;
    }

    @Override
    public int compareTo(Square o) {
        return Integer.compare(this.possible.size(), o.possible.size());
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
