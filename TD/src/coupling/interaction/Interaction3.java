package coupling.interaction;

import java.util.ArrayList;
import java.util.List;

public class Interaction3 extends Interaction1 {
	
	private Interaction3 preInteraction;
	private Interaction3 postInteraction;

	private int weight = 0;

	public Interaction3(String label, int valence){
		super(label, valence);
	}
	
	public Interaction3 getPreInteraction() {
		return preInteraction;
	}

	public void setPreInteraction(Interaction3 preInteraction) {
		this.preInteraction = preInteraction;
	}

	public Interaction3 getPostInteraction() {
		return postInteraction;
	}
	
	public void setPostInteraction(Interaction3 postInteraction) {
		this.postInteraction = postInteraction;
	}

	public int getWeight() {
		return weight;
	}

	public void incrementWeight() {
		this.weight++;
	}
	
	public String toString(){
		return this.getLabel() + "," + this.getValence() + "," + this.weight;
	}
	
	public List<Interaction3> getSeries(){
		List<Interaction3> series = new ArrayList<Interaction3>();
				
		if (this.getExperience().isPrimitive())	
			series.add(this);
		else{
			//System.out.println("Interaction " + this.toString());
			series.addAll(this.getPreInteraction().getSeries());
			series.addAll(this.getPostInteraction().getSeries());
		}
		
		return series;
	}

}