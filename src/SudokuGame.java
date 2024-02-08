import java.util.ArrayList;

/**
 * Find the solution to a Sudoku board.
 *
 * @author pilgrim_tabby
 * @version 0.0.2
 */
public class SudokuGame {
    private static int inserts;
    private static int newBoards;

    /**
     * Get a sudoku board and iterate through possible solutions
     * until the board is solved.
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SudokuBoard board = getSudokuBoard();
        if (board == null) { return; }
        Stack<SudokuBoard> boardStack = new Stack<>(board);
        inserts++;

        while (!boardStack.isEmpty()) {
            board = boardStack.pop();
            newBoards++;
            Square nextSquare = board.updateGrid();

            if (nextSquare == null) {
                // Solution found
                if (board.getFilled() == 81) {
                    if (!board.validBoard()) {
                        System.out.println("Solution found, but it's invalid");
                    } else {
                        System.out.println("SOLUTION FOUND!");
                    }
                    long totalTime = System.currentTimeMillis() - startTime;
                    System.out.printf("Boards generated: %d\n", inserts);
                    System.out.printf("Boards tested: %d\n", newBoards);
                    System.out.printf("Time elapsed: %d ms\n", totalTime);
                    System.out.println(board);
                    return;
                }
                continue;
            }

            // Move to next board if current board is impossible
            // Using a square with minimal possible solutions, make
            // new boards, one for each possible solution, and add
            // them to the stack.
            ArrayList<Integer> possible = new ArrayList<>(nextSquare.getPossible());
            for (int i : possible) {
                nextSquare.overwritePossible(i);
                boardStack.push(new SudokuBoard(board));
                inserts++;
            }
        }

        // Board is impossible
        System.out.println("Impossible board");
        System.out.printf("Boards generated: %d\n", inserts);
        System.out.printf("Boards tested: %d\n", newBoards);
    }

    /**
     * Get a sudoku board from the user.
     * @return The board.
     */
    private static SudokuBoard getSudokuBoard() {
        // TODO: Add way for user to input a board.
        int[][] grid = {
                { 1, 0, 6, 0, 0, 0, 0, 3, 0, },
                { 0, 2, 0, 0, 1, 8, 4, 0, 0, },
                { 0, 0, 0, 7, 0, 0, 0, 0, 0, },
                { 3, 0, 0, 0, 7, 5, 0, 4, 0, },
                { 0, 0, 0, 2, 0, 0, 7, 0, 0, },
                { 0, 5, 0, 9, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 9, 0, 0, 0, },
                { 0, 8, 0, 0, 5, 4, 1, 0, 0, },
                { 2, 0, 0, 0, 0, 0, 0, 0, 8, },
        };

        int[][] grid0 = {
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, },
        };

        SudokuBoard board = new SudokuBoard(grid);
        if (!board.validBoard()) {
            System.out.println("Invalid board");
            return null;
        }
        return board;
    }
}
