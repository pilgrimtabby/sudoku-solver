/**
 * Hold the contents of a sudoku board.
 *
 * @author pilgrim_tabby
 * @version 0.0.2
 */
public class SudokuBoard implements Comparable<SudokuBoard> {
    private final Square[][] grid = new Square[9][9];
    /** Number of squares with non-zero (solved) value */
    private int filled;
    /** Squares with 1 possible solution */
    private final Stack<Square> solvedSquares = new Stack<>();

    /**
     * Construct new board from 9 x 9 integer nested array.
     * Create Square objects for each square, and add squares with
     * only one possible solution to the solvedSquares stack.
     * @param grid The nested integer array of (1-digit) numbers.
     */
    public SudokuBoard(int[][] grid) {
        this.filled = 0;

        for (int row = 0; row <= 8; row++) {
            for (int col = 0; col <= 8; col++) {
                this.grid[row][col] = new Square(grid, row, col);
                if (grid[row][col] == 0) {
                    if (this.grid[row][col].getPossible().size() == 1) {
                        this.solvedSquares.push(this.grid[row][col]);
                    }
                } else {
                    this.filled++;
                }
            }
        }
    }

    /**
     * Construct new board from old board by deep copying its squares
     * and adding squares with 1 possible solution to solvedSquares.
     * @param board The old board
     */
    public SudokuBoard(SudokuBoard board) {
        // Deep copy squares of old board to new board
        for (Square[] row : board.grid) {
            for (Square square : row) {
                this.grid[square.getRow()][square.getCol()] = new Square(square);
                if (this.grid[square.getRow()][square.getCol()].getValue() == 0 &&
                        this.grid[square.getRow()][square.getCol()].getPossible().size() == 1) {
                    this.solvedSquares.push(this.grid[square.getRow()][square.getCol()]);
                }
            }
        }

        this.filled = board.filled;
    }

    /**
     * Iterate over each square with one possible solution and set
     * its value.
     * @return null if there are no more squares with
     * just 1 possible solution left, otherwise returns a square
     * with a small amount of possible solutions.
     */
    public Square updateGrid() {
        while (!this.solvedSquares.isEmpty()) {
            boolean boardIsPossible = update(this.solvedSquares.pop());
            if (!boardIsPossible) {
                return null;
            }
        }
        return getPriority();
    }

    /**
     * Verify a board is obeying "sudoku rules" (no duplicates of
     * 1-9 in a column, row, or 3x3 box).
     * @return false if a rule is broken, true otherwise.
     */
    public boolean validBoard() {
        return validInput() && validRows() && validColumns() && validBoxes();
    }

    /**
     * Get a square with a small amount of possible solutions.
     * @return The aforementioned square.
     */
    public Square getPriority() {
        if (getFilled() == 81) {
            return null;  // board is complete
        }

        // since board is not complete, this loop will set priority
        // to an actual square every time.
        Square priority = null;
        for (Square[] row : this.grid) {
            for (Square square : row) {
                if (square.getValue() == 0) {
                    priority = square;
                    break;
                }
            }
        }
        assert priority != null;

        // loop through grid looking for a square with lower priority
        // than current choice. Break if size is 2 (lowest possible)
        for (Square[] row : this.grid) {
            for (Square square : row) {
                if (square.getValue() == 0) {
                    if (square.compareTo(priority) < 0) {
                        priority = square;
                        if (priority.getPossible().size() == 2) {
                            return priority;
                        }
                    }
                }
            }
        }
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
     * Verify each value in the board is a single-digit integer.
     * @return true if only single-digit integers, false otherwise.
     */
    private boolean validInput() {
        for (int row = 0; row <= 8; row++) {
            for (int col = 0; col <= 8; col++) {
                if (this.grid[row][col].getValue() < 0 || this.grid[row][col].getValue() > 9) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verify each row contains no duplicate values.
     * @return true if each value is unique, false otherwise.
     */
    private boolean validRows() {
        for (int row = 0; row <= 8; row++) {
            boolean[] contents = {false, false, false, false, false, false, false, false, false};
            for (int col = 0; col <= 8; col++) {
                // Ignore "blank" squares
                if (this.grid[row][col].getValue() == 0) {
                    continue;
                }
                // Digit is a duplicate = invalid
                if (contents[this.grid[row][col].getValue() - 1]) {
                    return false;
                }
                contents[this.grid[row][col].getValue() - 1] = true;
            }
        }
        return true;
    }

    /**
     * Verify each column contains no duplicate values.
     * @return true if each value is unique, false otherwise.
     */
    private boolean validColumns() {
        for (int col = 0; col <= 8; col++) {
            boolean[] contents = {false, false, false, false, false, false, false, false, false};
            for (int row = 0; row <= 8; row++) {
                if (this.grid[row][col].getValue() == 0) {
                    continue;
                }
                if (contents[this.grid[row][col].getValue() - 1]) {
                    return false;
                }
                contents[this.grid[row][col].getValue() - 1] = true;
            }
        }
        return true;
    }

    /**
     * Verify each 3 x 3 box contains no duplicate values.
     * @return true if each value is unique, false otherwise.
     */
    private boolean validBoxes() {
        for (int topRow = 0; topRow <= 6; topRow+=3) {
            for (int leftCol = 0; leftCol <= 6; leftCol+=3) {

                boolean[] contents = {false, false, false, false, false, false, false, false, false};
                for (int row = topRow; row <= topRow + 2; row++) {
                    for (int col = leftCol; col <= leftCol + 2; col++) {

                        if (this.grid[row][col].getValue() == 0) {
                            continue;
                        }
                        if (contents[this.grid[row][col].getValue() - 1]) {
                            return false;
                        }
                        contents[this.grid[row][col].getValue() - 1] = true;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Set value for squares with only 1 possible solution.
     * Check their neighboring squares (row, column, 3x3 box) and
     * remove the square's new value from neighboring possible
     * solutions lists.
     * @param square The square.
     * @return false if a neighboring square becomes impossible,
     * otherwise true.
     */
    private boolean update(Square square) {
        square.setValue(square.getPossible().get(0));
        this.filled++;
        return updateNeighbors(square);
    }

    /**
     * After setting a square's value, check all squares in the same
     * row / column / 3x3 grid and update their possible values. If
     * this leaves a square with no possible solutions, return false.
     * @param square The square with the recently set value, whose
     *               neighbors are being checked.
     * @return false if a square becomes impossible, true otherwise.
     */
    private boolean updateNeighbors(Square square) {
        return updateRow(square) && updateCol(square) && updateBox(square);
    }

    /**
     * After setting a square's value, change possible solutions for
     * squares in the same row.
     * If a square's possible solutions are reduced to 1, add the
     * square to solvedSquares.
     * @param square The square whose value was changed.
     * @return false if any square becomes unsolvable, else true.
     */
    private boolean updateRow(Square square) {
        int formerPossibleCount;  // track pre-delete possibilities
        for (int col = 0; col <= 8; col++) {
            Square neighbor = this.grid[square.getRow()][col];
            if (neighbor.getValue() == 0) {

                formerPossibleCount = neighbor.getPossible().size();
                neighbor.delPossible(square.getValue());

                if (neighbor.getPossible().size() == 1 && formerPossibleCount != 1) {
                    this.solvedSquares.push(neighbor);
                } else if (neighbor.getPossible().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * After setting a square's value, change possible solutions for
     * squares in the same column.
     * If a square's possible solutions are reduced to 1, add the
     * square to solvedSquares.
     * @param square The square whose value was changed.
     * @return false if any square becomes unsolvable, else true.
     */
    private boolean updateCol(Square square) {
        int formerPossibleCount;
        for (int row = 0; row <= 8; row++) {
            Square neighbor = this.grid[row][square.getCol()];
            if (neighbor.getValue() == 0) {

                formerPossibleCount = neighbor.getPossible().size();
                neighbor.delPossible(square.getValue());

                if (neighbor.getPossible().size() == 1 && formerPossibleCount != 1) {
                    this.solvedSquares.push(neighbor);
                } else if (neighbor.getPossible().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * After setting a square's value, change possible solutions for
     * squares in the same 3x3 box.
     * If a square's possible solutions are reduced to 1, add the
     * square to solvedSquares.
     * @param square The square whose value was changed.
     * @return false if any square becomes unsolvable, else true.
     */
    private boolean updateBox(Square square) {
        int formerPossibleCount;
        int boxTopRow = (square.getRow() / 3) * 3;
        int boxLeftCol = (square.getCol() / 3) * 3;
        for (int row = boxTopRow; row <= boxTopRow + 2; row++) {
            for (int col = boxLeftCol; col <= boxLeftCol + 2; col++) {
                Square neighbor = this.grid[row][col];
                if (neighbor.getValue() == 0) {

                    formerPossibleCount = neighbor.getPossible().size();
                    neighbor.delPossible(square.getValue());

                    if (neighbor.getPossible().size() == 1 && formerPossibleCount != 1) {
                        this.solvedSquares.push(neighbor);
                    } else if (neighbor.getPossible().isEmpty()) {
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
