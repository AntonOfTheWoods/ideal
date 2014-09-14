package environment;

import coupling.interaction.Interaction;
import existence.Existence050;

public class Environment050 implements Environment {

	private Existence050 existence;

	public Environment050(Existence050  existence){
		this.existence = existence;
		this.existence.addOrGetPrimitiveInteraction(this.getExistence().LABEL_E1 + this.getExistence().LABEL_R1, -1);
		this.existence.addOrGetPrimitiveInteraction(this.getExistence().LABEL_E1 + this.getExistence().LABEL_R2, 1);
		this.existence.addOrGetPrimitiveInteraction(this.getExistence().LABEL_E2 + this.getExistence().LABEL_R1, -1);
		this.existence.addOrGetPrimitiveInteraction(this.getExistence().LABEL_E2 + this.getExistence().LABEL_R2, 1);
	}

	protected Existence050 getExistence(){
		return this.existence;
	}
	
	private Interaction previousInteraction;
	protected void setPreviousInteraction(Interaction previousInteraction){
		this.previousInteraction = previousInteraction;
	}
	protected Interaction getPreviousInteraction(){
		return this.previousInteraction;
	}

	private Interaction penultimateInteraction;
	protected void setPenultimateInteraction(Interaction penultimateInteraction){
		this.penultimateInteraction = penultimateInteraction;
	}
	protected Interaction getPenultimateInteraction(){
		return this.penultimateInteraction;
	}

	@Override
	public Interaction enact(Interaction intendedInteraction) {
		Interaction enactedInteraction = null;
	
		if (intendedInteraction.getLabel().contains(this.getExistence().LABEL_E1)){
			if (this.getPenultimateInteraction() != null && this.getPreviousInteraction() != null &&
					this.getPenultimateInteraction().getLabel().contains(this.getExistence().LABEL_E2) &&
					this.getPreviousInteraction().getLabel().contains(this.getExistence().LABEL_E1))
				enactedInteraction = this.getExistence().addOrGetPrimitiveInteraction(this.getExistence().LABEL_E1 + this.getExistence().LABEL_R2, 0);
			else
				enactedInteraction = this.getExistence().addOrGetPrimitiveInteraction(this.getExistence().LABEL_E1 + this.getExistence().LABEL_R1, 0);
		}
		else{
			if (this.getPenultimateInteraction() != null && this.getPreviousInteraction() != null &&
					this.getPenultimateInteraction().getLabel().contains(this.getExistence().LABEL_E1) &&
					this.getPreviousInteraction().getLabel().contains(this.getExistence().LABEL_E2))
				enactedInteraction = this.getExistence().addOrGetPrimitiveInteraction(this.getExistence().LABEL_E2 + this.getExistence().LABEL_R2, 0);
			else
				enactedInteraction = this.getExistence().addOrGetPrimitiveInteraction(this.getExistence().LABEL_E2 + this.getExistence().LABEL_R1, 0);
		}
			
		this.setPenultimateInteraction(this.getPreviousInteraction());
		this.setPreviousInteraction(enactedInteraction);

		return enactedInteraction;
	}
}	
