/**
 * Hold the contents of a sudoku board.
 *
 * @author pilgrim_tabby
 * @version 0.0.1
 */
public class SudokuBoard implements Comparable<SudokuBoard> {
    private final Square[][] grid = new Square[9][9];
    /** The square with the least possible solutions */
    private Square priority = new Square();
    /** Number of squares with non-zero (solved) value */
    private int filled;

    /**
     * Construct new board from 9 x 9 integer nested array.
     * @param grid The nested integer array of (1-digit) numbers.
     */
    public SudokuBoard(int[][] grid) {
        // Fill this.grid with Square objects
        for (int row = 0; row <= 8; row++) {
            for (int col = 0; col <= 8; col++) {
                this.grid[row][col] = new Square(grid, row, col);
            }
        }

        // Set this.filled to count of current solved squares
        setFilled();
    }

    /**
     * Construct new board from old board by deep copying its squares
     * @param board The old board
     */
    public SudokuBoard(SudokuBoard board) {
        // Deep copy squares of old board to new board
        for (Square[] row : board.grid) {
            for (Square square : row) {
                this.grid[square.getRow()][square.getCol()] = new Square(square);
            }
        }

        this.filled = board.filled;
    }

    /**
     * Iterate over each square and update its possible.
     * Runs in a loop until no square's value is updated.
     * @return false if any square is unsolvable, true otherwise.
     */
    public boolean updateGrid() {
        int oldFilled = -1;  // run loop until no square filled
        while (oldFilled < this.filled) {
            oldFilled = this.filled;
            for (Square[] row : this.grid) {
                for (Square square : row) {
                    boolean boardIsPossible = update(square);
                    if (!boardIsPossible) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Verify a board is obeying "sudoku rules" (no duplicates of
     * 1-9 in a column, row, or 3x3 box).
     * @return false if a rule is broken, true otherwise.
     */
    public boolean validBoard() {
        // Copy this array each time a row / column / box is checked.
        // Each index represents a digit from 1 to 9 respectively.
        // For example, if a row contains the digit 3, the value at
        // index 2 is set to true. If the method attempts to set a
        // value to true and it's already true (duplicate found),
        // it returns false.
        boolean[] contents = { false, false, false, false, false, false, false, false, false };

        // Check rows for duplicates
        for (int row = 0; row <= 8; row++) {
            boolean[] rowContents = contents.clone();
            for (int col = 0; col <= 8; col++) {
                // Ignore "blank" squares
                if (this.grid[row][col].getValue() == 0) {
                    continue;
                }
                // Digit is not 0-9 = invalid
                if (this.grid[row][col].getValue() < 0 || this.grid[row][col].getValue() > 9) {
                    return false;
                }
                // Digit is a duplicate = invalid
                if (rowContents[this.grid[row][col].getValue() - 1]) {
                    return false;
                }
                rowContents[this.grid[row][col].getValue() - 1] = true;
            }
        }

        // Check columns for duplicates
        for (int col = 0; col <= 8; col++) {
            boolean[] colContents = contents.clone();
            for (int row = 0; row <= 8; row++) {
                if (this.grid[row][col].getValue() == 0) {
                    continue;
                }
                if (colContents[this.grid[row][col].getValue() - 1]) {
                    return false;
                }
                colContents[this.grid[row][col].getValue() - 1] = true;
            }
        }

        // Check individual 3x3 boxes for duplicates
        for (int topRow = 0; topRow <= 6; topRow+=3) {
            for (int leftCol = 0; leftCol <= 6; leftCol+=3) {

                boolean[] boxContents = contents.clone();
                for (int row = topRow; row <= topRow + 2; row++) {
                    for (int col = leftCol; col <= leftCol + 2; col++) {
                        if (this.grid[row][col].getValue() == 0) {
                            continue;
                        }
                        if (boxContents[this.grid[row][col].getValue() - 1]) {
                            return false;
                        }
                        boxContents[this.grid[row][col].getValue() - 1] = true;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Getter method for this.priority.
     * @return square with the lowest number of possible solutions.
     */
    public Square getPriority() {
        return priority;
    }

    /**
     * Getter method for this.filled.
     * @return number of cells with non-zero values.
     */
    public int getFilled() {
        return this.filled;
    }

    /**
     * Set this.filled to the number of squares with non-zero values.
     */
    private void setFilled() {
        this.filled = 0;
        for (Square[] row : this.grid) {
            for (Square square : row) {
                if (square.getValue() != 0) {
                    this.filled++;
                }
            }
        }
    }

    /**
     * Updates possible solutions for each square.
     * If a square is already solved, return true.
     * If a square has no possible solutions, return false (invalid).
     * If a square has 1 possible solution, set the square's value,
     * and update the surrounding squares' possible solutions.
     * If a square has multiple possible solutions, set this.priority
     * to the square with fewer possible solutions.
     * @param square The square.
     * @return false if a square has no possible solution, else true.
     */
    private boolean update(Square square) {
        if (square.getValue() != 0) { return true; }
        if (square.getPossible().isEmpty()) { return false; }

        if (square.getPossible().size() == 1) {
            int value = square.getPossible().get(0);
            square.setValue(value);
            this.filled++;
            return updateNeighborsOf(square);
        } else {
            if (this.priority.compareTo(square) > 0) {
                this.priority = square;
            }
            return true;
        }
    }

    /**
     * After setting a square's value, check all squares in the same
     * row / column / 3x3 grid and update their possible values. If
     * this leaves a square with no possible solutions, return false.
     * @param square The square with the recently set value, whose
     *               neighbors are being checked.
     * @return false if a square becomes impossible, true otherwise.
     */
    private boolean updateNeighborsOf(Square square) {
        return checkRow(square) && checkCol(square) && checkBox(square);
    }

    /**
     * After setting a square's value, change possible solutions for
     * squares in the same row.
     * @param square The square whose value was changed.
     * @return false if any square becomes unsolvable, else true.
     */
    private boolean checkRow(Square square) {
        for (int col = 0; col <= 8; col++) {
            Square neighbor = this.grid[square.getRow()][col];
            if (neighbor.getValue() == 0) {
                neighbor.delPossible(square.getValue());
                if (neighbor.getPossible().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * After setting a square's value, change possible solutions for
     * squares in the same column.
     * @param square The square whose value was changed.
     * @return false if any square becomes unsolvable, else true.
     */
    private boolean checkCol(Square square) {
        for (int row = 0; row <= 8; row++) {
            Square neighbor = this.grid[row][square.getCol()];
            if (neighbor.getValue() == 0) {
                neighbor.delPossible(square.getValue());
                if (neighbor.getPossible().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * After setting a square's value, change possible solutions for
     * squares in the same 3x3 box.
     * @param square The square whose value was changed.
     * @return false if any square becomes unsolvable, else true.
     */
    private boolean checkBox(Square square) {
        int boxTopRow = (square.getRow() / 3) * 3;
        int boxLeftCol = (square.getCol() / 3) * 3;
        for (int row = boxTopRow; row <= boxTopRow + 2; row++) {
            for (int col = boxLeftCol; col <= boxLeftCol + 2; col++) {
                Square neighbor = this.grid[row][col];
                if (neighbor.getValue() == 0) {
                    neighbor.delPossible(square.getValue());
                    if (neighbor.getPossible().isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int compareTo(SudokuBoard o) {
        return Integer.compare(this.filled, o.filled);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row <= 8; row++) {
            for (int column = 0; column <= 8; column++) {
                sb.append(this.grid[row][column]);
                sb.append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
