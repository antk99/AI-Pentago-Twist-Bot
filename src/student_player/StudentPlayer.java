package student_player;

import java.util.ArrayList;

import boardgame.Move;

import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260869057");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {	
    	
    	// Keep track of the time used to return a move
    	final long startTime = System.nanoTime();
    	
    	// Checks through all possible moves to see if there is a winning move for us
    	// By generating all possible boards from the possible moves and checking if any
    	// of the generated boards result in a win for our player number.
    	ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
    	for (PentagoMove move: moves) {
    		if (moveResultInWin(boardState, move, boardState.getTurnPlayer()))
    			return move;
    	}
    	
    	// Processes 2 different possible moves on a cloned board (so it is now the opponent's
    	// turn on the cloned board), and then generates all possible opponent moves of the 
    	// cloned board & checks for each possible opponent move if any of those result in a win
    	// for them. If any of those moves result in a win for the opponent, we make that move to 
    	// block them from taking it.
    	for (int i = 0; (i < 2) && i < moves.size(); i++) {
        	PentagoMove randMove = moves.get(i);									// gets a random move we can make
        	PentagoBoardState randBoard = (PentagoBoardState) boardState.clone();	// clones current board
        	randBoard.processMove(randMove);	
        	
        	ArrayList<PentagoMove> oppMoves = randBoard.getAllLegalMoves();
        	for (PentagoMove move: oppMoves) {
        		if (moveResultInWin(randBoard, move, (1 - boardState.getTurnPlayer()))) {
    				if (boardState.isPlaceLegal(move.getMoveCoord()))
    					return move;
        		}
        	}
    	}

    	// If there is no immediate winning move for us or if the opponent has no immediate winning move
    	// we resort to running the Monte Carlo Tree Search for making our next move.
        return MCTS.getNextMove(boardState, startTime);
    }
    
    private static boolean moveResultInWin(PentagoBoardState boardState, PentagoMove move, int playerNum) {
    	PentagoBoardState newBoard = (PentagoBoardState) boardState.clone();
    	newBoard.processMove(move);
    	return newBoard.getWinner() == playerNum;
    }
}