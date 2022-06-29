This simple command line Java program solves the Hi-Q problem for a predetermined board. The Hi-Q problem is a game wherein there is a board of some shape, with some number (say, x) of holes in it. Of the holes, x-1 are filled. The player can jump a peg over an adjacent peg into an empty hole, removing the peg that was jumped over, similar to checkers. The objective is to clear the board such that only 1 peg remains. 

For the purposes of this program, the board layout is as follows:

0   1   2   3   4
  5   6   7   8
    9   10  11
      12  13
        14

The program can be modified for other board layouts by changing the available moves, but these are hardcoded as accepting variable board layouts via command line is not adviseable. 

As such, the user of this program need only enter the number corresponding to the empty hole (0-14, inclusive), and the program will solve the Hi-Q game, step by step.