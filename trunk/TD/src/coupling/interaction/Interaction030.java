package coupling.interaction;

/**
 * An Interaction030 is an Interaction that can be primitive or composite
 * A composite interaction has a preInteraction and a postInteraction.
 * @author Olivier
 */
public class Interaction030 extends Interaction020{
	
	private Interaction preInteraction;
	private Interaction postInteraction;

	public Interaction030(String label, int valence){
		super(label, valence);
	}
	
	public Interaction030 getPreInteraction() {
		return (Interaction030)preInteraction;
	}

	public void setPreInteraction(Interaction preInteraction) {
		this.preInteraction = preInteraction;
	}

	public Interaction030 getPostInteraction() {
		return (Interaction030)postInteraction;
	}
	
	public void setPostInteraction(Interaction postInteraction) {
		this.postInteraction = postInteraction;
	}

//	public List<Interaction2> getSeries(){
//		List<Interaction2> series = new ArrayList<Interaction2>();
//				
//		if (this.getExperience().isPrimitive())	
//			series.add(this);
//		else{
//			series.addAll(this.getPreInteraction().getSeries());
//			series.addAll(this.getPostInteraction().getSeries());
//		}		
//		return series;
//	}

	public String toString(){
		if (this.preInteraction != null)
			return this.preInteraction.getLabel() + this.postInteraction.getLabel() + "," + this.getValence();
		else
			return this.getExperience().getLabel() + this.getResult().getLabel() + "," + this.getValence();
	}
}