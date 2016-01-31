package script;

import java.awt.Component;

public class Task {
	private Action action;
	private Variable[] parameters;
	private Variable outputVar;

	public Task(Action action, Variable[] parameters, Variable outputVar) {
		this.action = action;
		this.parameters = parameters;
		this.outputVar = outputVar;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void destroy() {
		if(outputVar != null)
			outputVar.destroy();

		for(Variable v : parameters)
			v.destroy();
		
		action = null;
		parameters = null;
		outputVar = null;
	}
	
	public Variable run() {
		return action.run(outputVar, parameters);
	}

	public Variable getOutputVariable() {
		return outputVar;
	}

	public Variable[] getParameters() {return parameters;}
	public void addParameters(Variable[] parameters) {
		int lenOld = this.parameters.length,
			lenNew = parameters.length;
		Variable[] newPars = new Variable[lenOld + lenNew];
		
		for(int i = 0; i < lenOld; i++)
			newPars[i] = this.parameters[i];
		for(int i = 0; i < lenNew; i++)
			newPars[lenOld+i] = parameters[i];
		
		this.parameters = newPars;
	}
}
