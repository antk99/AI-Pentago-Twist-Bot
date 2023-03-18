package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

public class MCTS {
	/**
	 * Gets next PentagoMove from the MCTS algorithm
	 * @param board : the current board state from which we need to make a move
	 * @param startTime : the start time the move request was initiated
	 * @return PentagoMove from the MCTS algorithm
	 */
	public static PentagoMove getNextMove(PentagoBoardState board, long startTime) {
		Node root = new Node(board);
		
		// for 1995ms or 1.95s, keep running MCTS
		while((System.nanoTime() - startTime) / 1000000.0 < 1995) {
			
			// SELECTION (1/4)
			Node selectedNode = selectBestNodeUCT(root);
			
			// EXPANSION (2/4)
			Node randomExpansionNode = selectedNode.getRandomChild();
			
			// SIMULATION (3/4)
			// default policy - random rollout
			if (!selectedNode.getBoard().gameOver()) {
				
				int rolloutResult = randomRollout(randomExpansionNode);
				
				// BACK-PROPOGATION (4/4)
				backpropagation(rolloutResult, randomExpansionNode, board.getTurnPlayer());
			}
		}
		return root.getMaxChild().getMove();
	}

	/**
	 * Backpropagates the result from the node to the root & increments the visit count for all nodes in that path.
	 * @param rolloutResult : the result of the random playout: 0 --> resulted in a loss/tie OR 1 --> resulted in a win
	 * @param node : the start node from which we want to begin the backpropogation
	 */
	private static void backpropagation(int rolloutResult, Node node, int playerNumber) {
		while (node != null) {
			node.incrementNumOfVisits();
			if (rolloutResult == playerNumber)	// random playout resulted in a win
				node.incrementNumOfWins();
			node = node.getParent();
		}
	}

	/**
	 * Simulates a random playout from the start node until the game is over.
	 * @param expansionNode : the Node that has been expanded for which we want to run a random playout.
	 * @return int 0/1: 0 --> resulted in a loss/tie OR 1 --> resulted in a win
	 */
	private static int randomRollout(Node expansionNode) {
		Node copyNode = new Node(expansionNode);
		PentagoBoardState board = copyNode.getBoard();
		
		while(!board.gameOver()) {
			board.processMove((PentagoMove) board.getRandomMove());
		}
		return board.getWinner();
	}

	/**
	 * Iterates until we reach a leaf node by selecting the child with the max UCT
	 * value at each iteration.
	 * @param root : the root node of the MCT
	 * @return UCT selected node
	 */
	private static Node selectBestNodeUCT(Node root) {
		Node node = root;
		while (node.getChildren().size() > 0) {
			node = node.getMaxChildUCT();
		}
		return node;
	}
}
