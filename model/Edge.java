package softwaredev;

public class Edge {
	private String name;//Iteration 2/3
	private String type;//Iteration 2/3
	UNode end, start;
	
	
	public Edge(UNode n1, UNode n2) {
		start = n1;
		end = n2;
		name = "";
		type = "line";
	}
	

}

