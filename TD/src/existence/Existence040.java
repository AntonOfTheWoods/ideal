package existence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tracer.Trace;
import agent.Anticipation;
import agent.Anticipation031;
import coupling.Experience;
import coupling.Experience040;
import coupling.Result;
import coupling.interaction.Interaction;
import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;

/** 
* Existence040 is used to demonstrate an Existence capable of anticipating two steps to make a decision.
*/
public class Existence040 extends Existence031 {

	private Interaction040 previousSuperInteraction;
	private Interaction040 lastSuperInteraction;

	@Override
	protected void initExistence(){
		Experience040 e1 = (Experience040)addOrGetExperience(LABEL_E1);
		Experience040 e2 = (Experience040)addOrGetExperience(LABEL_E2);
		Result r1 = createOrGetResult(LABEL_R1);
		Result r2 = createOrGetResult(LABEL_R2);
		Interaction040 e11 = (Interaction040)addOrGetPrimitiveInteraction(e1, r1, -1);
		Interaction040 e12 = (Interaction040)addOrGetPrimitiveInteraction(e1, r2, 1);
		Interaction040 e21 = (Interaction040)addOrGetPrimitiveInteraction(e2, r1, -1);
		Interaction040 e22 = (Interaction040)addOrGetPrimitiveInteraction(e2, r2, 1);
		e1.setInteraction(e12); e1.resetAbstract();
		e2.setInteraction(e22); e2.resetAbstract();
	}
	
	@Override
	public String step() {
		
		Interaction040 intendedInteraction = chooseInteraction();
		Interaction040 enactedInteraction = enact(intendedInteraction);
		
		this.setPreviousSuperInteraction(this.getLastSuperInteraction());
		this.setContextInteraction(this.getEnactedInteraction());
		this.setEnactedInteraction(enactedInteraction);
		
		return enactedInteraction.toString();
	}
	
	/**
	 * Learn composite interactions from 
	 * the previous super interaction, the context interaction, and the enacted interaction
	 */
	public void learnCompositeInteraction(){
		Interaction040 previousInteraction = this.getContextInteraction();
		Interaction040 lastInteraction = this.getEnactedInteraction();
		Interaction040 previousSuperInteraction = this.getPreviousSuperInteraction();
		Interaction040 lastSuperIntearction = null;
        // learn [previous current]
		if (previousInteraction != null)
			lastSuperIntearction = addOrGetAndReinforceCompositeInteraction(previousInteraction, lastInteraction);
		
		// Learn three-step higher-level interactions
        if (previousSuperInteraction != null && previousInteraction.isPrimitive() && lastInteraction.isPrimitive()){	
            // learn [penultimate [previous current]]
            this.addOrGetAndReinforceCompositeInteraction(previousSuperInteraction.getPreInteraction(), lastSuperIntearction);
            // learn [[penultimate previous] current]
            this.addOrGetAndReinforceCompositeInteraction(previousSuperInteraction, lastInteraction);
        }
        this.setLastSuperInteraction(lastSuperIntearction);
	}
	
	public Interaction040 addOrGetAndReinforceCompositeInteraction(Interaction030 preInteraction, Interaction030 postInteraction){
		Interaction040 compositeInteraction = addOrGetCompositeInteraction(preInteraction, postInteraction);
		compositeInteraction.incrementWeight();
		return compositeInteraction;
	}

	/**
	 * Records or get a composite interaction in memory
	 * If a new composite interaction is created, then a new abstract experience is also created and associated to it.
	 * @param preInteraction: The composite interaction's pre-interaction
	 * @param postInteraction: The composite interaction's post-interaction
	 * @return the learned composite interaction
	 */
	@Override
	public Interaction040 addOrGetCompositeInteraction(Interaction030 preInteraction, Interaction030 postInteraction) {
		String label = "(" + preInteraction.getLabel() + postInteraction.getLabel() + ")";
        Interaction040 interaction = (Interaction040)getInteraction(label);
        if (interaction == null){
			interaction = (Interaction040)addOrGetInteraction(label); 
			interaction.setPreInteraction(preInteraction);
			interaction.setPostInteraction(postInteraction);
			interaction.setValence(preInteraction.getValence() + postInteraction.getValence());
			Experience040 abstractExperience = this.addOrGetAbstractExperience(interaction);
			interaction.setExperience(abstractExperience);
			System.out.println("learn " + interaction.getLabel());
        }
    	return interaction;
	}
	
    public Experience040 addOrGetAbstractExperience(Interaction040 compositeInteraction) {
        String label = compositeInteraction.getLabel();
        if (!EXPERIENCES.containsKey(label)){
        	Experience040 abstractExperience =  new Experience040(label);
        	abstractExperience.setInteraction(compositeInteraction);
            EXPERIENCES.put(label, abstractExperience);
        }
        return (Experience040)EXPERIENCES.get(label);
}

	@Override
	protected Interaction040 createInteraction(String label){
		return new Interaction040(label);
	}

	/**
	 * Compute the system's mood
	 * and choose the next intended interaction 
	 * @return The next intended interaction.
	 */
	public Interaction040 chooseInteraction(){
		Interaction040 previousEnactedInteraction = this.getEnactedInteraction();
		if (previousEnactedInteraction != null){
			if (previousEnactedInteraction.getValence() >= 0)
				Trace.addEventElement("mood", "PLEASED");
			else
				Trace.addEventElement("mood", "PAINED");
		}
		learnCompositeInteraction();
		List<Anticipation> anticipations = computeAnticipations();
		return selectInteraction(anticipations);
	}
	
	/**
	 * Get the list of activated interactions
	 * from the enacted Interaction
	 * and the last super interaction
	 * @param the enacted interaction
	 * @return the list of anticipations
	 */
	public List<Interaction> getActivatedInteractions() {
		List<Interaction> activatedInteractions = new ArrayList<Interaction>();
		for (Interaction interaction : this.INTERACTIONS.values()){
			Interaction040 activatedInteraction = (Interaction040)interaction;
			if (!activatedInteraction.isPrimitive())
				if (activatedInteraction.getPreInteraction() == this.getEnactedInteraction() ||
					activatedInteraction.getPreInteraction() == this.getLastSuperInteraction() ||
					!this.getEnactedInteraction().isPrimitive() &&
					activatedInteraction.getPreInteraction() == this.getEnactedInteraction().getPostInteraction()){
				activatedInteractions.add(activatedInteraction);
				}
		}
		return activatedInteractions;
	}	

	@Override
	protected List<Anticipation> getDefaultPropositions(){
		List<Anticipation> anticipations = new ArrayList<Anticipation>();
		for (Experience experience : this.EXPERIENCES.values()){
			Experience040 defaultExperience = (Experience040)experience;
			if (!defaultExperience.isAbstract()){
				Anticipation031 anticipation = new Anticipation031(experience, 0);
				anticipations.add(anticipation);
			}
		}
		return anticipations;
	}

	public Interaction040 selectInteraction(List<Anticipation> anticipations){
		// The list of anticipations is never empty because all the experiences are proposed by default with a proclivity of 0
		Collections.sort(anticipations);
		for (Anticipation anticipation : anticipations)
			System.out.println("propose " + anticipation.toString());
		
		Anticipation031 selectedAnticipation = (Anticipation031)anticipations.get(0);
		return ((Experience040)selectedAnticipation.getExperience()).getInteraction();
	}

	public Interaction040 enact(Interaction030 intendedInteraction){

		if (intendedInteraction.isPrimitive())
			return enactPrimitiveIntearction(intendedInteraction);
		else {			
			// Enact the pre-interaction
			Interaction040 enactedPreInteraction = enact(intendedInteraction.getPreInteraction());
			if (!enactedPreInteraction.equals(intendedInteraction.getPreInteraction())){
				// TODO interrupt
				return enactedPreInteraction;
			}
			else
			{
				// Enact the post-interaction
				Interaction040 enactedPostInteraction = enact(intendedInteraction.getPostInteraction());
				if (!enactedPostInteraction.equals(intendedInteraction.getPostInteraction())){
					// TODO interrupt
				}
				return (Interaction040)addOrGetCompositeInteraction(enactedPreInteraction, enactedPostInteraction);
			}
		}
	}

	/**
	 * Implements the cognitive coupling between the agent and the environment
	 * @param intendedPrimitiveInteraction: The intended primitive interaction to try to enact against the environment
	 * @param The actually enacted primitive interaction.
	 */
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		Experience experience = intendedPrimitiveInteraction.getExperience();
		/** Change the returnResult() to change the environment */		
		//Result result = returnResult010(experience);
		//Result result = returnResult030(experience);
		//Result result = returnResult031(experience);
		Result result = returnResult040(experience);
		return (Interaction040)this.addOrGetPrimitiveInteraction(experience, result);
	}

	@Override
	protected Experience040 createExperience(String label){
		return new Experience040(label);
	}

	public Interaction040 getContextInteraction(){
		return (Interaction040)super.getContextInteraction();
	}
	public Interaction040 getEnactedInteraction(){
		return (Interaction040)super.getEnactedInteraction();
	}
	public Interaction040 getPreviousSuperInteraction() {
		return previousSuperInteraction;
	}
	public void setPreviousSuperInteraction(Interaction040 previousSuperInteraction) {
		this.previousSuperInteraction = previousSuperInteraction;
	}
	public Interaction040 getLastSuperInteraction() {
		return lastSuperInteraction;
	}
	public void setLastSuperInteraction(Interaction040 lastSuperInteraction) {
		this.lastSuperInteraction = lastSuperInteraction;
	}

	/**
	 * Environment040
	 * Results in R2 when the current experience equals the previous experience and differs from the penultimate experience.
	 * and in R1 otherwise.
	 * e1->r1 e1->r2 e2->r1 e2->r2 etc. 
	 */
	private Experience penultimateExperience;
	protected void setPenultimateExperience(Experience penultimateExperience){
		this.penultimateExperience = penultimateExperience;
	}
	protected Experience getPenultimateExperience(){
		return this.penultimateExperience;
	}

	public Result returnResult040(Experience experience){
		
		Result result = this.createOrGetResult(this.LABEL_R1);

		if (this.penultimateExperience != experience &&
			this.getPreviousExperience() == experience)
			result =  this.createOrGetResult(this.LABEL_R2);
		
		this.setPenultimateExperience(this.getPreviousExperience());
		this.setPreviousExperience(experience);
		
		return result;
	}

}
