package student_player;

public class UCT {

	public static double computeUCT(Node node) {
		int numOfWins = node.getNumOfWins();
		int numOfVisits = node.getNumOfVisits();
		int parentNumOfVisits = node.getParent().getNumOfVisits();
		
		return ((double) numOfWins / (double) numOfVisits) + 
				Math.sqrt(2 * (Math.log(parentNumOfVisits) / (double) numOfVisits));
	}

}
