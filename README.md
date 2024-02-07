# About
Find the solution to any solvable sudoku board.

# Usage
This is just source code for now, but I plan to add a user interface later. 

To use the program, run the main method in SudokuGame.java.

This branch's algorithm, in particular the way it targets squares to update instead of looping over the whole grid repeatedly, is probably more elegant, but the results time-wise are almost exactly the same both ways on my machine. If this wasn't sudoku but something more memory / time intensive, the differences might be greater.
