package student_player;

import java.util.ArrayList;
import java.util.Random;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;


public class Node {
	private Node parent;
	private ArrayList<Node> children;
	
	private final PentagoBoardState board;
	private PentagoMove move;
	private int numOfVisits = 0;
	private int numOfWins = 0;

	/**
	 * Default node constructor
	 * @param board : this node's board state
	 */
	public Node(PentagoBoardState board) {
		this.children = new ArrayList<>();
		this.board = board;
	}
	
	/**
	 * Node constructor that stores the PentagoMove that yielded this node's state
	 * @param board : this node's board state
	 * @param move : the PentagoMove that was applied to this node's parent that yielded this node
	 */
	public Node(PentagoBoardState board, PentagoMove move) {
		this.children = new ArrayList<>();
		this.board = board;
		this.move = move;
	}
	
	/**
	 * Node deep copy-constructor
	 * @param node : the node for which we want a deep copy for
	 */
	public Node(Node node) {
		this.children = new ArrayList<>();
		this.parent = node.parent;
		
		this.board = (PentagoBoardState) node.board.clone();
		for (Node child: node.getChildren()) {
			this.children.add(new Node(child));
		}
	}

	// Field getters
	public Node getParent() { return this.parent; }
	public ArrayList<Node> getChildren() { return this.children; }
	public PentagoBoardState getBoard() { return this.board; }
	public int getNumOfVisits() { return this.numOfVisits; }
	public int getNumOfWins() { return this.numOfWins; }
	public PentagoMove getMove() { return this.move; }
	public void setMove(PentagoMove move) { this.move = move; }
	
	public void incrementNumOfVisits() { this.numOfVisits++; }
	public void incrementNumOfWins() { this.numOfWins++; }
	
	public void addChild(Node newChild) {
		this.children.add(newChild);
		newChild.parent = this;
	}
	
	/**
	 * @return Node from children with most # of visits
	 */
	public Node getMaxChild() {
		Node maxChild = null;
		for (Node child: this.children) {
			if (maxChild == null)
				maxChild = child;
			else {
				if (child.getNumOfVisits() > maxChild.getNumOfVisits())
					maxChild = child;
			}
		}
		return maxChild;
	}
	
	/**
	 * @return Node from children with highest UCT value
	 */
	public Node getMaxChildUCT() {
		Node maxChild = null;
		double maxUCT = -1.0;
		
		for (Node child: this.children) {
			if (maxChild == null) {
				maxChild = child;
				maxUCT = UCT.computeUCT(child);
			}
			else {
				double newUCT = UCT.computeUCT(child);
				if (newUCT > maxUCT) {
					maxChild = child;
					maxUCT = newUCT;
				}
			}
		}
		return maxChild;
	}

	/**
	 * Gets a random child from this node's children. If this node has no children, it generates a random child node.
	 * @return a random child Node from the list of children. If there are no children after generating all children, it returns itself.
	 */
	public Node getRandomChild() {
		this.generateAllChildren();
		if (this.children.size() == 0)
			return this;
		return this.children.get((new Random()).nextInt(this.children.size()));
	}
	
	/**
	 * Generates all children node for this node by iterating over all possible PentagoMoves from the current state
	 * and creating & appending a child Node for each derivative game state possible.
	 */
	private void generateAllChildren() {
		if (this.children.size() == 0) {
			ArrayList<PentagoMove> moves = this.board.getAllLegalMoves();
			for (PentagoMove move: moves) {
				Node newChild = new Node((PentagoBoardState) this.board.clone(), move);
				newChild.board.processMove(move);
				this.addChild(newChild);
			}
		}
	}
}
