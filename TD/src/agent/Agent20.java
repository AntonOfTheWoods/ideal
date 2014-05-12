package agent;

import java.util.ArrayList;
import java.util.List;

import agent.decider.Episode20;
import tracer.Trace;
import coupling.Coupling20;
import coupling.Experience;
import coupling.Result;
import coupling.interaction.Interaction2;

public class Agent20 implements Agent{

	public static int BOREDOME_LEVEL = 5;
	
	private Coupling20 coupling;
	private Episode20 contextEpisode;
	private Episode20 currentEpisode;	
	private int state;
	
	public Agent20(Coupling20 coupling){
		this.coupling = coupling;
	}
	
	public Experience chooseExperience(Result result){

		if (this.currentEpisode != null && this.currentEpisode.getInteraction().getResult().equals(result)){			
			Trace.addEventElement("mood", "CONTENT");
			this.state++;
		}
		else{
			Trace.addEventElement("mood", "FRUSTRATED");
			this.state = 0;
		}
		if (result != null)
			this.currentEpisode.record(result);

		if (this.contextEpisode != null )
			this.coupling.createCompositeInteraction(this.contextEpisode.getInteraction(), this.currentEpisode.getInteraction());

		List<Interaction2> proposedInteractions = new ArrayList<Interaction2>();
		if (this.currentEpisode != null)
			proposedInteractions = this.coupling.proposeInteractions(this.currentEpisode.getInteraction()); 
		
		Interaction2 intendedInteraction = (Interaction2)this.coupling.getOtherInteraction(null);
		if (this.state < BOREDOME_LEVEL){
			if (proposedInteractions.size() > 0)
				if (proposedInteractions.get(0).getValence() >= 0)
					intendedInteraction = proposedInteractions.get(0);
				else
					intendedInteraction = (Interaction2)this.coupling.getOtherInteraction(proposedInteractions.get(0));
		}
		else{
			Trace.addEventElement("mood", "BORED");
			this.state = 0;
			if (proposedInteractions.size() == 1)
				intendedInteraction = (Interaction2)this.coupling.getOtherInteraction(proposedInteractions.get(0));
			else if (proposedInteractions.size() > 1)
				intendedInteraction = proposedInteractions.get(1);
		}
					
		this.contextEpisode = this.currentEpisode;

		this.currentEpisode = this.coupling.createEpisode(intendedInteraction);
		Trace.addEventElement("intend", intendedInteraction.toString());

		return intendedInteraction.getExperience();
	}
}