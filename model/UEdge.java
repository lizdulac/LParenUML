package model;

public class UEdge {
	private String name;//derp
	private String type;//derp
	UNode end, start;//eeeyyyy
	
	
	public UEdge(UNode n1, UNode n2) {
		start = n1;
		end = n2;
		name = "";
		type = "line";
	}
	

}

